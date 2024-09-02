package com.dnai.cedre.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordUtil {
	
	private final static String CHAR_CODEUNIQUE = "abcdefghijklmnopqrstuvwxyz";
	private final static String SEPARATEUR = " ";
	
    public static void main(String[] args) {
    	
    	int nbcomptes = 1;
    	List<String> listComptes = new ArrayList<>();
    	
    	for(int i=0;i<nbcomptes;i++) {
    		String passclear = RandomStringUtils.random(6,CHAR_CODEUNIQUE);
    		String crypt = BCrypt.hashpw(passclear, BCrypt.gensalt());
    		String compte = passclear + SEPARATEUR + crypt;
    		listComptes.add(compte);
    		System.out.println(compte);
    	}
    	
    	System.out.println("");
    	
    	for(String compte : listComptes) {
    		System.out.println(compte.split(SEPARATEUR)[0]);
    	}
    	System.out.println("");
    	for(String compte : listComptes) {
    		System.out.println(compte.split(SEPARATEUR)[1]);
    	}
    }
}
