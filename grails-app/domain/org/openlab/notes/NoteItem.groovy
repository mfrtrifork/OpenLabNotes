package org.openlab.notes

class NoteItem{
	String title
	String status
	String note
	String finalizedNote
	Date dateCreated
	long timeSpent = 0L
	
	
	String toString(){
		title
	}
	
    static constraints = {
		title blank: false
		note blank: false
		finalizedNote nullable: true
		status inList:["open", "final", "signed"]
    }
	static mapping = {
		note type: 'text'
		//table 'olfNoteItem'
	}
}
