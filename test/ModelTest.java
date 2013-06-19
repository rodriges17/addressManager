import java.util.LinkedList;
import java.util.List;

import org.junit.*;

import com.ning.http.client.Request;

import static org.junit.Assert.*;

import play.mvc.*;
import play.test.*;
import static play.test.Helpers.*;

import models.*;

public class ModelTest extends WithApplication {
   
	@Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }
	
	@Test
    public void createAndRetrieveUser() {
        new User("bob@gmail.com", "secret", false).save();
        User bob = User.find.where().eq("email", "bob@gmail.com").findUnique();
        assertNotNull(bob);
        assertEquals("bob@gmail.com", bob.email);
        assertEquals("secret", bob.password);
    }
	
	@Test
	public void tryAuthenticateUser() {
		new User("bob@gmail.com", "secret", false).save();
		assertNotNull(User.authenticate("bob@gmail.com", "secret"));
		assertNull(User.authenticate("bob@gmail.com", "badpassword"));
		assertNull(User.authenticate("tom@gmail.com", "secret"));
	}
	
	@Test
    public void createAndRetrieveContact() {
		new User("admin@test.com", "admin", false).save();
		User admin = User.find.where().eq("email", "admin@test.com").findUnique();
		new ContactGroup("Bern", admin).save();
		ContactGroup cg = ContactGroup.find.where().eq("name", "Bern").findUnique();
//		new Contact("Doe", "Joe", "john.doe@test.com", "Test Street", "Test City", "01234567", cg).save();
    }
	
	@Test
    public void createAndRetrieveContactGroup() {
		new User("admin@test.com", "admin", false).save();
		User admin = User.find.where().eq("email", "admin@test.com").findUnique();
		new ContactGroup("Bern", admin).save();
		ContactGroup cg = ContactGroup.find.where().eq("name", "Bern").findUnique();
		assertNotNull(cg);
    }
	
	@Test
    public void findContactGroupsInvolving() {
        User bob = new User("bob@gmail.com", "secret", false);bob.save();
        new User("jane@gmail.com", "secret", false).save();
        
        ContactGroup.create("Bern", "bob@gmail.com");
        ContactGroup.create("Basel", "jane@gmail.com");

        List<ContactGroup> results = ContactGroup.findInvolvingOwner(bob);
        assertEquals(1, results.size());
        assertEquals("Bern", results.get(0).name);
    }
	
//	@Test
//	public void createContactBelongingToContactGroup() {
//		new User("admin@test.com", "admin").save();
//		User admin = User.find.where().eq("email", "admin@test.com").findUnique();
//		new ContactGroup("Bern", admin).save();
//		ContactGroup cg = ContactGroup.find.where().eq("name", "Bern").findUnique();
//		new Contact("Doe", "John", "john.doe@test.com", "Test Street", "Test City", "01234567", cg).save();
//	}

}
