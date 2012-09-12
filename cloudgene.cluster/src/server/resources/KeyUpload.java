package server.resources;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.representation.StringRepresentation;

import cloudgene.core.ClusterConfiguration;
import cloudgene.core.ClusterTask;
import cloudgene.core.programs.CloudgeneYaml;
import cloudgene.core.programs.Programs;
import cloudgene.database.UserDao;
import queue.ClusterQueue;
import queue.ClusterThreadPoolCreate;
import user.JSONAnswer;
import user.User;
import user.UserSessions;


public class KeyUpload extends ServerResource {

	@Post
	public Representation uploadKey(Representation entity)

	{
		StringRepresentation representation = null;
		try {
			UserSessions sessions = UserSessions.getInstance();
			User user = sessions.getUserByRequest(getRequest());

			if (entity != null && user != null) {
				CloudgeneYaml prog = null;
				File file = null;
				ClusterConfiguration clusterConfig = new ClusterConfiguration();
				String sshDir = "sshKey";
				String userLogs = "logs";
				String provider = "";
				String bucket = "";
				boolean saveCredential = false;
				boolean saveSSH = false;
				boolean s3Export = false;

				util.Utils.checkDirAvailable(userLogs);
				util.Utils.checkDirAvailable(sshDir);
				String name = System.currentTimeMillis() + "";
				String privateKeyLoc = sshDir + File.separatorChar + name
						+ ".key";
				String publicKeyLoc = sshDir + File.separator + name + ".pub";

				if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),
						true)) {
					List<FileItem> items = parseRequest();

					for (final Iterator<FileItem> it = items.iterator(); it
							.hasNext();) {
						FileItem fileItem = it.next();
						System.out.println(fileItem.getFieldName());
						if (fileItem.getFieldName().equals("loginUsername")) {
							clusterConfig
									.setCloudUsername(fileItem.getString());
						} else if (fileItem.getFieldName().equals(
								"loginPassword")) {
							clusterConfig
									.setCloudPassword(fileItem.getString());
						} else if (fileItem.getFieldName().equals("cluster")) {
							provider = fileItem.getString();
						} else if (fileItem.getFieldName().equals("name")) {
							clusterConfig.setName(fileItem.getString());
						} else if (fileItem.getFieldName().equals("amount")) {
							clusterConfig.setAmount(Integer.valueOf(fileItem
									.getString()));
						} else if (fileItem.getFieldName().equals("program")) {
							System.out.println("program");
							prog = Programs.getProgramByName(fileItem
									.getString());
						} else if (fileItem.getFieldName().equals("bucketName")) {
							System.out
									.println("yyoyoo " + fileItem.getString());
							bucket = fileItem.getString();
						} else if (fileItem.getFieldName().equals("type")) {
							clusterConfig.setInstanceType(fileItem.getString());
						} else if (fileItem.getFieldName().equals("saveCre")) {
							saveCredential = true;
						} else if (fileItem.getFieldName().equals("saveSsh")) {
							System.out.println("save ssh");
							saveSSH = true;
						} else if (fileItem.getFieldName().equals("s3Export")) {
							System.out.println("s3Export");
							s3Export = true;
						}

						// upload public key
						else if (fileItem.getFieldName().equals("public")) {
							if (!fileItem.getName().endsWith(".pub")) {
								return new JSONAnswer(
										"Public key should end with .pub!",
										false);
							}

							file = new File(publicKeyLoc);
							// Check for file size bigger than 250 kb
							if (fileItem.getSize() > 1024 * 250
									|| fileItem.getSize() <= 0) {
								try {
									// Read and discard all data
									entity.exhaust();
									return new JSONAnswer(
											"Please add valid SSH keys!", false);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							fileItem.write(file);
						}

						else if (fileItem.getFieldName().equals("private")) {

							if (!fileItem.getName().endsWith(".key")) {
								return new JSONAnswer(
										"Private key should end with .key!",
										false);
							}

							file = new File(privateKeyLoc);
							// Check for file size bigger than 250 kb
							if (fileItem.getSize() > 1024 * 250
									|| fileItem.getSize() <= 0) {
								try {
									// Read and discard all data
									entity.exhaust();
									return new JSONAnswer(
											"Please add valid SSH keys!", false);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							fileItem.write(file);
						}

					}

					// set cluster information
					clusterConfig.setSSHAvailable(false);
					clusterConfig.setSshPublic(publicKeyLoc);
					clusterConfig.setSshPrivate(privateKeyLoc);
					clusterConfig.setStartTime(System.currentTimeMillis());
					clusterConfig.setCloudgeneUser(user);
					clusterConfig.setLog(userLogs + File.separatorChar
							+ System.currentTimeMillis() + ".txt");
					clusterConfig.setCloudID(String.valueOf(System
							.currentTimeMillis()));
					// set Program for later
					clusterConfig.setProgram(prog.getCluster());
					clusterConfig.getProgram().setProvider(provider);
					clusterConfig
							.setActionType(ClusterConfiguration.CREATE_CLUSTER);
					clusterConfig.setState(ClusterConfiguration.QUEUE);

					UserDao dao = new UserDao();

					if (saveCredential) {
						user.setCloudKey(clusterConfig.getCloudUsername());
						user.setCloudSecure(clusterConfig.getCloudPassword());
						dao.updateCredential(user);
					} else {
						user.setCloudKey("");
						user.setCloudSecure("");
						dao.updateCredential(user);
					}
					user.setCloudKey(clusterConfig.getCloudUsername());
					user.setCloudSecure(clusterConfig.getCloudPassword());
					if (saveSSH) {
						user.setSshKey(clusterConfig.getSshPrivate());
						user.setSshPub(clusterConfig.getSshPublic());
						dao.updateSSH(user);
					}
					if (s3Export) {
						clusterConfig.setS3Bucket(bucket);
					}

					// add to queue
					ClusterQueue.getInstance().submit(clusterConfig);

					// add to threadpool
					ClusterTask task = new ClusterTask(clusterConfig);
					ClusterThreadPoolCreate.getInstance().runTask(task);
					return new JSONAnswer("Cluster started!", true);

				}
			}

			else {
				representation = new StringRepresentation("No user");
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				getResponse().setEntity(representation);
				return representation;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return representation;

	}

	private List<FileItem> parseRequest() {
		List<FileItem> items = null;
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1000240);
		RestletFileUpload upload = new RestletFileUpload(factory);
		try {
			items = upload.parseRequest(getRequest());
		} catch (FileUploadException e2) {
			e2.printStackTrace();
		}
		return items;
	}

}
