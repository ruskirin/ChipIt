package creations.rimov.com.chipit.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import creations.rimov.com.chipit.data.daos.ChipDao
import creations.rimov.com.chipit.data.objects.Chip

//TODO: implement migration
//TODO: handle schema exportation (see: https://stackoverflow.com/a/44645943/8916812)
@Database(
    entities = [Chip::class],
    version = 1,
    exportSchema = false)
@TypeConverters(DbVertexConverter::class)
abstract class ChipDatabase : RoomDatabase() {

    abstract fun chipDao(): ChipDao
}