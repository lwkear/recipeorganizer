<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="net.kear.recipeorganizer.persistence.model.Role"%>

<sec:authorize var="isAuth" access="isAuthenticated()"/>
<sec:authorize var="isAdmin" access="hasAuthority('${Role.TYPE_ADMIN}')"/>
<sec:authorize var="isEditor" access="hasAnyAuthority('${Role.TYPE_EDITOR}','${Role.TYPE_ADMIN}')"/>
<sec:authorize var="isAuthor" access="hasAnyAuthority('${Role.TYPE_AUTHOR}','${Role.TYPE_EDITOR}','${Role.TYPE_ADMIN}')"/>
<sec:authorize var="isGuest" access="hasAnyAuthority('${Role.TYPE_GUEST}','${Role.TYPE_AUTHOR}','${Role.TYPE_EDITOR}','${Role.TYPE_ADMIN}')"/>

<c:if test="${isAuth}">
	<sec:authentication var="firstname" property="principal.firstName"/>
	<sec:authentication var="lastname" property="principal.lastName" />
	<sec:authentication var="userId" property="principal.id" />
	<sec:authentication var="newmsgs" property="principal.newMsgCount" />
</c:if>

<c:set var="localeCode" value="${requestContext.locale.language}"/>
<input type="text" id="localeCode" style="display:none" value="${localeCode}"/>

<!-- Note: path is required for AJAX url's -->
<script>var appContextPath = "${pageContext.request.contextPath}"</script>

