package at.favre.tools.dconvert.ui;

import at.favre.tools.dconvert.arg.Arguments;

import java.io.*;
import java.util.Base64;
import java.util.prefs.Preferences;

/**
 * Simple persistence store for UI
 */
public class SerializePreferenceStore implements IPreferenceStore {
	public static final String ARGS_KEY = "args";
	private Preferences prefs;

	public SerializePreferenceStore() {
		this.prefs = Preferences.userNodeForPackage(GUI.class);
	}

	@Override
	public void save(Arguments arg) {
		try {
			prefs.put(ARGS_KEY, serialize(arg));
		} catch (Exception e) {
			prefs.remove(ARGS_KEY);
			e.printStackTrace();
		}
	}

	@Override
	public Arguments get() {
		try {
			String saved = prefs.get(ARGS_KEY, null);
			if (saved == null) {
				return null;
			}

			Object out = unserialize(saved);
			return (Arguments) out;
		} catch (Exception e) {
			prefs.remove(ARGS_KEY);
			e.printStackTrace();
			return null;
		}
	}

	private static String serialize(Serializable obj) throws IOException {
		ObjectOutput out = null;
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			out = new ObjectOutputStream(bos);
			out.writeObject(obj);
			return Base64.getEncoder().encodeToString(bos.toByteArray());
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
		}
	}

	private static Object unserialize(String base64Obj) throws IOException, ClassNotFoundException {
		try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(base64Obj)))) {
			return in.readObject();
		}
	}
}
