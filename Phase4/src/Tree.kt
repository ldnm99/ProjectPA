import javax.swing.JTree
import javax.swing.event.TreeModelEvent
import javax.swing.event.TreeModelListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeSelectionModel

class Tree: JTree() {

    private val rootnode = DefaultMutableTreeNode(Entity("Root", null))
    var treemodel = DefaultTreeModel(rootnode)
    var tree  = JTree(treemodel)

    init {
        treemodel.addTreeModelListener(TreelListener())
        tree.isEditable = true
        tree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
        tree.showsRootHandles = true
    }
}


class TreelListener : TreeModelListener {

    override fun treeNodesChanged(e: TreeModelEvent) {
        var node : DefaultMutableTreeNode = e.treePath.lastPathComponent as DefaultMutableTreeNode
        try {
            var index = e.childIndices[0]
            (node.getChildAt(index) as DefaultMutableTreeNode).also { node = it }
        }catch (exc : KotlinNullPointerException){
        }
    }

    override fun treeNodesInserted(e: TreeModelEvent?) {}
    override fun treeNodesRemoved(e: TreeModelEvent?) {}
    override fun treeStructureChanged(e: TreeModelEvent?) {}
}
