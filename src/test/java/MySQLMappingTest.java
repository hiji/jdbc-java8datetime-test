import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class MySQLMappingTest {

    private static Connection conn;
    private static PreparedStatement statement;
    private static ResultSet resultSet;

    @BeforeClass
    public static void setUpClass() throws Exception {
        conn = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:13306/mydb",
                "root", "mysql");
        statement = conn.prepareStatement("SELECT * FROM mydb.sample WHERE id='hoge'");
        resultSet = statement.executeQuery();
        resultSet.next();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        resultSet.close();
        statement.close();
        conn.close();
    }

    @Before
    public void setUp() throws Exception {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM mydb.sample WHERE id='hoge'");
        stmt.execute();
        stmt.close();
    }

    @Test
    public void 登録() throws Exception {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO mydb.sample(id, dt, dtm, tm, ts) VALUES (?, ?, ?, ?, ?)");

        stmt.setString(1, "hoge");

        stmt.setObject(2, LocalDate.of(2016, 4, 26));
        stmt.setObject(3, LocalDateTime.of(2016, 4, 26, 10, 11, 12, 123456));
        stmt.setObject(4, LocalTime.of(10, 11, 12));
        stmt.setObject(5, LocalDateTime.of(2016, 4, 26, 10, 11, 12, 123456));

        stmt.execute();
        stmt.close();
    }
}
