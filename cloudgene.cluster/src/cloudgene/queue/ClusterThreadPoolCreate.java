package cloudgene.queue;
/**
 * @author seppinho
 *
 */
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClusterThreadPoolCreate {
	
	int poolSize = 4;

	int maxPoolSize = 10;

	long keepAliveTime = 20;

	static ClusterThreadPoolCreate instance = null;

	ThreadPoolExecutor threadPool = null;

	final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(
			20);

	public static ClusterThreadPoolCreate getInstance() {
		if (instance == null) {
			instance = new ClusterThreadPoolCreate();
		}
		return instance;
	}

	public ClusterThreadPoolCreate() {
		threadPool = new ThreadPoolExecutor(poolSize, maxPoolSize,
				keepAliveTime, TimeUnit.SECONDS, queue);

	}

	public void runTask(Runnable task) {
		threadPool.execute(task);
		System.out.println("Run task count.." + queue.size());

	}

	public void shutDown() {
		threadPool.shutdown();
	}

}
