package models;
 
import java.util.*;

import javax.persistence.*;
 
import play.db.jpa.*;
 
@Entity
public class User extends Model {
	
	@OneToMany(mappedBy="author", cascade=CascadeType.ALL)
	public List<UserReport> reports;
	
    public String email;
    public String password;
    public String fullname;
    public boolean isActive;
    public boolean isAdmin;
    //to avoid spam
    public Date lastReminderDate; 
    
    public User(String email, String password, String fullname) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.reports = new ArrayList<UserReport>();
        this.isActive = true;
        this.isAdmin = true;
        this.lastReminderDate = new Date(0);
    }
    
    public static User connect(String email, String password) {
        return find("byEmailAndPassword", email.toLowerCase(), password).first();
    }
}