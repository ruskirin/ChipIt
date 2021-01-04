package creations.rimov.com.chipit.util.constants

enum class FileTypes {
    IMAGE {
        override fun types() = listOf("jpg", "jpeg")
    },
    VIDEO {
        override fun types() = listOf("mp4")
    },
    AUDIO {
        override fun types() = listOf("mp3")
    },
    TEXT {
        override fun types() = listOf("txt", "pdf")
    };

    abstract fun types(): List<String>
}