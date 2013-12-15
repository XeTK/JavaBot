package core.menu;

import java.util.ArrayList;

public class MenuItem {
	private String    nodeName_;
	private MenuItem  parentMI_;
	private int       nodeNumber_ ;
	private AuthGroup auth = AuthGroup.NONE;
	private ArrayList<MenuItem> children_ = new ArrayList<MenuItem>();
	
	public MenuItem(String nodeName, MenuItem parent, int nodeNumber){
		this.nodeName_   = nodeName;
		this.parentMI_   = parent;
		this.nodeNumber_ = nodeNumber;
	}
	
	public MenuItem(String nodeName, MenuItem parent, int nodeNumber, AuthGroup auth){
		this.nodeName_   = nodeName;
		this.parentMI_   = parent;
		this.nodeNumber_ = nodeNumber;
		this.auth        = auth;
	}
	
	public void onExecution(String args) { System.out.println("Test Line");}
	
	public void addChild(MenuItem child) {
		children_.add(child);
	}

	public String getNodeName() {
		return nodeName_;
	}

	public MenuItem getParentMI() {
		return parentMI_;
	}

	public ArrayList<MenuItem> getChildren() {
		return children_;
	}

	public int getNodeNumber() {
		return nodeNumber_;
	}

	public AuthGroup getAuth() {
		return auth;
	}
	
}
