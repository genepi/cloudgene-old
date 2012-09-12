package cloudgene.server.resources;

/**
 * @author seppinho
 *
 */
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import cloudgene.database.UserDao;
import cloudgene.user.User;
import cloudgene.user.UserSessions;
import cloudgene.util.Utils;

public class AddUser extends ServerResource {

	@Post
	public Representation addUser(Representation entity) {
		Representation representation = null;
		JsonRepresentation jsonRepresent = null;
		try {
			UserSessions sessions = UserSessions.getInstance();
			User user = sessions.getUserByRequest(getRequest());
			jsonRepresent = new JsonRepresentation(entity);
			JSONObject obj = jsonRepresent.getJsonObject();

			if (user != null) {
				String username = obj.get("username") + "";
				String pwd = obj.get("pwd") + "";
				String pwdControl = obj.get("pwdControl") + "";
				String type = obj.get("type") + "";

				UserDao uDao = new UserDao();
				if (uDao.usernameTaken(username)) {
					representation = new StringRepresentation(
							"Username already given");
					getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					getResponse().setEntity(representation);
					return representation;
				}

				else if (!pwd.equals(pwdControl)) {
					representation = new StringRepresentation(
							"Passwords not equal");
					getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					getResponse().setEntity(representation);
					return representation;
				}

				else if (!uDao.checkType(user.getUsername())) {
					representation = new StringRepresentation(
							"You are not logged in as an administator");
					getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					getResponse().setEntity(representation);
					return representation;

				} else {
					User newUser = new User();
					newUser.setUsername(username);
					newUser.setPassword(Utils.getMD5(pwd));
					if (type.equals("2"))
						newUser.setAdmin(true);
					else
						newUser.setAdmin(false);
					uDao.insertUser(newUser);
					getResponse().setStatus(Status.SUCCESS_OK);
					return representation;
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return representation;

	}
}
