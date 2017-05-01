package authserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Account_Server {
	public Account_Server() {

	}
	
	public static boolean userIsExist(String user){
		File file = new File("account/userTable.db");
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String s = input.readLine();
			while (s != null) {
				if (user.trim().equals(s.trim().split(" ")[0])) {
					input.close();
					return true;
				}
				s = input.readLine();
			}
			input.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean Authentication(String user_pass) {
		File file = new File("account/userTable.db");
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String s = input.readLine();
			while (s != null) {
				if (user_pass.trim().equals(s.trim())) {
					input.close();
					return true;
				}
				s = input.readLine();
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean CreateAccount(String user_pass) {
		File file = new File("account/userTable.db");
		try {
			FileOutputStream wirefile = new FileOutputStream(file, true);
			wirefile.write(("\n"+user_pass).getBytes());
			wirefile.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
