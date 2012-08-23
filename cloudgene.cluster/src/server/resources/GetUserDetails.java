package server.resources;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;

import user.User;
import user.UserSessions;


public class GetUserDetails extends ServerResource {

	@Get
	public Representation represent(Variant variant) throws ResourceException {

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());
		StringRepresentation representation = null;
		if (user != null) {

			JsonConfig config = new JsonConfig();
			config.setExcludes(new String[] { "password" });

			JSONObject object = JSONObject.fromObject(user, config);

			representation = new StringRepresentation(
					object.toString(), MediaType.APPLICATION_JSON);
			
		} 
		return representation;
	}
}
