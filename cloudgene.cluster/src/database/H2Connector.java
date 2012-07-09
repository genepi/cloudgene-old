package database;
/**
 * @author seppinho
 *
 */
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class H2Connector {

	private String path;
	
	private String user;
	
	private String password;
	
	private Connection connection;
	
	private static H2Connector instance = null;

	public static H2Connector getInstance() {
		if (instance == null)
			instance = new H2Connector("h2_db/Clustinator", "cloud", "cloud");
		return instance;
	}

	public H2Connector(String path, String user, String password) {

		this.path = path;
		this.user = user;
		this.password = password;
	}

	public void connect() throws SQLException {

		File file = new File(path + ".h2.db");
		boolean exists = file.exists();

		// log.debug("Establishing connection to " + user + "@" + path);

		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		connection = DriverManager.getConnection("jdbc:h2:" + path, user,
				password);
		// connection.setAutoCommit(false);

		if (!exists) {
			// create the schema
			createSchema();
			// create a user with uname/pwd 'cloud'
			createUser();
		}

	}

	public void disconnect() throws SQLException {
		connection.close();
		connection = null;

	}

	public Connection getConnection() {
		if (connection == null)
			try {
				connect();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return connection;
	}

	private void createSchema() {
		try {
			// log.debug("Creating tables...");
			executeSqlFromFile("create-tables.sql");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createUser() {
		try {
			// log.debug("Creating tables...");
			executeSqlFromFile("create-user.sql");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void executeSqlFromFile(String filename) throws SQLException,
			IOException, URISyntaxException {

		String sqlContent = readFileAsString(filename);

		PreparedStatement ps = connection.prepareStatement(sqlContent);
		ps.executeUpdate();
	}

	private String readFileAsString(String filename)
			throws java.io.IOException, URISyntaxException {
		InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream(filename);
		DataInputStream in = new DataInputStream(is);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		StringBuilder builder = new StringBuilder();
		while ((strLine = br.readLine()) != null) {
			builder.append("\n");
			builder.append(strLine);
		}

		in.close();

		return builder.toString();
	}

}
