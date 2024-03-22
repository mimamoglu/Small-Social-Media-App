import java.util.ArrayList;
public class User{

	private String userName;
	private String biography;
	private ArrayList<User> fallowers = new ArrayList<User>();
	public String[] Fallowers;
	
	public User(String uN, String Bio) {
		userName = uN;
		biography = Bio;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getBiography() {
		return biography;
	}

	public void addFallower(User u) {
		fallowers.add(u);
	}

	public ArrayList<User> getFallowers() {
		return fallowers;
	}

}
