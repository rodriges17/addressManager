package models;

import java.util.*;

public class MemberCategories {
	
	public static List<String> list() {
        List<String> all = new ArrayList<String>();
        all.add("Einzel");
        all.add("Student");
        all.add("Institution");
        all.add("Familie");
        all.add("Nichtmitglied");
        return all;
	}

}
