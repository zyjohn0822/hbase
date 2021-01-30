package com.hisense.hiatmp.hbase.api;

/**
 * row key 策略接口
 * @author zhangyong
 * @date 2020/12/26  22:12
 */
public interface RowKeyStrategy {
    /**
     * 功能描述: <br>
     * <p>row key 生成方法</p>
     * 修改描述: <br>
     * <p>修改人:</p>
     * <p>修改原因:</p>
     * @param id 标识
     * @return: byte[]
     * @since: 1.0.0
     * @Author: zhangyong
     * @Date: 2020/12/27 17:25
     */
    byte[] generate(String id);
}
