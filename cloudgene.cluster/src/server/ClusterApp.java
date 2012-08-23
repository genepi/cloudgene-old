package server;
/**
 * @author seppinho
 *
 */
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.LocalReference;
import org.restlet.resource.Directory;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.routing.TemplateRoute;

import server.resources.AddUser;
import server.resources.ChangePwd;
import server.resources.CheckKey;
import server.resources.CloudCredentials;
import server.resources.DestroyCluster;
import server.resources.GetMyBuckets;
import server.resources.GetTypes;
import server.resources.GetUserDetails;
import server.resources.KeyUpload;
import server.resources.GetClusters;
import server.resources.GetKeys;
import server.resources.GetLog;
import server.resources.GetPrograms;
import server.resources.LoadPrograms;
import server.resources.LogoutUser;
import server.resources.ValidateLogin;
import server.resources.CreateCluster;

public class ClusterApp extends Application {
	/**
	 * @author seppinho
	 * Add here all new resources for get / posts requests from the client.
	 Creates a root Restlet that will receive all incoming calls. 
	 */
	/**
	
	 */
	@Override
	public synchronized Restlet createRoot() {

		Router router = new Router(getContext());
		String target = "riap://host/index.html";
		Redirector redirector = new Redirector(getContext(), target,
				Redirector.MODE_SERVER_OUTBOUND);
		TemplateRoute route = router.attach("/", redirector);
		route.setMatchingMode(Template.MODE_EQUALS);
		//login resource
		router.attach("/checkLogin", ValidateLogin.class);
		// create a cluster via the wizard
		router.attach("/createCluster", CreateCluster.class);
		//destroy cluster via the wizard
		router.attach("/destroyCluster", DestroyCluster.class);
		//get all clusters from a user
		router.attach("/getClusters", GetClusters.class);
		//log out from cloudgene
		router.attach("/logout", LogoutUser.class);
		//download SSH key
		router.attach("/downloadKey", GetKeys.class);
		//download log
		router.attach("/downloadLog", GetLog.class);
		//upload SSh key
		router.attach("/keyUpload", KeyUpload.class);
		//change pwd
		router.attach("/changePwd", ChangePwd.class);
		//add user
		router.attach("/addUser", AddUser.class);
		//get user details
		router.attach("/getUserDetails", GetUserDetails.class);
		//get buckets
		router.attach("/getMyBuckets", GetMyBuckets.class);
		//get instance types
		router.attach("/getTypes", GetTypes.class);
		//check if key is valid
		router.attach("/checkKey", CheckKey.class);
		//get credentials
		router.attach("/getCloudCredentials", CloudCredentials.class);
		//reload all configurations when wizard starts
		router.attach("/loadPrograms", LoadPrograms.class);
		//getPrograms from User
		router.attach("/getPrograms", GetPrograms.class);
		
		Directory dir = new Directory(getContext(), new LocalReference(
				"clap://thread/web"));
		dir.setListingAllowed(false);

		route = router.attach("/", dir);
		route.setMatchingMode(Template.MODE_STARTS_WITH);

		String[] protectedFiles = { "/cloudgene.html", "/destroy.html" };
		LoginFilter filter = new LoginFilter("/", protectedFiles);
		filter.setNext(router);

		return filter;
	}

}