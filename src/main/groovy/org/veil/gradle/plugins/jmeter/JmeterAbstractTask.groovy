package org.veil.gradle.plugins.jmeter

import org.apache.commons.io.IOUtils
import org.gradle.api.GradleException
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.security.Permission


abstract class JmeterAbstractTask extends ConventionTask{

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private String jmeterVersion;

    private List<String> jmeterPluginJars;

    private File workDir;

    private File jmeterLog;

    private List<String> jmeterUserProperties;

    /**
     * Directory under which JMeter test XML files are stored.
     * <p/>
     * By default it src/test/jmeter
     */
    private File srcDir;

    private File jmeterPropertyFile;

    @TaskAction
    public void start() {
        loadJMeterVersion();

        loadPropertiesFromConvention();

        initJmeterSystemProperties();
        runTaskAction();
    }

    protected abstract void runTaskAction() throws IOException;

    protected void loadPropertiesFromConvention() {
        jmeterPropertyFile = getJmeterPropertyFile()
        jmeterPluginJars = getJmeterPluginJars()
        jmeterUserProperties = getJmeterUserProperties()
        srcDir = getSrcDir()
    }


    protected void loadJMeterVersion() {
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("jmeter-plugin.properties")
            Properties pluginProps = new Properties()
            pluginProps.load(is)
            jmeterVersion = pluginProps.getProperty("jmeter.version", null)
            if (jmeterVersion == null) {
                throw new GradleException("You should set correct jmeter.version at jmeter-plugin.properies file")
            }
        } catch (Exception e) {
            log.error("Can't load JMeter version, build will stop", e)
            throw new GradleException("Can't load JMeter version, build will stop", e)
        }
    }

    protected void initJmeterSystemProperties() throws IOException {
        workDir = new File(getProject().getBuildDir(), "/jmeter")
        workDir.mkdirs()


        jmeterLog = new File(workDir, "jmeter.log")
        try {
            System.setProperty("log_file", jmeterLog.getCanonicalPath())
        } catch (IOException e) {
            throw new GradleException("Can't get canonical path for log file", e)
        }
        initTempProperties()
        resolveJmeterSearchPath()
    }

    protected void resolveJmeterSearchPath() {
        String cp = ""
        URL[] classPath = ((URLClassLoader)this.getClass().getClassLoader()).getURLs()
        String jmeterVersionPattern = getJmeterVersion().replaceAll("[.]", "[.]")
        for (URL dep : classPath) {
            if (dep.getPath().matches("^.*org[.]apache[.]jmeter[/]jmeter-.*" +
                    jmeterVersionPattern + ".jar\$")) {
                cp += dep.getPath() + ";"
            } else if (dep.getPath().matches("^.*bsh.*[.]jar\$")) {
                cp += dep.getPath() + ";"
            } else if (jmeterPluginJars != null){
                for (String plugin: jmeterPluginJars) {
                    if(dep.getPath().matches("^.*" + plugin + "\$")) {
                        cp += dep.getPath() + ";"
                    }
                }
            }
        }
        System.setProperty("search_paths", cp);
    }

    protected void initTempProperties() throws IOException {
        List<File> tempProperties = new ArrayList<File>();
        String relativeBuildDir = getProject().getBuildDir().getAbsolutePath().substring(getProject().getProjectDir().getAbsolutePath().length());
        String jmeterResultDir = File.separator +  relativeBuildDir + File.separator + "jmeter" + File.separator;

        File saveServiceProperties = new File(workDir, "saveservice.properties");
        System.setProperty("saveservice_properties", jmeterResultDir + saveServiceProperties.getName());
        tempProperties.add(saveServiceProperties);

        File upgradeProperties = new File(workDir, "upgrade.properties");
        System.setProperty("upgrade_properties", jmeterResultDir + saveServiceProperties.getName());
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

    protected void initUserProperties(List<String> jmeterArgs) {
        if (jmeterUserProperties != null) {
            jmeterUserProperties.each {property -> jmeterArgs.add("-J" + property)}
        }
    }

    protected void setCustomUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            public void uncaughtException(Thread t, Throwable e) {
                if (e instanceof ExitException && ((ExitException) e).getCode() == 0) {
                    return; // Ignore
                }
                log.error("Error in thread " + t.getName());
            }
        });
    }

    protected void setCustomSecurityManager() {
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

    String getJmeterVersion() {
        return jmeterVersion
    }

    void setJmeterVersion(String jmeterVersion) {
        this.jmeterVersion = jmeterVersion
    }

    List<String> getJmeterPluginJars() {
        return jmeterPluginJars
    }

    void setJmeterPluginJars(List<String> jmeterPluginJars) {
        this.jmeterPluginJars = jmeterPluginJars
    }

    File getWorkDir() {
        return workDir
    }

    void setWorkDir(File workDir) {
        this.workDir = workDir
    }

    File getJmeterLog() {
        return jmeterLog
    }

    void setJmeterLog(File jmeterLog) {
        this.jmeterLog = jmeterLog
    }

    List<String> getJmeterUserProperties() {
        return jmeterUserProperties
    }

    void setJmeterUserProperties(List<String> jmeterUserProperties) {
        this.jmeterUserProperties = jmeterUserProperties
    }

    File getSrcDir() {
        return srcDir
    }

    void setSrcDir(File srcDir) {
        this.srcDir = srcDir
    }

    File getJmeterPropertyFile() {
        this.jmeterPropertyFile
    }

    void setJmeterPropertyFile(File jmeterPropertyFile) {
        this.jmeterPropertyFile = jmeterPropertyFile
    }
}
