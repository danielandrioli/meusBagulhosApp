package com.dboy.meusbagulhos.models

import java.io.Serializable
import java.text.DateFormat
import java.util.*

class Tarefa(var texto: String): Serializable{
    var dataCriacao: String = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
        private set
    var dataEdicao: String? = null
        private set
    var dataFinalizacao: String? = null
        private set
    var isFinalizado: Boolean = false
        private set
    var id: Int = 0
        private set
    var positionUndone: Int? = null
    var positionDone: Int? = null

    constructor(id: Int, texto: String, dataCriacao: String,
                isFinalizado: Boolean = false, dataEdicao: String? = null, dataFinalizacao: String? = null,
                positionUndone: Int? = null, positionDone: Int? = null): this(texto){
        this.dataCriacao = dataCriacao
        this.dataEdicao = dataEdicao
        this.dataFinalizacao = dataFinalizacao
        this.isFinalizado = isFinalizado
        this.id = id
        this.positionUndone = positionUndone
        this.positionDone = positionDone
    }

    fun editaTexto(novoTexto: String) { //DELETAR MÉTODO. TAREFADAO SERÁ RESPONSÁVEL POR EDIÇÃO
        texto = novoTexto
        dataEdicao = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

        //ATUALIZAR NO DB
    }

    fun finalizaTarefa() { //DELETAR MÉTODO. TAREFADAO SERÁ RESPONSÁVEL POR FINALIZAÇÃO
        dataFinalizacao = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
        isFinalizado = true


        //ATUALIZAR NO DB
    }

    fun desfinalizaTarefa(){ //DELETAR MÉTODO. TAREFADAO SERÁ RESPONSÁVEL POR DESFINALIZAÇÃO
        dataFinalizacao = null
        isFinalizado = false

        //ATUALIZAR NO DB
    }
}