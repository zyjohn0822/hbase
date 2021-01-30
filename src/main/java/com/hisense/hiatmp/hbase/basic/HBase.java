package com.hisense.hiatmp.hbase.basic;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.procedure2.util.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;


/**
 * @author zhangyong
 * @date 2020/11/18  13:54
 */
public class HBase {
    public final static Logger logger;
    static Connection connection;
    static Admin admin;
    static Configuration conf;

    public final static byte[] POSTFIX = new byte[]{0x00};

    static {
        logger = LoggerFactory.getLogger(HBase.class);
        // 取得一个数据库连接的配置参数对象
        conf = HBaseConfiguration.create();
        // 设置连接参数：HBase数据库所在的主机IP
        conf.set("hbase.zookeeper.quorum", "MN001,MN002,MN003");
        //conf.set("hbase.zookeeper.quorum", "hadoop-001");
        // 设置连接参数：HBase数据库使用的端口
        conf.set("hbase.zookeeper.property.clientPort", "24002");
        //conf.set("hbase.master", "hadoop-001:21300");
        // 取得一个数据库连接对象
        try {
            connection = ConnectionFactory.createConnection(conf);
            // 取得一个数据库元数据操作对象
            admin = connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通用方法 -> 获取table 用于添加和删除
     *
     * @param tableName 表名
     * @return Table
     */
    public static Table getTable(String tableName) {
        try {
            return connection.getTable(TableName.valueOf(tableName));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通用方法-> 创建表
     *
     * @param tableName    表名
     * @param columnFamily 列族集合
     * @return
     * @throws Exception
     */
    public static int createTable(String tableName, List<String> columnFamily) throws Exception {
//        if (admin.tableExists(TableName.valueOf(tableName))) {
//            logger.debug(MessageFormat.format("创建HBase表名：{0} 在HBase数据库中已经存在", tableName));
//            return 2;
//        } else {
        List<ColumnFamilyDescriptor> familyDescriptors = new ArrayList<>(columnFamily.size());
        for (String column : columnFamily) {
            familyDescriptors.add(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(column)).build());
        }
        TableDescriptor tableDescriptor = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName))
                .setColumnFamilies(familyDescriptors).build();
        admin.createTable(tableDescriptor);
        logger.info(MessageFormat.format("创建表成功：表名：{0}，列簇：{1}", tableName, columnFamily));
        return 1;
        //}
    }

    /**
     * 通用方法 -> 处理分页数据
     *
     * @param scan       过滤的数据
     * @param pageSize   每页显示的数量
     * @param lastRowKey 当前页的最后一行（rowKey）
     * @return
     */
    public static FilterList handlePageFilterData(Scan scan, int pageSize, String lastRowKey) {
        Filter pageFilter = new PageFilter(pageSize);
        FilterList pageFilterList = new FilterList();
        pageFilterList.addFilter(pageFilter);
        if (!StringUtils.isEmpty(lastRowKey)) {
            byte[] startRow = Bytes.add(Bytes.toBytes(lastRowKey), POSTFIX);
            scan.setStartRow(startRow);
        }
        return pageFilterList;
    }

    /**
     * 通用方法 -> 处理查询条件
     *
     * @param columnFamily 列簇
     * @param queryParam   过滤列集合   ("topicFileId,6282")=>("列,值")
     * @param regex        分隔字符
     * @param bool         查询方式：and 或 or | true : and ；false：or
     * @return
     */
    public static FilterList queryFilterData(String columnFamily, List<String> queryParam, String regex, boolean bool) {
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        if (!bool) {
            filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);
        }
        for (String param : queryParam) {
            String[] queryArray = param.split(regex);
            SingleColumnValueFilter singleColumnValueFilter = new SingleColumnValueFilter(Bytes.toBytes(columnFamily),
                    Bytes.toBytes(queryArray[0]),
                    CompareOperator.EQUAL,
                    Bytes.toBytes(queryArray[1]));
            singleColumnValueFilter.setFilterIfMissing(true);
            filterList.addFilter(singleColumnValueFilter);
        }
        return filterList;
    }

