package com.dnai.cedre.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.val;

@Configuration
public class HttpClientConfig {
    @Bean
    public HttpClientBuilder httpClientBuilder() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        val builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, (chain, authType) -> true);
        val sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
        return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory);
    }
}
