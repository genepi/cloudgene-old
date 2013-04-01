package cloudgene.mapred.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cloudgene.mapred.apps.Step;
import cloudgene.mapred.jobs.LogMessage;
import cloudgene.mapred.jobs.MapReduceJob;

public class StepDao extends Dao {

	private static final Log log = LogFactory.getLog(StepDao.class);

	public boolean insert(Step step) {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into steps (state, name, start_time, end_time, job_id) ");
		sql.append("values (?,?,?,?,?)");

		try {

			Object[] params = new Object[5];
			params[0] = 0;
			params[1] = step.getName();
			params[2] = System.currentTimeMillis();
			params[3] = System.currentTimeMillis();
			params[4] = step.getMyJob().getId();

			int id = updateAndGetKey(sql.toString(), params);
			System.out.println(id);
			step.setId(id);

			LogMessageDao dao = new LogMessageDao();
			if (step.getLogMessages() != null) {
				for (LogMessage logMessage : step.getLogMessages()) {
					dao.insert(logMessage);
				}
			}

			
			connection.commit();

			log.info("insert step successful.");

		} catch (SQLException e) {
			log.error("insert step failed.", e);
			return false;
		}

		return true;
	}

	public List<Step> findAllByJob(MapReduceJob job) {

		StringBuilder sql = new StringBuilder();
		sql.append("select * ");
		sql.append("from steps ");
		sql.append("where job_id = ? ");
		sql.append("order by start_time ");

		Object[] params = new Object[1];
		params[0] = job.getId();

		List<Step> result = new Vector<Step>();

		try {

			ResultSet rs = query(sql.toString(), params);
			while (rs.next()) {

				Step step = new Step();
				step.setId(rs.getInt("id"));
				step.setName(rs.getString("name"));

				// step.setState(rs.getInt("state"));
				step.setMyJob(job);

				LogMessageDao dao = new LogMessageDao();
				List<LogMessage> logMessages = dao.findAllByStep(step);
				step.setLogMessages(logMessages);

				result.add(step);
			}
			rs.close();

			log.info("find all log step successful. results: " + result.size());

			return result;
		} catch (SQLException e) {
			log.error("find all log step failed", e);
			return null;
		}
	}

}
