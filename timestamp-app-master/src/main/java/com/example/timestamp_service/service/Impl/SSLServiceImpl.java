package com.example.timestamp_service.service.Impl;

import com.example.timestamp_service.service.SSLService;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.cert.X509Certificate;

@Service
public class SSLServiceImpl implements SSLService {
    private final KeyStore keyStore;

    public SSLServiceImpl(KeyStore keyStore) {
        this.keyStore = keyStore;
    }


    @Override
    public byte[] hashFile(byte[] fileBytes) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(fileBytes);
    }

    @Override
    public  byte[] signWithPrivateKey(byte[] data, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    @Override
    public PrivateKey loadPrivateKey() throws KeyStoreException,  NoSuchAlgorithmException, UnrecoverableKeyException {
        String alias = "1";
        String password = "timestamp";

        return (PrivateKey) keyStore.getKey(alias,password.toCharArray());
    }

    @Override
    public X509Certificate loadCertificate() throws KeyStoreException {
        String alias = "1";
        return (X509Certificate) keyStore.getCertificate(alias);
    }

}
