package server.resources;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.jets3t.service.S3ServiceException;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import database.Dao;
import database.UserDao;
import user.User;
import user.UserSessions;

public class CloudCredentials extends ServerResource {

	@Get
	public Representation getCredentials(Variant variant)
			throws ResourceException {

		UserSessions sessions = UserSessions.getInstance();
		StringRepresentation representation = null;
		User user = sessions.getUserByRequest(getRequest());
		user.setSaveKey(true);
		UserDao dao = new UserDao();
		if (dao.getCloudAvailable(user.getUsername()).equals("")
				|| dao.getCloudAvailable(user.getUsername()) == null)
			user.setSaveKey(false);

		if (user != null) {

			JsonConfig config = new JsonConfig();
			config.setExcludes(new String[] { "password" });

			JSONObject object = JSONObject.fromObject(user, config);

			representation = new StringRepresentation(object.toString(),
					MediaType.APPLICATION_JSON);

		}
		return representation;
	}
}
