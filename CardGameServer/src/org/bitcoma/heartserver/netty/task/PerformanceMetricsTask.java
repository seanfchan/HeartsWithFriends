package org.bitcoma.heartserver.netty.task;

import java.util.concurrent.TimeUnit;

import org.bitcoma.hearts.netty.handler.ByteCounterHandler;
import org.bitcoma.hearts.netty.handler.MessageCounterHandler;
import org.bitcoma.hearts.utils.PerformanceMetrics;
import org.bitcoma.hearts.utils.PerformanceMetrics.CpuUsageInfo;
import org.bitcoma.hearts.utils.PerformanceMetrics.RamUsageInfo;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceMetricsTask implements TimerTask {

    // Values to use with TimeOutTaskCreator
    // Count performance metrics every second
    public static final long DELAY = 1;
    public static final TimeUnit UNIT = TimeUnit.SECONDS;

    private static final int BYTES_PER_KILO = 1024;
    private ByteCounterHandler byteCounter;
    private MessageCounterHandler msgCounter;
    private static Logger logger = LoggerFactory.getLogger(PerformanceMetricsTask.class);

    public PerformanceMetricsTask(ByteCounterHandler byteCounter, MessageCounterHandler msgCounter) {
        this.msgCounter = msgCounter;
        this.byteCounter = byteCounter;
    }

    @Override
    public void run(Timeout timeout) throws Exception {

        // ********************** NETWORK Performance Stats
        if (msgCounter != null || msgCounter != null) {
            if (logger.isInfoEnabled()) {

                // Bytes read and written in kilobytes
                double bytesRead = byteCounter.getReadBytesAndClear() / (double) BYTES_PER_KILO;
                double bytesWritten = byteCounter.getWrittenBytesAndClear() / (double) BYTES_PER_KILO;

                // Message read and written in thousands
                double msgRead = msgCounter.getReadMessagesAndClear() / (double) BYTES_PER_KILO;
                double msgWritten = msgCounter.getWrittenMessagesAndClear() / (double) BYTES_PER_KILO;

                String networkReport = String.format(
                        "Network Performance Report (Read/Write): Messages(1000s): (%.2f/%.2f) KB: (%.2f/%.2f)",
                        msgRead, msgWritten, bytesRead, bytesWritten);
                logger.info(networkReport);
            }
        }

        // *********************** CPU / Ram Performance Stats
        if (logger.isInfoEnabled()) {
            RamUsageInfo systemRam = PerformanceMetrics.instance().getSystemRamInfo();
            CpuUsageInfo cpuInfo = PerformanceMetrics.instance().getCpuUsageInfo();

            double cpuUsage = cpuInfo.totalPerc * 100;
            double ramUsage = (systemRam.ramUsed / (double) systemRam.ramMax) * 100;

            String cpuMemReport = String.format(
                    "CPU/Mem Performance Report: System CPU Usage (%%): %.2f System Ram Usage (%%): %.2f", cpuUsage,
                    ramUsage);
            logger.info(cpuMemReport);
        }

        // Make this task recurring
        TimeOutTaskCreator.createTask(new PerformanceMetricsTask(byteCounter, msgCounter), DELAY, UNIT);
    }
}
