fun main(){

    val xmlheader = Prolog("UTF-8","1.0")
    val header: String = serializationheader(xmlheader)

    val xmlobject = Entity(null)
    xmlobject.name = "Parent"
    //xmlobject.value = "Random long text"
    xmlobject.attributes.add("ID")
    xmlobject.attributes.add("ID2")

    val xmlobject2 = Entity(xmlobject)
    xmlobject2.name = "Children1"
    xmlobject2.value = "Random long text2"
    xmlobject2.attributes.add("ID3")
    xmlobject2.attributes.add("ID4")

    val xmlobject3 = Entity(xmlobject)
    xmlobject3.name = "Children2"
    xmlobject3.value = "Random long text3"
    xmlobject3.attributes.add("ID5")
    xmlobject3.attributes.add("ID6")
/*
    val xmlobject4 = Entity(null)
    xmlobject4.name = "Parent2"
    xmlobject4.value = "Random long text3"
    xmlobject4.attributes.add("ID7")
    xmlobject4.attributes.add("ID8")
*/

    var file : String = serialization(xmlobject, header)
   // var file2 : String = serialization(xmlobject4,file)
    println(file)
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

    val text = object : Visitor {
        var adder = header + "\n"
        override fun visit(entity: Entity): Boolean {
            if (entity.parent == null) {
                if(entity.attributes !=null)
                    adder += "<" + entity.name + " att=" + "\"" + entity.attributes.joinToString(separator = ",") + "\"" + ">"
                else
                    adder += "<" + entity.name + ">"
                if(entity.value != null) {
                    adder += "\n" + tab(entity.depth + 1) + entity.value + "\n"
                }else{
                    for (c in entity.children)
                        serialization(c,adder)
                }
            }else{
                if(entity.attributes !=null)
                    adder += "\n" + tab(entity.depth) + "<" + entity.name + " att=" + "\"" + entity.attributes.joinToString(separator = ",") + "\"" + ">"
                else
                    adder += "\n" + tab(entity.depth) + "<" + entity.name + ">"
                if(entity.value != null) {
                    adder += "\n" + tab(entity.depth + 1) + entity.value + "\n"
                }else{
                    for (c in entity.children)
                        serialization(c,adder)
                }
            }
            return true
        }
        override fun endvisit(entity: Entity) {
            adder += tab(entity.depth) + "<"+ entity.name + "/>"
        }
    }
    element.accept(text)
    return text.adder
}

