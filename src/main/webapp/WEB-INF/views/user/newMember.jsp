<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="newmember.title"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<c:url var="faqUrl" value="${pageContext.servletContext.contextPath}/faq"></c:url>
	<c:url var="passwordUrl" value="${pageContext.servletContext.contextPath}/user/changePassword"></c:url>
	<c:url var="recipeUrl" value="${pageContext.servletContext.contextPath}/recipe"></c:url>
	<c:url var="profileUrl" value="${pageContext.servletContext.contextPath}/user/profile"></c:url>
	<c:url var="dashboardUrl" value="${pageContext.servletContext.contextPath}/user/dashboard"></c:url>
	
	<div class="container container-white">
		<div class="col-sm-12 title-bar">
			<div class="page-header">
				<h3><spring:message code="dashboard.title"></spring:message>,&nbsp;${user.firstName}</h3>
			</div>
		</div>
		<div class="col-sm-12">
			<h4><spring:message code="newmember.welcome"></spring:message></h4>
			<div>
				<spring:message code="newmember.welcome.message" arguments="${faqUrl}"></spring:message>
				<c:if test="${user.invited}">
					<br><br>
					<em><spring:message code="newmember.welcome.resetpassword" arguments="${passwordUrl}"></spring:message></em>
				</c:if>
			</div>
		</div>
		<div class="col-sm-12 spacer-vert-xs">
			<h4><spring:message code="newmember.accounts"></spring:message></h4>
			<div>
				<spring:message code="newmember.accounts.levels"></spring:message>
				<ul><li><spring:message code="newmember.accounts.guest"></spring:message></li>
				<li><spring:message code="newmember.accounts.author"></spring:message></li>
				<li><spring:message code="newmember.accounts.editor"></spring:message></li></ul>
				<c:set var="roleName"><spring:message code='roles.${user.role.name}'></spring:message></c:set>
				<spring:message code="newmember.accounts.currentlevel" arguments="${roleName}"></spring:message>
				<c:if test="${isGuest and (not (isAuthor || isEditor || isAdmin))}">
					<br><br>
					<spring:message code="newmember.accounts.guestupgrade" arguments="${recipeUrl},${profileUrl}"></spring:message>
				</c:if>
				<c:if test="${isAuthor and (not (isEditor || isAdmin))}">
					<br><br>
					<spring:message code="newmember.accounts.authorupgrade"></spring:message>
				</c:if>
			</div>
		</div>
		<div class="col-sm-12 spacer-vert-xs">
			<h4><spring:message code="newmember.recipes"></spring:message></h4>
			<div>
				<spring:message code="newmember.recipes.description1" arguments="${recipeUrl}"></spring:message>
				<spring:message code="newmember.recipes.description2"></spring:message>
				<spring:message code="newmember.recipes.description3"></spring:message>
				<spring:message code="newmember.recipes.description4"></spring:message>
				<spring:message code="newmember.recipes.description5"></spring:message>
				<spring:message code="newmember.recipes.description6"></spring:message>
				<spring:message code="newmember.recipes.description7"></spring:message>
				<spring:message code="newmember.recipes.description8"></spring:message>
			</div>
		</div>
		<div class="col-sm-12 spacer-vert-xs">
			<h4><spring:message code="newmember.features"></spring:message></h4>
			<div>
				<spring:message code="newmember.features.description"></spring:message>
				<ul><li><spring:message code="email.invitation.feature2"></spring:message></li>
				<li><spring:message code="email.invitation.feature3"></spring:message></li>
				<li><spring:message code="email.invitation.feature4"></spring:message></li>
				<ul><li><spring:message code="email.invitation.feature5"></spring:message></li>
				<li><spring:message code="email.invitation.feature6"></spring:message></li>
				<li><spring:message code="email.invitation.feature7"></spring:message></li>
				<li><spring:message code="email.invitation.feature8"></spring:message></li>
				<li><spring:message code="email.invitation.feature9"></spring:message></li></ul></ul>
			</div>
		</div>
		<div class="col-sm-12 spacer-vert-xs">
			<h4><spring:message code="newmember.nextstep"></spring:message></h4>
			<div>
				<spring:message code="newmember.nextstep.profile" arguments="${profileUrl}"></spring:message>
				<spring:message code="newmember.nextstep.dashboard" arguments="${dashboardUrl}"></spring:message>
				<spring:message code="newmember.nextstep.search"></spring:message>
			</div>
		</div>
		<div class="col-sm-12 spacer-vert-xs">
			<strong><spring:message code="thankyou.description3"></spring:message>
			<spring:message code="email.common.enjoy"></spring:message></strong>
		</div>
	</div>
    
<%@include file="../common/footer.jsp" %>	

</body>
</html>
