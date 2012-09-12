package cloudgene.server.resources;

import net.sf.json.JSONArray;

import org.jets3t.service.ServiceException;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.representation.StringRepresentation;

import cloudgene.user.User;
import cloudgene.user.UserSessions;
import cloudgene.util.BucketItem;
import cloudgene.util.BucketTree;



public class GetMyBuckets extends ServerResource {

	@Post
	public Representation acceptRepresentation(Representation entity)
			throws ResourceException {
		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());
		StringRepresentation representation=null;
		Form form = new Form(entity);

		String username = form.getFirstValue("usr");
		String password = form.getFirstValue("pwd");
		if (user != null) {
			BucketItem[] items = null;
			try {

				items = BucketTree.getBucketTree(username,
						password, "");
				if (items == null) {
					
					representation = new StringRepresentation("");
					getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
					getResponse().setEntity(representation);
					return representation;
				}
				JSONArray jsonArray = JSONArray.fromObject(items);
				representation = new StringRepresentation(jsonArray.toString());
				getResponse().setEntity(representation);
				getResponse().setStatus(Status.SUCCESS_OK);
				return representation;

			} catch (ServiceException e) {
				representation = new StringRepresentation(null);
				getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				getResponse().setEntity(representation);
				return representation;

			}
		}
		return representation;
	}
}
