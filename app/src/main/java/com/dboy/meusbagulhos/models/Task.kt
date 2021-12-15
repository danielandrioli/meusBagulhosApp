package com.dboy.meusbagulhos.models

import java.text.DateFormat
import java.util.*

class Task(var text: String) {
    var dateCreation: String = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
        private set
    var dateEdition: String = ""
        private set
    var dateDone: String = ""
        private set
    var isDone: Boolean = false
        private set
    var id: Int = 0
        private set
    var positionUndone: Int = -1
    var positionDone: Int = -1

    constructor(
        id: Int, text: String, dateCreation: String,
        isDone: Boolean = false, dateEdition: String = "", dateDone: String = "",
        positionUndone: Int = -1, positionDone: Int = -1
    ) : this(text) {
        this.dateCreation = dateCreation
        this.dateEdition = dateEdition
        this.dateDone = dateDone
        this.isDone = isDone
        this.id = id
        this.positionUndone = positionUndone
        this.positionDone = positionDone
    }

    override fun toString(): String {
        return "id: $id - posU: $positionUndone - posD: $positionDone -\ntext: $text"
    }
}