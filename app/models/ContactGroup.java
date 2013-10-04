package models;

import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.validation.*;

@Entity
public class ContactGroup extends Model {
	
	@Id
	public Long id;

	@Constraints.Required
	public String name;
	
    @ManyToMany
	public List<User> owners = new LinkedList<User>();
	
	@ManyToMany(cascade=CascadeType.PERSIST)
	public List<Contact> contacts = new LinkedList<Contact>();
	
	public static Finder<Long, ContactGroup> find = new Finder<Long,ContactGroup>(Long.class, ContactGroup.class);
	
	public ContactGroup(String name, User owner) {
		this.name = name;
		this.owners.add(owner);
	}

	public static ContactGroup create(String name, String owner) {
		ContactGroup contactGroup = new ContactGroup(name, User.find.ref(owner));
		contactGroup.save();
		contactGroup.saveManyToManyAssociations("owners");
		return contactGroup;
	}
	
	public static List<ContactGroup> all() {
		return find.all();
	}
	
	public static List<ContactGroup> findInvolvingOwner(User owner) {
		return find.where().eq("owners.email", owner.email).findList();
	}
	
	public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(ContactGroup cg: ContactGroup.find.orderBy("name").findList()) {
            options.put(cg.id.toString(), cg.name);
        }
        return options;
    }
	
	public void delete(Long id) {
		find.ref(id).delete();
	}
	
	public static void create(ContactGroup contactGroup) {
		contactGroup.save();
	}
	
	public void addOwner(User owner) {
		if(!this.owners.contains(owner)){
			this.owners.add(owner);
			this.save();
		}	
	}
	
	public String toString() {
		return name;
	}
	
	public String ownersToString() {
		String ownersList = "";
		for(User owner : owners) {
			ownersList += owner.email;
			ownersList += " ";
		}
		return ownersList;
	}

	public void addContact(Contact contact) {
		this.contacts.add(contact);	
	}
	
}
