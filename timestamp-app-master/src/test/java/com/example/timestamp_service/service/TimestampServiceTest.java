package com.example.timestamp_service.service;

import com.example.timestamp_service.model.File;
import com.example.timestamp_service.model.Timestamps;
import com.example.timestamp_service.model.User;
import com.example.timestamp_service.model.roles.UserRole;
import com.example.timestamp_service.repository.FileRepository;
import com.example.timestamp_service.repository.TimestampsRepository;
import com.example.timestamp_service.service.Impl.TimestampsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.*;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TimestampServiceTest {

    @Mock
    private TimestampsRepository timestampsRepository;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @Mock
    private SSLService sslService;

    @InjectMocks
    private TimestampsServiceImpl timestampsService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        this.timestampsService = spy(new TimestampsServiceImpl(timestampsRepository, fileRepository, userService, emailService, sslService));
    }

    @Test
    public void testTimestampFile() throws Exception {
        byte[] file = "testFile".getBytes();
        String fileName = "testFileName";
        User user = mock(User.class);
        byte[] timestampedFile = "timestampedFile".getBytes();
        File file1 = new File(fileName, file);

        when(sslService.hashFile(any(byte[].class))).thenReturn("hashedFile".getBytes());
        when(sslService.loadPrivateKey()).thenReturn(mock(PrivateKey.class));
        when(sslService.loadCertificate()).thenReturn(mock(X509Certificate.class));
        when(sslService.signWithPrivateKey(any(byte[].class), any(PrivateKey.class))).thenReturn(timestampedFile);
        when(fileRepository.save(any(File.class))).thenReturn(file1);

        ArgumentCaptor<Timestamps> captor = ArgumentCaptor.forClass(Timestamps.class);
        doReturn(file1).when(fileRepository).save(any(File.class));
        doReturn(null).when(timestampsRepository).save(captor.capture());

        UserRole userRole = UserRole.ROLE_USER;
        when(user.getRole()).thenReturn(userRole);

        timestampsService.timestampFile(file, fileName, user);

        Timestamps savedTimestamps = captor.getValue();

        assertNotNull(savedTimestamps);
        assertEquals(fileName, savedTimestamps.getFile().getFileName());
        assertArrayEquals(file, savedTimestamps.getFile().getFileContent());
        assertArrayEquals(timestampedFile, savedTimestamps.getTimestampedFile());

        verify(timestampsRepository, times(1)).save(any(Timestamps.class));
    }
    @Test
    public void testSearchByName() {
        String name = "testFileName";
        User user = mock(User.class);
        UserRole userRole = UserRole.ROLE_USER;
        when(user.getRole()).thenReturn(userRole);
        when(userService.getCurrentUser()).thenReturn(user);

        timestampsService.searchByName(name);

        verify(timestampsRepository).findAllByFile_FileNameContainingAndUser(name, user);
    }

}
