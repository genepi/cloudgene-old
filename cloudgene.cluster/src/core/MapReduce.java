package core;

/**
 * @author seppinho
 *
 */
import java.io.FileNotFoundException;

import util.EC2Communication;
import util.Settings;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;


public class MapReduce {

	public void install(EC2Communication communication)
			throws FileNotFoundException, JSchException, SftpException {
		String logFile = ">log.log < /dev/null 2>&1";

		String address = Settings.getInstance().getMapRed();
		String[] file = address.split("/");

		communication.executeCmd("wget " + address + logFile);
		communication.executeCmd("unzip " + file[file.length - 1]);
	}

	public void startWebInterface(EC2Communication communication,
			int clusterPK, String username, String pwd, int port,
			String s3Bucket) throws FileNotFoundException, JSchException,
			SftpException {

		String cmd = "sudo hadoop jar " + "cloudgene-mapred.jar" + " -port "
				+ port + " -add-user " + username + " " + pwd + " -md5";

		if (!s3Bucket.equals(""))
			cmd += " -bucket " + s3Bucket;
		cmd += " > log.log 2>&1 < /dev/null &";

		communication.executeCmd(cmd);
		communication.executeCmd("sleep 10");
		communication.disconnect();
	}

}
