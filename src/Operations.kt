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

/*
    var entityfiltered= filterEntity(xmlobject, "Attribute2")
    println(serialization(entityfiltered,header))

    var entitysearched = findEntity(xmlobject,"Children1")
    println(entitysearched.name)
**/
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

fun findEntity(entity: Element, name: String): Entity? {
    val en = object : Visitor {
        var result = Entity("null", null)
        var o = entity
        override fun visit(e: Entity): Boolean {
            if (e.name == name) {
                result = e
            }
            if (e.children.isNotEmpty() && e.name != name) {
                for (c in e.children)
                    findEntity(c, name)
            }
            return true
        }
    }
    entity.accept(en)
    return if (en.result.name.equals("null")){
        println("No entity found!")
        null
    }else
        en.result
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




