package com.dboy.meusbagulhos.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dboy.meusbagulhos.R
import com.dboy.meusbagulhos.adapters.RViewDoneListAdapter
import com.dboy.meusbagulhos.auxiliares.TarefaDAO
import com.dboy.meusbagulhos.models.Tarefa
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DoneFragment : Fragment() {
    private lateinit var tarefaDao: TarefaDAO
    private lateinit var doneListAdapter: RViewDoneListAdapter
    private lateinit var fabDel: FloatingActionButton
    private lateinit var fabUndo: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_done, container, false)

        tarefaDao = TarefaDAO(requireContext())
        configuraBotoesFab(view)
        configuraAdapter()
        inicializaRecyclerView(view)
        configuraBotaoVoltar()

        return view
    }

    private fun configuraBotaoVoltar() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.i("done fragment", "Fragment back pressed invoked")
                    if (doneListAdapter.isSelectedMode) {
                        clearSelectedList()
                        doneListAdapter.notifyDataSetChanged()
                    } else {
                        isEnabled = false //se não colocar como false, ocorre stackoverflow
                        requireActivity().onBackPressed()
                    }
                }
            }
            )
    }

    private fun configuraBotoesFab(view: View?) {
        if (view != null) {
            fabDel = view.findViewById<FloatingActionButton>(R.id.doneFabDel)
            fabUndo = view.findViewById<FloatingActionButton>(R.id.doneFabUndo)
        }

        fabDel.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.aDialogTitle)
                .setMessage(R.string.aDialogMessageDelete)
                .setPositiveButton(R.string.aDialogYes) { _, _ ->
                    for (tarefa in doneListAdapter.listSelectedTasks) {
                        tarefaDao.deletar(tarefa)
                    }
                    clearSelectedList()
                    doneListAdapter.atualizaLista()
                }
                .setNegativeButton(R.string.aDialogNo) { _, _ ->
                }
                .show()
        }

        fabUndo.setOnClickListener {
            for (tarefa in doneListAdapter.listSelectedTasks) {
                tarefaDao.desfinalizarTarefa(tarefa)
            }
            clearSelectedList()
            doneListAdapter.atualizaLista()
        }
    }

    private fun clearSelectedList() {
        doneListAdapter.isSelectedMode = false
        doneListAdapter.listSelectedTasks.clear()
        hideButtonsFab()
    }

    override fun onResume() {
        super.onResume()
        doneListAdapter.atualizaLista()
    }

    private fun checaSelecao() {
        if (doneListAdapter.isSelectedMode) {
            fabDel.visibility = FloatingActionButton.VISIBLE
            fabUndo.visibility = FloatingActionButton.VISIBLE
        } else {
            fabDel.visibility = FloatingActionButton.INVISIBLE
            fabUndo.visibility = FloatingActionButton.INVISIBLE
        }
    }

    override fun onPause() {
        clearSelectedList()
        super.onPause()
    }

    private fun configuraAdapter() {
        doneListAdapter = RViewDoneListAdapter(requireContext(), tarefaDao)
        doneListAdapter.setOnTarefaClickListener(object : RViewDoneListAdapter.OnTarefaListener {
            override fun onTarefaClick(posicao: Int) {
                configuraDialog(doneListAdapter.listaTarefasFeitas[posicao])
            }

            override fun onTarefaLongClick(posicao: Int) {
                checaSelecao()
            }

            override fun onTarefaDoubleClick(posicao: Int) {
                tarefaDao.desfinalizarTarefa(doneListAdapter.listaTarefasFeitas[posicao])
                doneListAdapter.atualizaLista()
            }

            override fun hideButtons() {
                hideButtonsFab()
            }
        })
    }

    fun hideButtonsFab() {
        fabDel.visibility = FloatingActionButton.INVISIBLE
        fabUndo.visibility = FloatingActionButton.INVISIBLE
    }

    private fun inicializaRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.doneFragRecycler)
        recyclerView.adapter = doneListAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun configuraDialog(tarefa: Tarefa) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_tarefadone_layout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.window?.setWindowAnimations(R.style.AnimacoesDialog)

        val btnClose = dialog.findViewById<ImageButton>(R.id.dialog_done_img_close)
        val btnDelete = dialog.findViewById<ImageButton>(R.id.dialog_done_img_delete)
        val btnUndo = dialog.findViewById<ImageButton>(R.id.dialog_done_img_desfazer)
        val txtTarefa = dialog.findViewById<TextView>(R.id.dialog_done_txt)
        val txtCriadoEm = dialog.findViewById<TextView>(R.id.dialog_done_txt_created)
        val txtFinalizadoEm = dialog.findViewById<TextView>(R.id.dialog_done_finished)

        txtTarefa.text = tarefa.texto
        txtCriadoEm.text = "${getString(R.string.tarefaCriadaEm)} ${tarefa.dataCriacao}"
        txtFinalizadoEm.text = "${getString(R.string.tarefaProntaEm)} ${tarefa.dataFinalizacao}"

        btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.aDialogTitle)
                .setMessage(R.string.aDialogMessageDelete)
                .setPositiveButton(R.string.aDialogYes) { _, _ ->
                    if (tarefa != null) {
                        tarefaDao.deletar(tarefa)
                        doneListAdapter.atualizaLista()
                    }
                    dialog.cancel()
                }
                .setNegativeButton(R.string.aDialogNo) { _, _ ->
                }
                .show()
        }

        btnClose.setOnClickListener {
            dialog.cancel()
        }

        btnUndo.setOnClickListener {
            tarefaDao.desfinalizarTarefa(tarefa)
            dialog.cancel()
            doneListAdapter.atualizaLista()
        }

        dialog.show()
    }
}