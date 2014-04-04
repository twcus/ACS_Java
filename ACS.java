/*
 * Thomas Custer
 * twc463
 * Assignment 4
 * Unix Access Control System
 */

import java.io.*;
import java.util.*;

public class ACS {
	//Maps users to their associated group
	private Map<String, String> userList;

	//Maps files to their owners
	private Map<String, String> fileOwners;

	//Maps files to their mode (in a base-10 integer)
	private Map<String, Integer> fileModes;

	//If true, root user has full permissions
	private boolean r;

	/*
	 * Constructs an ACS using userList and fileList
	 * files to generate the user and file maps
	 */
	public ACS(String userList, String fileList, boolean r) throws Exception {
		this.r = r;
		this.userList = new HashMap<String, String>();
		this.fileOwners = new HashMap<String, String>();
		this.fileModes = new HashMap<String, Integer>();
		parseFiles(userList, fileList);
	}

	/*
	 * Parses the userList and fileList files
	 * by adding the users and files to the proper Maps with
	 * proper groups, owners, and modes
	 */
	private void parseFiles(String userList, String fileList) throws IOException {
		BufferedReader ubr = new BufferedReader(new FileReader(userList));
		BufferedReader fbr = new BufferedReader(new FileReader(fileList));
		String line;
		String[] lineSplit;
		int mode;

		//parse userList
		while ((line = ubr.readLine()) != null) {
			lineSplit = line.split(" ");
			this.userList.put(lineSplit[0].toLowerCase(), lineSplit[1].toLowerCase());
		}
		ubr.close();
		//add root user to userList, will only have full permissions if r is false
		this.userList.put("root", "root");

		//parse fileList
		while ((line = fbr.readLine()) != null) {
			lineSplit = line.split(" ");
				//only add file to system if it has a valid owner
				if (this.userList.containsKey(lineSplit[1])) {
				this.fileOwners.put(lineSplit[0].toLowerCase(), lineSplit[1].toLowerCase());
				mode = Integer.parseInt(lineSplit[2], 8);
				this.fileModes.put(lineSplit[0].toLowerCase(), mode);
			}
		}
		fbr.close();
	}

	/*
	 * Processes user input commands and outputs what occurs in
	 * the system after each command
	 */
	public void processCommands() throws Exception {
		Console console = System.console();
		String input, command, user, file;
		String[] inputSplit;

		//Ask user for commands until EXIT command is given
		do {
			//parse the input
			input = console.readLine("Input:\n");
			inputSplit = input.split(" ");
			if (inputSplit.length == 3 | (inputSplit.length == 4 & inputSplit[0].toLowerCase().equals("chmod"))) {
				command = inputSplit[0].toLowerCase();
				user = inputSplit[1].toLowerCase();
				file = inputSplit[2].toLowerCase();
				int newMode = 0;

				if (command.equals("chmod"))
					newMode = Integer.parseInt(inputSplit[3], 8);

				//perform command
				switch(command.toUpperCase()) {
					case "READ":
						read(user, file);
						break;
					case "WRITE":
						write(user, file);
						break;
					case "EXECUTE":
						execute(user, file);
						break;
					case "CHMOD":
						chmod(user, file, newMode);
						break;
					case "EXIT":
						exit();
						break;
					default:
						System.out.print("Invalid command\n");
						break;
				}
			} else if (input.toUpperCase().equals("EXIT")) {
				command = "EXIT";
				exit();
			} else {
				System.out.print("Invalid command\n");
				command = "INVALID";
			}
		} while (!(command.equalsIgnoreCase("EXIT")));
	}

