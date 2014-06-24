package org.openlab.notes

//import crypttools.PGPTools


//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.security.SecureRandom;
//import java.security.Security;
//import java.security.Signature;
import javax.crypto.SecretKeyFactory



//import cr.co.arquetipos.crypto.*

import crypttools.PGPCryptoBC

import java.security.KeyPair
import java.security.MessageDigest

import org.openlab.security.User

import java.security.NoSuchAlgorithmException

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder

class NoteItemController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST", finalize: "POST"]

	transient springSecurityService
	
    def noteAccessService

    def index() {
        redirect(action: "list", params: params)
    }
	def list = {
		User loggedInUser = springSecurityService.currentUser
		//params.max = Math.min(params.max ? params.int('max') : 10, 100)
		def notesList = NoteItem.findAllByCreator(loggedInUser, [sort: "id", order: "desc"])
		//[noteItemInstanceList: NoteItem.list(params), noteItemInstanceTotal: NoteItem.count(), bodyOnly: true]
		[noteItemInstanceList: notesList, noteItemInstanceTotal: NoteItem.count(), bodyOnly: true]
	}
	def listSupervisor = {
		User loggedInUser = springSecurityService.currentUser
		//params.max = Math.min(params.max ? params.int('max') : 10, 100)
		def notesList = NoteItem.findAllBySupervisor(loggedInUser, [sort: "id", order: "desc"])
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
		def creator = false
		def supervisor = false
		if(springSecurityService.currentUser == noteItemInstance.creator){
			creator = true
		}else if(springSecurityService.currentUser == noteItemInstance.supervisor){
			supervisor = true
		}
		[noteItemInstance: noteItemInstance, bodyOnly: true, creator: creator, supervisor: supervisor]
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
			return
        }
        else if(noteItemInstance.status != "draft"){
            flash.message = "You are not allowed to delete finalized notes"
            redirect(action: "show", id: id)
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

	def signNote(Long id){
		println(id + " signNote")
		def noteItemInstance = NoteItem.get(id)
		if (!noteItemInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
			redirect(action: "list")
			return
		}else if(!noteAccessService.grantAccess(noteItemInstance)){
			flash.message = "You do not have access to this particular note!"
			redirect(action: "list")
			return
		}
		/* Note exists and user has access */
		User currentUser = springSecurityService.currentUser
		
		def hasKeys = UserPGP.countByOwner(currentUser)
		if(UserPGP.countByOwner(currentUser) == 0){
			println("User does not have any keys!")
			redirect(action: "createKeys", id: id, params:[bodyOnly: true])
			return
		}
		def users = null
		User lastSupervisor = null
		if(noteItemInstance.creator == currentUser){
			/* currentUser is the author */
			users = User.findAllByUsernameNotEqual(currentUser.toString(), [sort:"userRealName"])
			def lastNote = NoteItem.findByCreatorAndSupervisorIsNotNull(currentUser, [sort: "id", order: "desc"])
			if(lastNote != null){
				lastSupervisor = lastNote.supervisor
			}
		}
		[noteItemInstance: noteItemInstance, users: users, lastSupervisor: lastSupervisor, bodyOnly: true]
	}
	
	def signNoteData(Long id){
		println(id + " signNoteData")
		def noteItemInstance = NoteItem.get(id)
		if (!noteItemInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
			redirect(action: "list")
			return
		}else if(!noteAccessService.grantAccess(noteItemInstance)){
			flash.message = "You do not have access to this particular note!"
			redirect(action: "list")
			return
		}
		
		/* Note exists and user has access */
		User currentUser = springSecurityService.currentUser
		String passphrase = params.password
		params.remove('password')
		String encodedPassword = springSecurityService.encodePassword(passphrase)
		/* Check if password is correct */
		if(!currentUser.password.equals(encodedPassword)){
			flash.message = "Incorrect password!"
			redirect(action: "signNote", id: id, params:[bodyOnly: true])
			return
		}
		
		/* Get secret key for user */
		UserPGP userKeys = UserPGP.findByOwner(currentUser)
		String secretKey = userKeys.secretKey
		
		PGPCryptoBC pgp = new PGPCryptoBC()
		pgp.setSecretKey(secretKey)

		if(noteItemInstance.creator == currentUser){
			/* If the author is signing, a supervisor is added to the note */
			def supervisor = User.find{id == params.supervisor}
			params.remove('supervisor')
			params.remove('status')
			
			/* Add the supervisor to the noteItem */
			noteItemInstance.properties = params
			noteItemInstance.supervisor = supervisor
			
			noteItemInstance.authorSignedData = pgp.signData(noteItemInstance.note,passphrase)
			noteItemInstance.status = 'final'
		}else if(noteItemInstance.supervisor == currentUser){
			noteItemInstance.supervisorSignedData = pgp.signData(noteItemInstance.note,passphrase)
			noteItemInstance.status = 'signed'
		}
		if (!noteItemInstance.save(flush: true)) {
			flash.message = "Could not save!"
			redirect(action: "signNote", id: id, params:[bodyOnly: true])
			return
		}
		flash.message = message(code: 'Note was signed', args: [message(code: 'noteItem.label', default: 'NoteItem'), noteItemInstance.id])
		redirect(action: "show", id: id, params:[bodyOnly: true])
	}
	
	def createKeys(Long id){
		println(id + "create keys")
		[UserPGPInstance: new UserPGP(params), noteId: id, bodyOnly: true]
	}
	
	def saveKeys(Long id){
		println(id + "save keys")
		User currentUser = springSecurityService.currentUser
		
		String passphrase = params.password
		String username = currentUser.toString()
		params.remove('password')
		String encodedPassword = springSecurityService.encodePassword(passphrase)
		
		if(!currentUser.password.equals(encodedPassword)){
			flash.message = "Incorrect password!"
            redirect(action: "createKeys", id: id, params:[bodyOnly: true])
			return
		}
		
		/* Generate keys for user */
		PGPCryptoBC pgp = new PGPCryptoBC()
		pgp.generateKeys(username, passphrase)
		
		/* Store keys on a new UserPGP instance for the user */
		def UserPGPInstance = new UserPGP()
		UserPGPInstance.owner = currentUser
		
		UserPGPInstance.secretKey = pgp.getSecretKey()
		UserPGPInstance.publicKey = pgp.getPublicKey()
		
		if(!UserPGPInstance.save(flush: true, failOnError:true)) {
			flash.message = "Something went wrong when generating your keys!"
			redirect(action: "createKeys", params:[bodyOnly: true])
			return
		}
		flash.message = 'Public and private keys has been generated for your account'
		redirect(action: "signNote", id: id, params:[bodyOnly: true])
	}
}