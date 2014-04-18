package core.menu;

import java.util.ArrayList;

import core.utils.Details;

public class MenuNav {
	
	public static MenuItem returnToRoot(UserLoc user) {
		MenuItem temp = user.getCurLoc();
		while (true){
			if (temp.getParentMI() == null || temp == null)
				break;
			
			temp = temp.getParentMI();
		}
		user.setCurLoc(temp);
		return temp;
	}
	
	public static MenuItem preLevel(UserLoc user) {
		MenuItem temp = user.getCurLoc();
		if (temp.getParentMI() != null) 
			temp = temp.getParentMI();
		user.setCurLoc(temp);
		return temp;
	}
	
	public static MenuItem selectNode(UserLoc user, int nodeNumber) {
		MenuItem temp = user.getCurLoc();		
		ArrayList<MenuItem> nodes = temp.getChildren();
		
		if (nodes != null) {
			for (MenuItem mi: nodes) {
				if (mi.getNodeNumber() == nodeNumber) {
					temp = mi;
					break;
				}
			}
		}
				
		return temp;
	}

	public static void executeNode(MenuItem node, UserLoc user, String args) {
		if (node.getChildren().size() == 0) {
			if (node.getAuth() == AuthGroup.ADMIN) {
				if (Details.getInstance().isAdmin(user.getUsername())) {
					node.onExecution(args, user.getUsername());
				}
			// TODO: implement registered group when auth is in.
			} else /*if (node.getAuth() == AuthGroup.NONE) For now while registered is not implemented*/ {
				node.onExecution(args, user.getUsername());
			}
			//returnToRoot(user);
		} else {
			user.setCurLoc(node);
		}
	}
	
	public static String helpNode(MenuItem node, UserLoc user) {
		String tempHelp = new String();
		if (node.getChildren().size() == 0) {
			if (node.getAuth() == AuthGroup.ADMIN) {
				if (Details.getInstance().isAdmin(user.getUsername())) {
					tempHelp = node.onHelp();
				}
				// TODO: implement registered group when auth is in.
			} else /*if (node.getAuth() == AuthGroup.NONE) For now while registered is not implemented*/ {
				tempHelp = node.onHelp();
			}
			//returnToRoot(user);
		} else {
			user.setCurLoc(node);
		}
		return tempHelp;
	}
}
