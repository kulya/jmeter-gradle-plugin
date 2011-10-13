package org.veil.gradle.plugins.jmeter;

import org.gradle.api.GradleException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ErrorScanner {
     private static final String PAT_ERROR = "<error>true</error>";
    private static final String PAT_FAILURE_REQUEST = "s=\"false\"";
    private static final String PAT_FAILURE = "<failure>true</failure>";

    private boolean ignoreErrors;

    private boolean ignoreFailures;

    /**
     *
     * @param ignoreErrors
     *            if an error is found with this scanner it will throw an
     *            exception instead of returning true;
     * @param ignoreFailures
     *            if a failure is found with this scanner it will throw an
     *            exception instead of returning true;
     */
    public ErrorScanner(boolean ignoreErrors, boolean ignoreFailures) {
        this.ignoreErrors = ignoreErrors;
        this.ignoreFailures = ignoreFailures;
    }

    public boolean scanForProblems(File file) throws IOException {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                this.lineContainsForErrors(line);
            }
        } finally {
            in.close();
        }
        return false;
    }

    /**
     * protected for testing
     * @param line
     * @return
     */
    protected boolean lineContainsForErrors(String line) {
    	if (line.contains(PAT_ERROR)) {
            if (this.ignoreErrors) {
                return true;
            } else {
                throw new GradleException("There were test errors.  See the jmeter logs for details.");
            }
        }
        if (line.contains(PAT_FAILURE) || line.contains(PAT_FAILURE_REQUEST)) {
            if (this.ignoreFailures) {
                return true;
            } else {
                throw new GradleException("There were test failures.  See the jmeter logs for details.");
            }
        }
        return false;
    }
}
