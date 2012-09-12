package server.resources;

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

import cloudgene.database.ClusterDao;

import user.User;
import user.UserSessions;


public class GetKeys extends ServerResource {

	@Get
	public Representation represent(Variant variant) throws ResourceException {

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());
		Representation representation;
		if (user != null) {
			ClusterDao dao = new ClusterDao();
			String filename = getQuery().getFirstValue("id");
			if (dao.checkKey(filename, user.getId())) {
				filename += ".zip";
				representation = new FileRepresentation(filename.trim(),
						MediaType.APPLICATION_ZIP);
				representation.setDownloadName("sshKey_" + user.getUsername());
				representation.setDownloadable(true);
				/*Disposition disp = new Disposition();
				disp.setFilename("sshKey_" + user.getUsername());
				representation.setDisposition(new Disposition());*/
				return representation;
			}
		}
		return new StringRepresentation("Access denied");
	}

}
