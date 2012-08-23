package database;

/**
 * @author seppinho
 *
 */
import java.sql.ResultSet;
import java.sql.SQLException;

import user.User;
import util.MySecretKey;

public class UserDao extends Dao {

	public int insertUser(User user) {
		int id;
		StringBuilder sql = new StringBuilder();

		sql.append("insert into USER ");
		sql.append(" (USERNAME, PASSWORD, ADMIN) ");
		sql.append("values (?,?,?)");
		try {
			Object[] params = new Object[3];
			params[0] = user.getUsername();
			params[1] = user.getPassword();
			params[2] = user.isAdmin();
			id = updateAndGetKey(sql.toString(), params);

			user.setId(id);
			connection.commit();

			System.out.println("inserted user " + user.getUsername()
					+ " successfully.");

		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}

		return id;
	}

	public User findByUsername(String user, String pwd) {

		StringBuffer sql = new StringBuffer();

		sql.append("select * ");
		sql.append("from user ");
		sql.append("where username = ? ");
		sql.append("and password = ?");
		Object[] params = new Object[2];
		params[0] = user;
		params[1] = pwd;
		User result = null;

		try {
			ResultSet rs = query(sql.toString(), params);
			while (rs.next()) {
				result = new User();
				result.setId(rs.getInt("pk"));
				result.setUsername(rs.getString("username"));
				result.setPassword(rs.getString("password"));
				if ((rs.getString("cloudkey") != null)
						&& (!rs.getString("cloudkey").equals(""))) {
					result.setCloudKey(MySecretKey.decrypt((rs
							.getString("cloudkey"))));
					result.setCloudSecure(MySecretKey.decrypt((rs
							.getString("cloudpwd"))));
				}
				result.setSshPub(rs.getString("sshpub"));
				result.setSshKey(rs.getString("sshkey"));
				result.setAdmin(rs.getBoolean("admin"));
			}
			rs.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return result;
	}

	public String getDatabasePwd(String user) {

		String executeSql = "select password from USER where USERNAME = '"
				+ user + "'";
		String result = "";
		try {
			ResultSet rs = query(executeSql);
			while (rs.next()) {
				result = rs.getString(1);

			}
			rs.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return result;
	}

	public String getCloudAvailable(String user) {
		String result = null;
		String executeSql = "select CLOUDKEY from USER where USERNAME = '"
				+ user + "'";
		try {
			ResultSet rs = query(executeSql);
			while (rs.next()) {
				result = rs.getString(1);

			}
			rs.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return result;
	}

	public boolean usernameTaken(String user) {
		String executeSql = "select * from USER where USERNAME = '" + user
				+ "'";
		ResultSet rs;
		boolean value = false;
		try {
			rs = query(executeSql);
			value = rs.first();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;

	}

	public boolean checkType(String user) {
		boolean type = false;
		String executeSql = "select admin from USER where username = '" + user
				+ "'";

		try {
			ResultSet rs = query(executeSql);
			while (rs.next()) {
				type = rs.getBoolean(1);

			}
			rs.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return type;
	}

	public boolean updatePwd(User user) {

		StringBuilder sql = new StringBuilder();

		sql.append("update USER SET password=?" + " where pk=?");
		try {

			Object[] params = new Object[2];
			params[0] = user.getPassword();
			params[1] = user.getId();
			update(sql.toString(), params);
			connection.commit();

			System.out.println("updated cluster successfully");

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean updateCredential(User user) {

		StringBuilder sql = new StringBuilder();

		sql.append("update USER SET CLOUDKEY=?, CLOUDPWD=?" + " where pk=?");
		try {
			Object[] params = new Object[3];
			if (!user.getCloudKey().equals("") && user.getCloudKey() != null) {
				params[0] = MySecretKey.encrypt(user.getCloudKey());
				params[1] = MySecretKey.encrypt(user.getCloudSecure());
			} else {
				params[0] = user.getCloudKey();
				params[1] = user.getCloudSecure();
			}
			params[2] = user.getId();
			update(sql.toString(), params);
			connection.commit();

			System.out.println("updated keys successfully "
					+ user.getCloudKey() + " " + user.getCloudSecure());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean updateSSH(User user) {

		StringBuilder sql = new StringBuilder();

		sql.append("update USER SET SSHKEY=?, SSHPUB=?" + " where pk=?");
		try {

			Object[] params = new Object[3];
			params[0] = user.getSshKey();
			params[1] = user.getSshPub();
			params[2] = user.getId();
			update(sql.toString(), params);
			connection.commit();

			System.out.println("updated ssh successfully" + " "
					+ user.getSshKey() + " " + user.getSshPub());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
