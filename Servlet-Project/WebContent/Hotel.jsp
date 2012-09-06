<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.sql.*"%>
<%@ page import="oracle.jdbc.pool.OracleDataSource"%>
<%@ page import="java.util.*"%>
<%
Connection conn = null;
ResultSet rset = null;
int columnCount;
String[] columnNames;
ArrayList queryResults = new ArrayList();
String rowData;
String error_msg = "";
StringBuffer buffer= new StringBuffer();
try {
OracleDataSource ods = new OracleDataSource();
ods.setURL("jdbc:oracle:thin:nbk2111/uMFjryhg@//w4111b.cs.columbia.edu:1521/ADB");
conn = ods.getConnection();
Statement stmt = conn.createStatement();
rset = stmt.executeQuery((String)request.getAttribute("QueryStatement"));
DatabaseMetaData dbMetaData = conn.getMetaData();
ResultSetMetaData resultsMetaData =
rset.getMetaData();
columnCount = resultsMetaData.getColumnCount();
columnNames = new String[columnCount];
for(int i=1; i<columnCount+1; i++) {
columnNames[i-1] =
resultsMetaData.getColumnName(i).trim();
}
while(rset.next()) {
String[] row = new String[columnCount];
for(int i=1; i<columnCount+1; i++) {
String entry = rset.getString(i);
if (entry != null) {
entry = entry.trim();
}
row[i-1] = entry;
}
queryResults.add(row);
}
buffer.append("<a align=\"left\" style=\"color:#FFFFFF\" href=\"http://localhost:9080/nbk2111/HomePage.html\"><font color=\"white\">Home</font></a>  <a style=\"color:#FFFFFF\" href=\"javascript: history.go(-1)\"><font color=\"white\">Back</font></a><br><br><br>");
buffer.append("<body background=\"HotelFront.jpg\">");
buffer.append("<h1 align=\"center\" style = \"font-color:white;\"><font color=\"white\">Hotels</font></h1>"+"<br><br>");
for(int row=0; row<queryResults.size(); row++) 
{
buffer.append("<form id=\"frm\" method="+"\""+"GET"+"\""+" action="+"\""+"HotelView.jsp"+"\""+">");
String[] rData = ((String[])queryResults.get(row));
buffer.append("<input type=\"button\" style=\"height: 25px; width: 120px\" value=\""+rData[0]+"\"onclick=\"SendValue('"+rData[0]+"')\"</a><br><br>");
}
buffer.append("<input type=\"hidden\" id=\"hidden1\" name=\"HotelName\" value=\"default\" />");
buffer.append("</form>");
out.print(buffer);
}
catch (SQLException e) 
{
error_msg = e.getMessage();
if( conn != null ) 
{
conn.close();
}
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Hotels</title>
<script type="text/javascript">
function SendValue(txt)
{
document.getElementById("hidden1").value=txt;
document.getElementById("frm").submit();
}
</script>
</head>
<form>
<input type= "button" style="height: 25px; width: 75px" value="Back" id="BackButton" onclick="javascript: history.go(-1)" />
</form>
</body>
</html>