package org.veil.gradle.plugins.jmeter;

import org.apache.commons.io.IOUtils;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JmeterRunTask extends ConventionTask{

    private static final Logger log = LoggerFactory.getLogger(JmeterRunTask.class);

    /**
     * Path to a Jmeter test XML file.
     * Relative to srcDir.
     * May be declared instead of the parameter includes.
     */
    private File jmeterTestFile;

    /**
     * Directory under which JMeter test XML files are stored.
     *
     * By default it src/test/jmeter
     */
    private File srcDir;

    /**
     * Sets the list of include patterns to use in directory scan for JMeter Test XML files.
     * Relative to srcDir.
     * May be declared instead of a single jmeterTestFile.
     * Ignored if parameter jmeterTestFile is given.
     */
    private List<String> includes;

    /**
     * Sets the list of exclude patterns to use in directory scan for Test files.
     * Relative to srcDir.
     * Ignored if parameter jmeterTestFile file is given.
     */
    private List<String> excludes;

    /**
     * Directory in which the reports are stored.
     *
     * By default build/jmeter-report"
     */
    private File reportDir;

     /**
     * Whether or not to generate reports after measurement.
     *
     * By default true
     */
    private boolean enableReports;

    /**
     * Use remote JMeter installation to run tests
     *
     * By default false
     */
    private boolean remote;

    /**
     * Sets whether ErrorScanner should ignore failures in JMeter result file.
     *
     * By default false
     */
    private boolean jmeterIgnoreFailure;

    /**
     * Sets whether ErrorScanner should ignore errors in JMeter result file.
     *
     * By default false
     */
    private boolean jmeterIgnoreError;

    /**
     * Postfix to add to report file.
     *
     * By default "-report.html"
     */
    private String reportPostfix;

    private File workDir;
    private File jmeterLog;

    @TaskAction
    public void start() throws IOException {

        initJmeterSystemProperties();


    }

    private void initJmeterSystemProperties() throws TaskExecutionException {
        workDir = new File(getProject().getBuildDir(), "/jmeter");
        workDir.mkdirs();



        jmeterLog = new File(workDir, "jmeter.log");
        try {
            System.setProperty("log_file", jmeterLog.getCanonicalPath());
        } catch (IOException e) {
            throw new GradleException("Can't get canonical path for log file", e);
        }
    }

    private void initTempProperties() throws IOException {
        List<File> tempProperties = new ArrayList<File>();

        File saveServiceProperties = new File(workDir, "saveservice.properties");
        System.setProperty("saveservice_properties", saveServiceProperties.getCanonicalPath());
        tempProperties.add(saveServiceProperties);

        File upgradeProperties = new File(workDir, "upgrade.properties");
        System.setProperty("upgrade_properties", saveServiceProperties.getCanonicalPath());
        tempProperties.add(upgradeProperties);

        for (File f : tempProperties) {
            try {
                FileWriter writer = new FileWriter(f);
                IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream(f.getName()), writer);
                writer.flush();
                writer.close();
            } catch (IOException ioe) {
                throw new GradleException("Couldn't create temporary property file " + f.getName() + " in directory " + workDir.getPath(), ioe);
            }

        }


    }
}
