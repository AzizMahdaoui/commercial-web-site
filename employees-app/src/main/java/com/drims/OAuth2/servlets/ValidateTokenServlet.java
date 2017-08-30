package com.drims.OAuth2.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.drims.OAuth2.TokenService;
import com.drims.OAuth2.json.serializable.models.ValidateTokenQueryModel;
import com.drims.OAuth2.json.serializable.models.ValidateTokenResponseModel;
import com.google.gson.Gson;

@WebServlet(
        name = "TokenServlet",
        urlPatterns = {"/token"}
)
public class ValidateTokenServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(ValidateTokenServlet.class);
	private static final Gson gson = new Gson();
	private static final TokenService tokenService = new TokenService();
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
		ValidateTokenQueryModel contentModel = gson.fromJson(json, ValidateTokenQueryModel.class);
		
		if(contentModel == null)
		{
			writer.write("{\"error\":\"server internal error, body is empty or json is not parsable\"}");
			logger.warn("Empty json connection attempt");
			writer.flush();
			writer.close();
			resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
			return;
		}else if(contentModel.validated_token == null) {
			writer.write("{\"error\":\"validated_token parameter is not provided in json\"}");
			logger.warn("Approve request attempt without client_id parameter");
			writer.flush();
			writer.close();
			resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
			return;
		}
		ValidateTokenResponseModel responseModel = new ValidateTokenResponseModel();
		responseModel.is_token_validated = tokenService.validateToken(contentModel.validated_token);
		writer.write(gson.toJson(responseModel));
		writer.flush();
		writer.close();
		return;
	}
}
