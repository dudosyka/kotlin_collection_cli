package multiproject.server.io

import java.io.BufferedReader

object IOData {
    var current: String = "console"
    var fileReader: BufferedReader? = null
    var commandHistory: MutableList<String> = mutableListOf()
}