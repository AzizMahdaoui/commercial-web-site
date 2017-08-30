package com.drims.OAuth2.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.RandomStringGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.drims.OAuth2.ClientService;
import com.drims.OAuth2.RequestService;
import com.drims.OAuth2.json.serializable.models.AuthorizationQueryModel;
import com.drims.OAuth2.json.serializable.models.AuthorizeServletResponseContentModel;
import com.drims.OAuth2.json.serializable.models.Client;
import com.google.gson.Gson;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

@WebServlet(
        name = "AuthorizeServlet",
        urlPatterns = {"/authorize"}
)
public class AuthorizeServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(AuthorizeServlet.class);
	
	private static final String AuthorizationEndpointAllowedContentType = "application/json";
	
	private static final Gson gson = new Gson();
	private static final ClientService clientService = new ClientService();
	private static final RequestService requestService = new RequestService(); 
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setContentType("application/json");
			
			String client_id = req.getParameter("client_id");
			PrintWriter writer = resp.getWriter();
			if(client_id == null) {
				writer.write("{\"error\":\"client_id parameter is not provided\"}");
				logger.warn("Authorization request attempt without client_id parameter");
				writer.flush();
				writer.close();
				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
				return;
			} else if(client_id.isEmpty()) {
				writer.write("{\"error\":\"client_id parameter is empty\"}");
				logger.warn("Authorization request attempt without client_id parameter");
				writer.flush();
				writer.close();
				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
				return;
			}
			
			//Client validation
			Client client = clientService.getClient(client_id);
			if(client == null) {
				writer.write("{\"error\":\"not allowed client_id\"}");
				logger.warn("Authorization request attempt with an unknonw client_id parameter");
				writer.flush();
				writer.close();
				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
				return;
			}
			
			RandomStringGenerator rg = new RandomStringGenerator.Builder().withinRange('0', 'Z').filteredBy(LETTERS,DIGITS).build();
			AuthorizeServletResponseContentModel model = new AuthorizeServletResponseContentModel();
			model.rid = rg.generate(8);
			logger.info(String.format("generated request id : \"%s\" for client_id : \"%s\"",model.rid,client_id));
			
			//Save request in database
			AuthorizationQueryModel authorizationQueryModel = new AuthorizationQueryModel();
			authorizationQueryModel.client_id = client_id;
			requestService.SaveRequest(authorizationQueryModel, model.rid);
			logger.info(String.format("save query at request id : \"%s\"", model.rid));
			writer.write(gson.toJson(model));
			
//			String jsonString = req.getParameter("body");
//			if(jsonString == null) {
//				writer.write("Error not provided body parameter");
//				writer.flush();
//				writer.close();
//				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
//				return;
//			}
//			
//			AuthorizationQueryModel authorizationRequestModel = 
//					gson.fromJson(jsonString, AuthorizationQueryModel.class);
//			logger.info(authorizationRequestModel.client_id);
//			logger.info(authorizationRequestModel.client_secret);
//			logger.info(authorizationRequestModel.redirect_uri);
//			
//			Client client = clientService.getClient(authorizationRequestModel.client_id);
//			
//			if(client == null)
//			{
//				logger.info("Unknown client_id providen : " + authorizationRequestModel.client_id);
//				writer.write("Unknown client");
//				writer.flush();
//				writer.close();
//				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
//				return;
//			}else if(!client.get_redirectUris().contains(authorizationRequestModel.client_secret))
//			{
//				logger.info(String.format("Mismatched client secret, expected %s got %s", authorizationRequestModel.client_id, authorizationRequestModel.client_secret));
//				writer.write("Invalid client");
//				writer.flush();
//				writer.close();
//			}
//			else
//			{
//				String[] requestScopes;
//				if(authorizationRequestModel.scope == null) { requestScopes = null;}
//				else if(authorizationRequestModel.scope.isEmpty()) {requestScopes = null; }
//				else { requestScopes = authorizationRequestModel.scope.split(" "); }
//				
//				String[] serverClientScopes;
//				if(client.get_scope() == null) { serverClientScopes = null;}
//				else if(client.get_scope().isEmpty()) {serverClientScopes = null; }
//				else { serverClientScopes = client.get_scope().split(" "); }
//				
//				HashSet<String> requestScopesHashSet = new HashSet<String>(Arrays.asList(requestScopes));
//				HashSet<String> serverClientScopesHashSet = new HashSet<String>(Arrays.asList(serverClientScopes));
//				
//				ArrayList<String> differenceArrayList = new ArrayList<>();
//				for(String currentRequestScope : requestScopes)
//				{
//					if(!serverClientScopesHashSet.contains(currentRequestScope))
//					{
//						differenceArrayList.add(currentRequestScope);
//					}
//				}
//				for(String currentClientScope : serverClientScopes)
//				{
//					if(!requestScopesHashSet.contains(currentClientScope))
//					{
//						differenceArrayList.add(currentClientScope);
//					}
//				}
//				if(!differenceArrayList.isEmpty())
//				{
//					String redirectionErrorUrl = authorizationRequestModel.redirect_uri;
//					if(!authorizationRequestModel.redirect_uri.contains("?"))
//					{
//						redirectionErrorUrl = redirectionErrorUrl + "?";
//					}
//					resp.sendRedirect(redirectionErrorUrl + "error=invalid_scope");
//				}
//				
//				RandomStringGenerator rg = new RandomStringGenerator.Builder().withinRange('0', 'Z').filteredBy(LETTERS,DIGITS).build();
//				String reqid = rg.generate(8);
//				
//				//Save request by requestId
//				RequestService requestService = new RequestService();
//				requestService.SaveRequest(authorizationRequestModel, reqid);
//				
//				resp.setContentType("application/html");
//				//Approval page content page => to deport in jsp
//				writer.write("<html>");
//				writer.write("<form>");
//				writer.write("<div>");
//				writer.write("<div>Client id : " + authorizationRequestModel.client_id + "</div>");
//				writer.write("<div>Request id: "+ reqid +"</div>");
//				writer.write("<div>scope : " + authorizationRequestModel.scope + "</div>");
//				writer.write("<button label=\"approve\">");
//				writer.write("<button label=\"decline\">");
//				writer.write("</div>");
//				writer.write("</form>");
//				writer.write("</html>");
//				writer.flush();
//				writer.close();
//				return;
//			}
		}catch(Exception ex)
		{
			logger.error(ex);
		}
		
	}
}
