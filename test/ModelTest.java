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
        System.out.println(User.find.all().size());
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
    public void createAndRetrieveContactGroup() {
		User admin = User.find.where().eq("email", "admin@test.com").findUnique();
		ContactGroup cg = ContactGroup.find.where().eq("name", "Bern").findUnique();
		assertNotNull(cg);
		assertEquals("Bern", cg.name);
		assertEquals("admin@test.com", cg.owners.get(0).email);
		assertEquals(8, ContactGroup.all().size());
    }
	
	@Test
    public void findContactGroupsInvolving() {
        User bob = new User("bob@gmail.com", "secret", false);
        bob.save();
        User jane = new User("jane@gmail.com", "secret", false);
        jane.save();   
        ContactGroup cg = ContactGroup.find.where().eq("name", "Bern").findUnique();
        cg.addOwner(bob);
        ContactGroup cg2 = ContactGroup.find.where().eq("name", "Basel").findUnique();
        cg2.addOwner(jane);
        List<ContactGroup> results = ContactGroup.findInvolvingOwner(bob);
        assertEquals(1, results.size());
        assertEquals("Bern", results.get(0).name);
    }
	
	@Test
    public void createAndRetrieveContact() {
		//new User("admin@test.com", "admin", false).save();
		User admin = User.find.where().eq("email", "admin@test.com").findUnique();
		System.out.println(admin);
		//new ContactGroup("Bern", admin).save();
		ContactGroup cg = ContactGroup.find.where().eq("name", "Bern").findUnique();
		new Contact("Mr.", "Doe", "Mark", "mark.doe@test.com", "Test Street 5", "App1", "App2", "3012", "Test City", "01234567", cg, true).save();
		Contact contact = Contact.create("Mr.", "Doe", "Joe", "joe.doe@test.com", "Test Street", "App1", "App2", "3012", "Test City", "01234567", "Bern", "ja");
		Contact con1 = new Contact("Mr.", "Doe", "Hal", "hal.doe@test.com", "Test Street", "App1", "App2", "3012", "Test City", "01234567", cg, false);
		con1.save();
		// 1 Kontakt wird in Global.java erstellt, daher 4
		assertEquals(4, Contact.all().size());
	}
	
	@Test
    public void findContactsInvolvingGroupOwner() {
		User bob = new User("bob@gmail.com", "secret", false);
        bob.save();
        User alice = new User("alice@gmail.com", "topsecret", false);
        alice.save();
		ContactGroup cg = ContactGroup.find.where().eq("name", "Bern").findUnique();
		ContactGroup cg2 = ContactGroup.find.where().eq("name", "Basel").findUnique();
        cg.addOwner(bob);
        cg2.addOwner(alice);
        Contact c1 = Contact.create("Mr.", "Test1", "fTest1", "email1@g.ch", "street", "App1", "App2", "3012", "city", "phone", "Bern", "ja");
        Contact c2 = Contact.create("Ms.", "Test2", "fTest2", "email2@g.ch", "street", "App1", "App2", "3012", "city", "phone", "Bern", "");
        List<Contact> results = Contact.findInvolvingGroupOwner("bob@gmail.com");
        // 1 Gruppe wird in Global.java erstellt, daher 3
        assertEquals(3, results.size());
        assertEquals("Test1", results.get(1).name);
        List<Contact> resultsEmpty = Contact.findInvolvingGroupOwner("alice@gmail.com");
        assertEquals(0, resultsEmpty.size());
    }

}
