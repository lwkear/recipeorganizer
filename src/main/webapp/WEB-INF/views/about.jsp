<!DOCTYPE html>
<html>
<head>


<title>About</title>

<%@include file="common/head.jsp" %>
<%-- <link href="<c:url value="/resources/css/layout.css" />" rel="stylesheet"> --%>

<style type="text/css">
html,body {
	margin:0;
	padding:0;
	height:100%; /* needed for container min-height */
	background:gray;
	
	/*font-family:arial,sans-serif;
	font-size:small;
	color:#666; */
}

/* h1 { 
	font:1.5em georgia,serif; 
	margin:0.5em 0;
}
 */
/* h2 {	font:1.25em georgia,serif; 
	margin:0 0 0.5em;
}
	h1, h2, a {
		color:orange;
	}

p { 
	line-height:1.5; 
	margin:0 0 1em;
} */

div#container {
	position:relative; /* needed for footer positioning*/
	/* margin:0 auto; */ /* center, not in IE5 */
	/* width:750px; */
	margin-top:-50px;
	margin-bottom:-50px;
	background:#ffffff;
	
	height:auto !important; /* real browsers */
	/* height:100%; */ /* IE6: treaded as min-height*/

	min-height:100%; /* real browsers */
}

div#header {
	/* padding:1em; */
	background:#ddd;
	/* background:#ddd url("../csslayout.gif") 98% 10px no-repeat;
	border-bottom:6px double gray; */
}

div#content {
	padding:1em 1em 5em; /* bottom padding for footer */
	min-height: 100%;
    height: auto;	
}

div#footer {
	position:absolute;
	width:100%;
	bottom:0; /* stick to bottom */
	/* background:#ddd;
	border-top:6px double gray; */
}

div#footer p {
		/* padding:1em; */
		/* margin:0; */
}


 #wrap {
    min-height: 100%;
    height: auto;
    height: 100%;
    /* Negative indent footer by it's height */
    margin: 0 auto -60px;
    /* I found on my computer the line above leaves a scroll bar.  To get rid of it, I pushed the sticky footer up a bit with this line of code below
    margin: 0 auto -80px; */
}

#wrap .container {
    /* background-color: lightgreen; */
    
    /*these 2 lines below are from the web page that I linked in the answer.  They allow the container to fill the entire space of the parent container and parent div.  I also set the height of the parent div, and the html and body tags to height: 100% as per instructions on explanation in the link */
    min-height: 100%;
    height: auto;  /* this line takes care of "more than enough content problem"  */
}

</style>

</head>

<body class="body-color" role="document">


<%@include file="common/nav.jsp" %>


	<div class="container" id="container">

		<!-- <div id="header">
		</div> -->

		<div id="content">

			<div class="container">
			<div class="col-sm-12">

			<h2>Min-height</h2>
			<p>
				The #container element of this page has a min-height of 100%. That way, if the content requires more height than the viewport provides, the height of #content forces #container to become longer as well. Possible columns in #content can then be visualised with a background image on #container; divs are not table cells, and you don't need (or want) the fysical elements to create such a visual effect. If you're not yet convinced; think wobbly lines and gradients instead of straight lines and simple color schemes.
			</p>
			<h2>Relative positioning</h2>
			<p>
				Because #container has a relative position, #footer will always remain at its bottom; since the min-height mentioned above does not prevent #container from scaling, this will work even if (or rather especially when) #content forces #container to become longer.
			</p>
			<h2>Padding-bottom</h2>
			<p>
				Since it is no longer in the normal flow, padding-bottom of #content now provides the space for the absolute #footer. This padding is included in the scrolled height by default, so that the footer will never overlap the above content.
			</p>
			<p>
				Scale the text size a bit or resize your browser window to test this layout. The <a href="css/layout1.css">CSS file is over here</a>.
			</p>
			<h2>Min-height</h2>
			<p>
				The #container element of this page has a min-height of 100%. That way, if the content requires more height than the viewport provides, the height of #content forces #container to become longer as well. Possible columns in #content can then be visualised with a background image on #container; divs are not table cells, and you don't need (or want) the fysical elements to create such a visual effect. If you're not yet convinced; think wobbly lines and gradients instead of straight lines and simple color schemes.
			</p>
			<h2>Relative positioning</h2>
			<p>
				Because #container has a relative position, #footer will always remain at its bottom; since the min-height mentioned above does not prevent #container from scaling, this will work even if (or rather especially when) #content forces #container to become longer.
			</p>
			<h2>Padding-bottom</h2>
			<p>
				Since it is no longer in the normal flow, padding-bottom of #content now provides the space for the absolute #footer. This padding is included in the scrolled height by default, so that the footer will never overlap the above content.
			</p>
			<p>
				Scale the text size a bit or resize your browser window to test this layout. The <a href="css/layout1.css">CSS file is over here</a>.
			</p>
			
			</div>
			</div>

		<!-- </div>

		<div id="footer"> -->

<%-- <%@include file="common/footer.jsp" %> --%>

		</div>
	</div>
	
<%@include file="common/nav.jsp" %>
	
</body>

</html>

<%-- <body class="body-color" role="document">
	<div id="wrap">

