package com.hisense.hiatmp.hbase.api;

import com.hisense.hiatmp.hbase.basic.Tuple2;
import org.apache.hadoop.hbase.client.Put;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 注解的解析接口
 *
 * @author zhangyong
 * @date 2020/12/27  16:52
 */
public interface IHBaseProcess extends Serializable {
    /**
     * 功能描述: <br>
     * <p>实体类中多个列族</p>
     * 修改描述: <br>
     * <p>修改人:</p>
     * <p>修改原因:</p>
     *
     * @param obj
     * @return: java.util.List<org.apache.hadoop.hbase.client.Put>
     * @since: 1.0.0
     * @Author: zhangyong
     * @Date: 2020/12/31 9:38
     */
    <T> Tuple2<String, List<Put>> processes(T obj) throws Exception;
    /**
     * 功能描述: <br>
     * <p>批量转换</p>
     * 修改描述: <br>
     * <p>修改人:</p>
     * <p>修改原因:</p>
     * @param objs
     * @return: java.util.Map<java.lang.String,java.util.List<org.apache.hadoop.hbase.client.Put>>
     * @since: 1.0.0
     * @Author: zhangyong
     * @Date: 2021/1/5 15:22
     */
    <T> Tuple2<String, List<Put>> processesLists(List<T> objs) throws Exception;
}
