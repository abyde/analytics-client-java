package com.acunu.performance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class YamlLoader {

	private final List<String> packages;
	@SuppressWarnings("rawtypes")
	private final Map<String, Class> tags;

	@SuppressWarnings("rawtypes")
	public YamlLoader(List<String> packages, Map<String, Class> tags) {
		this.packages = packages;
		this.tags = tags;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object load(File file) {

		try {
			Constructor constructor = new Constructor();

			// scan for tags corresponding to classes in the packages
			scanForTypes(new FileReader(file), constructor);

			// add usual tags
			for (Map.Entry<String, Class> entry : tags.entrySet()) {
				constructor.addTypeDescription(new TypeDescription(entry.getValue(), entry.getKey()));
			}

			Yaml yaml = new Yaml(constructor);
			return yaml.load(new FileReader(file));

		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void scanForTypes(Reader reader, Constructor constructor) {

		try {
			BufferedReader breader = new BufferedReader(reader);
			for (String line = breader.readLine(); line != null; line = breader.readLine()) {

				StringBuilder builder = null;

				for (char c : line.toCharArray()) {

					switch (c) {
					case '!':
						builder = new StringBuilder();
						break;

					case ' ':
					case '{':

						if (builder != null) {
							addTag(constructor, builder);
							builder = null;
						}
						break;

					default:
						if (builder != null) {
							builder.append((char) c);
						}
						break;
					}
				}

				if (builder != null) {
					addTag(constructor, builder);
					builder = null;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addTag(Constructor constructor, StringBuilder builder) {
		String tag = builder.toString();
		Class clss = findClass(tag);
		if (clss != null) {
			constructor.addTypeDescription(new TypeDescription(clss, "!" + tag));
		} else if (!tags.containsKey("!" + tag)) {
			System.out.println("warning: class not found: '" + tag + "'");
		}
	}

	@SuppressWarnings("rawtypes")
	private Class findClass(String tag) {

		for (String pack : packages) {
			String clssname = pack + "." + tag;

			try {
				return Class.forName(clssname);
			} catch (ClassNotFoundException e) {

			}
		}

		return null;
	}

}
