
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
<%--		<div class="nav" role="navigation">--%>
<%--			<ul>--%>
<%--				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>--%>
<%--				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>--%>
<%--				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>--%>
<%--			</ul>--%>
<%--		</div>--%>
		<div id="show-noteItem" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${noteItemInstance?.id}" />
<%--				IF FINAL, DO NOT SHOW, EDIT, FINAL AND DELETE OPTIONS	--%>
					<g:if test="${noteItemInstance?.status == 'open' && creator}">
						<g:remoteLink params="${[bodyOnly: true]}" update="body" class="edit" action="edit" id="${noteItemInstance.id}"><g:message code="default.button.edit.label" default="Edit" /></g:remoteLink>
						<g:remoteLink params="${[bodyOnly: true]}" update="body" class="save" action="finalizeNote" id="${noteItemInstance.id}"><g:message code="default.button.finalizeNote.label" default="Finalize" /></g:remoteLink>
						<g:submitToRemote params="${[bodyOnly: true]}" update="body" action="delete" name="delete" class="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" before="if(!confirm('Are you sure yu want to delete this note?')) return false"/>
					</g:if>
					<g:elseif test="${noteItemInstance?.status == 'final' && supervisor}">
						<g:remoteLink params="${[bodyOnly: true]}" update="body" class="save" action="sign" id="${noteItemInstance.id}"><g:message code="default.button.sign.label" default="Sign" /></g:remoteLink>
					</g:elseif>
					<g:elseif test="${supervisor}">
						Note is not finalized, and can not be signed.
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
<%--				This should be removed when deloyed--%>
				<g:if test="${noteItemInstance?.finalizedNote}">
				<li class="fieldcontain">
					<span id="hash-label" class="property-label"><g:message code="noteItem.hash.label" default="Hash" /></span>
						<span class="property-value" aria-labelledby="title-label"><g:fieldValue bean="${noteItemInstance}" field="finalizedNote"/></span>
				</li>
				</g:if>
<%--				To here--%>
			</ol>
		</div>
	</body>
</html>
