package cloudgene.core;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author seppinho Class to communicate only necessary data between Restlet and
 *         Extjs (other way see GetPrograms)
 */
public class ClusterDetails {

	private int id2;

	private String state;

	private String name;

	private String instanceType;

	private int amount;

	private String webAddress;

	private String ssh;

	private String log;

	private long startTime;

	private String upTime;

	public ClusterDetails() {

	}

	public ClusterDetails(ClusterConfiguration cluster) {
		setId2(cluster.getPk());
		name = String.valueOf(cluster.getName());
		if (cluster.getState() == ClusterConfiguration.BUILDING) {
			state = "building";
		} else if (cluster.getState() == ClusterConfiguration.QUEUE) {
			state = "waiting";
		} else if (cluster.getState() == ClusterConfiguration.DESTROYING) {
			state = "destroying";
		} else if (cluster.getState() == ClusterConfiguration.COPTY_DATA) {
			state = "copy user data";
		} else if (cluster.getState() == ClusterConfiguration.DOWN) {
			state = "down";
			long millis = cluster.getStartTime();
			if (millis > 0)
				setUpTime(getParsedTime(millis));
		} else {
			state = "ready";
			long millis = System.currentTimeMillis() - cluster.getStartTime();
			System.out.println(System.currentTimeMillis()+"millis "+millis);
			setUpTime(getParsedTime(millis));

		}

		setType(cluster.getInstanceType());
		setAmount(cluster.getAmount());
		setWebAddress(cluster.getWebAddress());
		if (cluster.isSSHAvailable())
			setSsh(cluster.getSshPrivate());
		setLog(cluster.getLog());
		setStartTime(cluster.getStartTime());
	}

	private String getParsedTime(long millis) {
		return String.format(
				"%d h %d min %d sec",
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
								.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes(millis)));
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.instanceType = type;
	}

	public String getWebAddress() {
		return webAddress;
	}

	public void setWebAddress(String webAddress) {
		this.webAddress = webAddress;
	}

	public String getType() {
		return instanceType;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}

	public void setId2(int id2) {
		this.id2 = id2;
	}

	public int getId2() {
		return id2;
	}

	public void setSsh(String ssh) {
		this.ssh = ssh;
	}

	public String getSsh() {
		return ssh;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getLog() {
		return log;
	}

	public void setStartTime(long date) {
		this.startTime = date;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setUpTime(String upTime) {
		this.upTime = upTime;
	}

	public String getUpTime() {
		return upTime;
	}

}
