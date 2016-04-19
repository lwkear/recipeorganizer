<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp" %>

<title><spring:message code="menu.faq"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<!-- http://getbootstrap.com/examples/dashboard/dashboard.css -->

<body role="document">

<%@include file="common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12 title-bar">
			<div class="page-header">
				<h3><spring:message code="menu.faq"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">
			<div class="row">
				<div class="col-sm-2">
					<ul class="nav">
						<li><a href="#">Reports</a></li>
						<li><a href="#">Reports</a></li>
						<li><a href="#">Reports</a></li>
						<li><a href="#">Reports</a></li>
					</ul>
				</div>
				<div class="col-sm-10"  style="border-left: 1px solid #000">
		            <div class="panel-heading">
		                <h5 class="panel-title">
		                	<a data-toggle="collapse" data-parent="#accordion" href="#panel1"><spring:message code="recipe.basics.title"></spring:message></a>
		                </h5>
		            </div>
		            <div id="panel1" class="panel-collapse collapse">
		            	<div class="panel-body">
		            		<span>Some content #1</span>
						</div>
					</div>
		            <div class="panel-heading">
		                <h5 class="panel-title">
		                	<a data-toggle="collapse" data-parent="#accordion" href="#panel2"><spring:message code="recipe.basics.title"></spring:message></a>
		                </h5>
		            </div>
		            <div id="panel2" class="panel-collapse collapse">
		            	<div class="panel-body">
		            		<span>Some content #2</span>
						</div>
					</div>
				</div>
			</div>
		</div>			
	</div>
   
   
<%@include file="common/footer.jsp" %>	
	
</body>
</html>