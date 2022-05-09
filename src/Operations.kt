import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

fun main(){

    val xmlheader = Prolog("UTF-8","1.0")
    val header: String = serializationheader(xmlheader)

    val xmlobject = Entity("Bookstore",null)
    xmlobject.attribute.add( Attribute("Owner","Lourenco"))
    xmlobject.attribute.add( Attribute("Category","Good Books"))

    val children1 = Entity("1984",xmlobject)
    children1.value = "Random text"
    children1.attribute.add(Attribute("ID","Book1"))

    val children2 = Entity("Odyssey", xmlobject)
    children2.attribute.add(Attribute("ID","Book2"))

    val children3 = Entity("Chapter1",children2)
    children3.value = "Random long text"
    children3.attribute.add(Attribute("Name","Intro"))

    val children4 = Entity("Chapter2", children2)
    children4.value = "Random long text"

    var text : String = serialization(xmlobject, header)
    println(text)


    var entitysearched = find(xmlobject,test2("Chapter1"))
    if (entitysearched != null) {
        println("Search result is the entity named: " + entitysearched.name)
    }

}

fun serializationheader(p: Element) : String {
    val text = object : Visitor {
        var adder = ""
        var leafdepth = 0
        override fun visit(p: Prolog) {
            adder = "<?xml version=" + "\"" + p.version + "\"" + " encoding=" + "\"" + p.encoding + "\"" + "?>"
            leafdepth = 0
        }
    }
    p.accept(text)
    return text.adder
}

fun serialization(element: Element, header: String) : String {

    fun tab(depth: Int): String{
        var tabs =""
        for (i in 0  until depth){
            tabs += "\t"}
        return tabs
    }

    fun addText(entity: Entity, text: String ): String {
        var adder = text
        if(entity.attribute.isNotEmpty())
            adder += tab(entity.depth) + "<" + entity.name + entity.parseAttribute() + ">"+ "\n"
        else
            adder += tab(entity.depth) + "<" + entity.name + ">"+ "\n"
        if(entity.value != null) {
            adder += tab(entity.depth + 1) + entity.value + "\n"
        }else{
            for (c in entity.children)
                serialization(c,adder)
        }
        return adder
    }

    val text = object : Visitor {
        var adder = header + "\n"
        override fun visit(entity: Entity): Boolean {
            adder = addText(entity,adder)
            return true
        }
        override fun endvisit(entity: Entity) {
            adder += tab(entity.depth) + "<" + entity.name + "/>"+ "\n"
        }
    }
    element.accept(text)
    return text.adder
}

//children number
fun test1(n: Int)      = {e:Entity -> e.children.size == n}
//entity name
fun test2(s: String)   = {e:Entity -> e.name == s}
//attribute number
fun test3(n: Int)      = {e:Entity -> e.attribute.size == n}

fun find(root:Entity, accept: (Entity) -> Boolean): Entity? {
    val en = object : Visitor {
        var result: Entity? = null
        override fun visit(e: Entity): Boolean {
            if (accept(e)) {
                result = e
            }
            e.children.forEach {
                if (accept(it as Entity)) {
                    result = it
                }
            }
            return true
        }
    }
    root.accept(en)
    return if (en.result == null ){
        println("No entity found!")
        null
    }else
        en.result
}


fun getXML(a: Any){

    return if(a is MutableList<*>){
        val children = mutableListOf<Element>()
        a.forEach { it ->
            children.add((getXML(it!!) as Element))
        }


    }else{
        if (a::class.isData){
            val clazz: KClass<Any> = a::class as KClass<Any>
            val xml = Entity("")
            clazz.declaredMemberProperties.forEach {
                if (it.hasAnnotation<XmlTagContent>()){
                    xml.value = it.findAnnotation<XmlTagContent>()?.toString()
                }else{
                    if(it.hasAnnotation<XmlName>()){
                        xml.name =  it.name
                    }
                }
            }



        }else{
            throw IllegalArgumentException("Not Supported")
        }
    }
}





/*
//filter according to the attribute with the same name???
fun filterEntity(entity: Element, name: String): Entity {
    val en = object : Visitor {
        var result = Entity()
        var o = entity
        override fun visit(e: Entity): Boolean {
            if (e.attribute!!.name.equals(name))
                result = e

            if(e.children.isNotEmpty() && e.name!=name){
                for (c in e.children)
                    filterEntity(c,name)
            }
            return true
        }
    }
    entity.accept(en)
    return en.result
}*/




