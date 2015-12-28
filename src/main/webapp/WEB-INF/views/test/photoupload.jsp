<!DOCTYPE html>
<html>
<head>

<title>Instructions</title>

<%@include file="../common/head.jsp"%>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3>Upload Photo</h3>
			</div>
		</div>
		<div class="col-sm-12">
			<form:form class="form-horizontal" role="form" name="photoForm" modelAttribute="recipe" enctype="multipart/form-data" autocomplete="off">	<!-- modelAttribute="recipe"  enctype="multipart/form-data"  -->
				<div class="form-group col-sm-12">
					<label class="control-label" style="text-align: left;">Photo:</label>
					<div class="col-sm-4" >
						<input type="file" name="file"/>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12">
						<small><spring:message code="common.requiredfield"></spring:message></small>
					</div>
				</div>
				<div class="row spacer-vert-lg">
					<div class="col-sm-5">
					</div>
					<div class="col-sm-2">
						<button class="btn btn-primary" type="submit" name="_eventId_proceed">Next</button>
					</div>
					<div class="col-sm-3">
					</div>
					<div class="col-sm-2">
						<button class="btn btn-default" type="submit" name="_eventId_cancel"><spring:message code="common.cancel"></spring:message></button>
					</div>
				</div>
				<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
			</form:form>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

</html>
