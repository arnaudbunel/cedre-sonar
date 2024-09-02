package com.dnai.cedre.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Objects;

public final class ApplicationContextHolder implements ApplicationContextAware {
    private enum InstanceHolder {
        INSTANCE;

        private ApplicationContext applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return InstanceHolder.INSTANCE.applicationContext;
    }

    @Override
    public void setApplicationContext(final ApplicationContext context) {
        InstanceHolder.INSTANCE.applicationContext = Objects.requireNonNull(context);
    }
}
