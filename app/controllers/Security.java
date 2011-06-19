package controllers;

import models.User;

public class Security extends Secure.Security {
	static boolean authenticate(String username, String password) {
		return User.connect(username, password) != null;
	}
	
	static boolean check(String profile) {
	    if("admin".equals(profile)) {
	        User u = User.find("byEmail", connected()).<User>first();
	        return u.isAdmin && u.isActive;
	    }
	    return false;
	}
}
