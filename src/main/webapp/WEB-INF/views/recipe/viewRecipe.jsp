<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title>${recipe.name} - <spring:message code="menu.product"></spring:message></title>

</head>

<sec:authentication var="viewerId" property="principal.id" />

<body role="document" onload="blurInputFocus()">

<c:set var="returnLabel" value="${sessionScope.returnLabel}"/>
<c:set var="returnUrl" value="${sessionScope.returnUrl}"/>

<%@include file="../common/nav.jsp" %>

<c:set var="privateRecipe" value="false"></c:set>
<c:if test="${(userId != recipe.user.id) and (recipe.copyrighted == true)}">
	<c:set var="privateRecipe" value="true"></c:set>
</c:if>
<c:if test="${isEditor}">
	<c:set var="privateRecipe" value="false"></c:set>
</c:if>
<c:set var="privateNotes" value="false"></c:set>
<c:if test="${userId == recipe.user.id}">
	<c:set var="privateNotes" value="true"></c:set>
</c:if>
<input type="text" id="recipeLocaleCode" style="display:none" value="${recipe.lang}"/>


	<div class="container container-white">	
	 	<div class="col-sm-12 title-bar">
			<c:if test="${not empty warningMaint}">
				<h5 class="bold-maroon text-center"><em>${warningMaint}</em></h5>
			</c:if>
			<div class="page-header">
				<c:if test="${not empty returnLabel}">
					<h5><a class="btn btn-link btn-xs" href="${returnUrl}"><spring:message code="${returnLabel}"></spring:message></a></h5>
				</c:if>
				<h3>${recipe.name}
					<button type="button" class="btn btn-link btn-sm" id="favLeft" onclick="removeFavorite(${viewerId}, ${recipe.id})" 
						style="margin-left:5px;font-size:20px;display:none"
						data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.favorite"></spring:message>">
						<span class="glyphicon glyphicon-star"></span>
					</button>
					<button type="button" class="btn btn-link btn-sm" id="madeLeft" onclick="selectMadeDate(${viewerId}, ${recipe.id})" 
						style="margin-left:5px;font-size:20px;display:none"
						data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.make"></spring:message>">
						<span class="glyphicon glyphicon-cutlery"></span>
					</button>
					<button type="button" class="btn btn-link btn-sm" id="noteLeft" onclick="addNote(${fn:escapeXml(jsonNote)})" 
						style="margin-left:5px;font-size:20px;display:none"
						data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.note"></spring:message>">
						<span class="glyphicon glyphicon-paperclip"></span>
					</button>
					<span class="pull-right">
						<button type="button" class="btn mic-btn-link btn-sm collapse" id="startSpeech" style="margin-left:30px;font-size:20px"
							data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.microphone"></spring:message>">
							<span class="fa fa-microphone fa-fw"></span>
						</button>
						<button type="button" class="btn btn-link btn-sm" id="htmlPrint" style="margin-left:5px;font-size:20px" 
							data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.print"></spring:message>">
							<span class="glyphicon glyphicon-print"></span>
						</button>
						<button type="button" class="btn btn-link btn-sm" id="share" onclick="shareRecipe(${viewerId}, ${recipe.id}, 
							'<spring:escapeBody javaScriptEscape="true">${recipe.name}</spring:escapeBody>')" style="margin-left:5px;font-size:20px"
							data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.email"></spring:message>">
							<span class="glyphicon glyphicon-envelope"></span>
						</button>
						<button type="button" class="btn btn-link btn-sm favorite" id="favRight" 
							onclick="addFavorite(${viewerId}, ${recipe.id})" style="margin-left:5px;font-size:20px"
							data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.favorite"></spring:message>">
							<span class="glyphicon glyphicon-star"></span>
						</button>
						<button type="button" class="btn btn-link btn-sm" id="madeRight" 
							onclick="selectMadeDate(${viewerId}, ${recipe.id})" style="margin-left:5px;font-size:20px"
							data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.make"></spring:message>">
							<span class="glyphicon glyphicon-cutlery"></span>
						</button>
						<button type="button" class="btn btn-link btn-sm" id="noteRight" 
							onclick="addNote(${fn:escapeXml(jsonNote)})" style="margin-left:5px;font-size:20px"
							data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.note"></spring:message>">
							<span class="glyphicon glyphicon-paperclip"></span>
						</button>
						<c:if test="${isAuth && (userId == recipe.user.id)}">
							<a class="btn btn-link btn-sm" id="edit" style="margin-left:5px;font-size:20px" href="<c:url value='/recipe/editRecipe/${recipe.id}'/>"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.edit"></spring:message>">
								<span class="glyphicon glyphicon-pencil"></span>
							</a>
						</c:if>
						<c:if test="${userId != recipe.user.id}">
							<button type="button" class="btn btn-link btn-sm" id="userMessage" 
								onclick="sendMessage(${viewerId}, ${recipe.user.id}, '${recipe.user.firstName}', '${recipe.user.lastName}', ${recipe.id}, 0,
								'<spring:escapeBody javaScriptEscape="true">${recipe.name}</spring:escapeBody>')" 
								style="margin-left:5px;font-size:20px" data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.message"></spring:message>">
								<span class="glyphicon glyphicon-user"></span>
							</button>
						</c:if>						
					</span>
				</h3>
				<h5>
					<span class="header-blue" id="submittedBy" data-toggle="popover"><spring:message code="common.submittedby"></spring:message>
					&nbsp;${recipe.user.firstName}&nbsp;${recipe.user.lastName}</span>
				</h5>
				<c:if test="${privateRecipe}">
					<h5 class="spacer-vert-xs">
						<span><spring:message code="recipe.view.copyright"></spring:message></span>
					</h5>
				</c:if>
			</div>

			<div id="popoverTitle" style="display:none;">
				<c:if test="${not empty profile.avatar}">
					<div>
						<span class="pull-left" style="margin-right:20px;">
							<img src="<c:url value="/user/avatar?id=${recipe.user.id}&filename=${profile.avatar}"/>" alt="" style="width:32px;height:32px;"/>
						</span>
					</div>
				</c:if>
				<div style="color:black;"><strong>${recipe.user.firstName}&nbsp;${recipe.user.lastName}</strong></div>
				<div style="color:black;">
					<c:if test="${not empty profile.city}">${profile.city}&nbsp;,&nbsp;</c:if>
					<c:if test="${not empty profile.state}">${profile.state}</c:if>
				</div>				
				<div class="clearfix"></div>
			</div>
			<div id="popoverContent" style="display:none;color:black;">
				<div style="color:black;">
					<div><strong><spring:message code="dashboard.membersince"></spring:message></strong>&nbsp;&nbsp;<fmt:formatDate type="date" value="${submitJoin}"/></div>
				</div>
				<c:if test="${not empty profile.interests}">
					<div style="color:black;">
						<div><strong><spring:message code="profile.interests"></spring:message></strong></div>
						<div>${profile.interests}</div>	
					</div>
				</c:if>				
			</div>
		</div>
		<div class="col-sm-12">
			<div>		
				<audio class="audioCtl"></audio>
			</div>			
							
			<div>
			
				<%@include file="recipeContent.jsp" %>
			
			</div>
			<div id="commentSection">
			
				<%@include file="comments.jsp" %>
			
			</div>
		</div>
	</div>		
	<div class="col-sm-12" style="display:none">
	<!-- <div class="col-sm-12"> -->
		<iframe id="iframerpt" name="iframerpt" width="100%" height="100%" src="<c:url value="/report/getHtmlRpt?uid=${userId}&rid=${recipe.id}"/>"></iframe>
	</div>
	<input type="hidden" id="userId" value="${recipe.user.id}"/>
	<input type="hidden" id="viewerId" value="${viewerId}"/>
	<input type="hidden" id="recipeId" value="${recipe.id}"/>
	<input type="hidden" id="recipeName" value="${recipe.name}"/>
	<input type="hidden" id="returnUrl" value="${returnUrl}"/>
	<input type="hidden" id="isFav" value="${favorite}"/>	

