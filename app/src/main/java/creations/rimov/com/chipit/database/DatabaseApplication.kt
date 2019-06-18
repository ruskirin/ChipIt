package creations.rimov.com.chipit.database

import android.app.Application
import androidx.room.Room

class DatabaseApplication : Application() {

    companion object {

        var database: ChipDatabase? = null
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            this, ChipDatabase::class.java, "name-db").build()
    }
}