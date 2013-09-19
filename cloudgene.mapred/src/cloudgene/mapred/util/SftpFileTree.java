package cloudgene.mapred.util;

import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import net.sf.json.JSONArray;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class SftpFileTree {
	
	public static FileItem[] getSftpFileTree(String path) {
		String SFTPHOST = "localhost";
		int    SFTPPORT = 49165;
		String SFTPUSER = "root";
		String SFTPPASS = "Deg";
		//String SFTPWORKINGDIR = "/root";
				
		Session 	session 	= null;
		Channel 	channel 	= null;
		ChannelSftp channelSftp = null;
		try{
			JSch jsch = new JSch();
			session = jsch.getSession(SFTPUSER,SFTPHOST,SFTPPORT);
			session.setPassword(SFTPPASS);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp)channel;
			channelSftp.cd(path);
			Vector<ChannelSftp.LsEntry> filelist = channelSftp.ls(path);
			FileItem[] results = null;
			results = new FileItem[filelist.size()];
			int count = 0;
			
			for(ChannelSftp.LsEntry entry : filelist) {
			 if(entry.getAttrs().isDir()){
				 results[count] = new FileItem();
				 results[count].setText(entry.getFilename());
				 results[count].setPath(path + "/" + entry.getFilename());
				 results[count].setCls("folder");
				 count++;
			 }
			}
			
			 for(ChannelSftp.LsEntry entry : filelist) {
				 if(!entry.getAttrs().isDir()){
					results[count] = new FileItem();
					results[count].setText(entry.getFilename());
					results[count].setPath(path + "/" + entry.getFilename());
					results[count].setId(path + "/" + entry.getFilename());
					results[count].setLeaf(true);
					results[count].setCls("file");
					results[count].setSize(FileUtils
							.byteCountToDisplaySize(entry.getAttrs().getSize()));
					count++;
				 }
			}
			return results;
			
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		
		
	}

}
