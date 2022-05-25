import kotlin.test.assertEquals
/*
internal class OperationsKtTest {

    @org.junit.jupiter.api.Test
    fun serializationheader() {
        val xmlheader = Prolog("UTF-8", "1.0")
        val header: String = serializationheader(xmlheader)
        val expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        assertEquals(expected,header)
    }

    @org.junit.jupiter.api.Test
    fun serialization() {
        val xmlobject = Entity("Bookstore", null)
        xmlobject.attribute.add(Attribute("Owner", "Lourenco"))
        xmlobject.attribute.add(Attribute("Category", "Good Books"))

        val children1 = Entity("1984", xmlobject)
        children1.value = "Random text"
        children1.attribute.add(Attribute("ID", "Book1"))

        val children2 = Entity("Odyssey", xmlobject)
        children2.attribute.add(Attribute("ID", "Book2"))

        val children3 = Entity("Chapter1", children2)
        children3.value = "Random long text"
        children3.attribute.add(Attribute("Name", "Intro"))

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

    @org.junit.jupiter.api.Test
    fun find() {
        val xmlheader = Prolog("UTF-8", "1.0")
        val header: String = serializationheader(xmlheader)

        val xmlobject = Entity("Bookstore", null)
        xmlobject.attribute.add(Attribute("Owner", "Lourenco"))
        xmlobject.attribute.add(Attribute("Category", "Good Books"))

        val children1 = Entity("1984", xmlobject)
        children1.value = "Random text"
        children1.attribute.add(Attribute("ID", "Book1"))

        val children2 = Entity("Odyssey", xmlobject)
        children2.attribute.add(Attribute("ID", "Book2"))

        val children3 = Entity("Chapter1", children2)
        children3.value = "Random long text"
        children3.attribute.add(Attribute("Name", "Intro"))

        val children4 = Entity("Chapter2", children2)
        children4.value = "Random long text"

        var entitysearched = find(xmlobject, entityName("Chapter1"))

        if (entitysearched != null) {
            assertEquals("Chapter1",entitysearched.name)
        }
    }

    @org.junit.jupiter.api.Test
    fun createXML(){
        val xmlheader = Prolog("UTF-8", "1.0")

        var c1: Chapter = Chapter(1, "Texto do capitulo 1")
        var c2: Chapter = Chapter(1, "Texto do capitulo 2")
        var mobyDick: Book = Book(1, "Moby Dick", false, Categories.Fiction, listOf(c1, c2))

        var xml = createXML(mobyDick, "1.0", "UTF-8")
        var text = serialization(xml.root, serializationheader(xmlheader))

        val expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Book ID=\"1\", Book Name=\"Moby Dick\", read=\"false\", category=\"Fiction\">\n" +
                "\t<chapters>\n" +
                "\t\t<Chapter ID=\"1\">\n" +
                "\t\t\t<Text>\n" +
                "\t\t\t\tTexto do capitulo 1\n" +
                "\t\t\t<Text/>\n" +
                "\t\t<Chapter/>\n" +
                "\t\t<Chapter ID=\"1\">\n" +
                "\t\t\t<Text>\n" +
                "\t\t\t\tTexto do capitulo 2\n" +
                "\t\t\t<Text/>\n" +
                "\t\t<Chapter/>\n" +
                "\t<chapters/>\n" +
                "<Book/>"+"\n"
        assertEquals(expected, text)
    }
}*/