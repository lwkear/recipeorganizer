<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp"%>

<title>${recipe.name} - <spring:message code="menu.product"></spring:message></title>

</head>

<c:set var="returnUrl" value="${sessionScope.returnUrl}"/>

<body role="document">

<%@include file="../common/nav.jsp" %>

    <!-- TODO: EXCEPTION: move this to the server into a custom message interpolator -->
	<%-- <c:if test="${fn:contains(nameCode,'Size')}">
		<c:set var="nameLen">${fn:length(nameDisplayValue)}</c:set>
		<c:if test="${nameLen gt 0}">
			<c:set var="nameError">${nameError += " (you entered " += nameLen += ")"}</c:set>  
		</c:if>
	</c:if> --%>	
    
	<spring:bind path="recipe.name"><c:set var="nameError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.description"><c:set var="descriptionError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.servings"><c:set var="servingsError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.prepHours"><c:set var="prepHourError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.prepMinutes"><c:set var="prepMinuteError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.category.id"><c:set var="categoryError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.numIngredSections"><c:set var="ingredSectError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.numInstructSections"><c:set var="instructSectError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.tags"><c:set var="tagsError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.cookbookPage"><c:set var="cookbookPageError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.cookbook"><c:set var="cookbookError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.magazine"><c:set var="magazineError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.magazinePubdate"><c:set var="magazinePubdateError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.newspaper"><c:set var="newspaperError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.newspaperPubdate"><c:set var="newspaperPubdateError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.person"><c:set var="personError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.other"><c:set var="otherError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.websiteUrl"><c:set var="websiteUrlError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.recipeUrl"><c:set var="recipeUrlError">${status.errorMessage}</c:set></spring:bind>

	<c:if test="${not empty prepHourError}"><c:set var="prepTimeError">X</c:set></c:if>
	<c:if test="${not empty prepMinuteError}"><c:set var="prepTimeError">X</c:set></c:if>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3>${recipe.name}</h3>
			</div>
		</div>

		<form:form class="form-horizontal" role="form" method="post" modelAttribute="recipe"  enctype="multipart/form-data" autocomplete="off">
		
			<form:hidden id="userID" path="user.id"/>
			<form:input type="text" style="display:none" path="views"/>
			<form:hidden id="ingredSections" path="numIngredSections"/>
			<form:hidden id="currIngredSect" path="currIngredSection"/>
			<form:hidden id="instructSections" path="numInstructSections"/>
			<form:hidden id="currInstructSect" path="currInstructSection"/>

			<div class="row">
				<div class="col-sm-12">
				    <div class="panel-group" id="accordion">
				    	<div class="panel panel-default">
				            <div class="panel-heading">
				                <h4 class="panel-title"><a data-toggle="collapse" href="#panel1"><spring:message code="recipe.basics.title"></spring:message></a></h4>
				            </div>
				            <div id="panel1" class="panel-collapse collapse">
				            	<div class="panel-body">
									<div class="row">
										<div class="col-sm-12">
										
										<%@include file="basicsFields.jsp" %>
										
										</div>
									</div>
								</div>
							</div>
						</div>
						<c:forEach items="${recipe.ingredSections}" var="ingred" varStatus="ingredLoop">
							<c:set var="currNdx" value="${ingredLoop.index}"/>
							<c:set var="ingredSects" value="${recipe.numIngredSections}"/>
							<c:set var="panelnum" value="panel${2+ingredLoop.index}"/>
							<c:set var="panelsection" value="ingredpanel${ingredLoop.index}"/>
						
							<div class="panel panel-default" id="${panelsection}">
					            <div class="panel-heading">
					                <!-- <h4 class="panel-title"><a data-toggle="collapse" href="#panel2"> -->
					                <h4 class="panel-title">
					                	<a data-toggle="collapse" href="#${panelnum}"><spring:message code="recipe.ingredients.title"></spring:message>: ${ingred.name}</a>
					                </h4>
					            </div>
					            <div id="${panelnum}" class="panel-collapse collapse">
					                <div class="panel-body">
					                	
										<spring:bind path="recipe.ingredSections[${currNdx}]"></spring:bind>
										<spring:bind path="recipe.ingredSections[${currNdx}].name"><c:set var="nameError">${status.errorMessage}</c:set></spring:bind>		
										<form:hidden path="ingredSections[${currNdx}].id"/>
										<form:hidden path="ingredSections[${currNdx}].sequenceNo"/>
	
										<%@include file="ingredientsFields.jsp" %>
										
					                </div>
					            </div>
							</div>
						</c:forEach>
						<c:forEach items="${recipe.instructSections}" var="instruct" varStatus="instructLoop">
							<c:set var="currNdx" value="${instructLoop.index}"/>
							<c:set var="instructSects" value="${recipe.numInstructSections}"/>							
							<c:set var="panelnum" value="panel${2+instructSects+instructLoop.index}"/>
							<c:set var="panelsection" value="instructpanel${instructLoop.index}"/>
						
					        <div class="panel panel-default" id="${panelsection}">
					            <div class="panel-heading">
					                <h4 class="panel-title">
					                    <a data-toggle="collapse" href="#${panelnum}"><spring:message code="recipe.instructions.title"></spring:message>: ${instruct.name}</a>
					                </h4>
					            </div>
					            <div id="${panelnum}" class="panel-collapse collapse">
					                <div class="panel-body">
							
										<spring:bind path="recipe.instructSections[${currNdx}]"></spring:bind>
										<spring:bind path="recipe.instructSections[${currNdx}].name"><c:set var="nameError">${status.errorMessage}</c:set></spring:bind>
										<form:hidden path="instructSections[${currNdx}].id"/>
										<form:hidden path="instructSections[${currNdx}].sequenceNo"/>
								
										<%@include file="instructionsFields.jsp" %>
								
									</div>
								</div>
							</div>
						</c:forEach>
				        <div class="panel panel-default">
				        	<c:set var="panelnum" value="panel${2+ingredSects+instructSects}"/>
				        
				            <div class="panel-heading">
				                <h4 class="panel-title">
				                    <a data-toggle="collapse" href="#${panelnum}"><spring:message code="recipe.optional.title"></spring:message></a>
				                </h4>
				            </div>
				            <div id="${panelnum}" class="panel-collapse collapse">
				                <div class="panel-body">
						
								<%@include file="optionalFields.jsp" %>						
						
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group col-sm-12 spacer-vert-md">
				<div class="col-sm-offset-5 col-sm-2 text-center">
					<button type="submit" class="btn btn-primary pull-left" id="save"><spring:message code="common.save"></spring:message></button>
					<a class="btn btn-default pull-right" href="${returnUrl}"><spring:message code="common.cancel"></spring:message></a>
				</div>
			</div>
		</form:form>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/emptyrows.js" />"></script>
<script src="<c:url value="/resources/custom/basics.js" />"></script>
<script src="<c:url value="/resources/custom/typeahead.js" />"></script>
<script src="<c:url value="/resources/custom/ingredients.js" />"></script>
<script src="<c:url value="/resources/custom/instructions.js" />"></script>
<script src="<c:url value="/resources/custom/optional.js" />"></script>
<script src="<c:url value="/resources/custom/recipeedit.js" />"></script>

</html>


<%-- 	<spring:hasBindErrors name="recipe">
    <c:set var="errorCnt">${errors.errorCount}</c:set>
    <p><b># of Errors:${errorCnt}</b></p>
    <p></p>
	<c:forEach var="error" items="${errors.allErrors}">
		<b><c:out value="${error}" /></b>
		<p></p>
	</c:forEach>
	</spring:hasBindErrors> --%>