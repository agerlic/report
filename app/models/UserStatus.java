package models;


/**
 * UserStatus is not persisted
 */
public class UserStatus {
	public String name;
	public boolean isComplete;
	
	public UserStatus(String name, boolean isComplete) {
		this.name = name;
		this.isComplete = isComplete;
	}
}
