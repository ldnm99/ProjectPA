import java.awt.*
import java.awt.event.MouseEvent
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.swing.*
import javax.swing.border.CompoundBorder

interface Command {
    fun run()
    fun undo()
}

class UndoStack {
    val stack = Stack<Command>()

    fun execute(c: Command) {
        c.run()
        stack.add(c)
    }

    fun undo() {
        if (stack.isNotEmpty())
            stack.pop().undo()
    }

    fun redo(){
        if (stack.isNotEmpty())
            execute(stack.peek())
    }
}

class SaveTree(var xml: XML): Command {
    override fun run() {
        val out =  PrintWriter(FileWriter("Test.xml"))
        out.write(serialization(xml))
        out.close()
    }

    override fun undo() {
        val path = Paths.get("Test.xml")
        try {
            val result = Files.deleteIfExists(path)
            if (result) {
                println("Deletion succeeded.")
            } else {
                println("Deletion failed.")
            }
        } catch (e: IOException) {
            println("Deletion failed.")
            e.printStackTrace()
        }
    }

}

class XMLEditor(var xml: XML): JFrame("XMLEditor") {

    val undoStack = UndoStack()
    var container = JPanel()
    var tree      = JPanel()

    var buttons = JMenuBar()
    var save    = JButton("Save to File")
    var undo    = JButton("Undo")
    var redo    = JButton("Redo")

    fun execute(c: Command) {
        undoStack.execute(c)
    }

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        size = Dimension(300, 300)

        container.layout = BoxLayout(container,BoxLayout.Y_AXIS)

        save.addActionListener {
            execute(SaveTree(xml))
        }
        undo.addActionListener {
            undoStack.undo()
        }
        redo.addActionListener {
            undoStack.redo()
        }

        buttons.add(save)
        buttons.add(undo)
        buttons.add(redo)
        container.add(buttons)

        tree = ComponentEnt(xml.tree)
        container.add(tree)
        container.add(JScrollPane(tree))
        add(container)
    }
    fun open() {
        isVisible = true
    }
}

class ComponentEnt(var e: Entity) : JPanel() {

    var attPanel = JPanel()

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.font = Font("Arial", Font.BOLD, 16)
        g.drawString(e.name, 10, 20)
    }

    init {
        layout = GridLayout(0, 1)
        border = CompoundBorder(
            BorderFactory.createEmptyBorder(30, 10, 10, 10),
            BorderFactory.createLineBorder(Color.BLACK, 2, true)
        )
        add(attPanel)
        mapEntity()
        createPopupMenu()
    }

    private fun mapEntity(){
        e.attribute.forEach {
            attPanel.add(ComponentAtt(it))
        }
        if(e.children.isEmpty()){
            add(JTextArea(e.value))
        }else{
            e.children.forEach {
                add(ComponentEnt(it as Entity))
            }
        }
    }

    private fun createPopupMenu() {

        val popupmenu = JPopupMenu("Actions")

        val a = JMenuItem("Rename Entity")
        a.addActionListener {
            val text = JOptionPane.showInputDialog("Rename to")
            this.e.name = text
            revalidate()
            repaint()
        }
        popupmenu.add(a)

        val b = JMenuItem("Add Attribute")
        b.addActionListener {
            val name = JOptionPane.showInputDialog("Attribute Name")
            val value = JOptionPane.showInputDialog("Attribute Text")

            val temp = Attribute(name, value, this.e)
            this.e.attribute.add(temp)
            add(ComponentAtt(temp))
            revalidate()
            repaint()
        }
        popupmenu.add(b)

        val c = JMenuItem("Add Entity")
        c.addActionListener {
            val text = JOptionPane.showInputDialog("Entity Name")
            add(ComponentEnt(Entity(text, e)))
            revalidate()
        }
        popupmenu.add(c)

        val d = JMenuItem("Delete Entity")
        d.addActionListener {
            e.parent?.children?.remove(e)
            val aux = parent
            aux.remove(this@ComponentEnt)
            aux.revalidate()
            aux.repaint()
        }
        popupmenu.add(d)

        addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e))
                    popupmenu.show(this@ComponentEnt, e.x, e.y)
            }
        })
    }
}

class ComponentAtt(var attribute: Attribute): JPanel(){

    var name = JLabel("")
    var value= JTextField("")

    init {
        layout = FlowLayout()
        name.text = attribute.name
        value.text = attribute.value
        value.addActionListener{
            attribute.value  = value.text
        }
        add(name)
        add(value)
        createPopupMenu()
    }

    private fun createPopupMenu() {

        val popupmenu = JPopupMenu("Actions")

        val delete = JMenuItem("Delete Attribute")
        delete.addActionListener {
            attribute.owner!!.attribute.remove(attribute)
            val aux = parent
            aux.remove(this)
            aux.revalidate()
            aux.repaint()
        }
        popupmenu.add(delete)

        addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e))
                    popupmenu.show(this@ComponentAtt, e.x, e.y)
            }
        })
    }

}

fun main() {
    val xmlheader = Prolog("UTF-8", "1.0")

    val root = Entity("Bookstore", null)
    root.attribute.add(Attribute("Owner", "Lourenço",root))
    root.attribute.add(Attribute("Category", "Good Books",root))

    val children1 = Entity("B1984", root)
    children1.setText("Random text")
    children1.attribute.add(Attribute("ID", "Book1",children1))

    val children2 = Entity("Odyssey", root)
    children2.attribute.add(Attribute("ID", "Book2",children2))

    val children3 = Entity("Chapter1", children2)
    children3.setText("Random long text")
    children3.attribute.add(Attribute("Name", "Intro",children3))

    val children4 = Entity("Chapter2", children2)
    children4.setText("Random long text")

    val xml = XML(xmlheader, root)

    val w = XMLEditor(xml)
    w.open()
}
