package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Report extends Model {
	@OneToMany(mappedBy="fullReport", cascade=CascadeType.ALL)
	public List<UserReport> reports;
	
	public Date creationDate;
	
	public boolean isCompleted;
	
	public Report() {
		creationDate = new Date();
		isCompleted = false;
		reports = new ArrayList<UserReport>();
	}
}
