package cloudgene.util;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.security.AWSCredentials;

public class ConnectionUtil {

	public static void getInputStream(String username, String pwd)
			throws S3ServiceException {
		AWSCredentials awsCredentials = new AWSCredentials(username, pwd);

		S3Service s3Service = new RestS3Service(awsCredentials);
		s3Service.listAllBuckets();
	}

}
