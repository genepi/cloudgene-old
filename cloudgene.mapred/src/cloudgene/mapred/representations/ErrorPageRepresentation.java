package cloudgene.mapred.representations;

import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;

public class ErrorPageRepresentation extends StringRepresentation {

	public ErrorPageRepresentation(String error) {

		super("<h1>Error</h1>" + error, MediaType.TEXT_HTML);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<html>");
		stringBuilder
				.append("<head><title>Cloudgene - MapReduce Interface</title>");
		stringBuilder
				.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/css/login.css\" />");
		stringBuilder.append("</head>");
		stringBuilder.append("<body>");

		stringBuilder
				.append("<div class=\"logo\"><img src=\"/images/cloudgene.png\"/>");
		stringBuilder.append("<p>MapReduce Interface</p>");
		stringBuilder.append("</div>");
		stringBuilder.append("<div style=\"clear: both; \"></div>");
		stringBuilder.append("<div style=\"margin: 50px;\" class=\"error\">" + error + "</div>");

		stringBuilder.append("</body>");
		stringBuilder.append("</html>");
		setText(stringBuilder.toString());

	}

}
