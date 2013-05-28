package cloudgene.mapred.jobs;

import java.io.File;

public abstract class CloudgeneStep {

	public String getFolder(Class clazz) {
		return new File(clazz.getProtectionDomain().getCodeSource()
				.getLocation().getPath()).getParent();
	}

	abstract public boolean run(CloudgeneContext context);

}
