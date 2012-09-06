

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.jdbc.pool.OracleDataSource;

/**
 * Servlet implementation class UpdateServlet
 */
@WebServlet("/UpdateServlet")
public class UpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String connect_string = "jdbc:oracle:thin:nbk2111/uMFjryhg@//w4111b.cs.columbia.edu:1521/ADB";
	private Connection conn;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String hotelName=request.getParameter("hotelName");
		String firstName=request.getParameter("firstname");
		String lastName=request.getParameter("lastname");
		String averageYearlyExpenditure=request.getParameter("averageyearlyexpenditure");
		String interest=request.getParameter("interest");
		String SSN=request.getParameter("SSN");
		String hotelID="";
		PrintWriter out=response.getWriter();
		//out.print(hotelName+firstName+lastName+averageYearlyExpenditure+interest+SSN);
		try {
			//if (conn == null) {
			// Create a OracleDataSource instance and set URL
			OracleDataSource ods = new OracleDataSource();
			ods.setURL(connect_string);
			conn = ods.getConnection();
			Statement stmt = conn.createStatement();
			String queryFeed="insert into Specialcustomer values ('"+firstName+"','"+lastName+"','"+Long.parseLong(averageYearlyExpenditure)+"','"+interest+"','"+Long.parseLong(SSN)+"')";
			//out.print(queryFeed);
			stmt.executeQuery(queryFeed);
			queryFeed="select hotelid from Hotel where name='"+hotelName+"'";
			ResultSet rset = stmt.executeQuery(queryFeed);
			while((rset.next()))
			{
			hotelID=rset.getString("hotelid");
			}
			queryFeed="insert into Has_Specialcustomer values ('"+SSN+"','"+hotelID+"')";
			stmt.executeQuery(queryFeed);
			out.print("<a style=\"color:#000000\" href=\"AddSpecialCustomer.jsp\"><font color=\"black\">Add SpecialCustomer</font></a>                     <a style=\"color:#000000\" href=\"http://localhost:9080/nbk2111/HomePage.html\"><font color=\"black\">Home</font></a><body background=\"Update.jpg\">" +
					"<h1 align=\"center\"><font color=\"Black\">The special customer has been added</font></h1></body>");
			conn.close();
			//}
			}
			catch (SQLException e) 
			{
				try{
					conn.close();
				}
					catch(Exception E)
					{}
				
				out.println(e.getMessage());
	
			}
		
	}

}
