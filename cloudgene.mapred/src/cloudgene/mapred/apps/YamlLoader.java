package cloudgene.mapred.apps;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import cloudgene.mapred.util.FileUtil;
import cloudgene.mapred.util.Settings;

import com.esotericsoftware.yamlbeans.YamlReader;

public class YamlLoader {

	public static App loadAppFromUrl(String filename) throws IOException {

		URL url2 = new URL(filename);
		HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
		YamlReader reader = new YamlReader(new InputStreamReader(
				conn.getInputStream()));

		reader.getConfig().setPropertyDefaultType(App.class, "mapred",
				MapReduceConfig.class);
		reader.getConfig().setPropertyElementType(MapReduceConfig.class,
				"steps", Step.class);
		reader.getConfig().setPropertyElementType(MapReduceConfig.class,
				"inputs", InputParameter.class);
		reader.getConfig().setPropertyElementType(MapReduceConfig.class,
				"outputs", OutputParameter.class);

		App app = reader.read(App.class);

		updateApp(filename, app);

		return app;

	}

	public static App loadAppFromFile(String filename) throws IOException {

		YamlReader reader = new YamlReader(new FileReader(filename));

		reader.getConfig().setPropertyDefaultType(App.class, "mapred",
				MapReduceConfig.class);
		reader.getConfig().setPropertyElementType(MapReduceConfig.class,
				"steps", Step.class);
		reader.getConfig().setPropertyElementType(MapReduceConfig.class,
				"inputs", InputParameter.class);
		reader.getConfig().setPropertyElementType(MapReduceConfig.class,
				"outputs", OutputParameter.class);

		App app = reader.read(App.class);

		updateApp(filename, app);

		return app;

	}

	private static void updateApp(String filename, App app) {

		MapReduceConfig config = app.getMapred();

		if (config != null) {
			String jar = config.getJar();
			String mapper = config.getMapper();
			String reducer = config.getReducer();
			String path = new File(filename).getParentFile().getAbsolutePath();
			config.setPath(path);

			// default step
			if (jar != null) {
				Step step = new Step();
				step.setJar(jar);
				step.setParams(config.getParams());
				config.getSteps().add(step);
			}

			if (mapper != null && reducer != null) {
				Step step = new Step();
				step.setMapper(mapper);
				step.setReducer(reducer);
				step.setParams(config.getParams());
				config.getSteps().add(step);
			}

		}

	}

	public static App loadApp(String name) {

		Settings settings = Settings.getInstance();

		File dir = new File(FileUtil.path(settings.getAppsPath(), name));

		File manifest = null;

		if (dir.isDirectory()) {
			//old style
			manifest = new File(FileUtil.path(dir.getAbsolutePath(),
					"cloudgene.yaml"));
		} else {
			//new style
			manifest = new File(FileUtil.path(settings.getAppsPath(), name)+".yaml");
		}

		if (manifest.exists()) {
			try {
				return loadAppFromFile(manifest.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {

			return null;
		}

	}

	public static List<Category> loadApps() {

		Settings settings = Settings.getInstance();

		File folder = new File(settings.getAppsPath());
		File[] listOfFiles = folder.listFiles();

		String filename = "";

		List<Category> result = new Vector<Category>();

		Map<String, List<AppMetaData>> categories = new HashMap<String, List<AppMetaData>>();

		for (int i = 0; i < listOfFiles.length; i++) {

			File dir = listOfFiles[i];
			filename = FileUtil.path(dir.getAbsolutePath(), "cloudgene.yaml");
			File manifest = new File(filename);

			// old style

			if (dir.isDirectory() && manifest.exists()) {
				filename = manifest.getAbsolutePath();
				try {

					App app = loadAppFromFile(filename);

					AppMetaData meta = (AppMetaData) app;

					if (meta != null && app.getMapred() != null) {

						meta.setId(dir.getName());

						List<AppMetaData> listApps = categories.get(meta
								.getCategory());
						if (listApps == null) {
							listApps = new Vector<AppMetaData>();
							categories.put(meta.getCategory(), listApps);
						}
						listApps.add(meta);

					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			// new style: all other yaml files.
			if (dir.isDirectory()) {
				File[] filesInDir = dir.listFiles();
				for (File file : filesInDir) {
					if (file.getName().endsWith(".yaml") && !file.getName().equals("cloudgene.yaml")) {
						try {
							filename = file.getAbsolutePath();
							App app = loadAppFromFile(filename);

							AppMetaData meta = (AppMetaData) app;

							if (meta != null && app.getMapred() != null) {

								meta.setId(dir.getName() + "/" + (file.getName()).replace(".yaml", ""));

								List<AppMetaData> listApps = categories
										.get(meta.getCategory());
								if (listApps == null) {
									listApps = new Vector<AppMetaData>();
									categories
											.put(meta.getCategory(), listApps);
								}
								listApps.add(meta);

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		for (String text : categories.keySet()) {
			Category tool = new Category();
			tool.setText(text);
			tool.setLeaf(false);

			List<AppMetaData> listApps = categories.get(text);
			AppMetaData[] children = new AppMetaData[listApps.size()];
			for (int i = 0; i < listApps.size(); i++) {
				children[i] = listApps.get(i);
			}
			tool.setChildren(children);
			result.add(tool);
			
		}

		Collections.sort(result);
		
		return result;

	}

}
