<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
String para= request.getParameter("HotelName");
//(getServletContext()).setInitParameter(para,"HotelName");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>HotelView</title>
<script type="text/javascript">
function SendValue(txt)
{
document.getElementById("hidden1").value=txt;
document.getElementById("frm").submit();
}
</script>
</head>
<a style="color:#FFFFFF"  href="http://localhost:9080/nbk2111/HomePage.html"><font color="white">Home</font></a>  <a style="color:#FFFFFF" href="javascript: history.go(-1)"><font color="white">Back</font></a><br><br><br>
<body background="HotelFront.jpg">
<h1 align="center" ><font color="White"><%out.print(para);%></font></h1>
<form id='frm'  method="GET"
action="HotelEntities">
<input type="button" value="Rooms" style="height: 25px; width: 120px" id="Has_Room" onclick="SendValue('Has_Room')"/><br><br><br>
<input type="button" value="Shops" style="height: 25px; width: 120px" id="Shop" onclick="SendValue('Shop')"/><br><br><br>
<input type="button" value="Restaurants" style="height: 25px; width: 120px" id="Restaurant" onclick="SendValue('Restaurant')"/><br><br><br>
<input type="button" value="Casinos" style="height: 25px; width: 120px" id="Casino" onclick="SendValue('Casino')"/><br><br><br>
<input type="button" value="Casino Games" style="height: 25px; width: 120px" id="Games" onclick="SendValue('Games')"/><br><br><br>
<input type="button" value="NightClubs" style="height: 25px; width: 120px" id="NightClubs" onclick="SendValue('NightClub')"/><br><br><br>
<input type="button" value="Shows" style="height: 25px; width: 120px" id="Shows" onclick="SendValue('Shows')"/><br><br><br>
<input type="button" value="SPA" style="height: 25px; width: 120px" id="SPA" onclick="SendValue('SPA')"/><br><br><br>
<input type="button" value="Specialcustomers" style="height: 25px; width: 120px" id="SpecialCustomer" onclick="SendValue('Has_SpecialCustomer')" /><br><br><br>
<input type="button" value="Employee" style="height: 25px; width: 100px" id="Employee" onclick="SendValue('Employee')"/><br><br><br>
<input type="hidden" name="TableName" id="hidden1" value="default"/>
<input type="hidden" name="HotelName" id="hidden2" value="<%=para%>"/><br><br>
<!--<input type= "button" style="height: 25px; width: 75px" value="Back" id="BackButton" onclick="javascript: history.go(-1)" />-->
</form>
</body>
</html>
