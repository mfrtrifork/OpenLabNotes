package org.openlab.module.menu

import groovy.xml.MarkupBuilder
import org.openlab.module.MenuModule

class NoteMenuModule implements MenuModule{

    def getPriority()
    {
        6
    }

    def getMenu()
    {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        def controller = "NoteItem"

        xml.root
                {
                    submenu(label: 'Notes')
                            {
                                menuitem(controller: controller, action: 'create', label: 'Create new Note')
                                menuitem(controller: controller, action: 'list', label: 'List Notes')
//                                menuitem(controller: controller, action: 'listStockItemsToOrder', label: 'List Lab Stocks that need ordering')
                            }
                }

        return writer.toString()
    }
}
