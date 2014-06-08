
<%@ page import="org.openlab.notes.NoteItem" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="${params.bodyOnly?'body':'main'}" />
		<g:set var="entityName" value="${message(code: 'noteItem.label', default: 'NoteItem')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
	<a href="#show-noteItem" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div id="show-noteItem" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${noteItemInstance?.id}" />
					<%-- if note is draft and the user is the author, show edit,save and delete options --%>
					<g:if test="${noteItemInstance?.status == 'draft' && creator}">
						<g:remoteLink params="${[bodyOnly: true]}" update="body" class="edit" action="edit" id="${noteItemInstance.id}"><g:message code="default.button.edit.label" default="Edit" /></g:remoteLink>
						<g:remoteLink params="${[bodyOnly: true]}" update="body" class="save" action="authorSign" id="${noteItemInstance.id}"><g:message code="default.button.authorSign.label" default="Finalize" /></g:remoteLink>
						<g:submitToRemote params="${[bodyOnly: true]}" update="body" action="delete" name="delete" class="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" before="if(!confirm('Are you sure yu want to delete this note?')) return false"/>
					</g:if>
					<%-- if note is final and the user is the supervisor, show sign option --%>
					<g:elseif test="${noteItemInstance?.status == 'final' && supervisor}">
						<g:remoteLink params="${[bodyOnly: true]}" update="body" class="save" action="supervisorSign" id="${noteItemInstance.id}"><g:message code="default.button.sign.label" default="Sign note" /></g:remoteLink>
					</g:elseif>
					<g:else>
						Note is finalized, and can not be edited or deleted.
					</g:else>
				</fieldset>
			</g:form>
			<ol class="property-list noteItem">
			
				<g:if test="${noteItemInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="noteItem.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${noteItemInstance?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${noteItemInstance?.note}">
				<li class="fieldcontain">
					<span id="note-label" class="property-label"><g:message code="noteItem.note.label" default="Note" /></span>
					
						<span class="property-value" aria-labelledby="note-label"><g:fieldValue bean="${noteItemInstance}" field="note"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${noteItemInstance?.status}">
				<li class="fieldcontain">
					<span id="status-label" class="property-label"><g:message code="noteItem.status.label" default="Status" /></span>
					
						<span class="property-value" aria-labelledby="status-label"><g:fieldValue bean="${noteItemInstance}" field="status"/></span>
					
				</li>
				</g:if>
				<g:if test="${noteItemInstance?.title}">
				<li class="fieldcontain">
					<span id="title-label" class="property-label"><g:message code="noteItem.title.label" default="Title" /></span>
					
						<span class="property-value" aria-labelledby="title-label"><g:fieldValue bean="${noteItemInstance}" field="title"/></span>
					
				</li>
				</g:if>
			</ol>
		</div>
	</body>
</html>
