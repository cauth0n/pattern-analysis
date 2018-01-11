package com.derek.uml.srcML;

import com.derek.Main;
import com.derek.uml.*;
import com.derek.uml.plantUml.PlantUMLTransformer;
import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class runs the srcML tool  (http://www.srcml.org/#home) on a software project.
 */
public class SrcMLRunner {

    private final boolean storeSrcML = true;
    private String projectWorkingDirectory;
    private final String xmlSpecifier = "specifier";
    private final String xmlName = "name";
    private UMLClassDiagram umlClassDiagram;

    public SrcMLRunner(String projectWorkingDirectory){
        this.projectWorkingDirectory = projectWorkingDirectory;
        generateSrcML();

        umlClassDiagram = new UMLClassDiagram();

        //parseSrcMLFile(new File("srcMLOutput/selenium36/AbstractAnnotations.xml"));
        //parseSrcMLFile(new File("srcMLOutput/selenium36/ExpectedConditions.xml"));
        //parseSrcMLFile(new File("srcMLOutput/selenium36/Architecture.xml"));

        //selenium test
        buildAllClasses();

        //guava test
        //buildClassDiagram(new File("srcMLOutput/guava13/Files.xml"));

        PlantUMLTransformer pltTransformer = new PlantUMLTransformer(umlClassDiagram);
        //used to print plantuml
        pltTransformer.generateClassDiagram();
    }

    private void parseSrcMLFile(File file){
        try{
            System.out.println("parsing " + file.getName());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            NodeList roots = doc.getElementsByTagName("unit");
            for (int i = 0; i < roots.getLength(); i++) {
                Node root = roots.item(i);
                //kick off parsing.
                SrcMLBlock block = new SrcMLBlock(XmlUtils.elementify(root));
                List<SrcMLClass> srcMLClasses = block.getClasses();
                for (SrcMLClass srcMLClass : srcMLClasses){
                    umlClassDiagram.addClassToDiagram(UMLGenerationUtils.getUMLClass(srcMLClass));
                }
                List<SrcMLInterface> srcMLInterfaces = block.getInterfaces();
                for (SrcMLInterface srcMLInterface : srcMLInterfaces){
                    umlClassDiagram.addClassToDiagram(UMLGenerationUtils.getUMLInterface(srcMLInterface));
                }
                List<SrcMLEnum> srcMLEnums = block.getEnums();
                for (SrcMLEnum srcMLEnum : srcMLEnums){
                    umlClassDiagram.addClassToDiagram(UMLGenerationUtils.getUMLEnum(srcMLEnum));
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void buildAllClasses(){
        //https://stackoverflow.com/questions/5694385/getting-the-filenames-of-all-files-in-a-folder
        File f = new File("srcMLOutput/selenium36/");
        File[] listOfFiles = f.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            parseSrcMLFile(listOfFiles[i]);
        }
    }

    public List<Path> getSourceCodeListFromProject(){
        try {
            //https://stackoverflow.com/questions/2534632/list-all-files-from-a-directory-recursively-with-java
            List<Path> toRet = Files.find(Paths.get(projectWorkingDirectory), 999, (p, bfa) -> bfa.isRegularFile() && p.getFileName().toString().matches(".*\\.java")).collect(Collectors.toList());
            return toRet;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void generateSrcML(){
        try{
            System.out.println("Starting srcML generation");
            //https://stackoverflow.com/questions/5604698/java-programming-call-an-exe-from-java-and-passing-parameters

            List<Path> pathsAsPath = getSourceCodeListFromProject();
            File mainDirectory = new File("srcMLOutput\\");
            if (!mainDirectory.exists()){
                Files.createDirectory(Paths.get("srcMLOutput\\"));
            }

            //change directory in future.
            File directory = new File("srcMLOutput\\" + Main.projectID + Main.testProject + "\\");
            if (!directory.exists()) {
                Files.createDirectory(Paths.get("srcMLOutput\\"+ Main.projectID + Main.testProject + "\\"));
            }

            for (Path p : pathsAsPath) {
                String current = quotify(p.toString());

                Runtime rt = Runtime.getRuntime();
                String[] commands = {"srcML ", current};
                Process proc = rt.exec(commands);
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                String srcMLOut = "";
                String s = "";
                if (storeSrcML) {
                    //take out .java extension and add .xml extension

                    File fout = new File("srcMLOutput\\" + directory.getName() + "\\" + p.getFileName().toString().split(".java")[0] + ".xml");
                    if (!fout.exists()) {
                        BufferedWriter bf = new BufferedWriter(new FileWriter(fout));
                        while ((s = stdInput.readLine()) != null) {
                            srcMLOut += s;
                            bf.write(s);
                        }
                        bf.close();
                    }
                }
                proc.destroy();
            }
            System.out.println("Ending srcML generation");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private String quotify(String s){
        s = "\"" + s + "\"";
        s = s.replace("/", "\\") ;
        return s;
    }
}
