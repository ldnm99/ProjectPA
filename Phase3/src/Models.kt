interface Visitor {
    fun visit(p: Prolog) {}
    fun visit(a: Attribute){}
    fun visit(e: Entity)  : Boolean = true
    fun endvisit(e: Entity){}
    fun endvisitatt(a: Array<Attribute>){}
}

abstract class Element( val parent: Entity? = null) : IObservable<(EventType,String , String?)-> Unit>{
    init { parent?.children?.add(this) }
    val depth: Int
        get() =
            if (parent == null) 0
            else 1 + parent.depth

    abstract fun accept(visitor: Visitor)
}

class Prolog(var encoding: String, var version: String) : Element(){
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    override val observers: MutableList<(EventType, String, String?) -> Unit>
        get() = TODO("Not yet implemented")
}

class Entity(var name: String, parent: Entity? = null) : Element(parent){
    var value  :String ?= null
    var children   = mutableListOf<Element>()
    var attribute  = mutableListOf<Attribute>()

    fun parseAttribute(): String{
        return attribute.joinToString(",")
    }

    fun setText(text: String){
        if (children.isEmpty()) {
            value = text
            specialChar()
        }else{
            foo()
        }
    }

    fun addChild(cName:String){
        if(children.add(Entity(cName,this))){
            notifyObservers {
                it(EventType.ADD,"cName",null)
            }
        }
    }

    fun removeChild(cName: String){
        run breaker@{
            children.forEach {
                it as Entity
                if (it.name.equals(cName)) return@breaker
                children.remove(it)
            }
        }
        notifyObservers {
            it(EventType.REMOVE, cName, null)
        }
    }

    fun addAtt(){

    }


    private fun specialChar() {
        if(value!!.contains("&"))
            value  =  value!!.replace("&","&amp")
        if(value!!.contains("\""))
            value  =  value!!.replace("\"","&quot")
        if(value!!.contains("\'"))
            value  =  value!!.replace("\'","&apos")
        if(value!!.contains("<"))
            value  =  value!!.replace("<","&lt")
        if(value!!.contains(">"))
            value  =  value!!.replace(">","&gt")
    }

    //returns attribute with specific name
    fun getAttribute(value: String): Attribute? {
        var att: Attribute? = null
        attribute.forEach{
            if (it.name.equals(value))
                att = it
        }
        return att
    }

    fun getAttribute2(value: Attribute): Attribute {
        var att = Attribute("", "")
        attribute.forEach{
            if(it == value)
                att = it
        }
        return att
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

    override val observers: MutableList<(EventType, String, String?) -> Unit>
        get() = TODO("Not yet implemented")
}

class Attribute(var name: String, var value: String, parent: Entity? = null) : Element(){

    override fun toString(): String {
        return " $name=\"$value\""
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    override val observers: MutableList<(EventType, String, String?) -> Unit>
        get() = TODO("Not yet implemented")
}

class XML(var header: Prolog?= null, var root: Entity){

    fun getXML(): XML {
        return this
    }
}


@Throws(UnsupportedOperationException::class)
fun foo() {
    throw UnsupportedOperationException("An entity can only have text or children entities.Not both")
}

