package dtu.compute.util;

public class Configuration {
    public static final String testUserPassword = "password";
    public static final int validSessionTime = 10;
    public static final String dbUrl = "jdbc:postgresql://localhost:5432/postgres";
    public static final String dbUsername = "postgres";
    public static final String dbPassword = "postgres";
    public static final int port = 5099;
    public static final String url = "rmi://localhost:" + port;
    public static final String randomHash = "$2a$10$9B18PoXbdXqjVKzVcZ9VQOV2w.so/b3f1BTjNueWqC9QnTjPbPDJ2";
    public static final String accessControlModel = "accessControlList";
    // public static final String accessControlModel = "roleBasedAccessControl";
    public static final String[] testACUser = {"A", "B", "C", "D", "E", "F", "G"};
    public static final int[] testACList = {0b111111111, 0b000111111, 0b111001000, 0b110000000, 0b110000000, 0b110000000, 0b110000000};
    public static final String[] testUserRole = {"boss", "staff,tech", "root_user", "user", "user", "user", "user"};
}
