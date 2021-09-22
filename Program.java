import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import java.util.Scanner;

import java.io.File;

public class Program{
    public static void main(String[] args){

        String ManifestPathString = "";
        String SFDXVersion = "";
        String OutputFilename = "";


        if(args.length == 0){
            Scanner in = new Scanner(System.in);
            System.out.print("Please enter the path of the folder where the XML files are located = ");
            ManifestPathString = in.nextLine();
            System.out.print("Please enter the Salesforce API version = ");
            SFDXVersion = in.nextLine();
            System.out.print("Please enter desired output filename = ");
            OutputFilename = in.nextLine();
            in.close();
        } else {
            ManifestPathString = args[0];
            SFDXVersion = args[1];
            OutputFilename = args[2];
        }
        
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
            SFDCMetadataXMLParser Parser = new SFDCMetadataXMLParser();
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

        System.out.println("We will merge the following metadata types: " + AllMetadataTypes.toString());

        SFDCMetadataXMLWriter Writer = new SFDCMetadataXMLWriter();
        Writer.AllTypesAndItems = AllTypesAndItems;
        Writer.SFDXVersion = SFDXVersion;
        Writer.FilePath = (ManifestPathString.endsWith("\\") == true) ? ManifestPathString + OutputFilename :  ManifestPathString + "\\" + OutputFilename;

        Writer.writeFile();

        System.out.println("The file was created successfully! Path: " + Writer.FilePath);
    }
}