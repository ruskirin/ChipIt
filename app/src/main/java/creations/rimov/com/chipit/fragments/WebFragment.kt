package creations.rimov.com.chipit.fragments

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.activities.MainActivity
import creations.rimov.com.chipit.adapters.WebRecyclerAdapter
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.objects.ChipAction
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.view_models.WebViewModel
import kotlinx.android.synthetic.main.web_detail_layout.*
import kotlinx.android.synthetic.main.web_layout.view.*

class WebFragment : Fragment(),
    WebRecyclerAdapter.WebAdapterHandler, View.OnTouchListener, MotionLayout.TransitionListener {

    //Passed Bundle from DirectoryFragment
    private val passedArgs by navArgs<WebFragmentArgs>()

    private lateinit var globalViewModel: GlobalViewModel
    private val localViewModel: WebViewModel by lazy {
        ViewModelProviders.of(this).get(WebViewModel::class.java)
    }

    private lateinit var motionLayout: MotionLayout

    private val detailImage: ImageView by lazy {webDetailImg}

    private val childrenAdapter: WebRecyclerAdapter by lazy {WebRecyclerAdapter(this@WebFragment)}

    private lateinit var gestureDetector: GestureDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            globalViewModel = ViewModelProviders.of(it).get(GlobalViewModel::class.java)

            gestureDetector = GestureDetector(it, ChipGestureDetector())
            gestureDetector.setIsLongpressEnabled(true)
        }

        val id = passedArgs.parentId

        if(id == -1L) globalViewModel.setFocusId(null)
        else globalViewModel.setFocusId(id)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.web_layout, container, false)

        motionLayout = (view as MotionLayout)
        motionLayout.setTransitionListener(this)

        view.webChildrenView.apply {
            adapter = childrenAdapter
            layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
            setHasFixedSize(true)
        }

        globalViewModel.getFocusId().observe(this, Observer {
            localViewModel.setFocusId(it)
        })

        localViewModel.getChip().observe(this, Observer {
            setDetail(it)
        })
        //Parents to display in toolbar from MainActivity
        localViewModel.getParents().observe(this, Observer {
            globalViewModel.setWebParents(it)
        })

        localViewModel.getChildren().observe(this, Observer {
            childrenAdapter.setChips(it)
        })

        return view
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        if(event == null) return false

        when(view?.id) {

//            R.id.webDetailBtnSettings -> {
//
//                if(event.action == MotionEvent.ACTION_UP) {
//
//                    localViewModel.getAsChip()?.let {
//                        globalViewModel.setChipAction(
//                            ChipAction.instance(it, MainActivity.EditorAction.EDIT))
//                    }
//                }
//            }
//
//            R.id.webDetailBtnDelete -> {
//
//                if(event.action == MotionEvent.ACTION_UP) {
//
//                    localViewModel.getAsChip()?.let {
//                        globalViewModel.setChipAction(
//                              ChipAction.instance(it, MainActivity.EditorAction.DELETE))
//                    }
//                }
//            }
        }

        return true
    }

    private fun setDetail(chip: ChipIdentity?) {

        if(chip == null || chip.isTopic) {
            motionLayout.transitionToState(R.id.motionSceneNoDetail)
            return
        }

        motionLayout.transitionToState(R.id.motionSceneWebStart)

        webDetailDesc.text = chip.desc
        Glide.with(this)
            .load(chip.imgLocation)
            .apply(
                RequestOptions()
                    .override(detailImage.width, detailImage.height)
                    .diskCacheStrategy(DiskCacheStrategy.DATA))
            .into(detailImage)
    }

    private fun loadChipper() {

        val directions =
            WebFragmentDirections.actionWebFragmentToChipperFragment(localViewModel.getChipId() ?: return)
        findNavController().navigate(directions)
    }

    //RecyclerAdapter Handler ---------------------------------------------------------
    override fun chipTouch(event: MotionEvent) {
        gestureDetector.onTouchEvent(event)
    }

    override fun chipDelete(chip: ChipCard) {
        globalViewModel.setChipAction(
              ChipAction.instance(
                    chip.getChip(localViewModel.getChipId()), MainActivity.EditorAction.DELETE))
    }
    //---------------------------------------------------------------------------------

    //MotionLayout.TransitionListener -------------------------------------------------
    override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {

        motionLayout?.let {
            if(startId == R.id.motionSceneWebStart && endId == R.id.motionSceneWebMax) {

                //TODO FUTURE: preload the bitmap here based on progress of transition

                if(progress > 0.5f) globalViewModel.setWebTransition(true)
                else globalViewModel.setWebTransition(false)
            }
        }
    }
    override fun onTransitionCompleted(motionLayout: MotionLayout?, id: Int) {

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
    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
    //---------------------------------------------------------------------------------

    inner class ChipGestureDetector : GestureDetector.SimpleOnGestureListener() {

        //Must return true to ensure gestures are not ignored
        override fun onDown(event: MotionEvent?): Boolean {
            return true
        }

        override fun onSingleTapUp(event: MotionEvent?): Boolean {

            globalViewModel.setFocusId(childrenAdapter.getSelectedId())
            return true
        }

        override fun onLongPress(event: MotionEvent?) {

            if(childrenAdapter.selectedChip.isEditing())
                childrenAdapter.selectedChip.edit(false)
            else
                childrenAdapter.selectedChip.edit(true)
        }
    }
}