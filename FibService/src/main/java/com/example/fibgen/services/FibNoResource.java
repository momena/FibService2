package com.example.fibgen.services;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.fibgen.businesslogic.FibonacciNumberGenerator;
import com.example.fibgen.businesslogic.FibonacciNumberGeneratorImpl;
import com.example.fibgen.contants.FibNumberConstants;
import com.example.fibgen.exceptions.FibGenOutOfBoundsException;

@Path(FibNumberConstants.FIBNUMBER_RESOURCE)
public class FibNoResource {

	private static Logger log = LoggerFactory.getLogger(FibNoResource.class);
    /**
     * This method takes a number as parameter of rest call and if the input parameter is valid,
     * it returns a json object with that many concatenated Fibonacci number with a space between them.
     *  The response also contains the input number and a status, of SUCCESS or FAILURE.
     *  It also returns http status of Response.Status.OK for successful call and 
     *  Response.Status.BAD_REQUEST in case of error with proper error message.
     * @param number the input number, this many Fibanocci numbers should be calculated
     * @return a json object described above
     */
	@GET
	@Path(FibNumberConstants.NUMBER)
	@Produces(MediaType.APPLICATION_JSON)
	public Response generateNumber(
			@PathParam(FibNumberConstants.NUMBER_PARAM) String number) {
		if (number != null) {
			try {
				number = URLDecoder.decode(number, "UTF-8").trim();
			} catch (UnsupportedEncodingException e) {
				// TODO: add msg catalog instead of hard-coded msg
				log.warn("Unsupported Encoding", e.getLocalizedMessage());
			}
		}

		// If number is null, the container would catch it. If not, then our
		// validator below would handle it. So no worries!
		String result = null;
		try {
			try {
				FibonacciNumberGenerator fng = new FibonacciNumberGeneratorImpl();
				result = formatJsonResponse(fng.FibNoGenerator(number), number,
						true);
			} catch (FibGenOutOfBoundsException e) {
				// throw new WebApplicationException(e,
				// Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build());
				result = formatJsonResponse(e.getLocalizedMessage(), number,
						false);
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(result).build();
			}
		} catch (JSONException e) {
			throw new WebApplicationException(
					Response.Status.INTERNAL_SERVER_ERROR);
		}
		return Response.ok(result, MediaType.APPLICATION_JSON).build();
	}
    
    /**
     * This method format the json response
     * @param responseString this is the output of the service
     * @param number this is the input to the service
     * @param success indicates whether the service call was successful or not
     * @return a STring representation of the json object generated by the three input parameters
     * @throws JSONException in case of error in putting strings to json object
     */
    private String formatJsonResponse(String responseString, String number, boolean success) throws JSONException {
    	String status;
    	if (success) {
    		status = "SUCCESS";
    	} else {
    		status = "FAILURE";
    	}
    	JSONObject obj = new JSONObject();
    	obj.put("Input", number.toString());
    	obj.put("Output", responseString);
    	obj.put("Status", status);
    	return obj.toString();
    }

}

