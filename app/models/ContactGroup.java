package models;

import play.data.Form;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.*;
import java.util.List.*;

@Entity
public class ContactGroup extends Model {
	
	@Id	
	public String name;
	
    @ManyToMany
	public List<User> owners = new LinkedList<User>();
	
//    @OneToMany(cascade = CascadeType.PERSIST)
//	public List<Contact> contacts = new LinkedList<Contact>();;
	
	public static Finder<String, ContactGroup> find = new Finder(String.class, ContactGroup.class);
	
	public ContactGroup(String name, User owner) {
		this.name = name;
		this.owners.add(owner);
	}

	public static ContactGroup create(String name, String owner) {
		ContactGroup contactGroup = new ContactGroup(name, User.find.byId(owner));
		contactGroup.save();
		contactGroup.saveManyToManyAssociations("owners");
		return contactGroup;
	}
	
	public static List<ContactGroup> all() {
		return find.all();
	}
	
	public void delete(String id) {
		find.ref(id).delete();
	}
	
}
