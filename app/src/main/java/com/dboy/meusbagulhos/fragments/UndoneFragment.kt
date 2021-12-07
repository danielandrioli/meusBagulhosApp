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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dboy.meusbagulhos.R
import com.dboy.meusbagulhos.adapters.RViewUndoneListAdapter
import com.dboy.meusbagulhos.adapters.RViewUndoneListAdapter.OnTarefaListener
import com.dboy.meusbagulhos.auxiliares.LimitedEditText
import com.dboy.meusbagulhos.auxiliares.SwipeGesture
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
        configuraAdapter()
        configuraFab(view)
        inicializaRecyclerView(view)

        return view
    }

    override fun onResume() {
        super.onResume()
        undoneAdapter.atualizarLista() //ARRUMAR ISSO AQUI. QUANDO INICIALIZA O APP, TÁ CHAMANDO DUAS VEZES O ATUALIZA PQ APOS O ONCREATE VEM ONRESUME
    }

    private fun configuraAdapter(){
        undoneAdapter = RViewUndoneListAdapter(requireContext(), tarefaDao)
        undoneAdapter.setOnTarefaClickListener(object: OnTarefaListener{
            override fun onTarefaClick(posicao: Int) {
                //MOSTRAR AQUI A TAREFA PARA SER LIDA E EDITADA
                configuraDialog(undoneAdapter.listaTarefas[posicao])
            }

            override fun onTarefaDoubleClick(posicao: Int) {
                tarefaDao.finalizarTarefa(undoneAdapter.listaTarefas[posicao])
                undoneAdapter.atualizarLista()
            }

        })
    }

    private fun inicializaRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.undoneFragRecycler)
        recyclerView.adapter = undoneAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val itemTouchHelper = ItemTouchHelper(SwipeGesture(tarefaDao, undoneAdapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun configuraFab(view: View) {
        val fab = view.findViewById<FloatingActionButton>(R.id.undoneFragFab)

        fab.setOnClickListener {
            configuraDialog()
        }
    }
/*irei mexer no configuraDialog*/
    private fun configuraDialog(tarefa: Tarefa? = null) {
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
        val textoCriadoEm = dialog.findViewById<TextView>(R.id.dialog_txt_created)
        val btnDelete = dialog.findViewById<ImageButton>(R.id.dialog_img_delete)
        val btnSave = dialog.findViewById<ImageButton>(R.id.dialog_img_save)

        if (tarefa != null){
            textoTarefa.setText(tarefa.texto)
            btnCheck.visibility = View.INVISIBLE
            textoCapacidade.text = "${textoTarefa.text.toString().length}" +
                    "/${textoTarefa.maxCharacters}"
            textoCriadoEm.text = if(tarefa.dataEdicao.isBlank()){
                "${getString(R.string.tarefaCriadaEm)} ${tarefa.dataCriacao}"}
            else{
                "${getString(R.string.tarefaEditadaEm)}: ${tarefa.dataEdicao}"
            }
            btnDelete.visibility = View.VISIBLE
            btnSave.visibility = View.VISIBLE
        } else{
            textoLinha.text = "${getString(R.string.dialogTarefaLinha)} 1/${textoTarefa.maxLines}"
        }

        textoTarefa.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

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
            if ((textoTarefa.text.toString().isNotEmpty() && tarefa == null) or
                (tarefa != null && tarefa.texto != textoTarefa.text.toString())) {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.aDialogTitle)
                    .setMessage(R.string.aDialogMessageChanges)
                    .setPositiveButton(R.string.aDialogYes) { _, _ ->
                        dialog.cancel()
                    }
                    .setNegativeButton(R.string.aDialogNo) { _, _ ->
                    }
                    .show()
            } else dialog.cancel()
        }
        btnCheck.setOnClickListener {
            if(textoTarefa.text.toString().isNotEmpty()){
                val tarefa = Tarefa(textoTarefa.text.toString())
                tarefaDao.criarTarefa(tarefa)
                undoneAdapter.atualizarLista()

                Toast.makeText(requireContext(), R.string.aDialogTaskCreated, Toast.LENGTH_SHORT).show()
            }

            dialog.cancel()
        }

        btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.aDialogTitle)
                .setMessage(R.string.aDialogMessageDelete)
                .setPositiveButton(R.string.aDialogYes) { _, _ ->
                    if (tarefa != null) {
                        tarefaDao.deletar(tarefa)
                        undoneAdapter.atualizarLista()
                    }
                    dialog.cancel()
                }
                .setNegativeButton(R.string.aDialogNo) { _, _ ->
                }
                .show()
        }

        btnSave.setOnClickListener {
            if(tarefa != null && textoTarefa.text.toString().isBlank()){
                tarefaDao.deletar(tarefa)
                undoneAdapter.atualizarLista()
                Toast.makeText(requireContext(), R.string.aDialogTaskDeleted, Toast.LENGTH_SHORT).show()
            }
            else if (tarefa != null && textoTarefa.text.toString() != tarefa.texto){
                tarefa.texto = textoTarefa.text.toString()
                tarefaDao.atualizarTexto(tarefa)
                undoneAdapter.atualizarLista()
                Toast.makeText(requireContext(), R.string.aDialogTaskEdited, Toast.LENGTH_SHORT).show()

            }
            dialog.cancel()
        }
        dialog.show()
    }
}