package code;

import java.util.Arrays;

public class GoodFakeArray {

    private static TreeNode[] bufferNodes = new TreeNode[TreeNode.MAX_TREENODE_COUNT + 1];

    private TreeNode rootNode = null;

    /**
     * 插入一个长住的头节点和尾巴节点，可以减少代码的特殊处理
     */
    public GoodFakeArray() {
        TreeNode treeNode = new TreeNode();
        LeafNode startNode = new LeafNode();
        LeafNode endNode = new LeafNode();
        treeNode.addNodeToLast(startNode);
        treeNode.addNodeToLast(endNode);
        treeNode.updateChildTreeNodeNum();
        treeNode.updateChildValueNodeNum();
        rootNode = treeNode;
    }

    public int length() {
        return this.rootNode.getChildValueNodeNum() - 2;
    }

    public Object get(int loc) {
        if(loc > this.length()) {
            System.out.println("some err in get");
            return null;
        }
        return ((LeafNode)findNode(loc + 1, this.rootNode)).getValue();
    }

    public boolean add(int loc, Object value) {
        if(loc > this.length()) {
            return false;
        }
        loc = loc + 1; //转化一下
        TreeNode markNode = findNode(loc, this.rootNode);
        TreeNode fatherNode = markNode.getFatherNode();
        TreeNode insertNode = new LeafNode();
        ((LeafNode)insertNode).setValue(value);
        while(insertNode != null && markNode != null) {
            if(fatherNode != null) {
                if(fatherNode.getChildTreeNodeNum() == TreeNode.MAX_TREENODE_COUNT) {
                    TreeNode newFatherNode = insertToNodeSplit(fatherNode, markNode, insertNode);
                    fatherNode.updateChildValueNodeNum();
                    newFatherNode.updateChildValueNodeNum();
                    insertNode = newFatherNode;
                    markNode = fatherNode;
                    fatherNode = fatherNode.getFatherNode();
                } else {
                    insertToNodeNoSplit(fatherNode, markNode, insertNode);
                    fatherNode.updateChildValueNodeNum();
                    fatherNode = fatherNode.getFatherNode();
                    while(fatherNode != null) {
                        fatherNode.updateChildValueNodeNum();
                        fatherNode = fatherNode.getFatherNode();
                    }
                    break;
                }
            } else {
                TreeNode newRoot = new TreeNode();
                newRoot.addNodeToLast(markNode);
                insertToNodeNoSplit(newRoot, markNode, insertNode);
                newRoot.updateChildValueNodeNum();
                this.rootNode = newRoot;
                break;
            }
        }
        return true;
    }

    public boolean addLast(Object value) {
        return this.add(this.length(), value);
    }

    public boolean addFirst(Object value) {
        return this.add(0, value);
    }

    public boolean delete(int loc) {
        if(loc >= this.length()) {
            return false;
        }
        loc = loc + 1;
        TreeNode deleteNode = findNode(loc, this.rootNode);
        TreeNode fatherNode = deleteNode.getFatherNode();
        while(deleteNode != null && fatherNode != null) {
            fatherNode.removeNode(deleteNode);
            if(fatherNode.getChildTreeNodeNum() == 0) {
                deleteNode = fatherNode;
                fatherNode = fatherNode.getFatherNode();
            } else {
                fatherNode.updateChildValueNodeNum();
                fatherNode = fatherNode.getFatherNode();
                while(fatherNode != null) {
                    fatherNode.updateChildValueNodeNum();
                    fatherNode = fatherNode.getFatherNode();
                }
                break;
            }
        }
        return true;
    }

    public int getDepth() {
        TreeNode node = this.rootNode;
        int depth = 0;
        while(node != null) {
            depth += 1;
            if(node.getSubNodes() != null) {
                node = node.getSubNodes()[0];
            } else {
                break;
            }
        }
        return depth;
    }

    private TreeNode findNode(int loc, TreeNode node) {
        if(node instanceof LeafNode) {
            return node;
        }
        for(TreeNode childNode : node.getSubNodes()) {
            if(childNode != null) {
                if(loc < childNode.getChildValueNodeNum()) {
                    return findNode(loc, childNode);
                } else {
                    loc -= childNode.getChildValueNodeNum();
                }
            }
        }
        assert false;
        return null;
    }

    private void insertToNodeNoSplit(TreeNode father, TreeNode marklocNode, TreeNode newNode) {
        assert father.getChildTreeNodeNum() < TreeNode.MAX_TREENODE_COUNT;
        father.addNodeBeforeNode(newNode, marklocNode);
    }

