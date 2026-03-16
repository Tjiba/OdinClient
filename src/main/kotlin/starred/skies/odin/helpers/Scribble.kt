/*
BSD 3-Clause License

Copyright (c) 2025-2026, Starred

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

@file:Suppress("UNUSED")

package starred.skies.odin.helpers

import com.google.gson.*
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import kotlinx.coroutines.*
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import starred.skies.odin.utils.asJsonObjectOrNull
import java.io.File
import kotlin.jvm.optionals.getOrNull
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Manages persistent storage of data in JSON format.
 *
 * @property path The relative path to the storage file (without extension)
 */
class Scribble(private val path: String) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    private val file: File = File(FabricLoader.getInstance().configDir.toFile(), "odinClient/$path.json").apply { parentFile.mkdirs() }
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var root: JsonObject? = null
    private var dirty = false
    private var job: Job? = null

    private fun <T : Any> JsonElement?.toData(codec: Codec<T>): T? = codec.parse(JsonOps.INSTANCE, this).result().getOrNull()
    private fun <T : Any> T.toJson(codec: Codec<T>): JsonElement? = codec.encodeStart(JsonOps.INSTANCE, this).result().getOrNull()

    init {
        ClientLifecycleEvents.CLIENT_STOPPING.register { _ ->
            scope.launch {
                job?.cancelAndJoin()
                save()
            }
        }
    }

    private fun load(): JsonObject {
        if (root != null) return root!!

        root = try {
            if (!file.exists() || file.length() <= 0) JsonObject()
            else JsonParser.parseString(file.readText()).asJsonObjectOrNull?.get("@odinClient:data")?.asJsonObjectOrNull ?: JsonObject()
        } catch (e: Exception) {
            JsonObject()
        }

        return root!!
    }

    private fun save() {
        if (!dirty) return

        try {
            val data = root ?: return

            val wrapped = JsonObject().apply {
                add("@odinClient:data", data)
            }

            val tempFile = File(file.parent, "${file.name}.tmp")
            tempFile.writeText(gson.toJson(wrapped))

            if (file.exists()) file.delete()
            if (!tempFile.renameTo(file)) {
                tempFile.copyTo(file, overwrite = true)
                tempFile.delete()
            }

            dirty = false
        } catch (e: Exception) {}
    }

    fun dirty() {
        dirty = true
        job?.cancel()
        job = scope.launch {
            delay(5000)
            save()
        }
    }


    fun reload() {
        root = null
        dirty = false
    }

    fun jsonObject(key: String, default: JsonObject = JsonObject()) = object : ReadWriteProperty<Any?, JsonObject> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): JsonObject {
            val obj = load()

            if (!obj.has(key)) {
                obj.add(key, default)
                dirty()
            }

            return obj.getAsJsonObject(key)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: JsonObject) {
            load().add(key, value)
            dirty()
        }
    }

    inner class Value<T : Any>(
        private val key: String,
        private val default: T,
        private val codec: Codec<T>
    ) : ReadWriteProperty<Any?, T> {
        var value: T
            get() = load().get(key)?.toData(codec) ?: default
            set(value) {
                value.toJson(codec)?.let {
                    load().add(key, it)
                    dirty()
                }
            }

        init {
            val obj = load()

            if (!obj.has(key)) default.toJson(codec)?.let {
                obj.add(key, it)
                dirty()
            }
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            this.value = value
        }

        fun update(block: T.() -> Unit) {
            value = value.apply(block)
        }
    }

    fun int(key: String, default: Int = 0) = Value(key, default, Codec.INT)
    fun long(key: String, default: Long = 0L) = Value(key, default, Codec.LONG)
    fun string(key: String, default: String = "") = Value(key, default, Codec.STRING)
    fun boolean(key: String, default: Boolean = false) = Value(key, default, Codec.BOOL)
    fun double(key: String, default: Double = 0.0) = Value(key, default, Codec.DOUBLE)
    fun float(key: String, default: Float = 0f) = Value(key, default, Codec.FLOAT)

    fun <T : Any> list(key: String, codec: Codec<T>, default: List<T> = emptyList()) = Value(key, default, codec.listOf())
    fun <T : Any> set(key: String, codec: Codec<T>, default: Set<T> = emptySet()) = Value(key, default, codec.listOf().xmap({ it.toSet() }, { it.toList() }))
    fun <K : Any, V : Any> map(key: String, keyCodec: Codec<K>, valueCodec: Codec<V>, default: Map<K, V> = emptyMap()) = Value(key, default, Codec.unboundedMap(keyCodec, valueCodec))

    fun <T : Any> mutableList(key: String, codec: Codec<T>, default: MutableList<T> = mutableListOf()) = Value(key, default, codec.listOf().xmap({ it.toMutableList() }, { it.toList() }))
    fun <T : Any> mutableSet(key: String, codec: Codec<T>, default: MutableSet<T> = mutableSetOf()) = Value(key, default, codec.listOf().xmap({ it.toMutableSet() }, { it.toList() }))
    fun <K : Any, V : Any> mutableMap(key: String, keyCodec: Codec<K>, valueCodec: Codec<V>, default: MutableMap<K, V> = mutableMapOf()) = Value(key, default, Codec.unboundedMap(keyCodec, valueCodec).xmap({ it.toMutableMap() }, { it.toMap() }))
}