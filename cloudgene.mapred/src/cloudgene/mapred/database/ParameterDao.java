package cloudgene.mapred.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cloudgene.mapred.apps.InputParameter;
import cloudgene.mapred.apps.OutputParameter;
import cloudgene.mapred.apps.Parameter;
import cloudgene.mapred.jobs.Job;

public class ParameterDao extends Dao {

	private static final Log log = LogFactory.getLog(ParameterDao.class);

	public boolean insert(Parameter parameter) {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into parameter (name, value, input, job_id, type, variable, download) ");
		sql.append("values (?,?,?,?,?,?,?)");

		try {

			Object[] params = new Object[7];
			params[0] = parameter.getDescription();
			params[1] = parameter.getValue();
			params[2] = parameter.isInput();
			params[3] = parameter.getJobId();
			params[4] = parameter.getType();
			params[5] = parameter.getId();
			params[6] = parameter.isDownload();

			update(sql.toString(), params);

			connection.commit();

			log.info("insert parameter '" + parameter.getId() + "' successful.");

		} catch (SQLException e) {
			log.error("insert parameter '" + parameter.getId() + "' failed.", e);
			return false;
		}

		return true;
	}

	public List<Parameter> findAllInputByJob(Job job) {

		StringBuilder sql = new StringBuilder();
		sql.append("select * ");
		sql.append("from parameter ");
		sql.append("where job_id = ? and input = true");

		Object[] params = new Object[1];
		params[0] = job.getId();

		List<Parameter> result = new Vector<Parameter>();

		try {

			ResultSet rs = query(sql.toString(), params);
			while (rs.next()) {

				Parameter parameter = new InputParameter();
				parameter.setDescription(rs.getString("name"));
				parameter.setValue(rs.getString("value"));
				parameter.setId(rs.getString("variable"));
				parameter.setInput(rs.getBoolean("input"));
				parameter.setJobId(rs.getString("job_id"));
				parameter.setType(rs.getString("type"));
				parameter.setDownload(rs.getBoolean("download"));
				result.add(parameter);
			}
			rs.close();

			log.info("find all input parameters for job '" + job.getId()
					+ "' successful. results: " + result.size());

			return result;
		} catch (SQLException e) {
			log.info("find all input parameters for job '" + job.getId()
					+ "' failed.", e);
			return null;
		}
	}

	public List<Parameter> findAllOutputByJob(Job job) {

		StringBuilder sql = new StringBuilder();
		sql.append("select * ");
		sql.append("from parameter ");
		sql.append("where job_id = ? and input = false");

		Object[] params = new Object[1];
		params[0] = job.getId();

		List<Parameter> result = new Vector<Parameter>();

		try {

			ResultSet rs = query(sql.toString(), params);
			while (rs.next()) {

				Parameter parameter = new OutputParameter();
				parameter.setDescription(rs.getString("name"));
				parameter.setId(rs.getString("variable"));
				parameter.setValue(rs.getString("value"));
				parameter.setInput(rs.getBoolean("input"));
				parameter.setJobId(rs.getString("job_id"));
				parameter.setType(rs.getString("type"));
				parameter.setDownload(rs.getBoolean("download"));
				result.add(parameter);

			}
			rs.close();

			log.info("find all output parameters for job '" + job.getId()
					+ "' successful. results: " + result.size());

			return result;
		} catch (SQLException e) {
			log.info("find all output parameters for job '" + job.getId()
					+ "' failed.", e);
			return null;
		}
	}

}