<nav class="navbar navbar-inverse navbar-fixed-top">
	<div class="container">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
			<c:choose>				
				<c:when test="${isAuth}">
					<a href="<c:url value="/user/dashboard" />"><img src="<c:url value="/resources/images/logo.png"/>" height=36 style="margin-top:7px"></a>
				</c:when>
				<c:otherwise>
					<a href="<c:url value="/home" />"><img src="<c:url value="/resources/images/logo.png"/>" height=36 style="margin-top:7px"></a>
				</c:otherwise>
			</c:choose>			
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<c:if test="${isGuest}">
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
								<spring:message code="menu.recipe"></spring:message><span class="caret"></span></a>
						<ul class="dropdown-menu" role="menu">
							<li><a href="<c:url value="/recipe"/>"><spring:message code="menu.submitrecipe"></spring:message></a></li>
							<li><a href="<c:url value="/recipe/recipeList" />"><spring:message code="menu.submittedrecipes"></spring:message></a></li>
							<li><a href="<c:url value="/recipe/favorites" />"><spring:message code="menu.favorites"></spring:message></a></li>
							<li role="separator" class="divider"></li>
							<li><a href="<c:url value="/recipe/browseRecipes" />"><spring:message code="menu.browse"></spring:message></a></li>
						</ul>
					</li>
				</c:if>
				<c:if test="${isEditor}">
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
								<spring:message code="menu.admin"></spring:message><span class="caret"></span></a>
						<ul class="dropdown-menu" role="menu">
							<li><a href="<c:url value="/admin/approval" />"><spring:message code="menu.approval"></spring:message></a></li>
							<li><a href="<c:url value="/admin/comments" />"><spring:message code="menu.comments"></spring:message></a></li>
							<li><a href="<c:url value="/admin/ingredients" />"><spring:message code="menu.ingredients"></spring:message></a></li>
							<c:if test="${isAdmin}">
								<li><a href="<c:url value="/admin/category" />"><spring:message code="menu.categories"></spring:message></a></li>							
								<li role="separator" class="divider"></li>
								<li><a href="<c:url value="/admin/users" />"><spring:message code="menu.users"></spring:message></a></li>
								<li><a href="<c:url value="/admin/invitation" />"><spring:message code="menu.invite"></spring:message></a></li>
								<li role="separator" class="divider"></li>
								<li><a href="<c:url value="/admin/maintenance" />"><spring:message code="menu.maintenance"></spring:message></a></li>
								<li><a href="<c:url value="/admin/certificate" />"><spring:message code="menu.certificate"></spring:message></a></li>
							</c:if>
						</ul>
					</li>
				</c:if>
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
							<spring:message code="menu.info"></spring:message><span class="caret"></span></a>
					<ul class="dropdown-menu" role="menu">
						<li><a href="<c:url value="/faq" />"><spring:message code="menu.faq"></spring:message></a></li>
						<li><a href="<c:url value="/whatsnew" />"><spring:message code="menu.whatsnew"></spring:message></a></li>
						<li role="separator" class="divider"></li>
						<li><a href="<c:url value="/contact" />"><spring:message code="menu.contact"></spring:message></a></li>							
						<li><a href="<c:url value="/policies" />"><spring:message code="menu.policies"></spring:message></a></li>
						<li><a href="<c:url value="/about" />"><spring:message code="menu.about"></spring:message></a></li>
						<%-- <c:if test="${isAdmin}">
							<li><a href="<c:url value="/test/testpage" />">Test</a></li>
						</c:if> --%>
					</ul>
				</li>
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
							<spring:message code="menu.search"></spring:message><span class="caret"></span></a>
					<ul class="dropdown-menu" role="menu">
						<li>
							<c:url var="searchUrl" value="/submitSearch"/>
							<form:form class="navbar-form" id="searchForm" action="${searchUrl}" method="post">
								<div class="input-group">
									<input class="form-control" type="text" style="min-width:400px" placeholder="<spring:message code="common.search"></spring:message>" name="searchTerm" id="searchTerm" autocomplete="off"/>	
									<div class="input-group-btn">
										<button class="btn btn-default" type="submit"><i class="glyphicon glyphicon-search"></i></button>
									</div>
								</div>
							</form:form>
						</li>
					</ul>
				</li>
			</ul>
			<c:choose>
				<c:when test="${isAuth}">
					<c:url var="logoutUrl" value="/logout"/>
					<form:form id="logoutForm" action="${logoutUrl}" method="post">
						<ul class="nav navbar-nav navbar-right">
							<li><a href="<c:url value="/user/messages" />"
									data-toggle="tooltip" data-placement="bottom" title="<spring:message code="menu.messages"></spring:message>">
									<span class="glyphicon glyphicon-inbox"></span>
									<c:if test="${newmsgs > 0}"><span class="badge" style="background-color:red;margin-bottom:5px;position:relative">${newmsgs}</span></c:if>
								</a>
							</li>
							<li class="dropdown">
								<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
									<span class="glyphicon glyphicon-edit"></span>&nbsp;&nbsp;${firstname}<span class="caret"></span></a>
						        <ul class="dropdown-menu" role="menu">
						        	<li><a href="<c:url value="/user/dashboard" />"><spring:message code="menu.dashboard"></spring:message></a></li>
						        	<li><a href="<c:url value="/user/messages" />"><spring:message code="menu.messages"></spring:message></a></li>						        	
						        	<li role="separator" class="divider"></li>
						        	<li><a href="<c:url value="/user/changeAccount" />"><spring:message code="menu.account"></spring:message></a></li>
						        	<li><a href="<c:url value="/user/profile" />"><spring:message code="menu.profile"></spring:message></a></li>
						        	<li role="separator" class="divider"></li>
						        	<li><a href="<c:url value="/user/newMember" />"><spring:message code="menu.newmember"></spring:message></a></li>
						        </ul>
						    </li>
					        <li><a href="javascript:submitLogoutForm()"><span class="glyphicon glyphicon-log-out"></span>&nbsp;&nbsp;<spring:message code="menu.logout"></spring:message></a></li>
						</ul>
					</form:form>
				</c:when>
				<c:otherwise>
					<ul class="nav navbar-nav navbar-right">
				        <li><a href="<c:url value="/user/signup" />"><span class="glyphicon glyphicon-user"></span>&nbsp;&nbsp;<spring:message code="menu.signup"></spring:message></a></li>
				        <li><a href="<c:url value="/user/login" />"><span class="glyphicon glyphicon-log-in"></span>&nbsp;&nbsp;<spring:message code="menu.login"></spring:message></a></li>
				    </ul>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</nav>

<c:if test="${empty vertFiller}">
	<div class="container verticalFiller"></div>
</c:if>
