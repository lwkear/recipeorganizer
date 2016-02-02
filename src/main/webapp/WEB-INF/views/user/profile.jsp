<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="profile.title"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<spring:bind path="userProfile.city"><c:set var="cityError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="userProfile.state"><c:set var="stateError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="userProfile.interests"><c:set var="interestsError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="userProfile.avatar"><c:set var="avatarError">${status.errorMessage}</c:set></spring:bind>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="profile.title"></spring:message></h3>
			</div>
			<div class="row">
				<form:form name="profileForm" id="profileForm" role="form" method="post" onsubmit="return checkAvatarOptions()" modelAttribute="userProfile" enctype="multipart/form-data">
					<input type="text" id="avatarErr" value="${avatarError}" style="display:none"></input>
					<div class="col-sm-12">
			        	<div class="form-group col-sm-6 col-sm-offset-2 <c:if test="${not empty cityError}">has-error</c:if>">
							<label class="control-label" for="city"><spring:message code="profile.city"></spring:message></label>
							<form:input class="form-control maxSize" type="text" id="city" name="city" path="city" autocomplete="off" data-max="${sizeMap['city.max']}"/>
							<span class="text-danger" id="cityErrMsg">${cityError}</span>
						</div>
			        	<div class="form-group col-sm-2 <c:if test="${not empty stateError}">has-error</c:if>">
							<label class="control-label" for="state"><spring:message code="profile.state"></spring:message></label>
							<form:input class="form-control maxSize" type="text" id="state" path="state" data-max="${sizeMap['state.max']}"/>
							<span class="text-danger" id="stateErrMsg">${stateError}</span>
						</div>
					</div>
					<div class="col-sm-12">
						<div class="col-sm-8 col-sm-offset-2">
							<label class="control-label"><spring:message code="profile.age"></spring:message></label>
						</div>
						<div class="form-group col-sm-8 col-sm-offset-2" id="ageGroup">
							<div class="radio-inline">
								<form:radiobutton value="1" id="age1" path="age"/>&lt;18
							</div>
							<div class="radio-inline">
								<form:radiobutton value="2" id="age2" path="age"/>18-30
							</div>
							<div class="radio-inline">
								<form:radiobutton value="3" id="age3" path="age"/>31-50
							</div>
							<div class="radio-inline">
								<form:radiobutton value="4" id="age4" path="age"/>51-70
							</div>
							<div class="radio-inline">
								<form:radiobutton value="5" id="age5" path="age"/>>70
							</div>
							<div class="radio-inline">
								<form:radiobutton value="0" id="age0" path="age"/><spring:message code="profile.nevermind"></spring:message>
							</div>
						</div>
					</div>
					<div class="col-sm-12">
			        	<div class="form-group col-sm-8 col-sm-offset-2 <c:if test="${not empty interestsError}">has-error</c:if>">
							<label class="control-label" for="interests"><spring:message code="profile.interests"></spring:message></label>
							<form:textarea class="form-control maxSize" rows="4" id="interests" path="interests" data-max="${sizeMap['interests.max']}"></form:textarea>
							<span class="text-danger" id="interestsErrMsg">${interestsError}</span>
						</div>
					</div>
					<div class="col-sm-12">
						<c:choose>
							<c:when test="${not empty userProfile.avatar}">
								<label class="control-label col-sm-3 col-sm-offset-2" style="text-align: left;" 
									id="photoLabel" for="selectedFile"><spring:message code="profile.photo"></spring:message></label>
								<label class="control-label col-sm-2" style="text-align: left;" 
									id="photoOptionsLabel" for="file"><spring:message code="common.photo.options"></spring:message></label>
								<label class="control-label col-sm-3 newphoto" style="text-align: left; display:none" 
									id="newPhotoLabel" for="file"><spring:message code="common.photo.new"></spring:message></label>
							</c:when>
							<c:otherwise>
								<label class="control-label col-sm-3 col-sm-offset-2" style="text-align: left;" 
									id="fileLabel" for="file"><spring:message code="profile.photo"></spring:message></label>
							</c:otherwise>
						</c:choose>
					</div>
					<div class="col-sm-12">					
						<c:choose>
							<c:when test="${not empty userProfile.avatar}">												
								<div class="col-sm-3 col-sm-offset-2">
									<!-- NOTE: disabled inputs are not submitted to the controller, hence the need for a hidden field -->
									<form:hidden id="avatar" path="avatar" value="${userProfile.avatar}"/>
									<input class="form-control" type="text" value="${userProfile.avatar}" disabled/>
								</div>
								<div class="col-sm-2">
									<div class="radio">
										<label><input  type="radio" name="photoOpts" value="keep" checked><spring:message code="common.photo.keep"></spring:message></label>
									</div>
									<div class="radio">
										<label><input type="radio" name="photoOpts" value="remove"><spring:message code="common.photo.remove"></spring:message></label>
									</div>
									<div class="radio">
										<label><input type="radio" name="photoOpts" value="change"><spring:message code="common.photo.change"></spring:message></label>
									</div>
								</div>
								<div class="col-sm-3 newphoto" style="display:none">
									<div class="input-group">
										<span class="input-group-btn">
											<span class="btn btn-default btn-file">
												<spring:message code="common.selectfile"></spring:message>&hellip;
												<input type="file" id="file" name="file">
											</span>
										</span>
										<input type="text" id="photoname" class="form-control" readonly>
									</div>
								</div>
							</c:when>
							<c:otherwise>									
								<div class="col-sm-4 col-sm-offset-2">
									<div class="input-group">
										<span class="input-group-btn">
											<span class="btn btn-default btn-file">
												<spring:message code="common.selectfile"></spring:message>&hellip;
												<input type="file" id="file" name="file">
											</span>
										</span>
										<input type="text" id="photoname" class="form-control" disabled>
									</div>
								</div>
							</c:otherwise>
						</c:choose>
					</div>	        		
			        <div class="form-group col-sm-2 col-sm-offset-5 spacer-vert-sm">
						<button class="btn btn-primary btn-block" type="submit" id="btnSubmit" name="btnSubmit">
							<spring:message code="common.submit"></spring:message></button>
	        		</div>
	        		<form:hidden path="id" />
	        		<form:hidden path="user.id" />
	      		</form:form>
			</div>
	    </div>
	</div>
	
<%@include file="../common/footer.jsp" %>

</body>

<script src="<c:url value="/resources/custom/typeahead.js" />"></script>
<script src="<c:url value="/resources/custom/userprofile.js" />"></script>

</html>


<%-- 
	<spring:hasBindErrors name="userProfile">
    <c:set var="errorCnt">${errors.errorCount}</c:set>
    <p><b># of Errors:${errorCnt}</b></p>
    <p></p>
	<c:forEach var="error" items="${errors.allErrors}">
		<b><c:out value="${error}" /></b>
		<p></p>
	</c:forEach>
	</spring:hasBindErrors>
 --%>