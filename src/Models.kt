interface Visitor {
    fun visit(p: Prolog) {}
    fun visit(a: Attribute){}
    fun visit(e: Entity)  : Boolean = true
    fun endvisit(e: Entity){}
    fun endvisitatt(a: Array<Attribute>){}
}

abstract class Element( val parent: Entity? = null) {
    init { parent?.children?.add(this) }
    val depth: Int
        get() =
            if (parent == null) 0
            else 1 + parent.depth

    abstract fun accept(visitor:Visitor)
}

class Prolog(var encoding: String, var version: String) : Element(){
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

class Entity(var name: String?, parent: Entity? = null) : Element(parent){
    var value  :String ?= null
    var children   = mutableListOf<Element>()
    var attribute  = mutableListOf<Attribute>()

    fun parseAttribute(): String{
        return attribute.joinToString(",")
    }

    override fun accept(visitor: Visitor) {
        if(visitor.visit(this)) {
            children.forEach {
                it.accept(visitor)
            }
            attribute.forEach {
                it.accept(visitor)
            }
        }
        visitor.endvisit(this)
    }
}


class Attribute(var name: String, var value: String) : Element(){

    override fun toString(): String {
        return " $name=\"$value\""
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

