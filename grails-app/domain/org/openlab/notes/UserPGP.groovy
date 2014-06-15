package org.openlab.notes

import org.openlab.security.User

import cr.co.arquetipos.crypto.*

class UserPGP {

	User owner
	String secretKey
	String publicKey

	static mapping = {
		secretKey type: 'text'
		publicKey type: 'text'
		table 'olfUserPGP'
	}
    static constraints = {
		owner unique: true
    }
}
