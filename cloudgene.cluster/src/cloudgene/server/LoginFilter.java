package cloudgene.server;

/**
 * @author seppinho
 *
 */
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.routing.Filter;

import cloudgene.user.User;
import cloudgene.user.UserSessions;


class LoginFilter extends Filter {

	CookieSetting forwardCookie;

	private String loginPage;

	private String[] protectedRequests;

	public LoginFilter(String loginPage, String[] protectedRequests) {
		this.loginPage = loginPage;
		this.protectedRequests = protectedRequests;
	}

	@Override
	protected int beforeHandle(Request request, Response response) {
		String path = request.getResourceRef().getPath();

		if (forwardCookie == null) {
			forwardCookie = new CookieSetting();
		}
		if (path.toLowerCase().equals(loginPage)) {
			String token = request.getCookies().getFirstValue(
					UserSessions.COOKIE_NAME);
			if (token != null) {
				UserSessions sessions = UserSessions.getInstance();
				User user = sessions.getUserByToken(token);
				if (user != null) {
					response.redirectTemporary("/cloudgene.html");
					return STOP;
				}
			}

		}

		if (isProtected(path)) {

			if (path.toLowerCase().equals("/destroy.html")) {
				Form form = request.getResourceRef().getQueryAsForm();
				String tmp = "?clusterID=" + form.getFirstValue("clusterID");
				path += tmp;
				setForwardCookie(response, path);
			}

			String token = request.getCookies().getFirstValue(
					UserSessions.COOKIE_NAME);

			if (token == null) {
				response.redirectTemporary(loginPage);
				return STOP;
			} else {
				UserSessions sessions = UserSessions.getInstance();
				User user = sessions.getUserByToken(token);
				if (user == null) {
					response.redirectTemporary(loginPage);
					return STOP;
				}
			}

		}

		return CONTINUE;
	}

	private void setForwardCookie(Response response, String path) {
		forwardCookie.setName("forwardFrom");
		forwardCookie.setMaxAge(20);
		forwardCookie.setValue(path);
		response.getCookieSettings().add(forwardCookie);
	}

	private boolean isProtected(String path) {
		for (String protectedRequest : protectedRequests) {
			if (path.toLowerCase().equals(protectedRequest)) {
				return true;
			}
		}
		return false;
	}
}
