<!DOCTYPE html>
<html>
<head>

<title>Dashboard</title>

<%@include file="../common/head.jsp" %>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12">
			<div class="page-header">
				<%-- <h3><spring:message code="about.title"></spring:message></h3> --%>
				<h3>
					<span><img src="<c:url value="/user/avatar?filename=${user.userProfile.avatar}"/>" style="width:50px;height:50px;"/></span>
					Welcome,&nbsp;${user.firstName}
				</h3>
			</div>
		</div>
	</div>
    
<%@include file="../common/footer.jsp" %>	
	
</body>
</html>