    private TreeNode insertToNodeSplit(TreeNode father, TreeNode marklocNode, TreeNode newNode) {
        assert father.getChildTreeNodeNum() == TreeNode.MAX_TREENODE_COUNT;
        TreeNode[] subNodes = father.getSubNodes();
        int addLoc = 0;
        for(int i = 0; i < subNodes.length; i++) {
            if(subNodes[i] != marklocNode) {
                bufferNodes[addLoc++] = subNodes[i];
            } else {
                bufferNodes[addLoc++] = newNode;
                bufferNodes[addLoc++] = subNodes[i];
            }
        }
        father.clearAllChild();
        TreeNode newFather = new TreeNode();
        addLoc = 0;
        int splitSize = (TreeNode.MAX_TREENODE_COUNT + 1) / 2;
        for(int i = 0; i < splitSize; i++) {
            newFather.addNodeToLast(bufferNodes[addLoc++]);
        }
        for(int i = 0; i < splitSize; i++) {
            father.addNodeToLast(bufferNodes[addLoc++]);
        }
        return newFather;
    }
}


class TreeNode {
    public static int MAX_TREENODE_COUNT = 5;
    protected TreeNode[] subNodes = new TreeNode[MAX_TREENODE_COUNT];
    private TreeNode fatherNode;
    private int childValueNodeNum;
    private int childTreeNodeNum;

    public TreeNode() {
        this.fatherNode = null;
        Arrays.fill(this.subNodes, null);
        this.childValueNodeNum = 0;
        this.childTreeNodeNum = 0;
    }

    public TreeNode getFatherNode() {
        return fatherNode;
    }

    public void setFatherNode(TreeNode fatherNode) {
        this.fatherNode = fatherNode;
    }

    public int getChildValueNodeNum() {
        return this.childValueNodeNum;
    }

    public void setChildValueNodeNum(int value) {
        this.childValueNodeNum = value;
    }

    public int getChildTreeNodeNum() {
        return this.childTreeNodeNum;
    }

    public void setChildTreeNodeNum(int value) {
        this.childTreeNodeNum = value;
    }

    public void updateChildTreeNodeNum() {
        int count = 0;
        for(TreeNode node : this.subNodes) {
            if(node != null) {
                count += 1;
            }
        }
        this.childTreeNodeNum = count;
        return;
    }

    public void updateChildValueNodeNum() {
        int count = 0;
        for(TreeNode node : this.subNodes) {
            if(node != null) {
                count += node.getChildValueNodeNum();
            }
        }
        this.childValueNodeNum = count;
        return;
    }

    public TreeNode[] getSubNodes() {
        return this.subNodes;
    }

    public void addNodeToLast(TreeNode node) {
        assert this.getChildTreeNodeNum() < TreeNode.MAX_TREENODE_COUNT;
        this.subNodes[this.getChildTreeNodeNum()] = node;
        node.setFatherNode(this);
        this.setChildTreeNodeNum(this.getChildTreeNodeNum() + 1);
    }

    public void removeNode(TreeNode childNode) {
        for(int i = 0; i <= this.subNodes.length - 1; i++) {
            if(childNode == this.subNodes[i]) {
                this.subNodes[i] = null;
                for(int j = i; j <= this.subNodes.length - 2; j++) {
                    this.swap(j, j + 1);
                }
                this.setChildTreeNodeNum(this.getChildTreeNodeNum() - 1);
                return;
            }
        }
        assert false;
        return;
    }

    public void addNodeBeforeNode(TreeNode node, TreeNode before) {
        assert this.getChildTreeNodeNum() < MAX_TREENODE_COUNT;
        for(int i = this.subNodes.length - 2; i >= 0; i--) {
            if(this.subNodes[i] != before) {
                this.swap(i, i + 1);
            } else {
                this.swap(i, i + 1);
                this.subNodes[i] = node;
                node.setFatherNode(this);
                this.setChildTreeNodeNum(this.getChildTreeNodeNum() + 1);
                return;
            }
        }
        assert  false;
    }

    public void clearAllChild() {
        Arrays.fill(this.subNodes, null);
        this.setChildTreeNodeNum(0);
        this.setChildValueNodeNum(0);
    }

    private void swap(int x, int y) {
        TreeNode temp = this.subNodes[x];
        this.subNodes[x] = this.subNodes[y];
        this.subNodes[y] = temp;
    }

}

class LeafNode extends  TreeNode {
    private Object value;
    public LeafNode() {
        this.value = null;
        this.setChildValueNodeNum(1);
        this.setChildTreeNodeNum(0);
        this.subNodes = null;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
