<%@include file="head.jsp"%>

<c:set var="now" value="<%=new java.util.Date()%>" />

<style type="text/css">
/* .navbar-center {
    width: 100%;
    text-align: center;
    > li {
      float: none;
      display: inline-block;
    }
}

.pull-center {
 	float: none;
    display: inline-block;
} */

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


<%-- <div class="footer">
	<div class="container">
		<div class="row">
			<div class="col_sm-12">
			<div class="text-muted text-center col-sm-offset-5" style="margin:0; border-bottom: none"><small>&copy;&nbsp;<fmt:formatDate pattern="yyyy" value="${now}"/>&nbsp;&nbsp;Larry Kear&nbsp;&nbsp;All Rights Reserved.</small></div>
			<div class="text-muted text-center" style="margin-top:0; margin-bottom:0; border-bottom: none"><small>&copy;&nbsp;<fmt:formatDate pattern="yyyy" value="${now}"/>&nbsp;&nbsp;Larry Kear&nbsp;&nbsp;All Rights Reserved.</small></div>
			<div class="text-muted col-sm-offset-2" style="margin:0; border-bottom: none"><a class="btn btn-default" href="<c:url value="/home"></c:url>" role="button">Home</a></div>
			<div class="" style="margin:0; border-bottom: none"><a class="btn btn-default" href="<c:url value="/about"></c:url>" role="button">About</a></div>
			</div>
		</div>
	</div>
</div> --%>

<nav class="container navbar navbar-default navbar-fixed-bottom">
    <p class="navbar-center">&copy;&nbsp;<fmt:formatDate pattern="yyyy" value="${now}"/>&nbsp;&nbsp;Larry Kear&nbsp;&nbsp;All Rights Reserved.</p>
    <div class="navbar-collapse collapse">
    	<ul class="nav navbar-nav navbar-right">
	        <%-- <li><a href="<c:url value="/user/signup"/>"><img src=<c:url value="/resources/us.png" /> class="img-responsive" style="width:24px;height:24x;border:0;"></a></li>
	        <li><a href="<c:url value="/user/signup"/>"><img src=<c:url value="/resources/fr.png" /> class="img-responsive" style="width:24px;height:24px;border:0;"></a></li> --%>
	        <%-- <li><a href="<c:url value="/user/signup"/>"><img src=<c:url value="/resources/us.png" /> class="img-responsive" style="padding=0"></a></li>
	        <li><a href="<c:url value="/user/signup"/>"><img src=<c:url value="/resources/fr.png" /> class="img-responsive" style="height=none"></a></li> --%>
	        <li><a href="?lang=en"><img src=<c:url value="/resources/us.png" /> class="img-responsive"></a></li>
	        <li><a href="?lang=fr"><img src=<c:url value="/resources/fr.png" /> class="img-responsive"></a></li>
	    </ul>
    </div>
</nav>


<!-- <nav class="navbar navbar-default navbar-fixed-bottom"> -->
<!-- <nav class="navbar navbar-fixed-bottom">
  <div class="navbar-header">
    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
    </button>    
  </div>
  <a class="navbar-brand" href="#">Brand</a>  
  <div class="navbar-collapse collapse">
    <ul class="nav navbar-nav navbar-left">
        <li><a href="#">Left</a></li>
        <li><a href="#about">Left</a></li>
    </ul>
    <ul class="nav navbar-nav navbar-right">
      <li><a href="#about">Right</a></li>
      <li><a href="#contact">Right</a></li>
    </ul>
  </div>
</nav> -->


<!-- <nav class="navbar navbar-default" role="navigation">
  <div class="container-fluid">
    Brand and toggle get grouped for better mobile display
    <div class="navbar-header">
      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="#">Brand Logo</a>
    </div>

    Collect the nav links, forms, and other content for toggling
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
      <ul class="nav navbar-nav navbar-center">
        <li class="pull-left"><a href="#">Dashboard</a></li>
        <li style="float: none;display: inline-block;"><a href="#">Stories</a></li>
        <li style="float: none;display: inline-block;"><a href="#">Videos</a></li>
        <li style="float: none;display: inline-block;"><a href="#">Photos</a></li>

        <li><a href="#">Stories</a></li>
        <li><a href="#">Videos</a></li>
        <li><a href="#">Photos</a></li>

        <li class="pull-center"><a href="#">Stories</a></li>
        <li class="pull-center"><a href="#">Videos</a></li>
        <li class="pull-center"><a href="#">Photos</a></li>
        <li class="social pull-right"><a href="#">Social Links</a></li>
      </ul>
    </div>/.navbar-collapse
  </div>/.container-fluid
</nav> -->