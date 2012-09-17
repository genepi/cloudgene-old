package cloudgene.mapred.resources.data;

import java.util.List;

import net.sf.json.JSONArray;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import cloudgene.mapred.core.User;
import cloudgene.mapred.core.UserSessions;
import cloudgene.mapred.database.JobDao;
import cloudgene.mapred.jobs.Job;
import cloudgene.mapred.representations.LoginPageRepresentation;
import cloudgene.mapred.util.HdfsItem;
import cloudgene.mapred.util.HdfsTree;
import cloudgene.mapred.util.Settings;

public class GetFormatsList extends ServerResource {

	@Post
	public Representation post(Representation entity) {

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		StringRepresentation representation = null;

		if (user != null) {
		
			
			Settings settings = Settings.getInstance();
			String workspace = settings.getHdfsWorkspace(user.getUsername());

			
			
			String format = (String) getRequest().getAttributes().get("format");

			JobDao dao = new JobDao();
			List<Job> jobs = dao.findAllByUserAndFormat(user, format);
			

			HdfsItem[] items = new HdfsItem[jobs.size()];
			int i = 0;
			for (Job job : jobs){
				items[i] = new HdfsItem();
				items[i].setText(job.getId());
				items[i].setLeaf(true);
				items[i].setPath(job.getOutputParams().get(0).getValue());
				i++;
			}
			JSONArray jsonArray = JSONArray.fromObject(items);

			representation = new StringRepresentation(jsonArray.toString());
			getResponse().setStatus(Status.SUCCESS_OK);
			getResponse().setEntity(representation);
			return representation;
			

		} else {

			representation = new LoginPageRepresentation();
			getResponse().setStatus(Status.SUCCESS_OK);
			getResponse().setEntity(representation);
			return representation;

		}

	}

}
