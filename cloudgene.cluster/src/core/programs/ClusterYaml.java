package core.programs;

/**
 * @author seppinho
 *
 */
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;

import util.EC2Communication;
import util.Settings;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class ClusterYaml {

	private String cloudFolder = Settings.getInstance().getCloudFolder();

	private boolean installMapred = true;

	private boolean creationOnly = false;

	private int mapredPort = 80;
	
	private String cidrs = "0.0.0.0/0";
	
	private String hadoopVersion = "cdh3u2";
	
	private String location = "us-east-1";

	private String service = "";

	private String user = "";

	private String initScript = "";

	private String execScript = "";

	private String owner = "";

	private String properties = "";

	private File folder;

	private String name;

	private String jarFile;

	private String ports;

	private String provider;

	private String command;

	private String image;

	private String type;

	public String getJarFile() {
		return jarFile;
	}

	public void setJarFile(String source) {
		this.jarFile = source;
	}

	public String getPorts() {
		return ports;
	}

	public void setPorts(String ports) {
		this.ports = ports;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getImage() {
		return image;
	}

	public void setFolder(File folder) {
		this.folder = folder;
	}

	public File getFolder() {
		return folder;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwner() {
		return owner;
	}

	public void setCreationOnly(boolean creationOnly) {
		this.creationOnly = creationOnly;
	}

	public boolean getCreationOnly() {
		return creationOnly;
	}

	public void setCidrs(String cidrs) {
		this.cidrs = cidrs;
	}

	public String getCidrs() {
		return cidrs;
	}

	public void setInstallMapred(boolean installMapred) {
		this.installMapred = installMapred;
	}

	public boolean isInstallMapred() {
		return installMapred;
	}

	public void setMapredPort(int mapredPort) {
		this.mapredPort = mapredPort;
	}

	public int getMapredPort() {
		return mapredPort;
	}

	public void setInitScript(String userScript) {
		this.initScript = userScript;
	}

	public String getInitScript() {
		return initScript;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	public String getProperties() {
		return properties;
	}

	public void setExecScript(String execScript) {
		this.execScript = execScript;
	}

	public String getExecScript() {
		return execScript;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public void setHadoopVersion(String hadoopVersion) {
		this.hadoopVersion = hadoopVersion;
	}

	public String getHadoopVersion() {
		return hadoopVersion;
	}

	public void setup(EC2Communication communication)
			throws FileNotFoundException, JSchException, SftpException {
		communication.executeCmd("mkdir " + cloudFolder);
		System.out.println("first command finished");
		communication.executeCmd("mkdir " + cloudFolder + "/"
				+ folder.getName());
		System.out.println("second command finished");
		communication
				.executeCmd("sudo rm /usr/lib/hadoop/lib/jets3t-0.6.1.jar");
		communication.executeCmd("sudo apt-get install unzip");
	}

	public void install(EC2Communication communication)
			throws FileNotFoundException, JSchException, SftpException {
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				return !file.getName().toLowerCase().startsWith(".");
			}
		};
		// copy program and sample data
		System.out.println("name: " + folder.getName());
		File[] files = folder.listFiles(fileFilter);
		String path = cloudFolder + "/" + folder.getName();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
			} else {
				communication.copyData(files[i].getPath(), path + "/"
						+ files[i].getName());
				/** unzip data if necessary */
				if (files[i].getName().endsWith("zip")) {
					communication.executeCmd("unzip " + path + "/"
							+ files[i].getName() + " -d " + path);
				}
			}

		}
	}

	public void startbyScript(EC2Communication communication, int clusterPK,
			String user, String pwd, int port, String bucket)
			throws FileNotFoundException, JSchException, SftpException {
		if (!execScript.equals("")) {
			communication.executeCmd("chmod 755 " + cloudFolder + "/"
					+ folder.getName() + "/" + execScript);
			communication.executeCmd("cd " + cloudFolder + "/"
					+ folder.getName() + ";./" + execScript);
		}
		
	}

}
