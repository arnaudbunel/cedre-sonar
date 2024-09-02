package com.dnai.cedre.util;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class UserUtil {
    public static void main(String[] args) {
    	String passclear = "test";
    	
    	String crypt = BCrypt.hashpw(passclear, BCrypt.gensalt());
    	
    	System.out.println("Mot de passe : " + passclear);
    	System.out.println("Mot de passe crypté : " + crypt);
    }
}
