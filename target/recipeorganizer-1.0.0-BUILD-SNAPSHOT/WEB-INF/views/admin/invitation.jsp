<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="menu.invite"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12 title-bar">
			<div class="page-header">
				<h3><spring:message code="invite.title"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">
			<form class="form-horizontal" autocomplete="off">
				<div class="row">
					<div class="col-sm-12">
						<div class="form-group col-sm-12" style="margin-bottom:0">
							<div class="form-group" style="margin-bottom:0">
								<label class="control-label col-sm-3" style="text-align: left" ><spring:message code="signup.firstname"></spring:message></label>
								<label class="control-label col-sm-3" style="text-align: left" ><spring:message code="signup.lastname"></spring:message></label>
								<label class="control-label col-sm-5" style="text-align: left" ><spring:message code="common.email"></spring:message></label>
							</div>
						</div>
						<div class="form-group col-sm-12 inviteGrp">
							<div class="form-group">
								<div class="col-sm-3">
									<input type="text" class="form-control firstName maxSize" id="firstName" data-max="${sizeMap['firstName.max']}"/>
									<span class="text-danger inviteErrGrp firstNameErrMsg"></span>
								</div>
								<div class="col-sm-3">
									<input type="text" class="form-control lastName maxSize" id="lastName" data-max="${sizeMap['lastName.max']}"/>
									<span class="text-danger inviteErrGrp lastNameErrMsg"></span>
								</div>
								<div class="col-sm-5">
									<div class="input-group">
										<input type="text" class="form-control email maxSize" id="email" data-max="${sizeMap['email.max']}"/>
										<span class="input-group-btn">
											<button class="btn btn-danger removeInvitation" type="button" style="display:none">
												<span class="glyphicon glyphicon-minus"></span>
											</button>
											<button class="btn btn-success addInvitation" type="button">
												<span class="glyphicon glyphicon-plus"></span>
											</button>
										</span>
									</div>
									<span class="text-danger inviteErrGrp emailErrMsg"></span>
								</div>
								<div class="col-sm-1" style="padding:0">
									<input type="text" class="resultbool" style="display:none"> 
									<div class="result"></div>
								</div>									
							</div>
						</div>
					</div>
				</div>
			</form>
			<div class="col-sm-12">
		        <div class="form-group col-sm-3 col-sm-offset-4 text-center spacer-vert-sm">
					<button class="btn btn-primary" type="submit" id="btnSubmit" name="btnSubmit"><spring:message code="common.submit"></spring:message></button>
					<input class="btn btn-default pull-right" type="button" id="btnReset" value="<spring:message code="common.reset"></spring:message>">
		   		</div>
		   	</div>
		</div>
	</div>
   
<%@include file="../common/footer.jsp" %>	
	
</body>

<script src="<c:url value="/resources/custom/invitations.js" />"></script>

</html>
