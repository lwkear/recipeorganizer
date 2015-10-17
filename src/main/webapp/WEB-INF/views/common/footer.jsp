<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="now" value="<%=new java.util.Date()%>" />

<div id="footer">
	<nav class="navbar navbar-default" style="margin-bottom: 0">
		<div class="container">
		    <p class="navbar-center">&copy;&nbsp;<fmt:formatDate pattern="yyyy" value="${now}"/>&nbsp;&nbsp;Larry Kear&nbsp;&nbsp;All Rights Reserved.</p>
		    <div class="navbar-collapse collapse">
		    	<ul class="nav navbar-nav navbar-right">
			        <li><a href="?lang=en"><img src=<c:url value="/resources/us.png" /> class="img-responsive"></a></li>
			        <li><a href="?lang=fr"><img src=<c:url value="/resources/fr.png" /> class="img-responsive"></a></li>
			    </ul>
		    </div>
		</div>
	</nav>
</div>
