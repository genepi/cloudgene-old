package cloudgene.mapred.jobs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import cloudgene.mapred.apps.InputParameter;
import cloudgene.mapred.apps.Parameter;
import cloudgene.mapred.tasks.ITask;
import cloudgene.mapred.util.FileUtil;
import cloudgene.mapred.util.Settings;

public class TaskJob extends Job {

	private ITask task;

	private List<ITask> tasks;

	public TaskJob() {

	}

	public TaskJob(ITask task) {

		this.task = task;
		task.setJob(this);

		tasks = new Vector<ITask>();
		tasks.add(task);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
		setId(task.getName() + "-" + sdf.format(new Date()));

		List<Parameter> inputParams = new Vector<Parameter>();

		for (int i = 0; i < task.getParameters().length; i++) {

			String param = task.getParameters()[i];
			String value = task.getValues()[i];

			InputParameter parameter = new InputParameter();
			parameter.setId("input_" + i);
			parameter.setDescription(param);
			parameter.setValue(value);

			inputParams.add(parameter);

		}

		setInputParams(inputParams);
	}

	@Override
	public boolean before() {

		return true;

	}

	@Override
	public boolean execute() {

		String localWorkspace = Settings.getInstance().getLocalWorkspace(
				getUser().getUsername());

		String localOutputDirectory = FileUtil.path(localWorkspace, "output",
				getId());

		FileUtil.createDirectory(localOutputDirectory);

		setCurrentStep("Importing...");

		return task.execute();

	}

	@Override
	public boolean after() {

		return true;

	}

	@Override
	public int getMap() {
		if (task != null) {
			return task.getProgress();
		} else {
			return -1;
		}
	}

	@Override
	public int getReduce() {
		return -1;
	}

	public void setTask(ITask task) {
		this.task = task;
	}

	public ITask getTask() {
		return task;
	}

	@Override
	public int getType() {
		return Job.TASK;
	}

}
