import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OracleDateMappingTest {

    private static Connection conn;
    private static PreparedStatement statement;
    private static ResultSet resultSet;

    @BeforeClass
    public static void setUpClass() throws Exception {
        conn = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1527:OraDoc",
                "system", "MyPasswd123");
        statement = conn.prepareStatement("SELECT * FROM JDBC_TEST WHERE id='fuga'");
        resultSet = statement.executeQuery();
        resultSet.next();

        DatabaseMetaData metaData = conn.getMetaData();
        System.out.println(metaData.getDriverVersion());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        resultSet.close();
        statement.close();
        conn.close();
    }

    @Before
    public void setUp() throws Exception {
//        PreparedStatement stmt = conn.prepareStatement("DELETE FROM JDBC_TEST WHERE id='hoge'");
//        stmt.execute();
//        stmt.close();
    }

    @Test
    public void 更新() throws Exception {
        PreparedStatement stmt = conn.prepareStatement("UPDATE JDBC_TEST SET ts=? WHERE id='hoge'");

        LocalDateTime of = LocalDateTime.of(2016, 5, 26, 10, 11, 12, 123456789);
//        java.sql.Timestamp ts = new Timestamp(of.getYear(), of.getMonthValue(), of.getDayOfMonth(), of.getHour(), of.getMinute(), of.getSecond(), of.getNano());
        stmt.setObject(1, of);

        stmt.execute();
        stmt.close();
    }

    @Test
    public void LocalDateで取得() throws Exception {
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM JDBC_TEST WHERE id='hoge'");
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        LocalDate result = resultSet.getObject("dt", LocalDate.class);
        assertThat(result, is(LocalDate.of(2016, 4, 26)));

        resultSet.close();
        statement.close();
    }

    @Test
    public void LocalDateTimeで取得() throws Exception {
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM JDBC_TEST WHERE id='hoge'");
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        LocalDateTime result = resultSet.getObject("ts", LocalDateTime.class);
        assertThat(result, is(LocalDateTime.of(2016, 4, 26, 10, 11, 12, 123456789)));

        resultSet.close();
        statement.close();
    }

    @Test
    public void OffsetDateTimeで取得() throws Exception {
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM JDBC_TEST WHERE id='hoge'");
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        OffsetDateTime result = resultSet.getObject("ts_tz", OffsetDateTime.class);
        assertThat(result, is(OffsetDateTime.of(2016, 4, 26, 10, 11, 12, 123456789, ZoneOffset.of("+08:00"))));

        resultSet.close();
        statement.close();
    }

    @Test
    public void LocalDateで更新() throws Exception {
        PreparedStatement stmt = conn.prepareStatement("UPDATE JDBC_TEST SET dt=? WHERE id='hoge'");

        stmt.setObject(1, LocalDate.of(2016, 4, 26));

        stmt.execute();
        stmt.close();
    }

    @Test
    public void LocalDateをVARCHARで更新() throws Exception {
        PreparedStatement stmt = conn.prepareStatement("UPDATE JDBC_TEST SET dt_char=? WHERE id='hoge'");

        stmt.setObject(1, LocalDate.of(2016, 4, 26), JDBCType.VARCHAR);

        stmt.execute();
        stmt.close();
    }

    @Test
    public void LocalDateTimeで更新() throws Exception {
        PreparedStatement stmt = conn.prepareStatement("UPDATE JDBC_TEST SET ts=? WHERE id='hoge'");

        stmt.setObject(1, LocalDateTime.of(2016, 4, 26, 10, 11, 12, 123456789));

        stmt.execute();
        stmt.close();
    }

    @Test
    public void OffsetDateTimeで更新() throws Exception {
        PreparedStatement stmt = conn.prepareStatement("UPDATE JDBC_TEST SET ts_tz=? WHERE id='hoge'");

        stmt.setObject(1, OffsetDateTime.of(2016, 4, 26, 10, 11, 12, 123456789, ZoneOffset.of("+08:00")));

        stmt.execute();
        stmt.close();
    }

//    @Test
//    public void 更新() throws Exception {
//        PreparedStatement stmt = conn.prepareStatement("UPDATE JDBC_TEST(dt) VALUES (?) WHERE id='hoge'");
//
//        stmt.setObject(1, LocalDateTime.of(2016, 4, 26, 10, 11, 12, 123456789));
//        stmt.setObject(3, LocalDateTime.of(2016, 4, 26, 10, 11, 12, 123456789));
//        stmt.setObject(4, OffsetDateTime.of(2016, 4, 26, 10, 11, 12, 123456789, ZoneOffset.of("+08:00")));
//
//        stmt.execute();
//        stmt.close();
//    }

    //    @Test
