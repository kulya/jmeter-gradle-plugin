package org.veil.gradle.plugins.jmeter;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.plugins.Convention;
import org.gradle.api.tasks.ConventionValue;

public class JmeterPlugin implements Plugin<Project>{

    public static final String JMETER_RUN = "jmeterRun";

    public static final String JMETER_PLUGIN_NAME = "jmeter";

    public void apply(final Project project) {
        final JmeterPluginConvention jmeterConvention = new JmeterPluginConvention(project);

        Convention convention = project.getConvention();
        convention.getPlugins().put(JMETER_PLUGIN_NAME, jmeterConvention);
        project.getTasks().withType(JmeterRunTask.class, new Action<JmeterRunTask>() {
            public void execute(JmeterRunTask jmeterRunTask) {
                configureJmeterTask(project, jmeterConvention, jmeterRunTask);
            }
        });
        JmeterRunTask runTask = project.getTasks().add(JMETER_RUN, JmeterRunTask.class);
        runTask.setDescription("Execute JMeter tests");
    }

    private void configureJmeterTask(Project project, final JmeterPluginConvention jmeterConvention, JmeterRunTask jmeterRunTask) {
         jmeterRunTask.getConventionMapping().map("jmeterTestFiles", new ConventionValue() {
             public Object getValue(Convention convention, IConventionAware iConventionAware) {
                 return jmeterConvention.getJmeterTestFiles();
             }
         });

        jmeterRunTask.getConventionMapping().map("srcDir", new ConventionValue() {
             public Object getValue(Convention convention, IConventionAware iConventionAware) {
                 return jmeterConvention.getSrcDir();
             }
         });

        jmeterRunTask.getConventionMapping().map("includes", new ConventionValue() {
             public Object getValue(Convention convention, IConventionAware iConventionAware) {
                 return jmeterConvention.getIncludes();
             }
         });

        jmeterRunTask.getConventionMapping().map("excludes", new ConventionValue() {
             public Object getValue(Convention convention, IConventionAware iConventionAware) {
                 return jmeterConvention.getExcludes();
             }
         });

        jmeterRunTask.getConventionMapping().map("reportDir", new ConventionValue() {
             public Object getValue(Convention convention, IConventionAware iConventionAware) {
                 return jmeterConvention.getReportDir();
             }
         });

        jmeterRunTask.getConventionMapping().map("enableReports", new ConventionValue() {
             public Object getValue(Convention convention, IConventionAware iConventionAware) {
                 return jmeterConvention.getEnableReports();
             }
         });

        jmeterRunTask.getConventionMapping().map("remote", new ConventionValue() {
             public Object getValue(Convention convention, IConventionAware iConventionAware) {
                 return jmeterConvention.getRemote();
             }
         });

        jmeterRunTask.getConventionMapping().map("jmeterIgnoreFailure", new ConventionValue() {
             public Object getValue(Convention convention, IConventionAware iConventionAware) {
                 return jmeterConvention.getJmeterIgnoreFailure();
             }
         });

        jmeterRunTask.getConventionMapping().map("jmeterIgnoreError", new ConventionValue() {
             public Object getValue(Convention convention, IConventionAware iConventionAware) {
                 return jmeterConvention.getJmeterIgnoreError();
             }
         });

        jmeterRunTask.getConventionMapping().map("reportPostfix", new ConventionValue() {
             public Object getValue(Convention convention, IConventionAware iConventionAware) {
                 return jmeterConvention.getReportPostfix();
             }
         });

        jmeterRunTask.getConventionMapping().map("reportXslt", new ConventionValue() {
             public Object getValue(Convention convention, IConventionAware iConventionAware) {
                 return jmeterConvention.getReportXslt();
             }
         });

        jmeterRunTask.getConventionMapping().map("jmeterUserProperties", new ConventionValue() {
            public Object getValue(Convention convention, IConventionAware iConventionAware) {
                return jmeterConvention.getJmeterUserProperties();
            }
        });
    }
}
