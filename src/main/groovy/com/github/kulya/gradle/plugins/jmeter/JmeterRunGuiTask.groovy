package com.github.kulya.gradle.plugins.jmeter

import org.apache.jmeter.JMeter
import org.gradle.api.GradleException

class JmeterRunGuiTask extends JmeterAbstractTask{

    @Override
    protected void runTaskAction() throws IOException{
        SecurityManager oldManager = System.getSecurityManager();
        Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();

        try {
            List<String> args = new ArrayList<String>();
            args.addAll(Arrays.asList(
                    "-d", getProject().getProjectDir().getCanonicalPath(),
                    "-p", getJmeterPropertyFile().getCanonicalPath()));

            initUserProperties(args);

            log.debug("JMeter is called with the following command line arguments: " + args.toString());

//            setCustomSecurityManager();

//            setCustomUncaughtExceptionHandler();

            try {
                JMeter jmeter = new JMeter();
                jmeter.start(args.toArray().asType(String []));
                BufferedReader reader = new BufferedReader(new FileReader(getJmeterLog()));
                while (!checkForEndOfTest(reader)) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            } catch (ExitException ee) {
                if (ee.getCode() != 0) {
                    throw new GradleException("Test failed", ee);
                } else {
                    return;
                }
            } finally {
                System.setSecurityManager(oldManager);
                Thread.setDefaultUncaughtExceptionHandler(oldHandler);
            }

        } catch (IOException e) {
            throw new GradleException("Can't execute test", e);
        }
    }

    private boolean checkForEndOfTest(BufferedReader reader) {
        boolean testEnded = false;
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Test has ended")) {
                    testEnded = true;
                    break;
                } else if (line.contains("Could not open")) {
                    testEnded = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new GradleException("Can't read log file", e);
        }
        return testEnded;
    }
}
