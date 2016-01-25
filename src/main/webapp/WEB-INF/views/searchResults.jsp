<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp" %>

<title><spring:message code="head.search"></spring:message> - <spring:message code="menu.product"></spring:message></title>

<style type="text/css">
.dataTable > thead > tr > th[class*="sort"]:after{
    content: "" !important;
}

</style>

</head>

<body role="document">
	
<%@include file="common/nav.jsp" %>	

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h1><spring:message code="title.searchresults"></spring:message> for "${searchTerm}"</h1>
			</div>
			<table class="table" id="recipeList">
				<thead>
					<tr>
						<th data-orderable="false"></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="recipe" items="${resultList}">
						<tr id="${recipe.id}">
							<td>
								<a href="<c:url value="/recipe/viewRecipe/${recipe.id}"/>" class="list-group-item">
								<c:if test="${not empty recipe.photo}">
									<span class="pull-right"><img src="<c:url value="/recipe/photo?id=${recipe.id}&filename=${recipe.photo}"/>" style="width:75px;height:75px;"/></span>
								</c:if>	
								<h4 class="header-blue">${recipe.name}</h4>
								<p class="list-group-item-text">${recipe.description}</p>
								<p class="clearfix"></p>										
								</a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>

<%@include file="common/footer.jsp" %>

</body>

<script type="text/javascript">

$('#recipeList').DataTable({
	language : {
    	emptyTable:     messageMap.get('recipe.table.emptyTable'),
	    info:           messageMap.get('recipe.table.info'),
	    infoEmpty:      messageMap.get('recipe.table.infoEmpty'),
	    infoFiltered:	messageMap.get('recipe.table.infoFiltered'),
	    lengthMenu:		messageMap.get('recipe.table.lengthMenu'),
	    zeroRecords:	messageMap.get('recipe.table.zeroRecords'),
	    search:			messageMap.get('common.table.search'),
	    paginate: {
	    	first:      messageMap.get('common.table.paginate.first'),
	    	last:       messageMap.get('common.table.paginate.last'),
	    	next:       messageMap.get('common.table.paginate.next'),
	    	previous:   messageMap.get('common.table.paginate.previous')
	    }
	},	
	stateSave : true
});

</script>

</html>
