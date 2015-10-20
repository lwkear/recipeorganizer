<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<sec:authorize var="isAuth" access="isAuthenticated()"/>
<sec:authorize var="isAdmin" access="hasAuthority('ADMIN')"/>
<sec:authorize var="isEditor" access="hasAnyAuthority('EDITOR','ADMIN')"/>
<sec:authorize var="isAuthor" access="hasAnyAuthority('AUTHOR','EDITOR','ADMIN')"/>
<sec:authorize var="isGuest" access="hasAnyAuthority('GUEST','AUTHOR','EDITOR','ADMIN')"/>

<c:if test="${isAuth}">
	<sec:authentication var="email" property="principal.username" />
	<sec:authentication var="firstname" property="principal.firstName"/>
	<sec:authentication var="lastname" property="principal.lastName" />
	<!-- TODO: GUI: figure out how to retrieve the user profile within the principal security object -->
	<%-- <sec:authentication var="city" property="principal.userProfile.city" /> --%>
</c:if>


<nav class="navbar navbar-inverse navbar-default" style="border-top:0; border-bottom:0; margin-bottom:0;z-index:100">
	<div class="container">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="<c:url value="/home" />">RecipeOrganizer</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<c:if test="${isAuth}">
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
							<spring:message code="menu.recipe"></spring:message><span class="caret"></span></a>
					<ul class="dropdown-menu" role="menu">
						<c:if test="${isAuthor}">
						<li><a href="<c:url value="/recipe/addRecipe" />"><spring:message code="menu.addrecipe"></spring:message></a></li>
						</c:if>
						<c:if test="${isGuest}">
						<li><a href="<c:url value="/recipe/listRecipes" />"><spring:message code="menu.listrecipe"></spring:message></a></li>
						</c:if>
					</ul>
				</li>
				</c:if>
				<c:if test="${isAdmin}">
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
							<spring:message code="menu.admin"></spring:message><span class="caret"></span></a>
					<ul class="dropdown-menu" role="menu">
						<li><a href="<c:url value="/admin/category" />"><spring:message code="menu.categories"></spring:message></a></li>
					</ul>
				</li>
				</c:if>
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
							<spring:message code="menu.info"></spring:message><span class="caret"></span></a>
					<ul class="dropdown-menu" role="menu">
						<li><a href="#"><spring:message code="menu.faq"></spring:message></a></li>
						<li><a href="#"><spring:message code="menu.contact"></spring:message></a></li>							
						<li><a href="<c:url value="/about" />"><spring:message code="menu.about"></spring:message></a></li>
					</ul>
				</li>
			</ul>
			<c:choose>
				<c:when test="${isAuth}">
					<c:url var="logoutUrl" value="/logout"/>
					<form:form id="logoutForm" action="${logoutUrl}" method="post">
						<ul class="nav navbar-nav navbar-right">
							<li class="dropdown">
								<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><span class="glyphicon glyphicon-edit"></span>&nbsp;&nbsp;${firstname}<span class="caret"></span></a>
						        <ul class="dropdown-menu" role="menu">
						        	<li><a href="<c:url value="/user/profile" />"><spring:message code="menu.profile"></spring:message></a></li>
						        	<li><a href="<c:url value="/user/changePassword" />"><spring:message code="menu.changepassword"></spring:message></a></li>
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
