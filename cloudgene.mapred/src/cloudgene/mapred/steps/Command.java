package cloudgene.mapred.steps;

import java.util.List;
import java.util.Vector;

import cloudgene.mapred.apps.Step;
import cloudgene.mapred.jobs.CloudgeneContext;

public class Command extends Hadoop {

	@Override
	public boolean run(CloudgeneContext context) {

		Step step = context.getStep();

		String[] params = context.resolveParams(step.getExec().split(" "));

		List<String> command = new Vector<String>();
		for (String param : params) {
			command.add(param);
		}

		try {
			return executeCommand(command, context);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

}
