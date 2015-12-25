<!DOCTYPE html>
<html>
<head>

<title>About</title>

<%@include file="common/head.jsp" %>

</head>

<body role="document">

<%@include file="common/nav.jsp" %>

<c:set var="filename" value="VA Beach - Me.JPG"/>

	<div class="container container-white">
		<div class="col-sm-12">
			<div class="page-header">
				<h3><spring:message code="about.title"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">
			<%-- <img src="<c:url value="/user/avatar/${filename}"/>" style="width:75;height:75;"/> --%> 
			<img src="<c:url value="/user/avatar?filename=VA Beach - Me.JPG"/>" style="width:75px;height:75px;"/>
		</div>
	</div>
    
<%@include file="common/footer.jsp" %>	
	
</body>
</html>
