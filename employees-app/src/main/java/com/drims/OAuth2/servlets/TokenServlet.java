package com.drims.OAuth2.servlets;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import javax.print.attribute.standard.DateTimeAtCompleted;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.drims.OAuth2.AccessTokenContainerService;
import com.drims.OAuth2.ClientService;
import com.drims.OAuth2.CodeService;
import com.drims.OAuth2.RequestService;
import com.drims.OAuth2.json.serializable.models.AccessTokenContainerModel;
import com.drims.OAuth2.json.serializable.models.ApproveServletQueryContentModel;
import com.drims.OAuth2.json.serializable.models.ApproveServletResponseContentModel;
import com.drims.OAuth2.json.serializable.models.BodyTokenModel;
import com.drims.OAuth2.json.serializable.models.Client;
import com.drims.OAuth2.json.serializable.models.ClientCredentialModel;
import com.drims.OAuth2.json.serializable.models.CodeModel;
import com.drims.OAuth2.json.serializable.models.JWTPayloadModel;
import com.drims.OAuth2.json.serializable.models.TokenResponseModel;
import com.drims.OAuth2.json.serializable.models.TokenServletQueryContentModel;
import com.drims.OAuth2.json.serializable.models.TokenServletResponseContentModel;
import com.google.gson.Gson;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;


@WebServlet(
        name = "TokenServlet",
        urlPatterns = {"/token"}
)
public class TokenServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(TokenServlet.class);
	
	private static final Gson gson = new Gson();
	private static final ClientService clientService = new ClientService();
	private static final RequestService requestService = new RequestService();
	private static final CodeService codeService = new CodeService();
	private static final AccessTokenContainerService accessTokenContainerService = new AccessTokenContainerService();
	
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
			TokenServletQueryContentModel contentModel = gson.fromJson(json, TokenServletQueryContentModel.class);
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
				writer.flush();
				writer.close();
				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
				return;
			} else if(contentModel.client_id.isEmpty()) {
				writer.write("{\"error\":\"client_id parameter is empty in json\"}");
				logger.warn("Approve request attempt without client_id parameter");
				writer.flush();
				writer.close();
				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
				return;
			}
			
			
			if(contentModel.code == null) {
				writer.write("{\"error\":\"code parameter is not provided in json\"}");
				logger.warn("Approve request attempt without client_id parameter");
				writer.flush();
				writer.close();
				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
				return;
			} else if(contentModel.code.isEmpty()) {
				writer.write("{\"error\":\"code parameter is empty in json\"}");
				logger.warn("Approve request attempt without client_id parameter");
				writer.flush();
				writer.close();
				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
				return;
			}
			
			RandomStringGenerator rg = new RandomStringGenerator.Builder().withinRange('0', 'Z').filteredBy(LETTERS,DIGITS).build();
			TokenServletResponseContentModel model = new TokenServletResponseContentModel();
			model.token = rg.generate(8);
			logger.info(String.format( "token : \"%s\" were generated for client_id : \"%s\" ", model.token,contentModel.client_id));
			writer.write(gson.toJson(model));
		}
		catch(Exception ex)
		{
			logger.error(ex);
		}
		
