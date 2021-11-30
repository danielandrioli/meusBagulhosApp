package com.dboy.meusbagulhos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dboy.meusbagulhos.R
import com.dboy.meusbagulhos.adapters.RViewDoneListAdapter
import com.dboy.meusbagulhos.models.Tarefa

class DoneFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_done, container, false)
        inicializaRecyclerView(view)

        return view
    }

    private fun inicializaRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.doneFragRecycler)
        val listaProvisoria = metodoProvisorioLista() //CAPTURAR LISTA DO BANCO DE DADOS!   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        recyclerView.adapter = RViewDoneListAdapter(requireContext(), listaProvisoria)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun metodoProvisorioLista(): List<Tarefa>{
        val t1 = Tarefa("Ae 01")
        val t2 = Tarefa("Ae 03")
        t1.finalizaTarefa()
        t2.finalizaTarefa()
        return listOf<Tarefa>(t1, t2, Tarefa("terceira, e nao finalizada"))
    }
}