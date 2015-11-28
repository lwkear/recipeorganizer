<!DOCTYPE html>
<html>
<head>

<title>Thankyou</title>

<%@include file="common/head.jsp" %>

</head>

<body role="document">
	
<%@include file="common/nav.jsp" %>	

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<%-- <h1><spring:message code="thankyou.title"></spring:message></h1> --%>
				<h1>Search Results</h1>
			</div>
		</div>
		<div class="row">
	        <div class="col-sm-12">
	        	<h4>Your search returned ${numFound} results!</h4>
       		</div>
			<div class="col-sm-12">
				<c:forEach var="recipe" items="${nameList}">
					<p>Recipe: ${recipe}</p>
				</c:forEach>
			</div>
		</div>	
	</div>

<%@include file="common/footer.jsp" %>
	
</body>
</html>
