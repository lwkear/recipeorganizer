<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp"%>

<%-- <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="ISO-8859-1"%> --%>

</head>

<body role="document">

<script type="text/javascript">

function viewtext() {
	var text = $('#greeting').val();
	alert(text);	
}

</script>

<%@include file="../common/nav.jsp" %>
	
	<spring:bind path="webGreeting.greeting"><c:set var="greetingError">${status.errorMessage}</c:set></spring:bind>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3>Test</h3>
			</div>
		</div>
		<div class="col-sm-12">
			${text}
		</div>
		<div class="col-sm-12">
			<form:form role="form" modelAttribute="webGreeting" method="post">
				<div>
					<form:input id="greeting" type="text" path="greeting"/>
					<span class="text-danger">${greetingError}</span>
				</div>
		        <div class="form-group col-sm-2 col-sm-offset-5 text-center spacer-vert-sm">
					<button class="btn btn-primary" onclick="viewtext()" type="submit" name="submit">
						<spring:message code="common.submit"></spring:message></button>
        		</div>
			</form:form>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>
</html>
