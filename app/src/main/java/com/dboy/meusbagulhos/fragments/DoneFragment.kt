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
import com.dboy.meusbagulhos.auxiliares.TarefaDAO
import com.dboy.meusbagulhos.models.Tarefa

class DoneFragment : Fragment() {
    private lateinit var tarefaDao: TarefaDAO
    private lateinit var doneListAdapter: RViewDoneListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_done, container, false)

        tarefaDao = TarefaDAO(requireContext())
        configuraAdapter()
        inicializaRecyclerView(view)

        return view
    }

    override fun onResume() {
        super.onResume()
        doneListAdapter.atualizaLista()
    }

    private fun configuraAdapter() {
        doneListAdapter = RViewDoneListAdapter(requireContext(), tarefaDao)
        doneListAdapter.setOnTarefaClickListener(object: RViewDoneListAdapter.OnTarefaListener{
            override fun onTarefaClick(posicao: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTarefaLongClick(posicao: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTarefaDoubleClick(posicao: Int) {
                tarefaDao.desfinalizarTarefa(doneListAdapter.listaTarefasFeitas[posicao])
                doneListAdapter.atualizaLista()
            }

        })
    }

    private fun inicializaRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.doneFragRecycler)
        recyclerView.adapter = doneListAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}