//		try {
//			String authorizationValue = req.getHeader("authorization"); //TODO Perform encoding and decoding of the authorization
//			String clientId = null;
//			String clientSecret = null;
//			if(authorizationValue != null)
//			{
//				// check the auth header
//				ClientCredentialModel clientCredential =  gson.fromJson(authorizationValue, ClientCredentialModel.class);
//				clientId = clientCredential.client_id;
//				clientSecret = clientCredential.client_secret;
//			}
//			
//			String json = IOUtils.toString(req.getInputStream());
//			BodyTokenModel body = gson.fromJson(json, BodyTokenModel.class);
//			
//			PrintWriter writer = resp.getWriter();
//			// otherwise, check the post body
//			if(body.client_id != null)
//			{
//				if(clientId != null)
//				{
//					logger.error("Client attempted to authenticate with multiple methods");
//					resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
//					writer.write("{error:invalid_client}");
//					writer.flush();
//					writer.close();
//				}
//				clientId = body.client_id;
//				clientSecret = body.client_secret;
//			}
//			
//			Client client = clientService.getClient(clientId);
//			if(client == null)
//			{
//				logger.error("Unknown client " + clientId);
//				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
//				writer.write("{error:invalid_client}");
//				writer.flush();
//				writer.close();
//			}
//			
//			if(!client.get_clientSecret().equals( clientSecret))
//			{
//				logger.error(String.format("Mismatched client secret, expected %s got %s",client.get_clientSecret(),clientSecret));
//				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
//				writer.write("{error:invalid_client}");
//				writer.flush();
//				writer.close();
//			}
//			
//			if(body.grant_type == "authorization_code")
//			{
//				CodeModel codeModel = codeService.GetRequestByCode(body.code);
//				if(codeModel != null)
//				{
//					codeService.DeleteRequestByRequestId(body.code);
//					if(codeModel.authorizationQuery.client_id == clientId)
//					{
//						RandomStringGenerator rg = new RandomStringGenerator.Builder().withinRange('0', 'Z').filteredBy(LETTERS,DIGITS).build();
//						String accessToken = rg.generate(8);
//						AccessTokenContainerModel accessTokenContainerModel = new AccessTokenContainerModel();
//						accessTokenContainerModel.access_token = accessToken;
//						accessTokenContainerModel.client_id = clientId;
//						accessTokenContainerModel.scope = codeModel.authorizationQuery.scope;
//						accessTokenContainerModel.user = codeModel.user;
//						accessTokenContainerService.Save(accessTokenContainerModel);
//						
//						logger.info(String.format("Issuing access token %s", accessToken));
//						logger.info(String.format("with scope %s", codeModel.authorizationQuery.scope));
//						
//						TokenResponseModel tokenResponseModel = new TokenResponseModel();
//						tokenResponseModel.access_token = accessToken;
//						tokenResponseModel.token_type = "Bearer";
//						tokenResponseModel.scope = codeModel.authorizationQuery.scope;
//						
//						if(codeModel.authorizationQuery.scope.contains(" openid "))
//						{
//							Date IatDate = new Date();
//							Date ExpDate = DateUtils.addMinutes(IatDate, 5);
//							
//							JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
//							JWTPayloadModel payloadModel = new JWTPayloadModel();
//							payloadModel.iss = "http://localhost:9001"; //TODO get this value dynamically, this is the token issuer server base address
//							payloadModel.sub = codeModel.user.sub;
//							payloadModel.aud = client.get_clientId();
//							payloadModel.iat = String.format("%d",IatDate.getTime());
//							payloadModel.exp = String.format("%d",ExpDate);
//							
//							//TODO Handle nonce here
//							
//							Payload payload = new Payload(gson.toJson(payloadModel));
//							JWSObject jwsObject = new JWSObject(header, payload);
//							
//							byte[] sharedKey = new byte[32];
//							new SecureRandom().nextBytes(sharedKey);
//							jwsObject.sign(new MACSigner(sharedKey));
//							tokenResponseModel.id_token = jwsObject.serialize();
//							logger.info(String.format("Issuing ID token %s", tokenResponseModel));
//						}
//						
//						resp.setContentType("application/json");
//						writer = resp.getWriter();
//						writer.write(gson.toJson(tokenResponseModel));
//						writer.flush();
//						writer.close();
//						return;
//					} else {
//						logger.error(String.format("Client mismatch, expected %s got %s",client.get_clientId(),clientId));
//						resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
//						writer.write("{error:'invalid_grant'}");
//						writer.flush();
//						writer.close();
//						return;
//					}
//				} else {
//					logger.error(String.format("Unknown code, %s", body.code));
//					resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
//					writer.write("{error:'invalid_grant'}");
//					writer.flush();
//					writer.close();
//					return;
//				}		
//			} else {
//				logger.error("Unknown grant type %s", body.grant_type);
//				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
//				writer.write("{error:'unsupported_grant_type'}");
//				writer.flush();
//				writer.close();
//				return;
//			}
//		}catch(Exception ex) {
//			logger.info(ex);
//		}
		
	}
	
}
