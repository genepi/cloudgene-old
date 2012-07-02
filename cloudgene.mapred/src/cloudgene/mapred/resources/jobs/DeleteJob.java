package cloudgene.mapred.resources.jobs;

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
import cloudgene.mapred.representations.ErrorPageRepresentation;
import cloudgene.mapred.representations.LoginPageRepresentation;
import cloudgene.mapred.util.FileUtil;
import cloudgene.mapred.util.HdfsUtil;
import cloudgene.mapred.util.Settings;

public class DeleteJob extends ServerResource {

	@Post
	protected Representation post(Representation entity, Variant variant) {
		Form form = new Form(entity);

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		if (user != null) {

			String id = form.getFirstValue("id");
			if (id != null) {

				// delete job from database
				JobDao dao = new JobDao();
				Job job = dao.findById(id);

				// delete hdfs folders
				String workspace = Settings.getInstance().getHdfsWorkspace(
						user.getUsername());

				String outputDirectory = HdfsUtil.makeAbsolute(HdfsUtil.path(
						workspace, "output", job.getId()));
				HdfsUtil.deleteDirectory(outputDirectory);

				String tempDirectory = HdfsUtil.makeAbsolute(HdfsUtil.path(
						workspace, "temp", job.getId()));
				HdfsUtil.deleteDirectory(tempDirectory);

				// delete local folder
				String localWorkspace = Settings.getInstance()
						.getLocalWorkspace(user.getUsername());

				String localOutputDirectory = FileUtil.path(localWorkspace,
						"output", job.getId());

				FileUtil.deleteDirectory(localOutputDirectory);

				dao.delete(job);

			} else {
				return new ErrorPageRepresentation("Id is missing.");
			}
		} else {

			return new LoginPageRepresentation();

		}

		return new StringRepresentation("ok");
	}

}
