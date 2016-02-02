<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="now" value="<%=new java.util.Date()%>" />

<footer class="footer">
	<nav class="navbar navbar-default navbar-inverse" style="margin-bottom: 0; margin-top: 0; border-top:0; border-bottom:0">
	    <p class="navbar-center" style="color:#ffffff">&copy;&nbsp;<fmt:formatDate pattern="yyyy" value="${now}"/>&nbsp;&nbsp;Larry Kear&nbsp;&nbsp;
	    		<spring:message code="footer.rights"></spring:message></p>
		    <div class="navbar-collapse collapse">
	    	<ul class="nav navbar-nav navbar-right">
		        <li><a href="?lang=en"><img src=<c:url value="/resources/us.png" /> class="img-responsive"></a></li>
		        <li><a href="?lang=fr"><img src=<c:url value="/resources/fr.png" /> class="img-responsive"></a></li>
		    </ul>
	    </div>
	</nav>
</footer>

<!-- Common dialogs, messages a javascript for this app -->
<%@include file="dialogs.jsp" %>
<%@include file="messages.jsp" %>
<%@include file="js.jsp" %>