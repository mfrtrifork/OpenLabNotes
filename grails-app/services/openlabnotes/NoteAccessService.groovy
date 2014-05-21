package openlabnotes

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

class NoteAccessService {

    def springSecurityService

    def grantAccess(def noteItemInstance) {

        if (springSecurityService.currentUser == noteItemInstance.creator) {
            return true
        } else if (springSecurityService.currentUser == noteItemInstance.supervisor) {
            return true
        }
        else return false
    }
}
