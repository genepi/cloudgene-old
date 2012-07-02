package cloudgene.mapred.resources.jobs;

import java.util.List;
import java.util.Vector;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import cloudgene.mapred.core.User;
import cloudgene.mapred.core.UserSessions;
import cloudgene.mapred.database.JobDao;
import cloudgene.mapred.jobs.Job;
import cloudgene.mapred.jobs.JobQueue;
import cloudgene.mapred.jobs.MapReduceJob;
import cloudgene.mapred.representations.LoginPageRepresentation;

public class GetJobStatus extends ServerResource {

	@Post
	protected Representation post(Representation entity, Variant variant) {

		Form form = new Form(entity);

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		if (user != null) {

			String jobId = form.getFirstValue("job_id");

			Job job = JobQueue.getInstance().getJobById(jobId);

			if (job == null) {

				JobDao dao = new JobDao();
				job = dao.findById(jobId, false);

			} else {

				if (job instanceof MapReduceJob) {

					((MapReduceJob) job).updateProgress();

				}

			}

			List<Job> jobs = new Vector<Job>();
			jobs.add(job);

			JsonConfig config = new JsonConfig();
			config.setExcludes(new String[] { "user", "outputParams",
					"inputParams", "output", "endTime", "startTime", "error",
					"s3Url", "task", "config" });
			JSONArray jsonArray = JSONArray.fromObject(jobs, config);

			return new StringRepresentation(jsonArray.toString());

		} else {

			return new LoginPageRepresentation();

		}

	}

}
