package cloudgene.database;
/**
 * @author seppinho
 *
 */
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import cloudgene.core.ClusterConfiguration;




public class ClusterDao extends Dao {

	public int insertCluster(ClusterConfiguration data) {
		int id;
		StringBuilder sql = new StringBuilder();

		sql.append("insert into CLUSTER ");
		sql.append(" (NAME, IDENTIFIER, CLOUD_USERNAME, ADDRESS, INSTANCE_TYPE, KEY_AVAILABLE, KEY_SEC, KEY_PUB, AMOUNT, STATE, LOG, CLIENT_ID, CREATION_TIME) ");
		sql.append("values (?,?,?,?,?,?,?,?,?,?,?,?,?)");
		try {
			Object[] params = new Object[13];
			params[0] = data.getName();
			params[1] = data.getCloudId();
			params[2] = data.getCloudUsername();
			params[3] = data.getWebAddress();
			params[4] = data.getInstanceType();
			params[5] = data.isSSHAvailable();
			params[6] = data.getSshPrivate();
			params[7] = data.getSshPublic();
			params[8] = data.getAmount();
			params[9] = data.getState();
			params[10] = data.getLog();
			params[11] = data.getCloudgeneUser().getId();
			params[12] = data.getStartTime();
			id = updateAndGetKey(sql.toString(), params);

			data.setPk(id);
			connection.commit();

			System.out.println("inserted cluster " + data.getName()
					+ " successfully. "+sql.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}

		return id;
	}

	public boolean updateCluster(ClusterConfiguration clust) {

		StringBuilder sql = new StringBuilder();

		sql.append("update CLUSTER SET state=?, address=?, log=?, key_sec=?, creation_time=?" + "where pk=?");
		try {

			Object[] params = new Object[6];
			params[0] = clust.getState();
			params[1] = clust.getWebAddress();
			params[2] = clust.getLog();
			params[3] = clust.getSshPrivate();
			params[4] = clust.getStartTime();
			params[5] = clust.getPk();
			update(sql.toString(), params);
			connection.commit();

			System.out.println("updated cluster successfully."+clust.getState());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	public boolean updateCluster(int pk, int state) {

		StringBuilder sql = new StringBuilder();

		sql.append("update CLUSTER SET state=? " + "where pk=?");
		try {

			Object[] params = new Object[2];
			params[0] = state;
			params[1] = pk;
			update(sql.toString(), params);
			connection.commit();

			System.out.println("updated cluster successfully.");

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean removeCluster(int pk) {

		StringBuilder sql = new StringBuilder();

		sql.append("delete from CLUSTER where pk=?");
		try {

			Object[] params = new Object[1];
			params[0] = pk;
			update(sql.toString(), params);
			connection.commit();

			System.out.println("deleted cluster " + pk + " successfully.");

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	public boolean checkKey(String key, int clientid) {
		boolean check=false;
		StringBuilder sql = new StringBuilder();

		sql.append("select * from CLUSTER where key_sec=? and client_id=?");
		try {

			Object[] params = new Object[2];
			params[0] = key;
			params[1] = clientid;
			ResultSet rs = query(sql.toString(), params);
			if(!rs.wasNull())
				check=true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return check;
	}

	public List<ClusterConfiguration> findAllClustersDone(int clientid) {
		List<ClusterConfiguration> result = new Vector<ClusterConfiguration>();
		StringBuilder sql = new StringBuilder();
		sql.append("select * ");
		sql.append("from cluster ");
		sql.append("where client_id = ? and state>=3");
		Object[] params = new Object[1];
		params[0] = clientid;

		try {

			ResultSet rs = query(sql.toString(), params);
			while (rs.next()) {
				
				ClusterConfiguration cluster = new ClusterConfiguration();
				// result.setId(id);
				cluster.setPk(rs.getInt("pk"));
				cluster.setName(rs.getString("name"));
				cluster.setAmount(rs.getInt("amount"));
				cluster.setInstanceType(rs.getString("instance_type"));
				cluster.setState(rs.getInt("state"));
				cluster.setWebAddress(rs.getString("address"));
				cluster.setSSHAvailable(rs.getBoolean("key_available"));
				cluster.setSshPrivate(rs.getString("key_sec"));
				cluster.setSshPublic(rs.getString("key_pub"));
				cluster.setLog(rs.getString("log"));
				cluster.setStartTime(rs.getLong("creation_time"));
				cluster.setCloudgeneUser(null);
				result.add(cluster);
			}
			rs.close();
			
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public ClusterConfiguration findSpecificCluster(int clientid, int clusterid) {
		StringBuilder sql = new StringBuilder();
		sql.append("select * ");
		sql.append("from cluster ");
		sql.append("where PK = ? and client_id = ?");
		Object[] params = new Object[2];
		params[0] = clusterid;
		params[1] = clientid;

		try {

			ResultSet rs = query(sql.toString(), params);
			ClusterConfiguration cluster = new ClusterConfiguration();
			while (rs.next()) {
				cluster.setPk(rs.getInt("pk"));
				cluster.setName(rs.getString("name"));
				cluster.setCloudID(rs.getString("identifier"));
				cluster.setCloudUsername(rs.getString("cloud_username"));
				cluster.setAmount(rs.getInt("amount"));
				cluster.setInstanceType(rs.getString("instance_type"));
				cluster.setState(rs.getInt("state"));
				cluster.setWebAddress(rs.getString("address"));
				cluster.setSSHAvailable(rs.getBoolean("key_available"));
				cluster.setSshPrivate(rs.getString("key_sec"));
				cluster.setSshPublic(rs.getString("key_pub"));
				cluster.setLog(rs.getString("log"));
				cluster.setStartTime(rs.getLong("creation_time"));
				cluster.setCloudgeneUser(null);

			}
			return cluster;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

}
