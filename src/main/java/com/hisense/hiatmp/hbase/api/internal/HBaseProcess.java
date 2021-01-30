package com.hisense.hiatmp.hbase.api.internal;

import com.hisense.hiatmp.hbase.annotation.Column;
import com.hisense.hiatmp.hbase.annotation.RowKey;
import com.hisense.hiatmp.hbase.annotation.Table;
import com.hisense.hiatmp.hbase.api.IHBaseProcess;
import com.hisense.hiatmp.hbase.api.RowKeyStrategy;
import com.hisense.hiatmp.hbase.basic.Tuple2;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyong
 * @date 2020/12/27  16:47
 */
public class HBaseProcess implements IHBaseProcess {
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
    @Override
    public <T> Tuple2<String, List<Put>> processes(T obj) throws Exception {
        List<Put> puts = new ArrayList<>();
        final Class<?> aClass = obj.getClass();
        final Table table = aClass.getAnnotation(Table.class);
        final String tableName = table.value();
        final RowKey key = aClass.getAnnotation(RowKey.class);
        //获取需要组合的id字段
        String id = key.value();
        //根据字段名获取字段的值
        final Field idField = aClass.getDeclaredField(id);
        //设置字段值是可获取的
        idField.setAccessible(true);
        final String idValue = String.valueOf(idField.get(obj));
        //获取row key的策略
        final Class<? extends RowKeyStrategy> strategy = key.strategy();
        final RowKeyStrategy rowKeyStrategy = strategy.newInstance();
        final byte[] rowKey = rowKeyStrategy.generate(idValue);
        //域的注解
        final Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotations().length != 0) {
                //获取权限
                field.setAccessible(true);
                final Column column = field.getAnnotation(Column.class);
                final String family = column.family();
                String columnName = column.value();
                //去除空值,减少HBase冗余信息
                if (field.get(obj) != null) {
                    Put put = new Put(rowKey).addColumn(
                            Bytes.toBytes(family),
                            Bytes.toBytes(columnName),
                            Bytes.toBytes(String.valueOf(field.get(obj))));
                    puts.add(put);
                }
            }
        }
        return new Tuple2<>(tableName, puts);
    }

    @Override
    public <T> Tuple2<String, List<Put>> processesLists(List<T> objs) throws Exception {
        List<Put> temp = new ArrayList<>();
        String tableName = null;
        for (T obj : objs) {
            final Tuple2<String, List<Put>> processesMap = processes(obj);
            temp.addAll(processesMap.f1);
            tableName = processesMap.f0;
        }
        return new Tuple2<>(tableName, temp);
    }
}