//    public void 登録() throws Exception {
//        PreparedStatement stmt = conn.prepareStatement("INSERT INTO JDBC_TEST(id, dt, ts, ts_tz) VALUES (?, ?, ?, ?, ?)");
//
//        LocalDate localDate = LocalDate.of(2016, 4, 26);
//        stmt.setString(1, "hoge");
//        stmt.setObject(2, LocalDate.of(2016, 4, 26));
//        stmt.setObject(3, LocalTime);
//
//    }

    @Test
    public void DATE型をDateとして取得する() throws Exception {
        java.sql.Date actual = resultSet.getDate("dt");

        // java.util.Date のサブクラスであるため java.util.Date として扱える
        assertThat(actual, instanceOf(java.util.Date.class));
        // DATE型ではミリ秒で保持していないため、0で保管されている
        assertThat(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(actual),
                is("2005/05/14 12:34:56.000"));
    }

    @Test
    public void DATE型をDateとしてgetObjectで取得する() throws Exception {
        java.util.Date actual = resultSet.getObject("dt", Date.class);

        // DATE型ではミリ秒で保持していないため、0で保管される
        assertThat(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(actual),
                is("2005/05/14 12:34:56.000"));
    }

    @Test
    public void TIMESTAMP型をDateとして取得する() throws Exception {
        Timestamp actual = resultSet.getTimestamp("ts");

        // java.util.Date のサブクラスであるため java.util.Date として扱える
        assertThat(actual, instanceOf(java.util.Date.class));
        assertThat(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(actual),
                is("2005/05/14 12:34:56.789"));
    }

    @Test
    public void TIMESTAMP型をDateとしてgetObjectで取得する() throws Exception {
        java.util.Date actual = resultSet.getObject("ts", Date.class);

        // TimestampではなくDateとして扱われるためか、ミリ秒が欠落する
        assertThat(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(actual),
                is("2005/05/14 12:34:56.000"));
    }

    @Test
    public void TIMESTAMP_TIMEZONE型をDateとして取得する() throws Exception {
        Timestamp actual = resultSet.getTimestamp("ts_tz");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getDefault());
        assertThat(formatter.format(actual),
                is("2005/05/14 12:34:56.789"));
    }

    @Test
    public void TIMESTAMP_TIMEZONE型をDateとして取得する2() throws Exception {
        // 経過時間を保持しているため、時差には影響無く取得できる
        Timestamp actual = resultSet.getTimestamp("ts_tz2");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getDefault());
        assertThat(formatter.format(actual),
                is("2005/05/14 13:34:56.789"));
    }

    @Test
    public void TIMESTAMP_LOCAL_TIMEZONE型をDateとして取得する() throws Exception {
        Timestamp actual = resultSet.getTimestamp("ts_ltz");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getDefault());
        assertThat(formatter.format(actual),
                is("2005/05/14 12:34:56.789"));
    }

    @Test
    public void DBのDATE型をLocalDateで取得する() throws Exception {
        System.out.println(resultSet.getString(1));
        System.out.println("--- DATE ---");
        System.out.println(resultSet.getObject(2, LocalDate.class));
        System.out.println(resultSet.getObject(2, LocalTime.class));
        System.out.println(resultSet.getObject(2, LocalDateTime.class));
        System.out.println(resultSet.getObject(2, OffsetDateTime.class));
        System.out.println("--- TIMESTAMP ---");
        System.out.println(resultSet.getObject(3, LocalDate.class));
        System.out.println(resultSet.getObject(3, LocalTime.class));
        System.out.println(resultSet.getObject(3, LocalDateTime.class));
        System.out.println(resultSet.getObject(3, OffsetDateTime.class));
        System.out.println("--- TIMESTAMP WITH TIME ZONE ---");
        System.out.println(resultSet.getObject(4, LocalDate.class));
        System.out.println(resultSet.getObject(4, LocalTime.class));
        System.out.println(resultSet.getObject(4, LocalDateTime.class));
        System.out.println(resultSet.getObject(4, OffsetDateTime.class));
        System.out.println("--- TIMESTAMP WITH LOCAL TIME ZONE ---");
        System.out.println(resultSet.getObject(5, LocalDate.class));
        System.out.println(resultSet.getObject(5, LocalTime.class));
        System.out.println(resultSet.getObject(5, LocalDateTime.class));
        System.out.println(resultSet.getObject(5, OffsetDateTime.class));
        System.out.println("");

        System.out.println(TimeZone.getDefault());
    }

}
