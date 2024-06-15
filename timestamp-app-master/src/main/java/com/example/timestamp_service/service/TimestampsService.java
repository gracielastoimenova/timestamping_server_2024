package com.example.timestamp_service.service;

import com.example.timestamp_service.model.dto.TimestampResponse;
import com.example.timestamp_service.model.Timestamps;
import com.example.timestamp_service.model.User;

import java.security.*;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Optional;

public interface TimestampsService {

     void timestampFile(byte[] file, String fileName, User user) throws Exception;
     byte[] createTimestamp(byte[] fileContent) throws Exception;
     byte[] concatenateData(byte[] data1, byte[] data2);
     String getCurrentTimestamp();
     Timestamps findByFile(byte[] file);
     List<TimestampResponse> getAllTimestampsByUser();
     boolean verifyFile(byte[] file) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, SignatureException, InvalidKeyException;
     List<TimestampResponse> searchByName(String name);
     List<TimestampResponse> getAllTimestamps();

}
