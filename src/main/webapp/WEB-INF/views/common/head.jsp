<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="custom" prefix="custom" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<meta charset="utf-8">
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1">

<link rel="icon" type="image/png" href="<c:url value="/resources/images/favicon-16x16.png"/>" sizes="16x16"/>
<link rel="icon" type="image/png" href="<c:url value="/resources/images/favicon-32x32.png"/>" sizes="32x32"/>
<link rel="icon" type="image/png" href="<c:url value="/resources/images/favicon-48x48.png"/>" sizes="48x48"/>

<!-- JQueryUI core CSS -->	
<%-- <link href="<c:url value="/resources/jqueryui-smoothness/jquery-ui.css" />" rel="stylesheet">
<link href="<c:url value="/resources/jqueryui-smoothness/jquery-ui.theme.css" />" rel="stylesheet">
<link href="<c:url value="/resources/jqueryui-smoothness/jquery-ui.structure.css" />" rel="stylesheet">
<link href="<c:url value="/resources/jqueryui-notheme/jquery-ui.css" />" rel="stylesheet">
<link href="<c:url value="/resources/jqueryui-notheme/jquery-ui.structure.css" />" rel="stylesheet"> --%>

<link href="<c:url value="/resources/jqueryui-smoothness/jquery-ui.min.css" />" rel="stylesheet">
<link href="<c:url value="/resources/jqueryui-smoothness/jquery-ui.theme.min.css" />" rel="stylesheet">
<link href="<c:url value="/resources/jqueryui-smoothness/jquery-ui.structure.min.css" />" rel="stylesheet">
<link href="<c:url value="/resources/jqueryui-notheme/jquery-ui.min.css" />" rel="stylesheet">
<link href="<c:url value="/resources/jqueryui-notheme/jquery-ui.structure.min.css" />" rel="stylesheet">
	
<!-- Bootstrap -->	
<link href="<c:url value="/resources/bootstrap/css/bootstrap.css" />" rel="stylesheet">
<link href="<c:url value="/resources/bootstrap/css/bootstrap-theme.css" />" rel="stylesheet">

<%-- <link href="<c:url value="/resources/bootstrap/css/bootstrap.min.css" />" rel="stylesheet">
<link href="<c:url value="/resources/bootstrap/css/bootstrap-theme.min.css" />" rel="stylesheet"> --%>
<link href="<c:url value="/resources/selectize/css/selectize.css" />" rel="stylesheet">
<link href="<c:url value="/resources/selectize/css/selectize.bootstrap3.css" />" rel="stylesheet">
<link href="<c:url value="/resources/bootstrap-multiselect/css/bootstrap-multiselect.css" />" rel="stylesheet">

<!-- Typeahead CSS -->	
<link href="<c:url value="/resources/typeahead/typeahead.css" />" rel="stylesheet">

<!-- Datatables CSS -->
<%-- <link href="<c:url value="/resources/DataTables/css/dataTables.foundation.css" />" rel="stylesheet"> --%>
<link href="<c:url value="/resources/DataTables/css/dataTables.bootstrap.css" />" rel="stylesheet">
<%-- <link href="<c:url value="/resources/DataTables/css/dataTables.bootstrap.min.css" />" rel="stylesheet"> --%>

<!-- Custom styles for this app -->
<link href="<c:url value="/resources/css/layout.css" />" rel="stylesheet">

<!-- Security tags -->
<sec:csrfMetaTags />
