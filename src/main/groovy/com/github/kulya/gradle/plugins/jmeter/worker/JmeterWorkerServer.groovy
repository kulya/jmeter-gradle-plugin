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
        JmeterResult result = executeJmeterTest()
        JmeterWorkerClientProtocol clientProtocol = workerProcessContext.getServerConnection()
                .addOutgoing(JmeterWorkerClientProtocol.class)
        clientProtocol.executed(result)
    }

    private JmeterResult executeJmeterTest() {
        LOGGER.debug("Executing JMeter worker")
        try {
            JmeterExecuter jmeterExecuter = new JmeterExecuter()
            return jmeterExecuter.runJmeter(specs)
        } catch (Exception e) {
            LOGGER.error("Exception occured during jmeter execution", e)
            return null
        }
    }
}
