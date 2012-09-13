package cloudgene.server.resources;

/**
 * @author seppinho
 *
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.representation.StringRepresentation;

import cloudgene.core.programs.Program;
import cloudgene.core.programs.Programs;
import cloudgene.user.User;
import cloudgene.user.UserSessions;
import cloudgene.util.Type;


public class GetTypes extends ServerResource {

	@Post
	public Representation acceptRepresentation(Representation entity) {
		StringRepresentation representation = null;
		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());
		Form form = new Form(entity);
		if (user != null) {
			Type cloudType;
			ArrayList<Type> typeList = new ArrayList<Type>();
			Program progYaml = Programs.getProgramByName(form
					.getFirstValue("prog"));
			JsonConfig config = new JsonConfig();
			String type = progYaml.getCluster().getType();
			StringTokenizer token = new StringTokenizer(type, ",");

			while (token.hasMoreTokens()) {
				cloudType = new Type();
				cloudType.setValue(token.nextToken());
				typeList.add(cloudType);
			}
			Collections.sort(typeList);
			JSONArray jsonArray = JSONArray.fromObject(typeList, config);
			representation = new StringRepresentation(jsonArray.toString());
			getResponse().setEntity(representation);
			getResponse().setStatus(Status.SUCCESS_OK);

		}
		return representation;
	}
}
