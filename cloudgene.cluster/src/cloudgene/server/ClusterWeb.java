package cloudgene.server;
/**
 * @author seppinho
 *
 */
import java.io.File;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.routing.VirtualHost;

public class ClusterWeb extends Component {

	public ClusterWeb(int httpPort, int httpsPort) throws Exception {
		String password = "importkey";
		File keystoreFile = new File("https/cloudgene.key");
		// ------------------
		// Add the connectors
		// ------------------
		getServers().add(Protocol.HTTP, httpPort);
		if (keystoreFile.exists()) {
			org.restlet.Server https = getServers().add(Protocol.HTTPS, httpsPort);
			https.getContext().getParameters()
					.add("keystorePath", keystoreFile.getAbsolutePath());
			https.getContext().getParameters()
					.add("keystorePassword", password);
			https.getContext().getParameters().add("keyPassword", password);
			https.getContext().getParameters().add("headerBufferSize", "50000"); // #28573
			https.getContext().getParameters()
					.add("requestBufferSize", "50000"); // #28573*/
		}
		else{System.out.println("NO HTTPS available");}
		getClients().add(Protocol.FILE);
		getClients().add(Protocol.CLAP);
		VirtualHost host = new VirtualHost(getContext());
		host.attach(new ClusterApp());
		getHosts().add(host);

	}

}
