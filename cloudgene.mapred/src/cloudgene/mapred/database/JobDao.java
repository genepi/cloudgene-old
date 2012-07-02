package cloudgene.mapred.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cloudgene.mapred.apps.Parameter;
import cloudgene.mapred.core.User;
import cloudgene.mapred.jobs.Job;
import cloudgene.mapred.jobs.JobFactory;
import cloudgene.mapred.jobs.MapReduceJob;

public class JobDao extends Dao {

	private static final Log log = LogFactory.getLog(JobDao.class);

	public boolean insert(Job job) {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into job (id, name, state, start_time, end_time, user_id, s3_url, type) ");
		sql.append("values (?,?,?,?,?,?,?,?)");

		try {

			Object[] params = new Object[8];
			params[0] = job.getId();
			params[1] = job.getName();
			params[2] = job.getState();
			params[3] = job.getStartTime();
			params[4] = job.getEndTime();
			params[5] = job.getUser().getId();
			params[6] = job.getS3Url();
			params[7] = job.getType();

			update(sql.toString(), params);

			connection.commit();

			ParameterDao dao = new ParameterDao();

			for (Parameter parameter : job.getInputParams()) {
				parameter.setJobId(job.getId());
				dao.insert(parameter);
			}
			for (Parameter parameter : job.getOutputParams()) {
				parameter.setJobId(job.getId());
				dao.insert(parameter);
			}
			connection.commit();
			log.info("insert job '" + job.getId() + "' successful.");

		} catch (SQLException e) {
			log.error("insert job '" + job.getId() + "' failed.", e);
			return false;
		}

		return true;
	}

	public boolean update(Job job) {
		StringBuilder sql = new StringBuilder();
		sql.append("update job ");
		sql.append("  set name = ?, state = ?, ");
		sql.append("  start_time = ?, end_time = ?, ");
		sql.append("  user_id = ?, s3_url = ?, type = ? ");
		sql.append("where id = ? ");
		try {

			Object[] params = new Object[8];
			params[0] = job.getName();
			params[1] = job.getState();
			params[2] = job.getStartTime();
			params[3] = job.getEndTime();
			params[4] = job.getUser().getId();
			params[5] = job.getS3Url();
			params[6] = job.getType();
			params[7] = job.getId();

			update(sql.toString(), params);

			connection.commit();

			log.info("update job successful.");

		} catch (SQLException e) {
			log.error("update job '" + job.getId() + "' failed", e);
			return false;
		}

		return true;
	}

	public boolean delete(Job job) {
		StringBuilder sql = new StringBuilder();
		sql.append("delete job ");
		sql.append("where id = ? ");
		try {

			Object[] params = new Object[1];
			params[0] = job.getId();

			update(sql.toString(), params);

			connection.commit();

			log.info("delete job '" + job.getId() + "' successful.");

		} catch (SQLException e) {
			log.error("delete job '" + job.getId() + "' failed", e);
			return false;
		}

		return true;
	}

	public List<Job> findAllByUser(User user, boolean loadParameters) {

		StringBuilder sql = new StringBuilder();
		sql.append("select * ");
		sql.append("from job ");
		sql.append("where user_id = ? ");
		sql.append("order by start_time desc ");

		Object[] params = new Object[1];
		params[0] = user.getId();

		List<Job> result = new Vector<Job>();

		try {

			ParameterDao dao = new ParameterDao();

			ResultSet rs = query(sql.toString(), params);
			while (rs.next()) {

				int type = rs.getInt("type");

				Job job = JobFactory.create(type);
				job.setId(rs.getString("id"));
				job.setName(rs.getString("name"));
				job.setState(rs.getInt("state"));
				job.setStartTime(rs.getLong("start_time"));
				job.setEndTime(rs.getLong("end_time"));
				job.setS3Url(rs.getString("s3_url"));
				job.setUser(user);

				if (loadParameters) {
					List<Parameter> inputParams = dao.findAllInputByJob(job);
					List<Parameter> outputParams = dao.findAllOutputByJob(job);
					job.setInputParams(inputParams);
					job.setOutputParams(outputParams);

				}

				result.add(job);
			}
			rs.close();

			log.info("find all jobs successful. results: " + result.size());

			return result;
		} catch (SQLException e) {
			log.error("find all jobs failed", e);
			return null;
		}
	}

	public Job findById(String id) {

		return findById(id, true);

	}

	public Job findById(String id, boolean loadParams) {

		StringBuilder sql = new StringBuilder();
		sql.append("select * ");
		sql.append("from job ");
		sql.append("where id = ?");

		Object[] params = new Object[1];
		params[0] = id;

		Job job = null;

		try {

			ParameterDao dao = new ParameterDao();

			ResultSet rs = query(sql.toString(), params);
			while (rs.next()) {

				int type = rs.getInt("type");

				job = JobFactory.create(type);
				job.setId(rs.getString("id"));
				job.setName(rs.getString("name"));
				job.setState(rs.getInt("state"));
				job.setStartTime(rs.getLong("start_time"));
				job.setEndTime(rs.getLong("end_time"));
				job.setS3Url(rs.getString("s3_url"));

				if (loadParams) {
					List<Parameter> inputParams = dao.findAllInputByJob(job);
					List<Parameter> outputParams = dao.findAllOutputByJob(job);
					job.setInputParams(inputParams);
					job.setOutputParams(outputParams);
				}

			}
			rs.close();

			if (job instanceof MapReduceJob) {
				((MapReduceJob) job).updateProgress();
			}

			if (job != null) {
				log.info("find job by id '" + id + "' successful.");
			} else {
				log.info("job '" + id + "' not found");
			}

			return job;
		} catch (SQLException e) {
			log.error("find job by id '" + id + "' failed", e);
			return null;
		}
	}
}
