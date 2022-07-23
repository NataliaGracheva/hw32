import org.junit.Assert
import org.junit.Before

import org.junit.Test
import java.util.*

class NoteTest {
    val note1 = Note(title = "Some Title 1", text = "some text 1", date = 123, ownerId = 123)
    val note2 = Note(title = "Some Title 2", text = "some text 2", date = 456, ownerId = 999)
    val note3 = Note(title = "Some Title 3", text = "some text 3", date = 789, ownerId = 123)
    val note4 = Note(title = "Some Title 4", text = "some text 4", date = 999, ownerId = 123)

    @Before
    fun setUp() {
        NotesService.clear()
    }

    @Test
    fun shouldAddNote() {
        val nid = NotesService.add(note1)
        Assert.assertEquals(1, nid)
    }

    @Test
    fun shouldAddComment() {
        val nid = NotesService.add(note1)
        val cid = NotesService.createComment(
            Comment(
                noteId = nid,
                ownerId = 123,
                message = "some message",
                guid = UUID.randomUUID().toString(),
                date = 123
            )
        )
        Assert.assertEquals(1, cid)
    }

    @Test
    fun shouldEditNote() {
        val nid = NotesService.add(note1)
        val bool = NotesService.edit(nid, "Updated Title", "updated text", Privacy.FRIENDS, Privacy.FRIENDS)
        Assert.assertTrue(bool)
    }

    @Test
    fun shouldEditComment() {
        val nid = NotesService.add(note1)
        val cid = NotesService.createComment(
            Comment(
                noteId = nid,
                ownerId = 123,
                message = "some message",
                guid = UUID.randomUUID().toString(),
                date = 123
            )
        )
        val bool = NotesService.editComment(cid, ownerId = 123, "updated message")
        Assert.assertTrue(bool)
    }

    @Test
    fun shouldNotEditNote() {
        val bool = NotesService.edit(1, "Updated Title", "updated text", Privacy.FRIENDS, Privacy.FRIENDS)
        Assert.assertFalse(bool)
    }

    @Test
    fun shouldNotEditComment() {
        val bool = NotesService.editComment(1, ownerId = 123, "updated message")
        Assert.assertFalse(bool)
    }

    @Test
    fun shouldReturnNotes() {
        val nid1 = NotesService.add(note1)
        val nid2 = NotesService.add(note2)
        val nid3 = NotesService.add(note3)
        val nid4 = NotesService.add(note4)
        val expectedList = listOf(note1.copy(nid = nid1), note3.copy(nid = nid3))
        val actualList = NotesService.get(listOf(nid4, nid2, nid3, nid1), 123, 2)
        Assert.assertEquals(expectedList, actualList)
    }

    @Test
    fun shouldReturnNotesDesc() {
        val nid1 = NotesService.add(note1)
        val nid2 = NotesService.add(note2)
        val nid3 = NotesService.add(note3)
        val nid4 = NotesService.add(note4)
        val expectedList = listOf(note4.copy(nid = nid4), note3.copy(nid = nid3))
        val actualList = NotesService.get(listOf(nid4, nid2, nid3, nid1), 123, 2, Sort.DESC)
        Assert.assertEquals(expectedList, actualList)
    }

    @Test
    fun shouldNotReturnNotes() {
        val list = NotesService.get(userId = 123, count = null)
        Assert.assertTrue(list.isEmpty())
    }

    @Test
    fun shouldReturnNote() {
        val nid = NotesService.add(note1)
        val note = NotesService.getById(nid, 123)
        Assert.assertEquals(note1.copy(nid = nid), note)
    }

    @Test
    fun shouldNotReturnNote() {
        val note = NotesService.getById(1, 123)
        Assert.assertNull(note)
    }

    @Test
    fun shouldReturnComments() {
        val nid1 = NotesService.add(note1)
        val comment1 = Comment(
            noteId = nid1,
            ownerId = 123,
            message = "some message 1",
            guid = UUID.randomUUID().toString(),
            date = 123
        )
        val comment2 = Comment(
            noteId = nid1,
            ownerId = 123,
            message = "some message 2",
            guid = UUID.randomUUID().toString(),
            date = 124
        )
        val comment3 = Comment(
            noteId = nid1,
            ownerId = 123,
            message = "some message 3",
            guid = UUID.randomUUID().toString(),
            date = 125
        )
        val cid1 = NotesService.createComment(comment1)
        val cid2 = NotesService.createComment(comment2)
        val cid3 = NotesService.createComment(comment3)
        val expectedList = listOf(comment1.copy(cid = cid1, noteId = nid1), comment2.copy(cid = cid2, noteId = nid1))
        val actualList = NotesService.getComments(nid1, 123, 2)
        Assert.assertEquals(expectedList, actualList)
    }

