package com.dboy.meusbagulhos.models

import java.io.Serializable
import java.text.DateFormat
import java.util.*

class Tarefa(var texto: String): Serializable{//nem precisa do Serializable
    var dataCriacao: String = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
        private set
    var dataEdicao: String = ""
        private set
    var dataFinalizacao: String = ""
        private set
    var isFinalizado: Boolean = false
        private set
    var id: Int = 0
        private set
    var positionUndone: Int = -1
    var positionDone: Int = -1

    constructor(id: Int, texto: String, dataCriacao: String,
                isFinalizado: Boolean = false, dataEdicao: String = "", dataFinalizacao: String = "",
                positionUndone: Int = -1, positionDone: Int = -1): this(texto){
        this.dataCriacao = dataCriacao
        this.dataEdicao = dataEdicao
        this.dataFinalizacao = dataFinalizacao
        this.isFinalizado = isFinalizado
        this.id = id
        this.positionUndone = positionUndone
        this.positionDone = positionDone
    }

    override fun toString(): String {
        return "id: $id - posU: $positionUndone - posD: $positionDone -\ntexto: $texto"
    }
}