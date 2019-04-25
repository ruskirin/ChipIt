package creations.rimov.com.chipit.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import creations.rimov.com.chipit.database.daos.ChipChildrenDao
import creations.rimov.com.chipit.database.daos.ChipDao
import creations.rimov.com.chipit.database.daos.TopicChipDao
import creations.rimov.com.chipit.database.daos.TopicDao
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.Topic

//TODO: implement migration
//TODO: handle schema exportation (see: https://stackoverflow.com/a/44645943/8916812)
@Database(entities = [Topic::class, Chip::class], version = 1, exportSchema = false)
@TypeConverters(DbVertexConverter::class)
abstract class TopicDatabase : RoomDatabase() {

    abstract fun topicDao(): TopicDao

    abstract fun chipDao(): ChipDao

    abstract fun topicChipDao(): TopicChipDao

    abstract fun chipChildrenDao(): ChipChildrenDao
}