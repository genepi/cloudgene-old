package cloudgene.core;

/**
 * @author seppinho
 *
 */
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.whirr.Cluster.Instance;
import org.apache.whirr.ClusterController;
import org.apache.whirr.ClusterSpec;
import org.apache.whirr.RolePredicates;
import org.apache.whirr.service.ComputeCache;
import org.apache.whirr.service.FirewallManager;
import org.apache.whirr.service.FirewallManager.Rule;
import org.apache.whirr.service.hadoop.HadoopCluster;
import org.jclouds.compute.ComputeServiceContext;

import cloudgene.core.programs.ClusterSetup;
import cloudgene.database.ClusterDao;
import cloudgene.queue.ClusterQueue;
import cloudgene.util.EC2Communication;



public class ClusterTask implements Runnable {

	private static final long serialVersionUID = 1L;

	private ClusterConfiguration clusterConfig;

	public ClusterTask(ClusterConfiguration _cloud) {
		this.clusterConfig = _cloud;
	}

	@Override
	public void run() {

		if (clusterConfig.getActionType() == ClusterConfiguration.CREATE_CLUSTER) {

			try {
				launchCluster();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (clusterConfig.getActionType() == ClusterConfiguration.DESTROY_CLUSTER) {
			try {
				destroyCluster();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void launchCluster() throws IOException {

		FileWriter logWriter = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logWriter = new FileWriter(clusterConfig.getLog());
		ClusterSetup prog = clusterConfig.getProgram();
		String namenode = null;
		org.apache.whirr.Cluster c;
		ClusterSpec spec;
		PropertiesConfiguration whirrConfig;

		clusterConfig.setState(ClusterConfiguration.BUILDING);

		logWriter.write("Cloudgene-Cluster log:\n");
		logWriter.flush();
		logWriter.append(df.format(new Date()) + ": launch cluster"
				+ System.getProperty("line.separator"));
		logWriter.flush();

		try {

			whirrConfig = clusterConfig.createStartUp();
			/*logWriter.append(df.format(new Date())
					+ ": using the following configuration: "
					+ prog.getName()+System.getProperty("line.separator"));*/
			//whirrConfig.save(logWriter);
			logWriter.flush();

			/**
			 ************************************************ 
			 * launch a new cluster
			 ************************************************ 
			 */

			spec = new ClusterSpec(whirrConfig);
			ClusterController controller = new ClusterController();
			c = controller.launchCluster(spec);

			/**
			 ************************************************ 
			 * modify cluster
			 ************************************************ 
			 */

			clusterConfig.setState(ClusterConfiguration.COPTY_DATA);

			/**
			 ************************************************ 
			 * open ports
			 ************************************************ 
			 */

			logWriter.append(df.format(new Date()) + ": ***PORTS***"
					+ System.getProperty("line.separator"));
			logWriter.flush();
			String ports = prog.getPorts();
			StringTokenizer st = new StringTokenizer(ports, ",");
			Instance namenode1 = c.getInstanceMatching(RolePredicates
					.role("hadoop-namenode"));
			ComputeServiceContext computeServiceContext = ComputeCache.INSTANCE
					.apply(spec);
			FirewallManager firewall = new FirewallManager(
					computeServiceContext, spec, c);
			while (st.hasMoreTokens()) {
				String port=st.nextToken();
				
				logWriter.write(df.format(new Date()) + ": port: "+port + System.getProperty("line.separator"));
				logWriter.flush();
				
				firewall.addRule(Rule.create().destination(namenode1)
						.ports(Integer.valueOf(port)));
			}
			namenode = HadoopCluster.getNamenodePublicAddress(c)
					.getHostAddress();
			
			Set<Instance> instances = c.getInstances();
			for (Instance i1 : instances) {
				logWriter.append(df.format(new Date()) + ": ***NODES*** "
						+ System.getProperty("line.separator"));
				logWriter.flush();
				
				logWriter.append(df.format(new Date())  
						+ ": IP "+i1.getPublicIp() + "METADATA " +i1.getNodeMetadata()
						+ System.getProperty("line.separator"));
				logWriter.flush();
				
			}
			
			logWriter.append(df.format(new Date()) + ": ***NAMENDOE ADDRESS**** " + namenode
					+ System.getProperty("line.separator"));
			logWriter.flush();
			
			/**
			 ************************************************ 
			 * copy user data
			 ************************************************ 
			 */

			logWriter.append(df.format(new Date()) + ": copy user data"
					+ System.getProperty("line.separator"));
			logWriter.flush();

			if (!prog.getCreationOnly()) {
				/** connect to EC2 */
				EC2Communication communication = new EC2Communication(namenode,
						clusterConfig.getSshPrivate());
				communication.connect();

				/**
				 ************************************************ 
				 * create folder
				 ************************************************ 
				 */

				prog.setup(communication);

				/**
				 ************************************************ 
				 * install programm data
				 ************************************************ 
				 */

				System.out.println("install");
				prog.install(communication);

				/**
				 ************************************************ 
				 * check if EMI (Elastic MapReduce Interface) is needed
				 ************************************************ 
				 */

				if (prog.isInstallMapred()) {
					logWriter.append(df.format(new Date())
							+ ": install Cloudgene-MapRed"
							+ System.getProperty("line.separator"));
					logWriter.flush();
					MapReduce cloudgeneMapRed = new MapReduce();
					cloudgeneMapRed.install(communication);
					cloudgeneMapRed.startWebInterface(communication,
							clusterConfig.getPk(), clusterConfig
									.getCloudgeneUser().getUsername(),
							clusterConfig.getCloudgeneUser().getPassword(),
							prog.getMapredPort(), clusterConfig.getS3Bucket());

				} else {
					logWriter.append(df.format(new Date())
							+ ": install webinterface"
							+ System.getProperty("line.separator"));
					logWriter.flush();

					prog.startbyScript(communication, clusterConfig.getPk(),
							clusterConfig.getCloudgeneUser().getUsername(),
							clusterConfig.getCloudgeneUser().getPassword(), 0,
							"");

				}
				communication.disconnect();
			}

			/**
			 ************************************************ 
			 * execute init script on all nodes
			 ************************************************ 
			 */

			if (prog.getInitScript() != "") {

				File script = new File(prog.getFolder().getPath()
						+ File.separatorChar + prog.getInitScript());
				System.out.println("Instance size is " + instances.size());
				for (Instance i1 : instances) {
					
					EC2Communication communication = new EC2Communication(
							i1.getPublicIp(), clusterConfig.getSshPrivate());
					communication.connect();
					FileInputStream fstream = new FileInputStream(script);
					// Get the object of DataInputStream
					DataInputStream in = new DataInputStream(fstream);
					BufferedReader br = new BufferedReader(
							new InputStreamReader(in));
					String strLine;
					// Read File Line By Line
					while ((strLine = br.readLine()) != null) {
						
						logWriter.append(df.format(new Date())  
								+ ": execute cmd " + strLine
								+ System.getProperty("line.separator"));
						logWriter.flush();
						
						communication.executeCmd(strLine);
					}
					communication.disconnect();
				}
			}

			/**
			 ************************************************ 
			 * database write, finalize job
			 ************************************************ 
			 */
			ClusterDao dao = new ClusterDao();
			if (prog.getMapredPort() != 80)
				namenode = namenode + ":" + prog.getMapredPort();
			clusterConfig.setWebAddress(namenode);
			clusterConfig.setState(ClusterConfiguration.UP);
			dao.insertCluster(clusterConfig);
			ClusterQueue.getInstance().delete(clusterConfig);

			logWriter.append(df.format(new Date()) + ": Cluster ready after!"
					+ System.getProperty("line.separator"));
			logWriter.close();

		} catch (Exception e) {
			logWriter.append(df.format(new Date()) + " - " + e.getMessage()
					+ System.getProperty("line.separator"));
			logWriter.close();

			destroyCluster();
			e.printStackTrace();
		}

	}

	private void destroyCluster() throws IOException {
		PropertiesConfiguration whirrConfig = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FileWriter logWriter = new FileWriter(clusterConfig.getLog(), true);
		ClusterDao dao = new ClusterDao();

		try {
			clusterConfig.setState(ClusterConfiguration.DESTROYING);
			whirrConfig = clusterConfig.createShutDown();

			/** destroy a cluster */

			logWriter.append(df.format(new Date()) + ": destroying cluster"
					+ System.getProperty("line.separator"));
			ClusterSpec spec = new ClusterSpec(whirrConfig);
			ClusterController cont = new ClusterController();
			cont.destroyCluster(spec);
			clusterConfig.setWebAddress("");
			clusterConfig.setSshPrivate("");
			long millis =System.currentTimeMillis()- clusterConfig.getStartTime();
			clusterConfig.setStartTime(millis);
			clusterConfig.setState(ClusterConfiguration.DOWN);
			ClusterQueue.getInstance().delete(clusterConfig);
			File file = new File(clusterConfig.getSshPrivate());
			file.delete();
			if (clusterConfig.getPk() == 0) {
				dao.insertCluster(clusterConfig);
			} else {
				dao.updateCluster(clusterConfig);
			}

			logWriter.append(df.format(new Date()) + ": cluster down!"
					+ System.getProperty("line.separator"));

		} catch (Exception e) {// TODO Auto-generated catch blocks
			clusterConfig.setState(ClusterConfiguration.UP);
			ClusterQueue.getInstance().delete(clusterConfig);
			dao.updateCluster(clusterConfig);
			logWriter.append(df.format(new Date()) + " - " + e.getMessage()
					+ System.getProperty("line.separator"));
			e.printStackTrace();
		}
		// finalize log file
		logWriter.close();
	}

}
