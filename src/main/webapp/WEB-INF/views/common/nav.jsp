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
<%-- <sec:authorize var="isAdmin" access="hasAnyRole('ADMIN')"/>
<sec:authorize var="isEditor" access="hasAnyRole('EDITOR','ADMIN')"/>
<sec:authorize var="isAuthor" access="hasAnyRole('AUTHOR','EDITOR','ADMIN')"/>
<sec:authorize var="isGuest" access="hasAnyRole('GUEST','EDITOR','AUTHOR','ADMIN')"/> --%>
<sec:authorize var="isAdmin" access="hasAuthority('ADMIN')"/>
<sec:authorize var="isEditor" access="hasAuthority('EDITOR')"/>
<sec:authorize var="isAuthor" access="hasAuthority('AUTHOR')"/>
<sec:authorize var="isGuest" access="hasAuthority('GUEST')"/>

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
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Recipes<span class="caret"></span></a>
					<ul class="dropdown-menu" role="menu">
						<c:if test="${isAuthor}">
						<li><a href="<c:url value="/recipe/addRecipe" />">Add a Recipe</a></li>
						</c:if>
						<c:if test="${isGuest}">
						<li><a href="<c:url value="/recipe/listRecipes" />">Recipe List</a></li>
						</c:if>
					</ul>
				</li>
				</c:if>
				<c:if test="${isAdmin}">
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Admin<span class="caret"></span></a>
					<ul class="dropdown-menu" role="menu">
						<li><a href="<c:url value="/admin/category" />">Categories</a></li>
					</ul>
				</li>
				</c:if>
				<li><a href="<c:url value="/about" />">About</a></li>
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
						        	<li><a href="<c:url value="/user/profile" />">Profile</a></li>
						        	<li><a href="<c:url value="/user/changePassword" />">Change Password</a></li>
						        </ul>
						    </li>
					        <li><a href="javascript:submitLogoutForm()"><span class="glyphicon glyphicon-log-out"></span>&nbsp;&nbsp;Logout</a></li>
						</ul>
					</form:form>
				</c:when>
				<c:otherwise>
					<ul class="nav navbar-nav navbar-right">
				        <li><a href="<c:url value="/user/signup" />"><span class="glyphicon glyphicon-user"></span>&nbsp;&nbsp;Sign Up</a></li>
				        <li><a href="<c:url value="/user/login" />"><span class="glyphicon glyphicon-log-in"></span>&nbsp;&nbsp;Login</a></li>
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