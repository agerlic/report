package notifiers;
 
import play.*;
import play.mvc.*;
import java.util.*;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import models.Report;
import models.UserReport;
import models.User;
 
public class Mails extends Mailer {
	
	private static List<String> jsonIterate(JsonObject obj) {
		List<String> res = new ArrayList<String>();
		Stack<JsonObject> st = new Stack<JsonObject>();
		st.push(obj);
		String format = "";
		while (!st.isEmpty()) {
			JsonObject root = st.pop();
			if (root == null) {
				format = format.substring(1);
				continue;
			}
			for (final Entry<String, JsonElement> entry : root.entrySet()) {
				if (entry.getKey().equalsIgnoreCase("data")) {
					if (format.isEmpty()) {
						res.add("=== " + entry.getValue().getAsString() + " ===");
						format = " * %s";
					} else {
						if (format.length() % 2 == 0) {
							format = format.replace("*", "-");
						} else {
							format = format.replace("-", "*");
						}
						res.add(String.format(format, entry.getValue().getAsString()));
					}
				} else if (entry.getKey().equalsIgnoreCase("children")) {
					st.push(null);
					format = " " + format;
					JsonArray children = entry.getValue().getAsJsonArray();
					for (int i = children.size() - 1; i >= 0; --i) {
						st.push(children.get(i).getAsJsonObject());		
					}
				}
			}
		}
		res.add("");
		return res;
	}

	public static void sendReport(Report report, User user) {
		setSubject("Report %s", report.creationDate.toString());
		String mailDestination = (String) play.Play.configuration.get("mailDestination");
		if (mailDestination == null || mailDestination.isEmpty()) {
			addRecipient(user.email);			
		} else {
			addRecipient(play.Play.configuration.get("mailDestination"));			
		}
		setFrom(play.Play.configuration.get("mailFrom"));
		JsonParser parser = new JsonParser();

		List<UserReport> userReports = report.reports;
		Collections.sort(userReports); //sort by username
		List<String> res = new ArrayList<String>();
		for (UserReport r : userReports) {
			JsonElement jsonElement = parser.parse(r.content);
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			res.addAll(jsonIterate(jsonObject));
		}

		send(report, res);
	}
 
   public static void sendReminders(Report report) {
	   setSubject((String)play.Play.configuration.get("mailReminderSubject"));
	   List<User> activeUsers = User.find("byIsActive", true).fetch();
	   if (activeUsers.size() == 0) {
		   return;
	   }
	   for (User u : activeUsers) {
		   if (UserReport.find("byAuthorAndFullReport", u, report).first() == null) {
			   addRecipient(u.email);
		   }
	   }
	   setFrom(play.Play.configuration.get("mailFrom"));
	   send();	   
   }
 
}