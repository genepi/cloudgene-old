package cloudgene.mapred.resources.jobs;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import cloudgene.mapred.core.User;
import cloudgene.mapred.core.UserSessions;
import cloudgene.mapred.database.JobDao;
import cloudgene.mapred.jobs.Job;
import cloudgene.mapred.jobs.JobQueue;
import cloudgene.mapred.jobs.MapReduceJob;
import cloudgene.mapred.representations.LoginPageRepresentation;
import cloudgene.mapred.util.Timer;

public class GetJobs extends ServerResource {

	/**
	 * Resource to get job status information
	 */

	@Get
	public Representation getJobs() {

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		if (user != null) {

			JobDao dao = new JobDao();

			int limit = 0;

			if (getRequest().getAttributes().get("limit") != null) {
				limit = Integer.parseInt((String) getRequest().getAttributes()
						.get("limit"));
			}

			// jobs in queue
			List<Job> jobs = JobQueue.getInstance().getJobsByUser(user);
			for (Job job : jobs) {

				if (job instanceof MapReduceJob) {

					((MapReduceJob) job).updateProgress();

				}

			}

			if (limit > 0) {
				limit = limit - jobs.size();

				// finished jobs
				Timer.start();
				List<Job> oldJobs = dao.findAllByUser(user, false, limit);
				Timer.stop();
				jobs.addAll(oldJobs);

			} else {

				Timer.start();
				List<Job> oldJobs = dao.findAllByUser(user, false, 0);
				Timer.stop();
				jobs.addAll(oldJobs);

			}

			JsonConfig config = new JsonConfig();
			config.setExcludes(new String[] { "user", "outputParams",
					"inputParams", "output", "endTime", "startTime", "error",
					"s3Url", "task", "config", "mapReduceJob", "myJob", "step" });
			JSONArray jsonArray = JSONArray.fromObject(jobs, config);

			return new StringRepresentation(jsonArray.toString());

		} else {

			return new LoginPageRepresentation();

		}

	}

}
