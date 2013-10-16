package com.github.kulya.gradle.plugins.jmeter

import com.github.kulya.gradle.plugins.jmeter.worker.JMeterRunner
import org.gradle.api.GradleException

class JmeterRunGuiTask extends JmeterAbstractTask{

    @Override
    protected void runTaskAction() throws IOException{

        try {
            List<String> args = new ArrayList<String>();
            args.addAll(Arrays.asList(
                    "-p", getJmeterPropertyFile().getCanonicalPath()));

            initUserProperties(args);

            log.debug("JMeter is called with the following command line arguments: " + args.toString());

            JmeterSpecs specs = new JmeterSpecs();
            specs.getSystemProperties().put("search_paths", System.getProperty("search_paths"));
            specs.getSystemProperties().put("jmeter.home", getWorkDir().getAbsolutePath());
            specs.getSystemProperties().put("saveservice_properties", System.getProperty("saveservice_properties"));
            specs.getSystemProperties().put("upgrade_properties", System.getProperty("upgrade_properties"));
            specs.getSystemProperties().put("log_file", System.getProperty("log_file"));
            specs.getJmeterProperties().addAll(args);
            specs.setMaxHeapSize(getMaxHeapSize());
            new JMeterRunner().executeJmeterCommand(specs, getWorkDir().getAbsolutePath());


        } catch (IOException e) {
            throw new GradleException("Can't execute test", e);
        }
    }
}