	/*
	 * Verifies if the calling user has permission to read the requested file
	 */
	private void read(String user, String file) {
		//check if user/file are in the system
		if (!userList.containsKey(user)) {
			System.out.println("User not found: " + user);
		} else if (!fileModes.containsKey(file)) {
			System.out.println("File not found: " + file);
		} else if (r & user.equals("root")) {	//root user action
			System.out.print("Output:\n");
			System.out.print("read root root 1\n");
		} else {	//determine is user has proper permissions
			int mode = fileModes.get(file);
			String modeBinary = modeToBinary(mode);
			String group = userList.get(user);
			String owner = fileOwners.get(file);
			String ownerGroup = userList.get(owner);

			System.out.print("Output:\n");
			//print out action and if it was allowed
			if (modeBinary.charAt(9) == '1' | (modeBinary.charAt(6) == '1' & group.equals(ownerGroup)) |
					(modeBinary.charAt(3) == '1' & user.equals(owner)))
				System.out.print("read " + user + " " + group + " " + "1\n");
			else
				System.out.print("read " + user + " " + group + " " + "0\n");
		}
	}

	/*
	 * Verifies if the calling user has permission to write the requested file
	 */
	private void write(String user, String file) {
		//check if user/file are in the system
		if (!userList.containsKey(user)) {
			System.out.println("User not found: " + user);
		} else if (!fileModes.containsKey(file)) {
			System.out.println("File not found: " + file);
		} else if (r & user.equals("root")) {	//root user action
			System.out.print("Output:\n");
			System.out.print("write root root 1\n");
		} else {	//determine is user has proper permissions
			int mode = fileModes.get(file);
			String modeBinary = modeToBinary(mode);
			String group = userList.get(user);
			String owner = fileOwners.get(file);
			String ownerGroup = userList.get(owner);

			System.out.print("Output:\n");
			//print out action and if it was allowed
			if (modeBinary.charAt(10) == '1' | (modeBinary.charAt(7) =='1' & group.equals(ownerGroup)) |
					(modeBinary.charAt(4) == '1' & user.equals(owner)))
				System.out.print("write " + user + " " + group + " " + "1\n");
			else
				System.out.print("write " + user + " " + group + " " + "0\n");
		}
	}

	/*
	 * Verifies if the calling user has permission to read the requested file
	 */
	private void execute(String user, String file) {
		//check if user/file are in the system
		if (!userList.containsKey(user)) {
			System.out.println("User not found: " + user);
		} else if (!fileModes.containsKey(file)) {
			System.out.println("File not found: " + file);
		} else if (r & user.equals("root")) {	//root user action
			System.out.print("Output:\n");
			System.out.print("execute root root 1\n");
		} else {	//determine is user has proper permissions
			int mode = fileModes.get(file);
			String modeBinary = modeToBinary(mode);
			String group = userList.get(user);
			String owner = fileOwners.get(file);
			String ownerGroup = userList.get(owner);
			String runningUser = user;
			String runningGroup = group;

			//running user/group is changed to owner if user-id/group-id bits are set
			if (modeBinary.charAt(0) == '1')
				runningUser = owner;
			if (modeBinary.charAt(1) == '1')
				runningGroup = ownerGroup;

			System.out.print("Output:\n");
			//print out action and if it was allowed
			if (modeBinary.charAt(11) == '1' | (modeBinary.charAt(8) =='1' & group.equals(ownerGroup)) |
					(modeBinary.charAt(5) == '1' & user.equals(owner)))
				System.out.print("execute " + runningUser + " " + runningGroup + " " + "1\n");
			else
				System.out.print("execute " + runningUser + " " + runningGroup + " " + "0\n");
		}
	}

	/*
	 * Verifies if the calling user has permission to read the requested file
	 * and modifies the file permissions accordingly
	 */
	private void chmod(String user, String file, int mode) {
		//check if user/file are in the system
		if (!userList.containsKey(user)) {
			System.out.println("User not found: " + user);
		} else if (!fileModes.containsKey(file)) {
			System.out.println("File not found: " + file);
		} else if (r & user.equals("root")) {	//root user action
			fileModes.put(file, mode);
			System.out.print("Output:\n");
			System.out.print("chmod root root 1\n");
		} else if (user.equals(fileOwners.get(file))) {	//file owner action
			fileModes.put(file, mode);
			System.out.print("Output:\n");
			System.out.print("chmod " + user + " " + userList.get(user) + " 1\n");
		} else {
			System.out.print("Output:\n");
			System.out.print("chmod " + user + " " + userList.get(user) + " 0\n");
		}
	}

