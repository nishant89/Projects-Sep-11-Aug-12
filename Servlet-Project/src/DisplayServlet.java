

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
/**
 * Servlet implementation class DisplayServlet
 */
@WebServlet("/DisplayServlet")
public class DisplayServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DisplayServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String tableName= request.getParameter("TableName");
		//String hotelViewFlag=request.getParameter("HotelViewButton");
		if(tableName.equalsIgnoreCase("HotelView"))
		{
			String query = "select * from Hotel";
			request.setAttribute("QueryStatement",query);
			RequestDispatcher view=request.getRequestDispatcher("Hotel.jsp");
			view.forward(request,response);
		}
//		PrintWriter out= response.getWriter();
//		out.println(tableName);
		else
		{
			String query = "select * from"+" "+tableName;
			request.setAttribute("QueryStatement",query);
			RequestDispatcher view = request.getRequestDispatcher("DBResults.jsp");
			view.forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
