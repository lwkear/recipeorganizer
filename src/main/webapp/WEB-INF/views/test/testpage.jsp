<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp"%>

<%-- <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="ISO-8859-1"%> --%>

</head>

<body role="document">

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
        <div class="col-sm-12">
			<div class="row">
				<div class="col-sm-5 <c:if test="${not empty tagsError}">has-error</c:if>">
					<select class="form-control col-sm-5" id="inputTags" multiple>
						<option value="">Enter a tag</option>
						<option value="Ch">Christmas</option>
						<option value="Th">Thanksgiving</option>
						<option value="It">Italian</option>
						<option value="Cn">Chinese</option>
					</select>
					<span class="text-danger">${tagsError}</span>
				</div>
			</div>
		</div>
		<div class="col-sm-12">
			<div class="col-sm-8 col col-sm-offset-2">			
				<div class="center alert text-center strong">
					<div><h4>${message}</h4></div>
				</div>
			</div>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<script type="text/javascript">

function viewtext() {
	var text = $('#inputTags option:selected').map(function () {
				return $(this).text();}).get();
	alert(text);	
}

$(function() {
	$('#inputTags').selectize({
		maxItems: 5,
	    persist: false,
	    diacritics: true,
	    create: function(input) {
	        return {
	            value: input,
	            text: input
	        }
	    }
	});
})

</script>

</html>
