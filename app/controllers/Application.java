package controllers;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import models.Contact;
import models.ContactGroup;
import models.User;

import org.apache.commons.io.FileUtils;

import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import util.pdf.PDF;
import utils.PDFiText;
import utils.PoiExcelFileReader;

public class Application extends Controller {

	private static final User ANONYMOUS = new User("anon@ymous.com", "nopass",
			false);

	static Form<Contact> contactForm = Form.form(Contact.class);
	static Form<User> userForm = Form.form(User.class);
	static Form<ContactGroup> contactGroupForm = Form.form(ContactGroup.class);

	public static Result login() {
		return ok(views.html.login.render(Form.form(Login.class),
				getCurrentUser()));
	}

	public static Result authenticate() {
		Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
		if (loginForm.hasErrors()) {
			return badRequest(views.html.login.render(loginForm,
					getCurrentUser()));
		} else {
			session().clear();
			session("email", loginForm.get().email);
			flash("success", "Sie haben sich erfolgreich eingeloggt als: "
					+ loginForm.get().email);
			return redirect(routes.Application.contacts());
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result logout() {
		session().clear();
		flash("success", "Sie sind nun ausgeloggt.");
		return redirect(routes.Application.login());
	}

	//	@Security.Authenticated(Secured.class)
	//	public static Result index() {
	//		return redirect(routes.Application.contacts());
	//	}

	/**
	 * Lists all the contacts, where the logged in user is owner of the
	 * corresponding contact group
	 */
	@Security.Authenticated(Secured.class)
	public static Result contacts() {
		User user = getCurrentUser();
		String btn = "all";
		return ok(views.html.index.render(
				Contact.findInvolvingGroupOwner(user.email), contactForm, user,
				btn));
	}

	@Security.Authenticated(Secured.class)
	public static Result editedContacts() {
		User user = getCurrentUser();
		return ok(views.html.editedContacts.render(
				Contact.findEditedContacts(), user));
	}

	/**
	 * Lists all the contacts of the specifed group
	 */
	@Security.Authenticated(Secured.class)
	public static Result filteredContactsBy(String groupname) {
		User user = getCurrentUser();
		if (!user.isAdmin)
			return redirect(routes.Application.contacts());
		String btn = groupname;
		return ok(views.html.index.render(Contact.findByGroupname(groupname),
				contactForm, user, btn));
	}

	/**
	 * Lists all the contacts with yearbook subscription
	 */
	@Security.Authenticated(Secured.class)
	public static Result filteredContactsWithYearbookSubscription() {
		System.out.println("Method: filteredContactsWithYearbookSubscription()");
		User user = getCurrentUser();
		if (!user.isAdmin)
			return redirect(routes.Application.contacts());
		String btn = "yearbook";
		return ok(views.html.index.render(Contact.withYearbookSubscription(),
				contactForm, user, btn));
	}

	/**
	 * Generates a pdf file of all the contacts, where the logged in user is
	 * owner of the corresponding contact group
	 */
	@Security.Authenticated(Secured.class)
	public static Result pdfSummary() {
		User user = getCurrentUser();
		return PDF.ok(views.html.pdfSummary.render(Contact
				.findInvolvingGroupOwner(user.email)));
	}

	@Security.Authenticated(Secured.class)
	public static Result pdfLabels() {
		User user = getCurrentUser();
		PDFiText.generateLabels(Contact.findInvolvingGroupOwner(user.email));
		response().setContentType("application/pdf");
		// response().setHeader("Content-disposition","attachment; filename=labels.pdf");
		return ok(new File("output/labels.pdf"));
	}

	/**
	 * Renders the excel import / export view
	 */
	@Security.Authenticated(Secured.class)
	public static Result excelImportExport() {
		return ok(views.html.excelImportExport.render(getCurrentUser()));
	}

	@Security.Authenticated(Secured.class)
	public static Result upload() {
		User user = getCurrentUser();
		if (!user.isAdmin)
			return redirect(routes.Application.contacts());
		Http.MultipartFormData body = request().body().asMultipartFormData();
		Http.MultipartFormData.FilePart contactfile = body
				.getFile("contactfile");
		if (contactfile != null) {
			String fileName = contactfile.getFilename();
			File file = contactfile.getFile();

			try {
				File f = new File("public/upload/" + fileName);
				if(f.isFile())
					f.delete();
				FileUtils.moveFile(file, new File("public/upload", fileName));
			} catch (IOException ioe) {
				System.out.println("Problem operating on filesystem");
			}

			PoiExcelFileReader.readFile(fileName);
			flash("success", "Datei: " + fileName
					+ " hochgeladen und Kontakte importiert");
			return redirect(routes.Application.contacts());
		} else {
			flash("error", "Ein Fehler ist aufgetreten, bitte versuchen sie es erneut");
			return redirect(routes.Application.contacts());
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result download() {
		User user = getCurrentUser();
		String filename = PoiExcelFileReader.writeFile(Contact.findInvolvingGroupOwner(user.email));
		response().setContentType("application/x-download");
		String headerName = "Content-disposition";
		String headerValue = "attachment; filename=" + filename;
		response().setHeader(headerName, headerValue); 
		return ok(new File(filename));
		//return redirect(routes.Application.contacts());
	}

	private static User getCurrentUser() {
		String currentId = request().username();
		User current;
		if (currentId == null)
			current = ANONYMOUS;
		else {
			current = User.find.byId(request().username());
		}
		return current;
	}

	// TODO change to use Contact.create() method
	@Security.Authenticated(Secured.class)
	public static Result newContact() {

		Form<Contact> filledForm = contactForm.bindFromRequest();

		String name = filledForm.data().get("name");
		String firstName = filledForm.data().get("firstName");
		String title = filledForm.data().get("title");
		String email = filledForm.data().get("email");
		String street = filledForm.data().get("street");
		String appendix1 = filledForm.data().get("appendix1");
		String appendix2 = filledForm.data().get("appendix2");
		String zipcode = filledForm.data().get("zipcode");
		String country = filledForm.data().get("country");
		String city = filledForm.data().get("city");
		String phone = filledForm.data().get("phone");
		String yearbook = filledForm.data().get("yearbookSubscription");
		String memberCategory = filledForm.data().get("memberCategory");
		String membershipSince = filledForm.data().get("membershipSince");
		String remarks = filledForm.data().get("remarks");
		String preferredLanguage = filledForm.data().get("preferredLanguage");

		Contact newContact = new Contact();
		newContact.name = name;
		newContact.firstName = firstName;
		newContact.title = title;
		newContact.email = email;
		newContact.street = street;
		newContact.appendix1 = appendix1;
		newContact.appendix2 = appendix2;
		newContact.zipcode = zipcode;
		newContact.city = city;
		newContact.country = country;
		newContact.phone = phone;
		newContact.remarks = remarks;
		newContact.preferredLanguage = preferredLanguage;

		if (yearbook.equals("true"))
			newContact.yearbookSubscription = true;
		newContact.memberCategory = memberCategory;

		for (int j = 0; j < ContactGroup.options().size(); j++) {
			String item = "belongsTo[" + j + "]";
			if (filledForm.data().get(item) != null) {
				ContactGroup cg = ContactGroup.find.byId((long) Integer
						.parseInt(filledForm.data().get(item)));
				newContact.belongsTo.add(cg);
			}
		}

		if (newContact.belongsTo.isEmpty())
			filledForm.reject("belongsTo[]", "Keine Sektion ausgewählt");
		
		//TODO Check fields for errors
		
		if(filledForm.hasErrors())
			System.out.println(filledForm.errors().toString());
		
		newContact.membershipSince = membershipSince;
		newContact.createdAt = new Timestamp(new Date().getTime());
		newContact.lastEditedAt = newContact.createdAt;
		newContact.save();
		flash("success", "Kontakt " + newContact + " erstellt und gespeichert.");
		return redirect(routes.Application.contacts());
	}

	/**
	 * Deletes the contact by first removing contact from corresponding contact
	 * groups and then deleting the contact
	 */
	@Security.Authenticated(Secured.class)
	public static Result deleteContact(Long id) {
		Contact.delete(id);
		return redirect(routes.Application.contacts());
	}

	@Security.Authenticated(Secured.class)
	public static Result updateContact(Long id) {

		Form<Contact> updatedForm = contactForm.bindFromRequest();

		String name = updatedForm.data().get("name");
		String firstName = updatedForm.data().get("firstName");
		String title = updatedForm.data().get("title");
		String email = updatedForm.data().get("email");
		String street = updatedForm.data().get("street");
		String appendix1 = updatedForm.data().get("appendix1");
		String appendix2 = updatedForm.data().get("appendix2");
		String zipcode = updatedForm.data().get("zipcode");
		String country = updatedForm.data().get("country");
		String city = updatedForm.data().get("city");
		String phone = updatedForm.data().get("phone");
		String memberCategory = updatedForm.data().get("memberCategory");
		String membershipSince = updatedForm.data().get("membershipSince");
		String yearbook = updatedForm.data().get("yearbookSubscription");

		String contactGroup = "";
		for (int j = 0; j < ContactGroup.options().size(); j++) {
			String item = "belongsTo[" + j + "]";
			if (updatedForm.data().get(item) != null) {
				if(j>0)
					contactGroup += "/";
				contactGroup += updatedForm.data().get(item);
			}
		}

		if (contactGroup.isEmpty())
			updatedForm.reject("belongsTo[]", "Keine Sektion ausgewählt");
		
		Contact.find.byId(id).update(title, name, firstName, email, street, appendix1, appendix2, zipcode, city, country, phone, membershipSince, memberCategory, yearbook, contactGroup);
		flash("success", "Kontakt bearbeitet und gespeichert.");
		return redirect(routes.Application.contacts());
	}

	@Security.Authenticated(Secured.class)
	public static Result viewContact(Long id) {
		String username = request().username();
		User current = User.find.byId(username);
		return ok(views.html.contactView.render(Contact.find.byId(id), current));
	}

	@Security.Authenticated(Secured.class)
	public static Result editContact(Long id) {
		Contact contact = Contact.find.byId(id);
		contactForm = contactForm.fill(contact);
		List<ContactGroup> belongingToGroups = contact.belongsTo;
		List<ContactGroup> allGroups = ContactGroup.find.all();
		return ok(views.html.edit
				.render(contactForm, contact, getCurrentUser(), allGroups, belongingToGroups));
	}

	@Security.Authenticated(Secured.class)
	public static Result add() {
		return ok(views.html.add.render(contactForm, getCurrentUser(),
				ContactGroup.all()));
	}

	@Security.Authenticated(Secured.class)
	public static Result addUser() {
		if (!getCurrentUser().isAdmin)
			return redirect(routes.Application.contacts());
		Form<User> userForm = Form.form(User.class);
		return ok(views.html.addUser.render(userForm, getCurrentUser(),
				User.find.all()));
	}

	@Security.Authenticated(Secured.class)
	public static Result newUser() {
		User user = getCurrentUser();
		if (!user.isAdmin)
			return redirect(routes.Application.contacts());
		Form<User> filledForm = userForm.bindFromRequest();

		if(!filledForm.field("password").valueOr("").isEmpty()) {
			if(!filledForm.field("password").valueOr("").equals(filledForm.field("repeatPassword").value())) {
				filledForm.reject("repeatPassword", "Passwörter stimmen nicht überein");
			}
		}
		
        if(!filledForm.hasErrors()) {
            if(userAlreadyExists(filledForm.get().email)) {
                filledForm.reject("email", "Diese Emailadresse ist bereits vergeben");
            }
        }

		if (filledForm.hasErrors()) {
			flash("error", "Bitte korrigieren sie ihre Eingaben!");
			return badRequest(views.html.addUser.render(filledForm,
					getCurrentUser(), User.find.all()));
		} else {
			User.create(filledForm.get());
			flash("success", "Benutzer " + filledForm.get().email
					+ " erstellt.");
			return redirect(routes.Application.contacts());
		}
	}

	private static boolean userAlreadyExists(String email) {
		return User.find.byId(email) != null;
	}

	@Security.Authenticated(Secured.class)
	public static Result addContactGroup() {
		if (!getCurrentUser().isAdmin)
			return redirect(routes.Application.contacts());
		Form<ContactGroup> contactGroupForm = Form.form(ContactGroup.class);
		return ok(views.html.addContactGroup.render(contactGroupForm,
				getCurrentUser(), ContactGroup.find.all()));
	}

	// TODO Automatic binding of owner is still missing at the moment
	@Security.Authenticated(Secured.class)
	public static Result newContactGroup() {
		User user = getCurrentUser();
		if (!user.isAdmin)
			return redirect(routes.Application.contacts());

		Form<ContactGroup> filledForm = contactGroupForm.bindFromRequest();

		if (filledForm.hasErrors()) {
			System.out.println(filledForm.errors().toString());
			flash("error", "Bitte korrigieren sie ihre Eingaben!");
			return badRequest(views.html.addContactGroup.render(filledForm,
					getCurrentUser(), ContactGroup.find.all()));
		} else {

			ContactGroup.create(filledForm.get());
			flash("success", "Kontaktgruppe " + filledForm.get().name
					+ " erstellt.");
			if (User.findByEmail(request().username()).isAdmin) {
				// manual binding of owner
				ContactGroup.find.ref(filledForm.get().id).addOwner(
						User.findByEmail(request().username()));
			}
			return redirect(routes.Application.contacts());
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result addOwner(Long id) {
		DynamicForm form = Form.form().bindFromRequest();
		Collection<String> newOwners = form.data().values();
		ContactGroup group = ContactGroup.find.byId(id);

		Iterator it = newOwners.iterator();
		while (it.hasNext()) {
			String userEmail = (String) it.next();
			group.addOwner(User.findByEmail(userEmail));
		}

		flash("success", newOwners.toString()
				+ " ist / sind nun Besitzer der Gruppe " + group.name);
		return ok(views.html.addContactGroup.render(contactGroupForm,
				getCurrentUser(), ContactGroup.find.all()));
	}

	@Security.Authenticated(Secured.class)
	public static Result showContactGroup(Long id) {
		ContactGroup cg = ContactGroup.find.ref(id);
		List<Contact> groupContacts = cg.contacts;
		List<User> groupOwners = cg.owners;
		List<User> allUsers = User.find.all();
		return ok(views.html.contactGroupView.render(cg, groupContacts,
				groupOwners, allUsers, getCurrentUser()));
	}

	public static class Login {

		public String email;
		public String password;

		public String validate() {
			if (User.authenticate(email, password) == null) {
				return "Invalid user or password";
			}
			return null;
		}
	}
}