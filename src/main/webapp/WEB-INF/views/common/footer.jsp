<%@include file="head.jsp"%>

<c:set var="now" value="<%=new java.util.Date()%>" />

<style type="text/css">
.navbar-center
{
	padding: 15px 15px;
    position: absolute;
    width: 100%;
    left: 0;
    text-align: center;
    margin: auto;
    font-size: 85%;
}
</style>

<div class="footer">
<nav class="container navbar navbar-default navbar-fixed-bottom">
    <p class="navbar-center">&copy;&nbsp;<fmt:formatDate pattern="yyyy" value="${now}"/>&nbsp;&nbsp;Larry Kear&nbsp;&nbsp;All Rights Reserved.</p>
    <div class="navbar-collapse collapse">
    	<ul class="nav navbar-nav navbar-right">
	        <li><a href="?lang=en"><img src=<c:url value="/resources/us.png" /> class="img-responsive"></a></li>
	        <li><a href="?lang=fr"><img src=<c:url value="/resources/fr.png" /> class="img-responsive"></a></li>
	    </ul>
    </div>
</nav>
</div>