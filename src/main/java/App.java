import java.sql.Connection;
import java.sql.DriverManager;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {
    public String getGreeting() {
        return "Hello world.";
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new App().getGreeting());

        Connection conn = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1527:OraDoc",
                "system", "MyPasswd123");

    }
}
