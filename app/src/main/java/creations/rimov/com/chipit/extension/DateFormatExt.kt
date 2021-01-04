package creations.rimov.com.chipit.extension

import java.text.SimpleDateFormat
import java.util.*

//TODO FUTURE: see DateTimeFormatter alternative that is threadsafe
fun Date.getChipUpdateDate(): String
  = SimpleDateFormat("MM-dd-yy '@' HH:mm:ss").format(this)

fun Date.getChipCreateDate(): String
  = SimpleDateFormat("MM-dd-yy").format(this)

fun Date.getChipFileDate(): String
  = SimpleDateFormat("yyyyMMdd'at'HHmmss").format(this)