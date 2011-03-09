package org.bitcoma.heartserver.netty.task;

import java.util.concurrent.TimeUnit;

import org.bitcoma.hearts.model.transfered.OneMessageProtos.OneMessage;
import org.bitcoma.hearts.netty.handler.ByteCounterHandler;
import org.bitcoma.hearts.netty.handler.MessageCounterHandler;
import org.bitcoma.hearts.utils.PerformanceMetrics;
import org.bitcoma.hearts.utils.PerformanceMetrics.CpuUsageInfo;
import org.bitcoma.hearts.utils.PerformanceMetrics.RamUsageInfo;
import org.bitcoma.hearts.utils.ReqRespMetrics;
import org.bitcoma.heartserver.ServerState;
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

        if (logger.isInfoEnabled()) {

            // ****************************** Network Stats

            // Bytes read and written in kilobytes
            double bytesRead = (byteCounter != null) ? byteCounter.getReadBytesAndClear()
                    / (double) (BYTES_PER_KILO * DELAY) : 0;
            double bytesWritten = (byteCounter != null) ? byteCounter.getWrittenBytesAndClear()
                    / (double) (BYTES_PER_KILO * DELAY) : 0;

            // Message read and written in thousands
            double msgRead = (msgCounter != null) ? msgCounter.getReadMessagesAndClear()
                    / (double) (BYTES_PER_KILO * DELAY) : 0;
            double msgWritten = (msgCounter != null) ? msgCounter.getWrittenMessagesAndClear()
                    / (double) (BYTES_PER_KILO * DELAY) : 0;

            // ***************************** CPU / RAM Stats

            RamUsageInfo systemRam = PerformanceMetrics.instance().getSystemRamInfo();
            CpuUsageInfo cpuInfo = PerformanceMetrics.instance().getCpuUsageInfo();

            double cpuUsage = cpuInfo.totalPerc * 100;
            double ramUsage = (systemRam.ramUsed / (double) systemRam.ramMax) * 100;

            // Form the reqRespReport
            String reqRespReport = "\t(Key:AvgResp)";
            for (Integer key : ServerState.reqRespMetricsMap.keySet()) {
                ReqRespMetrics.Data rrMet = ServerState.reqRespMetricsMap.get(key).getAndResetInfo();

                double avgResp = (rrMet.totalNumReqResps == 0) ? 0 : rrMet.totalRespTime
                        / (double) rrMet.totalNumReqResps;
                reqRespReport += String.format("(" + OneMessage.Type.valueOf(key) + ":%.2f) ", avgResp);
            }
            reqRespReport += "\n";

            String networkReport = String.format("Performance Report\n"
                    + "\t(Read/Write): Messages(1000s): (%.2f/%.2f) KB: (%.2f/%.2f)\n", msgRead, msgWritten, bytesRead,
                    bytesWritten);
            String statusReport = String.format("\tNum Active Connections: %d Num Logger In Users: %d\n",
                    ServerState.numActiveConnections.longValue(), ServerState.numLoggedInUsers.longValue());
            String cpuMemReport = String.format("\tSystem CPU Usage (%%): %.2f System Ram Usage (%%): %.2f", cpuUsage,
                    ramUsage);
            logger.info(networkReport + reqRespReport + statusReport + cpuMemReport);
        }

        // Make this task recurring
        TimeOutTaskCreator.createTask(new PerformanceMetricsTask(byteCounter, msgCounter), DELAY, UNIT);
    }
}
