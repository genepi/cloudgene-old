package cloudgene.core.programs;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import cloudgene.util.YamlLoader;

import com.esotericsoftware.yamlbeans.YamlReader;

public class Repository {

	private String url;

	private AppList appList;

	private List<Program> progs;

	public Repository(String url) {
		this.url = url;
	}

	public boolean load() throws IOException {

		progs = new Vector<Program>();

		YamlReader reader = null;
		try {
			URL url2 = new URL(url + "/apps.yaml");
			HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
			reader = new YamlReader(
					new InputStreamReader(conn.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		appList = reader.read(AppList.class);

		for (String pckg : appList.getApps()) {
			System.out.println(pckg);
			Program prog = YamlLoader.loadAppFromUrl(url + "/" + pckg
					+ "/cloudgene.yaml");
			// update url
			prog.setSource(url + "/" + pckg);
			progs.add(prog);
		}

		return true;

	}

	public List<Program> getApps() {
		return progs;
	}

	/*public static void main(String[] args) throws IOException {
		Repository repo = new Repository("http://cloudgene.uibk.ac.at/apps");
		repo.load();
		for (AppMetaData app : repo.getApps()) {
			System.out.println(app.getName());
		}

	}*/

}
