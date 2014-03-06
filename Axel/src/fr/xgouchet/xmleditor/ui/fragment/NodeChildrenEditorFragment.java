package fr.xgouchet.xmleditor.ui.fragment;

import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import fr.xgouchet.xmleditor.R;
import fr.xgouchet.xmleditor.common.AxelUtils;
import fr.xgouchet.xmleditor.data.tree.TreeNode;
import fr.xgouchet.xmleditor.data.xml.XmlData;
import fr.xgouchet.xmleditor.data.xml.XmlNode;
import fr.xgouchet.xmleditor.data.xml.XmlNodeStyler;
import fr.xgouchet.xmleditor.ui.activity.AxelActivity;
import fr.xgouchet.xmleditor.ui.adapter.NodeListAdapter;
import fr.xgouchet.xmleditor.ui.adapter.NodeViewListener;
import fr.xgouchet.xmleditor.ui.widget.FastScrollTrickListener;


public class NodeChildrenEditorFragment extends ANodeEditorFragment {
    
    private ListView mListView;
    private NodeListAdapter<XmlData> mAdapter;
    private NodeViewListener<XmlData> mNodeListener;
    
    
    //////////////////////////////////////////////////////////////////////////////////////
    // FRAGMENT LIFECYCLE
    //////////////////////////////////////////////////////////////////////////////////////    
    
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setHasOptionsMenu(true);
    }
    
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        
        View root = inflater.inflate(R.layout.fragment_node_children_editor, container, false);
        
        // Setup the listview
        mListView = (ListView) root.findViewById(android.R.id.list);
        
        // enable fast scroll
        mListView.setFastScrollEnabled(true);
        mListView.setOnScrollListener(new FastScrollTrickListener(mListView));
        
        // display the node's children
        setupChildrenListView();
        
        return root;
    }
    
    //////////////////////////////////////////////////////////////////////////////////////
    // OPTIONS MENU
    //////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        
        
        if (mNode == null) {
            return;
        }
        
        inflater.inflate(R.menu.node_children_editor, menu);
        
        Menu submenu = menu.findItem(R.id.action_add_child).getSubMenu();
        submenu.setGroupVisible(R.id.action_group_common_add_child, true);
        submenu.setGroupVisible(R.id.action_group_document_add_child, mNode.isDocument());
        submenu.setGroupVisible(R.id.action_group_element_add_child, mNode.isElement());
        
        if (mNode.isDocument()) {
            submenu.findItem(R.id.action_add_child_doctype).setEnabled(!mNode.hasDoctype());
            submenu.findItem(R.id.action_add_child_element).setEnabled(!mNode.hasRootChild());
        }
        
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        // probably an Add Node action
        XmlNode node = AxelUtils.createXmlNode(item.getItemId());
        if (node != null) {
            ((AxelActivity) getActivity()).addChildToNode(mNode, node);
            
            setupChildrenListView();
            
            getActivity().invalidateOptionsMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //////////////////////////////////////////////////////////////////////////////////////
    // GETTERS / SETTERS
    //////////////////////////////////////////////////////////////////////////////////////
    
    
    public void setNodeListener(final NodeViewListener<XmlData> nodeListener) {
        mNodeListener = nodeListener;
    }
    
    //////////////////////////////////////////////////////////////////////////////////////
    // UI SETUP
    //////////////////////////////////////////////////////////////////////////////////////
    
    private void setupChildrenListView() {
        
        // create the node list 
        List<TreeNode<XmlData>> list = new LinkedList<TreeNode<XmlData>>();
        if (!mNode.isDocument()) {
            list.add(mNode);
        }
        list.addAll(mNode.getChildren());
        
        // Display the tree 
        mAdapter = new NodeListAdapter<XmlData>(getActivity(), list, mNodeListener);
        mAdapter.setNodeStyler(new XmlNodeStyler());
        mAdapter.setHasRoot(!mNode.isDocument());
        mListView.setAdapter(mAdapter);
        
        
    }
    
    
}