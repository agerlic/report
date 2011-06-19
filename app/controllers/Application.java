package controllers;

import models.Report;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Application extends Controller {
	
    public static void index() {
    	if (Report.count() > 0) {
    		Reports.show(((Report)Report.find("order by creationDate desc").first()).id);		
    	} else { //only used to bootstrap
    		Report report = new Report().save();
    		Reports.show(report.id);
    	}			
    }

}