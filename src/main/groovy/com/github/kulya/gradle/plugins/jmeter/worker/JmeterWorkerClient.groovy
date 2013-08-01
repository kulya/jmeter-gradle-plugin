package com.github.kulya.gradle.plugins.jmeter.worker

import org.gradle.internal.UncheckedException

import java.util.concurrent.BlockingQueue
import java.util.concurrent.SynchronousQueue


class JmeterWorkerClient implements JmeterWorkerClientProtocol{

    private final BlockingQueue<JmeterResult> resultQueue = new SynchronousQueue<>();

    public JmeterResult getResult() {
        try {
            resultQueue.take()
        } catch (InterruptedException ie) {
            throw UncheckedException.throwAsUncheckedException(ie)
        }
    }

    @Override
    void executed(JmeterResult result) {
        try {
            resultQueue.put(resultQueue)
        } catch (InterruptedException ie) {
            throw UncheckedException.throwAsUncheckedException(ie)
        }
    }
}
