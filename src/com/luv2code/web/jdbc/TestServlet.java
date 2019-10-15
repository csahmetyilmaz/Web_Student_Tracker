package com.luv2code.web.jdbc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// define datasource/connection pool for resource Injection
	@Resource(name = "jdbc/web_student_tracker")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// step 1 : Setup the PrintWriter
		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");
		// Step 2: Get a connection to the database
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRes = null;

		try {
			myConn = dataSource.getConnection();
			// step 3 : Create a sql statement
			String sql = "SELECT * FROM student";
			myStmt = myConn.createStatement();
			// step 4 : Execute a query
			myRes = myStmt.executeQuery(sql);
			// Step 5 : Process the result set
			while(myRes.next()) {
				String email=myRes.getString("email");
				out.println(email);
			}
			
			
			
		} catch (Exception exc) {
			exc.printStackTrace();

		}

	}

}
