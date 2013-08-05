package com.github.kulya.gradle.plugins.jmeter.worker

import com.github.kulya.gradle.plugins.jmeter.ExitException
import com.github.kulya.gradle.plugins.jmeter.JmeterSpecs
import org.apache.jmeter.JMeter
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

import java.security.Permission

class JmeterExecuter {

    private final static Logger LOGGER = Logging.getLogger(JmeterExecuter.class)

    JmeterResult runJmeter(JmeterSpecs specs) {
        SecurityManager oldManager = System.getSecurityManager();
        Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();

        try {
            setCustomSecurityManager()
            setCustomUncaughtExceptionHandler()

            JMeter jmeter = new JMeter()
            jmeter.start({})

        } finally {
            System.setSecurityManager(oldManager)
            Thread.setDefaultUncaughtExceptionHandler(oldHandler)
        }

        return null
    }

    void setCustomSecurityManager() {
        System.setSecurityManager(new SecurityManager() {

            @Override
            public void checkExit(int status) {
                throw new ExitException(status);
            }

            @Override
            public void checkPermission(Permission perm, Object context) {
            }

            @Override
            public void checkPermission(Permission perm) {
            }
        });
    }

    void setCustomUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            public void uncaughtException(Thread t, Throwable e) {
                if (e instanceof ExitException && ((ExitException) e).getCode() == 0) {
                    return; // Ignore
                }
                LOGGER.error("Error in thread " + t.getName());
            }
        });
    }
}
