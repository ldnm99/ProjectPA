fun main(){

    val mode  =2

    val xmlHeader = Prolog("UTF-8", "1.0")

    val xmlTree = Entity("Bookstore", null)
    xmlTree.attribute.add(Attribute("Owner", "Lourenco"))
    xmlTree.attribute.add(Attribute("Category", "Good Books"))

    val children1 = Entity("1984", xmlTree)
    //children1.setText("Random text")
    children1.attribute.add(Attribute("ID", "Book1"))

    val children2 = Entity("Odyssey", xmlTree)
    children2.attribute.add(Attribute("ID", "Book2"))

    val children3 = Entity("Chapter1", children2)
    children3.setText("Random long text")
    children3.attribute.add(Attribute("Name", "Intro"))

    val children4 = Entity("Chapter2", children2)
    children4.setText("Random long text")

    val children5 = Entity("C1",children1)
    children5.setText("Random long text")
    children5.attribute.add(Attribute("Name","Intro"))

    val children6 = Entity("C2",children1)
    children6.setText("Random long text")
    children6.attribute.add(Attribute("Name","Intro"))

    val xml = XML(xmlHeader,xmlTree)


    if (mode == 0){
        val text : String = serialization(xml)
        println(text)
    }else if (mode == 1){
        val entitysearched = find(xml, entityName("Chapter1"))
        if (entitysearched != null) {
            println("Search result is the entity named: " + entitysearched.name)
        }
    }else if (mode == 2) {
        val xmlFiltered = filterEntity(xml, vAttribute("Book1"))
        println(serialization(xmlFiltered))

    }else if (mode == 3) {
        val c1 = Chapter(1, "Texto do capitulo 1")
        val c2 = Chapter(1, "Texto do capitulo 2")
        val mobyDick = Book(1, "Moby Dick", false, Categories.Fiction, listOf(c1, c2))

        val xmlNew = createXML(mobyDick, "1.0", "UTF-8")

        val text = serialization(xmlNew)
        println(text)
    }
}

fun serialization(xml: XML): String{
    var result =""
    if (xml.header!=null){
        result = serializationProlog(xml.header!!,result)
    }
    result = serializationTree(xml.tree,result)
    return result
}

fun serializationProlog(p: Element, aux : String) : String {
    val text = object : Visitor {
        var adder = aux
        var leafdepth = 0
        override fun visit(p: Prolog) {
            adder = "<?xml version=" + "\"" + p.version + "\"" + " encoding=" + "\"" + p.encoding + "\"" + "?>"
            leafdepth = 0
        }
    }
    p.accept(text)
    return text.adder
}

fun serializationTree(element: Element, header: String) : String {

    fun tab(depth: Int): String{
        var tabs =""
        for (i in 0  until depth){
            tabs += "\t"}
        return tabs
    }

    fun addText(entity: Entity, text: String ): String {
        var adder = text
        adder += if(entity.attribute.isNotEmpty())
            tab(entity.depth) + "<" + entity.name + entity.parseAttribute() + ">"+ "\n"
        else
            tab(entity.depth) + "<" + entity.name + ">"+ "\n"

        if(entity.value != null) {
            adder += tab(entity.depth + 1) + entity.value + "\n"
        }else{
            for (c in entity.children)
                serializationTree(c, adder)
        }
        return adder
    }

    val text = object : Visitor {
        var adder = header + "\n"
        override fun visit(e: Entity): Boolean {
            adder = addText(e,adder)
            return true
        }
        override fun endvisit(e: Entity) {
            adder += if(e.depth == 0){
                tab(e.depth) + "<" + e.name + "/>"
            }else{
                tab(e.depth) + "<" + e.name + "/>"+ "\n"
            }

        }
    }
    element.accept(text)
    return text.adder
}

fun find(xml: XML, accept: (Entity) -> Boolean): Entity? {
    val root = xml.tree
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

fun filterEntity(xml: XML, accept: (Entity) -> Boolean): XML {
    val root = xml.tree
    val en = object : Visitor {
        var newTree = Entity(root.name)
        override fun visit(e: Entity): Boolean {
            if (accept(e)) {
                if (!newTree.children.contains(e) && root != e) {
                    newTree.children.add(e)
                }
            }
            return true
        }
    }
    root.accept(en)
    return XML(xml.header, en.newTree)
}

//children number
fun nChild(n: Int)          = {e: Entity -> e.children.size == n}
//entity name
fun entityName(s: String)   = {e: Entity -> e.name == s}
//attribute number
fun nAttribute(n: Int)      = {e: Entity -> e.attribute.size == n}
//attribute name
fun sAttribute(s: String)      = {e: Entity -> e.getAttributeName(s) == s}
//attribute value
fun vAttribute(s: String)      = {e: Entity -> e.getAttributeValue(s) == s}