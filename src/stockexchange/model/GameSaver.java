/*Gergo Kovats*/
package stockexchange.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class GameSaver {
	

	public GameSaver(ArrayList<Object> savedProperties, boolean save, String pathFile) {
		File savedGameFile = new File(pathFile);
		//File savedGameFile = new File("./build/classes/images/savedGame.dat");
		//mentes
		if (save) {
			try {
				if (savedGameFile.exists())
					savedGameFile.delete();
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savedGameFile));
				for (Object savedProperty : savedProperties) {
					oos.writeObject(savedProperty);
				}
				oos.close();
				savedProperties.clear();
			}	catch (NotSerializableException e) {
				System.out.println("Not serializable object");
			} catch (IOException e) {
				System.out.println("I/O hiba: " + e.getMessage());
			}
		} else {
			//betoltes
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(savedGameFile));
				//System.out.println(savedGameFile.toString());
				//System.out.println(ois);
				savedProperties.clear();
				while (true)
					savedProperties.add(ois.readObject());
			} catch (ClassNotFoundException e) {
				System.out.println("Class not found");
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
	
	public static boolean isStructured(String pathFile) {
		ArrayList<Object> list = new ArrayList<>();
		new GameSaver(list, false, pathFile);
		boolean structured = true;
		int size = list.size();
		if (size > 7)
			if (list.get(0) instanceof Boolean
							& list.get(1) instanceof Boolean
							& list.get(2) instanceof Integer
							& list.get(3) instanceof Integer
							& list.get(4) instanceof ArrayList
							& list.get(5) instanceof Integer
							& list.get(6) instanceof Integer) {
				int n = 7 + (int) list.get(6);
				if (size > n)
					for (int i = 7; structured & i < n; i++)
						structured = list.get(i) instanceof Player;
				else
					structured = false;
				if (list.get(n) instanceof Integer) {
					int m = n + (int) list.get(n) + 1;
					if (size == m + 1)
						for (int i = n + 1; structured & i < m; i++)
							structured = list.get(i) instanceof Column;
					structured = structured & list.get(m) instanceof ArrayList;
				} else
					structured = false;
			} else
				structured = false;
		else
			structured = false;
		return structured;
	}

}
