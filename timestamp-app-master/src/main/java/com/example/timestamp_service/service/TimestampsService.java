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

     boolean verifyFile(byte[] file, String fileName);

     byte[] createTimestamp(byte[] fileContent) throws Exception;
     byte[] concatenateData(byte[] data1, byte[] data2);
     String getCurrentTimestamp();
     Timestamps findByFileName(String name);
     Timestamps findByFile(byte[] file);
     List<TimestampResponse> getAllTimestampsByUser();
     List<TimestampResponse> searchByName(String name);
     List<TimestampResponse> getAllTimestamps();

}
