package com.dboy.meusbagulhos.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dboy.meusbagulhos.R
import com.dboy.meusbagulhos.adapters.RViewUndoneListAdapter
import com.dboy.meusbagulhos.auxiliares.LimitedEditText
import com.dboy.meusbagulhos.auxiliares.TarefaDAO
import com.dboy.meusbagulhos.models.Tarefa
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UndoneFragment : Fragment() {
    private lateinit var tarefaDao: TarefaDAO
    private lateinit var undoneAdapter: RViewUndoneListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_undone, container, false)
        tarefaDao = TarefaDAO(requireContext())
        undoneAdapter = RViewUndoneListAdapter(requireContext(), tarefaDao)

        configuraFab(view)
        inicializaRecyclerView(view)

        return view
    }

    private fun inicializaRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.undoneFragRecycler)
        recyclerView.adapter = undoneAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun configuraFab(view: View) {
        val fab = view.findViewById<FloatingActionButton>(R.id.undoneFragFab)

        fab.setOnClickListener {
            configuraDialog()
        }
    }

    private fun configuraDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_tarefa_layout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.window?.setWindowAnimations(R.style.AnimacoesDialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false) //Não cancelará ao tocar no botão de voltar do Android

        val btnClose = dialog.findViewById<ImageButton>(R.id.dialog_img_close)
        val btnCheck = dialog.findViewById<ImageButton>(R.id.dialog_img_check)
        val textoTarefa = dialog.findViewById<LimitedEditText>(R.id.dialog_eTxt_tarefa)
        val textoCapacidade = dialog.findViewById<TextView>(R.id.dialog_txt_capacidade)
        val textoLinha = dialog.findViewById<TextView>(R.id.dialog_txt_linhas)

        textoLinha.text = "${getString(R.string.dialogTarefaLinha)} 1/${textoTarefa.maxLines}"

        textoTarefa.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Texto da capacidade máxima de caracteres. Ex: 21/160
                textoCapacidade.text = "${p0?.length}" +
                        "/${textoTarefa.maxCharacters}"
                //Texto da capacidade de linhas. Ex: 5/7
                textoLinha.text = "${getString(R.string.dialogTarefaLinha)} ${textoTarefa.lineCount}" +
                        "/${textoTarefa.maxLines}"

                //Cor da capacidade máxima de caracteres. Vermelho para máxima capacidade.
                if (p0?.length == textoTarefa.maxCharacters) textoCapacidade.setTextColor(
                    Color.parseColor(
                        "#FF0000"
                    )
                )
                else textoCapacidade.setTextColor(Color.parseColor("#FF323232"))

                //Cor da capacidade máxima de linhas. Vermelho para máxima capacidade.
                if (textoTarefa.lineCount == textoTarefa.maxLines) textoLinha.setTextColor(
                    Color.parseColor(
                        "#FF0000"
                    )
                )
                else textoLinha.setTextColor(Color.parseColor("#FF323232"))
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        btnClose.setOnClickListener {
            if (textoTarefa.text.toString().isNotEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.aDialogTitle)
                    .setMessage(R.string.aDialogMessage)
                    .setPositiveButton(R.string.aDialogYes) { _, _ ->
                        dialog.cancel()
                    }
                    .setNegativeButton(R.string.aDialogNo) { _, _ ->
                    }
                    .show()
            } else dialog.cancel()
        }
        btnCheck.setOnClickListener { /*ENVIAR TAREFA ESCRITA PARA O BANCO DE DADOS E ATUALIZAR O ADAPTER*/
            if(textoTarefa.text.toString().isNotEmpty()){
                val tarefa = Tarefa(textoTarefa.text.toString())
                tarefaDao.criarTarefa(tarefa)
                undoneAdapter.atualizarLista()

                Toast.makeText(requireContext(), R.string.aDialogTaskCreated, Toast.LENGTH_SHORT).show()
            }

            dialog.cancel()
        }

        dialog.show()
    }

}