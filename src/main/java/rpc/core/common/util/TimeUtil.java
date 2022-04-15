package rpc.core.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeUtil {
    private static final Logger logger = LoggerFactory.getLogger(TimeUtil.class);
    private volatile static Long startCurrentTimeMillis;

    public static void timerStart() {
        startCurrentTimeMillis = System.currentTimeMillis();
    }

    public static void timerStop() {
        Long endCurrentTimeMillis = System.currentTimeMillis();
        long res = endCurrentTimeMillis - startCurrentTimeMillis;
        if(res > 0) {
            logger.info("RPC server started by "+ res + " ms");
        }
    }
}
