package corp.jsonMappers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode


class CoordinatesDeserializer : JsonDeserializer<List<Any>>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): List<Any> {
        val codec = p.codec
        val node = codec.readTree<JsonNode>(p)

        if (node.isArray) {
            val list = mutableListOf<Any>()
            for (element in node) {
                when {
                    element.isNumber -> list.add(element.floatValue())
                    element.isArray -> list.add(deserialize(codec.treeAsTokens(element), ctxt))
                }
            }
            return list
        }
        throw JsonMappingException(p, "CoordinatesDeserializer error $node")
    }
}