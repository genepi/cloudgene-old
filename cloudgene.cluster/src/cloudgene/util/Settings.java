package cloudgene.util;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

public class Settings {
	
	public static String configLocation ="config/settings.yaml";

	private String cloudUser;

	private String appsPath;
	
	private String mapRed;
	
	private String cloudFolder;
	
	private String version;

	private static Settings instance = null;
	
	private Settings() {

	}
	public static void load(String filename) throws FileNotFoundException,
			YamlException {

		YamlReader reader = new YamlReader(new FileReader(filename));

		instance = reader.read(Settings.class);

	}

	public static Settings getInstance()  {
		if (instance == null) {
			instance = new Settings();
			try {
				load(configLocation);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		return instance;
	}

	public String getAppsPath() {
		return appsPath;
	}

	public void setAppsPath(String appsPath) {
		this.appsPath = appsPath;
	}


	public void setCloudUser(String cloudUser) {
		this.cloudUser = cloudUser;
	}

	public String getCloudUser() {
		return cloudUser;
	}

	public void setMapRed(String mapRed) {
		this.mapRed = mapRed;
	}

	public String getMapRed() {
		return mapRed;
	}

	public void setCloudFolder(String cloudFolder) {
		this.cloudFolder = cloudFolder;
	}

	public String getCloudFolder() {
		return cloudFolder;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getVersion() {
		return version;
	}

}
