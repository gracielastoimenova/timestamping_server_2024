package com.example.timestamp_service.model.dto;

import com.example.timestamp_service.model.File;

public interface TimestampResponse {
    Long getArchiveNum();
    String getDateAndTimeOfSigning();
    File getFile();



}
