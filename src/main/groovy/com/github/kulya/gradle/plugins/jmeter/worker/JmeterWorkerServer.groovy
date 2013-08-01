package com.github.kulya.gradle.plugins.jmeter.worker

import com.github.kulya.gradle.plugins.jmeter.JmeterSpecs
import org.gradle.api.Action
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.process.internal.WorkerProcessContext


class JmeterWorkerServer implements Action<WorkerProcessContext>, Serializable{

    private static final Logger LOGGER = Logging.getLogger(JmeterWorkerServer.class)

    private JmeterSpecs specs

    JmeterWorkerServer(JmeterSpecs specs) {
        this.specs = specs
    }

    @Override
    void execute(WorkerProcessContext workerProcessContext) {

    }
}
