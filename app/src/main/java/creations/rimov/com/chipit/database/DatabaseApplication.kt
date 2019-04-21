package creations.rimov.com.chipit.database

import android.app.Application
import androidx.room.Room

class DatabaseApplication : Application() {

    companion object {

        var database: TopicDatabase? = null
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            this, TopicDatabase::class.java, "topic-db").build()
    }
}