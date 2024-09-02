package com.dnai.cedre.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProfileUtilService {

    @Autowired
    private Environment env;

    public boolean isDevProfile() {
        boolean isDevProfile = false;

        String[] profiles = env.getActiveProfiles();
        for(String profile : profiles) {
            if("dev".equals(profile)) {
                isDevProfile = true;
                break;
            }
        }

        return isDevProfile;
    }
}
