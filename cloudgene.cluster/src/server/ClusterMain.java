package server;
/**
 * @author seppinho
 */
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class ClusterMain {
	private static Logger logger = Logger.getLogger(ClusterMain.class);
	public static void main(String[] args) throws IOException {
		System.out.println("Cloudgene-Cluster 0.2.0-120424\n");

		// create the command line parser
		CommandLineParser parser = new PosixParser();

		// create the Options
		Options options = new Options();
		Option portOption = new Option(null, "port", true,
				"runs Cloudgene-Cluster on HTTP port <HTTP-PORT>");
		portOption.setRequired(false);
		portOption.setArgName("HTTP-PORT");
		options.addOption(portOption);
		
		Option portOptionSecure = new Option(null, "portSecure", true,
		"runs Cloudgene-Cluster on HTTPS port <HTTPS-PORT>");
		portOptionSecure.setRequired(false);
		portOptionSecure.setArgName("HTTPS-PORT");
		options.addOption(portOptionSecure);
		
		// parse the command line arguments
		CommandLine line = null;
		try {

			line = parser.parse(options, args);

		} catch (Exception e) {

			System.out.println(e.getMessage() + "\n");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("hadoop jar cloudgene-cluster.jar", options);

			System.exit(1);

		}
		try {
			//PropertyConfigurator.configure("log4j.properties");
			int port = Integer.parseInt(line.getOptionValue("port", "8085"));
			int portS = Integer.parseInt(line.getOptionValue("portSecure", "4443"));
			
			logger.info("Entering application Cloudgene-Cluster on "+ " "+port +" "+ portS);
			new ClusterWeb(port,portS).start();
			
		} catch (Exception e) {
			System.err.println("Can't launch cloudgene-cluster.\nAn unexpected "
					+ "exception occured:");
			logger.error(e.getMessage());
			logger.debug(e.getMessage());
			e.printStackTrace(System.err);

		}

	}

}
