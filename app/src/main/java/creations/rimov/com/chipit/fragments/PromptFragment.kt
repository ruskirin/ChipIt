package creations.rimov.com.chipit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.constants.EditorConsts
import creations.rimov.com.chipit.extensions.getViewModel
import creations.rimov.com.chipit.extensions.gone
import creations.rimov.com.chipit.extensions.visible
import creations.rimov.com.chipit.view_models.CommsViewModel
import creations.rimov.com.chipit.viewgroups.prompt.PromptConfirmLayout
import creations.rimov.com.chipit.viewgroups.prompt.PromptMatAddLayout
import creations.rimov.com.chipit.viewgroups.prompt.PromptEditTextLayout
import kotlinx.android.synthetic.main.frag_prompt.view.*

class PromptFragment : DialogFragment(),
    PromptMatAddLayout.Handler,
    PromptEditTextLayout.Handler,
    PromptConfirmLayout.Handler {

    private lateinit var commsVM: CommsViewModel
    private val passedArgs by navArgs<PromptFragmentArgs>()

    private lateinit var matAddLayout: PromptMatAddLayout
    private lateinit var confirmLayout: PromptConfirmLayout
    private lateinit var editTextLayout: PromptEditTextLayout

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?): View? {

        val v: View = inflater.inflate(R.layout.frag_prompt, container, true)

        activity?.let {
            commsVM = it.getViewModel()
        }

        matAddLayout = v.promptMatAddLayout
        confirmLayout = v.promptConfirmLayout
        editTextLayout = v.promptEditTextLayout

        when(passedArgs.action) {
            EditorConsts.EDIT_RES                           -> {
                matAddLayout.visible()
                confirmLayout.gone()
                editTextLayout.gone()

                matAddLayout.prepare(this)
            }
            EditorConsts.EDIT_TITLE, EditorConsts.EDIT_DESC -> {
                matAddLayout.gone()
                confirmLayout.gone()
                editTextLayout.visible()

                editTextLayout.prepare(this, passedArgs.action, passedArgs.text)
            }
            EditorConsts.DELETE                            -> {
                matAddLayout.gone()
                confirmLayout.visible()
                editTextLayout.gone()

                confirmLayout.prepare(
                  this,
                  resources.getString(
                    R.string.prompt_text_delete,
                    commsVM.getFocusChip().value?.name))
            }
        }

        return v
    }

    override fun getMaterial(from: Int) {

        commsVM.setEditAction(from)
        findNavController().popBackStack()
    }

    override fun finishEdit(type: Int, text: String) {

        when(type) {
            EditorConsts.EDIT_TITLE -> commsVM.setName(text)
            EditorConsts.EDIT_DESC -> commsVM.setDesc(text)
        }

        findNavController().popBackStack()
    }

    /**
     * Important that the global editAction is set from PromptFrag instead of
     *   source, as the action has to first be confirmed
     */
    override fun confirm(accept: Boolean) {

        when(passedArgs.action) {
            EditorConsts.DELETE -> {
                if(accept) commsVM.setEditAction(passedArgs.action)
                else commsVM.setEditAction(EditorConsts.CANCEL)

                findNavController().popBackStack()
            }
        }
    }
}