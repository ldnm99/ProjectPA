import javax.lang.model.type.NullType

interface Visitor {
    fun visit(p: Prolog) {}
    fun visit(e: Entity)  : Boolean = true
    fun endvisit(e: Entity){}
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

//value é o texto
class Entity(parent: Entity? = null) : Element(parent){

    var name :String ?= null
    var value  :String ?= null
    var children   = mutableListOf<Element>()
    var attributes = mutableListOf<String>()


    override fun accept(visitor: Visitor) {
        if(visitor.visit(this)) { children.forEach { it.accept(visitor) } }
        visitor.endvisit(this)
    }
}

