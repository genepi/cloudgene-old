package cloudgene.mapred;

import org.restlet.Component;
import org.restlet.data.LocalReference;
import org.restlet.data.Protocol;
import org.restlet.routing.VirtualHost;

public class WebServer extends Component {

	public WebServer(LocalReference webRoot, int port) throws Exception {

		// ------------------
		// Add the connectors
		// ------------------
		getServers().add(Protocol.HTTP, port);
		getClients().add(Protocol.FILE);
		getClients().add(Protocol.CLAP);
		VirtualHost host = new VirtualHost(getContext());
		host.attach(new WebApp(webRoot));
		getHosts().add(host);

	}
}
