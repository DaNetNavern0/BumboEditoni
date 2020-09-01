package me.danetnaverno.editoni.util

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.parser.Feature
import com.alibaba.fastjson.serializer.JSONSerializer
import com.alibaba.fastjson.serializer.SerializeWriter
import com.alibaba.fastjson.serializer.SerializerFeature
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

@Suppress("unused")
object JsonUtil
{
    fun fromPair(key: String?, value: Any?): JSONObject
    {
        val `object` = JSONObject()
        `object`[key] = value
        return `object`
    }

    fun fromPairs(vararg pairs: Any?): JSONObject
    {
        val `object` = JSONObject()
        var i = 0
        while (i < pairs.size)
        {
            `object`[pairs[i] as String?] = pairs[i + 1]
            i += 2
        }
        return `object`
    }

    @Throws(IOException::class)
    fun fromFile(path: Path): JSONObject
    {
        return try
        {
            JSON.parseObject(String(Files.readAllBytes(path), StandardCharsets.UTF_8), Feature.AllowComment)
        }
        catch (notAJsonObject: ClassCastException)
        {
            throw JSONException("Top level object is not JSONObject!")
        }
    }

    @Throws(IOException::class)
    fun fromFileArray(path: Path): JSONArray
    {
        return try
        {
            JSON.parse(String(Files.readAllBytes(path), StandardCharsets.UTF_8), Feature.AllowComment) as JSONArray
        }
        catch (notAJsonObject: ClassCastException)
        {
            throw JSONException("Top level object is not JSONArray!")
        }
    }

    @Throws(IOException::class)
    fun saveJSONToFile(path: Path, json: JSON)
    {
        val writer = SerializeWriter()
        writer.config(SerializerFeature.PrettyFormat, true)
        writer.config(SerializerFeature.WriteEnumUsingName, true)
        JSONSerializer(writer).write(json)
        val parentDir = path.parent
        if (parentDir != null && !Files.exists(parentDir)) Files.createDirectories(parentDir)
        Files.write(path, writer.toBytes(StandardCharsets.UTF_8))
    }

    fun fromString(string: String?): JSONObject
    {
        return JSONObject.parseObject(string, Feature.AllowComment)
    }
}