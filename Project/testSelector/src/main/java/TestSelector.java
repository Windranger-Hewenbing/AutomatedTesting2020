import com.ibm.wala.ipa.callgraph.*;
import java.io.*;
import java.util.*;

public class TestSelector {

    public static HashSet<String> entrance(String[] args){
        String scopePath = "scope.txt";
        String exPath = "exclusion.txt";
        CHA selector = new CHA();
        selector.flag = args[0];

        selector.init(scopePath, exPath);

        String project_target_path = args[1];
        String change_info_path = args[2];

        ArrayList<String> src = new ArrayList<String>();
        ArrayList<String> test = new ArrayList<String>();

        File file = new File(project_target_path);
        if (file.exists()) {
            if (null == file.listFiles()) {
                return null;
            }
            LinkedList<File> list = new LinkedList<File>(Arrays.asList(file.listFiles()));
            File t = null;
            File s = null;
            for (File file1 : list) {
                if (file1.getName().equals("test-classes")) {
                    t = file1;
                }
                if (file1.getName().equals("classes")) {
                    s = file1;
                }
            }
            list = new LinkedList<File>(Arrays.asList(t.listFiles()));
            addFilePath(list, test);
            list = new LinkedList<File>(Arrays.asList(s.listFiles()));
            addFilePath(list, src);
        } else {
            System.out.println("文件不存在!");
        }

        for(String path:test){
            selector.addScope(path);
        }
        HashMap<CGNode, HashSet<CGNode>> testGraph = new HashMap<CGNode, HashSet<CGNode>>();
        selector.makeCallGraph();
        selector.findDependency(testGraph);

        for (String path : src) {
            selector.addScope(path);
        }
        HashMap<CGNode, HashSet<CGNode>> graph = new HashMap<CGNode, HashSet<CGNode>>();
        selector.makeCallGraph();
        selector.findDependency(graph);

        dotGenerate(graph, args[0], "graph.dot");

        ChangeInfo changeInfo = new ChangeInfo(change_info_path,args[0]);
        HashSet<String> result = new HashSet<String>();
        selector.select(changeInfo, graph, result, testGraph);
        return result;
    }

    public static void main(String[] args){
        HashSet<String> result = entrance(args);
        StringBuilder stringBuilder = new StringBuilder();
        for(String s: result){
            stringBuilder.append(s+"\n");
        }
        String outputFile;
        if(args[0].equals("-c")){
            outputFile = "selection-class.txt";
        }
        else{
            outputFile = "selection-method.txt";
        }
        try{
            BufferedWriter output = new BufferedWriter(new FileWriter(outputFile));
            output.write(stringBuilder.toString());
            output.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private static void addFilePath(LinkedList<File> list, ArrayList<String> arrayList) {
        while (!list.isEmpty()) {
            File[] files = list.removeFirst().listFiles();
            if (files == null) {
                continue;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    list.add(file);
                } else {
                    arrayList.add(file.getPath());
                }
            }
        }
    }

    private static void dotGenerate(HashMap<CGNode, HashSet<CGNode>> graph,String flag,String fileName){
        HashMap<String, HashSet<String>> dot = new HashMap<String, HashSet<String>>();
        for(CGNode node:graph.keySet()){
            String line;
            if(flag.equals("-c")){
                String classInnerName= node.getMethod().getDeclaringClass().getName().toString();
                line = "\""+classInnerName+"\"";
            }
            else{
                String signature = node.getMethod().getSignature();
                line = "\""+signature+"\"";
            }
            if(!dot.containsKey(line)){
                dot.put(line, new HashSet<String>());
            }
            String line2;
            for (CGNode t:graph.get(node)){
                if(flag.equals("-c")){
                    String classInnerName= t.getMethod().getDeclaringClass().getName().toString();
                    line2 = "\""+classInnerName+"\"";
                }
                else{
                    String signature = t.getMethod().getSignature();
                    line2 = "\""+signature+"\"";
                }
                dot.get(line).add(line2);
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("digraph g {\n");
        for(String key: dot.keySet()){
            for(String value: dot.get(key)){
                stringBuilder.append("\t"+key+" -> "+value+";\n");
            }
        }
        stringBuilder.append("}");

        try{
            BufferedWriter output = new BufferedWriter(new FileWriter(fileName));
            output.write(stringBuilder.toString());
            output.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


}
