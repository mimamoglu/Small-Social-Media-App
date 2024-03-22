import java.util.ArrayList;

public class DataBase{

	private ArrayList<User> User_database;
	
	public DataBase() {
		
	}
	
	public void addUsertoDataBase(User u) {
		User_database.add(u);
	}
	
	public ArrayList<User> getUsers() {
		return User_database;
	}



}
