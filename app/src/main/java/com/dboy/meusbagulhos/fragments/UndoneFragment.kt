package com.dboy.meusbagulhos.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dboy.meusbagulhos.R
import com.dboy.meusbagulhos.adapters.RViewUndoneListAdapter
import com.dboy.meusbagulhos.adapters.RViewUndoneListAdapter.OnTarefaListener
import com.dboy.meusbagulhos.helpers.LimitedEditText
import com.dboy.meusbagulhos.helpers.SwipeGesture
import com.dboy.meusbagulhos.helpers.TaskDAO
import com.dboy.meusbagulhos.models.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UndoneFragment : Fragment() {
    private lateinit var taskDao: TaskDAO
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

        taskDao = TaskDAO(requireContext())
        configuraAdapter()
        setFabButton(view)
        createRecyclerView(view)

        return view
    }

    override fun onResume() {
        super.onResume()
        undoneAdapter.updateList()
    }

    private fun configuraAdapter() {
        undoneAdapter = RViewUndoneListAdapter(requireContext(), taskDao)
        undoneAdapter.setOnTaskClickListener(object : OnTarefaListener {
            override fun onTaskClick(position: Int) {
                setDialog(undoneAdapter.listTaskUndone[position])
            }

            override fun onTaskDoubleClick(position: Int) {
                taskDao.finishTask(undoneAdapter.listTaskUndone[position])
                undoneAdapter.updateList()
            }

        })
    }

    private fun createRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.undoneFragRecycler)
        recyclerView.adapter = undoneAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val itemTouchHelper = ItemTouchHelper(SwipeGesture(taskDao, undoneAdapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setFabButton(view: View) {
        val fab = view.findViewById<FloatingActionButton>(R.id.undoneFragFab)

        fab.setOnClickListener {
            setDialog()
        }
    }

    private fun setDialog(task: Task? = null) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_creatingtask_layout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.window?.setWindowAnimations(R.style.AnimationsDialog)
        dialog.setCanceledOnTouchOutside(false)

        val btnClose = dialog.findViewById<ImageButton>(R.id.dialog_img_close)
        val btnCheck = dialog.findViewById<ImageButton>(R.id.dialog_img_check)
        val textTask = dialog.findViewById<LimitedEditText>(R.id.dialog_eTxt_task)
        val textSize = dialog.findViewById<TextView>(R.id.dialog_txt_capacity)
        val textLine = dialog.findViewById<TextView>(R.id.dialog_txt_lines)
        val textCreatedIn = dialog.findViewById<TextView>(R.id.dialog_txt_created)
        val btnDelete = dialog.findViewById<ImageButton>(R.id.dialog_img_delete)
        val btnSave = dialog.findViewById<ImageButton>(R.id.dialog_img_save)

        if (task != null) {
            textTask.setText(task.text)
            btnCheck.visibility = View.INVISIBLE
            textSize.text = "${textTask.text.toString().length}" +
                    "/${textTask.maxCharacters}"
            textCreatedIn.text = if (task.dateEdition.isBlank()) {
                "${getString(R.string.tarefaCriadaEm)} ${task.dateCreation}"
            } else {
                "${getString(R.string.tarefaEditadaEm)}: ${task.dateEdition}"
            }
            btnDelete.visibility = View.VISIBLE
            btnSave.visibility = View.VISIBLE
        } else {
            textLine.text = "${getString(R.string.dialogTarefaLinha)} 1/${textTask.maxLines}"
        }

        textTask.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Maximum capacity text. Eg: 21/160
                textSize.text = "${p0?.length}" +
                        "/${textTask.maxCharacters}"
                //Lines capacity. Ex: 5/7
                textLine.text =
                    "${getString(R.string.dialogTarefaLinha)} ${textTask.lineCount}" +
                            "/${textTask.maxLines}"

                //Maximum characters capacity color. Red is for the maximum.
                if (p0?.length == textTask.maxCharacters) textSize.setTextColor(
                    Color.parseColor(
                        "#FF0000" //red
                    )
                )
                else textSize.setTextColor(Color.parseColor("#FF323232"))

                //Maximum lines capacity color. Red is for the maximum.
                if (textTask.lineCount == textTask.maxLines) textLine.setTextColor(
                    Color.parseColor(
                        "#FF0000" //red
                    )
                )
                else textLine.setTextColor(Color.parseColor("#FF323232"))
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        dialog.setOnKeyListener(object : DialogInterface.OnKeyListener { //overrides back pressing
            override fun onKey(p0: DialogInterface?, p1: Int, p2: KeyEvent?): Boolean {
                if (p2 != null && p2.action != KeyEvent.ACTION_DOWN) {
                    return true //onKey method is always called twice. This return prevents the second AlertDialog.Builder call.
                }
                if ((textTask.text.toString().isNotEmpty() && task == null) or
                    (task != null && task.text != textTask.text.toString())
                ) {
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
                return p1 == KeyEvent.KEYCODE_BACK
            }
        })

        btnClose.setOnClickListener {
            if ((textTask.text.toString().isNotEmpty() && task == null) or
                (task != null && task.text != textTask.text.toString())
            ) {
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
            if (textTask.text.toString().isNotEmpty()) {
                val tarefa = Task(textTask.text.toString())
                taskDao.createTask(tarefa)
                undoneAdapter.updateList()

                Toast.makeText(requireContext(), R.string.aDialogTaskCreated, Toast.LENGTH_SHORT)
                    .show()
            }
            dialog.cancel()
        }

        btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.aDialogTitle)
                .setMessage(R.string.aDialogMessageDelete)
                .setPositiveButton(R.string.aDialogYes) { _, _ ->
                    if (task != null) {
                        taskDao.delete(task)
                        undoneAdapter.updateList()
                    }
                    dialog.cancel()
                }
                .setNegativeButton(R.string.aDialogNo) { _, _ ->
                }
                .show()
        }

        btnSave.setOnClickListener {
            if (task != null && textTask.text.toString().isBlank()) {
                taskDao.delete(task)
                undoneAdapter.updateList()
                Toast.makeText(requireContext(), R.string.aDialogTaskDeleted, Toast.LENGTH_SHORT)
                    .show()
            } else if (task != null && textTask.text.toString() != task.text) {
                task.text = textTask.text.toString()
                taskDao.updateText(task)
                undoneAdapter.updateList()
                Toast.makeText(requireContext(), R.string.aDialogTaskEdited, Toast.LENGTH_SHORT)
                    .show()
            }
            dialog.cancel()
        }
        dialog.show()
    }
}