package cloudgene.core;

/**
 * @author seppinho
 *
 */
import java.io.FileNotFoundException;

import cloudgene.util.EC2Communication;
import cloudgene.util.Settings;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class MapReduce {

	public void install(EC2Communication communication)
			throws FileNotFoundException, JSchException, SftpException {
		String logFile = ">log.log < /dev/null 2>&1";

		String address = Settings.getInstance().getMapRed();
		String[] file = address.split("/");

		communication.executeCmd("wget " + address + logFile);
		communication.executeCmd("unzip " + file[file.length - 1]+" > log.log 2>&1 < /dev/null &");
		communication.executeCmd("sleep 5");
	}

	public void startWebInterface(EC2Communication communication,
			int clusterPK, String username, String pwd, int port,
			String s3Bucket) throws FileNotFoundException, JSchException,
			SftpException {

		String cmdCreateUser = "sudo hadoop jar " + "cloudgene-mapred.jar"
				+ " -add-user " + username + " " + pwd + " -md5";

		String cmdExecute = "sudo hadoop jar " + "cloudgene-mapred.jar"
				+ " -port " + port;

		if (!s3Bucket.equals(""))
			cmdExecute += " -bucket " + s3Bucket;
		
		cmdExecute += " > log.log 2>&1 < /dev/null &";

		communication.executeCmd(cmdCreateUser);		
		communication.executeCmd(cmdExecute);
		
		communication.executeCmd("sleep 10");
		communication.disconnect();
	}

}
