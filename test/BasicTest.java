import java.util.List;

import models.Report;
import models.UserReport;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class BasicTest extends UnitTest {
	
    @Before
    public void setup() {
       Fixtures.deleteAllModels();
    }
	
	@Test
	public void createAndRetrieveUser() {
	    // Create a new user and save it
	    new User("bob@gmail.com", "secret", "Bob").save();
	    
	    // Retrieve the user with e-mail address bob@gmail.com
	    User bob = User.find("byEmail", "bob@gmail.com").first();
	    
	    // Test 
	    assertNotNull(bob);
	    assertEquals("Bob", bob.fullname);
	}

	@Test
	public void tryConnectAsUser() {
	    // Create a new user and save it
	    new User("bob@gmail.com", "secret", "Bob").save();
	    
	    // Test 
	    assertNotNull(User.connect("bob@gmail.com", "secret"));
	    assertNull(User.connect("bob@gmail.com", "badpassword"));
	    assertNull(User.connect("tom@gmail.com", "secret"));
	}
	
	@Test
	public void createReport() {
	    // Create a new user and save it
	    User bob = new User("bob@gmail.com", "secret", "Bob").save();
	    
	    // Create a new fullReport
	    Report fullReport = new Report().save();
	    
	    // Test that the post has been created
	    assertEquals(1, Report.count());
	    
	    // Create a new report
	    new UserReport(bob, fullReport, "my report").save();
	    
	    // Test that the report has been created
	    assertEquals(1, UserReport.count());
	    
	    
	    // Retrieve all reports created by Bob
	    List<UserReport> bobReports = UserReport.find("byAuthor", bob).fetch();
	    
	    // Tests Report
	    assertEquals(1, bobReports.size());
	    UserReport firstReport = bobReports.get(0);
	    assertNotNull(firstReport);
	    assertEquals(bob, firstReport.author);
	    assertEquals("my report", firstReport.content);
	    assertNotNull(firstReport.creationDate);
	    
	    // Test FullReport
	    assertEquals(1, fullReport.reports.size());
	}
}
