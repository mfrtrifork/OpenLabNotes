package org.openlab.notes



import org.junit.*
import grails.test.mixin.*

@TestFor(NoteItemController)
@Mock(NoteItem)
class NoteItemControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/noteItem/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.noteItemInstanceList.size() == 0
        assert model.noteItemInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.noteItemInstance != null
    }

    void testSave() {
        controller.save()

        assert model.noteItemInstance != null
        assert view == '/noteItem/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/noteItem/show/1'
        assert controller.flash.message != null
        assert NoteItem.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/noteItem/list'

        populateValidParams(params)
        def noteItem = new NoteItem(params)

        assert noteItem.save() != null

        params.id = noteItem.id

        def model = controller.show()

        assert model.noteItemInstance == noteItem
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/noteItem/list'

        populateValidParams(params)
        def noteItem = new NoteItem(params)

        assert noteItem.save() != null

        params.id = noteItem.id

        def model = controller.edit()

        assert model.noteItemInstance == noteItem
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/noteItem/list'

        response.reset()

        populateValidParams(params)
        def noteItem = new NoteItem(params)

        assert noteItem.save() != null

        // test invalid parameters in update
        params.id = noteItem.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/noteItem/edit"
        assert model.noteItemInstance != null

        noteItem.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/noteItem/show/$noteItem.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        noteItem.clearErrors()

        populateValidParams(params)
        params.id = noteItem.id
        params.version = -1
        controller.update()

        assert view == "/noteItem/edit"
        assert model.noteItemInstance != null
        assert model.noteItemInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/noteItem/list'

        response.reset()

        populateValidParams(params)
        def noteItem = new NoteItem(params)

        assert noteItem.save() != null
        assert NoteItem.count() == 1

        params.id = noteItem.id

        controller.delete()

        assert NoteItem.count() == 0
        assert NoteItem.get(noteItem.id) == null
        assert response.redirectedUrl == '/noteItem/list'
    }
}
