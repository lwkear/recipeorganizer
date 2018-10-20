<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="now" value="<%=new java.util.Date()%>" />

<div class="clearfix spacer-vert-sm">
</div>
<footer class="footer">
	<nav class="navbar navbar-default">
	    <p class="navbar-center">&copy;&nbsp;<fmt:formatDate pattern="yyyy" value="${now}"/>&nbsp;&nbsp;Larry Kear&nbsp;&nbsp;
	    		<spring:message code="footer.rights"></spring:message></p>
		    <div class="navbar-collapse collapse">
	    	<ul class="nav navbar-nav navbar-right">
		        <li><a href="?language=en"><img src=<c:url value="/resources/images/us.png" /> class="img-responsive"></a></li>
		        <li><a href="?language=fr"><img src=<c:url value="/resources/images/fr.png" /> class="img-responsive"></a></li>
		    </ul>
	    </div>
	</nav>
</footer>

<!-- Common dialogs, messages a javascript for this app -->
<%@include file="dialogs.jsp" %>
<%@include file="messages.jsp" %>
<%@include file="js.jsp" %>

<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-77863703-1', 'auto');
  ga('send', 'pageview');

</script>