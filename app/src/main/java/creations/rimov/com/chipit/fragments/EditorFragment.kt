package creations.rimov.com.chipit.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import creations.rimov.com.chipit.activities.MainActivity
import creations.rimov.com.chipit.constants.EditorConsts
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.repos.AsyncHandler
import creations.rimov.com.chipit.extensions.getStorageWritePermission
import creations.rimov.com.chipit.extensions.getViewModel
import creations.rimov.com.chipit.extensions.nav
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.view_models.EditorViewModel
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.viewgroups.EditorTextLayout
import creations.rimov.com.chipit.viewgroups.EditorMatPrevLayout
import kotlinx.android.synthetic.main.frag_editor.view.*

class EditorFragment : Fragment(),
    EditorTextLayout.Handler,
    EditorMatPrevLayout.Handler,
    AsyncHandler {

    private lateinit var globalVM: GlobalViewModel
    private lateinit var localVM: EditorViewModel

    private val passedArgs by navArgs<EditorFragmentArgs>()

    private lateinit var packageManager: PackageManager

    private lateinit var name: EditorTextLayout
    private lateinit var desc: EditorTextLayout
    private lateinit var material: EditorMatPrevLayout

    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            packageManager = it.packageManager
            globalVM = it.getViewModel()
            localVM = getViewModel {EditorViewModel(this)}
        }

        passedArgs.action.let {
            when(it) {
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
                    EditorTextLayout.Type.TITLE)
        }

        desc = v.matDescLayout.apply {
            prepare(this@EditorFragment,
                    EditorTextLayout.Type.DESC)
        }

        globalVM.getFocusChip().observe(viewLifecycleOwner, Observer {

            name.displayText(it?.name)
            desc.displayText(it?.desc)
            material.display(it?.matType, it?.matPath)
        })

        globalVM.getEditAction().observe(viewLifecycleOwner, Observer { action ->
            toggleVis(action)

            when(action) {
                EditorConsts.SAVE -> {
                    Log.i("EditorFrag", "Observer: edit action = SAVE")

                    globalVM.getFocusChip().value?.let {
                        if(it.name.isBlank()) {
                            Toast.makeText(
                              context,
                              "Please name the chip!",
                              Toast.LENGTH_SHORT).show()

                            findNavController().popBackStack()
                            return@Observer
                        }

                        //TODO IMPORTANT: (potentially) unreliable way of checking
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

                    globalVM.getFocusChip().value?.let {
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

        globalVM.loadBufferChip()
    }

    private fun toggleVis(action: Int) {

        when(action) {
            EditorConsts.CREATE -> {
//                ObjectAnimator.ofFloat(
//                  layoutAddMaterial, "alpha", 0f, 1f).apply {
//                    duration = 500
//                    addListener(object : AnimatorListenerAdapter() {
//                        override fun onAnimationStart(animation: Animator?) {}
//                    })
//                    start()
//                }
            }
            EditorConsts.EDIT -> {

            }
            EditorConsts.DELETE -> {

            }
        }
    }

    private fun startCreate() {
        globalVM.setFocusChip(
          Chip(0L, globalVM.getFocusId()), true)
    }

    private fun startEdit(id: Long) {
        localVM.getChip(id)
    }

    private fun getImageFrom(from: Int) {

        when(from) {
            //Check if storage write permission has already been granted;
            //  else request it
            EditorConsts.NEW  -> {
                activity?.let {
                    if(it.getStorageWritePermission()) takePicture()
                }
            }
            EditorConsts.STORAGE -> selectPicture()
        }
    }

    private fun getVideoFrom(from: Int) {

        when(from) {
            EditorConsts.NEW  -> {
                Toast.makeText(context,
                               "*Getting NEW video material*",
                               Toast.LENGTH_SHORT).show()
            }
            EditorConsts.STORAGE -> {
                Toast.makeText(context,
                               "*Getting SAVED video material*",
                               Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAudioFrom(from: Int) {

        when(from) {
            EditorConsts.NEW  -> {
                Toast.makeText(context,
                               "*Getting NEW audio material*",
                               Toast.LENGTH_SHORT).show()
            }
            EditorConsts.STORAGE -> {
                Toast.makeText(context,
                               "*Getting SAVED audio material*",
                               Toast.LENGTH_SHORT).show()
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
        globalVM.setFocusChip(data as Chip, true)
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
            EditorTextLayout.Type.TITLE -> {
                findNavController().nav(
                  EditorFragmentDirections.actionEditorFragmentToPromptFragment(
                    EditorConsts.EDIT_TITLE, text))
            }
            EditorTextLayout.Type.DESC -> {
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
            CameraUtil.CODE_GET_IMAGE -> {
                data?.let {
                    globalVM.setMat(
                      EditorConsts.IMAGE,
                      it.data?.toString() ?: "") //Save
                }
            }
            CameraUtil.CODE_TAKE_PICTURE -> {
                data?.let {
                    globalVM.setMat(
                      EditorConsts.IMAGE,
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
                   && grantResults[0] == PackageManager.PERMISSION_GRANTED)

                    takePicture()

                //TODO FUTURE: storage write permission has not been granted,
                //  inform the user
            }
        }
    }

    private fun takePicture() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        //Verifies that an application that can handle this intent exists
        if(intent.resolveActivity(packageManager)==null)
            throw UnsupportedOperationException("A camera is required for operation")

        activity?.let {
            uri =
                if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
                    CameraUtil.getImageUri(
                      it.applicationContext,
                      CameraUtil.getImageFile() ?: return)
                else {
                    CameraUtil.getImageUriNew(
                      it.applicationContext)
                }
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, CameraUtil.CODE_TAKE_PICTURE)
    }

    private fun selectPicture() {

        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            resolveActivity(packageManager)?.let {
                startActivityForResult(this, CameraUtil.CODE_GET_IMAGE)
            }
        }
    }
}