package cloudgene.server.resources;

import java.io.IOException;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;


import cloudgene.core.programs.Repository;
import cloudgene.user.User;
import cloudgene.user.UserSessions;

import com.esotericsoftware.yamlbeans.YamlException;

public class GetAppsFromRepository extends ServerResource {

	final static String repoPath="http://cloudgene.uibk.ac.at/apps";
	@Get
	public Representation get() {

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());

		if (user != null) {

			Repository repo = new Repository(repoPath);
			try {
				repo.load();
			} catch (YamlException e) {
				e.printStackTrace();
				return new StringRepresentation("error");
			} catch (IOException e) {
				e.printStackTrace();
				return new StringRepresentation("error");
			}
			JsonConfig config = new JsonConfig();
			config.setExcludes(new String[] { "mapred", "installed", "cluster"});
			JSONArray jsonArray = JSONArray.fromObject(repo.getApps(),config);
			
			return new StringRepresentation(jsonArray.toString());

		} else {

			return new StringRepresentation("error");

		}

	}

}
