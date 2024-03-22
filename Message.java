import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class Message {

	ArrayList<String> messages;
	
	public void exactTimes() {
		messages= new ArrayList<String>();
	}
	public void shareMessage(String[] splited_input, File profiles, File timelines, ArrayList<User> User_database) {
		int check3 = 0;
		String tweet = "";
		for (int i = 2; i < splited_input.length; i++) { // Get tweet
			if (i == splited_input.length - 1)
				tweet += splited_input[i];
			else
				tweet += splited_input[i] + " ";
		}
		System.out.println(splited_input[1]+" shared the following message: "+tweet);
		LocalDateTime now = LocalDateTime.now();  
		DateTimeFormatter save = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		messages.add(save.format(now)+" "+ splited_input[1] + " "+ tweet);
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		
		
		// Firstly add tweet to user profile file who is sharing
		File sharing_user_profile = new File(profiles, splited_input[1]+".txt");
			String lines4 = "";
			Scanner s4;
			try {
				s4 = new Scanner(sharing_user_profile);
				while (s4.hasNextLine()) {
					if (check3 == 2) {
						lines4 += dtf.format(now)+ " " + tweet + "\n";
					}
					lines4 += s4.nextLine() + "\n";
					check3++;
				}
				if (check3 == 2) {	//If there is no tweet in user's profile
					lines4 += dtf.format(now)+ " " + tweet + "\n";
				}
				s4.close();
				try {
					FileWriter fw2 = new FileWriter(sharing_user_profile, false);
					fw2.write(lines4);
					fw2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		
		// Check the followers and send this.tweet to them
		for(int i = 0; i < User_database.size(); i++) {
			if (User_database.get(i).getUserName().equals(splited_input[1])) { 
				for(int k = 0; User_database.get(i).getFallowers() != null && k < User_database.get(i).getFallowers().size(); k++) {
					File follower = new File(timelines, User_database.get(i).getFallowers().get(k).getUserName()+".txt");
					if (follower.length() == 0) {	//If follower's timeline is empty
						try {
							FileWriter fw = new FileWriter(follower, true);
							fw.write(dtf.format(now)+" "+ splited_input[1] + " "+ tweet+"\n");
							fw.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else {
						String lines2 = "";
						try {
							Scanner s2 = new Scanner(follower);
							while (s2.hasNextLine()) {
								lines2 += s2.nextLine() + "\n";	
							}
							lines2 = dtf.format(now)+ " " + splited_input[1] + " " + tweet + "\n" + lines2;
							s2.close();
							try {
								FileWriter fw3 = new FileWriter(follower, false);
								fw3.write(lines2);
								fw3.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
						
				}
			}
		}
	}
	
	public void editMessage(String[] splited_input, File profiles, File timelines, ArrayList<User> User_database) {
		String text = "", oldMessage = "", newMessage = "", line = "", line2 = "", profile_line = "", tweet, timeline = "";
		int check = 0, check2 = 0, check3 = 0, check4 = 0;
		// Get old message and new message
		for (int i = 0; i < splited_input.length; i++) {
			if (i >= 2) {
				if (i == splited_input.length-1) {
					text += splited_input[i];
				}
				else
					text += splited_input[i] + " ";
			}
		}
		String[] tmp = text.split(" ");
		for (int i = 0; i < tmp.length; i++) {
			if (tmp[i].equals("|")) {
				check3++;
			}
			if(i != Arrays.asList(tmp).indexOf("|")) {
				if(check3 == 1) {
					if (i == tmp.length-1)
						newMessage += tmp[i];
					else
						newMessage += tmp[i] + " ";
				}else {
					if(i+1 == Arrays.asList(tmp).indexOf("|"))
						oldMessage += tmp[i];
					else
						oldMessage += tmp[i] + " ";
				}
			}
		}
		// Update user profile
		File user_profile = new File(profiles, splited_input[1]+".txt");
		Scanner s;
		try {
			s = new Scanner(user_profile);
			while (s.hasNextLine()) {
				check++;
				if(check >= 3) {
					line = s.nextLine();
					String[] tmp2 = line.split(" ");
					tweet = "";
					for (int i = 0; i < tmp2.length; i++) {
						if (i >= 2) {
							if (i == tmp2.length-1)
								tweet += tmp2[i];
							else
								tweet += tmp2[i] + " ";
						}
					}
					if (tweet.equals(oldMessage) && check2 == 0) {
						line = tmp2[0] + " " + tmp2[1] + " " + newMessage;
						check2++; // This is for if we already edited the message, we don't have to edit message again even though there is a same message; we only edit
					}			  // top one
					profile_line += line + "\n";
				}
				else
					profile_line += s.nextLine() + "\n";
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//Overwriting the profile file
		if (check2 == 1) {
			try {
				FileWriter fw = new FileWriter(user_profile, false);
				fw.write(profile_line);
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// If we found the following tweet in user's profile and edited then we can change follower's timeline
			//Update timelines who following the user
			for(int i = 0; i < User_database.size(); i++) {
				if (User_database.get(i).getUserName().equals(splited_input[1])) { // User
					for(int k = 0; User_database.get(i).getFallowers() != null && k < User_database.get(i).getFallowers().size(); k++) {
						File follower = new File(timelines, User_database.get(i).getFallowers().get(k).getUserName()+".txt");
						//Search user's tweets in his/her followers timeline and edit
						Scanner s2;
						try {
							s2 = new Scanner(follower);
							while (s2.hasNextLine()) {
								line2 = s2.nextLine();
								String[] tmp5 = line2.split(" ");
								tweet = "";
								for (int y = 0; y < tmp5.length; y++) {
									if (y >= 3) {
										if (y == tmp5.length-1)
											tweet += tmp5[y];
										else
											tweet += tmp5[y] + " ";
									}
								}
								if (tweet.equals(oldMessage) && check4 == 0) {
									line2 = tmp5[0] + " " + tmp5[1] + " " + newMessage;
									check4++; // This is for if we already edited the message, we don't have to edit message again even though there is a same message; we only edit
								}			  // top one
								timeline += line2 + "\n";
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						
						//Overwriting follower's timeline
						try {
							FileWriter fw2 = new FileWriter(follower, false);
							fw2.write(timeline);
							fw2.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}