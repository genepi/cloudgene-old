package cloudgene.mapred.resources.apps;

import java.util.List;

import net.sf.json.JSONArray;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import cloudgene.mapred.apps.App;
import cloudgene.mapred.apps.Parameter;
import cloudgene.mapred.apps.YamlLoader;
import cloudgene.mapred.core.User;
import cloudgene.mapred.core.UserSessions;
import cloudgene.mapred.representations.LoginPageRepresentation;

public class GetAppParams extends ServerResource {

	@Post
	public Representation post(Representation entity) {
		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		Form form = new Form(entity);

		if (user != null) {

			App app = YamlLoader.loadApp(form.getFirstValue("tool"));

			List<Parameter> params = app.getMapred().getInputs();

			JSONArray jsonArray = JSONArray.fromObject(params);

			return new StringRepresentation(jsonArray.toString());

		} else {

			return new LoginPageRepresentation();

		}
	}

}
