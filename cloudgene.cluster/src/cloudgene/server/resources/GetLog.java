package cloudgene.server.resources;
/**
 * @author seppinho
 *
 */

import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;

import cloudgene.user.User;
import cloudgene.user.UserSessions;



public class GetLog extends ServerResource {
	

	@Get
	public Representation represent(Variant variant) throws ResourceException {

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		if (user != null) {
			String filename=getQuery().getFirstValue("id");
			System.out.println("filename "+filename);
			return new FileRepresentation(filename.trim(), MediaType.TEXT_PLAIN);
		}
		return new StringRepresentation("Access denied!");
	}
}
