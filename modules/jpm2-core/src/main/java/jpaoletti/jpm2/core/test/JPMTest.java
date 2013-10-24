package jpaoletti.jpm2.core.test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import org.hibernate.annotations.Type;

/**
 * Test model class
 *
 * @author jpaoletti
 * @since 21/09/2011
 * @version 1.0
 *
 */
@Entity
@Table(name = "test")
public class JPMTest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "string_field")
    private String string;
    @Column(name = "int_field")
    private Integer integer;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "date_field")
    private Date date;
    @Column(name = "bool_field")
    @Type(type = "yes_no")
    private Boolean bool;
    @Column(name = "decimal_field")
    private BigDecimal decimal;
    @ManyToOne()
    @JoinColumn(name = "test")
    private JPMTest test;
    private String bigstring;
    @Enumerated
    private TestEnum testEnum;
    @Lob
    @Column(columnDefinition = "mediumblob")
    private byte[] file;
    @OneToMany(mappedBy = "test")
    private List<JPMTestWeak> weaks;

    public JPMTest(Long id, String string, Integer integer, Date date, Boolean bool) {
        this.id = id;
        this.string = string;
        this.integer = integer;
        this.date = date;
        this.bool = bool;
        this.weaks = new ArrayList<>();
    }

    public JPMTest() {
    }

    public Boolean getBool() {
        return bool;
    }

    public void setBool(Boolean bool) {
        this.bool = bool;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public JPMTest getTest() {
        return test;
    }

    public void setTest(JPMTest test) {
        this.test = test;
    }

    @Override
    public String toString() {
        return "[" + getId() + "] " + getString();
    }

    public BigDecimal getDecimal() {
        return decimal;
    }

    public void setDecimal(BigDecimal decimal) {
        this.decimal = decimal;
    }

    public String getBigstring() {
        return bigstring;
    }

    public void setBigstring(String bigstring) {
        this.bigstring = bigstring;
    }

    public TestEnum getTestEnum() {
        return testEnum;
    }

    public void setTestEnum(TestEnum textEnum) {
        this.testEnum = textEnum;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public List<JPMTestWeak> getWeaks() {
        return weaks;
    }

    public void setWeaks(List<JPMTestWeak> weaks) {
        this.weaks = weaks;
    }
}
