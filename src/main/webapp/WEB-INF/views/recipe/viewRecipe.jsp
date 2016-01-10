<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title>${recipe.name} - <spring:message code="menu.product"></spring:message></title>

</head>

<c:set var="returnLabel" value="${sessionScope.returnLabel}"/>
<c:set var="returnUrl" value="${sessionScope.returnUrl}"/>
<sec:authentication var="viewerId" property="principal.id" />

<body role="document" onload="blurInputFocus()">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header">
				<c:if test="${not empty returnLabel}">
					<h5><a class="btn btn-link btn-xs" href="${returnUrl}"><spring:message code="${returnLabel}"></spring:message></a></h5>
				</c:if>
				<h3>${recipe.name}
					<c:if test="${favorite}">
						<button type="button" class="btn btn-link btn-sm" style="margin-left:5px;font-size:20px" disabled>
						  <span class="glyphicon glyphicon-star"></span>
						</button>
						<!-- <span class="btn-sm glyphicon glyphicon-star" style="margin-left:5px;font-size:20px"></span> -->
					</c:if>
					<c:if test="${madeCount > 0}">
						<button type="button" class="btn btn-link btn-sm madeDate" onclick="selectMadeDate(${viewerId}, ${recipe.id})" style="margin-left:5px;font-size:20px">
						  <span class="glyphicon glyphicon-cutlery"></span>
						</button>
					</c:if>
					<c:if test="${not empty recipeNote}">
						<button type="button" class="btn btn-link btn-sm" onclick="addNote(${fn:escapeXml(jsonNote)})" style="margin-left:5px;font-size:20px">
						  <span class="glyphicon glyphicon-paperclip"></span>
						</button>
					</c:if>
					<span class="pull-right">
						<button type="button" class="btn btn-link btn-sm" id="htmlPrint" style="margin-left:30px;font-size:20px">
						  <span class="glyphicon glyphicon-print"></span>
						</button>
						<button type="button" class="btn btn-link btn-sm" id="email" style="margin-left:5px;font-size:20px">
						  <span class="glyphicon glyphicon-envelope"></span>
						</button>
						<c:if test="${not favorite}">
							<button type="button" class="btn btn-link btn-sm favorite" onclick="addFavorite(${viewerId}, ${recipe.id})" style="margin-left:5px;font-size:20px">
							  <span class="glyphicon glyphicon-star"></span>
							</button>
						</c:if>
						<c:if test="${madeCount eq 0}">
							<button type="button" class="btn btn-link btn-sm madeDate" onclick="selectMadeDate(${viewerId}, ${recipe.id})" style="margin-left:5px;font-size:20px">
							  <span class="glyphicon glyphicon-cutlery"></span>
							</button>
						</c:if>
						<c:if test="${empty recipeNote}">
							<button type="button" class="btn btn-link btn-sm" onclick="addNote(${fn:escapeXml(jsonNote)})" style="margin-left:5px;font-size:20px">
							  <span class="glyphicon glyphicon-paperclip"></span>
							</button>
						</c:if>
						<c:if test="${isAuth && (userId == recipe.user.id)}">
							<a class="btn btn-link btn-sm" id="edit" style="margin-left:5px;font-size:20px" href="../editRecipe/${recipe.id}">
								<span class="glyphicon glyphicon-pencil"></span>
							</a>
						</c:if>
					</span>
				</h3>
				<h5>
					<spring:message code="common.submittedby"></spring:message>&nbsp;${recipe.user.firstName}&nbsp;${recipe.user.lastName}
				</h5>
			</div>
			
			<%@include file="recipeContent.jsp" %>
			
		</div>
	</div>		
	
	<div class="col-sm-12" style="display:none">
		<iframe id="iframerpt" name="iframerpt" width="100%" height="100%" src="<c:url value="/report/getHtmlRpt/${recipe.id}"/>"></iframe>
	</div>
	<input type="hidden" id="userId" value="${recipe.user.id}"/>
	<input type="hidden" id="viewerId" value="${viewerId}"/>
	<input type="hidden" id="recipeId" value="${recipe.id}"/>
	<input type="hidden" id="recipeName" value="${recipe.name}"/>
	<input type="hidden" id="returnUrl" value="${returnUrl}"/>

<%@include file="../common/footer.jsp" %>

<!-- enter made date dialog -->
<div class="modal fade" id="madeDateDlg" role="dialog">
	<div class="modal-dialog modal-sm">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">${recipe.name}</h4>
			</div>
			<div class="modal-body">
				<form role="form" class="form">
					<c:if test="${madeCount > 0}">
						<div class="form-group">
							<label class="control-label" for="madeDate"><spring:message code="recipe.made.madecount"></spring:message></label>&nbsp;<span id="displayCount">${madeCount}</span>
						</div>
						<div class="form-group">
							<label class="control-label" for="madeDate"><spring:message code="recipe.made.lastmade"></spring:message></label>&nbsp;<span id="displayDate">${lastMade}</span>
						</div>
					</c:if>
					<div class="form-group">
			            <label class="control-label" for="madeDate"><spring:message code="recipe.made.selectlabel"></spring:message></label>
			            <div id="madeDate"></div>
				    </div>           
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal" id="submitMadeDate"><spring:message code="common.submit"></spring:message></button>
				<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="common.cancel"></spring:message></button>
			</div>
		</div>
	</div>
</div>

<!-- enter note dialog -->
<div class="modal fade" id="noteDlg" role="dialog">
	<div class="modal-dialog modal-sm">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">${recipe.name}</h4>
			</div>
			<div class="modal-body">
				<form role="form" class="form">
					<div class="form-group">
			            <label class="control-label" for="recipeNote"><spring:message code="recipe.note.label"></spring:message></label>
			            <textarea class="form-control" rows="5" id="inputNote">${recipeNote}</textarea>
				    </div>           
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal" id="submitNote"><spring:message code="common.submit"></spring:message></button>
				<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="common.cancel"></spring:message></button>
			</div>
		</div>
	</div>
</div>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/recipeview.js" />"></script>

</html>