<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp"%>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="recipe.add.title"></spring:message></h3>
			</div>
		</div>
		<form:form class="form-horizontal" role="form" method="post" modelAttribute="recipe">		
			<div class="row">
				<div class="col-sm-12">
					<div class="form-group col-sm-9 <c:if test="${not empty nameError}">has-error</c:if>">
						<label class="control-label" id="nameLabel" for="inputName"><spring:message code="recipe.general.name"></spring:message></label>
						<form:input type="text" class="form-control recipeName" id="inputName" path="name" autocomplete="off"/>
						<span class="text-danger">${nameError}</span>
					</div>
					<div class="form-group col-sm-12">
						<label class="control-label" for="inputDesc"><spring:message code="recipe.general.description"></spring:message></label>
						<form:textarea class="form-control" rows="3" id="inputDesc" path="description"></form:textarea>
					</div>

					<div class="form-group col-sm-12">
						<div class="row">
							<label class="control-label col-sm-3" style="text-align: left;" id="categoryLabel" for="inputCategory">
								<spring:message code="recipe.general.category"></spring:message></label>
							<label class="control-label col-sm-3" style="text-align: left;" id="servingsLabel" for="inputServings">
								<spring:message code="recipe.general.servings"></spring:message></label>
							<label class="control-label col-sm-2" style="text-align: left;">
								<spring:message code="recipe.general.share"></spring:message></label>
						</div>
						<div class="row">
							<%-- <form:hidden id="catID" path="category.id"/> --%>
							<%-- <div class="col-sm-3 <c:if test="${not empty categoryError}">has-error</c:if>">
								<form:select class="form-control col-sm-3 select-placeholder" id="inputCategory" path="category.name">
									<option value="" style="display:none"><spring:message code="recipe.general.selectcat"></spring:message></option>
			            		</form:select>
			            		<span class="text-danger">${categoryError}</span>
			            	</div> --%>
							<div class="col-sm-3 <c:if test="${not empty servingsError}">has-error</c:if>">
								<form:input type="text" class="form-control col-sm-1" id="inputServings" path="servings" autocomplete="off"/>
								<span class="text-danger">${servingsError}</span>
							</div>
							<div class="col-sm-2">
								<div class="radio-inline">
									<form:radiobutton value="true" path="allowShare" checked="true"/><spring:message code="common.yes"></spring:message>
								</div>
								<div class="radio-inline">
									<form:radiobutton value="false" path="allowShare" /><spring:message code="common.no"></spring:message>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- <hidden id="userID" path="user.id"/> -->

			<div>
				<a class="btn btn-default" href="${flowExecutionUrl}&_eventId=proceed" role="button">Ingredients</a>
				<a class="btn btn-default" href="${flowExecutionUrl}&_eventId=cancel" role="button">Cancel</a>
			</div>
		</form:form>
	</div>
	

<%@include file="../common/footer.jsp" %>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/recipe.js" />"></script>

</html>
			