package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;
import models.Report;
import models.User;
import models.UserReport;
import models.UserStatus;
import notifiers.Mails;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Reports extends Controller {
	@Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.fullname);
        }
        renderArgs.put("historyReports", Report.find("order by creationDate desc").fetch());
    }

	public static Date computeDateDiff(Date d1, Date d2) {
		long m1 = d1.getTime();
		long m2 = d2.getTime();
		return new Date(m2 - m1);
	}
	
	public static void show(Long id) {
		Report report = Report.findById(id);
		Assert.assertNotNull(report);
		renderArgs.put("report_id", id);
		
		// user report content
		User currentUser = User.find("byEmail", Security.connected()).first();
		UserReport personalReport = UserReport.find("byFullReportAndAuthor", report, currentUser).first();	
		String userContent = null;
		if (personalReport != null) {
			userContent = personalReport.content;
		}
		

		List<UserStatus> usersStatus = new ArrayList<UserStatus>();
		List<User> users = User.findAll();
		boolean needReminder = false;
		for (User u : users) {
            if (!u.isActive) {
                continue;
            }
			if (UserReport.find("byFullReportAndAuthor", report, u).first() != null) {
				usersStatus.add(new UserStatus(u.fullname, true));
			} else {
				usersStatus.add(new UserStatus(u.fullname, false));
				needReminder = true;
			}
		}
		renderArgs.put("needReminder", needReminder);
		renderArgs.put("usersStatus", usersStatus);

		Long lastReportId = ((Report)Report.find("order by creationDate desc").first()).id;
		boolean canUpdate = (id.equals(lastReportId));
		boolean canSendReminder = true;
		if (currentUser.lastReminderDate != null) {
			Calendar cal = Calendar.getInstance();  
			cal.setTime(new Date());  
			int currentDays = cal.get(Calendar.DAY_OF_YEAR);
			cal.setTime(currentUser.lastReminderDate);
			int lastReminderDays = cal.get(Calendar.DAY_OF_YEAR);
			canSendReminder = Math.abs(currentDays - lastReminderDays) > 1;			
		}
		render(userContent, canUpdate, canSendReminder);	
	}

	public static void mergeReports(Long id) {
		Report report = Report.findById(id);
		Assert.assertNotNull(report);
		if (report.reports.size() > 0) {
			Collections.sort(report.reports);
			String data = "[";
			for (UserReport r : report.reports) {
				data += r.content + ",";
			}
			data = data.substring(0, data.length() - 1) + "]";
			renderJSON(data);
		} else {
			renderJSON("[]");
		}
	}
	
	
    public static void sendReport(Long id) {
    	Report report = Report.findById(id);
    	User user = User.find("byEmail", Security.connected()).first();
    	Mails.sendReport(report, user);
    	show(id);
    }
    
    public static void sendReminders(Long id) {
    	Report report = Report.findById(id);
    	Mails.sendReminders(report);
    	User user = User.find("byEmail", Security.connected()).first();
    	user.lastReminderDate = new Date();
    	user.save();
    	show(id);
    }
    
    public static void newReport() {
    	Report r = new Report().save();
    	show(r.id);
    }
    
    public static void save(Long id, String content) {
    	Report report = Report.findById(id);
    	User user = User.find("byEmail", Security.connected()).first();
    	UserReport myReport = UserReport.find("byAuthorAndFullReport", user, report).first();
    	//remove brackets
    	String cleanedContent = content.substring(1, content.length() - 1);
    	if (myReport != null) {
    		myReport.content = cleanedContent;
    	} else {
    		myReport = new UserReport(user, report, cleanedContent);    		
    	}
    	myReport.save();
    	show(id);
    }
}
