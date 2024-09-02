package com.dnai.cedre.service;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public class TokenService {

	private final static String UNKNOWN = "unknown";
	
	public String calculToken(HttpServletRequest httpServletRequest) {
		return UUID.randomUUID().toString();
	}
	
	public String getAdresseIp(HttpServletRequest httpServletRequest) {
		String ip = httpServletRequest.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getRemoteAddr();
		}
		return ip;
	}	
}
