package com.example.timestamp_service.service;

import java.security.*;
import java.security.cert.X509Certificate;

public interface SSLService {

    byte[] hashFile(byte[] fileBytes)throws NoSuchAlgorithmException;
    byte[] signWithPrivateKey(byte[] data, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException;

    PrivateKey loadPrivateKey() throws KeyStoreException,  NoSuchAlgorithmException, UnrecoverableKeyException;
    PublicKey loadPublicKey() throws KeyStoreException;
    X509Certificate loadCertificate() throws KeyStoreException;

    boolean verifyWithPublicKey (byte[] data, byte[] signature, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException;

}
