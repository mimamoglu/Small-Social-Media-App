import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
public class Main {

	
	public static void main(String[] args) {
		
		Scanner key = new Scanner(System.in);
		String input = "";
		int q = 0;
		int check5 = 0;
		ArrayList<String> commands = new ArrayList<String>();
		boolean manual_commands = !input.equals("Exit");
		boolean loop;
		
		if(args.length>0) {
			//Automatic code
			check5 = 1;
			File commands_txt = new File(args[0]);
			Scanner read_commands;
			try {
				read_commands = new Scanner(commands_txt);
				while(read_commands.hasNextLine()) {
					commands.add(read_commands.nextLine());
				}
			} catch (FileNotFoundException e2) {
				e2.printStackTrace();
			}
			boolean auto_commands = q < commands.size() && !commands.get(q).equals("Exit");
			loop = auto_commands;
		}else
			loop = manual_commands;
		
		File profiles = new File("profiles"); // Profile directory which keeps user's nickname, their biography and tweets
		profiles.mkdirs();
		File timelines = new File("timelines"); // Timeline directory which keeps followed user's tweets
		timelines.mkdirs();
		ArrayList<User> User_database = new ArrayList<User>();
		Message m = new Message();
		m.exactTimes();
		outsideloop:
		while (loop) {	// Until the user exits code continue doing processes and taking input
			
			if(check5 == 1) { //check5 for if it's an automatic code then we use commands ArrayList to enter inputs
				if(q == commands.size()) {
					break outsideloop;
				}else {
					System.out.println("Please type your command");
					input = commands.get(q);
				}
			}
			else {
				System.out.println("Please type your command");
				input = key.nextLine();
			}
			String[] splited_input = input.split(" ");
			
			if (splited_input[0].equals("Create")) {
				//Check if userName already exists
				String[] flist = profiles.list();
				if (flist == null || !(checkIfUserExists(User_database, splited_input[1]))) {
					String bio = "";
					for (int i = 2; i < splited_input.length; i++) { // Get bio 
						if (i == splited_input.length - 1)
							bio += splited_input[i];
						else
							bio += splited_input[i] + " ";
					}
					User new_user = new User(splited_input[1], bio); //Add a new user
					User_database.add(new_user); //Add new user to database
					File N_user = new File(profiles, splited_input[1]+".txt"); //Create a new user under profiles directory
					try {
						FileWriter N_user_data = new FileWriter(N_user, false);
						N_user_data.write("User Name: " + splited_input[1] + "\n" + "Bio: " + bio); //Write user data in text file
						N_user_data.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					File N_user_timeline = new File(timelines, splited_input[1]+".txt"); //Create a new user under timelines directory
					try {
						N_user_timeline.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println(new_user.getUserName() + ".txt has been created!");
				}
				else
					System.out.println("Already existing user name");
			} // create close
		
			if (splited_input[0].equals("Follow")) {
				int check3 = 0; // Control mechanism for code when user1 already following user2
				if (checkIfUserExists(User_database, splited_input[1]) && checkIfUserExists(User_database, splited_input[2])) { //Check if both users exist
					for(int i = 0; i < User_database.size(); i++) {
						if (User_database.get(i).getUserName().equals(splited_input[2])) { // User_database.get(i) gives us informations about user2
							for(int k = 0; k < User_database.size(); k++) {
								if (User_database.get(k).getUserName().equals(splited_input[1])) { // User_database.get(k) gives us informations about user1.
									if (User_database.get(i).getFallowers() != null  && User_database.get(i).getFallowers().contains(User_database.get(k))) { // If already following
										System.out.println("Already following");
										check3 = 1;
									}else {
										User_database.get(i).addFallower(User_database.get(k)); // This is for whenever user2 shares a tweet user1 will get it.
										System.out.println(splited_input[1] + " is following " + splited_input[2]);
									}
								}
							}
						}
					}
					if (check3 == 0) {
						// Read user2's tweet in it's profile and save somewhere.
						String line = "", timeline1 = "";
						int check2 = 0; // First 2 line of profiles are for information but not tweet therefor we need a control mechanism.
						File user2_profile = new File(profiles, splited_input[2]+".txt");
						Scanner s;
						try {
							s = new Scanner(user2_profile);
							while (s.hasNextLine()) {
								check2++;
								if(check2 >= 3) {
									line = s.nextLine() + "\n";
									timeline1 += line.substring(0,16) + " " + splited_input[2] + " " + line.substring(17,line.length());
								}
								else
									s.nextLine();
							}
							s.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						File user1_timeline = new File(timelines, splited_input[1]+".txt");
						// Add user2's tweets to user1's timeline
						try {
							FileWriter fw = new FileWriter(user1_timeline, true);
							fw.write(timeline1);
							fw.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						//Sorting user1's timeline with the new tweets as for date
						try {
							sortTweetsByDate(user1_timeline, m);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
						
					}
				}else
					System.out.println("At least one of the users does not exist");
			} // follow close
		
			if (splited_input[0].equals("Share")) {
				if (checkIfUserExists(User_database, splited_input[1])) { //Check if user exist
					m.shareMessage(splited_input, profiles, timelines, User_database);
				}else 
					System.out.println("User does not exists");
			} // share close
		
		
		
			if (splited_input[0].equals("Unfollow")) {
				String timeline = "";
				if (checkIfUserExists(User_database, splited_input[1]) && checkIfUserExists(User_database, splited_input[2])) {	// Check if both users exist
					if (isFollower(User_database, splited_input[1], splited_input[2])) {
						unfollow(User_database, splited_input[1], splited_input[2]);
						// Delete all tweets in user1's timeline that belongs to user2
						File user1_timeline = new File(timelines, splited_input[1]+".txt");
						Scanner s2;
						try {
							s2 = new Scanner(user1_timeline);
							while (s2.hasNextLine()) {
								String tmp = s2.nextLine();				// It's enough to check 3rd word in a line which is information of who shared the tweet
								String[] splited_line = tmp.split(" ");
								if (!splited_line[2].equals(splited_input[2])) { 
									timeline += tmp + "\n";
								}
							}
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
						
						// Overwriting user1's timeline
						try {
							FileWriter fw = new FileWriter(user1_timeline, false);
							fw.write(timeline);
							fw.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.out.println(splited_input[1]+" is unfollowing "+ splited_input[2]);
					}
					else
						System.out.println("Already not following");
				}
				else
					System.out.println("At least one of the users does not exist");
			} // unfollow close
	
			if (splited_input[0].equals("Edit")) {
				if(checkIfUserExists(User_database, splited_input[1])) {
					m.editMessage(splited_input, profiles, timelines, User_database);
				}
				else {
					System.out.println("User does not exists");
				}
			}
			q++;
		}	// while close
		key.close();
	} 	// main close
	
	public static void sortTweetsByDate(File timeline, Message mes) throws ParseException {
		String tmp = "", tweet, user_timeline = "",tweet2, exactDate = "01/01/2022 15:13:53";
		String[] splited_tmp_message = null;
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		ArrayList<Date> dates= new ArrayList<Date>();
		ArrayList<String> dates2 = new ArrayList<String>();
		Map<String,String> map= new HashMap<String,String>();
		Scanner s;
		try {
			s = new Scanner(timeline);
			while (s.hasNextLine()) {
				tweet = "";
				tmp = s.nextLine();
				String[] splited = tmp.split(" ");
				for(int i = 2; i < splited.length; i++) {
					if(i == splited.length-1)
						tweet += splited[i];
					else
						tweet += splited[i]+" ";
				}
				
				System.out.println(tweet);
				for(int j=0; j<mes.messages.size(); j++) {
					tweet2 = "";
					String tmp_message = mes.messages.get(j);
					splited_tmp_message = tmp_message.split(" ");
					for(int t = 2; t<splited_tmp_message.length; t++) {
						if(t == splited_tmp_message.length-1)
							tweet2 += splited_tmp_message[t];
						else
							tweet2 += splited_tmp_message[t]+" ";
					}
					System.out.println(tweet2);
					if(tweet.equals(tweet2)) {
						exactDate = splited_tmp_message[0] + " "+ splited_tmp_message[1]; 
					}
				}
				
				System.out.println(exactDate);
				
				Date date123 = format.parse(exactDate);
				dates.add(date123);
				map.put(exactDate,tweet);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Collections.sort(dates); 
		for(int i = 0; i<dates.size(); i++) {
			dates2.add(format.format(dates.get(i)));
		}
		for(int k = dates2.size()-1; k >= 0; k--) {
			user_timeline += dates2.get(k)+" "+map.get(dates2.get(k))+"\n";
		}
		
		try {
			FileWriter fw6 = new FileWriter(timeline, false);
			fw6.write(user_timeline);
			fw6.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void unfollow(ArrayList<User> database, String user1, String user2) {
		// user1 stops following user2
		for(int i = 0; i < database.size(); i++) {
			if (database.get(i).getUserName().equals(user2)) { // User_database.get(i) gives us informations about user2
				for(int k = 0; k < database.size(); k++) {
					if (database.get(k).getUserName().equals(user1)) { // User_database.get(k) gives us informations about user1.
						database.get(i).getFallowers().remove(database.get(k));
					}
				}
			}
		}
	}
	
	public static boolean isFollower(ArrayList<User> database, String user1, String user2) {
		// Check if user1 is follower of user2
		boolean check = false;
		for(int i = 0; i < database.size(); i++) {
			if (database.get(i).getUserName().equals(user2)) { // User_database.get(i) gives us informations about user2
				for(int k = 0; k < database.size(); k++) {
					if (database.get(k).getUserName().equals(user1)) { // User_database.get(k) gives us informations about user1.
						if (database.get(i).getFallowers() != null  || database.get(i).getFallowers().contains(database.get(k))) { 
							check = true;
						}	
					}	
				}
			}
		}
		return check;
	}
	
	public static void writeTimeline(String user_name, String text, File timelines) {
		File user_timeline = new File(timelines, user_name+".txt");
		try {
			FileWriter fw = new FileWriter(user_timeline, true);
			fw.write(text);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean checkIfUserExists(ArrayList<User> data_base, String user) {
		boolean check = false;
		for(int i = 0;!data_base.isEmpty() && i < data_base.size(); i++) {
			if (data_base.get(i).getUserName().equals(user))
				check = true;
		}
		return check;
	}
}