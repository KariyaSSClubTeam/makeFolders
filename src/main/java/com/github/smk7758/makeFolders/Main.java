package com.github.smk7758.makeFolders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

public class Main {
	private final String foldersPathString = "folders.yml";
	private final Yaml yaml = new Yaml();

	public static void main(String[] args) {
		new Main().start();
	}

	public void start() {
		System.out.println("This will create folder in there;"
				+ " Please write the drive letter of the HDD(or write the path of the folder):");
		String driveLetter = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		do {
			try {
				driveLetter = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (driveLetter == null);
		System.out.println("Drive letter: " + driveLetter);
		if (driveLetter.length() < 2) {
			driveLetter += ":";
		}
		Path folder = Paths.get(driveLetter, "メディア", String.valueOf(LocalDateTime.now().getYear()));
		createDirectory(folder);
		// top dir

		// sub dir
		Map<?, ?> yamlMap_temp = null;
		try {
			yamlMap_temp = yaml.load(this.getClass().getResourceAsStream(foldersPathString));
		} catch (YAMLException e) {
			System.err.println("Yaml type is error.");
		} catch (ClassCastException e) {
			System.err.println("Cast to map error.");
		}

		if (yamlMap_temp == null) System.err.println("Cannot load yaml.");
		createDirectory(yamlMap_temp, folder);
	}

	public void createDirectory(Path folder) {
		if (!Files.exists(folder)) {
			System.out.println("Creating directory at \"" + folder.toString() + "\".");
			try {
				Files.createDirectories(folder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Already exists: " + folder.toString());
		}
	}

	public void createDirectory(Map<?, ?> yaml_map, Path parent_folder) {
		for (Map.Entry<?, ?> yaml_entry : yaml_map.entrySet()) {
			if (yaml_entry.getKey() instanceof String) {
				// TODO cast or toString?
				Path dir_path = Paths.get(parent_folder.toString(), yaml_entry.getKey().toString());
				createDirectory(dir_path);
				if (yaml_entry.getValue() instanceof Map) {
					System.out.println("Go down with map.");
					createDirectory((Map<?, ?>) yaml_entry.getValue(),
							Paths.get(parent_folder.toString(), yaml_entry.getKey().toString()));
				} else if (yaml_entry.getValue() instanceof List) {
					System.out.println("Go down with list by: " + yaml_entry.getValue());
					for (Object value : (List<?>) yaml_entry.getValue()) {
						if (value instanceof String) {
							createDirectory(Paths.get(parent_folder.toString(),
									yaml_entry.getKey().toString(), (String) value));
						}
					}
				} else if (yaml_entry.getValue() instanceof String) {
					createDirectory(Paths.get(parent_folder.toString(),
							yaml_entry.getKey().toString(), (String) yaml_entry.getValue()));
				}
			} else {
				System.out.println("The key of the map is not a string.");
				System.out.println(yaml_entry.getKey().toString());
			}
		}
	}
}
