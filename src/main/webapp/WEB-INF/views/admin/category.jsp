<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="category.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="category.title"></spring:message></h3>
			</div>
			<div class="row">			
		    <form:form class="form-horizontal" role="form" action="category" method="post" modelAttribute="category">
		    	<form:hidden id="selID" path="id" value="0"/>
				<div class="form-group">
		            <label class="control-label col-sm-4" for="inputCategory"><spring:message code="category.list"></spring:message>:</label>
		            <div class="col-sm-3">
		                <select class="form-control" id="inputCategory" onChange="catSelect()">
		                	<c:forEach items="${categoryList}" var="category" varStatus="loopCounter">
		                		<option data-id="${category.id}"><c:out value="${category.name}" /></option>
		                	</c:forEach>
		                </select>
		           </div>
		        </div>           
		    	<div class="form-group">
		            <label class="control-label col-sm-4" for="inputName"><spring:message code="category.description"></spring:message>:</label>
		            <div class="col-sm-3">
		                <form:input type="text" class="form-control" id="inputName" path="name" />
		            </div>
		        </div>
		        <br>
		        <div class="form-group">
		            <div class="col-sm-offset-4 col-sm-3 text-center">
		                <form:button type="submit" class="btn btn-primary pull-left" name="save"><spring:message code="common.save"></spring:message></form:button>
		                <form:button type="submit" class="btn btn-primary" name="delete"><spring:message code="common.delete"></spring:message></form:button>
		                <input type="reset" class="btn btn-default pull-right" value="Reset">
		            </div>
		        </div>
			</form:form>
		</div>
	</div>
</div>

<%@include file="../common/footer.jsp" %>

<script type="text/javascript">
function catSelect() {
	var catSel = document.getElementById("inputCategory");
	var selected = catSel.options[catSel.selectedIndex];
	var catID = selected.getAttribute("data-id")
		
	var id = document.getElementById("selID");
	id.value = catID;

	var desc = document.getElementById("inputName");
	desc.value = selected.value;
}
</script>

</body>
</html>
