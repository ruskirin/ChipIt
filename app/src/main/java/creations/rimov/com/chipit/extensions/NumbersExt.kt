package creations.rimov.com.chipit.extensions

fun Long?.secToTimeFormat(): String? {

    if(this==null) return null

    val hr = this / 3600
    val min = (this - hr*3600) / 60
    val sec = this - min*60 - hr*3600

    return if(hr==0L) String.format("%02d:%02d", min, sec)
           else String.format("%02d:%02d:%02d", hr, min, sec)
}