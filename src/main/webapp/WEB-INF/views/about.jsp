<!DOCTYPE html>
<html>
<head>

<title>About</title>

<%@include file="common/head.jsp" %>

</head>

<body role="document">

<%@include file="common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12">
			<div class="page-header">
				<h3><spring:message code="about.title"></spring:message></h3>
			</div>
		</div>
		<form:form class="form-horizontal" role="form" name="photoForm" enctype="multipart/form-data" autocomplete="off">	<!-- modelAttribute="recipe"  enctype="multipart/form-data"  -->
			<div class="form-group col-sm-12">
				<label class="control-label" style="text-align: left;">Photo:</label>
				<div class="col-sm-4" >
					<input type="file" name="file"/>
				</div>
			</div>
			<div class="col-sm-2">
				<button class="btn btn-primary" type="submit" name="Submit">Submit</button>
			</div>
		</form:form>
	</div>
    
<%@include file="common/footer.jsp" %>	
	
</body>
</html>
