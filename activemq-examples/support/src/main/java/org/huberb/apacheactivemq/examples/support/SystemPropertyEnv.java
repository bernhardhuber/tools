/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.huberb.apacheactivemq.examples.support;

/**
 *
 * @author pi
 */
public class SystemPropertyEnv {
    
    public String fromSystemPropertyEnv(String key, String defaultValue) {
        String rc = fromSystemProperty(key, null);
        if (rc == null) {
            rc = fromEnv(key, defaultValue);
        }
        return rc;
    }

    public String fromSystemProperty(String key, String defaultValue) {
        String rc = System.getProperty(key, defaultValue);
        return rc;
    }

    public String fromEnv(String key, String defaultValue) {
        String rc = System.getenv(key);
        if (rc == null) {
            return defaultValue;
        }
        return rc;
    }

    //---
    public static String env(String key, String defaultValue) {
        String rc = new SystemPropertyEnv().fromSystemPropertyEnv(key, defaultValue);
        return rc;
    }
    
}
