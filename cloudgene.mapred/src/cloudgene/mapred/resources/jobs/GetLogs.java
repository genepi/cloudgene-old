package cloudgene.mapred.resources.jobs;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import cloudgene.mapred.core.User;
import cloudgene.mapred.core.UserSessions;
import cloudgene.mapred.database.JobDao;
import cloudgene.mapred.jobs.Job;
import cloudgene.mapred.representations.LoginPageRepresentation;
import cloudgene.mapred.util.FileUtil;

public class GetLogs extends ServerResource {

	@Get
	public Representation get() {

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		if (user != null) {
			String id = (String) getRequest().getAttributes().get("id");

			JobDao jobDao = new JobDao();
			Job job = jobDao.findById(id);

			if (job != null) {

				job.setUser(user);

				StringBuffer buffer = new StringBuffer();

				String log = FileUtil.readFileAsString(job.getLogOutFile());
				String output = FileUtil.readFileAsString(job.getStdOutFile());

				if (!log.isEmpty()) {
					buffer.append("job.txt:\n\n");
					buffer.append(log);

				}

				if (!output.isEmpty()) {

					buffer.append("\n\nstd.out:\n\n");
					buffer.append(output);

				}

				return new StringRepresentation(buffer.toString());

			} else {

				return new LoginPageRepresentation();
			}

		} else {

			return new LoginPageRepresentation();

		}

	}

}
