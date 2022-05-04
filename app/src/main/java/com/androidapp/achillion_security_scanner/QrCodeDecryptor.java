package com.androidapp.achillion_security_scanner;

public class QrCodeDecryptor {

    public static String getDecryptedData(String binaryCode)
    {
        String[] code = binaryCode.split(" ");
        String word="";
        for(int i=0;i<code.length;i++)
        {
            word+= (char)Integer.parseInt(code[i],2);
        }

        return word;
    }
}
