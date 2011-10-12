package org.veil.gradle.plugins.jmeter;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.Convention;

public class JmeterPlugin implements Plugin<Project>{

    public static final String JMETER_RUN = "jmeterRun";

    public static final String JMETER_PLUGIN_NAME = "jmeter";

    public void apply(Project project) {
        JmeterPluginConvention jmeterConvention = new JmeterPluginConvention();

        Convention convention = project.getConvention();
        convention.getPlugins().put(JMETER_PLUGIN_NAME, jmeterConvention);
    }
}
