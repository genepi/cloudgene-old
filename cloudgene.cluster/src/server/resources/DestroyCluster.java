package server.resources;

/**
 * @author seppinho
 *
 */

import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import cloudgene.core.ClusterConfiguration;
import cloudgene.core.ClusterTask;
import cloudgene.database.ClusterDao;
import queue.ClusterQueue;
import queue.ClusterThreadPoolDelete;
import user.User;
import user.UserSessions;
import util.ConnectionUtil;

public class DestroyCluster extends ServerResource {

	@Post
	public StringRepresentation destroy (Representation entity) {
		StringRepresentation representation = null;

		JsonRepresentation represent;
		try {
			represent = new JsonRepresentation(entity);
			UserSessions sessions = UserSessions.getInstance();
			User user = sessions.getUserByRequest(getRequest());
			JSONObject obj = represent.getJsonObject();
			ClusterDao dao = new ClusterDao();

			if (user != null) {
				int clusterID = Integer.valueOf(obj.get("0") + "");
				String pwd = obj.get("1") + "";
				if(pwd.equals(""))
					pwd=user.getCloudSecure();

				ClusterConfiguration clusterConfig = dao.findSpecificCluster(
						user.getId(), clusterID);
				if (clusterConfig != null) {
					ConnectionUtil.getInputStream(
							clusterConfig.getCloudUsername(), pwd);

					if (clusterConfig.getState() == ClusterConfiguration.UP) {
						clusterConfig.setCloudPassword(pwd);
						clusterConfig
								.setActionType(ClusterConfiguration.DESTROY_CLUSTER);
						clusterConfig.setCloudgeneUser(user);
						clusterConfig.setState(ClusterConfiguration.QUEUE);

						// that it doesn't appear in queue / db at the same time
						dao.updateCluster(clusterConfig.getPk(), 1);
						// add to queue
						ClusterQueue.getInstance().submit(clusterConfig);

						// add to threadpool
						ClusterTask task = new ClusterTask(clusterConfig);
						ClusterThreadPoolDelete.getInstance().runTask(task);
					}

					getResponse().setStatus(Status.SUCCESS_OK);
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			getResponse().setEntity(representation);
			return representation;
		}
		return representation;

	}

}
