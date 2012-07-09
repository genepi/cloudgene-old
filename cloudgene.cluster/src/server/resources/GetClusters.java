package server.resources;
/**
 * @author seppinho
 *
 */
import java.util.List;

import net.sf.json.JSONArray;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;

import core.ClusterDetails;


import queue.ClusterQueue;
import user.User;
import user.UserSessions;


public class GetClusters extends ServerResource {

	@Get
	public Representation represent(Variant variant) throws ResourceException {

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		if (user != null) {
			ClusterQueue queue = ClusterQueue.getInstance();
			List<ClusterDetails> jobs = queue.getJobsByUser(user);
			JSONArray jsonArray = JSONArray.fromObject(jobs);
			return new StringRepresentation(jsonArray.toString());
		}
		return new StringRepresentation("Access denied");
	}

}
