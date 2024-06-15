package com.example.timestamp_service.service;


import com.example.timestamp_service.model.File;
import com.example.timestamp_service.repository.FileRepository;
import com.example.timestamp_service.service.Impl.FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.*;

public class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileServiceImpl fileService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        this.fileService = Mockito.spy(new FileServiceImpl(this.fileRepository));
    }

    @Test
    public void testGetFileData() {
        String fileName = "testFile";
        byte[] fileContent = "fileContent".getBytes();
        File file = new File(fileName, fileContent);

        when(fileRepository.findByFileName(fileName)).thenReturn(file);

        byte[] result = fileService.getFileData(fileName);

        assertArrayEquals(fileContent, result);
        verify(fileRepository).findByFileName(fileName);
    }
}
