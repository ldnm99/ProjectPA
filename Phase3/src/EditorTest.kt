import java.awt.*
import java. awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.FileWriter
import java.io.PrintWriter
import java.util.*
import javax.swing.*
import javax.swing.border.CompoundBorder

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
    val stack = Stack<Command>()

    fun execute(c: Command) {
        c.run()
        stack.add(c)
    }

    fun undo() {
        if (stack.isNotEmpty())
            stack.pop().undo()
    }
}

interface Command {
    fun run()
    fun undo()
}

interface EditorEvent {
    fun entModified(old: Entity, new: Entity) {}
    fun entDeleted(entity: Entity) {}
}

enum class EventType {
    ADD, REMOVE, RENAME
}

class XMLEditor2(var xml: XML?= null): JFrame("XMLEditor") {

    var root = Entity("", null)
    var header : Prolog?= null

    var container = JPanel()
    var tree      = JPanel()
    var operation = JPanel()

    var buttons = JMenuBar()
    var save    = JButton("Save to File")
    var undo    = JButton("WIP undo")

    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        size = Dimension(300, 300)

        if (xml != null){
            root = xml!!.root
            header = xml!!.header
        }

        container.layout = BoxLayout(container,BoxLayout.Y_AXIS)

        save.addActionListener {
            val out =  PrintWriter(FileWriter("Test.xml"))
            if(header != null){
                out.write(serialization(root, serializationheader(header!!)))
            }else
                out.write(serialization(root, ""))
            out.close()
        }


        buttons.add(save)
        buttons.add(undo)
        operation.add(buttons)

        tree = ComponentSkeleton(root)

        container.add(operation)
        container.add(JScrollPane(tree))

        add(container)
    }
    fun open() {
        isVisible = true
    }

    class ComponentSkeleton(var e: Entity) : JPanel(), IObservable<EditorEvent> {

        var attributes = JPanel()
        val undoStack = UndoStack()

        fun execute(c: Command) {
            undoStack.execute(c)
        }

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
            add(attributes)
            mapEntity()
            createPopupMenu()
        }

        private fun mapEntity(){
            e.attribute.forEach {
                var aux = JPanel(FlowLayout())
                aux.add(JLabel(it.name))
                var textfield = JTextField(it.value)
                aux.add(textfield)
                var attribute = it
                textfield.addActionListener{
                    execute(EditAttribute(attribute,attribute.value,textfield.text))
                }
                attributes.add(aux)
            }
            if(e.children.isEmpty()){
                add(JTextArea(e.value))
            }else{
                e.children.forEach {
                    add(ComponentSkeleton(it as Entity))
                }
            }
        }

        private fun createPopupMenu() {

            val popupmenu = JPopupMenu("Actions")

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
                val text = JOptionPane.showInputDialog("Attribute Name")
                var aux = JPanel(FlowLayout())
                aux.add(JLabel(text))

                val temp = Attribute(text, "", this.e)
                this.e.attribute.add(temp)
                val aux2 = e.getAttribute2(temp)
                val textfield = JTextField("Insert Attribute Value")
                textfield.addActionListener{
                    aux2.value  = textfield.text
                }

                aux.add(textfield)
                attributes.add(aux)
                revalidate()
                repaint()
            }
            popupmenu.add(b)

            val c = JMenuItem("Add Entity")
            c.addActionListener {
                val text = JOptionPane.showInputDialog("Entity Name")
                //add(ComponentSkeleton(Entity(text,e)))
                execute(AddEntity(e, text))
                revalidate()
            }
            popupmenu.add(c)

            //não faz update no gui
            val d = JMenuItem("Delete Entity")
            d.addActionListener {
                execute(RemoveEntity(e,e.name))
                revalidate()
                repaint()
            }
            popupmenu.add(d)

            //não faz update no gui
            val da = JMenuItem("Delete Attribute")
            da.addActionListener {
                val text = JOptionPane.showInputDialog("Attribute name you want to delete")
                if( e.getAttribute(text) != null){
                    e.attribute.remove(e.getAttribute(text))
                }
                revalidate()
                repaint()
            }
            popupmenu.add(da)

            val undo = JMenuItem("Undo")
            undo.addActionListener {
                undoStack.undo()
            }

            addMouseListener(object : java.awt.event.MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isRightMouseButton(e))
                        popupmenu.show(this@ComponentSkeleton, e.x, e.y)
                }
            })
        }

        override val observers: MutableList<EditorEvent>  = mutableListOf()
    }
}

class RenameEntity(val entity: Entity, val oldValue:String, val newValue:String): Command {
    override fun run() {
        entity.rename(newValue,oldValue)
    }

    override fun undo() {
        entity.rename(oldValue,newValue)
    }
}

class AddEntity(val parent: Entity, val name:String): Command {

    override fun run() {
        parent.addChild(name)
    }

    override fun undo() {
        parent.removeChild(name)
    }
}

class RemoveEntity(val parent: Entity, val name:String): Command {
    override fun run() {
        parent.removeChild(name)
    }

    override fun undo() {
        parent.addChild(name)
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

    val w = XMLEditor2(xml)
    w.open()
}

