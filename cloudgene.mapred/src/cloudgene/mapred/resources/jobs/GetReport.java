package cloudgene.mapred.resources.jobs;

import org.restlet.data.MediaType;
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
import cloudgene.mapred.util.FileUtil;

public class GetReport extends ServerResource {

	@Get
	public Representation get() {

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		if (user != null) {
			String id = (String) getRequest().getAttributes().get("id");
			String file = (String) getRequest().getAttributes().get("file");
			if (file != null){
				id += "/" + file;
			}
			
			JobDao jobDao = new JobDao();
			Job job = jobDao.findById(id);

			if (job == null) {
				job = JobQueue.getInstance().getJobById(id);
			}

			if (job != null) {

				job.setUser(user);

				if (job instanceof MapReduceJob) {

					MapReduceJob mapReduceJob = (MapReduceJob) job;

					StringBuffer buffer = new StringBuffer();

					String log = FileUtil.readFileAsString(mapReduceJob
							.getReportFile());

					if (!log.isEmpty()) {
						buffer.append(log);

					}else{
						return new StringRepresentation("No report file found.");
					}

					return new StringRepresentation(buffer.toString(),MediaType.TEXT_HTML);
				}
				return new StringRepresentation("No report file found.");

			} else {

				return new LoginPageRepresentation();
			}

		} else {

			return new LoginPageRepresentation();

		}

	}

}
