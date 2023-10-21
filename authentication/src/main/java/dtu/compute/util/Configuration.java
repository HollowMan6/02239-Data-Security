package dtu.compute.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	public static final String testUsername = "testuser";
	public static final String testUserPassword = "password";
	public static final int validSessionTime = 10;
	public static final String dbUrl = "jdbc:postgresql://localhost:5432/postgres";
	public static final String dbUsername = "postgres";
	public static final String dbPassword = "postgres";
	public static final int port = 5099;
	public static final String url = "rmi://localhost:" + port;
	public static final String randomHash = "$2a$10$9B18PoXbdXqjVKzVcZ9VQOV2w.so/b3f1BTjNueWqC9QnTjPbPDJ2";
}
