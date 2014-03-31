package org.openlab.notes

import org.springframework.dao.DataIntegrityViolationException

class NoteItemController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [noteItemInstanceList: NoteItem.list(params), noteItemInstanceTotal: NoteItem.count()]
    }

    def create() {
        [noteItemInstance: new NoteItem(params)]
    }

    def save() {
		println params
        def noteItemInstance = new NoteItem(params)
        if (!noteItemInstance.save(flush: true, failOnError:true)) {
            render(view: "create", model: [noteItemInstance: noteItemInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), noteItemInstance.id])
        redirect(action: "show", id: noteItemInstance.id, params:[bodyOnly: true]) // , params:[bodyOnly: true]
    }

    def show(Long id) {
        def noteItemInstance = NoteItem.get(id)
        if (!noteItemInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
            redirect(action: "list")
            return
        }

        [noteItemInstance: noteItemInstance]
        
    }

    def edit(Long id) {
        def noteItemInstance = NoteItem.get(id)
        if (!noteItemInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
            redirect(action: "list")
            return
        }

        [noteItemInstance: noteItemInstance]
    }

    def update(Long id, Long version) {
        def noteItemInstance = NoteItem.get(id)
        if (!noteItemInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (noteItemInstance.version > version) {
                noteItemInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'noteItem.label', default: 'NoteItem')] as Object[],
                          "Another user has updated this NoteItem while you were editing")
                render(view: "edit", model: [noteItemInstance: noteItemInstance])
                return
            }
        }

        noteItemInstance.properties = params

        if (!noteItemInstance.save(flush: true)) {
            render(view: "edit", model: [noteItemInstance: noteItemInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), noteItemInstance.id])
        redirect(action: "show", id: noteItemInstance.id)
    }

    def delete(Long id) {
        def noteItemInstance = NoteItem.get(id)
        if (!noteItemInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
            redirect(action: "list")
            return
        }

        try {
            noteItemInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
            redirect(action: "list", params:[bodyOnly: true]) // , params:[bodyOnly: true]
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
            redirect(action: "show", id: id)
        }
    }
}
