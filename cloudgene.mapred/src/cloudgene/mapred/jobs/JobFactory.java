package cloudgene.mapred.jobs;

public class JobFactory {

	public static Job create(int type) {

		switch (type) {
		case Job.TASK:

			return new TaskJob();

		case Job.MAPREDUCE:

			return new MapReduceJob();

		default:
			return null;
		}

	}

}
