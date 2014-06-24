import org.openlab.security.User

import org.openlab.notes.IntegrityStatus

import org.openlab.notes.NoteItem
import org.openlab.notes.UserPGP
import crypttools.PGPCryptoBC



class NoteValidationJob {
  def static triggers = {
	  cron name: 'myIntegrityTrigger', cronExpression: "0 0 4 * * ?"
//    simple name: 'mySimpleTrigger', startDelay: 30000, repeatInterval: 5000  
  }
  def group = "MyGroup"
  def execute(){
	  
    def notes = NoteItem.findAll()
	boolean dataUntampered = true
	for(noteItemInstance in notes){
		if(noteItemInstance.status == 'final'){
//			println("Validating final  note("+noteItemInstance.id+"): " + noteItemInstance.title)
			PGPCryptoBC pgp = new PGPCryptoBC()
			UserPGP userKeys = UserPGP.findByOwner(noteItemInstance.creator)
			if(!pgp.validateData(noteItemInstance.authorSignedData, userKeys.publicKey)){
				dataUntampered = false
			}
		}
		if(noteItemInstance.status == 'signed'){
//			println("Validating signed note("+noteItemInstance.id+"): " + noteItemInstance.title)
			PGPCryptoBC pgp = new PGPCryptoBC()
			UserPGP userKeys = UserPGP.findByOwner(noteItemInstance.creator)
			if(!pgp.validateData(noteItemInstance.authorSignedData, userKeys.publicKey)){
				dataUntampered = false
			}
			PGPCryptoBC pgp2 = new PGPCryptoBC()
			UserPGP userKeys2 = UserPGP.findByOwner(noteItemInstance.supervisor)
			if(!pgp2.validateData(noteItemInstance.supervisorSignedData, userKeys2.publicKey)){
				dataUntampered = false
			}
		}
	}
	def integrityStatusInstance = new IntegrityStatus()
	integrityStatusInstance.dateCreated = new Date()
	integrityStatusInstance.integrity = dataUntampered
	println(integrityStatusInstance.dateCreated)
	println(integrityStatusInstance.integrity)
	if (!integrityStatusInstance.save(flush: true, failOnError:true)) {
		println("Failed to save integrity")
	}
  }
}
