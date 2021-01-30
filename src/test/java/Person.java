import com.hisense.hiatmp.hbase.api.internal.SaltMD5IDStrategy;
import com.hisense.hiatmp.hbase.annotation.Column;
import com.hisense.hiatmp.hbase.annotation.RowKey;
import com.hisense.hiatmp.hbase.annotation.Table;

/**
 * @author zhangyong
 * @date 2020/12/31  13:03
 */
@Table("person")
@RowKey(value = "id", strategy = SaltMD5IDStrategy.class)
public class Person {
    String id;
    @Column(value = "name", family = "info")
    String name;
    @Column(value = "age", family = "info")
    int age;
    @Column(value = "address", family = "info")
    String address;
    @Column(value = "fatherName", family = "link")
    String fatherName;
    @Column(value = "motherName", family = "link")
    String motherName;

    public Person() {
    }

    public Person(String id, String name, int age, String address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public Person(String id, String name, int age, String address, String fatherName, String motherName) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
        this.fatherName = fatherName;
        this.motherName = motherName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }
}