    /**
     * 通用方法 -> 根据条件查询
     *
     * @param table 表名
     * @param scan  过滤条件
     * @return
     */
    public static List<Map<String, Object>> queryData(Table table, Scan scan) {
        ResultScanner resultScanner = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            resultScanner = table.getScanner(scan);
            for (Result result : resultScanner) {
                logger.info(MessageFormat.format("查询每条HBase数据的行号：{0}", Bytes.toString(result.getRow())));
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("rowKey", Bytes.toString(result.getRow()));
                for (Cell cell : result.listCells()) {
                    resultMap.put(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()),
                            Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength())
                    );
                }
                resultList.add(resultMap);
            }
            logger.info(MessageFormat.format("查询指定表中数据信息：表名：{0}，查询结果：{1}", Bytes.toString(table.getName().getName()), resultList));
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(MessageFormat.format("查询指定表中数据信息：表名：{0}，错误信息：{1}", Bytes.toString(table.getName().getName()), e.getMessage()));
        } finally {
            close(null, resultScanner, table);
        }
        return resultList;
    }

    /**
     * 通用方法 -> 统计
     *
     * @param table
     * @param scan
     * @return
     */
    public static int queryDataCount(Table table, Scan scan) {
        scan.setFilter(new FirstKeyOnlyFilter());
        ResultScanner resultScanner = null;
        int rowCount = 0;
        try {
            resultScanner = table.getScanner(scan);
            for (Result result : resultScanner) {
                rowCount += result.size();
            }
            logger.info(MessageFormat.format("统计全表数据总数：表名：{0}，查询结果：{1}", Bytes.toString(table.getName().getName()), rowCount));
            return rowCount;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(MessageFormat.format("查询指定表中数据信息：表名：{0}，错误信息：{1}", Bytes.toString(table.getName().getName()), e.getMessage()));
            return rowCount;
        } finally {
            close(null, resultScanner, table);
        }
    }

    /**
     * 同时创建多张数据表
     *
     * @param tableMap 数据表 map<表名,列簇集合>
     * @return
     */
    public boolean createManyTable(Map<String, List<String>> tableMap) {
        try {
            if (admin != null) {
                for (Map.Entry<String, List<String>> confEntry : tableMap.entrySet()) {
                    createTable(confEntry.getKey(), confEntry.getValue());
                }
            }
        } catch (Exception e) {
            logger.error(MessageFormat.format("创建多个表出现未知错误：{0}", e.getMessage()));
            e.printStackTrace();
            return false;
        } finally {
            close(admin, null, null);
        }
        return true;
    }

    /**
     * 创建hbase表和列簇
     *
     * @param tableName    表名
     * @param columnFamily 列簇
     * @return 1：创建成功；0:创建出错；2：创建的表存在
     */
    public int createOneTable(String tableName, String... columnFamily) {
        try {
            //创建表，先查看表是否存在，然后在删除重新创建
            if (admin != null) {
                return createTable(tableName, Arrays.asList(columnFamily));
            } else {
                logger.error("admin变量没有初始化成功");
                return 0;
            }
        } catch (Exception e) {
            logger.debug(MessageFormat.format("创建表失败：{0},错误信息是：{1}", tableName, e.getMessage()));
            e.printStackTrace();
            return 0;
        } finally {
            close(admin, null, null);
        }
    }

    /**
     * 插入or 更新记录（单行单列族-多列多值)
     *
     * @param tableName    表名
     * @param row          行号  唯一
     * @param columnFamily 列簇名称
     * @param columns      多个列
     * @param values       对应多个列的值
     */
    public boolean insertManyColumnRecords(String tableName, String row, String columnFamily, List<String> columns, List<String> values) {
        try {
            Table table = getTable(tableName);
            Put put = new Put(Bytes.toBytes(row));
            for (int i = 0; i < columns.size(); i++) {
                put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columns.get(i)), Bytes.toBytes(values.get(i)));
                assert table != null;
                table.put(put);
            }
            logger.info(MessageFormat.format("添加单行单列族-多列多值数据成功：表名：{0},列名：{1},列值：{2}", tableName, columns, values));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(MessageFormat.format("添加单行单列族-多列多值数据失败：表名：{0}；错误信息是：{1}", tableName, e.getMessage()));
            return false;
        }
    }

    /**
     * 插入or更新记录（单行单列族-单列单值)
     *
     * @param tableName    表名
     * @param row          行号  唯一
     * @param columnFamily 列簇名称
     * @param column       列名
     * @param value        列的值
     */
    public static boolean insertOneColumnRecords(String tableName, String row, String columnFamily, String column, String value) {
        try {
            Table table = getTable(tableName);
            Put put = new Put(Bytes.toBytes(row));
            put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));
            table.put(put);
            logger.info(MessageFormat.format("添加单行单列族-单列单值数据成功：表名：{0}，列名：{1}，列值：{2}", tableName, column, value));
            return true;
        } catch (Exception e) {
            logger.debug(MessageFormat.format("添加单行单列族-单列单值数据失败：表名：{0}，错误信息是：{1}", tableName, e.getMessage()));
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 插入or更新记录（单行单列族-单列单值)
     *
     * @param table        表实体
     * @param row          行号  唯一
     * @param columnFamily 列簇名称
     * @param column       列名
     * @param value        列的值
     */
    public static boolean insertOneColumnRecords(Table table, String row, String columnFamily, String column, String value) {
        try {
            Put put = new Put(Bytes.toBytes(row));
            put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));
            table.put(put);
            logger.info(MessageFormat.format("添加单行单列族-单列单值数据成功：表名：{0}，列名：{1}，列值：{2}", table.getName(), column, value));
            return true;
        } catch (Exception e) {
            logger.debug(MessageFormat.format("添加单行单列族-单列单值数据失败：表名：{0}，错误信息是：{1}", table.getName(), e.getMessage()));
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据行号删除表中一条记录
     *
     * @param tableName 表名
     * @param rowNumber 行号
     */
    public boolean deleteDataByRowNumber(String tableName, String rowNumber) {
        try {
            Table table = getTable(tableName);
            Delete delete = new Delete(Bytes.toBytes(rowNumber));
            table.delete(delete);
            logger.info(MessageFormat.format("根据行号删除表中记录成功：表名：{0}，行号：{1}", tableName, rowNumber));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(MessageFormat.format("根据行号删除表中记录失败：表名：{0}，行号：{1}", tableName, rowNumber));
            return false;
        }
    }

    /**
     * 删除列簇下所有数据
     *
     * @param tableName    表名
     * @param columnFamily 列簇
     */
    public boolean deleteDataByColumnFamily(String tableName, String columnFamily) {
        try {
            if (!admin.tableExists(TableName.valueOf(tableName))) {
                logger.debug(MessageFormat.format("根据行号和列簇名称删除这行列簇相关的数据失败：表名不存在：{0}", tableName));
                return false;
            }
            admin.deleteColumnFamily(TableName.valueOf(tableName), Bytes.toBytes(columnFamily));
            logger.info(MessageFormat.format("删除该表中列簇下所有数据成功：表名：{0},列簇：{1}", tableName, columnFamily));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(MessageFormat.format("删除该表中列簇下所有数据失败：表名：{0},列簇：{1}，错误信息：{2}", tableName, columnFamily, e.getMessage()));
            return false;
        }
    }

    /**
     * 删除指定的列 ->删除最新列,保留旧列。
     * 如 相同的rowkey的name列数据 提交两次数据，此方法只会删除最近的数据，保留旧数据
     *
     * @param tableName    表名
     * @param rowNumber    行号
     * @param columnFamily 列簇
     * @param cloumn       列
     */
    public boolean deleteDataByColumn(String tableName, String rowNumber, String columnFamily, String cloumn) {
        try {
            if (!admin.tableExists(TableName.valueOf(tableName))) {
                logger.debug(MessageFormat.format("根据行号表名列簇删除指定列 ->删除最新列,保留旧列失败：表名不存在：{0}", tableName));
                return false;
            }
            Table table = getTable(tableName);
            Delete delete = new Delete(rowNumber.getBytes());
            delete.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(cloumn));
            table.delete(delete);
            logger.info(MessageFormat.format("根据行号表名列簇删除指定列 ->删除最新列,保留旧列成功：表名：{0},行号：{1}，列簇：{2}，列：{3}", tableName, rowNumber, columnFamily, cloumn));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(MessageFormat.format("根据行号表名列簇删除指定列 ->删除最新列,保留旧列失败：表名：{0},行号：{1}，列簇：{2}，列：{3}，错误信息：{4}", tableName, rowNumber, columnFamily, cloumn, e.getMessage()));
            return false;
        }
    }

    /**
     * 删除指定的列 ->新旧列都会删除
     *
     * @param tableName    表名
     * @param rowNumber    行号
     * @param columnFamily 列簇
     * @param cloumn       列
     */
    public boolean deleteDataByAllcolumn(String tableName, String rowNumber, String columnFamily, String cloumn) {
        try {
            if (!admin.tableExists(TableName.valueOf(tableName))) {
                logger.debug(MessageFormat.format("根据行号表名列簇删除指定列 ->新旧列都会删除失败：表名不存在：{0}", tableName));
                return false;
            }
            Table table = getTable(tableName);

            Delete delete = new Delete(rowNumber.getBytes());
            delete.addColumns(Bytes.toBytes(columnFamily), Bytes.toBytes(cloumn));
            table.delete(delete);
            logger.info(MessageFormat.format("根据行号表名列簇删除指定列 ->新旧列都会删除成功：表名：{0},行号：{1}，列簇：{2}，列：{3}", tableName, rowNumber, columnFamily, cloumn));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(MessageFormat.format("根据行号表名列簇删除指定列 ->新旧列都会删除失败：表名：{0},行号：{1}，列簇：{2}，列：{3}，错误信息：{4}", tableName, rowNumber, columnFamily, cloumn, e.getMessage()));
            return false;
        }
    }

    /**
     * 删除表
     *
     * @param tableName 表名
     */
    public boolean deleteTable(String tableName) {
        try {
            TableName table = TableName.valueOf(tableName);
            if (admin.tableExists(table)) {
                //禁止使用表,然后删除表
                admin.disableTable(table);
                admin.deleteTable(table);
            }
            logger.info(MessageFormat.format("删除表成功:{0}", tableName));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(MessageFormat.format("删除表失败：{0}，错误信息是：{1}", tableName, e.getMessage()));
            return false;
        } finally {
            close(admin, null, null);
        }
    }

    /**
     * 查询所有表名
     */
    public List<String> getAllTableNames() {
        List<String> resultList = new ArrayList<>();
        try {
            TableName[] tableNames = admin.listTableNames();
            for (TableName tableName : tableNames) {
                resultList.add(tableName.getNameAsString());
            }
            logger.info(MessageFormat.format("查询库中所有表的表名成功:{0}", resultList));
        } catch (IOException e) {
            logger.error("获取所有表的表名失败", e);
        } finally {
            close(admin, null, null);
        }
        return resultList;
    }

    /**
     * 根据表名和行号查询数据
     *
     * @param tableName 表名
     * @param rowNumber 行号
     * @return
     */
    public Map<String, Object> selectOneRowDataMap(String tableName, String rowNumber) {
        Map<String, Object> resultMap = new HashMap<>();
        Get get = new Get(Bytes.toBytes(rowNumber));
        Table table = getTable(tableName);
        try {
            Result result = table.get(get);
            if (result != null && !result.isEmpty()) {
                for (Cell cell : result.listCells()) {
                    resultMap.put(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()),
                            Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength())
                    );
                }
            }
            logger.info(MessageFormat.format("根据表名和行号查询数据：表名：{0}，行号：{1}，查询结果：{2}", tableName, rowNumber, resultMap));
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(MessageFormat.format("根据表名和行号查询数据失败：表名：{0}，行号：{1}，错误信息：{2}", tableName, rowNumber, e.getMessage()));
        } finally {
            close(null, null, table);
        }
        return resultMap;
    }

    /**
     * 根据不同条件查询数据
     *
     * @param tableName    表名
     * @param columnFamily 列簇
     * @param queryParam   过滤列集合   ("topicFileId,6282")=>("列,值")
     * @param regex        分隔字符
     * @param bool         查询方式：and 或 or | true : and ；false：or
     * @return
     */
    public List<Map<String, Object>> selectTableDataByFilter(String tableName, String columnFamily, List<String> queryParam, String regex, boolean bool) {
        Scan scan = new Scan();
        Table table = getTable(tableName);
        FilterList filterList = queryFilterData(columnFamily, queryParam, regex, bool);
        scan.setFilter(filterList);
        return queryData(table, scan);
    }

    /**
     * 查根据不同条件查询数据,并返回想要的单列 =>返回的列必须是过滤中存在
     *
     * @param tableName    表名
     * @param columnFamily 列簇
     * @param queryParam   过滤列集合   ("topicFileId,6282")=>("列,值")
     * @param regex        分隔字符
     * @param column       返回的列
     * @param bool         查询方式：and 或 or | true : and ；false：or
     * @return
     */
    public List<Map<String, Object>> selectColumnValueDataByFilter(String tableName, String columnFamily, List<String> queryParam, String regex, String column, boolean bool) {
        Scan scan = new Scan();
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        if (!bool) {
            filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);
        }
        Table table = getTable(tableName);
        for (String param : queryParam) {
            String[] queryArray = param.split(regex);
            SingleColumnValueExcludeFilter singleColumnValueExcludeFilter = new SingleColumnValueExcludeFilter(Bytes.toBytes(columnFamily), Bytes.toBytes(queryArray[0]), CompareOperator.EQUAL, Bytes.toBytes(queryArray[1]));
            filterList.addFilter(singleColumnValueExcludeFilter);
            scan.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(queryArray[0]));
        }
        scan.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
        scan.setFilter(filterList);
        return queryData(table, scan);

    }

    /**
     * 查询表中所有数据信息
     *
     * @param tableName 表名
     * @return
     */
    public List<Map<String, Object>> selectTableAllDataMap(String tableName) {
        Table table = getTable(tableName);
        Scan scan = new Scan();
        return queryData(table, scan);
    }

    /**
     * 根据表名和列簇查询所有数据
     *
     * @param tableName    表名
     * @param columnFamily 列簇
     * @return
     */
    public List<Map<String, Object>> selectTableAllDataMap(String tableName, String columnFamily) {
        Table table = getTable(tableName);
        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes(columnFamily));
        return queryData(table, scan);
    }

    /**
     * 根据相同行号和表名及不同的列簇查询数据
     *
     * @param tableName    表名
     * @param rowNumber    行号
     * @param columnFamily 列簇
     * @return
     */
    public Map<String, Object> selectTableByRowNumberAndColumnFamily(String tableName, String rowNumber, String columnFamily) {
        ResultScanner resultScanner = null;
        Map<String, Object> resultMap = new HashMap<>();
        Table table = getTable(tableName);
        try {
            Get get = new Get(Bytes.toBytes(rowNumber));
            get.addFamily(Bytes.toBytes(columnFamily));
            Result result = table.get(get);
            for (Cell cell : result.listCells()) {
                resultMap.put(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()),
                        Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength())
                );
            }
            logger.info(MessageFormat.format("根据行号和列簇查询表中数据信息：表名：{0}，行号：{1}，列簇：{2},查询结果：{3}", tableName, rowNumber, columnFamily, resultMap));
        } catch (IOException e) {
            e.printStackTrace();
            logger.info(MessageFormat.format("根据行号和列簇查询表中数据信息：表名：{0}，行号：{1}，列簇：{2},错误信息：{3}", tableName, rowNumber, columnFamily, e.getMessage()));
        } finally {
            close(null, resultScanner, table);
        }
        return resultMap;
    }

    /**
     * 查询某行中单列数据
     *
     * @param tableName    表名
     * @param rowNumber    行号
     * @param columnFamily 列簇
     * @param column       列
     * @return
     */
    public String selectColumnValue(String tableName, String rowNumber, String columnFamily, String column) {
        Table table = getTable(tableName);
        try {
            Get get = new Get(Bytes.toBytes(rowNumber));
            get.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
            Result result = table.get(get);
            logger.info(MessageFormat.format("根据表名、行号、列簇、列查询指定列的值：表名：{0}，行号：{1}，列簇：{2}，列名：{3}，查询结果：{4}", tableName, rowNumber, columnFamily, column, Bytes.toString(result.value())));
            return Bytes.toString(result.value());
        } catch (IOException e) {
            e.printStackTrace();
            logger.info(MessageFormat.format("根据表名、行号、列簇、列查询指定列的值：表名：{0}，行号：{1}，列簇：{2}，列名：{3}，错误信息：{4}", tableName, rowNumber, columnFamily, column, e.getMessage()));
            return "";
        } finally {
            close(null, null, table);
        }
    }

    /**
     * 分页的根据不同条件查询数据
     *
     * @param tableName    表名
     * @param columnFamily 列簇
     * @param queryParam   过滤列集合   ("topicFileId,6282")=>("列,值")
     * @param regex        分隔字符
     * @param bool         查询方式：and 或 or | true : and ；false：or
     * @param pageSize     每页显示的数量
     * @param lastRow      当前页的最后一行
     * @return
     */
    public List<Map<String, Object>> selectTableDataByFilterPage(String tableName, String columnFamily, List<String> queryParam, String regex, boolean bool, int pageSize, String lastRow) {
        Scan scan = new Scan();
        Table table = getTable(tableName);
        FilterList filterList = queryFilterData(columnFamily, queryParam, regex, bool);
        FilterList pageFilterList = handlePageFilterData(scan, pageSize, lastRow);
        pageFilterList.addFilter(filterList);
        scan.setFilter(pageFilterList);
        return queryData(table, scan);
    }

    /**
     * 分页查询表中所有数据信息
     *
     * @param tableName 表名
     * @param pageSize  每页数量
     * @param lastRow   当前页的最后一行
     * @return
     */
    public List<Map<String, Object>> selectTableAllDataMapPage(String tableName, int pageSize, String lastRow) {
        Scan scan = new Scan();
        Table table = getTable(tableName);
        FilterList pageFilterList = handlePageFilterData(scan, pageSize, lastRow);
        scan.setFilter(pageFilterList);
        return queryData(table, scan);
    }

    /**
     * 通用方法 ->关闭流
     */
    public static void close(Admin admin, ResultScanner rs, Table table) {
        if (admin != null) {
            try {
                admin.close();
            } catch (IOException e) {
                logger.error("关闭Admin失败", e);
            }
        }

        if (rs != null) {
            rs.close();
        }

        if (table != null) {
            try {
                table.close();
            } catch (IOException e) {
                logger.error("关闭Table失败", e);
            }
        }
    }

}
