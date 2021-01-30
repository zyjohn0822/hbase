import com.hisense.hiatmp.hbase.api.internal.HBaseProcess;
import com.hisense.hiatmp.hbase.api.RowKeyStrategy;
import com.hisense.hiatmp.hbase.annotation.RowKey;
import com.hisense.hiatmp.hbase.basic.HBase;
import com.hisense.hiatmp.hbase.basic.Tuple2;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.MD5Hash;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhangyong
 * @date 2020/11/18  14:42
 */
public class HBaseTest {
    @Test
    public void annocationTest() throws NoSuchFieldException, IllegalAccessException {
        Person person = new Person();
        person.setName("zhangyong46");
        person.setFatherName("zsj");
        person.setMotherName("tgf");
        person.setId("0014904C");
        person.setAddress("aaaa");
        person.setAge(12);
        final Class<? extends Person> aClass = person.getClass();
//        final Field address = aClass.getField("address");
//        address.setAccessible(true);
        Field[] declaredFields = aClass.getDeclaredFields();
        Field address = aClass.getDeclaredField("address");
        Field age = aClass.getDeclaredField("name");
        address.setAccessible(true);
        age.setAccessible(true);
        Object o = address.get(person);
        Object o1 = age.get(person);
        System.out.println(o.getClass().getName() + "---" + o.toString());
        System.out.println(o1.getClass().getName() + "---" + o1.toString());
    }
    @Test
    public void interfaceTests() throws IllegalAccessException, InstantiationException {
        Person person = new Person();
        person.setAddress("aaaa");
        person.setAge(12);
        final Class<? extends Person> aClass = person.getClass();
        final RowKey rowKey = aClass.getAnnotation(RowKey.class);
        final Class<? extends RowKeyStrategy> strategy = rowKey.strategy();
        final RowKeyStrategy rowKeyStrategy = strategy.newInstance();
        final byte[] generate = rowKeyStrategy.generate("0014904C");
        System.out.println(Arrays.toString(generate));
    }
    @Test
    public void HBaseProcessTest() throws Exception {
        Person person = new Person();
        person.setName("zhangyong46");
        person.setFatherName("zsj");
        person.setMotherName("tgf");
        person.setId("0014904C");
        person.setAddress("aaaa");
        person.setAge(12);
        HBaseProcess hBaseProcess = new HBaseProcess();
        Tuple2<String, List<Put>> processes = hBaseProcess.processes(person);
        System.out.println(processes);
    }
    @Test
    public void commonTest(){
        //d9875fe8e285c566e110a54d4da5f543
        //d9875fe8e285c566e110a54d4da5f543
        MD5Hash.getMD5AsHex(Bytes.toBytes("0014904C")).substring(0, 4);
        final byte[] bytes = Bytes.toBytes(Long.MAX_VALUE - System.currentTimeMillis());
        //System.out.println(Arrays.toString(bytes1));
        //System.out.println(Arrays.toString(Bytes.toBytes(MD5Hash.getMD5AsHex(Bytes.toBytes("0014904C")).substring(0, 8))));

        //System.out.println(Arrays.toString(bytes));
        //d9875fe8
        //System.out.println(MD5Hash.getMD5AsHex(Bytes.toBytes("0014845A")).substring(0, 8));
        AtomicLong count = new AtomicLong(1);
        System.out.println(count.get());
        System.out.println(count.get());
    }
    @Test
    public void createTableTest() throws Exception {
        String[] family = new String[]{"info"};
        int ex = HBase.createTable("hisense-test", Arrays.asList("info", "job"));
        Assert.assertEquals(ex, 2);
    }

    @Test
    public void insertColumnRecordsTest() throws IOException {
        String tableName = "hisense-test";
        Table table = HBase.getTable(tableName);
        List<Put> puts = new ArrayList<>();
        final long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            //com.hisense.hiatmp.hbase.basic.HBase.insertOneColumnRecords(table, "000002", "job", "name", "" + i);
            puts.add(new Put(Bytes.toBytes("000002")).addColumn(Bytes.toBytes("job"),Bytes.toBytes("name"),Bytes.toBytes("" + i)));
            System.out.println("生成");
        }
        assert table != null;
        table.put(puts);
        final long end = System.currentTimeMillis();

        System.out.println(end - start);
        //192214

    }
}
