import javax.lang.model.type.NullType

interface Visitor {
    fun visit(prolog: Prolog) {}
    fun visit(o: Object)  : Boolean = true
    fun endObject(o: Object){}
    fun visit(array: Array)  : Boolean = true
    fun endArray(array: Array){}
    fun visit(text: Text){}
    fun visit(attribute: Attribute){}
    fun visit(nulls : Null){}
}

class Prolog() {
    var version: String? = null
    var encoding: String? = null

    fun setHeader(value1: String, value2:String) {
        version  = value1
        encoding = value2
    }

    fun accept(v: Visitor) {
        v.visit(this)
    }
}


// não seria melhor interface em vez de abstract class??? perguntar ao prof
abstract class Element {
    var name: String? = null
    var parent: Element? = null

    abstract fun observe(visitor:Visitor)
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

    override fun observe(visitor: Visitor) {
        if(visitor.visit(this)) {
            xmlContent.forEach{it.observe(visitor)}
        }
        visitor.endObject(this)
    }
}

class Array(var value: kotlin.Array<Element>): Element() {
    var children = mutableListOf<Element>()

    override fun observe(visitor: Visitor) {
        if(visitor.visit(this)) {
            children.forEach { it.observe(visitor) }
        }
        visitor.endArray(this)
    }
}

class Text(var value: String): Element() {
    override fun observe(visitor: Visitor) {
        visitor.visit(this)
    }
}

class Attribute(var value: String): Element() {
    override fun observe(visitor: Visitor) {
        visitor.visit(this)
    }
}

class Null(var value: NullType?): Element() {

    override fun observe(visitor: Visitor) {
        visitor.visit(this)
    }
}

