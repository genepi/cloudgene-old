package cloudgene.server.resources;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import cloudgene.user.User;
import cloudgene.user.UserSessions;
import cloudgene.util.FileUtil;
import cloudgene.util.Settings;
import cloudgene.util.ZipUtil;


public class InstallApp extends ServerResource {

	@Post
	public Representation post(Representation entity) {

		StringRepresentation representation = null;

		try {
			JsonRepresentation represent = new JsonRepresentation(entity);

			UserSessions sessions = UserSessions.getInstance();
			User user = sessions.getUserByRequest(getRequest());
			JSONObject obj = represent.getJsonObject();

			if (user != null) {

				String url = obj.get("package-url").toString();
				System.out.println(url);
				String[] tiles = url.split("/");
				String name = tiles[tiles.length - 1];
				String localFile = FileUtil.getTempFilename(url);
				downloadPackage(url + "/app.zip", localFile);
				installPackage(localFile, name);

				representation = new StringRepresentation(
						"Application import finished! Create a cluster now.");
				getResponse().setStatus(Status.SUCCESS_OK);
				getResponse().setEntity(representation);
				return representation;

			} else {
				representation = new StringRepresentation("No user");
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				getResponse().setEntity(representation);
				return representation;
			}
		} catch (JSONException e) {
			representation = new StringRepresentation(e.getMessage());
			getResponse().setEntity(representation);
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			e.printStackTrace();
			return representation;
		} catch (IOException e) {
			representation = new StringRepresentation(e.getMessage());
			getResponse().setEntity(representation);
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			e.printStackTrace();
			return representation;
		}

	}

	/**
	 * Mandatory. Specifies that this resource supports POST requests.
	 */
	public boolean allowPost() {
		return true;
	}
	
	
	public static boolean downloadPackage(String weburl, String localFile)
			throws IOException {

		URL url = new URL(weburl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		BufferedInputStream in = new BufferedInputStream(conn.getInputStream());

		FileOutputStream out = new FileOutputStream(localFile);

		IOUtils.copy(in, out);

		IOUtils.closeQuietly(in);
		IOUtils.closeQuietly(out);

		return true;
	}


	public static boolean installPackage(String localFile, String name) {

		String tools = Settings.getInstance().getAppsPath();
		ZipUtil.extract(localFile, tools);

		return true;
	}

}
