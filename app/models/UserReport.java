package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class UserReport extends Model implements Comparable<UserReport>{
	@ManyToOne
	public User author;
	
	@ManyToOne
	public Report fullReport;
	
	@Lob
	public String content;
	
	public Date creationDate;
	
	public UserReport(User author, Report fullReport, String content) {
		this.author = author;
		this.author.reports.add(this);
		this.fullReport = fullReport;
		this.fullReport.reports.add(this);
		this.content = content;
		this.creationDate = new Date();
	}

	@Override
	public int compareTo(UserReport o) {
		return this.author.fullname.compareTo(o.author.fullname);
	}
}
