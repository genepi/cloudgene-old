package cloudgene.server.resources;

/**
 * @author seppinho
 *
 */
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.representation.StringRepresentation;

import cloudgene.core.programs.Program;
import cloudgene.core.programs.ClusterYaml;
import cloudgene.core.programs.Programs;
import cloudgene.user.User;
import cloudgene.user.UserSessions;
import cloudgene.util.Settings;
import cloudgene.util.YamlLoader;

import com.esotericsoftware.yamlbeans.YamlException;


public class LoadPrograms extends ServerResource {

	@Get
	public Representation loadPrograms() {

		FileFilter yamlFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.getName().toLowerCase().endsWith(".yaml");
			}
		};

		Settings settings = null;
		settings = Settings.getInstance();
		File root = new File(settings.getAppsPath());
		System.out.println(root.getAbsolutePath());

		File[] files = root.listFiles();
		if (files == null) {
			// Either dir does not exist or is not a directory
		} else {
			for (int i = 0; i < files.length; i++) {
				// Get filename of file or directory
				File[] yamlFile;
				if (files[i].isDirectory()) {
					yamlFile = files[i].listFiles(yamlFilter);

					// only if YAML file is included
					if (yamlFile.length >= 1) {
						Program progYaml;
						try {
							progYaml = YamlLoader.loadAppFromFile(yamlFile[0]
									.getPath());
							// set program path
							ClusterYaml cluster = progYaml.getCluster();
							if (progYaml.getCluster() != null) {
								File folder = new File(files[i].getPath());
								cluster.setFolder(folder);
								Programs.getInstance().addProgram(progYaml);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return null;

	}

}
