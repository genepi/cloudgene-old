package cloudgene.mapred.steps;

import java.util.List;
import java.util.Vector;

import cloudgene.mapred.apps.Step;
import cloudgene.mapred.jobs.CloudgeneContext;
import cloudgene.mapred.util.FileUtil;
import cloudgene.mapred.util.Settings;

public class PigHadoop extends Hadoop {

	@Override
	public boolean run(CloudgeneContext context) {

		String pigPath = Settings.getInstance().getPigPath();
		String pig = FileUtil.path(pigPath, "bin", "pig");

		Step step = context.getStep();

		// params
		String paramsString = step.getParams();
		String[] params = context.resolveParams(paramsString.split(" "));

		// pig script
		List<String> command = new Vector<String>();

		command.add(pig);
		command.add("-f");
		command.add(step.getPig());

		// params
		String[] tiles1 = step.getParams().split(" ");
		for (String tile : tiles1) {
			command.add(tile.trim());
		}

		try {
			return executeCommand(command, context);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

}
