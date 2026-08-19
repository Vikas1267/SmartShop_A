import db.DatabaseInitializer;
import Exception.DataAccessException;
import ui.ConsoleApp;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Starting Smart Shop...");
            new DatabaseInitializer().initialize();
            new ConsoleApp().start();
        } catch (DataAccessException exception) {
            System.out.println(exception.getMessage());
            if (exception.getCause() != null && exception.getCause().getMessage() != null) {
                System.out.println("Details: " + exception.getCause().getMessage());
            }
        }
    }
}