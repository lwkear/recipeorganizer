<%@include file="head.jsp"%>

<script>
	function submitLogoutForm() {
		document.forms["logoutForm"].submit();
	}

	$(window).on('load', function() {
		//getSessionTimeout();
	});

	/* $(window).on('unload', function() {
		 
	}); */	
</script>

<!-- Fixed navbar -->
<nav class="navbar navbar-inverse navbar-fixed-top">
	<div class="container">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="<c:url value="/home" />">RecipeOrganizer</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<sec:authorize var="isAuth" access="isAuthenticated()"/>
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
			<ul class="nav navbar-nav">
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Recipes<span class="caret"></span></a>
					<ul class="dropdown-menu" role="menu">
						<li><a href="<c:url value="/recipe/addRecipe" />">Add a Recipe</a></li>
						<li><a href="<c:url value="/recipe/listRecipes" />">Recipe List</a></li>
					</ul>
				</li>
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Admin<span class="caret"></span></a>
					<ul class="dropdown-menu" role="menu">
						<li><a href="<c:url value="/admin/category" />">Categories</a></li>
					</ul>
				</li>
				<li><a href="<c:url value="/about" />">About</a></li>
			</ul>
		</div>
	</div>
</nav>
<%-- <div>
<h3>Session ID: ${pageContext.session.id}</h3>
</div> --%>

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