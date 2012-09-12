package util;

import java.io.FileReader;
import java.io.IOException;

import cloudgene.core.programs.CloudgeneYaml;
import cloudgene.core.programs.ClusterYaml;

import com.esotericsoftware.yamlbeans.YamlReader;


public class YamlLoader {
	
	public static CloudgeneYaml loadAppFromFile(String filename) throws IOException {

		YamlReader reader = new YamlReader(new FileReader(filename));

		reader.getConfig().setPropertyDefaultType(CloudgeneYaml.class, "cluster",
				ClusterYaml.class);
		

		CloudgeneYaml prog = reader.read(CloudgeneYaml.class);
		return prog;

	}
	
	

}
