package com.drims.OAuth2.servlets;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

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

import org.apache.commons.io.IOUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.drims.OAuth2.ClientService;
import com.drims.OAuth2.CodeService;
import com.drims.OAuth2.RequestService;
import com.drims.OAuth2.UserService;
import com.drims.OAuth2.json.serializable.models.ApproveQueryModel;
import com.drims.OAuth2.json.serializable.models.ApproveServletQueryContentModel;
import com.drims.OAuth2.json.serializable.models.ApproveServletResponseContentModel;
import com.drims.OAuth2.json.serializable.models.AuthorizationQueryModel;
import com.drims.OAuth2.json.serializable.models.AuthorizeServletResponseContentModel;
import com.drims.OAuth2.json.serializable.models.Client;
import com.drims.OAuth2.json.serializable.models.CodeModel;
import com.drims.OAuth2.json.serializable.models.UserModel;
import com.google.gson.Gson;


@WebServlet(
        name = "ApproveServlet",
        urlPatterns = {"/approve"}
)
public class ApproveServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(ApproveServlet.class);
	
	private static final Gson gson = new Gson();
	private static final RequestService requestService = new RequestService();
//	private static final ClientService clientService = new ClientService();
//	private static final CodeService codeService = new CodeService();
//	private static final UserService userService = new UserService();
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setContentType("application/json");
			String contentType = req.getContentType();
			PrintWriter writer = resp.getWriter();
			if(contentType == null)
			{
				writer.write("{\"error\":\"content-type not provided\"}");
				logger.warn("Approve request attempt without content type specified");
				writer.flush();
				writer.close();
				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
				return;
			}else if(!"application/json".equals(contentType))
			{
				writer.write("{\"error\":\"content-type is not is not application/json\"}");
				logger.warn("Approve request attempt without content type specified");
				writer.flush();
				writer.close();
				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
				return;
			}
			String json = IOUtils.toString(req.getInputStream());
			ApproveServletQueryContentModel contentModel = gson.fromJson(json, ApproveServletQueryContentModel.class);
			if(contentModel == null)
			{
				writer.write("{\"error\":\"server internal error, body is empty or json is not parsable\"}");
				logger.warn("Empty json connection attempt");
				writer.flush();
				writer.close();
				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
				return;
			}else if(contentModel.client_id == null) {
				writer.write("{\"error\":\"client_id parameter is not provided in json\"}");
				logger.warn("Approve request attempt without client_id parameter");
//				writer.flush();
//				writer.close();
				//resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
				return;
			} else if(contentModel.client_id.isEmpty()) {
				writer.write("{\"error\":\"client_id parameter is empty in json\"}");
				logger.warn("Approve request attempt without client_id parameter");
//				writer.flush();
//				writer.close();
				//resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
				return;
			}
			
			
			
			if(contentModel.rid == null) {
				writer.write("{\"error\":\"rid parameter is not provided in json\"}");
				logger.warn("Approve request attempt without rid parameter");
				writer.flush();
				writer.close();
				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
				return;
			} else if(contentModel.rid.isEmpty()) {
				writer.write("{\"error\":\"rid parameter is empty in json\"}");
				logger.warn("Approve request attempt without rid parameter");
				writer.flush();
				writer.close();
				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
				return;
			}
			// Validating provided request id
			AuthorizationQueryModel authorizationQuery = requestService.GetRequestByRequestId(contentModel.rid);
			requestService.DeleteRequestByRequestId(contentModel.rid);
			if(authorizationQuery == null)
			{
				writer.write("{\"error\":\"unknown rid provided\"}");
				logger.warn("Approve request attempt unknown rid parameter");
				writer.flush();
				writer.close();
				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
				return;
			} else if(authorizationQuery.client_id != contentModel.client_id)
			{
				writer.write("{\"error\":\"client_id error for provided rid\"}");
				logger.warn("Approve request attempt with annother client_id connection for provided rid");
				writer.flush();
				writer.close();
				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
				return;
			}
			
			
			RandomStringGenerator rg = new RandomStringGenerator.Builder().withinRange('0', 'Z').filteredBy(LETTERS,DIGITS).build();
			ApproveServletResponseContentModel model = new ApproveServletResponseContentModel();
			model.code = rg.generate(8);
			logger.info(String.format("code : \"%s\" were generated for client_id: \"%s\" ",model.code, contentModel.client_id));
			writer.write(gson.toJson(model));
			
			
