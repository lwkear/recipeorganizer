<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="row">
	<c:choose>
		<c:when test="${recipe.numInstructSections > 1}">
			<div class="col-sm-12">
				<div class="form-group col-sm-3 <c:if test="${not empty nameError}">has-error</c:if>">
					<label class="control-label" id="nameLabel" for="inputName">*<spring:message code="recipe.instructions.sectionname"></spring:message>${currNdx+1}</label>
					<form:input type="text" class="form-control" id="name" path="instructSections[${currNdx}].name" autocomplete="off"/>
					<span class="text-danger">${nameError}</span>
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<form:hidden id="name" path="instructSections[${currNdx}].name" value="XXXX"/>
		</c:otherwise>
	</c:choose>
	<div class="col-sm-12">
		<c:set var="instructplaceholder"><spring:message code="recipe.instructions.placeholder"></spring:message></c:set>
		<label class="control-label" id="descLabel" for="inputDesc">*<spring:message code="recipe.instructions.step"></spring:message></label>
		<div class="col-sm-12">
			<spring:bind path="recipe.instructSections[${currNdx}].instructions[0]"></spring:bind>
			<c:forEach items="${recipe.instructSections[currNdx].instructions}" varStatus="loop">
				<spring:bind path="instructSections[${currNdx}].instructions[${loop.index}].description">
					<c:set var="instructError">${status.errorMessage}</c:set>
				</spring:bind>
				<div class="instructGrp">
					<div class="form-group <c:if test="${not empty instructError}">has-error</c:if>">
						<div class="input-group">
							<form:hidden class="instructId instruct" path="instructSections[${currNdx}].instructions[${loop.index}].id"/>
							<form:hidden class="instructSeq instruct" path="instructSections[${currNdx}].instructions[${loop.index}].sequenceNo"/>
							<form:textarea class="form-control instructDesc instruct" id="inputDesc" rows="2" path="instructSections[${currNdx}].instructions[${loop.index}].description"
								placeholder="${instructplaceholder}" />
							<span class="input-group-btn">
								<button class="btn btn-danger removeInstruction" type="button" style="<c:if test="${loop.last}">display:none</c:if>">
									<span class="glyphicon glyphicon-minus"></span>
								</button>
								<button class="btn btn-success addInstruction" type="button">
									<span class="glyphicon glyphicon-plus"></span>
								</button>
							</span>
						</div>
						<span class="text-danger instructErr">${instructError}</span>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>
</div>
