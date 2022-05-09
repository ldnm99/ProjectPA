import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor

@Target(AnnotationTarget.PROPERTY)
annotation class XmlIgnore

@Target(AnnotationTarget.PROPERTY)
annotation class XmlName(val value: String)

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
    @XmlIgnore
    val id:  Int?,
    @XmlTagContent
    val chapters: String
)

enum class Categories {Fiction, Comedy, Romance}
var c1: Chapter = Chapter(1, "Texto do capitulo 1")
var c2: Chapter = Chapter(1, "Texto do capitulo 2")
var mobyDick: Book= Book(1,"Moby Dick", false, Categories.Fiction, listOf(c1,c2) )

private fun getClassName(c: KClass<*>)=
    if(c.hasAnnotation<XmlName>())
        c.findAnnotation<XmlName>()!!.value
    else
        c.simpleName


private fun getPropName(p: KProperty<*>)=
    if(p.hasAnnotation<XmlName>())
        p.findAnnotation<XmlName>()!!.value
    else
        p.name

private fun proper(c:KClass<*>): List<KProperty1<*,*>>{
    require(c.isData)
    val construcParam = c.primaryConstructor!!.parameters
    return c.declaredMemberProperties.sortedWith{
            x,y -> construcParam.indexOfFirst {it.name == x.name}
            -
            construcParam.indexOfFirst {it.name == y.name}
    }
}

private fun createParent(c:Any, parent: Entity?= null): Entity{
    var root: Entity
    var name = getClassName(c::class)
    root = if(parent == null) {
        Entity(name = name!!)
    }else{
        Entity(name, parent)
    }

    return root
}
/*
private fun createChildren(c:Any, parent: Entity): Entity{
    var proper = proper(c::class)

}*/


