//			if(req.getContentType() == null)
//			{
//				writer.write("{error:'The provided query has no content type specified'}");
//				writer.flush();
//				writer.close();
//				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
//				return;
//			}
//			
//			if(!"application/json".equals(req.getContentType()))
//			{
//				writer.write("{error:\"The provided query is not an 'application/json' content type\"}");
//				writer.flush();
//				writer.close();
//				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
//				return;
//			}
//			
//			String json = IOUtils.toString(req.getInputStream());
//			logger.info(String.format("provided json : %s",json));
//			
//			ApproveQueryModel approveQueryModel = gson.fromJson(json, ApproveQueryModel.class);
//			logger.info("client_id approved : " + approveQueryModel.client_id + " with reqId:" +  approveQueryModel.reqId);
//		try {
//			String json = IOUtils.toString(req.getInputStream());
//			ApproveQueryModel approveQuery = gson.fromJson(json, ApproveQueryModel.class);
//			AuthorizationQueryModel authorizationQuery =  requestService.GetRequestByRequestId(approveQuery.reqId);
//			requestService.DeleteRequestByRequestId(approveQuery.reqId);
//			
//			
//			PrintWriter writer = resp.getWriter();
//			if(authorizationQuery == null) {
//				writer.write("No matching authorization request");
//				writer.flush();
//				writer.close();
//				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//				return;
//			}
//			
//			if(approveQuery.approve != null)
//			{
//				if(authorizationQuery.response_type == "code")
//				{
//					// user approved access
//					RandomStringGenerator rg = new RandomStringGenerator.Builder().
//							withinRange('0', 'Z').filteredBy(LETTERS,DIGITS).build();
//					String code = rg.generate(8);
//					
//					UserModel user = userService.getUser(approveQuery.user);
//					
//					String scope = approveQuery.scope;
//					
//					Client client = clientService.getClient(authorizationQuery.client_id);
//					
//					String[] requestScopes;
//					if(scope == null) { requestScopes = null;}
//					else if(scope.isEmpty()) {requestScopes = null; }
//					else { requestScopes = scope.split(" "); }
//					
//					String[] serverClientScopes;
//					if(client.get_scope() == null) { serverClientScopes = null;}
//					else if(client.get_scope().isEmpty()) {serverClientScopes = null; }
//					else { serverClientScopes = client.get_scope().split(" "); }
//					
//					HashSet<String> requestScopesHashSet = new HashSet<String>(Arrays.asList(requestScopes));
//					HashSet<String> serverClientScopesHashSet = new HashSet<String>(Arrays.asList(serverClientScopes));
//					
//					ArrayList<String> differenceArrayList = new ArrayList<>();
//					for(String currentRequestScope : requestScopes)
//					{
//						if(!serverClientScopesHashSet.contains(currentRequestScope))
//						{
//							differenceArrayList.add(currentRequestScope);
//						}
//					}
//					for(String currentClientScope : serverClientScopes)
//					{
//						if(!requestScopesHashSet.contains(currentClientScope))
//						{
//							differenceArrayList.add(currentClientScope);
//						}
//					}
//					if(!differenceArrayList.isEmpty())
//					{
//						String redirectionErrorUrl = authorizationQuery.redirect_uri;
//						if(!authorizationQuery.redirect_uri.contains("?"))
//						{
//							redirectionErrorUrl = redirectionErrorUrl + "?";
//						}
//						resp.sendRedirect(redirectionErrorUrl + "error=invalid_scope");
//					}
//
//					// save the code and request for later
//					CodeModel codeModel = new CodeModel();
//					codeModel.authorizationQuery = authorizationQuery;
//					codeModel.code = code;
//					codeModel.scope = scope;
//					codeModel.user = user;
//					codeService.SaveCode(codeModel);
//					
//					resp.setHeader("code", code);
//					resp.setHeader("state",authorizationQuery.state);
//					resp.sendRedirect(authorizationQuery.redirect_uri);
//					return;
//
//				} else {
//					// user denied access
//					String redirectionErrorUrl = authorizationQuery.redirect_uri;
//					if(!authorizationQuery.redirect_uri.contains("?"))
//					{
//						redirectionErrorUrl = redirectionErrorUrl + "?";
//					}
//					resp.sendRedirect(redirectionErrorUrl + "error=unsupported_response_type");
//					return;
//				}
//			} else {
//				// user denied access
//				String redirectionErrorUrl = authorizationQuery.redirect_uri;
//				if(!authorizationQuery.redirect_uri.contains("?"))
//				{
//					redirectionErrorUrl = redirectionErrorUrl + "?";
//				}
//				resp.sendRedirect(redirectionErrorUrl + "error=access_denied");
//				return;
//			}
		}catch(Exception ex) {
			logger.info(ex);
		}
	}
}
