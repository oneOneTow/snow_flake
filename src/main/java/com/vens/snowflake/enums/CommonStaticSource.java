package com.vens.snowflake.enums;

import java.util.Date;

/**
 * @author LuZhiqing
 * @Description:
 * @date 2018/9/5
 */
public class CommonStaticSource {
    public static final long beginTime=System.currentTimeMillis();
    public static final int timeNum=41;
    public static final int dataCenterNum=5;
    public static final int workerNum=5;
    public static final int sequenceNum=12;
    public static final long maxWorkerId=-1L^(-1L<<workerNum);
    public static final long maxDataCenterId=-1L^(-1L<<dataCenterNum);
    public static final long maxSequence=-1L^(-1L<<sequenceNum);
    public static final long timeMoveNum=22;
    public static final long dataCenterMoveNum=17;
    public static final long workerMoveNum=12;
}
