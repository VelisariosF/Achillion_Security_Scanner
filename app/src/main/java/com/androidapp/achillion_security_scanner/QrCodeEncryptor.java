package com.androidapp.achillion_security_scanner;

public class QrCodeEncryptor {
    public static String getEcryptedData(String text)
    {
        String bString="";
        String temp="";
        for(int i=0;i<text.length();i++)
        {
            temp=Integer.toBinaryString(text.charAt(i));
            for(int j=temp.length();j<8;j++)
            {
                temp="0"+temp;
            }
            bString+=temp+" ";
        }


        return bString;
    }
}
