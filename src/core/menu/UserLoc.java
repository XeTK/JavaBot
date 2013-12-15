package core.menu;

public class UserLoc {
	
	private MenuItem curLoc_;
	
	private String   username_;
	
	private boolean  inMenu_;
	
	public UserLoc(String username, MenuItem root) {
		this.username_ = username;
		this.curLoc_   = root;
	}

	public MenuItem getCurLoc() {
		return curLoc_;
	}

	public void setCurLoc(MenuItem curLoc) {
		this.curLoc_ = curLoc;
	}

	public boolean isInMenu() {
		return inMenu_;
	}

	public void setInMenu(boolean inMenu) {
		this.inMenu_ = inMenu;
	}

	public String getUsername() {
		return username_;
	}
	
	
}
