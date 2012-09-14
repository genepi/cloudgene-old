package cloudgene.queue;
/**
 * @author seppinho
 *
 */
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClusterThreadPoolDelete {
	
	int poolSize = 2;

	int maxPoolSize = 2;

	long keepAliveTime = 10;

	static ClusterThreadPoolDelete instance = null;

	ThreadPoolExecutor threadPool = null;

	final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(
			5);

	public static ClusterThreadPoolDelete getInstance() {
		if (instance == null) {
			instance = new ClusterThreadPoolDelete();
		}
		return instance;
	}

	public ClusterThreadPoolDelete() {
		threadPool = new ThreadPoolExecutor(poolSize, maxPoolSize,
				keepAliveTime, TimeUnit.SECONDS, queue);

	}

	public void runTask(Runnable task) {
		threadPool.execute(task);
		System.out.println("Delete task count.." + queue.size());

	}

	public void shutDown() {
		threadPool.shutdown();
	}

}
