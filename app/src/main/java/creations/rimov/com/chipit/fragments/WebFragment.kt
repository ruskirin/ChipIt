package creations.rimov.com.chipit.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.adapters.WebRecyclerAdapter
import creations.rimov.com.chipit.adapters.viewholders.web.WebTouchCallback
import creations.rimov.com.chipit.constants.EditorConsts
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.extensions.getViewModel
import creations.rimov.com.chipit.extensions.nav
import creations.rimov.com.chipit.view_models.CommsViewModel
import creations.rimov.com.chipit.view_models.WebViewModel
import kotlinx.android.synthetic.main.frag_web.view.*
import kotlinx.android.synthetic.main.web_detail.*

class WebFragment
    : Fragment(),
      MotionLayout.TransitionListener,
      View.OnTouchListener,
      WebTouchCallback.Handler {

    //Passed Bundle from DirectoryFragment
    private val passedArgs by navArgs<WebFragmentArgs>()

    private lateinit var commsVM: CommsViewModel
    private val localVM: WebViewModel by lazy {
        getViewModel<WebViewModel>()
    }

    private lateinit var motionLayout: MotionLayout

    private val touchHelper: ItemTouchHelper by lazy {
        ItemTouchHelper(WebTouchCallback(this))
    }

    private val detailImage: ImageView by lazy {webDetailImg}

    private lateinit var childrenAdapter: WebRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            commsVM = it.getViewModel()
            childrenAdapter = WebRecyclerAdapter(it.applicationContext)
        }

        localVM.setChip(passedArgs.parentId)
    }

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.frag_web, container, false)

        motionLayout = (view as MotionLayout)
        motionLayout.setTransitionListener(this)

        view.webChildrenView.apply {
            adapter = childrenAdapter
            layoutManager = StaggeredGridLayoutManager(
              2, RecyclerView.VERTICAL)
            setHasFixedSize(true)

            touchHelper.attachToRecyclerView(this)
        }

        localVM.getChip().observe(viewLifecycleOwner, Observer {
            Log.i("WebFrag", "chipObserver: currently displaying " +
                             "chip \"${it?.name}\"")

            commsVM.setFocusChip(it, false)
            setDetail(it)
        })
        //Parents to display in toolbar from MainActivity
        localVM.getParents().observe(viewLifecycleOwner, Observer {
            commsVM.setWebParents(it)
        })

        localVM.getChildren().observe(viewLifecycleOwner, Observer {
            childrenAdapter.setChips(it)
        })

        return view
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        if(event == null) return false

        when(view?.id) {
            //TODO FUTURE: see if anything needs to be done here
        }

        return true
    }

    private fun setDetail(chip: Chip?) {

        if(chip?.parentId == null) {
            motionLayout.transitionToState(R.id.motionSceneNoDetail)
            return
        }

        motionLayout.transitionToState(R.id.motionSceneWebStart)

        webDetailDesc.text = chip.desc
        Glide.with(this)
            .load(chip.matPath)
            .apply(
                RequestOptions()
                    .override(detailImage.width, detailImage.height)
                    .diskCacheStrategy(DiskCacheStrategy.DATA))
            .into(detailImage)
    }

    private fun loadChipper() {

        val directions = WebFragmentDirections
            .actionWebFragmentToChipperFragment(
              localVM.getChip().value?.id ?: return)

        findNavController().nav(directions)
    }

    //WebTouchCallback Handler -------------------------------------------------------------
    override fun promptDelete(id: Long) {

        findNavController().nav(
          WebFragmentDirections.actionWebFragmentToEditorFragment(
            EditorConsts.DELETE, id))
    }

    //MotionLayout.TransitionListener ------------------------------------------
    override fun onTransitionChange(
      motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {

        motionLayout?.let {
            if(startId == R.id.motionSceneWebStart && endId == R.id.motionSceneWebMax) {

                //TODO FUTURE: preload the bitmap here based on progress of transition

                if(progress > 0.5f) commsVM.setWebTransition(true)
                else commsVM.setWebTransition(false)
            }
        }
    }

    override fun onTransitionCompleted(
      motionLayout: MotionLayout?, id: Int) {

        motionLayout?.let {
            when(id) {
                R.id.motionSceneWebMax -> {
                    loadChipper()
                }
                R.id.motionSceneWebStart -> {

                }
            }
        }
    }

    override fun onTransitionTrigger(
      p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}

    override fun onTransitionStarted(
      p0: MotionLayout?, p1: Int, p2: Int) {}

    //--------------------------------------------------------------------------
}