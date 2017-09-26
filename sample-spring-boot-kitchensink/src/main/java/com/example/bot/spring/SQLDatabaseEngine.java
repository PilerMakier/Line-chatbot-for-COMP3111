package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	@Override
	String search(String text) throws Exception {
		// Write your code here
		String result = null;
		BufferedReader br = null;
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(this.getClass().getResourceAsStream(FILENAME));
			br = new BufferedReader(isr);
			String sCurrentLine;

			Connection connection = getConnection();
			PreparedStatement stmt = connection
					.prepareStatement("SELECT * FROM line_chatbot where keyword like concat('%', ?, '%')");

			stmt.setString(1, text);
			ResultSet rs = stmt.executeQuery();

			while (result == null && (sCurrentLine = br.readLine()) != null) {
				String[] parts = sCurrentLine.split(":");
				if (text.toLowerCase().contains(rs.getString(1).toLowerCase())) {
					result = rs.getString(2);
				}
			}
		} catch (IOException e) {
			log.info("IOException while reading file: {}", e.toString());
		} finally {
			try {
				if (br != null)
					br.close();
				if (isr != null)
					isr.close();
			} catch (IOException ex) {
				log.info("IOException while closing file: {}", ex.toString());
			}
		}
		if (result != null)
			return result;
		throw new Exception("NOT FOUND");
	}

	private final String FILENAME = "/static/database.txt";

	// try {
	//
	// String username = "zrrgkkaeyjxogd";
	// String password =
	// "e2e5e10007006db01464dae5614ce30165032dd060b33d01a0cc10f33d432ec7";
	// String dbUrl = "ec2-54-225-192-243.compute-1.amazonaws.com";
	// Connection connection = DriverManager.getConnection(dbUrl, username,
	// password);
	// PreparedStatement stmt = connection
	// .prepareStatement("SELECT * FROM line_chatbot where keyword like concat('%',
	// ?, '%')");
	//
	// stmt.setString(1, text);
	// ResultSet rs = stmt.executeQuery();
	// while (rs.next()) {
	// System.out.println("Keyword: " + rs.getString(1) + "\tResponse:" +
	// rs.getString(2));
	// }
	// rs.close();
	// stmt.close();
	// connection.close();
	// return rs.getString(2);
	// } catch (Exception e) {
	// System.out.println(e);
	// }
	// return null;

	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath()
				+ "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info("dbUrl: {}", dbUrl);

		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
