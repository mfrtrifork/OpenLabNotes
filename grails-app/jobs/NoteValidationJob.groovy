import org.openlab.security.User
import org.openlab.settings.UserSetting
import org.openlab.notes.NoteItem
import org.openlab.notes.UserPGP
import crypttools.PGPCryptoBC

class NoteValidationJob {
  static triggers = {
    simple name: 'mySimpleTrigger', startDelay: 30000, repeatInterval: 5000  
  }
  def group = "MyGroup"
  def settingsService
  def execute(){
    def notes = NoteItem.findAll()
	boolean dataUntampered = true
	for(noteItemInstance in notes){
		if(noteItemInstance.status == 'final'){
			println("Validating final  note("+noteItemInstance.id+"): " + noteItemInstance.title)
			PGPCryptoBC pgp = new PGPCryptoBC()
			UserPGP userKeys = UserPGP.findByOwner(noteItemInstance.creator)
			pgp.setPublicKey(userKeys.publicKey)
			if(!pgp.validateData(noteItemInstance.authorSignedData)){
				dataUntampered = false
			}
		}
		if(noteItemInstance.status == 'signed'){
			println("Validating signed note("+noteItemInstance.id+"): " + noteItemInstance.title)
			PGPCryptoBC pgp = new PGPCryptoBC()
			UserPGP userKeys = UserPGP.findByOwner(noteItemInstance.creator)
			pgp.setPublicKey(userKeys.publicKey)
			if(!pgp.validateData(noteItemInstance.authorSignedData)){
				dataUntampered = false
			}
			userKeys = UserPGP.findByOwner(noteItemInstance.supervisor)
			pgp.setPublicKey(userKeys.publicKey)
			if(!pgp.validateData(noteItemInstance.supervisorSignedData)){
				dataUntampered = false
			}
		}
	}
	println(dataUntampered)
	def setting = UserSetting.findByKey("dataIntegrity")
	if(setting == null){
		setting = new UserSetting(key: "dataIntegrity", value: dataUntampered)
		setting.add()
		//settingsService.setSetting(key: "dataIntegrity", value: dataUntampered)
	}
	setting = UserSetting.findByKey("dataIntegrity")
	println(setting)
  }
}