    @Test
    fun shouldReturnCommentsDesc() {
        val nid1 = NotesService.add(note1)
        val comment1 = Comment(
            noteId = nid1,
            ownerId = 123,
            message = "some message 1",
            guid = UUID.randomUUID().toString(),
            date = 123
        )
        val comment2 = Comment(
            noteId = nid1,
            ownerId = 123,
            message = "some message 2",
            guid = UUID.randomUUID().toString(),
            date = 124
        )
        val comment3 = Comment(
            noteId = nid1,
            ownerId = 123,
            message = "some message 3",
            guid = UUID.randomUUID().toString(),
            date = 125
        )
        val cid1 = NotesService.createComment(comment1)
        val cid2 = NotesService.createComment(comment2)
        val cid3 = NotesService.createComment(comment3)
        val expectedList = listOf(comment3.copy(cid = cid3, noteId = nid1), comment2.copy(cid = cid2, noteId = nid1))
        val actualList = NotesService.getComments(nid1, 123, 2, sort = Sort.DESC)
        Assert.assertEquals(expectedList, actualList)
    }

    @Test
    fun shouldNotReturnComments() {
        val list = NotesService.getComments(1, 123, null)
        Assert.assertTrue(list.isEmpty())
    }

    @Test
    fun shouldDeleteNote() {
        val nid = NotesService.add(note1)
        val bool = NotesService.delete(nid)
        Assert.assertTrue(bool)
    }

    @Test
    fun shouldDeleteComment() {
        val nid = NotesService.add(note1)
        val cid = NotesService.createComment(
            Comment(
                noteId = nid,
                ownerId = 123,
                message = "some message",
                guid = UUID.randomUUID().toString(),
                date = 123
            )
        )
        val bool = NotesService.deleteComment(cid, 123)
        Assert.assertTrue(bool)
    }

    @Test
    fun shouldNotDeleteNote() {
        val bool = NotesService.delete(1)
        Assert.assertFalse(bool)
    }

    @Test
    fun shouldNotDeleteComment() {
        val bool = NotesService.deleteComment(1, 123)
        Assert.assertFalse(bool)
    }

    @Test
    fun shouldRestoreComment() {
        val nid = NotesService.add(note1)
        val cid = NotesService.createComment(
            Comment(
                noteId = nid,
                ownerId = 123,
                message = "some message",
                guid = UUID.randomUUID().toString(),
                date = 123
            )
        )
        NotesService.deleteComment(cid, 123)
        val bool = NotesService.restoreComment(cid, 123)
        Assert.assertTrue(bool)
    }

    @Test
    fun shouldNotRestoreComment() {
        val bool = NotesService.restoreComment(1, 123)
        Assert.assertFalse(bool)
    }

    @Test
    fun shouldNotReturnDeletedNote() {
        val nid = NotesService.add(note1)
        NotesService.delete(nid)
        val list = NotesService.get(userId = 123, count = null)
        Assert.assertTrue(list.isEmpty())
    }

    @Test
    fun shouldNotReturnDeletedNoteById() {
        val nid = NotesService.add(note1)
        NotesService.delete(nid)
        val note = NotesService.getById(nid, 123)
        Assert.assertNull(note)
    }

    @Test
    fun shouldNotReturnDeletedComment() {
        val nid = NotesService.add(note1)
        val cid = NotesService.createComment(
            Comment(
                noteId = nid,
                ownerId = 123,
                message = "some message",
                guid = UUID.randomUUID().toString(),
                date = 123
            )
        )
        NotesService.deleteComment(cid, 123)
        val list = NotesService.getComments(nid, 123, null)
        Assert.assertTrue(list.isEmpty())
    }

    @Test
    fun shouldNotReturnCommentOfDeletedNote() {
        val nid = NotesService.add(note1)
        val cid = NotesService.createComment(
            Comment(
                noteId = nid,
                ownerId = 123,
                message = "some message",
                guid = UUID.randomUUID().toString(),
                date = 123
            )
        )
        NotesService.delete(nid)
        val list = NotesService.getComments(nid, 123, null)
        Assert.assertTrue(list.isEmpty())
    }
}