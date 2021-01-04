package creations.rimov.com.chipit.ui.editor

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.MainActivity
import creations.rimov.com.chipit.util.constants.EditorConsts
import creations.rimov.com.chipit.data.objects.Chip
import creations.rimov.com.chipit.data.repos.AsyncHandler
import creations.rimov.com.chipit.extension.getRecordAudioPermission
import creations.rimov.com.chipit.extension.getStorageWritePermission
import creations.rimov.com.chipit.extension.getViewModel
import creations.rimov.com.chipit.extension.nav
import creations.rimov.com.chipit.fragments.EditorFragmentArgs
import creations.rimov.com.chipit.fragments.EditorFragmentDirections
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.viewmodel.MainViewModel
import creations.rimov.com.chipit.viewmodel.EditorViewModel
import kotlinx.android.synthetic.main.frag_editor.view.*

class EditorFragment : Fragment(),
    TextLayout.Handler,
    MatPrevLayout.Handler,
    AsyncHandler {

    private lateinit var mainVM: MainViewModel
    private lateinit var localVM: EditorViewModel

    private val passedArgs by navArgs<EditorFragmentArgs>()

    private lateinit var packageManager: PackageManager

    private lateinit var name: TextLayout
    private lateinit var desc: TextLayout
    private lateinit var material: MatPrevLayout

    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            packageManager = it.packageManager
            mainVM = it.getViewModel()
            localVM = getViewModel {EditorViewModel(this)}
        }

        when(passedArgs.action) {
            EditorConsts.CREATE ->
                startCreate()
            EditorConsts.EDIT ->
                startEdit(passedArgs.chipId)
            EditorConsts.DELETE -> {
                startEdit(passedArgs.chipId)

                findNavController().nav(
                  EditorFragmentDirections.actionEditorFragmentToPromptFragment(
                    EditorConsts.DELETE)
                )
            }
        }
    }

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.frag_editor, container, false)

        material = v.matPreviewLayout.apply {
            prepare(this@EditorFragment)
        }

        name = v.matTitleLayout.apply {
            prepare(this@EditorFragment,
                    TextLayout.Type.TITLE)
        }

        desc = v.matDescLayout.apply {
            prepare(this@EditorFragment,
                    TextLayout.Type.DESC)
        }

        mainVM.getFocusChip().observe(viewLifecycleOwner, Observer {focusChip ->
            Log.i("EditorFrag",
                  "Observer(focusChip): displaying chip $focusChip")

            //TODO: HACKY
            localVM.chipBackup.let {backup ->
                if(backup?.name != focusChip?.name)
                    name.displayText(focusChip?.name)
                if(backup?.desc != focusChip?.desc)
                    desc.displayText(focusChip?.desc)
                if(backup?.matPath != focusChip?.matPath)
                    material.display(focusChip?.matType, focusChip?.matPath)
            }

            //Keep track of the changes so as to not update views unnecessarily
            localVM.chipBackup = focusChip
        })

        mainVM.getEditAction().observe(viewLifecycleOwner, Observer {action ->
            toggleVis(action)

            when(action) {
                EditorConsts.SAVE -> {
                    Log.i("EditorFrag", "Observer: edit action = SAVE")

                    mainVM.getFocusChip().value?.let {
                        if(it.name.isBlank()) {
                            Toast.makeText(
                              context,
                              "Please name the chip!",
                              Toast.LENGTH_SHORT).show()

                            findNavController().popBackStack()
                            return@Observer
                        }

                        //TODO FUTURE: (potentially) unreliable way of checking
                        //  if chip is new or not
                        //All new Chips have id=0L before being put into database
                        if(it.id==0L) localVM.saveNew(it)
                        else localVM.saveEdit(it)

                        findNavController().popBackStack()
                    }
                }
                EditorConsts.CANCEL    -> {
                    Log.i("EditorFrag", "Observer: edit action = CANCEL")

                    if(findNavController().currentDestination?.id == R.id.editorFragment)
                        findNavController().popBackStack()
                }
                EditorConsts.DELETE -> {
                    Log.i("EditorFrag", "Observer: edit action = DELETE")

                    mainVM.getFocusChip().value?.let {
                        localVM.deleteChip(it)
                    }

                    findNavController().popBackStack()
                }
                EditorConsts.NEW_IMAGE -> getImageFrom(EditorConsts.NEW)
                EditorConsts.STOR_IMAGE -> getImageFrom(EditorConsts.STORAGE)

                EditorConsts.NEW_VIDEO -> getVideoFrom(EditorConsts.NEW)
                EditorConsts.STOR_VIDEO -> getVideoFrom(EditorConsts.STORAGE)

                EditorConsts.NEW_AUDIO -> getAudioFrom(EditorConsts.NEW)
                EditorConsts.STOR_AUDIO -> getAudioFrom(EditorConsts.STORAGE)

                EditorConsts.NEW_TEXT -> getTextFrom(EditorConsts.NEW)
                EditorConsts.STOR_TEXT  -> getTextFrom(EditorConsts.STORAGE)
            }
        })

        return v
    }

    override fun onDestroy() {
        super.onDestroy()

        mainVM.loadBufferChip()
    }

    private fun toggleVis(action: Int) {

        when(action) {
            EditorConsts.CREATE -> {
                ObjectAnimator.ofFloat(
                  material, "alpha", 0f, 1f).apply {
                    duration = 500
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {}
                    })
                    start()
                }
            }
            EditorConsts.EDIT -> {

            }
            EditorConsts.DELETE -> {

            }
        }
    }

    private fun startCreate() {
        mainVM.setFocusChip(
          Chip(0L, mainVM.getFocusId()), true)
    }

    private fun startEdit(id: Long) {
        localVM.getChip(id)
    }

    private fun getImageFrom(from: Int) {

        activity?.let {
            when(from) {
                //Check if storage write permission has already been granted;
                //  else request it
                EditorConsts.NEW  -> {
                    if(it.getStorageWritePermission()) {
                        uri = CameraUtil.getCameraUri(
                          it.applicationContext, EditorConsts.IMAGE)
                        startActivityForResult(
                          CameraUtil.intentCaptureMedia(
                            it.applicationContext, uri, EditorConsts.IMAGE),
                          CameraUtil.CAPTURE_PIC)
                    }
                }
                EditorConsts.STORAGE -> {
                    startActivityForResult(
                      CameraUtil.intentFindMedia(
                        it.applicationContext, EditorConsts.IMAGE),
                      CameraUtil.FIND_PIC)
                }
            }
        }
    }

    private fun getVideoFrom(from: Int) {

        activity?.let {
            when(from) {
                //Check if storage write permission has already been granted;
                //  else request it
                EditorConsts.NEW  -> {
                    if(it.getStorageWritePermission()) {
                        uri = CameraUtil.getCameraUri(
                          it.applicationContext, EditorConsts.VIDEO)
                        startActivityForResult(
                          CameraUtil.intentCaptureMedia(
                            it.applicationContext, uri, EditorConsts.VIDEO),
                          CameraUtil.CAPTURE_VID)
                    }
                }
                EditorConsts.STORAGE -> {
                    startActivityForResult(
                      CameraUtil.intentFindMedia(
                        it.applicationContext, EditorConsts.VIDEO),
                      CameraUtil.FIND_VID)
                }
            }
        }
    }

    private fun getAudioFrom(from: Int) {

        activity?.let {
            when(from) {
                //Check permission to record audio
                EditorConsts.NEW  -> {
                    if(it.getRecordAudioPermission()) {
                        TODO("Record audio permission granted; do stuff")
                    }
                }
                EditorConsts.STORAGE -> {
                    startActivityForResult(
                      CameraUtil.intentFindMedia(
                        it.applicationContext, EditorConsts.AUDIO),
                      CameraUtil.FIND_AUDIO)
                }
            }
        }
    }

    private fun getTextFrom(from: Int) {

        when(from) {
            EditorConsts.NEW  -> {
                Toast.makeText(context,
                               "*Getting NEW text material*",
                               Toast.LENGTH_SHORT).show()
            }
            EditorConsts.STORAGE -> {
                Toast.makeText(context,
                               "*Getting SAVED text material*",
                               Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * AsyncHandler method
     * Executed in onPostExecute in AsyncTask; retrieves Chip from repo
     */
    override fun <T> setData(data: T) {
        mainVM.setFocusChip(data as Chip, true)
    }

    //PromptMatPrevLayout Handler------------------------------------------------------
    override fun promptAddMat() {

        findNavController().nav(
          EditorFragmentDirections.actionEditorFragmentToPromptFragment(
            EditorConsts.EDIT_RES))
    }

    //EditorTextLayout Handler---------------------------------------------------------
    override fun promptEditText(type: Int, text: String) {

        when(type) {
            TextLayout.Type.TITLE -> {
                findNavController().nav(
                  EditorFragmentDirections.actionEditorFragmentToPromptFragment(
                    EditorConsts.EDIT_TITLE, text))
            }
            TextLayout.Type.DESC  -> {
                findNavController().nav(
                  EditorFragmentDirections.actionEditorFragmentToPromptFragment(
                    EditorConsts.EDIT_DESC, text))
            }
        }
    }

    //After startActivityForResult()
    override fun onActivityResult(
      requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        Log.i("EditorFrag", "onActivityResult(): resultCode is OK? " +
                            "${resultCode==Activity.RESULT_OK}")

        if(resultCode != Activity.RESULT_OK) return

        when(requestCode) {
            CameraUtil.FIND_PIC    -> {
                data?.let {
                    mainVM.setMat(
                      EditorConsts.IMAGE,
                      it.data?.toString() ?: "") //Save
                }
            }
            CameraUtil.CAPTURE_PIC -> {
                data?.let {
                    mainVM.setMat(
                      EditorConsts.IMAGE,
                      uri.toString()) //Save
                }
            }
            CameraUtil.FIND_VID    -> {
                data?.let {
                    mainVM.setMat(
                      EditorConsts.VIDEO,
                      it.data?.toString() ?: "") //Save
                }
            }
            CameraUtil.CAPTURE_VID -> {
                data?.let {
                    mainVM.setMat(
                      EditorConsts.VIDEO,
                      uri.toString()) //Save
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
      requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode) {
            MainActivity.Constant.REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if(grantResults.isNotEmpty()
                   && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //TODO FUTURE: permission granted, restart the intent
                    //  to capture media
//                    activity?.applicationContext?.let {
//                        uri = CameraUtil.getCameraUri(
//                          it, EditorConsts.IMAGE)
//                        startActivityForResult(
//                          CameraUtil.intentCaptureMedia(
//                            it, uri, EditorConsts.IMAGE),
//                          CameraUtil.CAPTURE_PIC)
//                    }
                }

                //TODO FUTURE: storage permission has not been granted,
                //  inform the user
            }
        }
    }
}