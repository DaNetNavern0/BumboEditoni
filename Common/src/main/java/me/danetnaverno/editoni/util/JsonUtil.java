package me.danetnaverno.editoni.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class JsonUtil
{
    @SuppressWarnings("unchecked")
    public static JSONArray JsonArray(List<?> elements)
    {
        return new JSONArray((List<Object>) elements);
    }

    public static JSONObject fromPair(String key, Object value)
    {
        JSONObject object = new JSONObject();
        object.put(key, value);
        return object;
    }

    public static JSONObject fromPairs(Object... pairs)
    {
        JSONObject object = new JSONObject();
        for (int i = 0; i < pairs.length; i += 2)
            object.put((String) pairs[i], pairs[i + 1]);
        return object;
    }

    public static JSONObject fromMap(Map<String, Object> map)
    {
        JSONObject object = new JSONObject();
        map.forEach(object::put);
        return object;
    }

    public static JSONObject fromFile(Path path) throws IOException
    {
        try
        {
            return JSON.parseObject(
                    new String(Files.readAllBytes(path), StandardCharsets.UTF_8),
                    Feature.AllowComment);
        }
        catch (ClassCastException notAJsonObject)
        {
            throw new JSONException("Top level object is not JSONObject!");
        }
    }

    public static JSONArray fromFileArray(Path path) throws IOException
    {
        try
        {
            return (JSONArray) JSON.parse(
                    new String(Files.readAllBytes(path), StandardCharsets.UTF_8),
                    Feature.AllowComment);
        }
        catch (ClassCastException notAJsonObject)
        {
            throw new JSONException("Top level object is not JSONArray!");
        }
    }

    public static void saveJSONToFile(Path path, JSON json) throws IOException
    {
        SerializeWriter writer = new SerializeWriter();
        writer.config(SerializerFeature.PrettyFormat, true);
        writer.config(SerializerFeature.WriteEnumUsingName, true);
        new JSONSerializer(writer).write(json);
        Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir))
            Files.createDirectories(parentDir);
        Files.write(path, writer.toBytes(StandardCharsets.UTF_8));
    }

    public static JSONObject fromString(String string)
    {
        return JSONObject.parseObject(string, Feature.AllowComment);
    }
}