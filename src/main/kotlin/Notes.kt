data class Note(
    val nid: Int = 0,
    var title: String,
    var text: String,
    var privacy: Privacy = Privacy.ALL,
    var commentPrivacy: Privacy = Privacy.ALL,
    // доп. поля
    val date: Long, // для сортировки по дате добавления
    val ownerId: Int // для метода get
)

enum class Privacy(i: Int) {
    ALL(0), FRIENDS(1), FRIENDS_OF_FRIENDS(2), ONLY_ME(3)
}

data class Comment(
    val cid: Int = 0,
    val noteId: Int,
    var ownerId: Int,
    val replyTo: Int? = null,
    var message: String,
    val guid: String,
    // доп. поле
    val date: Long, // для сортировки по дате добавления
    var deleted: Boolean = false // для отмены удаления
)

object NotesService {
    private var counter: Int = 0
    private var notes = mutableListOf<Note>()

    private var commentCounter: Int = 0
    private var comments = mutableListOf<Comment>()

    fun add(note: Note): Int {
        notes += note.copy(nid = ++counter)
        return notes.last().nid
    }

    fun createComment(comment: Comment): Int {
        val noteId = comment.noteId
        val nid = notes.find { it.nid == noteId }?.nid ?: throw NoteNotFoundException("No note with id $noteId")
        comments += comment.copy(cid = ++commentCounter, noteId = nid)
        return comments.last().cid
    }

    fun edit(noteId: Int, title: String, text: String, privacy: Privacy, commentPrivacy: Privacy): Boolean {
        for ((index, el) in notes.withIndex()) {
            if (el.nid == noteId) {
                notes[index].title = title
                notes[index].text = text
                notes[index].privacy = privacy
                notes[index].commentPrivacy = commentPrivacy
                return true
            }
        }
        return false
    }

    fun editComment(commentId: Int, ownerId: Int, message: String): Boolean {
        for ((index, el) in comments.withIndex()) {
            if (el.cid == commentId && el.ownerId == ownerId && !el.deleted) {
                comments[index].message = message
                return true
            }
        }
        return false
    }

    fun clear() {
        counter = 0
        notes.clear()
        commentCounter = 0
        comments.clear()
    }

    fun get(ids: List<Int> = emptyList(), userId: Int, count: Int?, sort: Sort = Sort.ASC): List<Note> {
        var list = notes.filter { it.ownerId == userId }
        if (list.isEmpty()) return list
        if (ids.isNotEmpty()) list = list.filter { ids.contains(it.nid) }
        list = if (sort == Sort.DESC) list.sortedByDescending { it.date } else list.sortedBy { it.date }
        if (count != null && count < list.size) list = list.subList(0, count)
        return list
    }

    fun getById(id: Int, ownerId: Int): Note? {
        return notes.filter { it.ownerId == ownerId }.find { it.nid == id }
    }

    fun getComments(noteId: Int, ownerId: Int, count: Int?, sort: Sort = Sort.ASC): List<Comment> {
        var list = comments.filter { it.ownerId == ownerId && it.noteId == noteId && !it.deleted }
        if (list.isEmpty()) return list
        list = if (sort == Sort.DESC) list.sortedByDescending { it.date } else list.sortedBy { it.date }
        if (count != null && count < list.size) list = list.subList(0, count)
        return list
    }

    fun delete(noteId: Int): Boolean {
//        for ((index, el) in notes.withIndex()) {
//            if (el.nid == noteId) {
//                notes.drop(index)
//                val list = getComments(noteId, el.ownerId, null)
//                for (el in list) {
//                    deleteComment(el.cid, el.ownerId)
//                }
//                return true
//            }
//        }
        val note = notes.find { it.nid == noteId } ?: return false
        val list = getComments(noteId, note.ownerId, null)
        for (el in list) {
            deleteComment(el.cid, el.ownerId)
        }
        return notes.remove(note)
    }

    fun deleteComment(commentId: Int, ownerId: Int): Boolean {
        for ((index, el) in comments.withIndex()) {
            if (el.cid == commentId && el.ownerId == ownerId && !el.deleted) {
                comments[index].deleted = true
                return true
            }
        }
        return false
    }

    fun restoreComment(commentId: Int, ownerId: Int): Boolean {
        for ((index, el) in comments.withIndex()) {
            if (el.cid == commentId && el.ownerId == ownerId && el.deleted) {
                comments[index].deleted = false
                return true
            }
        }
        return false
    }

    fun print() {
        println(notes)
        println(comments)
    }

}

class NoteNotFoundException(message: String) : RuntimeException(message)

enum class Sort {
    ASC, DESC
}