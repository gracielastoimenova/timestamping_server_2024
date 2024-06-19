package com.example.timestamp_service.web.controller;

import com.example.timestamp_service.model.dto.TimestampResponse;
import com.example.timestamp_service.model.exceptions.NoFileForTimestampingException;
import com.example.timestamp_service.service.EmailService;
import com.example.timestamp_service.service.FileService;
import com.example.timestamp_service.service.TimestampsService;
import com.example.timestamp_service.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.*;
import java.util.List;

@Controller
@RequestMapping("/main")
public class TimestampController  {

    private final TimestampsService timestampsService;
    private final UserService userService;
    private final FileService fileService;
    private final EmailService emailService;

    public TimestampController(TimestampsService timestampsService, UserService userService, FileService fileService, EmailService emailService) {
        this.timestampsService = timestampsService;
        this.userService = userService;
        this.fileService = fileService;
        this.emailService = emailService;
    }


    @GetMapping
    public String getMainPage(){
        return "main";
    }

    @GetMapping("/timestamp")
    public String getTimestampPage(){
        return "timestamp";
    }


    @GetMapping("/viewTimestamps")
    public String viewAllTimestampedDocuments(){
        return "viewTimestamps";
    }

    @GetMapping("/verify_timestamp")
    public String getVerifyTimestamp(Model model){
        model.addAttribute("verify", null);
        return "verify";
    }

    @GetMapping("/giveRole")
    public String getGiveRole(Model model){
        model.addAttribute("users", userService.getAllUsersWithoutRole());
        return "giveRole";
    }

    @PostMapping("/giveRole")
    public String giveRolePost(@RequestParam String username, @RequestParam String role, Model model){
        userService.setRole(username, role);
        emailService.sendEmail(userService.geUserEmail(username), "You were given a role for the timestamping app", "The user " + userService
                .getCurrentUser().getUsername() + " has given you the role: "+ role+ ". You can timestamp your documents now");
        model.addAttribute("users", userService.getAllUsersWithoutRole());
        return "giveRole";
    }

    @PostMapping("/timestamp")
    public String timestampDocument(@RequestParam("fileToUpload") MultipartFile fileToBeTimestamped) {
        try {
            timestampsService.timestampFile(
                    fileToBeTimestamped.getBytes(),
                    fileToBeTimestamped.getOriginalFilename(),
                    userService.getCurrentUser());

            return "redirect:/main/viewTimestamps";
        }catch (Exception e) {
            return "redirect:/main/timestamp";
        }

    }

    @PostMapping("/verify_timestamp")
    public String verifyTimestamp(@RequestParam("file") MultipartFile fileToBeChecked, Model model)  {
             try {
                 if (timestampsService.findByFileName(fileToBeChecked.getOriginalFilename()) != null) {
                     model.addAttribute("verify", timestampsService.verifyFile(fileToBeChecked.getBytes(), fileToBeChecked.getOriginalFilename()));
                 } else {
                     model.addAttribute("verify", "not_found");
                 }
                 return "verify";
             } catch (IOException e){
                 System.out.println("verifyTimestamp threw exception controller");
                 return "redirect:/main/verify_timestamp";
             }

    }

    @PostMapping("/viewTimestamps")
    public String search(@RequestParam String searchInput, Model model){

      List<TimestampResponse> timestamp = timestampsService.searchByName(searchInput);
      if (!timestamp.isEmpty())
        model.addAttribute("timestamps", timestampsService.searchByName(searchInput));
      else
          model.addAttribute("timestamps", null);
        return "viewTimestamps";

    }

    @PostMapping("/viewTimestamps/all")
    public String viewTimestampsAll(Model model){
        model.addAttribute("timestamps", timestampsService.getAllTimestamps());
        return "viewTimestamps";
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String fileName)  {
        byte[] fileData = fileService.getFileData(fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(fileData);
    }

}
