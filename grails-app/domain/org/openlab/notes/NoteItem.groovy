package org.openlab.notes

import org.openlab.main.DataObject

class NoteItem{
	String title
	String status
	String note
	Date dateCreated
	long timeSpent = 0L
	
	String toString(){
		title
	}
	
    static constraints = {
		title blank: false
		note blank: false
		status inList:["open", "final"]
    }
	static mapping = {
		//table 'olfNoteItem'
	}
}
