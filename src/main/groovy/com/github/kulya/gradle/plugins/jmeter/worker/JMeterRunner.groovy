package com.github.kulya.gradle.plugins.jmeter.worker

import com.github.kulya.gradle.plugins.jmeter.JmeterSpecs
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

class JMeterRunner {

    private final static Logger LOGGER = Logging.getLogger(JMeterRunner.class)

    void executeJmeterCommand(JmeterSpecs specs, String workingDirectory) {
        ProcessBuilder processBuilder = new ProcessBuilder(createArgumentList(specs, workingDirectory))
        processBuilder.redirectErrorStream(true)
        processBuilder.directory(new File(workingDirectory))
        Process p = processBuilder.start()
        int processResult = p.waitFor()
        if (processResult != 0) {
            throw new GradleException("Something goes wrong when we execute jmeter test.... Please see jmeter logs for more information")
        }
    }

    private String[] createArgumentList(JmeterSpecs specs, String workDir) {
        String javaRuntime = "java"

        List<String> argumentsList = new ArrayList<>()
        argumentsList.add(javaRuntime)
        argumentsList.add("-Xms${specs.maxHeapSize}".toString())
        argumentsList.add("-Xmx${specs.maxHeapSize}".toString())
        specs.getSystemProperties().each {k,v ->
            argumentsList.add("-D$k=$v".toString())
        }
        argumentsList.add("-cp")
        argumentsList.add(workDir + File.separator + "lib" + System.getProperty("path.separator") +
                workDir + File.separator + "lib" + File.separator + "ext" + System.getProperty("path.separator") +
                getCurrentClassPath())
        argumentsList.add("org.apache.jmeter.NewDriver")
        argumentsList.addAll(specs.jmeterProperties)
        LOGGER.debug("Command to run is $argumentsList")
        argumentsList.toArray(new String[argumentsList.size()])
    }

    private String getCurrentClassPath() {
        StringBuilder builder = new StringBuilder();
        URL[] classPath = ((URLClassLoader)this.getClass().getClassLoader()).getURLs()
        classPath.each {u ->
            builder.append(u.getPath())
            builder.append(System.getProperty("path.separator"))
        }
        builder.substring(0, builder.size() - 1)
    }
}
