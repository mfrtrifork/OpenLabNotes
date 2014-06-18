package org.openlab.notes

import org.openlab.main.DataObject
import org.openlab.genetracker.CellLine
import org.openlab.genetracker.CellLineData
import org.openlab.genetracker.Recombinant
import org.openlab.security.User

class NoteItem extends DataObject{
	String title
	String status
	String note
	String authorSignedData
	String supervisorSignedData
	User supervisor
	
	static hasMany = [cellLine: CellLine, cellLineData: CellLineData, recombinant: Recombinant]
	
	String toString(){
		title
	}
	
    static constraints = {
		title blank: false
		note blank: false
		authorSignedData nullable: true
		supervisorSignedData nullable: true
		supervisor nullable: true
		status inList:["draft", "final", "signed"]
    }
	static mapping = {
		note type: 'text'
		authorSignedData type: 'text'
		supervisorSignedData type: 'text'
		table 'olfNoteItem'
	}

    static String type = "noteItem"
    static String typeLabel = "NoteItem"
}
