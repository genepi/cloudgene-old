package queue;

/**
 * @author seppinho
 *
 */
import java.util.List;
import java.util.Vector;

import cloudgene.core.ClusterConfiguration;
import cloudgene.core.ClusterDetails;
import cloudgene.database.ClusterDao;


import user.User;




public class ClusterQueue {

	static ClusterQueue instance = null;

	private Vector<ClusterConfiguration> queue;

	public static ClusterQueue getInstance() {

		if (instance == null) {

			instance = new ClusterQueue();
		}

		return instance;

	}

	public ClusterQueue() {
		queue = new Vector<ClusterConfiguration>();
	}

	public void submit(ClusterConfiguration cluster) {
		queue.add(cluster);

		System.out
				.println("Added cluster '" + cluster.getName() + "' to queue");
	}

	public void delete(ClusterConfiguration cluster) {
		queue.remove(cluster);

		System.out.println("Removed cluster '" + cluster.getName()
				+ "' from queue");
	}

	public List<ClusterDetails> getJobsByUser(User user) {
		List<ClusterDetails> result = new Vector<ClusterDetails>();
		System.out.println(user.getId());
		// get working jobs from queue
		for (ClusterConfiguration running : queue) {
			System.out.println("ID is "+running.getCloudgeneUser().getId());
			if (running.getCloudgeneUser().getId() == user.getId()) {
				ClusterDetails info = new ClusterDetails(running);
				result.add(info);
			}
		}
		// get finalized jobs from db
		ClusterDao dao = new ClusterDao();
		System.out.println(user.getId());
		List<ClusterConfiguration> dbObjects = dao.findAllClustersDone(user
				.getId());
		for (ClusterConfiguration finished : dbObjects) {
			System.out.println("FIN "+finished.getName());
			ClusterDetails info = new ClusterDetails(finished);
			result.add(info);
		}
		return result;
	}

}
