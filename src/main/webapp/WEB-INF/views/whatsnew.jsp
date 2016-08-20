<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp" %>

<title><spring:message code="menu.whatsnew"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12 title-bar">
			<div class="page-header">
				<h3><spring:message code="title.whatsnew"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">
			<div class="row">
				<c:forEach var="release" items="${releases}" varStatus="loop">
					<div class="col-sm-12">
						<h4><spring:message code="whatsnew.releasedate"></spring:message> <fmt:formatDate type="date" value="${release.releaseDate}"/></h4>
					</div>
					<div class="col-sm-12">
						<ul>
						<c:forEach var="description" items="${release.descriptions}" varStatus="loop">
							<li style="margin-top: 10px">${description}</li>
						</c:forEach>
						</ul>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>
   
<%@include file="common/footer.jsp" %>	
	
</body>
</html>
