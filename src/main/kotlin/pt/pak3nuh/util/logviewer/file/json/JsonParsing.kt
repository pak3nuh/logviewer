package pt.pak3nuh.util.logviewer.file.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlin.reflect.KClass

typealias JsonMap = Map<String, Any>

object JsonParsing {

    private val jsonParser: Gson = GsonBuilder().create()

    fun <T: Any> parse(json: String, kClass: KClass<T>): T {
        return jsonParser.fromJson(json, kClass.java)
    }

    inline fun <reified T: Any> parse(json: String): T = parse(json, T::class)
}