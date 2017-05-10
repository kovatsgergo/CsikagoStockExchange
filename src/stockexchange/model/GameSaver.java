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

	private GameSaver() {
	}

	public static void save(ArrayList<Object> savedProperties, String pathFile) throws GameFileException {
		File savedGameFile = new File(pathFile);
		try {
			if (savedGameFile.exists())
				savedGameFile.delete();
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savedGameFile));
			for (Object savedProperty : savedProperties) {
				oos.writeObject(savedProperty);
			}
			oos.close();
			savedProperties.clear();
		} catch (NotSerializableException e) {
			System.out.println("Not serializable object");
			throw new GameFileException("Error while saving file");
		} catch (IOException e) {
			System.out.println("I/O Exception: " + e.getMessage());
			throw new GameFileException("Error while saving file");
		}
	}

	public static void load(ArrayList<Object> savedProperties, String pathFile) throws GameFileException {
		File savedGameFile = new File(pathFile);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(savedGameFile));
			savedProperties.clear();
			while (true)
				savedProperties.add(ois.readObject());
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found");
			throw new GameFileException("Wrong file format");
		} catch (IOException e) {
			System.out.println("I/O Exception: " + e.getMessage());
			if (e.getMessage() != null)
				if (e.getMessage().contains("local class incompatible"))
					throw new GameFileException("Wrong (probably older) file format");
				else
					throw new GameFileException("Error while loading file");
		} finally {
			try {
				if (ois != null)
					ois.close();
			} catch (IOException e) {
				System.out.println("I/O Exception: " + e.getMessage());
			}
		}
	}

}
