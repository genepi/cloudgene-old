package cloudgene.server.resources;

/**
 * @author seppinho
 *
 */
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.representation.StringRepresentation;

import cloudgene.user.User;
import cloudgene.user.UserSessions;


public class LogoutUser extends ServerResource {

	@Get
	public Representation logOut() {
		StringRepresentation representation = null;

		String token = getRequest().getCookies().getFirstValue(
				UserSessions.COOKIE_NAME);

		// logout and remove cookie
		if (token != null) {
			UserSessions sessions = UserSessions.getInstance();
			User user = sessions.getUserByRequest(getRequest());
			user.setCloudKey("");
			user.setCloudSecure("");
			sessions.logoutUserByToken(token);
			getRequest().getCookies().removeAll(UserSessions.COOKIE_NAME);
		}

		getResponse().redirectTemporary("/");

		return representation;
	}

}
