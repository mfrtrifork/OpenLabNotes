package org.openlab.notes

import org.openlab.main.DataObject
import org.openlab.security.User

class NoteItem extends DataObject{
	String title
	String status
	String note
	String finalizedNote
	User supervisor
	
	String toString(){
		title
	}
	
    static constraints = {
		title blank: false
		note blank: false
		finalizedNote nullable: true
		supervisor nullable: true
		status inList:["open", "final", "signed"]
    }
	static mapping = {
		note type: 'text'
		table 'olfNoteItem'
	}

    static String type = "noteItem"
    static String typeLabel = "NoteItem"
}
