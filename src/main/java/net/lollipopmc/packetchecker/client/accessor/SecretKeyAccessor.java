package net.lollipopmc.packetchecker.client.accessor;

import javax.crypto.SecretKey;

public class SecretKeyAccessor {

    private static SecretKey secretKey;

    public static SecretKey getSecretKey() {
        return secretKey;
    }

    public static void setSecretKey(SecretKey secretKey) {
        SecretKeyAccessor.secretKey = secretKey;
    }
}
