import kotlin.test.assertEquals

internal class OperationsKtTest {

    @org.junit.jupiter.api.Test
    fun serialization() {
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


        val text  = serialization(xml)
        val expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Bookstore Owner=\"Lourenco\"  Category=\"Good Books\">\n" +
                "\t<1984 ID=\"Book1\">\n" +
                "\t\t<C1 Name=\"Intro\">\n" +
                "\t\t\tRandom long text\n" +
                "\t\t<C1/>\n" +
                "\t\t<C2 Name=\"Intro\">\n" +
                "\t\t\tRandom long text\n" +
                "\t\t<C2/>\n" +
                "\t<1984/>\n" +
                "\t<Odyssey ID=\"Book2\">\n" +
                "\t\t<Chapter1 Name=\"Intro\">\n" +
                "\t\t\tRandom long text\n" +
                "\t\t<Chapter1/>\n" +
                "\t\t<Chapter2>\n" +
                "\t\t\tRandom long text\n" +
                "\t\t<Chapter2/>\n" +
                "\t<Odyssey/>\n" +
                "<Bookstore/>"
        assertEquals(expected, text)
    }

    @org.junit.jupiter.api.Test
    fun find() {
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
        val entitysearched = find(xml, entityName("Chapter1"))

        if (entitysearched != null) {
            assertEquals("Chapter1",entitysearched.name)
        }
    }

    @org.junit.jupiter.api.Test
    fun filter() {
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

        val children5 = Entity("C1", children1)
        children5.setText("Random long text")
        children5.attribute.add(Attribute("Name", "Intro"))

        val children6 = Entity("C2", children1)
        children6.setText("Random long text")
        children6.attribute.add(Attribute("Name", "Intro"))

        val xml = XML(xmlHeader, xmlTree)
        val xmlFiltered = filterEntity(xml, vAttribute("Book1"))
        val text = serialization(xmlFiltered)
        val expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Bookstore>\n" +
                "\t<1984 ID=\"Book1\">\n" +
                "\t\t<C1 Name=\"Intro\">\n" +
                "\t\t\tRandom long text\n" +
                "\t\t<C1/>\n" +
                "\t\t<C2 Name=\"Intro\">\n" +
                "\t\t\tRandom long text\n" +
                "\t\t<C2/>\n" +
                "\t<1984/>\n" +
                "<Bookstore/>"
        assertEquals(expected, text)
    }

    @org.junit.jupiter.api.Test
    fun createXML(){
        val c1 = Chapter(1, "Texto do capitulo 1")
        val c2 = Chapter(1, "Texto do capitulo 2")
        val mobyDick = Book(1, "Moby Dick", false, Categories.Fiction, listOf(c1, c2))

        val xml = createXML(mobyDick, "1.0", "UTF-8")
        val text = serialization(xml)

        val expected = "<?xml version=\"UTF-8\" encoding=\"1.0\"?>\n" +
                "<Book ID=\"1\"  Book Name=\"Moby Dick\"  read=\"false\"  category=\"Fiction\">\n" +
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
                "<Book/>"
        assertEquals(expected, text)
    }
}