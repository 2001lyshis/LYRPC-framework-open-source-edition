package rpc.core.common.util;

public class TimeUtil {
    private volatile static Long startCurrentTimeMillis;
    private volatile static Long endCurrentTimeMillis;

    public static void timerStart() {
        startCurrentTimeMillis = System.currentTimeMillis();
    }

    public static void timerStop() {
        endCurrentTimeMillis = System.currentTimeMillis();
        Long res = endCurrentTimeMillis - startCurrentTimeMillis;
        if(res > 0) {
            System.out.println(res + "ms");
        }
    }
}
