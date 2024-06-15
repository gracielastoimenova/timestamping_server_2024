package com.example.timestamp_service.service.Impl;


import com.example.timestamp_service.repository.FileRepository;
import com.example.timestamp_service.service.FileService;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {

private final FileRepository fileRepository;

    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public byte[] getFileData(String file_name) {
        return fileRepository.findByFileName(file_name).getFileContent();
    }


}
