package server.resources;

/**
 * @author seppinho
 *
 */

import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.representation.StringRepresentation;
import database.UserDao;
import user.User;
import user.UserSessions;
import util.Utils;

public class ChangePwd extends ServerResource {

	@Post
	public Representation changePwd(Representation entity) {
		Representation representation = null;
		try {
			JsonRepresentation represent = new JsonRepresentation(entity);

			UserSessions sessions = UserSessions.getInstance();
			User user = sessions.getUserByRequest(getRequest());

			JSONObject obj = represent.getJsonObject();
			if (user != null) {
				String pwdUI = Utils.getMD5(obj.get("pwdOld") + "");
				String pwdNew = obj.get("pwdNew") + "";
				String pwdControl = obj.get("pwdControl") + "";

				UserDao uDao = new UserDao();
				String pwd = uDao.getDatabasePwd(user.getUsername());
				if (pwd.equals(pwdUI) && pwdNew.equals(pwdControl)) {
					user.setPassword(Utils.getMD5(pwdNew));
					uDao.updatePwd(user);
					getResponse().setStatus(Status.SUCCESS_OK);
					
				} else {
					representation = new StringRepresentation("Check passwords");
					getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					getResponse().setEntity(representation);
					return representation;
				}

			} else {
				representation = new StringRepresentation("No user");
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				getResponse().setEntity(representation);
				return representation;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return representation;

	}
}
