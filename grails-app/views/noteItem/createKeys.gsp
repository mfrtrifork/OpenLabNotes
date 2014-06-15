<%@ page import="org.openlab.notes.NoteItem" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="${params.bodyOnly?'body':'main'}" />
		<g:set var="entityName" value="${message(code: 'noteItem.label', default: 'NoteItem')}" />
		<title><g:message code="default.authorSign.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#edit-noteItem" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div id="edit-noteItem" class="content scaffold-edit" role="main">
			<h1>Generate keys</h1>
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
			<g:form method="post" onsubmit="return false;">
				<fieldset class="form">
					For security reasons we need to generate keys for encryption of notes. Please confirm your password.
					<table>
						<tbody>
							<tr>
								<td>
									<label for="password">Password</label>
								</td>
								<td>
									<g:passwordField name="password" value="" style="width:99%"/>
								</td>
							</tr>
						</tbody>
					</table>
				</fieldset>
				<fieldset class="buttons">
					<g:submitToRemote update="body" id="${noteId}" action="saveKeys" class="save" value="${message(code: 'default.button.saveKeys.label', default: 'Generate keys')}"/>
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
