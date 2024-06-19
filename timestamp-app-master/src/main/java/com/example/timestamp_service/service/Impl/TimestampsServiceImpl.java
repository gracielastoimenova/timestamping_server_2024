package com.example.timestamp_service.service.Impl;


import com.example.timestamp_service.model.File;
import com.example.timestamp_service.model.dto.TimestampResponse;
import com.example.timestamp_service.model.Timestamps;
import com.example.timestamp_service.model.User;
import com.example.timestamp_service.model.exceptions.NoFileForTimestampingException;
import com.example.timestamp_service.repository.FileRepository;
import com.example.timestamp_service.repository.TimestampsRepository;
import com.example.timestamp_service.service.EmailService;
import com.example.timestamp_service.service.SSLService;
import com.example.timestamp_service.service.TimestampsService;
import com.example.timestamp_service.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.*;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class TimestampsServiceImpl  implements TimestampsService{

    private final TimestampsRepository timestampsRepository;
    private final FileRepository fileRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final SSLService sslService;

    public TimestampsServiceImpl(TimestampsRepository timestampsRepository, FileRepository fileRepository, UserService userService, EmailService emailService, SSLService sslService) {
        this.timestampsRepository = timestampsRepository;
        this.fileRepository = fileRepository;
        this.userService = userService;
        this.emailService = emailService;
        this.sslService = sslService;
    }

    @Override
    @Transactional
    public void timestampFile(byte[] file, String fileName, User user) throws NoFileForTimestampingException {

           if (file.length!=0){
               byte[] timestampedFile = new byte[0];
               try {
                   timestampedFile = createTimestamp(file);
               } catch (Exception e) {
                   emailService.sendEmail("gracielastoimenova@gmail.com" , "Problem with your certificate for timestamp application",
                           "There was a problem loading some parts of your certificate that we need for the timestamp application.");
               }
               File file1 = new File(fileName, file);

               if (timestampedFile!=null)
                   timestampsRepository.save(new Timestamps(
                           getCurrentTimestamp(),
                           user,
                           file1,
                           timestampedFile
                   ));
               fileRepository.save(file1);
           } else throw  new NoFileForTimestampingException();


    }

    @Override
    public List<TimestampResponse> searchByName(String name){
        User user = userService.getCurrentUser();
        if (user.getRole().getAuthority().equals("ROLE_ADMIN")){
            return timestampsRepository.findAllByFile_FileNameContaining(name);
        }
        else return timestampsRepository.findAllByFile_FileNameContainingAndUser(name, user);
    }

    @Override
    public List<TimestampResponse> getAllTimestamps() {
        User user = userService.getCurrentUser();
        if (user.getRole().getAuthority().equals("ROLE_ADMIN")){
            return timestampsRepository.findAllTimestampResponses();
        }else return timestampsRepository.findAllByUser(user);
    }

    @Override
    public boolean verifyFile(byte[] file, String fileName)  {
        File file1 = fileRepository.findByFileName(fileName);
        Timestamps timestamps = timestampsRepository.findByFile(file1);

    try {
        PrivateKey privateKey = sslService.loadPrivateKey();

        String timestamp = timestamps.getDateAndTimeOfSigning();
        byte[] timestampedFile = timestamps.getTimestampedFile();

        byte[] hashedFileWithTimestamp = sslService.hashFile(concatenateData(sslService.hashFile(file), timestamp.getBytes())) ;
        hashedFileWithTimestamp = sslService.signWithPrivateKey(hashedFileWithTimestamp, privateKey);

        return Arrays.equals(hashedFileWithTimestamp, timestampedFile);
    } catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException | SignatureException | InvalidKeyException e){
        System.out.println("verifyFile threw exception for file: " + file1.getFileName());
        return false;
    }

    }

    @Override
    public byte[] createTimestamp(byte[] fileContent) throws Exception {
        byte[] hashedFile=sslService.hashFile(fileContent);

        PrivateKey privateKey = sslService.loadPrivateKey();
        X509Certificate certificate = sslService.loadCertificate();

        try{
            certificate.checkValidity(new Date());
            byte[] hashedWithTime = concatenateData(hashedFile, getCurrentTimestamp().getBytes());
            hashedWithTime = sslService.hashFile(hashedWithTime);

            return sslService.signWithPrivateKey(hashedWithTime, privateKey);
        }
        catch (CertificateExpiredException | CertificateNotYetValidException e){
            emailService.sendEmail("gracielastoimenova@gmail.com", "Update your certificate for timestamp application",
                    "Your certificate for timestamping app is no longer valid.");
            return null;
                }
    }


    @Override
    public byte[] concatenateData(byte[] data1, byte[] data2) {
        byte[] combined = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, combined, 0, data1.length);
        System.arraycopy(data2, 0, combined, data1.length, data2.length);
        return combined;
    }

    @Override
    public String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    @Override
    public Timestamps findByFile(byte[] file) {
        return timestampsRepository.findByFile(fileRepository.findByFileContent(file));
    }
    @Override
    public Timestamps findByFileName( String name) {
        return timestampsRepository.findByFile(fileRepository.findByFileName(name));
    }

    @Override
    public List<TimestampResponse> getAllTimestampsByUser() {
        return timestampsRepository.findAllByUser(userService.getCurrentUser());
    }

 


}
