package com.dnai.cedre.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class CommonUtilService {


    public double parseDouble(final String dstr) {
        double valdouble = 0;
        try {
            if(dstr!=null) {
                valdouble = Double.parseDouble(dstr);
            }
        }catch(Exception e) {
            log.warn("parseDouble : dstr : {}",dstr,e);
        }
        return valdouble;
    }

    public String calculLastmaj() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return now.format(formatter);
    }
}
