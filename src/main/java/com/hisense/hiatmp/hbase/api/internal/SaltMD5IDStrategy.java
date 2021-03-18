package com.hisense.hiatmp.hbase.api.internal;

import com.hisense.hiatmp.hbase.api.RowKeyStrategy;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.MD5Hash;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 支持实体类的一个标识组合方式
 * MD5(RSUID)[0,4]+(LONG.MAX-TIMESTAMP)+COUNT
 *
 * @author zhangyong
 * @date 2020/12/27  17:29
 */
public class SaltMD5IDStrategy implements RowKeyStrategy {
    //AtomicLong保证多线程修改的原子性
    private final AtomicLong count = new AtomicLong(1);

    public SaltMD5IDStrategy() {

    }

    /**
     * 功能描述: <br>
     * <p>row key 生成方法</p>
     * 修改描述: <br>
     * <p>修改人:</p>
     * <p>修改原因:</p>
     *
     * @return: java.lang.String
     * @since: 1.0.0
     * @Author: zhangyong
     * @Date: 2020/12/27 17:25
     */
    @Override
    public byte[] generate(String id) {
        byte[] result;
        final byte[] temp;
        long max = Long.MAX_VALUE;
        if (count.get() == max) {
            count.set(1);
        }
        try {
            temp = Bytes.add(MD5Hash.getMD5AsHex(id.getBytes()).substring(0, 4).getBytes(),
                    Bytes.toBytes(max - System.currentTimeMillis()));
            result = Bytes.add(temp, Bytes.toBytes(count.get()));
        } finally {
            count.incrementAndGet();
        }
        return result;
    }
}
