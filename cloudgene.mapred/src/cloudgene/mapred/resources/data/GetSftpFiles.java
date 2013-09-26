package cloudgene.mapred.resources.data;

import net.sf.json.JSONArray;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import cloudgene.mapred.core.User;
import cloudgene.mapred.core.UserSessions;
import cloudgene.mapred.representations.LoginPageRepresentation;
import cloudgene.mapred.util.FileItem;

public class GetSftpFiles extends ServerResource {

	@Post
	public Representation post(Representation entity) {

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		Form form = new Form(entity);
		String node = form.getFirstValue("node");

		StringRepresentation representation = null;

		if (user != null) {
			
			if (node.equals("NOLOAD")){
				getResponse().setStatus(Status.SUCCESS_OK);
				return;
			}
			else {
	
			FileItem[] items = cloudgene.mapred.util.SftpFileTree.getSftpFileTree(node);
			JSONArray jsonArray = JSONArray.fromObject(items);
			representation = new StringRepresentation(jsonArray.toString());
			getResponse().setStatus(Status.SUCCESS_OK);
			getResponse().setEntity(representation);
			return representation;
			}

		} else {

			representation = new LoginPageRepresentation();
			getResponse().setStatus(Status.SUCCESS_OK);
			getResponse().setEntity(representation);
			return representation;

		}

	}

}