	/*
	 * Exits the system and prints state to state.log
	 */
	private void exit() throws Exception {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("state.log")));
		String mode, file, owner, group;
		for (Map.Entry<String, Integer> entry : fileModes.entrySet()) {
			file = entry.getKey();
			mode = modeToString(entry.getValue());
			owner = fileOwners.get(file);
			group = userList.get(owner);

			bw.write(mode + " " + owner + " " + group + " " + file + "\n");
		}
		bw.close();
	}

	/*
	 * Returns the binary representation of a mode in a binary String format
	 * Pad the left with 0s if length of String < 12
	 */
	private String modeToBinary(int mode) {
		String modeBinary = Integer.toBinaryString(mode);
		if (modeBinary.length() < 12) {
			int pad = 12 - modeBinary.length();
			for (int i = 0; i < pad; i++)
				modeBinary = "0" + modeBinary;	//pad left with 0 until 12 bits
		}
		return modeBinary;
	}

	/*
	 * Returns the string representation of a mode
	 */
	private String modeToString(int mode) {
		StringBuilder modeString;
		String modeBinary;
		boolean setUser, setGroup, sticky;
		setUser = setGroup = sticky = false;

		//get mode binary String and determine if setUser, setGroup, sticky are set
		modeBinary = modeToBinary(mode);
		if (modeBinary.charAt(0) == '1')
			setUser = true;
		if (modeBinary.charAt(1) == '1')
			setGroup = true;
		if (modeBinary.charAt(2) == '1')
			sticky = true;

		//convert mode from binary to String representation
		modeString = new StringBuilder(modeBinary.substring(3));
		modeBinary = modeBinary.substring(3);
		for (int i = 0; i < modeString.length(); i++) {
			if (i == 0 | i == 3 | i == 6) {	//set read bits
				if (modeBinary.charAt(i) == '1')
					modeString.setCharAt(i, 'r');
				else
					modeString.setCharAt(i, '-');
			} else if (i == 1 | i == 4 | i == 7) {	//set write bits
				if (modeBinary.charAt(i) == '1')
					modeString.setCharAt(i, 'w');
				else
					modeString.setCharAt(i, '-');
			} else if (i == 2) {	//set User execute bit
				if (setUser & modeBinary.charAt(i) == '0')
					modeString.setCharAt(i, 'S');
				else if (setUser & modeBinary.charAt(i) == '1')
					modeString.setCharAt(i, 's');
				else if (modeBinary.charAt(i) == '1')
					modeString.setCharAt(i, 'x');
				else
					modeString.setCharAt(i, '-');
			} else if (i == 5) {	//set Group execute bit
				if (setGroup & modeBinary.charAt(i) == '0')
					modeString.setCharAt(i, 'S');
				else if (setGroup & modeBinary.charAt(i) == '1')
					modeString.setCharAt(i, 's');
				else if (modeBinary.charAt(i) == '1')
					modeString.setCharAt(i, 'x');
				else
					modeString.setCharAt(i, '-');
			} else {	//i == 8, last bit, set Other execute bit
				if (sticky & modeBinary.charAt(i) == '0')
					modeString.setCharAt(i, 'T');
				else if (sticky & modeBinary.charAt(i) == '1')
					modeString.setCharAt(i, 't');
				else if (modeBinary.charAt(i) == '1')
					modeString.setCharAt(i, 'x');
				else
					modeString.setCharAt(i, '-');
			}		
		}
		return modeString.toString();
	}

	/*
	 * Creates a system based on the input userList and fileList
	 * and processes commands input by the user
	 * -r flag indicates there is no root user in the system
	 */
	public static void main(String[] args) throws Exception {
		boolean r;
		String userList, fileList;
		//check if -r flag is set
		if (args[0].equals("-r")) {
			r = false;
			userList = args[1];
			fileList = args[2];
		} else {
			r = true;
			userList = args[0];
			fileList = args[1];
		}

		ACS acs = new ACS(userList, fileList, r);
		acs.processCommands();
	}
}
