<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>
<%@ page import="net.kear.recipeorganizer.enums.CertMode"%>

<title><spring:message code="certificate.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<spring:message var="yesLabel" code="common.yes"></spring:message>
<spring:message var="noLabel" code="common.no"></spring:message>

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">
	 	<div class="col-sm-12 title-bar">
			<c:if test="${not empty warningMaint}">
				<h5 class="bold-maroon text-center"><em>${warningMaint}</em></h5>
			</c:if>
			<div class="page-header"> 		
				<h3><spring:message code="certificate.title"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">
			<input type="hidden" id="changeMode" value="<c:url value="/admin/changeMode"/>"/>
			<input type="hidden" id="genAccountKey" value="<c:url value="/admin/genAccountKey"/>"/>
			<input type="hidden" id="genDomainKey" value="<c:url value="/admin/genDomainKey"/>"/>
			<input type="hidden" id="acceptAgreement" value="<c:url value="/admin/acceptAgreement"/>"/>
			<input type="hidden" id="registerAccount" value="<c:url value="/admin/registerAccount"/>"/>			
			<input type="hidden" id="authorizeDomains" value="<c:url value="/admin/authorizeDomains"/>"/>
			<input type="hidden" id="getCertificate" value="<c:url value="/admin/getCertificate"/>"/>

			<form:form name="certForm" id="certForm" role="form" method="post" modelAttribute="certificateDto">
				<form:hidden id="accountKey" path="accountKey"/>
				<form:hidden id="accountKeyFile" path="accountKeyFile"/>
				<form:hidden id="domainKey" path="domainKey"/>
				<form:hidden id="domainKeyFile" path="domainKeyFile"/>
				<form:hidden id="agreement" path="agreement"/>
				<form:hidden id="agreementUri" path="agreementUri"/>
				<form:hidden id="registered" path="registered"/>
				<form:hidden id="registrationUri" path="registrationUri"/>
				<form:hidden id="authorized" path="authorized"/>
				<form:hidden id="certificate" path="certificate"/>
				<form:hidden id="domainCsrFile" path="domainCsrFile"/>
				<form:hidden id="domainCertFile" path="domainCertFile"/>
				<form:hidden id="domainCertChainFile" path="domainCertChainFile"/>
				<form:hidden id="domainNdx" path="domainNdx"/>
				<form:hidden id="domainCount" path="domainCount"/>
			
				<div class="col-sm-12">
					<div class="row spacer-vert-xs">
						<div class="col-sm-2">
		            		<label class="control-label"><spring:message code="certificate.mode"></spring:message></label>
		            	</div>
						<div class="col-sm-4">
							<div class="radio-inline">
								<form:radiobutton id="modeProd" class="certMode" value="${CertMode.PROD}" path="mode"/><spring:message code="certificate.production"></spring:message>
							</div>
							<div class="radio-inline">
								<form:radiobutton id="modeTest" class="certMode" value="${CertMode.TEST}" path="mode"/><spring:message code="certificate.test"></spring:message>
							</div>
						</div>
					</div>
				</div>			
				<c:if test="${not empty certificateDto.errorMsg}"> 
					<div class="col-sm-12 text-center text-danger">
						<div class="row spacer-vert-md">
							<span>${certificateDto.errorMsg}</span>				
						</div>
					</div>
				</c:if>			
				<div class="col-sm-12 spacer-vert-md">
					<div class="row">
						<div class="col-sm-2">
							<div class="row">
								<div class="col-sm-8">
		            				<label class="control-label"><spring:message code="certificate.accountKey"></spring:message></label>
		            			</div>
								<div class="col-sm-4">
									<span>${certificateDto.accountKey ? yesLabel : noLabel}</span>
								</div>
		            		</div>
		            	</div>
						<div class="col-sm-4">
							<div>${certificateDto.accountKeyFile}</div>
						</div>
						<div class="col-sm-4">
						</div>
						<div class="col-sm-2">
		            		<button class="btn btn-primary btn-sm btn-block" type="submit" id="btnAccountKey" name="btnAccountKey"><spring:message code="certificate.genAccountKey"></spring:message></button>
		            	</div>
					</div>
				</div>
				<div class="col-sm-12 spacer-vert-md">
					<div class="row">
						<div class="col-sm-2">
							<div class="row">
								<div class="col-sm-8">
		            				<label class="control-label"><spring:message code="certificate.domainKey"></spring:message></label>
		            			</div>
								<div class="col-sm-4">
									<span>${certificateDto.domainKey ? yesLabel : noLabel}</span>
								</div>
		            		</div>
		            	</div>
						<div class="col-sm-8">
							<div>${certificateDto.domainKeyFile}</div>
						</div>
						<div class="col-sm-2">
		            		<button class="btn btn-primary btn-sm btn-block" type="submit" id="btnDomainKey" name="btnDomainKey"><spring:message code="certificate.genDomainKey"></spring:message></button>
		            	</div>
					</div>
				</div>
				<div class="col-sm-12">
					<div class="row spacer-vert-md">				
						<div class="col-sm-2">
							<div class="row">
								<div class="col-sm-8">
		            				<label class="control-label"><spring:message code="certificate.agreement"></spring:message></label>
		            			</div>
								<div class="col-sm-4">
									<span>${certificateDto.agreement ? yesLabel : noLabel}</span>
								</div>
		            		</div>
		            	</div>
						<div class="col-sm-8">
							<c:if test="${not empty certificateDto.agreementUri}">
								<span><a href="${certificateDto.agreementUri}" target="_blank"><spring:message code="certificate.termsofservice"></spring:message></a></span>
							</c:if>
						</div>
						<div class="col-sm-2">
		            		<button class="btn btn-primary btn-sm btn-block" type="submit" id="btnAcceptTOS" name="btnAcceptTOS"><spring:message code="certificate.acceptTOS"></spring:message></button>
		            	</div>
					</div>
				</div>
				<div class="col-sm-12">
					<div class="row spacer-vert-md">				
						<div class="col-sm-2">
							<div class="row">
								<div class="col-sm-8">
		            				<label class="control-label"><spring:message code="certificate.registration"></spring:message></label>
		            			</div>
								<div class="col-sm-4">
									<span>${certificateDto.registered ? yesLabel : noLabel}</span>
								</div>
		            		</div>
		            	</div>
						<div class="col-sm-8">
							<div>${certificateDto.registrationUri}</div>
						</div>
						<div class="col-sm-2">
		            		<button class="btn btn-primary btn-sm btn-block" type="submit" id="btnRegister" name="btnRegister"><spring:message code="certificate.register"></spring:message></button>
		            	</div>
					</div>
				</div>
				<div class="col-sm-12">
					<div class="row spacer-vert-md">				
						<div class="col-sm-2">
							<div class="row">
								<div class="col-sm-8">
		            				<label class="control-label"><spring:message code="certificate.authorization"></spring:message></label>
		            			</div>
								<div class="col-sm-4">
									<span>${certificateDto.authorized ? yesLabel : noLabel}</span>
								</div>
		            		</div>
		            	</div>
						<div class="col-sm-8">
							<div>${certificateDto.authDomains}</div>
						</div>
						<div class="col-sm-2">
		            		<button class="btn btn-primary btn-sm btn-block" type="submit" id="btnAuthorized" name="btnAuthorized"><spring:message code="certificate.authorize"></spring:message></button>
		            	</div>
					</div>
				</div>
				<%-- <div class="col-sm-12 spacer-vert-md">
					<c:forEach var="domainChallenge" items="${certificateDto.domainChallengeList}" varStatus="loop">
						<form:hidden path="domainChallengeList[${loop.index}].challenged"/>
						<form:hidden path="domainChallengeList[${loop.index}].domain"/>
						<form:hidden path="domainChallengeList[${loop.index}].challengeUri"/>
						<form:hidden path="domainChallengeList[${loop.index}].challengeFile"/>
						<div class="row <c:if test="${loop.index > 0}">spacer-vert-xs</c:if>">						
							<div class="col-sm-2">
								<div class="row">
									<div class="col-sm-8">
			            				<label class="control-label"><spring:message code="certificate.challenge"></spring:message></label>
			            			</div>
									<div class="col-sm-4">
										<div class="challengedState">${domainChallenge.challenged ? yesLabel : noLabel}</div>
									</div>
			            		</div>
			            	</div>
							<div class="col-sm-2">
								<div>${domainChallenge.domain}</div>
							</div>
							<div class="col-sm-3">
								<div>${domainChallenge.challengeUri}</div>
							</div>
							<div class="col-sm-3">
								<div style="text-overflow: ellipsis; overflow: hidden; white-space: nowrap; margin: 0;">${domainChallenge.challengeFile}</div>
							</div>
							<div class="col-sm-2">
			            		<button class="btn btn-primary btn-sm btn-block btnChallenge 
			            			<c:if test='${empty domainChallenge.challengeFile || domainChallenge.challenged}'>disabled</c:if>" 
			            			type="submit" id="btnTestChallenge${loop.index}" 
			            			name="btnTestChallenge"><spring:message code="certificate.testChallenge"></spring:message></button>
			            	</div>
			        	</div>
			        </c:forEach>
				</div> --%>
				<div class="col-sm-12">
					<div class="row spacer-vert-md">				
						<div class="col-sm-2">
							<div class="row">
								<div class="col-sm-8">
		            				<label class="control-label"><spring:message code="certificate.certificate"></spring:message></label>
		            			</div>
								<div class="col-sm-4">
									<span>${certificateDto.certificate ? yesLabel : noLabel}</span>
								</div>
		            		</div>
		            	</div>
						<div class="col-sm-8">
							<%-- <div style="overflow-wrap: break-word">${certificateDto.domainCertChainFile}</div> --%>
							<div style="overflow-wrap: break-word">${certificateDto.domainCertFile}</div>
						</div>
						<div class="col-sm-2">
		            		<button class="btn btn-primary btn-sm btn-block" type="submit" id="btnCertificate" name="btnCertificate"><spring:message code="certificate.genCertificate"></spring:message></button>
		            	</div>
					</div>
				</div>
				<!-- <div class="col-sm-12">
					<div class="row spacer-vert-md">
						<div class="col-sm-12">
						</div>
					</div>
				</div> -->				
			</form:form>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- include certificate routines -->
<script src="<c:url value="/resources/custom/certificate.js" />"></script>

</html>
