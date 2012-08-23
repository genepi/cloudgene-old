package server.resources;

/**
 * @author seppinho
 *
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.jets3t.service.S3ServiceException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.representation.StringRepresentation;
import queue.ClusterQueue;
import queue.ClusterThreadPoolCreate;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

import user.User;
import user.UserSessions;
import util.ConnectionUtil;
import util.Utils;
import core.ClusterConfiguration;
import core.ClusterTask;
import core.programs.CloudgeneYaml;
import core.programs.Programs;
import database.UserDao;

public class CreateCluster extends ServerResource {


	@Post
	public Representation acceptRepresentation(Representation entity) {
		Representation representation = null;
		try {
			JsonRepresentation represent = new JsonRepresentation(entity);

			UserSessions sessions = UserSessions.getInstance();
			User user = sessions.getUserByRequest(getRequest());

			JSONObject obj = represent.getJsonObject();
			if (user != null) {
				ClusterConfiguration clusterConfig = new ClusterConfiguration();
				String usr = obj.get("loginUsername").toString();
				String pwd = obj.get("loginPassword").toString();
				String program = obj.get("program").toString();
				String ssh = obj.get("key").toString();
				String name = obj.get("name").toString();
				String provider = obj.get("cluster").toString();
				String amount = obj.get("amount").toString();
				String bucket = obj.get("bucketName").toString();
				String type = obj.get("type").toString();
				String dirLog = "logs";

				/** check credentials */
				ConnectionUtil.getInputStream(usr, pwd);
				UserDao dao = new UserDao();
				if (!obj.has("saveCre")) {
					user.setCloudKey("");
					user.setCloudSecure("");
					dao.updateCredential(user);
				}
				else{
					user.setCloudKey(usr);
					user.setCloudSecure(pwd);
					dao.updateCredential(user);
				}
				user.setCloudKey(usr);
				user.setCloudSecure(pwd);
				
				/** save ssh data */
				if (ssh.equals("1")) {
					createKey(clusterConfig, user);
				}
				else if (ssh.equals("3")) {
					clusterConfig.setSshPrivate(user.getSshKey());
					clusterConfig.setSshPublic(user.getSshPub());
				}
				if (obj.has("saveSsh")) {
					dao.updateSSH(user);
				}
				if (obj.has("s3Export")) {
					clusterConfig.setS3Bucket(bucket);
				}

				/**
				 * CONFIGURE CLUSTER
				 */
				CloudgeneYaml prog = Programs.getProgramByName(program);
				clusterConfig.setProgram(prog.getCluster());
				Utils.checkDirAvailable(dirLog);
				clusterConfig.setLog(dirLog + File.separatorChar
						+ System.currentTimeMillis() + ".txt");
				clusterConfig.setCloudID(String.valueOf(System
						.currentTimeMillis()));
				clusterConfig.setName(name);
				clusterConfig.getProgram().setProvider(provider);
				clusterConfig.setCloudUsername(usr);
				clusterConfig.setCloudPassword(pwd);
				clusterConfig.setAmount(Integer.valueOf(amount));
				clusterConfig.setCloudgeneUser(user);
				clusterConfig.setSSHAvailable(true);
				clusterConfig.setInstanceType(type);
				clusterConfig.setStartTime(System.currentTimeMillis());
				clusterConfig
						.setActionType(ClusterConfiguration.CREATE_CLUSTER);
				clusterConfig.setState(ClusterConfiguration.QUEUE);

				/**
				 * add to queue
				 */
				ClusterQueue.getInstance().submit(clusterConfig);

				/**
				 * add to threadpool
				 */
				
				ClusterTask task = new ClusterTask(clusterConfig);
				ClusterThreadPoolCreate.getInstance().runTask(task);
				getResponse().setStatus(Status.SUCCESS_OK);

			} else {
				representation = new StringRepresentation("No user");
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				getResponse().setEntity(representation);
				return representation;
			}
		} catch (S3ServiceException e) {
			representation = new StringRepresentation(
					"Please check your security credentials");
			getResponse().setEntity(representation);
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			e.printStackTrace();
			return representation;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			representation = new StringRepresentation("Error occured");
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			getResponse().setEntity(representation);
			e.printStackTrace();
			return representation;
			
		}
		return representation;

	}

	private void createKey(ClusterConfiguration clusterConfig, User user)
			throws JSchException, FileNotFoundException, IOException {
		String dir = "sshKey";
		Utils.checkDirAvailable(dir);
		JSch jsch = new JSch();
		String name= "cloudgene_"+System.currentTimeMillis()+"";
		KeyPair key;
		key = KeyPair.genKeyPair(jsch, com.jcraft.jsch.KeyPair.RSA);
		String publicKeyLoc = dir + File.separator + name
				+ ".pub";
		String privateKeyLoc = dir + File.separatorChar
				+ name + ".key";
		key.writePrivateKey(privateKeyLoc);
		key.writePublicKey(publicKeyLoc, "");
		// set for launch cluster
		clusterConfig.setSshPublic(publicKeyLoc);
		clusterConfig.setSshPrivate(privateKeyLoc);
		user.setSshKey(privateKeyLoc);
		user.setSshPub(publicKeyLoc);
		createZip(publicKeyLoc, privateKeyLoc);
		key.dispose();
	}

	/** create zip file of ssh keys */
	public void createZip(String publicKey, String privateKey) {
		// These are the files to include in the ZIP file
		String[] source = new String[] { publicKey, privateKey };

		// Create a buffer for reading the files
		byte[] buf = new byte[1024];

		try {
			// Create the ZIP file
			String target = privateKey + ".zip";
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					target));

			// Compress the files
			for (int i = 0; i < source.length; i++) {
				FileInputStream in = new FileInputStream(source[i]);
				// Add ZIP entry to output stream.
				out.putNextEntry(new ZipEntry(source[i]));
				// Transfer bytes from the file to the ZIP file
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				// Complete the entry
				out.closeEntry();
				in.close();
			}

			// Complete the ZIP file
			out.close();
		} catch (IOException e) {
		}
	}
}
