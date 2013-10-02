package cloudgene.mapred.util;

import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.io.FileUtils;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class SftpFileTree {
	private static final Log log = LogFactory.getLog(SftpFileTree.class);

	public static FileItem[] getSftpFileTree(String path, String SFTPHOST,
			String SFTPUSER, String SFTPPASS, int SFTPPORT) throws JSchException, SftpException {

		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;

		JSch jsch = new JSch();
		try {
			session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		session.setPassword(SFTPPASS);
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		try {
			session.connect();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			
			throw e;
			
		}
		try {
			channel = session.openChannel("sftp");
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		try {
			channel.connect();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		channelSftp = (ChannelSftp) channel;
		log.info("PATH IS " + path);
		if (path.equals("~/")) {
			try {
				path = channelSftp.pwd();
			} catch (SftpException e) {
				// TODO Auto-generated catch block
				throw e;
			}
		}
		log.info("PATH IS  after if " + path);

		try {
			channelSftp.cd(path);
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		Vector<ChannelSftp.LsEntry> filelist = null;
		try {
			filelist = channelSftp.ls(path);
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		// log.info("pwd is  " + channelSftp.pwd());
		FileItem[] results = null;
		// -2 to take away folder ".." and "."
		results = new FileItem[filelist.size() - 2];
		int count = 0;
		for (ChannelSftp.LsEntry entry : filelist) {
			if (entry.getAttrs().isDir()
					&& !((entry.getFilename().equals(".") || (entry
							.getFilename().equals(".."))))) {
				results[count] = new FileItem();
				results[count].setText(entry.getFilename());
				results[count].setLeaf(false);
				results[count].setCls("folder");
				results[count].setId(path + "/" + entry.getFilename());
				results[count].setPath(path + "/" + entry.getFilename());
				count++;
			}
		}
		for (ChannelSftp.LsEntry entry : filelist) {

			if (entry.getAttrs().isLink()) {
				String link = null;
				boolean linkIsdir = false;
				try {
					link = channelSftp.readlink(entry.getFilename());
				} catch (SftpException e) {
					throw e;
				}
				try {
					linkIsdir = ((ChannelSftp.LsEntry) channelSftp.ls(link)
							.get(0)).getAttrs().isDir();
				} catch (com.jcraft.jsch.SftpException ex) {
					if (ex.getMessage().equals("No such file")) {
						results[count] = new FileItem();
						results[count].setText(entry.getFilename()
								+ " BROKEN LINK");
						results[count].setLeaf(true);
						results[count].setCls("file");
						results[count].setDisabled(true);
						count++;
						continue;
					} else {
						ex.printStackTrace();
					}
				}
				if (linkIsdir) {
					results[count] = new FileItem();
					results[count].setText(entry.getFilename());
					results[count].setLeaf(false);
					results[count].setCls("folder");
					results[count].setId(path + "/" + entry.getFilename());
					results[count].setPath(path + "/" + entry.getFilename());
					count++;
				} else {
					results[count] = new FileItem();
					results[count].setText(entry.getFilename());
					results[count].setPath(link);
					results[count].setId(link);
					results[count].setLeaf(true);
					results[count].setCls("file");
					results[count]
							.setSize(FileUtils.byteCountToDisplaySize(entry
									.getAttrs().getSize()));
					count++;
				}

			} 		
				 else if (!entry.getAttrs().isDir() && !((entry.getFilename().equals(".") || (entry
					.getFilename().equals(".."))))) {
				results[count] = new FileItem();
				results[count].setText(entry.getFilename());
				results[count].setPath(path + "/" + entry.getFilename());
				results[count].setId(path + "/" + entry.getFilename());
				results[count].setLeaf(true);
				results[count].setCls("file");
				results[count].setSize(FileUtils.byteCountToDisplaySize(entry
						.getAttrs().getSize()));
				count++;
			}

		}

		return results;

	}

}
