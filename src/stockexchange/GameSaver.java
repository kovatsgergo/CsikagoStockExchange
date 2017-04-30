/*Gergo Kovats*/
package stockexchange;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class GameSaver {

	public GameSaver(ArrayList<Object> savedProperties, boolean save) {
		System.out.println("Working Directory = " +
              System.getProperty("user.dir"));
		File savedGameFile = new File("./build/classes/images/savedGame.dat");
		//mentes
		if (save) {
			try {
				if(savedGameFile.exists())
					savedGameFile.delete();
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savedGameFile));
				for (Object savedProperty : savedProperties) {
					oos.writeObject(savedProperty);
				}
				oos.close();
				savedProperties.clear();
			} catch (IOException e) {
				System.out.println("I/O hiba: " + e.getMessage());
			}
		} else {
			//betoltes
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(savedGameFile));
				System.out.println(savedGameFile.toString());
				System.out.println(ois);
				savedProperties.clear();
				while (true)
					savedProperties.add(ois.readObject());
			} catch (ClassNotFoundException e) {
				System.out.println("Hibas osztaly!");
			} catch (IOException e) {
				System.out.println("I/O hiba: " + e.getMessage());
			} finally {
				try {
					if (ois != null)
						ois.close();
				} catch (IOException e) {
					System.out.println("I/O hiba: " + e.getMessage());
				}
			}
		}
	}

}
