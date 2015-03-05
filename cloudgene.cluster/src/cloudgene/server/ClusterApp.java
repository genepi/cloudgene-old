package cloudgene.server;
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

import cloudgene.server.resources.AddUser;
import cloudgene.server.resources.ChangePwd;
import cloudgene.server.resources.CheckKey;
import cloudgene.server.resources.CloudCredentials;
import cloudgene.server.resources.CreateCluster;
import cloudgene.server.resources.DestroyCluster;
import cloudgene.server.resources.GetAppsFromRepository;
import cloudgene.server.resources.GetClusters;
import cloudgene.server.resources.GetKeys;
import cloudgene.server.resources.GetLog;
import cloudgene.server.resources.GetMyBuckets;
import cloudgene.server.resources.GetPrograms;
import cloudgene.server.resources.GetTypes;
import cloudgene.server.resources.GetUserDetails;
import cloudgene.server.resources.InstallApp;
import cloudgene.server.resources.KeyUpload;
import cloudgene.server.resources.LoadPrograms;
import cloudgene.server.resources.LogoutUser;
import cloudgene.server.resources.ValidateLogin;


public class ClusterApp extends Application {
	/**
	 * @author seppinho
	 * Add here all new resources for get / posts requests from the client.
	 Creates a root Restlet that will receive all incoming calls. 
	 */
	/**
	
	 */
	@Override
	public synchronized Restlet createInboundRoot() {

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
		//install apps
		router.attach("/getAppsFromRepo", GetAppsFromRepository.class);
		router.attach("/installApp", InstallApp.class);		
		
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