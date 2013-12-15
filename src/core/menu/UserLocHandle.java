package core.menu;

import java.util.ArrayList;

public class UserLocHandle {

	private ArrayList<UserLoc> users = new ArrayList<UserLoc>();
	
	private MenuItem rootMenuItem;
	
	public UserLocHandle(MenuItem rootMenuItem) {
		this.rootMenuItem = rootMenuItem;
	}
	
	public UserLoc getUser(String userName) {
		UserLoc temp = null;
		boolean found = false;
		for (UserLoc user: users) {
			if (user.getUsername().equals(userName)) {
				temp = user;
				found = true;
				break;	
			}
		}
		if (!found){
			temp = new UserLoc(userName, rootMenuItem);
			users.add(temp);
		}
		return temp;
	}
	
}
