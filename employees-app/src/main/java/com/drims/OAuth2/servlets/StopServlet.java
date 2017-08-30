package com.drims.OAuth2.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        name = "StopServlet",
        urlPatterns = {"/stop"}
)
public class StopServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Socket s = new Socket("localhost",8080);
        if(s.isConnected()){
            PrintWriter print = new PrintWriter(s.getOutputStream(),true);
            print.println("SHUTDOWN"); /*Command to stop tomcat according to the line "<Server port="8005" shutdown="SHUTDOWN">" in catalina_home/conf/server.xml*/
            print.close();
            s.close();
        }
	}

}