<%@include file="../common/footer.jsp" %>

<!-- enter made date dialog -->
<div class="modal" id="madeDateDlg" role="dialog">
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
							<label class="control-label" for="madeDate"><spring:message code="recipe.made.madecount"></spring:message></label>&nbsp;
							<span id="displayCount">${madeCount}</span>
						</div>
						<div class="form-group">
							<label class="control-label" for="madeDate"><spring:message code="recipe.made.lastmade"></spring:message></label>&nbsp;
							<span id="displayDate"><fmt:formatDate type="date" value="${lastMade}" /></span>
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
<div class="modal" id="noteDlg" role="dialog">
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
			            <textarea class="form-control" rows="5" id="inputNote">${recipeNote.note}</textarea>
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

<!-- add comment dialog -->
<div class="modal" id="commentDlg" role="dialog">
	<div class="modal-dialog modal-sm">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">${recipe.name}</h4>
			</div>
			<div class="modal-body">
				<form role="form" class="form">
					<div class="form-group">
			            <label class="control-label" for="recipeComment"><spring:message code="recipe.comments.label"></spring:message></label>
			            <textarea class="form-control maxSize" rows="5" id="userComment" data-max="${sizeMap['userComment.max']}"></textarea>
			            <span class="text-danger" id="userCommentErrMsg">${userCommentErr}</span>
				    </div>           
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal" id="submitComment"><spring:message code="common.submit"></spring:message></button>
				<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="common.cancel"></spring:message></button>
			</div>
		</div>
	</div>
</div>

<!-- share recipe dialog -->
<div class="modal" id="shareRecipeDlg" role="dialog">
	<div class="modal-dialog modal-sm">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">${recipe.name}</h4>
			</div>
			<div class="modal-body">
				<form:form role="form" class="form" modelAttribute="recipeShareDto"  >
					<div class="form-group">
			            <label class="control-label" for="recipientName"><spring:message code="share.recipient.name.label"></spring:message></label>
			            <form:input type="text" class="form-control" path="recipientName" id="recipientName"></form:input>
			            <span class="text-danger" id="recipientNameErrMsg"></span>
				    </div>           
					<div class="form-group">
			            <label class="control-label" for="recipientEmail"><spring:message code="share.recipient.email.label"></spring:message></label>
			            <form:input type="text" class="form-control" path="recipientEmail" id="recipientEmail"></form:input>
			            <span class="text-danger" id="recipientEmailErrMsg"></span>
				    </div>           
					<div class="form-group">
			            <label class="control-label" for="emailMsg"><spring:message code="share.message.label"></spring:message></label>
			            <form:textarea class="form-control" path="emailMsg" rows="5" id="emailMsg"></form:textarea>
				    </div>           
				</form:form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" id="submitShare"><spring:message code="common.submit"></spring:message></button>
				<button type="button" class="btn btn-default" id="cancelShare"><spring:message code="common.cancel"></spring:message></button>
			</div>
		</div>
	</div>
</div>

<%@include file="../user/userMessage.jsp" %>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/audio.js" />"></script>
<script src="<c:url value="/resources/custom/viewrecipe.js" />"></script>
<script src="<c:url value="/resources/custom/usermessage.js" />"></script>

</html>
