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
		new User("admin@test.com", "admin", false).save();
		User admin = User.find.where().eq("email", "admin@test.com").findUnique();
		new ContactGroup("Bern", admin).save();
		ContactGroup cg = ContactGroup.find.where().eq("name", "Bern").findUnique();
		assertNotNull(cg);
		assertEquals("Bern", cg.name);
		assertEquals("admin@test.com", cg.owners.get(0).email);
		new ContactGroup("Basel", admin).save();
		assertEquals(2, ContactGroup.all().size());
    }
	
	
	
	@Test
    public void findContactGroupsInvolving() {
        User bob = new User("bob@gmail.com", "secret", false);
        bob.save();
        new User("jane@gmail.com", "secret", false).save();
        
        ContactGroup.create("Bern", "bob@gmail.com");
        ContactGroup.create("Basel", "jane@gmail.com");

        List<ContactGroup> results = ContactGroup.findInvolvingOwner(bob);
        assertEquals(1, results.size());
        assertEquals("Bern", results.get(0).name);
    }
	
	@Test
    public void createAndRetrieveContact() {
		new User("admin@test.com", "admin", false).save();
		User admin = User.find.where().eq("email", "admin@test.com").findUnique();
		new ContactGroup("Bern", admin).save();
		ContactGroup cg = ContactGroup.find.where().eq("name", "Bern").findUnique();

		new Contact("Doe", "Mark", "mark.doe@test.com", "Test Street", "Test City", "01234567", cg).save();

		Contact contact = Contact.create("Doe", "Joe", "joe.doe@test.com", "Test Street", "Test City", "01234567", "Bern");
		
		Contact con1 = new Contact("Doe", "Hal", "hal.doe@test.com", "Test Street", "Test City", "01234567", cg);
		con1.save();
		assertEquals(3, Contact.all().size());
	}
	
	@Test
    public void findContactsInvolvingGroupOwner() {
		User bob = new User("bob@gmail.com", "secret", false);
        bob.save();
        User alice = new User("alice@gmail.com", "topsecret", false);
        alice.save();

        ContactGroup contactGroup = ContactGroup.create("Bern", "bob@gmail.com");
        ContactGroup contactGroup2 = ContactGroup.create("Basel", "alice@gmail.com");
        
        Contact c1 = Contact.create("Test1", "fTest1", "email1@g.ch", "street", "city", "phone", "Bern");
        Contact c2 = Contact.create("Test2", "fTest2", "email2@g.ch", "street", "city", "phone", "Bern");

        List<Contact> results = Contact.findInvolvingGroupOwner("bob@gmail.com");
        assertEquals(2, results.size());
        assertEquals("Test1", results.get(1).name);
        List<Contact> resultsEmpty = Contact.findInvolvingGroupOwner("alice@gmail.com");
        assertEquals(0, resultsEmpty.size());
    }

}
