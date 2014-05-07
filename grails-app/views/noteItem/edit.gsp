<%@ page import="org.openlab.notes.NoteItem" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="${params.bodyOnly?'body':'main'}" />
		<g:set var="entityName" value="${message(code: 'noteItem.label', default: 'NoteItem')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
		
<%--		<g:javascript src="js/tinymce/tinymce.min.js" />--%>
<%--		<g:javascript src="js/tinymce/themes/modern/theme.min.js" />--%>
<%--		<g:javascript src="js/tinymce/plugins/table/plugin.min.js" />--%>
<%--		<g:javascript src="js/tinymce/plugins/textcolor/plugin.min.js" />--%>
<%--		<g:javascript src="js/tinymce/plugins/code/plugin.min.js" />--%>
<%--		<link rel="stylesheet" href="${resource(dir: 'js/tinymce/skins/lightgray', file: 'skin.min.css')}" type="text/css">--%>
<%--		<link rel="stylesheet" href="${resource(dir: 'js/tinymce/skins/lightgray', file: 'content.min.css')}" type="text/css">--%>
	</head>
	<body>
		<a href="#edit-noteItem" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<%--		<div class="nav" role="navigation">--%>
<%--			<ul>--%>
<%--				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>--%>
<%--				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>--%>
<%--				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>--%>
<%--			</ul>--%>
<%--		</div>--%>
		<div id="edit-noteItem" class="content scaffold-edit" role="main">
			<h1><g:message code="default.edit.label" args="[entityName]" /></h1>
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
				<g:hiddenField name="id" value="${noteItemInstance?.id}" />
				<g:hiddenField name="version" value="${noteItemInstance?.version}" />
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
<%--					<g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />--%>
					<g:submitToRemote update="body" action="update" class="save" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
<%--					<g:submitToRemote update="body" action="finalizeNote" class="save" value="${message(code: 'default.button.finalize.label', default: 'Finalize')}"/>--%>
<%--										<g:submitToRemote before="jQuery('#note').html(tinyMCE.activeEditor.getContent());" update="body" action="update" class="save" value="${message(code: 'default.button.update.label', default: 'Update')}"/>z--%>
<%--					<g:submitToRemote before="jQuery('#note').html(tinyMCE.activeEditor.getContent());if(!confirm('Are you sure you want to finalize this note?')) return false" update="body" action="finalizeNote" class="save" value="${message(code: 'default.button.finalize.label', default: 'Finalize')}"/>--%>
<%--					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" formnovalidate="" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />--%>
				</fieldset>
			</g:form>
		</div>
		<script>
			//THIS WILL PRINT! 
			//alert('test');
		</script>
	</body>
</html>
