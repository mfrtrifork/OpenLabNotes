<%@ page import="org.openlab.notes.NoteItem" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="${params.bodyOnly?'body':'main'}" />
		<g:set var="entityName" value="${message(code: 'noteItem.label', default: 'NoteItem')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
		<script>
			jQuery(document).ready(function(){ 
				function getTinyContent(){
					alert('TEST');
				}
			});
		</script>
	</head>
	<body>
		<div id="create-noteItem" class="content scaffold-create" role="main">
<%--			<h1><g:message code="default.create.label" args="[entityName]" /></h1>--%>
			<h1>Create new Note</h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${noteItemInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${noteItemInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form action="save" >
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:submitToRemote before="jQuery('#note').html(tinyMCE.activeEditor.getContent());" update="body" action="save" name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}"/>
<%--						<g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />--%>
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
