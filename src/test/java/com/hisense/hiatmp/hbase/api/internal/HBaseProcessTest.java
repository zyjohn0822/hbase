package com.hisense.hiatmp.hbase.api.internal;

import com.hisense.hiatmp.hbase.basic.HBase;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HBaseProcess Tester.
 *
 * @author <Authors zhangyong>
 * @version 1.0
 * @since <pre>01/05/2021</pre>
 */
public class HBaseProcessTest {
    HBaseProcess hBaseProcess;
    Table table;
    @Before
    public void before() throws Exception {
        hBaseProcess = new HBaseProcess();
        //获取表对象
        table = HBase.getTable("person");
    }

    @After
    public void after() throws Exception {

    }

    /**
     * Method: processes(T obj)
     */
    @Test
    public void testProcesses() throws Exception {
        //TODO: Test goes here...
        Person person = new Person();
        person.setName("zhangyong46");
        person.setFatherName("zsj");
        person.setMotherName("tgf");
        person.setId("0014904C");
        person.setAddress("aaaa");
        person.setAge(12);
        //final Map<String, List<Put>> processes = hBaseProcess.processes(person);
        //table.put(processes.get("person"));
    }

    /**
     * Method: processesLists(List<T> objs)
     */
    @Test
    public void testProcessesLists() throws Exception {
        //TODO: Test goes here...
    }


} 
