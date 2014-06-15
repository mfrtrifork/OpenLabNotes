
<%@ page import="org.openlab.notes.NoteItem" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="${params.bodyOnly?'body':'main'}" />
		<g:set var="entityName" value="${message(code: 'noteItem.label', default: 'NoteItem')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
			<script>
				tinymce.init({
					menubar:false,
				    width: 800,
				    height: 600,
				    selector: "textarea",
				    readonly: 1,
				    toolbar:false,
				    statusbar:false,
				});
			</script>
			<script>
				$('.mce-toolbar').hide();
			</script>
	<a href="#show-noteItem" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div id="show-noteItem" class="content scaffold-show" role="main">
<%--			<h1><g:message code="default.show.label" args="[entityName]" /></h1>--%>
			<h1>${noteItemInstance?.title}<font style="font-size: 12pt; font-wight:normal;color:grey;">  (<g:formatDate format="yyyy-MM-dd HH:mm" date="${noteItemInstance?.dateCreated}"/>)</font></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${noteItemInstance?.id}" />
					<%-- if note is draft and the user is the author, show edit,save and delete options --%>
					<g:if test="${creator}">
						<g:if test="${noteItemInstance?.status == 'draft'}">
							<g:remoteLink params="${[bodyOnly: true]}" update="body" class="edit" action="edit" id="${noteItemInstance.id}"><g:message code="default.button.edit.label" default="Edit" /></g:remoteLink>
							<g:remoteLink params="${[bodyOnly: true]}" update="body" class="save" action="signNote" id="${noteItemInstance.id}"><g:message code="default.button.authorSign.label" default="Finalize" /></g:remoteLink>
							<g:submitToRemote params="${[bodyOnly: true]}" update="body" action="delete" name="delete" class="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" before="if(!confirm('Are you sure yu want to delete this note?')) return false"/>							
						</g:if>
						<g:elseif test="${noteItemInstance?.status == 'final'}">
							Pending supervisor signature, note can not be edited or deleted.
						</g:elseif>
						<g:elseif test="${noteItemInstance?.status == 'signed'}">
							Signed by you and the supervisor, note can not be edited or deleted.
						</g:elseif>
					</g:if>
					<g:if test="${supervisor}">
						<g:if test="${noteItemInstance?.status == 'final'}">
							<g:remoteLink params="${[bodyOnly: true]}" update="body" class="save" action="signNote" id="${noteItemInstance.id}"><g:message code="default.button.sign.label" default="Sign note" /></g:remoteLink>
						</g:if>
						<g:else>
							This note is signed by you and the author.
						</g:else>
					</g:if>
				</fieldset>
			</g:form>
			<div style="width:800px;margin-left:auto;margin-right:auto;margin-top:20px;">
			<textarea name="note">${noteItemInstance?.note}</textarea>
			</div>
		</div>
	</body>
</html>
