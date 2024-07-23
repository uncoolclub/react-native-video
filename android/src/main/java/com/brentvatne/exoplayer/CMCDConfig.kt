package com.brentvatne.exoplayer

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.upstream.CmcdConfiguration
import androidx.media3.exoplayer.upstream.CmcdConfiguration.*
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.google.common.collect.ImmutableListMultimap

data class CMCDConfig(
    val cmcdObject: List<Pair<String, String>>?,
    val cmcdRequest: List<Pair<String, String>>?,
    val cmcdSession: List<Pair<String, String>>?,
    val cmcdStatus: List<Pair<String, String>>?,
    val mode: Int
) {
    fun hasSettings(): Boolean = cmcdObject != null || cmcdRequest != null || cmcdSession != null || cmcdStatus != null

    fun toCmcdConfigurationFactory(): Factory =
        Factory { mediaItem ->
            createCmcdConfiguration(mediaItem)
        }

    private fun createCmcdConfiguration(mediaItem: MediaItem): CmcdConfiguration =
        CmcdConfiguration(
            java.util.UUID.randomUUID().toString(),
            mediaItem.mediaId,
            object : RequestConfig {
                override fun getCustomData(): ImmutableListMultimap<String, String> = buildCustomData()
            },
            mode
        )

    private fun buildCustomData(): ImmutableListMultimap<String, String> {
        val builder = ImmutableListMultimap.builder<String, String>()
        addFormattedData(builder, KEY_CMCD_OBJECT, cmcdObject)
        addFormattedData(builder, KEY_CMCD_REQUEST, cmcdRequest)
        addFormattedData(builder, KEY_CMCD_SESSION, cmcdSession)
        addFormattedData(builder, KEY_CMCD_STATUS, cmcdStatus)
        return builder.build()
    }

    private fun addFormattedData(builder: ImmutableListMultimap.Builder<String, String>, key: String, dataList: List<Pair<String, String>>?) {
        dataList?.forEach { (dataKey, dataValue) ->
            builder.put(key, "$dataKey=$dataValue")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CMCDConfig

        if (!cmcdObject.contentDeepEquals(other.cmcdObject)) return false
        if (!cmcdRequest.contentDeepEquals(other.cmcdRequest)) return false
        if (!cmcdSession.contentDeepEquals(other.cmcdSession)) return false
        if (!cmcdStatus.contentDeepEquals(other.cmcdStatus)) return false
        if (mode != other.mode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cmcdObject.contentDeepHashCode()
        result = 31 * result + (cmcdRequest.contentDeepHashCode())
        result = 31 * result + (cmcdSession.contentDeepHashCode())
        result = 31 * result + (cmcdStatus.contentDeepHashCode())
        result = 31 * result + mode
        return result
    }

    companion object {
        private const val PROP_CMCD_OBJECT = "object"
        private const val PROP_CMCD_REQUEST = "request"
        private const val PROP_CMCD_SESSION = "session"
        private const val PROP_CMCD_STATUS = "status"
        private const val PROP_CMCD_MODE = "mode"

        @JvmStatic
        fun fromReadableMap(cmcd: ReadableMap): CMCDConfig =
            Builder()
                .setObject(cmcd.getArray(PROP_CMCD_OBJECT)?.let { convertToList(it) })
                .setRequest(cmcd.getArray(PROP_CMCD_REQUEST)?.let { convertToList(it) })
                .setSession(cmcd.getArray(PROP_CMCD_SESSION)?.let { convertToList(it) })
                .setStatus(cmcd.getArray(PROP_CMCD_STATUS)?.let { convertToList(it) })
                .setMode(cmcd.getInt(PROP_CMCD_MODE))
                .build()

        private fun convertToList(readableArray: ReadableArray): List<Pair<String, String>> =
            (0 until readableArray.size()).mapNotNull { i ->
                val item = readableArray.getMap(i)
                val key = item?.getString("key")
                val value = item?.getString("value")
                if (key != null && value != null) Pair(key, value) else null
            }
    }

    class Builder {
        private var cmcdObject: List<Pair<String, String>>? = null
        private var cmcdRequest: List<Pair<String, String>>? = null
        private var cmcdSession: List<Pair<String, String>>? = null
        private var cmcdStatus: List<Pair<String, String>>? = null
        private var mode: Int = CmcdConfiguration.MODE_QUERY_PARAMETER

        fun setObject(value: List<Pair<String, String>>?) = apply { this.cmcdObject = value }
        fun setRequest(value: List<Pair<String, String>>?) = apply { this.cmcdRequest = value }
        fun setSession(value: List<Pair<String, String>>?) = apply { this.cmcdSession = value }
        fun setStatus(value: List<Pair<String, String>>?) = apply { this.cmcdStatus = value }
        fun setMode(value: Int) = apply { this.mode = value }

        fun build(): CMCDConfig = CMCDConfig(cmcdObject, cmcdRequest, cmcdSession, cmcdStatus, mode)
    }
}

fun <T> List<T>?.contentDeepEquals(other: List<T>?): Boolean {
    if (this === other) return true
    if (this == null || other == null) return false
    if (this.size != other.size) return false
    return this.zip(other).all { (a, b) -> a == b }
}

fun <T> List<T>?.contentDeepHashCode(): Int {
    if (this == null) return 0
    return this.fold(1) { acc, elem -> 31 * acc + (elem?.hashCode() ?: 0) }
}
