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
	def authorSign(Long id) {
		/* Check if user has keys for encryption */
		def hasKeys = UserPGP.countByOwner(springSecurityService.currentUser)
		if(hasKeys == 0){
			params.from = 'authorSign'
			params.noteId = id
			redirect(action: "createKeys", params: params)
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		def users = User.findAll {
			username != springSecurityService.currentUser.toString()
			/* TODO: enabled = true */
		}
		def noteItemInstance = NoteItem.get(id)
		if (!noteItemInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
			redirect(action: "list")
			return
		}else if(!noteAccessService.grantAccess(noteItemInstance)){
            flash.message = "You do not have access to this particular note!"
            redirect(action: "list")
        }
		
		[noteItemInstance: noteItemInstance, users:users, bodyOnly: true]
	}
	def authorSignNote(){
		/* Check of noteItem exists */
		def noteItemInstance = NoteItem.get(params.id)
		if (!noteItemInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
			redirect(action: "list")
			return
		}else if(!noteAccessService.grantAccess(noteItemInstance)){
            flash.message = "You do not have access to this particular note!"
            redirect(action: "list")
        }
		
		User loggedInUser = springSecurityService.currentUser
		
		String passphrase = params.password
		String username = loggedInUser
		params.remove('password')
		String encodedPassword = springSecurityService.encodePassword(passphrase)
		
		/* Check password */
		if(!loggedInUser.password.equals(encodedPassword)){
			flash.message = "Incorrect password!"
			params.bodyOnly = true
			redirect(action: "authorSign", params: params)
			return;
		}
		
		/* Find supervisor from id */
		def supervisor = User.find{id == params.supervisor}
		params.remove('supervisor')
		params.remove('status')
		
		noteItemInstance.properties = params
		/* Add the supervisor to the noteItem */
		noteItemInstance.supervisor = supervisor
		
		/* Get secret key for user */
		UserPGP userKeys = UserPGP.findByOwner(loggedInUser)
		String secretKey = userKeys.secretKey
		
		PGPCryptoBC pgp = new PGPCryptoBC()
		pgp.setSecretKey(secretKey)
				
		noteItemInstance.authorSignedData = pgp.signData(noteItemInstance.note,passphrase) 
		noteItemInstance.status = 'final'
		if (!noteItemInstance.save(flush: true)) {
			redirect(action: "list")
			return
		}
		flash.message = message(code: 'Note was finalized', args: [message(code: 'noteItem.label', default: 'NoteItem'), noteItemInstance.id])
		redirect(action: "show", id: noteItemInstance.id, params:[bodyOnly: true])
	}
	
	def supervisorSign(Long id) {
		/* Check if user has keys for encryption */
		def hasKeys = UserPGP.countByOwner(springSecurityService.currentUser)
		if(hasKeys == 0){
			params.from = 'supervisorSign'
			params.noteId = id
			redirect(action: "createKeys", params: params)
		}
		
		def noteItemInstance = NoteItem.get(id)
		if (!noteItemInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
			redirect(action: "list")
			return
		}else if(!noteAccessService.grantAccess(noteItemInstance)){
			flash.message = "You do not have access to this particular note!"
			redirect(action: "list")
		}
		[noteItemInstance: noteItemInstance, bodyOnly: true]
	}
	def supervisorSignNote() {
		/* Check of noteItem exists */
		def noteItemInstance = NoteItem.get(params.id)
		if (!noteItemInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'noteItem.label', default: 'NoteItem'), id])
			redirect(action: "list")
			return
		}else if(!noteAccessService.grantAccess(noteItemInstance)){
            flash.message = "You do not have access to this particular note!"
            redirect(action: "list")
			return
        }
		
		User loggedInUser = springSecurityService.currentUser
		
		String passphrase = params.password
		String username = loggedInUser
		params.remove('password')
		String encodedPassword = springSecurityService.encodePassword(passphrase)
		
		/* Check password */
		if(!loggedInUser.password.equals(encodedPassword)){
			flash.message = "Incorrect password!"
			params.bodyOnly = true
			redirect(action: "authorSign", params: params)
			return;
		}
		
		params.remove('status')
		
		noteItemInstance.properties = params
		
		/* Get secret key for user */
		UserPGP userKeys = UserPGP.findByOwner(loggedInUser)
		String secretKey = userKeys.secretKey
		
		PGPCryptoBC pgp = new PGPCryptoBC()
		pgp.setSecretKey(secretKey)
				
		noteItemInstance.supervisorSignedData = pgp.signData(noteItemInstance.note,passphrase) 
		noteItemInstance.status = 'signed'
		if (!noteItemInstance.save(flush: true)) {
			redirect(action: "list")
			return
		}
		flash.message = message(code: 'Note was signed', args: [message(code: 'noteItem.label', default: 'NoteItem'), noteItemInstance.id])
		redirect(action: "show", id: noteItemInstance.id, params:[bodyOnly: true])
	}
	
	def createKeys(){
		def from = params.from
		def noteId = params.noteId
		[UserPGPInstance: new UserPGP(params), from :from, noteId: noteId, bodyOnly: true]
	}
	
	def saveKeys(){
		def from = params.from
		def noteId = params.noteId
		params.remove('from')
		params.remove('noteId')
		User loggedInUser = springSecurityService.currentUser
		
		String passphrase = params.password
		String username = loggedInUser
		params.remove('password')
		String encodedPassword = springSecurityService.encodePassword(passphrase)
		
		if(!loggedInUser.password.equals(encodedPassword)){
			flash.message = "Incorrect password!"
            redirect(action: "createKeys", params:[bodyOnly: true])
			return
		}
		
		/* Generate keys for user */
		PGPCryptoBC pgp = new PGPCryptoBC()
		pgp.generateKeys(username, passphrase)
		
		/* Store keys on a new UserPGP instance for the user */
		def UserPGPInstance = new UserPGP()
		UserPGPInstance.owner = loggedInUser
		
		UserPGPInstance.secretKey = pgp.getSecretKey()
		UserPGPInstance.publicKey = pgp.getPublicKey()
		
		if(!UserPGPInstance.save(flush: true, failOnError:true)) {
			flash.message = "Something went wrong!"
			redirect(action: "createKeys", params:[bodyOnly: true])
			return
		}
		flash.message = 'Public and private keys has been generated for your account'
		if(from.equals('authorSign')){
			println('author')
			redirect(action: "authorSign", params:[bodyOnly: true, id:noteId], id:noteId)
			return
		}
		if(from.equals('supervisorSign')){
			println('supervisor')
			redirect(action: "supervisorSign", params:[bodyOnly: true], id:noteId)
			return
		}
		println('none')
		redirect(action: "list", params:[bodyOnly: true])
	}
}