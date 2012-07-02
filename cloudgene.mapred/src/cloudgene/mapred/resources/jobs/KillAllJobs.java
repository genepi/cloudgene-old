package cloudgene.mapred.resources.jobs;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import cloudgene.mapred.core.User;
import cloudgene.mapred.core.UserSessions;
import cloudgene.mapred.representations.LoginPageRepresentation;
import cloudgene.mapred.util.HadoopUtil;

public class KillAllJobs extends ServerResource {

	@Get
	public Representation get() {

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());
		if (user != null) {
			HadoopUtil.getInstance().killAll(user);
			return new StringRepresentation("Lukas");

		} else {

			return new LoginPageRepresentation();

		}
	}

}
