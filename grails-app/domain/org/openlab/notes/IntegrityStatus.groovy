package org.openlab.notes

class IntegrityStatus {
	
	Date dateCreated
	boolean integrity

    static constraints = {
		dateCreated nullable: false
		integrity nullable: false
    }
}
