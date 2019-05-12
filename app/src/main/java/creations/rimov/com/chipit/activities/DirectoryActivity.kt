package creations.rimov.com.chipit.activities

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.Topic
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.view_models.DirectoryViewModel
import creations.rimov.com.chipit.view_models.WebViewModel
import java.io.IOException

class DirectoryActivity : AppCompatActivity() {

    private val dirViewModel: DirectoryViewModel by lazy {
        ViewModelProviders.of(this).get(DirectoryViewModel::class.java)
    }

    private val navHostFragment: NavHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.directory_nav_host) as NavHostFragment
    }

    private lateinit var addChipFab: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.directory_layout)

        addChipFab = findViewById(R.id.directory_layout_fab_action)

        addChipFab.setOnClickListener { view ->

            dirViewModel.setFabTouch(true)

            addChipFab.hide()
        }
    }
}