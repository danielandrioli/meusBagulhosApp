package com.dboy.meusbagulhos.fragments

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
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.dboy.meusbagulhos.R
import com.dboy.meusbagulhos.models.LimitedEditText
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UndoneFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_undone, container, false)
        configuraFab(view)

        return view
    }

    private fun configuraFab(view: View) {
        val fab = view.findViewById<FloatingActionButton>(R.id.undoneFragFab)

        //CONFIGURAR UM BOX PARA DIGITAR O TEXTO. TER LIMITE DE 180 CARACTERES. MOSTRAR QNTOS FALTAM
        //EXEMPLO: 21/180. TER BOTÃO DE CONFIRMAR E DE CANCELAR. CANCELAR SE TECLAR FORA DO BOX.
        fab.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_tarefa_layout)
            dialog.window?.setBackgroundDrawable(ColorDrawable(0))
            dialog.window?.setWindowAnimations(R.style.AnimacoesDialog)

            val btnClose = dialog.findViewById<ImageButton>(R.id.dialog_img_close)
            val btnCheck = dialog.findViewById<ImageButton>(R.id.dialog_img_check)
            val textoTarefa = dialog.findViewById<LimitedEditText>(R.id.dialog_eTxt_tarefa)
            val textoCapacidade = dialog.findViewById<TextView>(R.id.dialog_txt_capacidade)
            val textoLinha = dialog.findViewById<TextView>(R.id.dialog_txt_linhas)

            textoLinha.text = "${getString(R.string.tarefaLinha)} 1/${textoTarefa.maxLines}"

            textoTarefa.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    textoCapacidade.text = "${p0?.length}" +
                            "/${textoTarefa.maxCharacters}"
                    textoLinha.text = "${getString(R.string.tarefaLinha)} ${textoTarefa.lineCount}" +
                            "/${textoTarefa.maxLines}"


                    if (p0?.length == textoTarefa.maxCharacters) textoCapacidade.setTextColor(Color.parseColor("#FF0000"))
                    else textoCapacidade.setTextColor(Color.parseColor("#FF323232"))

                    if (textoTarefa.lineCount == textoTarefa.maxLines) textoLinha.setTextColor(Color.parseColor("#FF0000"))
                    else textoLinha.setTextColor(Color.parseColor("#FF323232"))
                }

                override fun afterTextChanged(p0: Editable?) {
//                    if (p0?.length == 160) textoCapacidade.setTextColor(Color.parseColor("#FF0000"))

                  //tudook
                }
            })

            btnClose.setOnClickListener { /*VERIFICAR SE TEM ALGO ESCRITO. SE HOUVER, PEDIR CONFIRMAÇÃO*/

                dialog.cancel()
            }

            btnCheck.setOnClickListener { /*ENVIAR TAREFA ESCRITA PARA O BANCO DE DADOS E ATUALIZAR O ADAPTER*/

                dialog.cancel()
            }

            dialog.show()
        }
    }
}