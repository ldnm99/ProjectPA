class XML_Prolog() {
    var  version: String?= null
    var encoding: String?= null
}

abstract class XML_Element {
    var name: String? = null
    var parent: XML_Element? = null
}

class XML_Object: XML_Element() {
    var xmlContent = mutableListOf<XML_Element>()

    fun setProperty(key: String, xmlElement: XML_Element) {
        val newElement: XML_Element?
        when (xmlElement) {
            is XML_Object -> {
                newElement = XML_Object()
            }
            is XML_Array -> {
                newElement = XML_Array(xmlElement.value)
            }
            is XML_Text -> XML_Text(xmlElement.value)
            is XML_Attribute -> newElement = XML_Attribute(xmlElement.value)
            is XML_Null -> newElement = XML_Null(xmlElement.value)
            else -> newElement = null
        }
        this.xmlContent.add(newElement)
    }


}

class XML_Array(var value: Array<XML_Element>): XML_Element() {
    var children = mutableListOf<XML_Element>()
}

class XML_Text(var value: String): XML_Element() {

}

class XML_Attribute(var value: String): XML_Element() {

}

class XML_Null(var value: NullType?): XML_Element() { // "null"

}

