package server.resources;
/**
 * @author seppinho
 *
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.representation.StringRepresentation;

import user.User;
import user.UserSessions;
import core.programs.CloudgeneYaml;
import core.programs.Programs;

public class GetPrograms extends ServerResource {

	
	@Get
	public Representation getPrograms() {

		UserSessions sessions = UserSessions.getInstance();
		User user = sessions.getUserByRequest(getRequest());
		if (user != null) {
			
			Map<String, CloudgeneYaml> progs = Programs.getInstance().getProgs();
			if(!progs.isEmpty()){
			System.out.println("DAT "+progs.get("CloudBurst"));
			JsonConfig config = new JsonConfig();
			Map<String, CloudgeneYaml> sortedMap = new TreeMap<String, CloudgeneYaml>(progs);
			config.setExcludes(new String[] { "mapred", "installed", "cluster"});
			JSONArray jsonArray = JSONArray.fromObject(sortedMap.values(),config);
			return new StringRepresentation(jsonArray.toString());
			}

		}
		return new StringRepresentation("Progs not loaded");

	}

}
