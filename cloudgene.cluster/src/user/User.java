package user;
/**
 * @author seppinho
 *
 */


public class User {
	
	private String username;
	
	private String pwd;
	
	private String cloudKey;
	
	private String cloudSecure;
	
	private String sshKey;
	
	private String sshPub;
	
	private int id;
	
	private boolean admin;
	
	private boolean saveKey;
	
	public User() {
		super();
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String pwd) {
		this.pwd = pwd;
	}

	public String getPassword() {
		return pwd;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setCloudKey(String cloudKey) {
		this.cloudKey = cloudKey;
	}

	public String getCloudKey() {
		return cloudKey;
	}

	public void setCloudSecure(String cloudSecure) {
		this.cloudSecure = cloudSecure;
	}

	public String getCloudSecure() {
		return cloudSecure;
	}

	public void setSshKey(String sshKey) {
		this.sshKey = sshKey;
	}

	public String getSshKey() {
		return sshKey;
	}

	public void setSshPub(String sshPub) {
		this.sshPub = sshPub;
	}

	public String getSshPub() {
		return sshPub;
	}

	public void setSaveKey(boolean saveKey) {
		this.saveKey = saveKey;
	}

	public boolean isSaveKey() {
		return saveKey;
	}


}
