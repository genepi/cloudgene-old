package cloudgene.mapred.resources.jobs;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import cloudgene.mapred.apps.MyFile;
import cloudgene.mapred.apps.OutputParameter;
import cloudgene.mapred.apps.Parameter;
import cloudgene.mapred.core.User;
import cloudgene.mapred.core.UserSessions;
import cloudgene.mapred.database.JobDao;
import cloudgene.mapred.jobs.Job;
import cloudgene.mapred.jobs.JobQueue;
import cloudgene.mapred.representations.ErrorPageRepresentation;
import cloudgene.mapred.representations.LoginPageRepresentation;
import cloudgene.mapred.util.FileItem;
import cloudgene.mapred.util.FileUtil;
import cloudgene.mapred.util.Settings;

public class GetJobDetails extends ServerResource {

	@Post
	protected Representation post(Representation entity, Variant variant) {

		Form form = new Form(entity);

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		if (user != null) {

			String jobId = form.getFirstValue("id");

			if (jobId != null) {

				Job job = JobQueue.getInstance().getJobById(jobId);

				if (job == null) {

					JobDao dao = new JobDao();
					job = dao.findById(jobId, true);

				}

				Settings settings = Settings.getInstance();
				String workspace = settings.getLocalWorkspace(user
						.getUsername());

				if (job != null) {

					for (Parameter param : job.getOutputParams()) {

						OutputParameter outParam = (OutputParameter) param;

						String n = FileUtil.path(workspace, "output",
								job.getId(), param.getId());

						File f = new File(n);

						if (f.exists() && f.isDirectory()) {

							FileItem[] items = cloudgene.mapred.util.FileTree
									.getFileTree(FileUtil.path(workspace,
											"output", job.getId()), param
											.getId());

							List<MyFile> files = new Vector<MyFile>();

							for (FileItem item : items) {
								MyFile myFile = new MyFile();
								myFile.setName(item.getText());
								myFile.setPath(item.getId());
								myFile.setSize(item.getSize());
								files.add(myFile);
							}

							Collections.sort(files);

							outParam.setFiles(files);
						}

					}

					JsonConfig config = new JsonConfig();
					config.setExcludes(new String[] { "user", "task","mapReduceJob","myJob","step" });

					JSONObject object = JSONObject.fromObject(job, config);

					return new StringRepresentation(object.toString(),
							MediaType.APPLICATION_JSON);

				} else {

					return new StringRepresentation("[]",
							MediaType.APPLICATION_JSON);

				}

			} else {

				return new ErrorPageRepresentation("Id is missing.");

			}

		} else {

			return new LoginPageRepresentation();

		}
	}

}