<%@include file="common/nav.jsp" %>
	
		<div class="container">
			<div class="col-sm-12">
			<h2>Min-height</h2>
			<p>
				The #container element of this page has a min-height of 100%. That way, if the content requires more height than the viewport provides, the height of #content forces #container to become longer as well. Possible columns in #content can then be visualised with a background image on #container; divs are not table cells, and you don't need (or want) the fysical elements to create such a visual effect. If you're not yet convinced; think wobbly lines and gradients instead of straight lines and simple color schemes.
			</p>
			<h2>Relative positioning</h2>
			<p>
				Because #container has a relative position, #footer will always remain at its bottom; since the min-height mentioned above does not prevent #container from scaling, this will work even if (or rather especially when) #content forces #container to become longer.
			</p>
			<h2>Padding-bottom</h2>
			<p>
				Since it is no longer in the normal flow, padding-bottom of #content now provides the space for the absolute #footer. This padding is included in the scrolled height by default, so that the footer will never overlap the above content.
			</p>
			<p>
				Scale the text size a bit or resize your browser window to test this layout. The <a href="css/layout1.css">CSS file is over here</a>.
			</p>
			<p>
				<a href="../css.html">Back to CSS Examples</a>
			</p>
			</div>
	</div>

<%@include file="common/footer.jsp" %>
	
</body>
 --%>


<!-- <body>

	<div id="container">

		<div id="header">
			<h1>CSS layout: 100% height with header and footer</h1> 
			<p>Sometimes things that used to be really simple with tables can still appear pretty hard with CSS. This layout for instance would consist of 3 cells; two with a fixed height, and a third one in the center filling up the remaining space. Using CSS, however, you have to take a different approach.</p>
		</div>

		<div id="content">
			<h2>Min-height</h2>
			<p>
				The #container element of this page has a min-height of 100%. That way, if the content requires more height than the viewport provides, the height of #content forces #container to become longer as well. Possible columns in #content can then be visualised with a background image on #container; divs are not table cells, and you don't need (or want) the fysical elements to create such a visual effect. If you're not yet convinced; think wobbly lines and gradients instead of straight lines and simple color schemes.
			</p>
			<h2>Relative positioning</h2>
			<p>
				Because #container has a relative position, #footer will always remain at its bottom; since the min-height mentioned above does not prevent #container from scaling, this will work even if (or rather especially when) #content forces #container to become longer.
			</p>
			<h2>Padding-bottom</h2>
			<p>
				Since it is no longer in the normal flow, padding-bottom of #content now provides the space for the absolute #footer. This padding is included in the scrolled height by default, so that the footer will never overlap the above content.
			</p>
			<p>
				Scale the text size a bit or resize your browser window to test this layout. The <a href="css/layout1.css">CSS file is over here</a>.
			</p>
			<p>
				<a href="../css.html">Back to CSS Examples</a>
			</p>
		</div>

		<div id="footer">
			<p>
				This footer is absolutely positioned to bottom:0; of  #container. The padding-bottom of #content keeps me from overlapping it when the page is longer than the viewport.
			</p>
		</div>
	</div>
</body>
 -->
 
 
<%--  <body class="body-color" role="document">
	<div  id="container">

<%@include file="common/nav.jsp" %>

	<div class="container-white" id="content">	
		<div>
			<div class="col-sm-12">
			<div class="page-header">
				<h3><spring:message code="about.title"></spring:message></h3>
			</div>
			</div>
		</div>
	</div>

<%@include file="common/footer.jsp" %>

	</div>
	
</body>
 --%> 
 
 
 
 
<!--  <div id="wrap">
  Begin page content
  <div class="container">
    <div class="page-header">
      <h1>Sticky footer</h1>
    </div>
    <p class="lead">This is the sticky footer template taken from the Bootstrap web site.</p>
    <p class="lead">How can we make this green .container div the full height of its parent #wrap div?</p>
    <p class="lead">The solution I found is the url below.</p>
    <p class="lead">http://v1.reinspire.net/blog/2005/10/11/css_vertical_stretch/</p>
  </div>
</div>
<div id="footer">
  <div class="container">
    <h2> REAL Sticky footer is down here </h2>
  </div>
</div> -->

<!-- #wrap .container {
    background-color: lightgreen;
    
    /*these 2 lines below are from the web page that I linked in the answer.  They allow the container to fill the entire space of the parent container and parent div.  I also set the height of the parent div, and the html and body tags to height: 100% as per instructions on explanation in the link */
    min-height: 100%;
    height: auto;  /* this line takes care of "more than enough content problem"  */
}
/* Sticky footer styles
      -------------------------------------------------- */
 html, body {
    height: 100%;
    background-color: #dedede; /* added to see effects*/
    padding: 0;  /* added to */
    margin: 0;  /*remove any unwanted padding and margin*/
}
/* Wrapper for page content to push down footer */
 #wrap {
    min-height: 100%;
    height: auto;
    height: 100%;
    /* Negative indent footer by it's height */
    margin: 0 auto -60px;
    /* I found on my computer the line above leaves a scroll bar.  To get rid of it, I pushed the sticky footer up a bit with this line of code below
    margin: 0 auto -80px; */
}

/* the class below was added to ensure there was no space at the top.  Line height on H1 was adding space, but a padding of 1px to the h1 tag ensured that the light green filled the space that the h1 line height was taking(comment it out if you want to see what it did) */
.page-header {
  padding: 1px;
}
/* Set the fixed height of the footer here */
 #push, #footer {
    height: 60px;
}
#footer {
    background-color: #333;
    color: #fff;
    /* added to show a difference in the footer and main content */
}
/* Lastly, apply responsive CSS fixes as necessary */
 @media (max-width: 767px) {
    #footer {
        margin-left: -20px;
        margin-right: -20px;
        padding-left: 20px;
        padding-right: 20px;
    }
}
/* Custom page CSS
      -------------------------------------------------- */

/* Not required for template or sticky footer method. */
 .container {
    width: auto;
    max-width: 680px;
}
.container .credit {
    margin: 20px 0;
} -->