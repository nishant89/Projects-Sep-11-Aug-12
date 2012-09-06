<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<!-- This import is necessary for JDBC -->
<%@ page import="java.sql.*"%>
<%@ page import="oracle.jdbc.pool.OracleDataSource"%>
<!-- Database lookup -->
<%
Connection conn = null;
ResultSet rset = null;
String error_msg = "";
try {
OracleDataSource ods = new OracleDataSource();
ods.setURL("jdbc:oracle:thin:nbk2111/uMFjryhg@//w4111b.cs.columbia.edu:1521/ADB");
conn = ods.getConnection();
Statement stmt = conn.createStatement();
rset = stmt.executeQuery("select * from Hotel");
} catch (SQLException e) {
error_msg = e.getMessage();
if( conn != null ) {
conn.close();
}
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Hotel Table JSP Sample</title>
</head>
<body>
<H2>Hotel Table</H2>
<TABLE>
<tr>
<td>HotelName</td><td>HotelID</td><td>Location</td>
</tr>
<tr>
<td><b>----------</b></td><td><b>----------</b></td><td><b>----------</b></td>
</tr>
<%
if(rset != null) {
while(rset.next()) {
out.print("<tr>");
out.print("<td>" + rset.getString("name") + "</td><td>" +
rset.getString("hotelid") + "</td>" +
"<td>" + rset.getString("location") + "</td>");
out.print("</tr>");
}
} else {
out.print(error_msg);
}
if( conn != null ) {
conn.close();
}
%>
</TABLE>
</body>
</html>