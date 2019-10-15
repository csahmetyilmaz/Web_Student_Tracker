package com.luv2code.web.jdbc;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class StudentControllerServlet
 */
@WebServlet("/StudentControllerServlet")
public class StudentControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private StudentDbUtil studentDbUtil;

	@Resource(name = "jdbc/web_student_tracker")
	private DataSource dataSource;

	@Override
	public void init() throws ServletException {
		super.init();

		// Create our Students dbUtil ... and pass the conn pool / dataSource
		try {
			studentDbUtil = new StudentDbUtil(dataSource);
		} catch (Exception exc) {
			throw new ServletException(exc);
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			// read the "command" parameter
			String theCommand = request.getParameter("command");

			// route to the appropriate method
			switch (theCommand) {

			case "ADD":
				addStudent(request, response);
				break;

			default:
				listStudents(request, response);
			}

		} catch (Exception exc) {
			throw new ServletException(exc);
		}

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {

			// READ the COMMAND parameter
			String theCommand = request.getParameter("command");
			// route to the appropriate method
			if (theCommand == null) {
				theCommand = "LIST";
			}

			switch (theCommand) {
			case "LIST":
				listStudents(request, response);
				break;

			case "LOAD":
				loadStudents(request, response);
				break;

			case "UPDATE":
				updateStudent(request, response);
				break;
				
			case "DELETE":
				deleteStudent(request,response);
				break;

			default:
				listStudents(request, response);
				break;
			}

			// List students wit MVC fashion
			listStudents(request, response);
		} catch (Exception exc) {
			throw new ServletException(exc);
		}

	}

	private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		// read students info from form data
		String theStudentId =request.getParameter("studentId");
		//perform delete on database
		studentDbUtil.deleteStudent(theStudentId);
		// send them back to the "list students" page
		
	}

	private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// read students info from form data
		int id = Integer.parseInt(request.getParameter("studentId"));
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");

		// create a new student object
		Student theStudent = new Student(id, firstName, lastName, email);
		// perform update on database
		studentDbUtil.updateStudent(theStudent);

		// send them back to the "list students" page
		listStudents(request, response);
	}

	private void loadStudents(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// read student ID data from form data
		String theStudentId = request.getParameter("studentId");

		// get student from database
		Student theStudent = studentDbUtil.getStudent(theStudentId);

		// place student in the request attribute
		request.setAttribute("THE_STUDENT", theStudent);

		// send to JSP page (update-student-form.jsp
		RequestDispatcher dispatcher = request.getRequestDispatcher("/update-student-form.jsp");
		dispatcher.forward(request, response);

	}

	private void addStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// read student info from data
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		// create a new student object
		Student theStudent = new Student(firstName, lastName, email);
		// add the student to the database
		studentDbUtil.addStudent(theStudent);
		// send back to main page (student List)
		// listStudents(request, response);
		// SEND AS REDIRECT to avoid multiple-browser reload issue
		response.sendRedirect(request.getContextPath() + "/StudentControllerServlet?command=LIST");

	}

	private void listStudents(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// get Students from dbUtil
		List<Student> students = studentDbUtil.getStudents();
		// add students to the request object
		request.setAttribute("STUDENT_LIST", students);
		// send to jsp page(view)
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list-students.jsp");
		dispatcher.forward(request, response);
	}

}
