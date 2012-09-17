package cloudgene.core;

/**
 * @author seppinho
 *
 */
import org.apache.commons.configuration.PropertiesConfiguration;

import cloudgene.core.programs.ClusterSetup;
import cloudgene.user.User;
import cloudgene.util.Settings;



public class ClusterConfiguration {

	public static final int BUILDING = 1;

	public static final int QUEUE = 2;

	public static final int UP = 3;

	public static final int DOWN = 4;

	public static final int DESTROYING = 5;

	public static final int COPTY_DATA = 6;

	public static final short CREATE_CLUSTER = 0;

	public static final short DESTROY_CLUSTER = 1;

	private int pk;

	private String cloudID;

	private String name;

	private ClusterSetup program;

	private String cloudUsername;

	private String cloudPwd;

	private String webAddress;

	private String provider;

	private int amount;

	private int state;

	private User cloudgeneUser;

	private String sshPublic;

	private String sshPrivate;

	private short actionType;

	private boolean downloadSSH;
	
	private String instanceType;
	
	private String s3Bucket="";

	private String log;
	
	private long startTime;

	public void setPk(int id) {
		this.pk = id;
	}

	public int getPk() {
		return pk;
	}

	public String getName() {
		return name;
	}

	public void setName(String l) {
		this.name = l;
	}

	public String getCloudUsername() {
		return cloudUsername;
	}

	public void setCloudUsername(String username) {
		this.cloudUsername = username;
	}

	public String getCloudPassword() {
		return cloudPwd;
	}

	public void setCloudPassword(String password) {
		this.cloudPwd = password;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setCloudgeneUser(User user) {
		this.cloudgeneUser = user;
	}

	public User getCloudgeneUser() {
		return cloudgeneUser;
	}

	public void setWebAddress(String webAddress) {
		this.webAddress = webAddress;
	}

	public String getWebAddress() {
		return webAddress;
	}

	public void setCloudID(String fileId) {
		this.cloudID = fileId;
	}

	public String getCloudId() {
		return cloudID;
	}

	public void setActionType(short actionType) {
		this.actionType = actionType;
	}

	public short getActionType() {
		return actionType;
	}

	public void setProgram(ClusterSetup program) {
		this.program = program;
	}

	public ClusterSetup getProgram() {
		return program;
	}

	public void setSshPublic(String sshPublic) {
		this.sshPublic = sshPublic;
	}

	public String getSshPublic() {
		return sshPublic;
	}

	public void setSshPrivate(String sshPrivate) {
		this.sshPrivate = sshPrivate;
	}
 
	public String getSshPrivate() {
		return sshPrivate;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getLog() {
		return log;
	}

	public void setSSHAvailable(boolean downloadSSH) {
		this.downloadSSH = downloadSSH;
	}

	public boolean isSSHAvailable() {
		return downloadSSH;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getProvider() {
		return provider;
	}

	public PropertiesConfiguration createStartUp() {
		PropertiesConfiguration conf;
		conf = new PropertiesConfiguration();
		
		conf.addProperty("whirr.cluster-name", cloudID);
		conf.addProperty("whirr.private-key-file", sshPrivate);
		conf.addProperty("whirr.public-key-file", sshPublic);
		conf.addProperty("whirr.identity", cloudUsername);
		conf.addProperty("whirr.credential", cloudPwd);
		conf.addProperty("whirr.instance-templates",
				"1 nn+jt," + amount
						+ " dn+tt");
		conf.addProperty("whirr.cluster-user", Settings.getInstance().getCloudUser());
		conf.addProperty("whirr.provider", program.getProvider());
		conf.addProperty("whirr.image-id", program.getImage());
		if (!program.getUser().equals(""))
		conf.addProperty("whirr.login-user", program.getUser());
		conf.addProperty("whirr.hardware-id", instanceType);
		//conf.addProperty("whirr.location-id", program.getLocation());
		if (!program.getOwner().equals(""))
			conf.addProperty("jclouds.ec2.ami-owners", program.getOwner());
		if (program.getService().equals("hadoop")) {
			conf.addProperty("whirr.hadoop.install-function",
					"install_cdh_hadoop");
			conf.addProperty("whirr.hadoop.configure-function",
					"configure_cdh_hadoop");
		}
		/** user defined variables */
		 conf.addProperty("whirr.max-startup-retries", 2);
		 if(getAmount()>6)
		 conf.addProperty("whirr.instance-templates-max-percent-failures", "100 nn+jt,60 dn+tt");
		 conf.addProperty("whirr.client-cidrs", program.getCidrs());
		 conf.addProperty("whirr.env.repo", program.getHadoopVersion());
		 if (program.getProperties()!=""){
		 for(String newProb: program.getProperties().split(",")){
			 conf.addProperty(newProb.split("=")[0].trim(),newProb.split("=")[1].trim());}
		 }
		return conf;
	}
	
	public PropertiesConfiguration createShutDown() {
		PropertiesConfiguration conf;
		conf = new PropertiesConfiguration();
		conf.addProperty("whirr.cluster-name", cloudID);
		conf.addProperty("whirr.private-key-file", sshPrivate);
		conf.addProperty("whirr.public-key-file", sshPublic);
		conf.addProperty("whirr.identity", cloudUsername);
		conf.addProperty("whirr.credential", cloudPwd);
		conf.addProperty("whirr.instance-templates",
				"1 hadoop-namenode+hadoop-jobtracker+," + amount
						+ " hadoop-datanode+hadoop-tasktracker");
		conf.addProperty("whirr.cluster-user", Settings.getInstance().getCloudUser());
		conf.addProperty("whirr.provider", "aws-ec2");
		return conf;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

	public String getInstanceType() {
		return instanceType;
	}

	public void setS3Bucket(String s3Export) {
		this.s3Bucket = s3Export;
	}

	public String getS3Bucket() {
		return s3Bucket;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getStartTime() {
		return startTime;
	}

}
