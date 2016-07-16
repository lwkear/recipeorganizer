<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

			<div class="page-header" style="margin-top:0;padding-bottom:0">
				<h5>
					<button type="button" class="btn btn-link btn-sm" onclick="toggleComments()"><spring:message code="recipe.comments"></spring:message>
						<span class="badge" style="background-color:#337ab7">${commentCount}</span>
					</button>
					<button type="button" class="btn btn-link btn-sm" id="commentBtn" onclick="addComment(${viewerId}, ${recipe.id})" style="font-size:20px"
						data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.comment"></spring:message>">
						<span class="glyphicon glyphicon-comment"></span>
					</button>
				</h5>
			</div>
			<div class="list-group col-sm-12 hidden" id="commentDiv">
				<c:forEach var="comment" items="${commentList}">
					<div class="list-group-item">
						<h5 class="list-group-item-heading">
							<c:if test="${not empty comment.avatar}">
								<span><img src="<c:url value="/user/avatar?id=${comment.userId}&filename=${comment.avatar}"/>" style="width:25px;height:25px;"/></span>
							</c:if>
							<span><fmt:formatDate type="both" timeStyle="short" value="${comment.dateAdded}" /></span>
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
						<p class="list-group-item-text">${comment.userComment}</p>
					</div>
				</c:forEach>
			</div>
