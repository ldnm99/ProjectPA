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

interface Command {
    fun run()
    fun undo()
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

enum class EventType {
    ADDEnt, REMOVEEnt, RENAMEEnt, ADDAtt, REMOVEAtt, RENAMEAtt
}

interface EditorEvent {
    fun entModified(old: Entity, new: Entity) {}
    fun entDeleted(entity: Entity) {}
}

class AddEntity(val parent: Entity, val new: Entity): Command {

    override fun run() {
        parent.addChild(new)
    }

    override fun undo() {
        parent.removeChild(new)
    }
}

class RemoveEntity(val parent: Entity, val new: Entity): Command {
    override fun run() {
        parent.removeChild(new)
    }

    override fun undo() {
        parent.addChild(new)
    }
}

/*
class RenameEntity(val entity: Entity, val oldValue:String, val newValue:String): Command {
    override fun run() {
        entity.rename(newValue,oldValue)
    }

    override fun undo() {
        entity.rename(oldValue,newValue)
    }
}
class AddAttribute(val attribute: Attribute, val entity: Entity): Command {
    override fun run() {
        entity.addAtt(attribute)
    }

    override fun undo() {
        entity.removeAtt(attribute)
    }
}

class RemoveAttribute(val attribute: Attribute, val entity: Entity): Command {
    override fun run() {
        entity.removeAtt(attribute)    }

    override fun undo() {
        entity.addAtt(attribute)
    }
}

class EditAttribute(val attribute: Attribute, val oldValue:String, val newValue:String): Command {
    override fun run() {
        attribute.replaceValue(oldValue, newValue)
    }

    override fun undo() {
        attribute.replaceValue(newValue, oldValue)
    }
}
*/

//Save to file and undo work
class SaveTree(val root: Entity,var header: Prolog?= null): Command {
    override fun run() {
        val out =  PrintWriter(FileWriter("Test.xml"))
        if(header != null){
            out.write(serialization(root, serializationheader(header!!)))
        }else
            out.write(serialization(root, ""))
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

class GUI(var xml: XML): JFrame("XMLEditor"){

    val undoStack = UndoStack()
    var buttons   = JMenuBar()
    var save      = JButton("Save to File")
    var undo      = JButton("Undo")
    var redo    = JButton("Redo")
    var tree      = JPanel()
    var container = JPanel()

    fun execute(c: Command) {
        undoStack.execute(c)
    }

    fun open() {
        isVisible = true
    }

    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        size = Dimension(300, 300)

        container.layout = BoxLayout(container,BoxLayout.Y_AXIS)

        save.addActionListener {
            execute(SaveTree(xml.root,xml.header))
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

        tree = ComponentEnt(xml.root)
        container.add(tree)
        container.add(JScrollPane(tree))
        add(container)
    }

    inner class ComponentEnt(var e: Entity) : JPanel(), IObservable<EditorEvent> {

        var attPanel = JPanel()

        override val observers: MutableList<EditorEvent>  = mutableListOf()

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
            e.addObserver{event, p, aux ->
                when (event) {
                    EventType.ADDEnt    -> addEntityGUI(p)
                    EventType.REMOVEEnt -> removeEntityGUI()
                    EventType.RENAMEEnt -> replaceEntityGUI(aux!!, p)
                    EventType.ADDAtt    -> addAttributeGUI(p)
                    EventType.REMOVEAtt -> removeAttributeGUI(p)
                    EventType.RENAMEAtt -> editAttributeGUI(aux!!,p)
                }
            }
        }

        private fun createPopupMenu() {

            val popupmenu = JPopupMenu("Actions")
/*
            //Rename entity works
            val a = JMenuItem("Rename Entity")
            a.addActionListener {
                val text = JOptionPane.showInputDialog("Rename to")
                execute(RenameEntity(this.e,this.e.name,text))
                revalidate()
                repaint()
            }
            popupmenu.add(a)

            //Rename attribute works only when you press enter after
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
*/
            //Add and Remove work but remove doesn't update GUI
            val c = JMenuItem("Add Entity")
            c.addActionListener {
                val text = JOptionPane.showInputDialog("Entity Name")
                execute(AddEntity(e,Entity(text)))
            }
            popupmenu.add(c)

            val d = JMenuItem("Delete Entity")
            d.addActionListener {
                if(e.parent  !=  null){
                    execute(RemoveEntity(e.parent!!,e))
                }
            }
            popupmenu.add(d)

            addMouseListener(object : java.awt.event.MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isRightMouseButton(e))
                        popupmenu.show(this@ComponentEnt, e.x, e.y)
                }
            })
        }

        private fun addEntityGUI(e: Element){
            add(ComponentEnt(e as Entity))
            revalidate()
            repaint()
        }

        private fun removeEntityGUI(){
            e.parent?.children?.remove(e)
            val aux = parent
            aux.remove(this@ComponentEnt)
            aux.revalidate()
            aux.repaint()
        }

        private fun replaceEntityGUI(old: Element, new: Element){
            if(old != new && this.e == old){
                this.e = new as Entity
            }
        }

        private fun addAttributeGUI(a:Element){
            a as Attribute
            this.e.attribute.add(a)
            var att = ComponentAtt(a)
            attPanel.add(att)
            revalidate()
            repaint()
        }

        private fun removeAttributeGUI(a:Element){
            this.e.attribute.remove(a)
            revalidate()
            repaint()
        }

        private fun editAttributeGUI(old: Element, new: Element){
            new as Attribute
            if(old == new){
                this.e.getAttribute2(old as Attribute).value= new.value
            }
            revalidate()
            repaint()
        }

    }

    inner class ComponentAtt(var attribute: Attribute): JPanel(){

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

}

fun main() {
    val xmlheader = Prolog("UTF-8", "1.0")

    val root = Entity("Bookstore", null)
    root.attribute.add(Attribute("Owner", "Lourenço"))
    root.attribute.add(Attribute("Category", "Good Books"))

    val children1 = Entity("1984", root)
    children1.setText("Random text")
    children1.attribute.add(Attribute("ID", "Book1"))

    val children2 = Entity("Odyssey", root)
    children2.attribute.add(Attribute("ID", "Book2"))

    val children3 = Entity("Chapter1", children2)
    children3.setText("Random long text")
    children3.attribute.add(Attribute("Name", "Intro"))

    val children4 = Entity("Chapter2", children2)
    children4.setText("Random long text")

    val xml = XML(xmlheader, root)

    val w = GUI(xml)
    w.open()
}