package cloudgene.mapred.representations;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.representation.*;
import org.restlet.service.StatusService;

public class CustomStatusService extends StatusService {

	@Override
	public Representation getRepresentation(Status status, Request request,
			Response response) {

		if (status.isClientError()) {
			return new ErrorPageRepresentation("Oje!!");
		} else {

			// TODO Auto-generated method stub
			return super.getRepresentation(status, request, response);
		}
	}

}
