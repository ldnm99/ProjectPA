import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.*

@Target(AnnotationTarget.PROPERTY)
annotation class XmlIgnore

@Target(AnnotationTarget.PROPERTY)
annotation class XmlName(val name: String)

@Target(AnnotationTarget.PROPERTY)
annotation class XmlTagContent

data class Book(
    @XmlName("ID")
    val id:  Int?,
    @XmlName("Book Name")
    val name: String?,
    val read: Boolean?,
    val category: Enum<Categories>?,
    @XmlTagContent
    val chapters: List<Chapter>?,
)

data class Chapter(
    @XmlName("ID")
    val id:  Int?,
    @XmlTagContent
    val Text: String
)

enum class Categories {Fiction, Comedy, Romance}

private fun KClassifier?.isEnum() = this is KClass<*> && this.isSubclassOf(Enum::class)

fun createXML(a:Any, encoding: String?, version: String?): XML {
    var root: Entity = createTree(a)
    var header: Prolog?=null
    if (encoding != null && version != null) {
        header = Prolog(encoding, version)
    }
    return XML(header, root)
}

private fun getClassName(c: KClass<*>)=
    if(c.hasAnnotation<XmlName>()) c.findAnnotation<XmlName>()!!.name
    else c.simpleName

private fun getPropName(p: KProperty<*>)=
    if(p.hasAnnotation<XmlName>()) p.findAnnotation<XmlName>()!!.name
    else p.name

private fun proper(c:KClass<*>): List<KProperty1<*,*>>{
    require(c.isData)
    val constructParam = c.primaryConstructor!!.parameters
    return c.declaredMemberProperties.sortedWith{
            x,y -> constructParam.indexOfFirst {it.name == x.name} - constructParam.indexOfFirst {it.name == y.name}
    }
}

private fun mapObject(a: Any?):String{
    return if (a == null) "Null"
    else if (a is Int) a.toString()
    else if (a is Boolean) a.toString()
    else if (a  is Collection<*>) a.joinToString(",")
    else if (a is String || a::class.isEnum()) a.toString()
    else "Not Defined"
}

private fun createTree(c:Any, parent: Entity?= null): Entity {
    fun aux(c:Any, parent: Entity){
        var proper = proper(c::class)
        proper.forEach {
            if (!it.hasAnnotation<XmlIgnore>()) {
                var a: Any? = it.call(c)
                if (it.hasAnnotation<XmlTagContent>()) {
                    createElement(parent, it, a, 0)
                } else
                    createElement(parent, it, a, 1)
            }
        }
    }
    var root: Entity
    var name = getClassName(c::class)
    root = if(parent == null) {
        Entity(name = name!!)
    }
    else{
        Entity(name = name!!, parent)
    }
    aux(c,root)
    return root
}

//identifier 0 if entity
//identifier 1 if attribute
private fun createElement(parent: Entity, p: KProperty<*>, a: Any?, identifier: Int){
    if(a !is Collection<*> ) {
        if(identifier == 0){
            var e = Entity(name = getPropName(p), parent = parent)
            e.value = mapObject(a)
        }else if(identifier == 1){
            if(a !is Collection<*> || !a.first()!!::class.isData){
                var att = Attribute(name = getPropName(p), value = mapObject(a), parent = parent)
                parent.attribute.add(att)
            }
        }
    }else{
        var c = Entity(getPropName(p), parent = parent)
        a.forEach {
                temp ->
            if (temp!!::class.isData) {
                createTree(temp, c)
            }else {
                c.value = mapObject(a)
            }
        }
    }
}

















