package server.resources;

/**
 * @author seppinho
 *
 */
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import com.esotericsoftware.yamlbeans.YamlException;
import user.JSONAnswer;
import user.User;
import user.UserSessions;
import util.Utils;
import util.Settings;
import util.YamlLoader;
import core.programs.ClusterYaml;
import core.programs.CloudgeneYaml;
import core.programs.Programs;
import database.UserDao;

public class ValidateLogin extends ServerResource {

	@Post
	public Representation validateLogin(Representation entity) {
		Form form = new Form(entity);
		String username = form.getFirstValue("loginUsername");
		String password = form.getFirstValue("loginPassword");
		password = Utils.getMD5(password);
		UserDao dao = new UserDao();
		String pwd = dao.getDatabasePwd(username);
		if (pwd.equals(password)) {
			User user = dao.findByUsername(username, password);
			if (user != null) {
				UserSessions sessions = UserSessions.getInstance();
				String token = sessions.loginUser(user);
				CookieSetting cookie = new CookieSetting(
						UserSessions.COOKIE_NAME, token);
				getResponse().getCookieSettings().add(cookie);
				return new JSONAnswer("Login successful.", true);
			} 
			else {
				return new JSONAnswer("Login Failed! Wrong Username or Password.",
						false);
			}
		} else {
			return new JSONAnswer("Login Failed! Wrong Username or Password.",
					false);
		}
	}
}
