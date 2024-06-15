package com.example.timestamp_service.service;

import com.example.timestamp_service.service.Impl.SSLServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import java.security.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class SSLServiceTest {
    @Mock
    private KeyStore keyStore;

    @InjectMocks
    private SSLServiceImpl sslService;

    private PrivateKey realPrivateKey;
    private X509Certificate realCertificate;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Load the actual certificate file and private key
        FileInputStream certFile = new FileInputStream("C:/Users/graci/root_ca/ib_ca/timestamps.p12");
        FileInputStream trustStoreFile = new FileInputStream("C:/Users/graci/root_ca/ib_ca/truststore.jks");

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        KeyStore trustStore = KeyStore.getInstance("JKS");

        keyStore.load(certFile, "timestamp".toCharArray());
        trustStore.load(trustStoreFile, "timestamp".toCharArray());

        // Assuming the alias of the certificate and private key is "1"
        realCertificate = (X509Certificate) keyStore.getCertificate("1");
        realPrivateKey = (PrivateKey) keyStore.getKey("1", "timestamp".toCharArray());

        when(this.keyStore.getCertificate("1")).thenReturn(realCertificate);
        when(this.keyStore.getKey("1", "timestamp".toCharArray())).thenReturn(realPrivateKey);
    }


    @Test
    public void testHashFile() throws NoSuchAlgorithmException {
        byte[] fileBytes = "testFile".getBytes();
        byte[] hashedFile = sslService.hashFile(fileBytes);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] expectedHashedFile = digest.digest(fileBytes);

        assertArrayEquals(expectedHashedFile, hashedFile);
    }

    @Test
    public void testSignWithPrivateKey() throws Exception {
        byte[] data = "testData".getBytes();
        byte[] signature = sslService.signWithPrivateKey(data, realPrivateKey);

        assertNotNull(signature);
        assertTrue(signature.length > 0);
    }

    @Test
    public void testLoadPrivateKey() throws Exception {
        PrivateKey privateKey = sslService.loadPrivateKey();

        assertNotNull(privateKey);
        assertEquals(realPrivateKey, privateKey);
    }

    @Test
    public void testLoadCertificate() throws Exception {
        X509Certificate certificate = sslService.loadCertificate();

        assertNotNull(certificate);
        assertEquals(realCertificate, certificate);
    }
}
