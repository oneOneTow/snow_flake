package com.vens.snowflake.service;

/**
 * @author LuZhiqing
 * @Description:
 * @date 2018/9/5
 */

import com.vens.snowflake.enums.CommonStaticSource;

/**
 * Twitter_Snowflake<br>
 * SnowFlake�Ľṹ����(ÿ������-�ֿ�):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * 1λ��ʶ������long����������Java���Ǵ����ŵģ����λ�Ƿ���λ��������0��������1������idһ�������������λ��0<br>
 * 41λʱ���(���뼶)��ע�⣬41λʱ��ز��Ǵ洢��ǰʱ���ʱ��أ����Ǵ洢ʱ��صĲ�ֵ����ǰʱ��� - ��ʼʱ���)
 * �õ���ֵ��������ĵĿ�ʼʱ��أ�һ�������ǵ�id��������ʼʹ�õ�ʱ�䣬�����ǳ�����ָ���ģ������������IdWorker���startTime���ԣ���41λ��ʱ��أ�����ʹ��69�꣬��T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10λ�����ݻ���λ�����Բ�����1024���ڵ㣬����5λdatacenterId��5λworkerId<br>
 * 12λ���У������ڵļ�����12λ�ļ���˳���֧��ÿ���ڵ�ÿ����(ͬһ������ͬһʱ���)����4096��ID���<br>
 * �������պ�64λ��Ϊһ��Long�͡�<br>
 * SnowFlake���ŵ��ǣ������ϰ���ʱ���������򣬲��������ֲ�ʽϵͳ�ڲ������ID��ײ(����������ID�ͻ���ID������)������Ч�ʽϸߣ������ԣ�SnowFlakeÿ���ܹ�����26��ID���ҡ�
 */
public class SnowflakeService {
    private final long workerId;
    private final long dataCenterId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeService(long workerId, long dataCenterId) {
        if (workerId > CommonStaticSource.maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", CommonStaticSource.maxWorkerId));
        }
        if (dataCenterId > CommonStaticSource.maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", CommonStaticSource.maxDataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }


    /**
     * �����һ��ID (�÷������̰߳�ȫ��)
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = generateTime();
        //�����ǰʱ��С����һ��ID���ɵ�ʱ�����˵��ϵͳʱ�ӻ��˹����ʱ��Ӧ���׳��쳣
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        //�����ͬһʱ�����ɵģ�����к���������
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & CommonStaticSource.maxSequence;
            //�������������
            if (sequence == 0) {
                //��������һ������,����µ�ʱ���
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //ʱ����ı䣬��������������
        else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        //��λ��ͨ��������ƴ��һ�����64λ��ID
        long spaceTime = timestamp - CommonStaticSource.beginTime;
        long id = (spaceTime << CommonStaticSource.timeMoveNum)
                | (dataCenterId << CommonStaticSource.dataCenterMoveNum)
                | (workerId << CommonStaticSource.workerMoveNum) | sequence;
        return id;
    }

    /**
     * ��������һ�����룬ֱ������µ�ʱ���
     *
     * @param lastTimestamp �ϴ�����ID��ʱ���
     * @return ��ǰʱ���
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = generateTime();
        while (timestamp <= lastTimestamp) {
            timestamp = generateTime();
        }
        return timestamp;
    }

    /**
     * �����Ժ���Ϊ��λ�ĵ�ǰʱ��
     *
     * @return ��ǰʱ��(����)
     */
    protected long generateTime() {
        return System.currentTimeMillis();
    }
}