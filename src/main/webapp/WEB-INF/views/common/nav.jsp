<%@include file="head.jsp"%>

<!-- <style type="text/css">
html {
  position: relative;
  min-height: 100%;
}
body {
  /* Margin bottom by footer height */
  margin-bottom: 80px;
}
.footer {
  position: absolute;
  bottom: 0;
  width: 100%;
  /* Set the fixed height of the footer here */
  height: 80px;
  background-color: #f5f5f5;
}
</style> -->
   
<script>
	function submitLogoutForm() {
		document.forms["logoutForm"].submit();
	}

	$(window).on('load', function() {
		getSessionTimeout();
	});
</script>

<sec:authorize var="isAuth" access="isAuthenticated()"/>
<sec:authorize var="isAdmin" access="hasAuthority('ADMIN')"/>
<sec:authorize var="isEditor" access="hasAnyAuthority('EDITOR','ADMIN')"/>
<sec:authorize var="isAuthor" access="hasAnyAuthority('AUTHOR','EDITOR','ADMIN')"/>
<sec:authorize var="isGuest" access="hasAnyAuthority('GUEST','AUTHOR','EDITOR','ADMIN')"/>

<nav class="navbar navbar-inverse navbar-fixed-top">
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
					<sec:authentication var="secUser" property="principal.firstName"/>
					<c:url var="logoutUrl" value="/logout"/>
					<form:form id="logoutForm" action="${logoutUrl}" method="post">
						<ul class="nav navbar-nav navbar-right">
							<li class="dropdown">
								<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><span class="glyphicon glyphicon-edit"></span>&nbsp;&nbsp;${secUser}<span class="caret"></span></a>
						        <ul class="dropdown-menu" role="menu">
						        	<li><a href="<c:url value="/user/profile" />"><spring:message code="menu.profile"></spring:message></a></li>
						        	<li><a href="<c:url value="/user/changePassword" />"><spring:message code="menu.changepassword"></spring:message></a></li>
						        </ul>
						    </li>
					        <li><a href="javascript:submitLogoutForm()"><span class="glyphicon glyphicon-log-out"></span>&nbsp;&nbsp;Logout</a></li>
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

<div class="modal fade" id="sessionTimeout" role="dialog">
	<div class="modal-dialog modal-sm">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">Session Timeout</h4>
			</div>
			<div class="modal-body">
				<div class="text-center" id="timeoutText"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
				<button type="button" class="btn btn-primary" data-dismiss="modal" onclick="setSessionTimeout()">OK</button>
			</div>
		</div>
	</div>
</div>