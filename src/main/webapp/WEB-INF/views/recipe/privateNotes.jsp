<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="net.kear.recipeorganizer.enums.AudioType"%>

<c:if test="${not empty recipeNote.note}">
	<div class="col-sm-12 spacer-vert-xs">
		<strong><spring:message code="recipe.note.privatenotes"></spring:message></strong>
		<button type="button" class="btn btn-link playBtn audioBtn collapse" data-rel="tooltip"  
			onclick="playAudio(${recipeNote.id.userId}, ${recipeNote.id.recipeId}, 0, '${AudioType.PRIVATENOTES}')"
			data-toggle="tooltip" data-container="body" data-placement="top" title="<spring:message code="tooltip.play.privatenotes"></spring:message>">
			<span class="glyphicon glyphicon-play"></span>
		</button>
		<button type="button" class="btn btn-link pauseBtn audioBtn collapse">
			<span class="glyphicon glyphicon-pause"></span>
		</button>
		<button type="button" class="btn btn-link stopBtn audioBtn collapse">
			<span class="glyphicon glyphicon-stop"></span>
		</button>
		<p class="privateNotes">${recipeNote.note}</p>
	</div>
</c:if>
