

import java.io.IOException;
import java.sql.*;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oracle.jdbc.pool.OracleDataSource;
/**
 * Servlet implementation class DisplayHotelOptions
 */
@WebServlet("/DisplayHotelOptions")
public class DisplayHotelOptions extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String connect_string = "jdbc:oracle:thin:nbk2111/uMFjryhg@//w4111b.cs.columbia.edu:1521/ADB";
	private Connection conn;  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DisplayHotelOptions() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String tableName= request.getParameter("TableName");
		String hotelName=request.getParameter("HotelName");
		String hotelID=null;
		PrintWriter out=response.getWriter();
		out.println("TableName "+tableName);
		out.println("HotelName "+hotelName);
		try {
			if (conn == null) {
			// Create a OracleDataSource instance and set URL
			OracleDataSource ods = new OracleDataSource();
			ods.setURL(connect_string);
			conn = ods.getConnection();
			}
			Statement stmt = conn.createStatement();
			String queryFeed="select h.hotelid from Hotel h where h.name= '"+hotelName+"'";
			ResultSet rset = stmt.executeQuery(queryFeed);
			while((rset.next()))
			{
			hotelID=rset.getString("hotelid");
			//out.print(hotelID);
			}
			}
			catch (SQLException e) 
				{
					out.println(e.getMessage());
				}
		
			if(tableName.equalsIgnoreCase("Games"))
			{	
				String query="select * from Games NATURAL JOIN Casino where Casino.hotelid= '"+hotelID+"'";
				request.setAttribute("QueryStatement",query);
				RequestDispatcher view=request.getRequestDispatcher("DBResults.jsp");
				view.forward(request,response);
			}
			else
			{
				if(tableName.equalsIgnoreCase("Has_SpecialCustomer"))
				{	
					String query="select * from SpecialCustomer NATURAL JOIN Has_SpecialCustomer where Has_SpecialCustomer.hotelid= '"+hotelID+"'";
					request.setAttribute("QueryStatement",query);
					RequestDispatcher view=request.getRequestDispatcher("DBResults.jsp");
					view.forward(request,response);
				}
				else
				{	
				String query = "select * from "+tableName+" where hotelid='"+hotelID+"'";
				request.setAttribute("QueryStatement",query);
				RequestDispatcher view=request.getRequestDispatcher("DBResults.jsp");
				view.forward(request,response);
				}
			}
		}	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
