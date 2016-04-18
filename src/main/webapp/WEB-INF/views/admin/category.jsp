<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="category.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<spring:bind path="category.name"><c:set var="nameError">${status.errorMessage}</c:set></spring:bind>

	<div class="container container-white">
	 	<div class="col-sm-12 title-bar">
			<c:if test="${not empty warningMaint}">
				<h5 class="bold-maroon text-center"><em>${warningMaint}</em></h5>
			</c:if>
			<div class="page-header"> 		
				<h3><spring:message code="category.title"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">
			<div class="row">			
		    <form:form class="form-horizontal" role="form" action="category" method="post" modelAttribute="category">
		    	<form:hidden id="catId" path="id"/>
				<div class="form-group">
		        	<label class="control-label col-sm-4" for="inputCategory"><spring:message code="category.list"></spring:message>:</label>
		            <div class="col-sm-3">
		                <select class="form-control select-placeholder" id="inputCategory">
		                	<option value="" style="display:none"><spring:message code="recipe.basics.selectcat"></spring:message></option>
						</select>
					</div>
		        </div>           
		    	<div class="form-group">
		            <label class="control-label col-sm-4" for="name"><spring:message code="category.description"></spring:message>:</label>
		            <div class="col-sm-3">
		                <form:input class="form-control maxSize" type="text" id="name" path="name" autocomplete="off" data-max="${sizeMap['name.max']}"/>
		                <span class="text-danger" id="nameErrMsg">${nameError}</span>
		            </div>
		        </div>
		        <div class="form-group spacer-vert-sm">
		            <div class="col-sm-offset-4 col-sm-3 text-center">
		                <form:button type="submit" class="btn btn-primary pull-left" name="save"><spring:message code="common.save"></spring:message></form:button>
		                <form:button type="submit" class="btn btn-primary" name="delete"><spring:message code="common.delete"></spring:message></form:button>
		                <input type="reset" class="btn btn-default pull-right" value="Reset">
		            </div>
		        </div>
			</form:form>
		</div>
	</div>
</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- include category-specific routines -->
<script src="<c:url value="/resources/custom/category.js" />"></script>

</html>
