package cloudgene.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.fuin.utils4j.Utils4J;

public class ZipUtil {

	public static boolean extract(String filename, String folder) {

		try {

			ZipInputStream zipinputstream = new ZipInputStream(
					new FileInputStream(filename));

			byte[] buf = new byte[1024];
			ZipEntry zipentry = zipinputstream.getNextEntry();

			while (zipentry != null) {
				// for each entry to be extracted
				String entryName = zipentry.getName();

				if (!zipentry.isDirectory()) {

					String target = FileUtil.path(folder, entryName);

					File file = new File(target);
					File parent = file.getParentFile();
					if (!parent.exists()) {
						parent.mkdirs();
					}

					FileOutputStream out = new FileOutputStream(file);

					int n;
					while ((n = zipinputstream.read(buf, 0, 1024)) > -1)
						out.write(buf, 0, n);
					out.close();

					zipinputstream.closeEntry();
				}

				zipentry = zipinputstream.getNextEntry();

			}// while

			zipinputstream.close();
			System.out.println("done extracting");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public static boolean zip() {
		final int BUFFER = 2048;
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(Settings.getInstance().getAppsPath()+"/"+"here.zip");
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					dest));
			// out.setMethod(ZipOutputStream.DEFLATED);
			byte data[] = new byte[BUFFER];
			// get a list of files from current directory
			File f = new File(Settings.getInstance().getAppsPath()+"/"+"cloudburst");
			System.out.println(f.getAbsolutePath());
			String files[] = f.list();

			for (int i = 0; i < files.length; i++) {
				System.out.println("Adding: " + files[i]);
				FileInputStream fi = new FileInputStream(files[i]);
				origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(files[i]);
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public static void main(String[] args) {
		//zip();
		try {
			Utils4J.zipDir(new File(Settings.getInstance().getAppsPath()+"/"+"crossbow"), "", new File(Settings.getInstance().getAppsPath()+"/"+"crossbow.zip"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub

	}
}
