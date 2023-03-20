package com.compuware.jenkins.common.configuration;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HostConnectionProps {

    public static String getProperties(String key) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = loader.getResourceAsStream("hostConnectionTest.properties");
        Properties props = new Properties();
        props.load(inputStream);
        return props.getProperty(key);
    }

}
