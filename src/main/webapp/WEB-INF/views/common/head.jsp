<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">

<!-- JQueryUI core CSS -->	
<link href="<c:url value="/resources/jqueryui-smoothness/jquery-ui.css" />" rel="stylesheet">
<link href="<c:url value="/resources/jqueryui-smoothness/jquery-ui.theme.css" />" rel="stylesheet">
<link href="<c:url value="/resources/jqueryui-smoothness/jquery-ui.structure.css" />" rel="stylesheet">
	
<!-- Bootstrap core CSS -->	
<link href="<c:url value="/resources/bootstrap/css/bootstrap.css" />" rel="stylesheet">
<!-- Bootstrap theme -->	
<link href="<c:url value="/resources/bootstrap/css/bootstrap-theme.css" />" rel="stylesheet">
<!-- Bootstrap-tagsinput -->	
<link href="<c:url value="/resources/bootstrap-tagsinput/bootstrap-tagsinput.css" />" rel="stylesheet">

<!-- Typeahead CSS -->	
<link href="<c:url value="/resources/typeahead/typeahead.css" />" rel="stylesheet">

<!-- Custom styles for this app -->
<link href="<c:url value="/resources/css/layout.css" />" rel="stylesheet">

<!-- Security tags -->
<sec:csrfMetaTags />
    
<!-- Custom javascript for this app -->
<%@include file="js.jsp" %>
