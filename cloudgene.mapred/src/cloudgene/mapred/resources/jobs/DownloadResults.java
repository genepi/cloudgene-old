package cloudgene.mapred.resources.jobs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import cloudgene.mapred.core.User;
import cloudgene.mapred.core.UserSessions;
import cloudgene.mapred.database.JobDao;
import cloudgene.mapred.jobs.Job;
import cloudgene.mapred.representations.LoginPageRepresentation;
import cloudgene.mapred.util.FileUtil;
import cloudgene.mapred.util.Settings;

public class DownloadResults extends ServerResource {

	private static final Log log = LogFactory.getLog(DownloadResults.class);

	@Get
	public Representation get() {

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		JobDao jobDao = new JobDao();

		if (user != null) {

			String jobId = (String) getRequest().getAttributes().get("job");
			String id = (String) getRequest().getAttributes().get("id");

			String filename = null;

			if (getRequest().getAttributes().containsKey("filename")) {

				filename = (String) getRequest().getAttributes()
						.get("filename");

			}

			if (getRequest().getAttributes().containsKey("filename2")) {

				jobId = (String) getRequest().getAttributes().get("job") + "/"
						+ (String) getRequest().getAttributes().get("id");
				id = (String) getRequest().getAttributes().get("filename");

				filename = (String) getRequest().getAttributes().get(
						"filename2");

			}

			Job job = jobDao.findById(jobId);

			MediaType mediaType = MediaType.ALL;
			if (filename.endsWith(".zip")) {
				mediaType = MediaType.APPLICATION_ZIP;
			} else if (filename.endsWith(".txt") || id.endsWith(".csv")) {
				mediaType = MediaType.TEXT_PLAIN;
			} else if (filename.endsWith(".pdf")) {
				mediaType = MediaType.APPLICATION_PDF;
			} else if (filename.endsWith(".html")) {
				mediaType = MediaType.TEXT_HTML;
			}

			Settings settings = Settings.getInstance();
			String workspace = settings.getLocalWorkspace(user.getUsername());

			String resultFile = FileUtil.path(workspace, "output", job.getId(),
					id, filename);

			log.debug("Downloading file " + resultFile);

			return new FileRepresentation(resultFile, mediaType);

		} else {

			return new LoginPageRepresentation();

		}

	}

}
