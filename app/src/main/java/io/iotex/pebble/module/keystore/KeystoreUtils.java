package io.iotex.pebble.module.keystore;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigInteger;

public class KeystoreUtils {
    static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static KeystoreFile createWalletFileByAccount(String password, Account account) {
        return Keystore.createStandard(password, Numeric.toBigInt(account.privateKey()));
    }

    public static KeystoreFile createWalletFileByKey(String password, BigInteger privateKey) {
        return Keystore.createStandard(password, privateKey);
    }

    public static BigInteger loadKeyFromWalletFile(String password, String keystore) {
        try {
            KeystoreFile walletFile = objectMapper.readValue(keystore, KeystoreFile.class);
            return Keystore.decrypt(password, walletFile);
        } catch (IOException e) {
            throw new RuntimeException("read keystore json error", e);
        }
    }
}
