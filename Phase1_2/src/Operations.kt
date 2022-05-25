fun main(){

    val mode  = 0

    val xmlheader = Prolog("UTF-8", "1.0")
    val header: String = serializationheader(xmlheader)

    val xmlobject = Entity("Bookstore", null)
    xmlobject.attribute.add(Attribute("Owner", "Lourenco"))
    xmlobject.attribute.add(Attribute("Category", "Good Books"))

    val children1 = Entity("B1984", xmlobject)
    children1.setText("Random text")
    children1.attribute.add(Attribute("ID", "Book1"))

    val children2 = Entity("Odyssey", xmlobject)
    children2.attribute.add(Attribute("ID", "Book2"))

    val children3 = Entity("Chapter1", children2)
    children3.setText("Random long text")
    children3.attribute.add(Attribute("Name", "Intro"))

    val children4 = Entity("Chapter2", children2)
    children4.setText("Random long text")
/*
    val children5 = Entity("Chapter1",children1)
    children5.setText("Random long text")
    children5.attribute.add(Attribute("Name","Intro"))

    val children6 = Entity("Chapter1",children1)
    children6.setText("Random long text")
    children6.attribute.add(Attribute("Name","Intro"))
*/


    if (mode == 0){
        var text : String = serialization(xmlobject, header)
        println(text)
    }else if (mode == 1){
        var entitysearched = find(xmlobject, entityName("Chapter1"))
        if (entitysearched != null) {
            println("Search result is the entity named: " + entitysearched.name)
        }
    }else if (mode == 2) {
        var entitysearched = filterEntity(xmlobject, nChild(2))
        if (entitysearched != null) {
            println(serialization(entitysearched, header))
        }
    }else if (mode == 3) {
        var c1: Chapter = Chapter(1, "Texto do capitulo 1")
        var c2: Chapter = Chapter(1, "Texto do capitulo 2")
        var mobyDick: Book = Book(1, "Moby Dick", false, Categories.Fiction, listOf(c1, c2))

        var xml = createXML(mobyDick, "1.0", "UTF-8")

        var text = serialization(xml.root, serializationheader(xmlheader))
        println(text)
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

//TODO REMOVE NEWLINE AT END OF FILE
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
                serialization(c, adder)
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
fun nChild(n: Int)      = {e: Entity -> e.children.size == n}
//entity name
fun entityName(s: String)   = {e: Entity -> e.name == s}
//attribute number
fun nAttribute(n: Int)      = {e: Entity -> e.attribute.size == n}

fun find(root: Entity, accept: (Entity) -> Boolean): Entity? {
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




//TODO not remove from existing file but create new one
fun filterEntity(root: Element, accept: (Entity) -> Boolean): Entity {
    val en = object : Visitor {
        var result = root as Entity
        override fun visit(e: Entity): Boolean {
            if (accept(e)) {
                result.children.remove(e)
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
    return en.result
}



