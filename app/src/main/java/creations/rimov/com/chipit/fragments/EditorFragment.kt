package creations.rimov.com.chipit.fragments

import android.Manifest
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.activities.MainActivity
import creations.rimov.com.chipit.constants.EditorConsts
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.view_models.EditorViewModel
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.viewgroups.EditorTextLayout
import creations.rimov.com.chipit.viewgroups.EditorMatPrevLayout
import kotlinx.android.synthetic.main.frag_editor.view.*

class EditorFragment : Fragment(),
    EditorTextLayout.Handler,
    EditorMatPrevLayout.Handler {

    private lateinit var globalVM: GlobalViewModel
    private val localVM by lazy {
        ViewModelProvider(this).get(EditorViewModel::class.java)
    }

    private val passedArgs by navArgs<EditorFragmentArgs>()

    private lateinit var packageManager: PackageManager

    private lateinit var name: EditorTextLayout
    private lateinit var desc: EditorTextLayout
    private lateinit var material: EditorMatPrevLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            packageManager = it.packageManager
            globalVM = ViewModelProvider(it).get(GlobalViewModel::class.java)
        }

        Log.i("LIFE CYCLE", "PromptFragment.onCreate() - passedArgs = $passedArgs")

        globalVM.setAction(passedArgs.action)
    }

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.frag_editor, container, false)

        material = v.matPreviewLayout.apply {
            prepare(this@EditorFragment)
        }

        passedArgs.chip.let {
            name = v.matTitleLayout.apply {
                prepare(this@EditorFragment,
                        EditorTextLayout.Type.TITLE,
                        it?.name)
            }
            desc = v.matDescLayout.apply {
                prepare(this@EditorFragment,
                        EditorTextLayout.Type.DESC,
                        it?.desc)
            }
        }

        globalVM.getAction().observe(viewLifecycleOwner, Observer { action ->
            Log.i("EVENT", "PromptFrag: new globalVM action = $action")
            toggleVis(action)

            when(action) {
                EditorConsts.SAVE -> {
                    //TODO HIGH: test if all required fields were entered then save
                }
                EditorConsts.DELETE -> {
                    //TODO HIGH: call delete from the repository
                }
                EditorConsts.CANCEL    -> {
                    //TODO HIGH: (see if necessary)
                    //  delete any material that was taken before cancellation
                }
                EditorConsts.NEW_IMAGE -> {
                    getImageFrom(EditorConsts.NEW)
                }
                EditorConsts.STOR_IMAGE -> {
                    getImageFrom(EditorConsts.STORAGE)
                }
                EditorConsts.NEW_VIDEO -> {
                    getVideoFrom(EditorConsts.NEW)
                }
                EditorConsts.STOR_VIDEO -> {
                    getVideoFrom(EditorConsts.STORAGE)
                }
                EditorConsts.NEW_AUDIO -> {
                    getAudioFrom(EditorConsts.NEW)
                }
                EditorConsts.STOR_AUDIO -> {
                    getAudioFrom(EditorConsts.STORAGE)
                }
                EditorConsts.NEW_TEXT -> {
                    getTextFrom(EditorConsts.NEW)
                }
                EditorConsts.STOR_TEXT  -> {
                    getTextFrom(EditorConsts.STORAGE)
                }
            }
        })

        return v
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

    //PromptMatPrevLayout Handler------------------------------------------------------
    override fun promptAddMat() {

        findNavController().navigate(
          EditorFragmentDirections.actionEditorFragmentToPromptFragment(
            EditorConsts.EDIT_RES))
    }

    //EditorTextLayout Handler---------------------------------------------------------
    override fun promptEditText(type: Int, text: String) {

        when(type) {
            EditorTextLayout.Type.TITLE -> {
                findNavController().navigate(
                  EditorFragmentDirections.actionEditorFragmentToPromptFragment(
                    EditorConsts.EDIT_TITLE))
            }
            EditorTextLayout.Type.DESC -> {
                findNavController().navigate(
                  EditorFragmentDirections.actionEditorFragmentToPromptFragment(
                    EditorConsts.EDIT_DESC))
            }
        }
    }

    //---------------------------------------------------------------------------------

    private fun getImageFrom(from: Int) {

        when(from) {
            EditorConsts.NEW  -> {
                //Check if storage write permission has already been granted;
                //  else request it
                if(getStorageWritePermission())
                    takePicture()
            }
            EditorConsts.STORAGE -> {
                selectPicture()
            }
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

    private fun takePicture() {

        val addChipCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //Verifies that an application that can handle this intent exists
        if(addChipCameraIntent.resolveActivity(packageManager)==null) {
            Log.e("Touch Event", "Main#takePicture(): no app to handle " +
                                 "ACTION_IMAGE_CAPTURE!")
            return
        }

        var uri: Uri? = null

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            val imageFile = CameraUtil.getImageFile() ?: return

            activity?.let {
                uri = CameraUtil.getImageUri(it.applicationContext, imageFile)
            }
        } else {
            activity?.let {
                uri = CameraUtil.getImageUriNew(it.applicationContext)
            }
        }

        addChipCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(addChipCameraIntent, CameraUtil.CODE_TAKE_PICTURE)
    }

    private fun selectPicture() {

        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            resolveActivity(packageManager)?.let {
                startActivityForResult(this, CameraUtil.CODE_GET_IMAGE)
            }
        }
    }

    //After startActivityForResult()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != Activity.RESULT_OK) return

        when(requestCode) {
            CameraUtil.CODE_GET_IMAGE -> {
                data?.let {
                    material.setPreview(EditorConsts.IMAGE, it.data)

                    localVM.setMat(
                      EditorConsts.IMAGE,
                      it.data?.toString() ?: "") //Save
                }
            }
            CameraUtil.CODE_TAKE_PICTURE -> {
                data?.let {
                    Log.i("EditorFragment",
                          "onActivityResult(): picture taken! Intent extras " +
                          "null? ${it.extras==null}. Intent data null? ${it.data==null}")

                    material.setPreview(EditorConsts.IMAGE, it.extras?.get("data"))

                    localVM.setMat(
                      EditorConsts.IMAGE,
                      it.data?.toString() ?: "") //Save
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
                //TODO FUTURE: storage write permission has not been granted, inform the user
            }
        }
    }

    //Since SDK 23(24?), permission must be requested at runtime if it has not
    // already been granted
    private fun getStorageWritePermission(): Boolean {

        activity?.let {
            if(ContextCompat.checkSelfPermission(
                it, Manifest.permission.WRITE_EXTERNAL_STORAGE)
              == PackageManager.PERMISSION_GRANTED) {
                return true //Permission has already been granted
            }

            //Explain why you need the permission
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                it, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //TODO FUTURE: display rationale for this request
                Toast.makeText(it, "Need permission please!",
                               Toast.LENGTH_SHORT).show()
            }

            //Permission has not yet been granted, check onRequestPermissionResult()
            ActivityCompat.requestPermissions(
              it,
              arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
              MainActivity.Constant.REQUEST_WRITE_EXTERNAL_STORAGE)
        }

        return false
    }
}