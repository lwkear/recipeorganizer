<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="noPhoto"><spring:message code="common.photo.nophoto"></spring:message></c:set>

<div class="page-header">  <!-- style="margin-top:0;padding-bottom:0;padding-left:0"> -->
	<h5>
		<button type="button" class="btn btn-link btn-sm" onclick="toggleComments()" style="padding-left:0"><spring:message code="recipe.comments"></spring:message>
			<span class="badge" style="background-color:#337ab7">${commentCount}</span>
		</button>
		<button type="button" class="btn btn-link btn-sm" id="commentBtn" onclick="addComment(${viewerId}, ${recipe.id})" style="font-size:20px"
			data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.comment"></spring:message>">
			<span class="glyphicon glyphicon-comment"></span>
		</button>
	</h5>
</div>

<div class="list-group hidden" id="commentDiv">
	<c:forEach var="comment" items="${commentList}">
		<div class="list-group-item">
			<div class="media">
				<div class="media-left">
					<c:choose>
						<c:when test="${not empty comment.avatar}">
							<img class="media-object" style="width:48px" 
								src="<c:url value="/user/avatar?id=${comment.userId}&filename=${comment.avatar}"/>" alt="${noPhoto}">
						</c:when>
						<c:otherwise>
							<img class="media-object" style="width:48px" 
								data-src="holder.js/48x48?auto=yes&amp;text=${noPhoto}">
						</c:otherwise>
					</c:choose>
				</div>
				<div class="media-body">
					<h5 class="media-heading" style="font-weight: bold;">
						<fmt:formatDate type="both" timeStyle="short" value="${comment.dateAdded}" />
						&nbsp;&nbsp;&nbsp;${comment.firstName}&nbsp;${comment.lastName}
						<span class="pull-right">
							<button type="button" class="btn btn-link btn-sm" id="flagged-${comment.id}" 
								style="margin-left:5px;font-size:20px;color: #a66759; <c:if test="${comment.flag == 0}">display:none</c:if>"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.flagged"></spring:message>">
								<span class="glyphicon glyphicon-flag"></span>
							</button>
							<button type="button" class="btn btn-link btn-sm commentFlag" id="flagComment-${comment.id}" onclick="flagComment(${comment.id})" 
								style="margin-left:5px;font-size:20px; <c:if test="${comment.flag == 1}">display:none</c:if>" 
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.flagcomment"></spring:message>">
								<span class="glyphicon glyphicon-flag"></span>
							</button>
						</span>
					</h5>
					<div>${comment.userComment}</div>
				</div>
			</div>
		</div>
	</c:forEach>
</div>
