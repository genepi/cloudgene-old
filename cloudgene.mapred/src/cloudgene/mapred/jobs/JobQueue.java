package cloudgene.mapred.jobs;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cloudgene.mapred.core.User;
import cloudgene.mapred.database.JobDao;
import cloudgene.mapred.util.HadoopUtil;

public class JobQueue implements Runnable {

	static JobQueue instance = null;

	private Vector<Job> queue;

	private JobDao dao;

	private Thread thread;

	private static final Log log = LogFactory.getLog(JobQueue.class);

	public static JobQueue getInstance() {
		if (instance == null) {
			instance = new JobQueue();
		}
		return instance;
	}

	private JobQueue() {
		queue = new Vector<Job>();
		dao = new JobDao();

	}

	public void submit(Job job) {

		queue.add(job);
		log.info("Submit job...");

	}

	public void cancel(Job job) {

		if (job.getState() == Job.RUNNING
				|| job.getState() == Job.EXPORTING_DATA) {

			log.info("Cancel Job ...");

			job.cancle();

			thread.stop();

			if (job instanceof MapReduceJob) {

				try {

					MapReduceJob mrJob = ((MapReduceJob) job);

					log.info(" Cancel Job " + mrJob.getHadoopJobId());

					HadoopUtil.getInstance().kill(mrJob.getHadoopJobId());

				} catch (IOException e) {

					log.error(" Cancel Job failed: ", e);

				}
			}

		}

		if (job.getState() == Job.WAITING) {

			log.info("Cancel Job...");

			job.cancle();

			queue.remove(job);

		}

	}

	@Override
	public void run() {
		while (true) {

			while (queue.size() > 0) {
				Job nextJob = queue.get(0);

				log.info("Job " + nextJob.getId() + ": queueing.");

				thread = new Thread(nextJob);
				thread.start();

				try {

					thread.join();

					log.info("Job " + nextJob.getId() + ": finished");

				} catch (InterruptedException e) {

					log.info("Job " + nextJob.getId() + " was canceld by user.");

				}

				if (thread.isInterrupted()) {
					log.info("Job " + nextJob.getId() + " was canceld by user.");
				}

				// add to database
				dao.insert(nextJob);

				queue.remove(nextJob);
			}

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public List<Job> getJobsByUser(User user) {

		List<Job> result = new Vector<Job>();

		for (Job job : queue) {

			if (job.getUser().getId() == user.getId()) {
				result.add(job);
			}

		}

		return result;
	}

	public Job getJobById(String id) {

		for (Job job : queue) {

			if (job.getId().equals(id)) {
				return job;
			}

		}

		return null;
	}

}
