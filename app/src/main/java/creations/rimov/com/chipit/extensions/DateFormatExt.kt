package creations.rimov.com.chipit.extensions

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Date.getChipUpdateDate() = SimpleDateFormat("MM-dd-yy '@' HH:mm:ss").format(this)

fun Date.getChipCreateDate() = SimpleDateFormat("MM-dd-yy").format(this)

fun Date.getChipFileDate() = SimpleDateFormat("yyyyMMdd'at'HHmmss").format(this)