package com.github.kulya.gradle.plugins.jmeter.worker

import com.github.kulya.gradle.plugins.jmeter.JmeterSpecs
import org.gradle.api.file.FileCollection
import org.gradle.process.internal.JavaExecHandleBuilder
import org.gradle.process.internal.WorkerProcess
import org.gradle.process.internal.WorkerProcessBuilder
import org.gradle.internal.Factory


class JmeterWorkerManager {

    public JmeterResult runWorker(File workingDir, Factory<WorkerProcessBuilder> workerFactory,
                                    FileCollection applicationClasspath, JmeterSpecs specs) {
        WorkerProcess workerProcess = createWorkerProcess(workingDir, workerFactory, applicationClasspath, specs)
        workerProcess.start()

        JmeterWorkerClient workerClientCallback = new JmeterWorkerClient()
        workerProcess.connection.addIncoming(JmeterWorkerClientProtocol.class, workerClientCallback)
        JmeterResult result = workerClientCallback.getResult()

        workerProcess.waitForStop()
        return result
    }

    private WorkerProcess createWorkerProcess(File workingDir, Factory<WorkerProcessBuilder> workerFactory,
                                              FileCollection applicationClasspath, JmeterSpecs specs) {
        WorkerProcessBuilder builder = workerFactory.create()
        builder.applicationClasspath(applicationClasspath)
        builder.sharedPackages(Arrays.asList("org.apache.jmeter"))

        JavaExecHandleBuilder javaCommand = builder.getJavaCommand()
        javaCommand.setWorkingDir(workingDir)
        javaCommand.setMaxHeapSize(specs.getMaxHeapSize())

        WorkerProcess workerProcess = builder.worker(/*TODO*/).build()
        return workerProcess
    }
}
