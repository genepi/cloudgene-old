package cloudgene.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cloudgene.core.programs.Program;
import cloudgene.core.programs.ClusterYaml;

import com.esotericsoftware.yamlbeans.YamlReader;


public class YamlLoader {
	
	public static Program loadAppFromFile(String filename) throws IOException {

		YamlReader reader = new YamlReader(new FileReader(filename));

		reader.getConfig().setPropertyDefaultType(Program.class, "cluster",
				ClusterYaml.class);
		

		Program prog = reader.read(Program.class);
		return prog;

	}
	
	public static Program loadAppFromUrl(String filename) throws IOException {

		URL url2 = new URL(filename);
		HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
		YamlReader reader = new YamlReader(new InputStreamReader(
				conn.getInputStream()));


		Program prog = reader.read(Program.class);
		return prog;

	}
	
	

}
