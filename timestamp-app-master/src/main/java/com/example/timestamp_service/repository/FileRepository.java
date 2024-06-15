package com.example.timestamp_service.repository;

import com.example.timestamp_service.model.File;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FileRepository extends JpaRepository<File, Long> {
    File findByFileName(String file_name);
    File findByFileContent(byte[] content);
}
