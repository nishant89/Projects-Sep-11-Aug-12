<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.sql.*"%>
<%@ page import="oracle.jdbc.pool.OracleDataSource"%>
<%@ page import="java.util.*"%>
<%
int i=0;
Connection conn = null;
ResultSet rset = null;
ArrayList<String> queryResults = new ArrayList<String>();
String error_msg = "";
//StringBuffer buffer= new StringBuffer();              
try {
OracleDataSource ods = new OracleDataSource();
ods.setURL("jdbc:oracle:thin:nbk2111/uMFjryhg@//w4111b.cs.columbia.edu:1521/ADB");
conn = ods.getConnection();
Statement stmt = conn.createStatement();
rset = stmt.executeQuery("Select name from Hotel");
while((rset.next()))
queryResults.add((String)rset.getString("name"));
//buffer.append("<body> <h1 align =\"center\" style=\"color: Black\">Enter SpecialCustomer Details</h1><form id=\"frm\" method=\"post\">");
//buffer.append("Firstname: <input type=\"text\" name=\"firstname\" value=\"default\"/><br>Lastname: <input type=\"text\" name=\"Lastname\" value=\"default\"/><br>Averageyearlyexpenditure: <input type=\"text\" name=\"Averageyearlyexpenditure\" value=\"0\"/><br>Interest: <input type=\"text\" name=\"Interest\" value=\"default\"/><br>SSN: <input type=\"text\" name=\"SSN\" value=\"0\"/><br>HotelID: <input type=\"text\" name=\"HotelID\" value=\"0\"/>");
//while(i<queryResults.size())
//{
//String temp=(String)queryResults.get(i);
//i++;
//buffer.append("<input type=\"radio\" name=\"HotelName\" id=\""+temp+"\" value=\""+temp+"\"/>"+temp+"<br>");
//}
//buffer.append("<input type=\"submit\" value=\"Submit\" onclick=\"check()\"/></form></body>");
//out.print(buffer);
conn.close();
}
catch (SQLException e) 
{
	out.println(e.getMessage());
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>AddSpecialCustomer</title>
<script type="text/javascript">
 function check()
 		{
	 var flag=0;
		if(document.getElementById("firstname").value=="")
			{
			flag=1;
			alert("Please enter the firstname");
			}
		else {
			if(document.getElementById("lastname").value=="")
				{
				flag=1;
				alert("Please enter the lastname");
				}
				 else{
					
					 if(document.getElementById("averageyearlyexpenditure").value=="")
						{
						flag=1;
						alert("Enter the averageyearlyexpenditure");
						}
						else
						{
							if(document.getElementById("interest").value=="")
							{
							flag=1;
							alert("Enter the interest");
							}
							else{			
								 if(document.getElementById("SSN").value=="")
										{
										flag=1;
										alert("Please enter the SSN");
										}
							else
								{
									if(document.getElementById("SSN").value.length>9)
										{
										flag=1;
										alert("SSN cannot be longer than 9 digits");
										}
							else{
							var patt=/[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]/g;
							if(!patt.test(document.getElementById("SSN").value))
								{
								flag=1;
								alert("Enter 9 digits for SSN ");
								}
							else
							{
								if(document.getElementById("default").checked)
									{
									flag=1;
									alert("Select a Hotel");
									}
							else{
								if(!flag)
									{
									document.getElementById("frm").submit();
									}
									}
								}
							}
						}
					}
				 	}
				}
			}
	}
</script>
</head>
<body background="AddCustomer.jpg"> 
<a style="color:#000000"  href="http://localhost:9080/nbk2111/HomePage.html"><font color="black">Home</font></a>
<h1 align ="center" style="color: Black">Enter SpecialCustomer Details</h1>
<form id="frm" method="POST" action="UpdateServlet">
<table border="0">
<tr>
<td><label for ="firstname">Firstname<font color="brown">*</font></label></td>
<td><input type="text" name="firstname" id="firstname"/></td>
</tr>
<tr>
<td><label for ="lastname">Lastname<font color="brown">*</font></label></td>
<td><input type="text" name="lastname"  id="lastname"/></td>
</tr>
<tr>
<td><label for ="averageyearlyexpenditure">AverageYearlyExpenditure<font color="brown">*</font></label></td>
<td><input type="text" name="averageyearlyexpenditure"  id="averageyearlyexpenditure"/></td>
</tr>
<tr>
<td><label for ="interest">Interest</label><font color="brown">*</font></td>
<td><input type="text" name="interest"  id="interest"/></td>
</tr>
<tr>
<td><label for ="SSN">SSN<font color="brown">*</font></label></td>
<td><input type="text" name="SSN" id="SSN"/></td>
</tr>
</table>
<br><br>
<font>Enter the Hotel to which the Specialcustomer belongs</font><br>
<input type="radio" name="hotelName" id="default" checked="checked"/>Select Hotel<br>
<%
while(i<queryResults.size())
{
String temp=(String)queryResults.get(i);
i++;
%>
<input type="radio" name="hotelName" id="<%=temp%>" value="<%=temp%>"/> <%out.print(temp);%> <br>
<%}%>
<br><input type="button" value="submit" onclick="check()"/>
</form>
</body>
</html>