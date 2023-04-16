package ui.assignments.a4notes.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ui.assignments.a4notes.R
import ui.assignments.a4notes.viewmodel.NotesViewModel

class MainPage(val create:Boolean = false, val note: NotesViewModel.VMNote? = null) : Fragment() {
    private fun buildlinearLayout(list :MutableList<LiveData<NotesViewModel.VMNote>>, model:NotesViewModel, view:View){
        val showArchive = model.getViewArchived().value
        val linearLayout = requireView().findViewById<LinearLayout>(R.id.NoteHolder)
        linearLayout.removeAllViews()
        list.forEach { oneNote ->
            if (oneNote.value?.archived == true){
                if (showArchive == false){
                    return@forEach
                }
            }
            layoutInflater.inflate(R.layout.note, null, false).apply {
                findViewById<TextView>(R.id.title).text = oneNote.value?.title ?: ""
                findViewById<TextView>(R.id.content).text = oneNote.value?.content?:""
                findViewById<TextView>(R.id.deleteButton).setOnClickListener {
                    model.deleteNote(oneNote.value!!.id!!)

                }
                findViewById<TextView>(R.id.archiveButton).setOnClickListener {
                    model.changeArchive(oneNote.value!!.id!!)
                }
                val tv = findViewById<LinearLayout>(R.id.onenoteContainer)
                if (oneNote.value?.important == true ) {
                    tv.background = ContextCompat.getDrawable(context, R.color.light_blue_400)
                }
                if (oneNote.value?.archived == true ) {
                    tv.background = ContextCompat.getDrawable(context, R.color.gray_400)
                }
                tv.setOnClickListener{
                    val bundle = bundleOf("id" to oneNote.value!!.id)
                    Navigation.findNavController(view).navigate(R.id.action_mainPage_to_textEditor,bundle);
                }
                linearLayout.addView(this)
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.frag_main, container, false)
//        val model : NotesViewModel by viewModels { NotesViewModel.Factory }
        val model = ViewModelProvider(requireActivity()).get(NotesViewModel::class.java)

        view.findViewById<Switch>(R.id.archiveSwitch).setOnCheckedChangeListener{ _, isChecked ->
            model.setViewArchived(isChecked)
        }
        view.findViewById<FloatingActionButton>(R.id.addButton).setOnClickListener{
            val bundle = bundleOf("id" to -1)
            Navigation.findNavController(view).navigate(R.id.action_mainPage_to_textEditor,bundle);
        }

        model.getNotes().observe(viewLifecycleOwner){
            buildlinearLayout(it, model, view)

        }
//        handle search action
        view.findViewById<Button>(R.id.search_button).setOnClickListener{
            val text = view.findViewById<EditText>(R.id.searchEdit).text.toString()
            model.filter(text)
        }
//        model.getNotes().observe(viewLifecycleOwner) {
//            Log.i("MainActivity", it.size.toString())
//            Log.i("MainActivity", it?.fold("Visible Note IDs:") { acc, cur -> "$acc ${cur.value?.id}" } ?: "[ERROR]")
//        }
        return view

    }

}
