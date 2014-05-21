package org.openlab.notes

import cr.co.arquetipos.crypto.*

import java.security.MessageDigest

import org.openlab.security.User

import java.security.NoSuchAlgorithmException

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

class NoteItemController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST", finalize: "POST"]

    def noteAccessService

    def index() {
        redirect(action: "list", params: params)
    }
	def list = {
		/* This encryption should be moved when done testing. Perhaps PGP should be saved for each user or note? */
		def pgp = PGP.generateKeyPair()
		println(pgp.getClass())
		cr.co.arquetipos.crypto.PGP tests = pgp
		String passphrase = 'demo0815'
		String message = 'Hush Hush TESTING'
		
		String encodedPublic = pgp.encodedPublicKey
		String encodedPrivate = pgp.getEncodedPrivateKey(passphrase)

		//PGP publicOnly = new PGP(encodedPublic, '')
		PGP privateOnly = new PGP('', encodedPrivate, passphrase)
		
		String encrypted = privateOnly.encryptBase64(message)
		println('Encrypted message: ' + encrypted)
		String decrypted = pgp.decryptBase64(encrypted)
		println('Decrypted message: ' + decrypted)
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User loggedInUser = User.find{username == auth.name}
		//params.max = Math.min(params.max ? params.int('max') : 10, 100)
		def notesList = NoteItem.findAllByCreatorOrSupervisor(loggedInUser, loggedInUser, [sort: "id", order: "desc"])
		//[noteItemInstanceList: NoteItem.list(params), noteItemInstanceTotal: NoteItem.count(), bodyOnly: true]
		[noteItemInstanceList: notesList, noteItemInstanceTotal: NoteItem.count(), bodyOnly: true]
	}

    def create() {
        [noteItemInstance: new NoteItem(params), bodyOnly: true]
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
        if(!noteAccessService.grantAccess(noteItemInstance)){
            flash.message = "You do not have access to this particular note!"
            redirect(action: "list")
        }
        if (!noteItemInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
            redirect(action: "list")
            return
        }
        [noteItemInstance: noteItemInstance, bodyOnly: true]

    }

    def edit(Long id) {
        def noteItemInstance = NoteItem.get(id)
        if(!noteAccessService.grantAccess(noteItemInstance)){
            flash.message = "You do not have access to this particular note!"
            redirect(action: "list")
        }
        if (!noteItemInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
            redirect(action: "list")
            return
        }
        [noteItemInstance: noteItemInstance, bodyOnly: true]
    }

    def update(Long id, Long version) {
        def noteItemInstance = NoteItem.get(id)
        if(!noteAccessService.grantAccess(noteItemInstance)){
            flash.message = "You do not have access to this particular note!"
            redirect(action: "list")
        }
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
        else if(!noteAccessService.grantAccess(noteItemInstance)){
            flash.message = "You do not have access to this particular note!"
            redirect(action: "list")
        }
        else if(noteItemInstance.status != "open"){
            flash.message = "You are not allowed to delete finalized notes"
            redirect(action: "show", id: id)
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
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		def users = User.findAll {
			username != auth.name
		}
		def noteItemInstance = NoteItem.get(id)
		if (!noteItemInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
			redirect(action: "list")
			return
		}
		[noteItemInstance: noteItemInstance, users:users, bodyOnly: true]
	}
	def actualFinalize(){
		println(params)
		/* Find supervisor from id */
		def supervisor = User.find{id == params.supervisor}
		println(supervisor)
		/* Remove id from parameters */
		params.remove('supervisor')
		println("actualFinalize")
		def noteItemInstance = NoteItem.get(params.id)
		if (!noteItemInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
			redirect(action: "list")
			return
		}
		[noteItemInstance: noteItemInstance]
		noteItemInstance.properties = params
		/* Add the supervisor to the noteItem */
		noteItemInstance.supervisor = supervisor
		println(noteItemInstance.creator.id)
		
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