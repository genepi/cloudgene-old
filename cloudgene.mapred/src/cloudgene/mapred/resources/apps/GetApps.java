package cloudgene.mapred.resources.apps;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import cloudgene.mapred.apps.Category;
import cloudgene.mapred.apps.YamlLoader;
import cloudgene.mapred.core.User;
import cloudgene.mapred.core.UserSessions;
import cloudgene.mapred.representations.LoginPageRepresentation;

public class GetApps extends ServerResource {

	@Get
	public Representation get() {

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		if (user != null) {

			List<Category> apps = YamlLoader.loadApps();

			JsonConfig config = new JsonConfig();
			config.setExcludes(new String[] { "mapred", "installed", "cluster" });
			JSONArray jsonArray = JSONArray.fromObject(apps, config);

			return new StringRepresentation(jsonArray.toString());

		} else {

			return new LoginPageRepresentation();

		}

	}

}
