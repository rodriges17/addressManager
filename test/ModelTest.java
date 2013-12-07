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
		// By using fakeGlobal() we override behaviour from Global.java
        start(fakeApplication(inMemoryDatabase(), fakeGlobal()));
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
    public void createAndRetrieveContactGroup() {
		new User("admin@test.com", "admin", true).save();
		User admin = User.find.where().eq("email", "admin@test.com").findUnique();
		new ContactGroup("Bern", admin).save();
		ContactGroup cg = ContactGroup.find.where().eq("name", "Bern").findUnique();
		assertNotNull(cg);
		assertEquals("Bern", cg.name);
		assertEquals("admin@test.com", cg.owners.get(0).email);
		assertEquals(1, ContactGroup.all().size());
    }
	
	@Test
    public void findContactGroupsInvolvingOwner() {
		User admin = new User("admin@test.com", "admin", true);
		admin.save();
        User bob = new User("bob@gmail.com", "secret", false);
        bob.save();
        User jane = new User("jane@gmail.com", "secret", false);
        jane.save();   
        new ContactGroup("Bern", admin).save();
        new ContactGroup("Basel", admin).save();
        ContactGroup bern= ContactGroup.find.where().eq("name", "Bern").findUnique();
        bern.addOwner(bob);
        ContactGroup basel = ContactGroup.find.where().eq("name", "Basel").findUnique();
        basel.addOwner(jane);
        List<ContactGroup> results = ContactGroup.findInvolvingOwner(bob);
        assertEquals(1, results.size());
        assertEquals("Bern", results.get(0).name);
    }
	
	@Test
    public void createAndRetrieveContact() {
		new User("admin@test.com", "admin", true).save();
		User admin = User.find.where().eq("email", "admin@test.com").findUnique();
		new ContactGroup("Bern", admin).save();
		ContactGroup cg = ContactGroup.find.where().eq("name", "Bern").findUnique();
		new Contact("Mr.", "Doe", "Mark", "mark.doe@test.com", "Test Street 5", "App1", "App2", "3012", "Test City", "Switzerland", "01234567", cg, true, "Blabla", "Deutsch").save();
		Contact contact = Contact.create("Mr.", "Doe", "Joe", "joe.doe@test.com", "Test Street", "App1", "App2", "3012", "Test City", "Switzerland", "01234567", "Bern", "ja", "This is a remark", "Deutsch");
		Contact con1 = new Contact("Mr.", "Doe", "Hal", "hal.doe@test.com", "Test Street", "App1", "App2", "3012", "Test City", "Switzerland", "01234567", cg, false, "This is a remark 2", "Deutsch");
		con1.save();
		assertEquals(3, Contact.all().size());
	}
	
	@Test
    public void findContactsInvolvingGroupOwner() {
		User admin = new User("admin@test.com", "admin", true);
		admin.save();
		User bob = new User("bob@gmail.com", "secret", false);
        bob.save();
        User alice = new User("alice@gmail.com", "topsecret", false);
        alice.save();
        new ContactGroup("Bern", admin).save();
        new ContactGroup("Basel", admin).save();
		ContactGroup bern = ContactGroup.find.where().eq("name", "Bern").findUnique();
		ContactGroup basel = ContactGroup.find.where().eq("name", "Basel").findUnique();
        bern.addOwner(bob);
        basel.addOwner(alice);
        Contact.create("Mr.", "Test1", "fTest1", "email1@g.ch", "street", "App1", "App2", "3012", "city", "country", "phone", "Bern", "ja", "Blabla", "Deutsch");
        Contact.create("Ms.", "Test2", "fTest2", "email2@g.ch", "street", "App1", "App2", "3012", "city", "country", "phone", "Bern", "", "Blabla", "Deutsch");
        List<Contact> results = Contact.findInvolvingGroupOwner("bob@gmail.com");
        assertEquals(2, results.size());
        assertEquals("Test1", results.get(0).name);
        List<Contact> resultsEmpty = Contact.findInvolvingGroupOwner("alice@gmail.com");
        assertEquals(0, resultsEmpty.size());
    }
	
	@Test
	public void deleteContact() {
		User admin = new User("admin@test.com", "admin", true);
		admin.save();
		new ContactGroup("Bern", admin).save();
		Contact.create("Mr.", "Test1", "fTest1", "email1@g.ch", "street", "App1", "App2", "3012", "city", "country", "phone", "Bern", "ja", "This is a remark", "Deutsch");
		assertEquals(1, Contact.all().size());
		Contact contactToDelete = Contact.find.where().eq("name", "Test1").findUnique();
		contactToDelete.delete(contactToDelete.id);
		assertEquals(0, Contact.all().size());
	}

}
