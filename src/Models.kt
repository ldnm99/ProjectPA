import javax.lang.model.type.NullType

class Prolog() {
    var version: String? = null
    var encoding: String? = null

    fun setHeader(value1: String, value2:String) {
        version  = value1
        encoding = value2
    }
}

abstract class Element {
    var name: String? = null
    var parent: Element? = null
}

class Object: Element() {

    var xmlContent = mutableListOf<Element>()

    // revisit recursive stuff
    // one function for arrays and one for the rest
    private fun temp(array: Array, xmlElement: Element) {
        val newElement: Element?
        when (xmlElement) {
            is Object -> {
                newElement = Object()
                xmlElement.xmlContent.forEach {newElement.setProperty(it.name!!, it)}
            }
            is Array -> {
                newElement = Array(xmlElement.value)
                newElement.value.forEach{temp(newElement, it)}
            }
            is Text -> newElement = Text(xmlElement.value)
            is Attribute -> newElement = Attribute(xmlElement.value)
            is Null -> newElement = Null(xmlElement.value)
            else -> newElement = null
        }
        newElement!!.parent = array
        array.children.add(newElement)
    }

    fun setProperty(name: String, xmlElement: Element) {
        val newElement: Element?
        when (xmlElement) {
            is Object -> {
                newElement = Object()
                xmlElement.xmlContent.forEach {newElement.setProperty(it.name!!, it)}
            }
            is Array -> {
                newElement = Array(xmlElement.value)
                newElement.value.forEach {temp(newElement, it)}
            }
            is Text -> newElement = Text(xmlElement.value)
            is Attribute -> newElement = Attribute(xmlElement.value)
            is Null -> newElement = Null(xmlElement.value)
            else -> newElement = null
        }
        newElement!!.parent = this
        newElement.name = name
        this.xmlContent.add(newElement)
    }
}

class Array(var value: kotlin.Array<Element>): Element() {
    var children = mutableListOf<Element>()
}

class Text(var value: String): Element() {

}

class Attribute(var value: String): Element() {

}

class Null(var value: NullType?): Element() {

}

