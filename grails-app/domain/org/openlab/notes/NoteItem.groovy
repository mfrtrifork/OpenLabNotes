package org.openlab.notes

import org.openlab.main.DataObject
import org.openlab.security.User


class NoteItem extends DataObject{
	String title
	String status
	String note
	String finalizedNote
	User supervisor
	
	//Date dateCreated
	//long timeSpent = 0L
	
	
	String toString(){
		title
	}
	
    static constraints = {
		title(blank:false)
		//title blank: false
		note blank: false
		finalizedNote nullable: true
		supervisor nullable: true
		status inList:["open", "final", "signed"]
    }
	static mapping = {
		note type: 'text'
		//table 'olfNoteItem'
	}
}
