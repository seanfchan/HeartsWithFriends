package org.bitcoma.hearts.utils;

import org.jasypt.digest.PooledStringDigester;

public class Encryptor {

    private static Encryptor instance = null;
    private PooledStringDigester m_digester = null;

    public Encryptor() {
        m_digester = new PooledStringDigester();
        m_digester.setPoolSize(4); // This would be a good value for a 4-core
                                   // system
        m_digester.setAlgorithm("SHA-1");
        m_digester.setIterations(2000);
    }

    public static Encryptor instance() {
        if (instance == null)
            instance = new Encryptor();
        return instance;
    }

    public String encrypt(String textToEncrypt) {
        return m_digester.digest(textToEncrypt);
    }

    public boolean matches(String input, String encryptedToCompare) {
        return m_digester.matches(input, encryptedToCompare);
    }

    public static void main(String[] args) {

        String pass = "word";
        String[] values = new String[4];
        values[0] = Encryptor.instance().encrypt(pass);
        values[1] = Encryptor.instance().encrypt(pass);
        values[2] = Encryptor.instance().encrypt(pass);
        values[3] = Encryptor.instance().encrypt(pass);

        for (int i = 0; i < values.length; i++) {
            boolean val = Encryptor.instance().matches(pass, values[i]);
            assert (val);
        }

    }
}
