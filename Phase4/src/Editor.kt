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

interface TableEvent {
    fun pairModified() {}
    fun pairDeleted() {}
}

interface IObservable<O> {
    val observers: MutableList<O>

    fun addObserver(observer: O) {
        observers.add(observer)
    }

    fun removeObserver(observer: O) {
        observers.remove(observer)
    }

    fun notifyObservers(handler: (O) -> Unit) {
        observers.toList().forEach { handler(it) }
    }
}

class UndoStack {
    private val stack = Stack<Command>()

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

enum class EventType {
    ADDEnt, REMOVEEnt, RENAMEEnt, ADDAtt, REMOVEAtt, RENAMEAtt
}

class SaveTree(private var xml: XML): Command {
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

class AddEntity(private val parent: Entity, private val new: Entity): Command{
    override fun run() {
        parent.addChild(new)
    }

    override fun undo() {
        new.removeEnt(new)
    }
}

class RemoveEntity(private val parent: Entity?, private val new: Entity): Command {
    override fun run() {
        new.removeEnt(new)
    }

    override fun undo() {
        parent!!.addChild(new)
    }
}

class RenameEntity(private val entity: Entity, private val oldValue:String, private val newValue:String): Command {
    override fun run() {
        entity.rename(newValue,oldValue)
    }

    override fun undo() {
        entity.rename(oldValue,newValue)
    }
}

class AddAttribute(private val attribute: Attribute, private val entity: Entity): Command {
    override fun run() {
        entity.addAtt(attribute)
    }

    override fun undo() {
        attribute.removeAtt(attribute)
    }
}

class RemoveAttribute(private val attribute: Attribute, private val entity: Entity): Command {
    override fun run() {
        attribute.removeAtt(attribute)
    }

    override fun undo() {
        entity.addAtt(attribute)
    }
}

class RenameAttribute(private val attribute: Attribute, private val oldValue:String, private val newValue:String): Command {
    override fun run() {
        attribute.rename(attribute, oldValue, newValue)
    }

    override fun undo() {
        attribute.rename(attribute, newValue, oldValue)
    }
}

class XMLEditor(private var xml: XML): JFrame("XMLEditor") {

    private val undoStack = UndoStack()
    private var container = JPanel()
    private var tree = JPanel()

    private var buttons = JMenuBar()
    private var save = JButton("Save to File")
    private var undo = JButton("Undo")
    private var redo = JButton("Redo")

    fun execute(c: Command) {
        undoStack.execute(c)
    }

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        size = Dimension(300, 300)

        container.layout = BoxLayout(container, BoxLayout.Y_AXIS)

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

    inner class ComponentEnt(private var ent: Entity) : JPanel(), IObservable<TableEvent> {

        override val observers: MutableList<TableEvent> = mutableListOf()

        private var attPanel = JPanel()

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            g.font = Font("Arial", Font.BOLD, 16)
            g.drawString(ent.name, 10, 20)
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

        private fun mapEntity() {
            ent.attribute.forEach {
                attPanel.add(ComponentAtt(it))
            }
            if (ent.children.isEmpty()) {
                add(JTextArea(ent.value))
            } else {
                ent.children.forEach {
                    addComp(it as Entity)
                }
            }

            ent.addObserver { e, p, _ ->
                when (e) {
                    EventType.ADDEnt    -> addComp(p as Entity)
                    EventType.REMOVEEnt -> removeComp(p as Entity)
                    EventType.RENAMEEnt -> renameComp(p as Entity)
                    EventType.ADDAtt    -> addAttComp(p as Attribute)
                    else -> {}
                }
            }
        }

        private fun createPopupMenu() {

            val popupmenu = JPopupMenu("Actions")

            val a = JMenuItem("Rename Entity")
            a.addActionListener {
                val text = JOptionPane.showInputDialog("Rename to")
                execute(RenameEntity(ent,ent.name,text))
            }
            popupmenu.add(a)

            val b = JMenuItem("Add Attribute")
            b.addActionListener {
                val name = JOptionPane.showInputDialog("Attribute Name")
                val value = JOptionPane.showInputDialog("Attribute Text")
                val temp = Attribute(name, value, this.ent)
                execute(AddAttribute(temp,this.ent))
            }
            popupmenu.add(b)

            val c = JMenuItem("Add Entity")
            c.addActionListener {
                val text = JOptionPane.showInputDialog("Entity Name")
                val aux  =  Entity(text)
                execute(AddEntity(ent,aux))
            }
            popupmenu.add(c)

            val d = JMenuItem("Delete Entity")
            d.addActionListener {
                execute(RemoveEntity(ent.parent,ent))
            }
            popupmenu.add(d)

            addMouseListener(object : java.awt.event.MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isRightMouseButton(e))
                        popupmenu.show(this@ComponentEnt, e.x, e.y)
                }
            })
        }

        private fun addComp(e:Entity){
            add(ComponentEnt(e))
            revalidate()
            repaint()
        }

        private fun addAttComp(a:Attribute){
            add(ComponentAtt(a))
            revalidate()
            repaint()
        }

        private fun removeComp(e:Entity){
            if(this.ent == e){
                val parent = this.parent
                parent.remove(this@ComponentEnt)
                parent.revalidate()
                parent.repaint()
            }
        }

        private fun renameComp(e:Entity){
            this.ent.name = e.name
            revalidate()
            repaint()
        }

    }

    inner class ComponentAtt(private var attribute: Attribute) : JPanel(), IObservable<TableEvent> {

        private var name = JLabel("")
        private var value = JTextField("")

        override val observers: MutableList<TableEvent> = mutableListOf()

        init {
            layout = FlowLayout()
            name.text = attribute.name
            value.text = attribute.value
            value.addActionListener {
                execute(RenameAttribute(this.attribute,attribute.value,value.text))
            }
            add(name)
            add(value)

            attribute.addObserver { e, p, _ ->
                when (e) {
                    EventType.REMOVEAtt    -> removeComp(p as Attribute)
                    EventType.RENAMEAtt    -> renameComp(p as Attribute)
                    else -> {}
                }
            }

            createPopupMenu()
        }

        private fun createPopupMenu() {

            val popupmenu = JPopupMenu("Actions")

            val delete = JMenuItem("Delete Attribute")
            delete.addActionListener {
                execute(RemoveAttribute(attribute, attribute.owner!!))
            }
            popupmenu.add(delete)

            addMouseListener(object : java.awt.event.MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isRightMouseButton(e))
                        popupmenu.show(this@ComponentAtt, e.x, e.y)
                }
            })
        }

        private fun removeComp(a:Attribute){
            if(this.attribute == a){
                val parent = this.parent
                parent.remove(this@ComponentAtt)
                parent.revalidate()
                parent.repaint()
            }
        }

        private fun renameComp(a:Attribute){
            this.attribute.value = a.value
            revalidate()
            repaint()
        }
    }
}

fun main() {
    val xmlheader = Prolog("UTF-8", "1.0")

    val root = Entity("Bookstore", null)
    root.attribute.add(Attribute("Owner", "Lourenço",root))
    root.attribute.add(Attribute("Category", "Good Books",root))

    val children1 = Entity("1984", root)
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
