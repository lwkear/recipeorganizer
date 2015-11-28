<!DOCTYPE html>
<html>
<head>

<title>About</title>

<%@include file="common/head.jsp" %>

</head>

<body role="document">

<%@include file="common/nav.jsp" %>

	<%-- <div class="container container-white">
		<div class="col-sm-12">
			<div class="page-header">
				<h3><spring:message code="about.title"></spring:message></h3>
			</div>
		</div>
	</div> --%>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="about.title"></spring:message></h3>
			</div>			
			<div class="row">
				<form role="form" name="searchForm" method="post">
			        <div class="col-sm-12">
				        <div class="form-group col-sm-4 col-sm-offset-4">
							<%-- <label class="control-label" id="searchLabel" for="searchInput"><spring:message code="common.email"></spring:message></label> --%>
							<label class="control-label" id="searchLabel" for="searchTerm">Enter search word or phrase</label>
							<input class="form-control" type="text" id="searchTerm" name="searchTerm" autocomplete="off"/>
							<%-- <span class="text-danger" id="emailErrMsg">${emailError}</span> --%>
						</div>
					</div>
					<div class="form-group col-sm-12">&nbsp;</div>
			        <div class="row">				
				        <div class="form-group col-sm-2 col-sm-offset-5">
							<button class="btn btn-primary btn-block" type="submit" name="submit">
								<spring:message code="common.submit"></spring:message></button>
		        		</div>
	        		</div>
	        		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	      		</form>
			</div>
    	</div>
    </div>
    
<%@include file="common/footer.jsp" %>	
	
</body>
</html>
