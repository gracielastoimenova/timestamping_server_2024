package com.example.timestamp_service.web;

import com.example.timestamp_service.model.Timestamps;
import com.example.timestamp_service.model.User;
import com.example.timestamp_service.model.dto.TimestampResponse;
import com.example.timestamp_service.service.EmailService;
import com.example.timestamp_service.service.FileService;
import com.example.timestamp_service.service.TimestampsService;
import com.example.timestamp_service.service.UserService;
import com.example.timestamp_service.web.controller.TimestampController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TimestampControllerTest {

    @Mock
    private TimestampsService timestampsService;

    @Mock
    private UserService userService;

    @Mock
    private FileService fileService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TimestampController timestampController;

    private User mockUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@domain.com");
        when(userService.getCurrentUser()).thenReturn(mockUser);
    }

    @Test
    public void testGetMainPage() {
        String view = timestampController.getMainPage();
        assertEquals("main", view);
    }

    @Test
    public void testGetTimestampPage() {
        String view = timestampController.getTimestampPage();
        assertEquals("timestamp", view);
    }

    @Test
    public void testViewAllTimestampedDocuments() {
        String view = timestampController.viewAllTimestampedDocuments();
        assertEquals("viewTimestamps", view);
    }

    @Test
    public void testGetVerifyTimestamp() {
        Model model = mock(Model.class);
        String view = timestampController.getVerifyTimestamp(model);
        assertEquals("verify", view);
        verify(model).addAttribute("verify", null);
    }

    @Test
    public void testTimestampDocument() throws Exception {
        MultipartFile fileToBeTimestamped = mock(MultipartFile.class);
        byte[] fileBytes = "testFile".getBytes();
        when(fileToBeTimestamped.getBytes()).thenReturn(fileBytes);
        when(fileToBeTimestamped.getOriginalFilename()).thenReturn("testFile");

        String view = timestampController.timestampDocument(fileToBeTimestamped);

        verify(timestampsService).timestampFile(fileBytes, "testFile", mockUser);
        assertEquals("redirect:/main/viewTimestamps", view);
    }


    @Test
    public void testTimestampDocumentException() throws Exception {
        MultipartFile fileToBeTimestamped = mock(MultipartFile.class);
        byte[] fileBytes = "testFile".getBytes();
        when(fileToBeTimestamped.getBytes()).thenReturn(fileBytes);
        when(fileToBeTimestamped.getOriginalFilename()).thenReturn("testFile");

        doThrow(new RuntimeException()).when(timestampsService).timestampFile(fileBytes, "testFile", mockUser);

        String view = timestampController.timestampDocument(fileToBeTimestamped);

        assertEquals("redirect:/main/timestamp", view);
    }

    @Test
    public void testVerifyTimestamp() throws Exception {
        MultipartFile fileToBeChecked = mock(MultipartFile.class);
        byte[] fileBytes = "testFile".getBytes();
        when(fileToBeChecked.getBytes()).thenReturn(fileBytes);
        Model model = mock(Model.class);

        when(timestampsService.findByFile(fileBytes)).thenReturn(mock(Timestamps.class));
        when(timestampsService.verifyFile(fileBytes)).thenReturn(true);

        String view = timestampController.verifyTimestamp(fileToBeChecked, model);

        verify(model).addAttribute("verify", true);
        assertEquals("verify", view);
    }

    @Test
    public void testVerifyTimestampNotFound() throws Exception {
        MultipartFile fileToBeChecked = mock(MultipartFile.class);
        byte[] fileBytes = "testFile".getBytes();
        when(fileToBeChecked.getBytes()).thenReturn(fileBytes);
        Model model = mock(Model.class);

        when(timestampsService.findByFile(fileBytes)).thenReturn(null);

        String view = timestampController.verifyTimestamp(fileToBeChecked, model);

        verify(model).addAttribute("verify", "not_found");
        assertEquals("verify", view);
    }

    @Test
    public void testVerifyTimestampException() throws Exception {
        MultipartFile fileToBeChecked = mock(MultipartFile.class);
        byte[] fileBytes = "testFile".getBytes();
        when(fileToBeChecked.getBytes()).thenReturn(fileBytes);
        Model model = mock(Model.class);

        doThrow(new RuntimeException()).when(timestampsService).findByFile(fileBytes);

        String view = timestampController.verifyTimestamp(fileToBeChecked, model);

        assertEquals("redirect:/main/verify_timestamp", view);
    }

    @Test
    public void testSearch() {
        String searchInput = "testFile";
        Model model = mock(Model.class);
        List<TimestampResponse> timestampResponses = List.of(mock(TimestampResponse.class));

        when(timestampsService.searchByName(searchInput)).thenReturn(timestampResponses);

        String view = timestampController.search(searchInput, model);

        verify(model).addAttribute("timestamps", timestampResponses);
        assertEquals("viewTimestamps", view);
    }

    @Test
    public void testSearchNoResults() {
        String searchInput = "testFile";
        Model model = mock(Model.class);

        when(timestampsService.searchByName(searchInput)).thenReturn(List.of());

        String view = timestampController.search(searchInput, model);

        verify(model).addAttribute("timestamps", null);
        assertEquals("viewTimestamps", view);
    }

    @Test
    public void testViewTimestampsAll() {
        Model model = mock(Model.class);
        List<TimestampResponse> timestampResponses = List.of(mock(TimestampResponse.class));

        when(timestampsService.getAllTimestamps()).thenReturn(timestampResponses);

        String view = timestampController.viewTimestampsAll(model);

        verify(model).addAttribute("timestamps", timestampResponses);
        assertEquals("viewTimestamps", view);
    }

    @Test
    public void testGiveRoleGet() {
        Model model = mock(Model.class);
        List<User> usersWithoutRole = List.of(mock(User.class));

        when(userService.getAllUsersWithoutRole()).thenReturn(usersWithoutRole);

        String view = timestampController.getGiveRole(model);

        verify(model).addAttribute("users", usersWithoutRole);
        assertEquals("giveRole", view);
    }

    @Test
    public void testGiveRolePost() {
        String username = "testuser";
        String role = "admin";
        Model model = mock(Model.class);

        when(userService.geUserEmail(username)).thenReturn("test@domain.com");

        String view = timestampController.giveRolePost(username, role, model);

        verify(userService).setRole(username, role);
        verify(emailService).sendEmail("test@domain.com", "You were given a role for the timestamping app", "The user " + userService
                .getCurrentUser().getUsername() + " has given you the role: " + role + ". You can timestamp your documents now");
        verify(model).addAttribute("users", userService.getAllUsersWithoutRole());
        assertEquals("giveRole", view);
    }

    @Test
    public void testDownloadFile() {
        String fileName = "testFile";
        byte[] fileData = "testFileData".getBytes();

        when(fileService.getFileData(fileName)).thenReturn(fileData);

        ResponseEntity<byte[]> response = timestampController.downloadFile(fileName);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("attachment; filename=\"testFile\"", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(fileData, response.getBody());
    }
}
