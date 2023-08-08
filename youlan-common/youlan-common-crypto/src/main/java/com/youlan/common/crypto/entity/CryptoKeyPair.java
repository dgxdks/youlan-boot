package com.youlan.common.crypto.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@Accessors(chain = true)
public class CryptoKeyPair {

    private PrivateKey privateKey;

    private PublicKey publicKey;

    private String privateKeyBase64;

    private String publicKeyBase64;
}
