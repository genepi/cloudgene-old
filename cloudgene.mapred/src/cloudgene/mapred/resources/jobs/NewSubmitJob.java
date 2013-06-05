package cloudgene.mapred.resources.jobs;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import cloudgene.mapred.apps.App;
import cloudgene.mapred.apps.YamlLoader;
import cloudgene.mapred.core.User;
import cloudgene.mapred.core.UserSessions;
import cloudgene.mapred.jobs.JobQueue;
import cloudgene.mapred.jobs.MapReduceJob;
import cloudgene.mapred.representations.JSONAnswer;
import cloudgene.mapred.util.FileUtil;
import cloudgene.mapred.util.HdfsUtil;
import cloudgene.mapred.util.S3Util;
import cloudgene.mapred.util.Settings;

public class NewSubmitJob extends ServerResource {

	@Post
	public Representation post(Representation entity) {
		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());
		if (user != null) {

			// tring tool = "minimac/new-simple";// props.get("tool");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
			String id = "job-" + sdf.format(new Date());// props.get("job-name");

			Map<String, String> props = new HashMap<String, String>();

			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),
					true)) {
				System.out.println("Multipart");
				List<FileItem> items = parseRequest();
				for (FileItem item : items) {
					String name = item.getName();
					// normal parameter
					if (name == null) {
						try {
							props.put(item.getFieldName(),
									new String(item.get(), "UTF-8"));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {

						System.out.println("Filed: " + item.getFieldName());

						// file parameter
						// write local file
						String tmpFile = FileUtil.getTempFilename(item
								.getName());
						File file = new File(tmpFile);
						try {
							item.write(file);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// import into hdfs
						if (file.exists()) {
							String entryName = item.getName();
							Settings settings = Settings.getInstance();
							String workspace = settings.getHdfsWorkspace(user
									.getUsername());

							String target = HdfsUtil.path(workspace, id,
									item.getFieldName(), entryName);

							System.out.println(target);

							//TODO: put only hdfs-folder/hdfs-file into hdfs, local files not
							
							HdfsUtil.put(tmpFile, target);

							if (props.containsKey(item.getFieldName())) {
								// folder
								String targetName = HdfsUtil.path(id,
										item.getFieldName());
								props.put(item.getFieldName(), targetName);
							} else {
								// file
								String targetName = HdfsUtil.path(id,
										item.getFieldName(), entryName);
								props.put(item.getFieldName(), targetName);
							}
						}

					}

				}

			}

			// Form form = new Form(entity);

			// check aws credentials and s3 bucket
			if (user.isExportToS3()) {

				if (!S3Util.checkBucket(user.getAwsKey(),
						user.getAwsSecretKey(), user.getS3Bucket())) {

					return new JSONAnswer(
							"Your AWS-Credentials are wrong or your S3-Bucket doesn't exists.<br/><br/>Please update your AWS-Credentials.",
							false);
				}

			}

			JobQueue queue = JobQueue.getInstance();
			String filename = Settings.getInstance().getApp();
			App app = null;
			try {
				app = YamlLoader.loadAppFromFile(filename);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (app.getMapred() != null) {

				try {

					MapReduceJob job = new MapReduceJob(app.getMapred());

					job.setId(id);
					job.setName(id);
					job.setUser(user);

					Set<String> names = props.keySet();

					// parse params
					for (String paramName : names) {
						System.out.println(paramName);
						if (paramName.startsWith("input-")) {
							String key = paramName.replace("input-", "");
							String value = props.get(paramName);
							job.setInputParam(key, value);
						}

					}

					queue.submit(job);

				} catch (Exception e) {
					e.printStackTrace();
					return new JSONAnswer(e.getMessage(), false);

				}

			}

			JSONObject answer = new JSONObject();
			try {
				answer.put("id", id);
				answer.put("success", true);
				answer.put("message",
						"Your job was successfully added to the job queue.");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return new StringRepresentation(answer.toString());

		} else {
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);

		}

		getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return new StringRepresentation("");

	}

	private List<FileItem> parseRequest() {
		List<FileItem> items = null;
		// 1/ Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1000240);

		// 2/ Create a new file upload handler based on the Restlet
		// FileUpload extension that will parse Restlet requests and
		// generates FileItems.
		RestletFileUpload upload = new RestletFileUpload(factory);

		try {
			items = upload.parseRequest(getRequest());
		} catch (FileUploadException e2) {
			e2.printStackTrace();
		}
		return items;
	}

}
