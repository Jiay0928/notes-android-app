package ui.assignments.a4notes.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import ui.assignments.a4notes.R
import ui.assignments.a4notes.viewmodel.NotesViewModel


class TextEditor() : Fragment() {
    var noteid = -1
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.frag_edit, container, false)
        var create = false
        noteid = arguments?.getInt("id")!!
        if (noteid == -1){
            create = true
        }
        val titleEdit = view.findViewById<EditText>(R.id.titleEdit)
        val contentEdit = view.findViewById<EditText>(R.id.contentEdit)
//        val model : NotesViewModel by viewModels { NotesViewModel.Factory }
        val model = ViewModelProvider(requireActivity()).get(NotesViewModel::class.java)
        val note = model.getNote(noteid)
        val importantSwitch = view.findViewById<Switch>(R.id.importanceEdit)
        val archiveswitch = view.findViewById<Switch>(R.id.archiveEdit)
        importantSwitch.isChecked = note?.important == true
        archiveswitch.isChecked = note?.archived == true
        val createBtn =  view.findViewById<Button>(R.id.createButton)
        if (!create){
            createBtn.visibility = View.GONE
            titleEdit.setText(note!!.title)
            contentEdit.setText(note!!.content)
        }else{
            archiveswitch.visibility = View.GONE
            createBtn.setOnClickListener{
                val title = titleEdit.text.toString()
                val content = contentEdit.text.toString()
                val importance = importantSwitch.isChecked
                model.addNote(title, content, importance)
                Navigation.findNavController(view).navigate(R.id.action_textEditor_pop);
            }
        }
        importantSwitch.setOnCheckedChangeListener{
            _, isChecked ->
                if (!create){
                    model.changeImportance(noteid,isChecked)
                    if (isChecked){
                        archiveswitch.isChecked = false
                    }
                }
        }
        archiveswitch.setOnCheckedChangeListener{
            _, isChecked ->
            model.changeArchiveWithBool(noteid, isChecked)
            if (isChecked){
                    importantSwitch.isChecked = false

            }

        }
        titleEdit.addTextChangedListener(
            object:TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (!   create) {

                        model.changeTitle(noteid ,s.toString())
                    }
                }
            }
        )
        contentEdit.addTextChangedListener(
            object:TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (! create) {

                        model.changeContent(noteid,s.toString())
                    }
                }
            }
        )
        return view

    }

}


