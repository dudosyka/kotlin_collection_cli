package multiproject.client.io

import java.io.BufferedReader

object IOData {
    var changeSourceCommand = "execute_script"
    var current: String = "console"
    var fileReader: BufferedReader? = null
    var commandHistory: MutableList<String> = mutableListOf()
}