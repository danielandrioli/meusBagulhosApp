package com.dboy.meusbagulhos.models

import java.text.DateFormat
import java.util.*

class Tarefa(texto: String) {
    var texto: String = texto
        private set //método setter vai modificar data de edicao
    val dataCriacao: String = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
    var dataEdicao: String? = null
        private set
    var dataFinalizacao: String? = null
        private set
    var isFinalizado: Boolean = false
    var isEditado: Boolean = false
        private set

    fun editaTexto(novoTexto: String) {
        texto = novoTexto
        dataEdicao = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
        isEditado = true
    }

    fun finalizaTarefa() {
        dataFinalizacao = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
        isFinalizado = true
    }
}