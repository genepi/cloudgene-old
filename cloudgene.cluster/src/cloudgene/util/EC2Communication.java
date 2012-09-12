package cloudgene.util;

/**
 * @author seppinho
 *
 */
import com.jcraft.jsch.*;

import java.io.*;
import java.util.Properties;

public class EC2Communication {

	private Session session;

	public EC2Communication(String host, String privateSShKey) {
		JSch jsch = new JSch();
		try {
			jsch.addIdentity(privateSShKey);
			this.session = jsch.getSession(Settings.getInstance().getCloudUser(), host, 22);
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void connect() throws JSchException {
		if (!session.isConnected()) {
			System.out.println("Connect");
			Properties config = new Properties();
			config.setProperty("StrictHostKeyChecking", "no");
			session.setConfig(config);
			// session.setDaemonThread(true);
			session.connect();
		}

	}

	public void disconnect() throws JSchException {
		if (session.isConnected()) {
			System.out.println("disconnect...");
			session.disconnect();
		}

	}

	public void copyData(String src, String dest) throws JSchException,
			SftpException, FileNotFoundException {
		ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
		channel.connect();
		System.out.println("copy src ->"+src +" to dest ->" + dest);
		channel.put(new FileInputStream(src), dest);
	}

	public void executeCmd(String cmd) throws JSchException, SftpException,
			FileNotFoundException {

		try {
			System.out.println("CMD IS "+cmd);
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setErrStream(System.err);
			((ChannelExec) channel).setCommand(cmd);
			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				//System.out.println(".");
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					System.out.print(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					System.out.println("exit-status: "
							+ channel.getExitStatus());
					break;
				}
			}
			System.out.println("disconnected");
			channel.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	 public boolean executeCmd1(String command) throws JSchException, SftpException,
		FileNotFoundException {

		  try {
			  System.out.println("CMD IS "+command);
		   // exec 'scp -t rfile' remotely
		  // String command = "mkdir \"" + target + "\"";
		   Channel channel = session.openChannel("exec");
		   ((ChannelExec) channel).setCommand(command);

		   // get I/O streams for remote scp
		   InputStream in = channel.getInputStream();

		   channel.connect();

		   if (checkAck(in) != 0) {
			   System.out.println("false");
		    return false;
		   }

		   channel.disconnect();
		   System.out.println("true");
		   return true;

		  } catch (Exception e) {

		   throw new JSchException(e.getMessage());
		  }

		 }
		private int checkAck(InputStream in) throws Exception {
		  int b = in.read();
		  // b may be 0 for success,
		  // 1 for error,
		  // 2 for fatal error,
		  // -1
		  if (b == 0)
		   return b;
		  if (b == -1)
		   return b;

		  if (b == 1 || b == 2) {
		   StringBuffer sb = new StringBuffer();
		   int c;
		   do {
		    c = in.read();
		    sb.append((char) c);
		   } while (c != '\n');
		   if (b == 1) { // error
		    throw new Exception(sb.toString());
		   }
		   if (b == 2) { // fatal error
		    throw new Exception(sb.toString());
		   }
		  }
		  return b;
		 }

/*	public static void main(String[] args) throws IOException, JSchException,
			SftpException {
		System.out.println(File.separator);
		EC2Communication comm = new EC2Communication(
				"107.20.89.1",
				"sshKey/1311856872372.key");
		comm.connect();
		String name="snpfinder";
		//comm.executeCmd("wget http://cloudgene.uibk.ac.at/downloads/cloudgene-mapred-0.1.1.jar >log.log < /dev/null 2>&1");
		comm.copyData("sshKey/1311856872372.key", "tools/"+name+"/1311856872372.key.key");
		
		//comm.executeCmd("wget --output-document=myrna-1.1.2.zip http://sourceforge.net/projects/bowtie-bio/files/myrna/1.1.2/myrna-1.1.2.zip/download"); 
		//comm.executeCmd("unzip *.zip");
		//comm.executeCmd("wget http://trace.ncbi.nlm.nih.gov/Traces/sra/static/sratoolkit.2.1.2-ubuntu32.tar.gz"); 
		//comm.executeCmd("tar xvfz *.gz");
		//comm.executeCmd("sudo ln -s sratoolkit.2.1.2-ubuntu32/fastq-dump.2.1.2 /usr/bin/fastq-dump");
		
		comm.disconnect();
	}*/

}
