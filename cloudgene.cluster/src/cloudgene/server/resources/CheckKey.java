package cloudgene.server.resources;

import org.jets3t.service.S3ServiceException;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.representation.StringRepresentation;

import cloudgene.user.User;
import cloudgene.user.UserSessions;
import cloudgene.util.ConnectionUtil;


public class CheckKey extends ServerResource {

	@Post
	public Representation checkKey(Representation entity) {
		Representation representation = null;
		try {
			UserSessions sessions = UserSessions.getInstance();
			User user = sessions.getUserByRequest(getRequest());

			if (user != null) {
				Form form = new Form(entity);
				String key = form.getFirstValue("usr").toString();
				String pwd = form.getFirstValue("pwd").toString();
				// check credentials for S3
				ConnectionUtil.getInputStream(key, pwd);
				getResponse().setStatus(Status.SUCCESS_ACCEPTED);
				getResponse().setEntity(representation);
			}
		} catch (S3ServiceException e) {
			// TODO Auto-generated catch block
			representation = new StringRepresentation(
					"Please recheck your credentials");
			getResponse().setEntity(representation);
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			e.printStackTrace();
			return representation;
		}
		return representation;
	}
	
	
	@Get
	public Representation keyAvailable(Representation entity) {
		Representation representation = null;
		
			UserSessions sessions = UserSessions.getInstance();
			User user = sessions.getUserByRequest(getRequest());
			if (user != null) {
				if(user.getCloudKey()!=null&&!user.getCloudKey().equals("")){
				getResponse().setStatus(Status.SUCCESS_ACCEPTED);
				getResponse().setEntity(representation);
				return representation;
			}
				else{
					getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					return representation;
				}
			}
		
		return null;
	}
}
