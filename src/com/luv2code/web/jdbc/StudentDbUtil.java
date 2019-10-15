package com.luv2code.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDbUtil {

	private DataSource dataSource;

	public StudentDbUtil(DataSource theDataSource) {

		this.dataSource = theDataSource;
	}

	public List<Student> getStudents() throws Exception {
		List<Student> students = new ArrayList<>();

		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRes = null;

		try {
			// get a connection
			myConn = dataSource.getConnection();
			// create a sql statement
			String sql = "SELECT * FROM student order by id ";
			myStmt = myConn.createStatement();
			// execute query
			myRes = myStmt.executeQuery(sql);
			// process resultset
			while (myRes.next()) {
				// retrieve data from resultset
				int id = myRes.getInt("id");
				String firstName = myRes.getString("first_name");
				String lastName = myRes.getString("last_name");
				String email = myRes.getString("eMail");

				// create a new student object
				Student tempStudent = new Student(id, firstName, lastName, email);
				// add it to the list of students
				students.add(tempStudent);
			}

			return students;
		} finally {
			// close JDBC object
			close(myConn, myStmt, myRes);

		}

	}

	private void close(Connection myConn, Statement myStmt, ResultSet myRes) {

		try {
			if (myRes != null) {
				myRes.close();
			}
			if (myStmt != null) {
				myStmt.close();
			}
			if (myConn != null) {
				myConn.close(); // doesn't really close it. Just puts back in connection pool
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}

	}

	public void addStudent(Student theStudent) throws Exception {
		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			// get the db connection
			myConn = dataSource.getConnection();
			// create sql for insert
			String sql = "INSERT INTO student " + "(first_name, last_name, email) " + "values (?, ?, ?)";
			myStmt = myConn.prepareStatement(sql);

			// set the param values for the student
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());

			// execute sql insert query
			myStmt.execute();
		} finally {
			// Clean JDBC object
			close(myConn, myStmt, null);
		}

	}

	public Student getStudent(String theStudentId) throws Exception {

		Student theStudent = null;

		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRes = null;
		int studentId;

		try {

			// convert the id to int
			studentId = Integer.parseInt(theStudentId);
			// get connection to db
			myConn = dataSource.getConnection();

			// create sql to get selected student
			String sql = "select * from student where id=?";

			// create prepared statement
			myStmt = myConn.prepareStatement(sql);

			// set params
			myStmt.setInt(1, studentId);

			// execute
			myRes = myStmt.executeQuery();
			// retrieve data from result set row
			if (myRes.next()) {
				String firstName = myRes.getString("first_name");
				String lastName = myRes.getString("last_name");
				String email = myRes.getString("email");

				// using the student id for constructor
				theStudent = new Student(studentId, firstName, lastName, email);
			} else {
				throw new Exception("Couldn't find Student id : " + studentId);
			}

			return theStudent;
		} finally {
			// clean up JDBC objects
			close(myConn, myStmt, myRes);
		}
	}

	public void updateStudent(Student theStudent) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			// get connection to db
			myConn = dataSource.getConnection();

			// create sql to get UPDATE student
			String sql = "UPDATE student " + "set first_name=?, last_name=?, email=? " + "where id=?";

			// prepare statement
			myStmt = myConn.prepareStatement(sql);
			// set params
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			myStmt.setInt(4, theStudent.getId());

			// execute SQL stmt
			myStmt.execute();
		} finally {
			// clean up JDBC objects
			close(myConn, myStmt, null);
		}

	}

	public void deleteStudent(String theStudentId) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			// Convert id to integer
			int id = Integer.parseInt(theStudentId);
			// get connection to database
			myConn = dataSource.getConnection();
			// create sql delete student
			String sql = "DELETE FROM student WHERE id=?";
			// prepare statement to delete
			myStmt = myConn.prepareStatement(sql);
			// set params
			myStmt.setInt(1, id);
			// Execute sql statement
			myStmt.execute();

		} finally {
			// clean up JDBC objects
			close(myConn, myStmt, null);
		}
	}

}
