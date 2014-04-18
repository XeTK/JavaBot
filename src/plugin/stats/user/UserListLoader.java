package plugin.stats.user;

import java.io.File;

import core.Channel;
import core.menu.MenuItem;
import core.plugin.Plugin;
import core.utils.JSON;

public class UserListLoader extends Plugin{
	private final String DB_FILE = "Users.json";

	private String dbFile_ = new String();
	
	private UserList userList_;
	
	public void onCreate(Channel inChannel) throws Exception {
		super.onCreate(inChannel);
		
		dbFile_ = channel_.getPath() + DB_FILE;
		
		if (new File(dbFile_).exists()){	
			userList_ = (UserList) JSON.load(dbFile_, UserList.class);
		} 
		
		if (userList_ == null) {
			userList_ = new UserList();
			JSON.save(dbFile_, userList_);
		}
	}
	
	public void rawInput(String inStr) throws Exception 
	{
		if (userList_ != null && dbFile_.isEmpty() == false)
			JSON.save(dbFile_, userList_);
	}
	
	public UserList getUserList(){
		return userList_;
	}

	@Override
	public void getMenuItems(MenuItem rootItem) {
		//No menu needed.
	}
}
