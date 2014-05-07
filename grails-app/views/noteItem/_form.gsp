<%@ page import="org.openlab.notes.NoteItem" %>

<%--		<script>--%>
<%--		tinymce.init({--%>
<%--			menubar:false,--%>
<%--			plugins: [--%>
<%--		               --%>
<%--		                "table textcolor code"--%>
<%--		        ],--%>
<%--		    selector: "textarea",--%>
<%--		    toolbar1: "cut copy paste | searchreplace | bullist numlist | outdent indent blockquote | undo redo | link unlink anchor image media code | inserttime preview | table",--%>
<%--	        toolbar2: "bold italic underline strikethrough |formatselect fontselect fontsizeselect | alignleft aligncenter alignright alignjustify | forecolor backcolor",--%>
<%--		});
<%--		</script>--%>

<g:hiddenField name="status" value="open"/>
 
<%--<div class="fieldcontain ${hasErrors(bean: noteItemInstance, field: 'status', 'error')} ">--%>
<%--	<label for="status">--%>
<%--		<g:message code="noteItem.status.label" default="Status" />--%>
<%--	</label>--%>
<%--	<g:textField name="status" value="${noteItemInstance?.status}" />--%>
<%--</div>--%>

<%--<div class="fieldcontain ${hasErrors(bean: noteItemInstance, field: 'timeSpent', 'error')} ">--%>
<%--	<label for="timeSpent">--%>
<%--		<g:message code="noteItem.timeSpent.label" default="Time Spent" />--%>
<%--		--%>
<%--	</label>--%>
<%--	<g:field type="number" name="timeSpent" value="${noteItemInstance.timeSpent}" />--%>
<%--</div>--%>

<table>
	<tbody>
		<tr>
			
			<td>
				<div class="fieldcontain ${hasErrors(bean: noteItemInstance, field: 'title', 'error')} ">
				<label for="title">
					<g:message code="noteItem.title.label" default="Title" />
				</label>
				</div>
			</td>
			<td>
				<g:textField name="title" value="${noteItemInstance?.title}" style="width:99%"/>
			</td>
		</tr>
		<tr>
			<td>
				<div class="fieldcontain ${hasErrors(bean: noteItemInstance, field: 'note', 'error')} ">
				<label for="note">
					<g:message code="noteItem.note.label" default="Note" />
				</label>
				</div>
			</td>
			<td>
				<textarea name="note">${noteItemInstance?.note}</textarea>
			</td>
		</tr>
	</tbody>
</table>
