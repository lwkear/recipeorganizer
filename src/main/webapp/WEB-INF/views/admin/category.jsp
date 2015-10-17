<!DOCTYPE html>
<html>
<head>

	<%@include file="../common/head.jsp" %>
	
	<title>Category</title>

<!-- Placed at the end of the document so the pages load faster -->
<%@include file="../common/js.jsp" %>

<style type="text/css">
    /* h1{
        margin: 30px 0;
        padding: 0 200px 15px 0;
        border-bottom: 1px solid #E5E5E5;
    } */
	.bs-example{
    	margin: 20px;
    }
</style>

<script>
function catSelect() {
	var catSel = document.getElementById("inputCategory");
	var selected = catSel.options[catSel.selectedIndex];
	var catID = selected.getAttribute("data-id")
		
	var id = document.getElementById("selID");
	id.value = catID;

	var desc = document.getElementById("inputName");
	desc.value = selected.value;
}

/* $(function() {
	$( "#inputCategory" ).selectmenu();
}); */

</script>

</head>

<body role="document">
<div id="wrap">
<%@include file="../common/nav.jsp" %>

<!-- <div class="bs-example"> -->
	<div class="container">
		<h1>Categories</h1>
	    <form:form class="form-horizontal" role="form" action="category" method="post" modelAttribute="category">
	    	<form:hidden id="selID" path="id" value="0"/>
			<div class="form-group">
	            <label class="control-label col-sm-4" for="inputCategory">Categories:</label>
	            <div class="col-sm-3">
	                <select class="form-control" id="inputCategory" onChange="catSelect()">
	                	<c:forEach items="${categoryList}" var="category" varStatus="loopCounter">
	                		<option data-id="${category.id}"><c:out value="${category.name}" /></option>
	                	</c:forEach>
	                </select>
	           </div>
	        </div>           
	    	<div class="form-group">
	            <label class="control-label col-sm-4" for="inputName">Description:</label>
	            <div class="col-sm-3">
	                <form:input type="text" class="form-control" id="inputName" placeholder="Name" path="name" />
	            </div>
	        </div>
	        <br>
	        <div class="form-group">
	            <div class="col-sm-offset-4 col-sm-3 text-center">
	                <form:button type="submit" class="btn btn-primary pull-left" name="save" value="Save">Save</form:button>
	                <form:button type="submit" class="btn btn-primary" name="delete" value="Delete">Delete</form:button>
	                <input type="reset" class="btn btn-default pull-right" value="Reset">
	            </div>
	        </div>
		</form:form>
	</div>
</div>

<%@include file="../common/footer.jsp" %>


<!-- </div> -->

</body>
</html>
