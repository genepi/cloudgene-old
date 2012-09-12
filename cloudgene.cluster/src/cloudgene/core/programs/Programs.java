package cloudgene.core.programs;

/**
 * @author seppinho
 *
 */
import java.util.HashMap;
import java.util.Map;

public class Programs {

	private static Programs instance;

	private static Map<String, CloudgeneYaml> progs;

	public static Programs getInstance() {
		if (instance == null) {
			instance = new Programs();
		}
		return instance;
	}

	private Programs() {
		progs = new HashMap<String, CloudgeneYaml>();
	}

	public void addProgram(CloudgeneYaml prog) {
		System.out.println("add program "+prog.getName());
		progs.put(prog.getName(), prog);
	}

	public void deleteProgram(String token) {
		progs.remove(token);
	}

	public static CloudgeneYaml getProgramByName(String name) {
		return progs.get(name);
	}
	public Map<String, CloudgeneYaml> getProgs() {
		return progs;
	}


}
