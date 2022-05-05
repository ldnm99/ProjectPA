import org.junit.jupiter.api.Assertions.*
import kotlin.test.assertEquals

internal class OperationsKtTest {

    @org.junit.jupiter.api.Test
    fun serializationheader() {
        val xmlheader = Prolog("UTF-8","1.0")
        val header: String = serializationheader(xmlheader)
        val expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        assertEquals(expected,header)
    }

    @org.junit.jupiter.api.Test
    fun serialization() {
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


        var text : String = serialization(xmlobject, "")
        val expected = "\n"+ "<Bookstore Owner=\"Lourenco\", Category=\"Good Books\">\n" +
                "\t<1984 ID=\"Book1\">\n" +
                "\t\tRandom text\n" +
                "\t<1984/>\n" +
                "\t<Odyssey ID=\"Book2\">\n" +
                "\t\t<Chapter1 Name=\"Intro\">\n" +
                "\t\t\tRandom long text\n" +
                "\t\t<Chapter1/>\n" +
                "\t\t<Chapter2>\n" +
                "\t\t\tRandom long text\n" +
                "\t\t<Chapter2/>\n" +
                "\t<Odyssey/>\n" +
                "<Bookstore/>" +"\n"
        assertEquals(expected, text)
    }
/*
    @org.junit.jupiter.api.Test
    fun findEntity() {
        val xmlheader = Prolog("UTF-8","1.0")
        val header: String = serializationheader(xmlheader)

        val xmlobject = Entity(null)
        xmlobject.name = "Parent"
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

        var entitysearched = findEntity(xmlobject,"Children1")

        assertEquals("Children1",entitysearched.name)
    }*/
}