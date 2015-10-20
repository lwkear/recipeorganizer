<!DOCTYPE html>
<html>
<head>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="">

    <title>Sticky Footer Navbar Template for Bootstrap</title>

    <!-- Bootstrap core CSS -->
    <!-- <link href="../../dist/css/bootstrap.min.css" rel="stylesheet"> -->
    <link href="<c:url value="/resources/bootstrap/css/bootstrap.css" />" rel="stylesheet">

    <!-- Custom styles for this template -->
    <!-- <link href="sticky-footer-navbar.css" rel="stylesheet"> -->

    <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
    <!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
    <!-- <script src="../../assets/js/ie-emulation-modes-warning.js"></script> -->

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

<style type="text/css">
html {
	/* added */
	/* margin:0; */
	/* padding:0; */
	/* height:100%; */
	/* original */
	position: relative;
	min-height: 100%;
}
body {
	/* added */
	/* height:100%; */
	/* min-height: 100%; */
	/* margin:0; */
	/* padding:0; */
	/* original */ 
	/* Margin bottom by footer height */
	margin-bottom: 50px;
	background:gray;
	box-sizing: border-box;
}
.container-white {
	/* added */
	/* padding-bottom:100%; */
	/* original */
	position:relative; /* needed for footer positioning*/
	margin-top:50px;
	background:#ffffff;
	height:100%;
	min-height:100%;
	box-sizing: border-box;
}
.footer {
	/* added */
	/* position: relative; */
	/* original */
	position: absolute;
	bottom: 0;
	width: 100%;
	/* Set the fixed height of the footer here */
	height: 50px;
	background-color: #f5f5f5;
}
.verticalFiller {
    display:block;
    position:absolute;
    height:100%;
    bottom:0;
    top:0;
    left:0;
    right:0;
    z-index: -9999;
    background-color: #ffffff;
}

/* Custom page CSS
-------------------------------------------------- */
/* Not required for template or sticky footer method. */

/* body > .container {
  padding: 60px 15px 0;
}
.container .text-muted {
  margin: 20px 0;
}

.footer > .container {
  padding-right: 15px;
  padding-left: 15px;
}

code {
  font-size: 80%;
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


</head>

 <body>

<%@include file="common/nav.jsp" %>

	<div class="container verticalFiller">
	</div>

    <!-- Begin page content -->
    <div class="container container-white">
      <div class="page-header">
        <h1>Sticky footer with fixed navbar</h1>
      </div>
      <p class="lead">Pin a fixed-height footer to the bottom of the viewport in desktop browsers with this custom HTML and CSS. A fixed navbar has been added with <code>padding-top: 60px;</code> on the <code>body > .container</code>.</p>
      <p>Back to <a href="../sticky-footer">the default sticky footer</a> minus the navbar.</p>
        <p>Some content1</p>
        <p>Some content2</p>
        <p>Some content3</p>
        <p>Some content4</p>
        <p>Some content5</p>
        <p>Some content6</p>
        <p>Some content7</p>
        <p>Some content8</p>
        <p>Some content9</p>
        <p>Some content10</p>
        <!-- <p>Some content1</p>
        <p>Some content2</p>
        <p>Some content3</p>
        <p>Some content4</p>
        <p>Some content5</p>
        <p>Some content6</p>
        <p>Some content7</p>
        <p>Some content8</p>
        <p>Some content9</p>
        <p>Some content20</p>
        <p>Some content1</p>
        <p>Some content2</p>
        <p>Some content3</p>
        <p>Some content4</p>
        <p>Some content5</p>
        <p>Some content6</p>
        <p>Some content7</p>
        <p>Some content8</p>
        <p>Some content9</p>
        <p>Some content30</p> -->
    </div>

<%@include file="common/footer.jsp" %>

    <!-- <footer class="footer">
      <div class="container">
        <p class="text-muted">Place sticky footer content here.</p>
      </div>
    </footer> -->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="<c:url value="/resources/js/jquery-2.1.3.js" />"></script>
    <script src="<c:url value="/resources/bootstrap/js/bootstrap.js" />"></script>
    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <!-- <script src="../../assets/js/ie10-viewport-bug-workaround.js"></script> -->
  </body>
</html>


    <!-- Fixed navbar -->
    <!-- <nav class="navbar navbar-default navbar-fixed-top">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">Project name</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">
          <ul class="nav navbar-nav">
            <li class="active"><a href="#">Home</a></li>
            <li><a href="#about">About</a></li>
            <li><a href="#contact">Contact</a></li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Dropdown <span class="caret"></span></a>
              <ul class="dropdown-menu">
                <li><a href="#">Action</a></li>
                <li><a href="#">Another action</a></li>
                <li><a href="#">Something else here</a></li>
                <li role="separator" class="divider"></li>
                <li class="dropdown-header">Nav header</li>
                <li><a href="#">Separated link</a></li>
                <li><a href="#">One more separated link</a></li>
              </ul>
            </li>
          </ul>
        </div>/.nav-collapse
      </div>
    </nav> -->

