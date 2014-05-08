package org.openlab.notes

import java.security.MessageDigest

import java.security.NoSuchAlgorithmException

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

class NoteItemController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST", finalize: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }
	def list = {
		params.sort = "dateCreated"
		params.order = "desc"
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		[noteItemInstanceList: NoteItem.list(params), noteItemInstanceTotal: NoteItem.count(), bodyOnly: false]
	}

//    def list = {
//		params.max = Math.min(15, 100)
//        //params.max = Math.min(max ?: 15, 100)
//		params.sort = "dateCreated"
//        [noteItemInstanceList: NoteItem.list(params), noteItemInstanceTotal: NoteItem.count(), bodyOnly: false]
//		params.order = "desc"
//    }

    def create() {
        [noteItemInstance: new NoteItem(params)]
    }

    def save() {
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
        redirect(action: "show", id: noteItemInstance.id, params:[bodyOnly: true])
    }

    def delete(Long id) {
        def noteItemInstance = NoteItem.get(id)
        if (!noteItemInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
            redirect(action: "list")
            return
        }

        try {
            noteItemInstance.delete(flush: true, failOnError:true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
            redirect(action: "list", params:[bodyOnly: true]) // , params:[bodyOnly: true]
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
            redirect(action: "show", id: id)
        }
    }
	def finalizeNote(Long id) {
		println("finalizeNote");
		def noteItemInstance = NoteItem.get(id)
		if (!noteItemInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
			redirect(action: "list")
			return
		}
		[noteItemInstance: noteItemInstance]
//		def noteItemInstance = NoteItem.get(params.id)
//		if (!noteItemInstance) {
//			flash.message = message(code: 'default.not.found.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
//			redirect(action: "list")
//			return
//		}
//		params.status = 'final';
//		noteItemInstance.properties = params
//		
//		/* Remove attributes which shouldn't be hashed */
//		params.remove('status')	
//		params.remove('version')
//		params.remove('controller')
//		params.remove('lang')
//		params.remove('action')
//		/* Set username in string to be hashed */
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		params.username = auth.name;
//		String hash = params.toString()
//		
//		// STORE PASSWORD IN STRING
//		
//		println("finalizedNote before:" + noteItemInstance.finalizedNote)
//		println("params before:" + hash)
//		
//		noteItemInstance.setFinalizedNote(sha256(hash))
//		
//		println("finalizedNote after:" + noteItemInstance.finalizedNote)
//		redirect(action: "show", id: noteItemInstance.id, params:[bodyOnly: true])
	}
	def actualFinalize(){
		println("actualFinalize")
		def noteItemInstance = NoteItem.get(params.id)
		if (!noteItemInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
			redirect(action: "list")
			return
		}
		[noteItemInstance: noteItemInstance]
		
		noteItemInstance.properties = params
		
		params.remove('status')
		params.remove('version')
		params.remove('action')
		params.remove('controller')
		params.remove('lang')
		String hash = params.toString()
		noteItemInstance.setFinalizedNote(sha256(hash))
		
		println(noteItemInstance.finalizedNote)
		if (!noteItemInstance.save(flush: true)) {
			render(view: "edit", model: [noteItemInstance: noteItemInstance])
			return
		}
		flash.message = message(code: 'Note was finalized', args: [message(code: 'noteItem.label', default: 'NoteItem'), noteItemInstance.id])
		redirect(action: "show", id: noteItemInstance.id, params:[bodyOnly: true])
	}
	static String sha256(String input) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
		byte[] result = mDigest.digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}
}