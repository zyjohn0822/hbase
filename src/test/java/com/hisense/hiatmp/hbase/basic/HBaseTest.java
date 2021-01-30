package com.hisense.hiatmp.hbase.basic;

import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.MD5Hash;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HBase Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>01/05/2021</pre>
 */
public class HBaseTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getTable(String tableName)
     */
    @Test
    public void testGetTable() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: createTable(String tableName, List<String> columnFamily)
     */
    @Test
    public void testCreateTable() throws Exception {
        //创建表
        List<String> familyList = new ArrayList<>();
        familyList.add("info");
        familyList.add("link");
        HBase.createTable("person", familyList);
    }

    /**
     * Method: handlePageFilterData(Scan scan, int pageSize, String lastRowKey)
     */
    @Test
    public void testHandlePageFilterData() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: queryFilterData(String columnFamily, List<String> queryParam, String regex, boolean bool)
     */
    @Test
    public void testQueryFilterData() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: queryData(Table table, Scan scan)
     */
    @Test
    public void testQueryData() throws Exception {
        //TODO: Test goes here...
        Table table = HBase.getTable("person");
        String personId = "0014904C";
        String start = MD5Hash.getMD5AsHex(personId.getBytes()).substring(0, 4);
        String stop = MD5Hash.getMD5AsHex(personId.getBytes()).substring(0, 4) + "1";
        Scan scan = new Scan();
        scan.withStartRow(start.getBytes());
        scan.withStopRow(stop.getBytes());
        final List<Map<String, Object>> maps = HBase.queryData(table, scan);
        System.out.println(maps);
    }

    /**
     * Method: queryDataCount(Table table, Scan scan)
     */
    @Test
    public void testQueryDataCount() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: createManyTable(Map<String, List<String>> tableMap)
     */
    @Test
    public void testCreateManyTable() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: createOneTable(String tableName, String... columnFamily)
     */
    @Test
    public void testCreateOneTable() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: insertManyColumnRecords(String tableName, String row, String columnFamily, List<String> columns, List<String> values)
     */
    @Test
    public void testInsertManyColumnRecords() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: insertOneColumnRecords(String tableName, String row, String columnFamily, String column, String value)
     */
    @Test
    public void testInsertOneColumnRecordsForTableNameRowColumnFamilyColumnValue() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: insertOneColumnRecords(Table table, String row, String columnFamily, String column, String value)
     */
    @Test
    public void testInsertOneColumnRecordsForTableRowColumnFamilyColumnValue() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: deleteDataByRowNumber(String tableName, String rowNumber)
     */
    @Test
    public void testDeleteDataByRowNumber() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: deleteDataByColumnFamily(String tableName, String columnFamily)
     */
    @Test
    public void testDeleteDataByColumnFamily() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: deleteDataByColumn(String tableName, String rowNumber, String columnFamily, String cloumn)
     */
    @Test
    public void testDeleteDataByColumn() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: deleteDataByAllcolumn(String tableName, String rowNumber, String columnFamily, String cloumn)
     */
    @Test
    public void testDeleteDataByAllcolumn() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: deleteTable(String tableName)
     */
    @Test
    public void testDeleteTable() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getAllTableNames()
     */
    @Test
    public void testGetAllTableNames() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: selectOneRowDataMap(String tableName, String rowNumber)
     */
    @Test
    public void testSelectOneRowDataMap() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: selectTableDataByFilter(String tableName, String columnFamily, List<String> queryParam, String regex, boolean bool)
     */
    @Test
    public void testSelectTableDataByFilter() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: selectColumnValueDataByFilter(String tableName, String columnFamily, List<String> queryParam, String regex, String column, boolean bool)
     */
    @Test
    public void testSelectColumnValueDataByFilter() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: selectTableAllDataMap(String tableName)
     */
    @Test
    public void testSelectTableAllDataMapTableName() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: selectTableAllDataMap(String tableName, String columnFamily)
     */
    @Test
    public void testSelectTableAllDataMapForTableNameColumnFamily() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: selectTableByRowNumberAndColumnFamily(String tableName, String rowNumber, String columnFamily)
     */
    @Test
    public void testSelectTableByRowNumberAndColumnFamily() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: selectColumnValue(String tableName, String rowNumber, String columnFamily, String column)
     */
    @Test
    public void testSelectColumnValue() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: selectTableDataByFilterPage(String tableName, String columnFamily, List<String> queryParam, String regex, boolean bool, int pageSize, String lastRow)
     */
    @Test
    public void testSelectTableDataByFilterPage() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: selectTableAllDataMapPage(String tableName, int pageSize, String lastRow)
     */
    @Test
    public void testSelectTableAllDataMapPage() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: close(Admin admin, ResultScanner rs, Table table)
     */
    @Test
    public void testClose() throws Exception {
//TODO: Test goes here... 
    }


} 
