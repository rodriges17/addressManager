package models;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.data.Form;
import play.data.format.Formats;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Contact extends Model implements Comparable<Contact> {

	@Id
	public Long id;

	@Required
	public String name;

	@Required
	public String firstName;

	public String title;

	@Email
	@Pattern(value = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
			+ "[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
			+ "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")
	public String email;

	public String phone;

	public String street;

	public String appendix1;

	public String appendix2;

	@Required
	public String zipcode;

	@Required
	public String city;

	public String country;

	public Date createdAt;

	public Date lastEditedAt;

	public boolean isEdited = false;

	@Formats.DateTime(pattern = "dd.MM.yyyy")
	public String membershipSince;

	public String memberCategory;

	public boolean yearbookSubscription;
	
	public String remarks;
	
	public String preferredLanguage;

	@ManyToMany(cascade = CascadeType.PERSIST, mappedBy = "contacts")
	public List<ContactGroup> belongsTo = new LinkedList<ContactGroup>();

	public static Finder<Long, Contact> find = new Finder<Long, Contact>(
			Long.class, Contact.class);

	public Contact() {
	};

	public Contact(String title, String name, String firstName, String email,
			String street, String appendix1, String appendix2, String zipcode,
			String city, String country, String phone, ContactGroup belongsTo,
			boolean yearbookSubscription, String remarks, String preferredLanguage) {
		this.title = title;
		this.name = name;
		this.firstName = firstName;
		this.email = email;
		this.street = street;
		this.appendix1 = appendix1;
		this.appendix2 = appendix2;
		this.zipcode = zipcode;
		this.city = city;
		this.country = country;
		this.phone = phone;
		this.createdAt = new Date();
		this.lastEditedAt = this.createdAt;
		this.save();
		// register contact in specified contact group
		belongsTo.addContact(this);
		belongsTo.save();
		this.belongsTo.add(belongsTo);
		this.yearbookSubscription = yearbookSubscription;
		this.remarks = remarks;
		this.preferredLanguage = preferredLanguage;
	}

	public Contact(String title, String name, String firstName, String email,
			String street, String appendix1, String appendix2, String zipcode,
			String city, String country, String phone,
			List<ContactGroup> belongingContactGroups,
			boolean yearbookSubscription, String remarks, String preferredLanguage) {
		this.title = title;
		this.name = name;
		this.firstName = firstName;
		this.email = email;
		this.street = street;
		this.appendix1 = appendix1;
		this.appendix1 = appendix2;
		this.zipcode = zipcode;
		this.city = city;
		this.country = country;
		this.phone = phone;
		this.createdAt = new Date();
		this.lastEditedAt = this.createdAt;
		this.save();
		// register contact in specified contact groups
		for (int i = 0; i < belongingContactGroups.size(); i++) {
			belongingContactGroups.get(i).addContact(this);
			belongingContactGroups.get(i).save();
			this.belongsTo.add(belongingContactGroups.get(i));

		}
		this.yearbookSubscription = yearbookSubscription;
		this.remarks = remarks;
		this.preferredLanguage = preferredLanguage;
	}

	public static Contact create(String title, String name, String firstName,
			String email, String street, String appendix1, String appendix2,
			String zipcode, String city, String country, String phone, String belongsTo,
			String yearbook, String remarks, String preferredLanguage) {
		//System.out.println(belongsTo);
		boolean yearbookSubscription = false;
		if (yearbook.contains("ja") || yearbook.contains("Ja"))
			yearbookSubscription = true;
		// case: contact belongs to more than 1 contact group
		if (belongsTo.contains("/")) {
			String[] belongsToSplit = belongsTo.split("/");
			List<ContactGroup> belongingContactGroups = new LinkedList<ContactGroup>();
			for (int i = 0; i < belongsToSplit.length; i++) {
				ContactGroup cg = ContactGroup.find.where()
						.eq("name", belongsToSplit[i]).findUnique();
				belongingContactGroups.add(cg);
			}
			Contact contact = new Contact(title, name, firstName, email,
					street, appendix1, appendix2, zipcode, city, country, phone,
					belongingContactGroups, yearbookSubscription, remarks, preferredLanguage);
			contact.save();
			return contact;
		}
		// case: contact belongs to only 1 contact group
		else {
			ContactGroup cg = ContactGroup.find.where().eq("name", belongsTo)
					.findUnique();
			Contact contact = new Contact(title, name, firstName, email,
					street, appendix1, appendix2, zipcode, city, country, phone, cg,
					yearbookSubscription, remarks, preferredLanguage);
			contact.save();
			return contact;
		}
	}

	/**
	 * Lists all the contacts alphabetically sorted using Comparable interface.
	 */
	public static List<Contact> all() {
		List<Contact> all = find.all();
		Collections.sort(all);
		return all;
	}

	public static List<Contact> findInvolvingGroupOwner(String user) {
		return find.fetch("belongsTo").where()
				.eq("belongsTo.owners.email", user).orderBy("name asc")
				.findList();
	}

	public List<Contact> ofGroup(String groupName) {
		return find.where().eq(groupName, belongsTo).orderBy("name asc")
				.findList();
	}

	public static void delete(Long id) {
		Contact toDelete = find.ref(id);
		for (int i = 0; i < toDelete.belongsTo.size(); i++) {
			ContactGroup cg = toDelete.belongsTo.get(i);
			cg.contacts.remove(toDelete);
			cg.save();
		}
		toDelete.update();
		toDelete.delete();
	}

	public void update(Form<Contact> updatedForm) {
		Contact updatedSource = updatedForm.get();
		this.name = updatedSource.name;
		this.firstName = updatedSource.firstName;
		this.title = updatedSource.title;
		this.email = updatedSource.email;
		this.phone = updatedSource.phone;
		this.street = updatedSource.street;
		this.city = updatedSource.city;
		this.country = updatedSource.country;
		this.belongsTo = updatedSource.belongsTo;
		this.lastEditedAt = new Date();
		this.isEdited = true;
		this.membershipSince = updatedSource.membershipSince;
		this.memberCategory = updatedSource.memberCategory;
		this.yearbookSubscription = updatedSource.yearbookSubscription;
		this.save();
	}
	
	public void update(String title, String name, String firstName,
			String email, String street, String appendix1, String appendix2,
			String zipcode, String city, String country, String phone, String membershipSince, 
			String memberCategory, String yearbook, String belongsTo) {
		
		boolean yearbookSubscription = false;
		if (yearbook.contains("true"))
			yearbookSubscription = true;
		
		List<ContactGroup> belongingContactGroups = new LinkedList<ContactGroup>();
		// case: contact belongs to more than 1 contact group
		if (belongsTo.contains("/")) {
			String[] belongsToSplit = belongsTo.split("/");
			for (int i = 0; i < belongsToSplit.length; i++) {
				
				ContactGroup cg = ContactGroup.find.where()
						.eq("id", belongsToSplit[i]).findUnique();
				if(cg!=null)
				belongingContactGroups.add(cg);
			}
		} else {
			if(!belongsTo.isEmpty()) {
			ContactGroup cg = ContactGroup.find.where()
					.eq("id", belongsTo).findUnique();
			if(cg!=null)
				belongingContactGroups.add(cg);
			}
		}
		
		this.title = title;
		this.name = name;
		this.firstName = firstName;
		this.email = email;
		this.street = street;
		this.appendix1 = appendix1;
		this.appendix2 = appendix2;
		this.zipcode = zipcode;
		this.city = city;
		this.country = country;
		this.phone = phone;
		this.memberCategory = memberCategory;
		this.yearbookSubscription = yearbookSubscription;
		this.isEdited = true;
		this.lastEditedAt = new Date();
		this.belongsTo = belongingContactGroups;	
		this.save();		
	}

	public static Contact create(Contact contact) {
		contact.save();
		return contact;
	}

	public String toString() {
		return "Name: " + name + ", First Name: " + firstName + ", Street: "
				+ street + ", City: " + city + ", Country: " + country
				+ ", Email: " + email + ", Phone: " + phone + ", Group: "
				+ belongsTo;
	}

	public static List<Contact> findEditedContacts() {
		return find.where().eq("isEdited", Boolean.TRUE).orderBy("name asc").findList();
	}

	public static List<Contact> findByGroupname(String groupname) {
		if (groupname.equals("StGallenZurich"))
			groupname = "St. Gallen-Zürich";
		if (groupname.equals("zurich"))
			groupname = "Zürich";
		List<Contact> all = all();
		List<Contact> result = new LinkedList<Contact>();
		for (Contact contact : all) {
			for (int i = 0; i < contact.belongsTo.size(); i++) {
				if (contact.belongsTo.get(i).name.equals(groupname))
					result.add(contact);
			}
		}
		//List<Contact> sortedList = new LinkedList<Contact>(result);
		//Collections.sort(sortedList, new ContactComparator());
		return result;
	}

	public static List<Contact> withYearbookSubscription() {
		return find.where().eq("yearbookSubscription", Boolean.TRUE).orderBy("name asc").findList();
	}

	public String belongsTo() {
		String result = "";
		for (int i = 0; i < belongsTo.size(); i++) {
			result += belongsTo.get(i).toString();
			result += " ";
		}
		return result;
	}

	public static boolean alreadyExists(String name, String firstName,
			String street, String city) {
		Contact result = Contact.findByNameFirstNameStreetCity(name, firstName, street, city);
		return result != null;
	}

	private static Contact findByNameFirstNameStreetCity(String name,
			String firstName, String street, String city) {
		return find.where().eq("name", name)
		.eq("firstName", firstName)
		.eq("street", street)
		.eq("city", city).findUnique();
	}

	@Override
	public int compareTo(Contact o) {
		if(this.name.equals(o.name)) {
			if(this.firstName.equals(o.firstName)) {
				return this.city.compareTo(o.city);
			} else {
				return this.firstName.compareTo(o.firstName);
			} 
		} else {
			return this.name.compareTo(o.name);
		}
	}

}
