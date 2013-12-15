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
	
	public static MenuItem selectNode(UserLoc user, int nodeNumber, String args) {
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
		
		if (temp.getChildren().size() == 0) {
			if (temp.getAuth() == AuthGroup.ADMIN) {
				if (Details.getInstance().isAdmin(user.getUsername())) {
					temp.onExecution(args);
				}
			} else if (temp.getAuth() == AuthGroup.NONE) {
				temp.onExecution(args);
			}
			//returnToRoot(user);
		} else {
			user.setCurLoc(temp);
		}
		
		return user.getCurLoc();
	}

}
