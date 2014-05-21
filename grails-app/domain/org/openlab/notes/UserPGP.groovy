package org.openlab.notes

import org.openlab.security.User

import cr.co.arquetipos.crypto.*

class UserPGP {

	User owner
//	cr.co.arquetipos.crypto.PGP userKeys
	
    static constraints = {
		owner unique: true
    }
}
