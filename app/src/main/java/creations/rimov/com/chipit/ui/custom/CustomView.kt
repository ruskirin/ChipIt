package creations.rimov.com.chipit.ui.custom

interface CustomView<T> {

    var handler: T

    /** Way of initializing a custom viewgroup with a communication interface
     * @param handler: instance of an interface for communication with parent
     * @param opts: optional arguments
     */
    fun prepare(handler: T, vararg opts: Any?)
}