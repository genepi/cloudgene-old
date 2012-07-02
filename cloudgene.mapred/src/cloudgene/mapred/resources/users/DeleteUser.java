package cloudgene.mapred.resources.users;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import cloudgene.mapred.core.User;
import cloudgene.mapred.core.UserSessions;
import cloudgene.mapred.database.UserDao;
import cloudgene.mapred.representations.ErrorPageRepresentation;
import cloudgene.mapred.representations.LoginPageRepresentation;

public class DeleteUser extends ServerResource {

	@Post
	public Representation post(Representation entity) {

		Form form = new Form(entity);

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		if (user != null) {

			String id = form.getFirstValue("id");
			if (id != null) {

				// delete job from database
				UserDao dao = new UserDao();
				User user1 = dao.findById(Integer.parseInt(id));
				dao.delete(user1);

				return new StringRepresentation("OK");

			} else {
				return new ErrorPageRepresentation("Id is missing.");
			}
		} else {

			return new LoginPageRepresentation();

		}
	}

}
