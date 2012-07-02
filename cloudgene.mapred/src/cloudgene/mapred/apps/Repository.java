package cloudgene.mapred.apps;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import com.esotericsoftware.yamlbeans.YamlReader;

public class Repository {

	private String url;

	private AppList appList;

	private List<App> apps;

	public Repository(String url) {
		this.url = url;
	}

	public boolean load() throws IOException {

		apps = new Vector<App>();

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
			App app = YamlLoader.loadAppFromUrl(url + "/" + pckg
					+ "/cloudgene.yaml");
			// update url
			app.setSource(url + "/" + pckg);
			apps.add(app);
		}

		return true;

	}

	public List<App> getApps() {
		return apps;
	}

	public static void main(String[] args) throws IOException {
		Repository repo = new Repository("http://cloudgene.uibk.ac.at/apps");
		repo.load();
		for (AppMetaData app : repo.getApps()) {
			System.out.println(app.getName());
		}

	}

}
