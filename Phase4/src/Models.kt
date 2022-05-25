interface Visitor {
    fun visit(p: Prolog) {}
    fun visit(a: Attribute){}
    fun visit(e: Entity)  : Boolean = true
    fun endvisit(e: Entity){}
    fun endvisitatt(a: Array<Attribute>){}
}

abstract class Element( val parent: Entity? = null) : IObservable<(EventType,Element , Element?)-> Unit>{
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

    override val observers: MutableList<(EventType, Element, Element?) -> Unit> = mutableListOf()

}

class Entity(var name: String, parent: Entity? = null) : Element(parent){
    var value  :String ?= null
    var children   = mutableListOf<Element>()
    var attribute  = mutableListOf<Attribute>()

    fun parseAttribute(): String{
        return attribute.joinToString(" ")
    }

    fun setText(text: String){
        if (children.isEmpty()) {
            value = text
            specialChar()
        }else{
            foo()
        }
    }

    fun addChild(e: Entity){
        if(children.add(e)){
            notifyObservers {
                it(EventType.ADDEnt,e,null)
            }
        }
    }

    fun removeChild(e: Entity){
        e.parent?.children?.remove(e)
        notifyObservers {
            it(EventType.REMOVEEnt, e, null)
        }
    }
/*
    fun rename(newName:String, oldName:String){
        if(oldName != newName){
            this.name=newName
            notifyObservers {
                it(EventType.RENAMEEnt, newName,oldName)
            }
        }
    }

    fun addAtt(a: Attribute){
        if(attribute.add(a)){
            notifyObservers {
                it(EventType.ADDAtt,"a",null)
            }
        }
    }

    fun removeAtt(a: Attribute){
        run breaker@{
            attribute.forEach {
                it as Attribute
                if (it == a) return@breaker
                attribute.remove(it)
            }
        }
        notifyObservers {
            it(EventType.REMOVEAtt, a.name, null)
        }
    }
*/
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

    override val observers: MutableList<(EventType, Element, Element?) -> Unit> = mutableListOf()

}

class Attribute(var name: String, var value: String, parent: Entity? = null) : Element(){

    override fun toString(): String {
        return " $name=\"$value\""
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
/*
    fun replaceValue(oldValue:String, newValue:String ){
        if(oldValue != newValue){
            this.value=newValue
            notifyObservers {
                it(EventType.RENAMEAtt, newValue,oldValue)
            }
        }
    }
*/
    override val observers: MutableList<(EventType, Element, Element?) -> Unit> = mutableListOf()
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

