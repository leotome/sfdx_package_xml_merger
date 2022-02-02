package sfdx_manifestmerger;

import java.util.*;

import java.io.File;

public class Program {

	public static void main(String[] args) {
		Boolean UnixOS = (System.getProperty("os.name").toUpperCase().contains("WINDOWS") == false);
		String PathSeparator = (UnixOS == true) ? "/" : "\\";

		Map<String, String> ConfigArgs = parseArgs(args);
		if(ConfigArgs.size() != 3) {
			System.exit(1);
		}
		
        String ManifestPathString = ConfigArgs.get("PATH");
        String SFDXVersion = ConfigArgs.get("API");
        String OutputFilename = ConfigArgs.get("OUTPUT");
        
        
        File ManifestFolder = new File(ManifestPathString);
        File[] AllFiles = ManifestFolder.listFiles();
        List<File> XMLFiles = new ArrayList<>();

        for (File UntreatedFile : AllFiles) {
            if(UntreatedFile.getName().endsWith(".xml")){
                XMLFiles.add(UntreatedFile);
            }
        }

        System.out.println("");

        if(XMLFiles.size() > 0){
            System.out.println("We managed to find the following files inside the directory: ");

            for (File TreatedFile : XMLFiles) {
                System.out.println("- " + TreatedFile.getName());
            }

            System.out.println("");
            System.out.println("Those files will be parsed. Please wait.");

        } else {
            System.out.println("We could not find any *.xml files inside the directory. Please try again. Aborting.");
            System.exit(1);
        }

        List<String> AllMetadataTypes = new ArrayList<String>();
        Map<String, List<String>> AllTypesAndItems = new HashMap<String, List<String>>();

        for (File TreatedFile : XMLFiles) {
            ManifestParser Parser = new ManifestParser();
            Parser.setFile(TreatedFile);
            Parser.processFile();
            for(String key : Parser.getKeys()){
                if(AllMetadataTypes.contains(key) != true){
                    AllMetadataTypes.add(key);
                }
                if(AllTypesAndItems.get(key) != null){
                    List<String> AllTypesAndItems_VList = AllTypesAndItems.get(key);
                    for(String value : Parser.getValues(key)){
                        if(AllTypesAndItems_VList.contains(value) != true){
                            AllTypesAndItems_VList.add(value);
                        }
                    }
                } else {
                    List<String> AllTypesAndItems_VList = new ArrayList<String>();
                    for(String value : Parser.getValues(key)){
                        AllTypesAndItems_VList.add(value);
                    }
                    AllTypesAndItems.put(key, AllTypesAndItems_VList);
                }
            }
        }

        for(String key : AllTypesAndItems.keySet()){
            Collections.sort(AllTypesAndItems.get(key));
        }

        System.out.println("We will merge the following metadata types: " + AllMetadataTypes.toString());

        ManifestWriter Writer = new ManifestWriter();
        Writer.AllTypesAndItems = AllTypesAndItems;
        Writer.SFDXVersion = SFDXVersion;
        Writer.FilePath = (ManifestPathString.endsWith(PathSeparator) == true) ? ManifestPathString + OutputFilename :  ManifestPathString + PathSeparator + OutputFilename;

        Writer.writeFile();

        System.out.println("The file was created successfully! Path: " + Writer.FilePath);
	}
	
	private static Map<String, String> parseArgs(String[] args){
		Map<String, String> ConfigArgs = new HashMap<String, String>();
		if(args.length == 0){
			System.out.println("No arguments were provided. Please refer to the documentation.");
		} else if (args.length % 2 != 0 ){
			System.out.println("Error when parsing arguments. Please review, and refer to the documentation.");
		} else {
			Integer Index = 0;
			for(String arg : args) {
				if(arg.contains("--") == true) {
					switch(arg.replace("--","").toUpperCase()) {
						case "PATH":
							ConfigArgs.put("PATH", args[Index + 1]);
							break;
						case "API":
							ConfigArgs.put("API", args[Index + 1]);
							break;
						case "OUTPUT":
							ConfigArgs.put("OUTPUT", args[Index + 1]);
							break;
						default:
							break;
					}
				}
				Index++;
			}
		}
		System.out.println(ConfigArgs.toString());
		return ConfigArgs;
	}

}
