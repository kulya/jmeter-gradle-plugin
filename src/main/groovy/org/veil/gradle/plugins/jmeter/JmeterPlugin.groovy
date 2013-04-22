package org.veil.gradle.plugins.jmeter;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.Convention;

import java.util.concurrent.Callable;

public class JmeterPlugin implements Plugin<Project> {

    public static final String JMETER_RUN = "jmeterRun";

    public static final String JMETER_RUN_GUI = "jmeterEditor";

    public static final String JMETER_PLUGIN_NAME = "jmeter";

    public static final String PERFORMANCE_GROUP = "performance";

    private JmeterRunGuiTask runGuiTask;

    private JmeterRunTask runTask;

    public void apply(final Project project) {
        final JmeterPluginConvention jmeterConvention = new JmeterPluginConvention(project);

        Convention convention = project.getConvention();
        convention.getPlugins().put(JMETER_PLUGIN_NAME, jmeterConvention);

//        runGuiTask = project.task(JMETER_RUN_GUI, group: PERFORMANCE_GROUP, description: "Start JMeter GUI", type: JmeterRunGuiTask)

        project.getTasks().withType(JmeterRunGuiTask.class, new Action<JmeterRunGuiTask>() {
            @Override
            public void execute(JmeterRunGuiTask jmeterRunGuiTask) {
                configureJmeterAbstractTask(project, jmeterConvention, jmeterRunGuiTask);
            }
        });
        JmeterRunGuiTask runGuiTask = project.getTasks().add(JMETER_RUN_GUI, JmeterRunGuiTask.class);
        runGuiTask.setDescription("Start JMeter GUI");
        runGuiTask.setGroup(PERFORMANCE_GROUP);

        project.getTasks().withType(JmeterRunTask.class, new Action<JmeterRunTask>() {
            public void execute(JmeterRunTask jmeterRunTask) {
                configureJmeterTask(project, jmeterConvention, jmeterRunTask);
            }
        });
        JmeterRunTask runTask = project.getTasks().add(JMETER_RUN, JmeterRunTask.class);
        runTask.setDescription("Execute JMeter tests");
        runTask.setGroup(PERFORMANCE_GROUP);


    }

    private void configureJmeterTask(Project project, final JmeterPluginConvention jmeterConvention, JmeterRunTask jmeterRunTask) {
        configureJmeterAbstractTask(project, jmeterConvention, jmeterRunTask);
        jmeterRunTask.getConventionMapping().map("jmeterTestFiles", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getJmeterTestFiles();
            }
        });

        jmeterRunTask.getConventionMapping().map("includes", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getIncludes();
            }
        });

        jmeterRunTask.getConventionMapping().map("excludes", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getExcludes();
            }
        });

        jmeterRunTask.getConventionMapping().map("reportDir", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getReportDir();
            }
        });

        jmeterRunTask.getConventionMapping().map("enableReports", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getEnableReports();
            }
        });

        jmeterRunTask.getConventionMapping().map("remote", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getRemote();
            }
        });

        jmeterRunTask.getConventionMapping().map("jmeterIgnoreFailure", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getJmeterIgnoreFailure();
            }
        });

        jmeterRunTask.getConventionMapping().map("jmeterIgnoreError", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getJmeterIgnoreError();
            }
        });

        jmeterRunTask.getConventionMapping().map("reportPostfix", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getReportPostfix();
            }
        });

        jmeterRunTask.getConventionMapping().map("reportXslt", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getReportXslt();
            }
        });
    }

    private void configureJmeterAbstractTask(Project project, final JmeterPluginConvention jmeterConvention, JmeterAbstractTask jmeterRunGuiTask) {

        jmeterRunGuiTask.getConventionMapping().map("srcDir", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getSrcDir();
            }
        });

        jmeterRunGuiTask.getConventionMapping().map("jmeterUserProperties", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getJmeterUserProperties();
            }
        });

        jmeterRunGuiTask.getConventionMapping().map("jmeterPluginJars", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getJmeterPluginJars();
            }
        });

        jmeterRunGuiTask.getConventionMapping().map("jmeterPropertyFile", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getJmeterPropertyFile();
            }
        });
    }
}
