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
        var text : String = serialization(xmlobject, "")
        val expected = "\n"+ "<Parent att=\"ID,ID2\">\n" +
                "\t<Children1 att=\"ID3,ID4\">\n" +
                "\t\tRandom long text2\n" +
                "\t<Children1/>\n" +
                "\t<Children2 att=\"ID5,ID6\">\n" +
                "\t\tRandom long text3\n" +
                "\t<Children2/>\n" +
                "<Parent/>"
        assertEquals(expected, text)
    }

    @org.junit.jupiter.api.Test
    fun findEntity() {
    }
}