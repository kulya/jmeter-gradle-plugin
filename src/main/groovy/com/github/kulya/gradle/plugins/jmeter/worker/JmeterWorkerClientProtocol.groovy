package com.github.kulya.gradle.plugins.jmeter.worker


interface JmeterWorkerClientProtocol {
    void executed(JmeterResult result)
}
