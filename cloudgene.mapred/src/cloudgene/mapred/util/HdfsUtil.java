package cloudgene.mapred.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.LineReader;

public class HdfsUtil {

	private static final Log log = LogFactory.getLog(HdfsUtil.class);

	public static void compress(String zipFile, String hdfs) {
		// Create a buffer for reading the files
		byte[] buf = new byte[1024];

		try {
			// Create the ZIP file
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					zipFile));

			// Compress the files
			Configuration conf = new Configuration();

			FileSystem fileSystem = FileSystem.get(conf);
			Path pathFolder = new Path(hdfs);
			FileStatus[] files = fileSystem.listStatus(pathFolder);

			if (files != null) {
				for (FileStatus file : files) {
					Path path = file.getPath();
					if (!file.isDir()
							&& !file.getPath().getName().startsWith("_")) {
						FSDataInputStream in = fileSystem.open(path);
						out.putNextEntry(new ZipEntry(path.getName()));

						// Transfer bytes from the file to the ZIP file
						int len;
						while ((len = in.read(buf)) > 0) {
							out.write(buf, 0, len);
						}

						// Complete the entry
						out.closeEntry();

						in.close();
					}
				}

				// Complete the ZIP file
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void compressAndMerge(String zipFile, String hdfs,
			boolean removeHeader) {

		try {
			// Create the ZIP file
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					zipFile));

			// Compress the files
			Configuration conf = new Configuration();

			FileSystem fileSystem = FileSystem.get(conf);
			Path pathFolder = new Path(hdfs);
			FileStatus[] files = fileSystem.listStatus(pathFolder);

			// Add ZIP entry to output stream.
			out.putNextEntry(new ZipEntry(pathFolder.getName() + ".txt"));

			Text line = new Text();

			if (files != null) {

				boolean firstFile = true;

				for (FileStatus file : files) {
					Path path = file.getPath();
					if (!file.isDir()
							&& !file.getPath().getName().startsWith("_")) {
						FSDataInputStream in = fileSystem.open(path);

						LineReader reader = new LineReader(in);

						boolean header = true;
						while (reader.readLine(line, 1000) > 0) {

							if (removeHeader) {

								if (header) {
									if (firstFile) {
										out.write(line.toString().getBytes());
										firstFile = false;
									}
									header = false;
								} else {
									out.write('\n');
									out.write(line.toString().getBytes());
								}

							} else {

								if (header) {
									if (firstFile) {
										firstFile = false;
									} else {
										out.write('\n');
									}
									header = false;
								} else {
									out.write('\n');

								}
								out.write(line.toString().getBytes());
							}
						}
						line.clear();

						in.close();

					}
				}
				out.closeEntry();

				// Complete the ZIP file
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void exportDirectoryAndMerge(String folder, String name,
			String hdfs, boolean removeHeader) {

		try {
			FileOutputStream out = null;

			Configuration conf = new Configuration();

			FileSystem fileSystem = FileSystem.get(conf);
			Path pathFolder = new Path(hdfs);
			FileStatus[] files = fileSystem.listStatus(pathFolder);

			out = new FileOutputStream(FileUtil.path(folder, name + ".txt"));

			Text line = new Text();

			if (files != null) {
				boolean firstFile = true;
				for (FileStatus file : files) {
					Path path = file.getPath();
					if (!file.isDir()
							&& !file.getPath().getName().startsWith("_")) {
						FSDataInputStream in = fileSystem.open(path);

						LineReader reader = new LineReader(in);

						boolean header = true;
						while (reader.readLine(line, 1000) > 0) {

							if (removeHeader) {

								if (header) {
									if (firstFile) {
										out.write(line.toString().getBytes());
										firstFile = false;
									}
									header = false;
								} else {
									out.write('\n');
									out.write(line.toString().getBytes());
								}

							} else {

								if (header) {
									if (firstFile) {
										firstFile = false;
									} else {
										out.write('\n');
									}
									header = false;
								} else {
									out.write('\n');

								}
								out.write(line.toString().getBytes());
							}
						}
						line.clear();

						in.close();
					}
				}

				out.close();

				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void exportDirectory(String folder, String name, String hdfs) {

		byte[] buf = new byte[1024];

		try {

			Configuration conf = new Configuration();

			FileSystem fileSystem = FileSystem.get(conf);
			Path pathFolder = new Path(hdfs);
			FileStatus[] files = fileSystem.listStatus(pathFolder);

			if (files != null) {
				for (FileStatus file : files) {
					Path path = file.getPath();
					if (!file.isDir()
							&& !file.getPath().getName().startsWith("_")) {
						FSDataInputStream in = fileSystem.open(path);
						FileOutputStream out = new FileOutputStream(
								FileUtil.path(folder, path.getName()));

						int len;
						while ((len = in.read(buf)) > 0) {
							out.write(buf, 0, len);
						}

						out.close();

						in.close();
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void exportFile(String folder, String hdfs) {

		byte[] buf = new byte[1024];

		try {

			Configuration conf = new Configuration();

			FileSystem fileSystem = FileSystem.get(conf);

			Path path = new Path(hdfs);

			FSDataInputStream in = fileSystem.open(path);
			FileOutputStream out = new FileOutputStream(FileUtil.path(folder,
					path.getName()));

			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			out.close();

			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void compressFile(String folder, String hdfs) {

		byte[] buf = new byte[1024];

		try {

			Configuration conf = new Configuration();

			FileSystem fileSystem = FileSystem.get(conf);

			Path path = new Path(hdfs);

			FSDataInputStream in = fileSystem.open(path);

			// Create the ZIP file
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					FileUtil.path(folder, path.getName() + ".zip")));

			out.putNextEntry(new ZipEntry(path.getName()));

			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			// Complete the entry
			out.closeEntry();

			out.close();

			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean deleteDirectory(FileSystem fileSystem,
			String directory) {
		Path path = new Path(directory);
		try {
			if (fileSystem.exists(path)) {
				fileSystem.delete(path);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static boolean deleteDirectory(String directory) {
		Configuration conf = new Configuration();
		FileSystem fileSystem;
		try {
			fileSystem = FileSystem.get(conf);
			return deleteDirectory(fileSystem, directory);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String path(String... paths) {
		String result = "";
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			if (!path.isEmpty()) {
				if (i > 0 && !path.startsWith("/") && !result.endsWith("/")) {
					result += "/" + path;
				} else {
					result += path;
				}
			}
		}
		return result;
	}

	public static String makeAbsolute(String path) {

		Configuration conf = new Configuration();
		FileSystem fileSystem;

		try {
			fileSystem = FileSystem.get(conf);
			String temp = fileSystem.getHomeDirectory().toString() + "/" + path;
			temp = temp.replaceFirst("//([a-zA-Z\\-.\\d]*)(:(\\d*))?/", "///");
			return temp;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
