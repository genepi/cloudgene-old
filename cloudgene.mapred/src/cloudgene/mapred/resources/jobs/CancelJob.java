package cloudgene.mapred.resources.jobs;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import cloudgene.mapred.core.User;
import cloudgene.mapred.core.UserSessions;
import cloudgene.mapred.jobs.Job;
import cloudgene.mapred.jobs.JobQueue;
import cloudgene.mapred.representations.ErrorPageRepresentation;
import cloudgene.mapred.representations.LoginPageRepresentation;

public class CancelJob extends ServerResource {

	@Post
	protected Representation post(Representation entity, Variant variant) {

		Form form = new Form(entity);

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		if (user != null) {

			String id = form.getFirstValue("id");
			if (id != null) {

				Job job = JobQueue.getInstance().getJobById(id);

				if (job != null) {

					JobQueue.getInstance().cancel(job);
					return new StringRepresentation("ok");
					/*
					 * // delete hdfs folders String workspace =
					 * Settings.getInstance().getHdfsWorkspace(
					 * user.getUsername());
					 * 
					 * String outputDirectory = HdfsUtil.makeAbsolute(HdfsUtil
					 * .path(workspace, "output", job.getName()));
					 * HdfsUtil.deleteDirectory(outputDirectory);
					 * 
					 * String tempDirectory =
					 * HdfsUtil.makeAbsolute(HdfsUtil.path( workspace, "temp",
					 * job.getName())); HdfsUtil.deleteDirectory(tempDirectory);
					 * 
					 * // delete local folder String localWorkspace =
					 * Settings.getInstance()
					 * .getLocalWorkspace(user.getUsername());
					 * 
					 * String localOutputDirectory =
					 * FileUtil.path(localWorkspace, "output", job.getName());
					 * 
					 * FileUtil.deleteDirectory(localOutputDirectory);
					 */
				} else {
					return new StringRepresentation("not ok");
				}

			} else {
				return new ErrorPageRepresentation("Id is missing.");
			}
		} else {

			return new LoginPageRepresentation();

		}
	}

}
