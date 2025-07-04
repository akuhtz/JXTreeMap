/**
 * 
 */
package net.sf.jtreemap.swing.provider;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.tree.TreePath;

import net.sf.jtreemap.swing.JXTreeMap;
import net.sf.jtreemap.swing.TreeMapNode;

public class ZoomAction extends AbstractAction {
    private static final long serialVersionUID = -8559400865920393294L;

    private final TreeMapNode node;
    private final JXTreeMap jTreeMap;

    /**
     * Constructor
     * 
     * @param node
     *            where you want to zoom/unzoom
     * @param icon
     *            icon corresponding to the operation (zoom or unzoom)
     */
    public ZoomAction(final JXTreeMap jTreeMap, final TreeMapNode node, final Icon icon) {
        super(node.getLabel(), icon);
        this.node = node;
        this.jTreeMap = jTreeMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        if (jTreeMap.getTreeView() == null) {
            jTreeMap.zoom(this.node);
            jTreeMap.repaint();
        } else {
            final TreePath path = new TreePath(this.node.getPath());
            jTreeMap.getTreeView().setSelectionPath(path);
            jTreeMap.getTreeView().scrollPathToVisible(path);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.Action#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}