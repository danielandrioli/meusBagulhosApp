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
import com.dboy.meusbagulhos.helpers.TaskDAO
import com.dboy.meusbagulhos.models.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DoneFragment : Fragment() {
    private lateinit var taskDao: TaskDAO
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

        taskDao = TaskDAO(requireContext())
        setFabButtons(view)
        setAdapter()
        createRecyclerView(view)
        setBackButton()

        return view
    }

    private fun setBackButton() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.i("done fragment", "Fragment back pressed invoked")
                    if (doneListAdapter.isSelectedMode) {
                        clearSelectedList()
                        doneListAdapter.notifyDataSetChanged()
                    } else {
                        isEnabled = false //if not set to false, stackoverflow exception occurs.
                        requireActivity().onBackPressed()
                    }
                }
            }
            )
    }

    private fun setFabButtons(view: View?) {
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
                        taskDao.delete(tarefa)
                    }
                    clearSelectedList()
                    doneListAdapter.updateList()
                }
                .setNegativeButton(R.string.aDialogNo) { _, _ ->
                }
                .show()
        }

        fabUndo.setOnClickListener {
            for (tarefa in doneListAdapter.listSelectedTasks) {
                taskDao.undoTask(tarefa)
            }
            clearSelectedList()
            doneListAdapter.updateList()
        }
    }

    private fun clearSelectedList() {
        doneListAdapter.isSelectedMode = false
        doneListAdapter.listSelectedTasks.clear()
        hideButtonsFab()
    }

    override fun onResume() {
        super.onResume()
        doneListAdapter.updateList()
    }

    private fun checkSelection() {
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

    private fun setAdapter() {
        doneListAdapter = RViewDoneListAdapter(requireContext(), taskDao)
        doneListAdapter.setOnTaskClickListener(object : RViewDoneListAdapter.OnTaskListener {
            override fun onTaskClick(position: Int) {
                setDialog(doneListAdapter.listTaskDone[position])
            }

            override fun onTaskLongClick(position: Int) {
                checkSelection()
            }

            override fun onTaskDoubleClick(position: Int) {
                taskDao.undoTask(doneListAdapter.listTaskDone[position])
                doneListAdapter.updateList()
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

    private fun createRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.doneFragRecycler)
        recyclerView.adapter = doneListAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setDialog(task: Task) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_taskdone_layout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.window?.setWindowAnimations(R.style.AnimationsDialog)

        val btnClose = dialog.findViewById<ImageButton>(R.id.dialog_done_img_close)
        val btnDelete = dialog.findViewById<ImageButton>(R.id.dialog_done_img_delete)
        val btnUndo = dialog.findViewById<ImageButton>(R.id.dialog_done_img_undo)
        val textTask = dialog.findViewById<TextView>(R.id.dialog_done_txt)
        val textCreatedIn = dialog.findViewById<TextView>(R.id.dialog_done_txt_created)
        val textFinishedIn = dialog.findViewById<TextView>(R.id.dialog_done_finished)

        textTask.text = task.text
        textCreatedIn.text = "${getString(R.string.tarefaCriadaEm)} ${task.dateCreation}"
        textFinishedIn.text = "${getString(R.string.tarefaProntaEm)} ${task.dateDone}"

        btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.aDialogTitle)
                .setMessage(R.string.aDialogMessageDelete)
                .setPositiveButton(R.string.aDialogYes) { _, _ ->
                    if (task != null) {
                        taskDao.delete(task)
                        doneListAdapter.updateList()
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
            taskDao.undoTask(task)
            dialog.cancel()
            doneListAdapter.updateList()
        }

        dialog.show()
    }
}