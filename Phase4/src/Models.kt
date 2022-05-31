interface Visitor {
    fun visit(p: Prolog) {}
    fun visit(a: Attribute){}
    fun visit(e: Entity)  : Boolean = true
    fun endvisit(e: Entity){}
    fun endvisitatt(a: Array<Attribute>){}
}

abstract class Element(var parent: Entity? = null) {
    init { parent?.children?.add(this) }
    val depth: Int
        get() =
            if (parent == null) 0
            else 1 + parent!!.depth

    abstract fun accept(visitor: Visitor)
}

class Prolog(var encoding: String, var version: String) : Element(){
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

class Entity(var name: String, parent: Entity? = null) : Element(parent), IObservable<(EventType, Element, Element?) -> Unit>{
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
            throw UnsupportedOperationException("An entity can only have text or children entities.Not both")
        }
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
            if (it.name == value)
                att = it
        }
        return att
    }

    //returns string with specific name
    fun getAttributeName(value: String): String? {
        var att: String? = null
        attribute.forEach{
            if (it.name == value)
                att = it.name
        }
        return att
    }

    //returns attribute with specific name
    fun getAttributeValue(value: String): String? {
        var att: String? = null
        attribute.forEach{
            if (it.value == value)
                att = it.value
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

    fun addChild(e: Entity){
        e.parent = this
        if(children.add(e)){
            notifyObservers {
                it(EventType.ADDEnt,e,null)
            }
        }
    }

    fun removeChild(e: Entity){
        children.remove(e)
        notifyObservers {
            it(EventType.REMOVEEnt, this, null)
        }
    }

    fun removeEnt(e: Entity){
        e.parent?.children?.remove(e)
        notifyObservers {
            it(EventType.REMOVEEnt, e, null)
        }
    }

    fun rename(newName:String, oldName:String){
        if(oldName != newName){
            this.name = newName
            notifyObservers {
                it(EventType.RENAMEEnt, this, null )
            }
        }
    }

    fun addAtt(a: Attribute){
        if(attribute.add(a)){
            notifyObservers {
                it(EventType.ADDAtt,a,null)
            }
        }
    }


}

class Attribute(var name: String, var value: String, var owner: Entity? = null) : Element(), IObservable<(EventType, Element, Element?) -> Unit>{

    fun removeAtt(a: Attribute){
        a.owner?.attribute?.remove(a)
        notifyObservers {
            it(EventType.REMOVEAtt, a, null)
        }
    }

    fun rename(a: Attribute,oldName: String, newName: String){
        if(oldName != newName){
            a.value = newName
            notifyObservers {
                it(EventType.RENAMEAtt, a, null)
            }
        }
    }

    override val observers: MutableList<(EventType, Element, Element?) -> Unit> = mutableListOf()

    override fun toString(): String {
        return " $name=\"$value\""
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

class XML(var header: Prolog?= null, var tree: Entity){

    fun getXML(): XML {
        return this
    }
}


