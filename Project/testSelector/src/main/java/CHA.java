import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.config.AnalysisScopeReader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CHA {

    private AnalysisScope scope;
    private CHACallGraph chaCallGraph;
    public String flag;

    public void init(String scopePath,String exPath){
        //生成分析域
        try {
            this.scope = AnalysisScopeReader.readJavaScope(scopePath, new File(exPath), ClassLoader.getSystemClassLoader());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void addScope(String path){
        //将class添加进分析域
        try{
            scope.addClassFileToScope(ClassLoaderReference.Application, new File(path));
            //System.out.println(scope);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void makeCallGraph(){
        //生成类层次
        ClassHierarchy cha;
        try {
            cha = ClassHierarchyFactory.makeWithRoot(scope);
        }catch (ClassHierarchyException e){
            System.out.println("cha is null!");
            return;
        }

        //确定进入点
        //针对主程序生成进入点
        //Iterable<Entrypoint> entrypoints = Util.makeMainEntrypoints(scope, cha);
        //针对所有Application类（非原生类）生成进入点
        Iterable<Entrypoint> entrypoints = new AllApplicationEntrypoints(scope,cha);//推荐使用这个

        //构建调用图
        //使用类层次分析算法构建调用图
        chaCallGraph = new CHACallGraph(cha);
        try {
            chaCallGraph.init(entrypoints);
        }catch (CancelException e){
            System.out.println("Cancel Exception!");
        }
    }

    public void findDependency(HashMap<CGNode, HashSet<CGNode>> graph){
        //遍历chaCallGraph中的所有节点
        for(CGNode node: chaCallGraph){
            //node中包含了很多信息，包括类加载器、方法信息等，这里只筛选出需要的信息
            if(node.getMethod() instanceof ShrikeBTMethod){
                //node.getMethod返回一个比较泛化的IMethod实例，不能获取到我们想要的信息
                //一般地，本项目中所有和业务逻辑相关的方法都是ShrikeBTMethod对象
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                //使用Primordial类加载器加载的类都属于Java原生类，我们一般不关心。
                if("Application".equals(method.getDeclaringClass().getClassLoader().toString())){
                    //获取声明该方法的类的内部表示
                    //String classInnerName = method.getDeclaringClass().getName().toString();
                    //获取方法签名
                    //String signature = method.getSignature();
                    //System.out.println(classInnerName+" "+signature);

                    if(!graph.containsKey(node)){
                        graph.put(node, new HashSet<CGNode>());
                    }

                    //找到该节点的所有后继节点
                    Iterator<CGNode> cgNodeIterator = chaCallGraph.getPredNodes(node);
                    while(cgNodeIterator.hasNext()){
                        CGNode dest = cgNodeIterator.next();
                        if(dest.getMethod() instanceof ShrikeBTMethod){
                            if("Application".equals(dest.getMethod().getDeclaringClass().getClassLoader().toString())){
                                //将新的边添加到图中
                                graph.get(node).add(dest);
                                //System.out.println("+");
                            }
                        }
                    }
                }
                //else{
                    //System.out.println(String.format("'%s'不是一个ShrikeBTMethod: %s", node.getMethod(),node.getMethod().getClass()));
                //}
            }
        }
    }

    public void select(ChangeInfo changeinfo, HashMap<CGNode, HashSet<CGNode>> graph, HashSet<String> result, HashMap<CGNode, HashSet<CGNode>> testGraph){
        //先选出所有变化了的节点到队列里面
        Queue<CGNode> queue = new LinkedList<CGNode>();
        for (CGNode key : graph.keySet()) {
            if(changeinfo.isChange(key)){
                queue.add(key);
            }
        }
        HashSet<CGNode> visited = new HashSet<CGNode>();
        while (!queue.isEmpty()) {
            //再取出所有依赖于这个变化节点的所有节点到队列里面
            CGNode head = queue.poll();
            if (visited.contains(head)) {
                continue;
            }
            visited.add(head);
            if (graph.containsKey(head)) {
                queue.addAll(graph.get(head));
                if(flag.equals("-c")) {
                    //based on class
                    for (CGNode node : graph.get(head)) {
                        //只关心存在于测试代码里面的节点
                        if (testGraph.containsKey(node)) {
                            for (CGNode node1 : testGraph.keySet()) {
                                //那么遍历测试依赖图的所有节点，找到和变化节点属于同一个类的节点
                                boolean isTest = false;
                                if (node1.getMethod().getAnnotations().toString().contains("Test")) {
                                    isTest = true;
                                }
                                if (node.getMethod().getDeclaringClass().getName().toString().equals(node1.getMethod().getDeclaringClass().getName().toString()) && isTest) {
                                    String line = node1.getMethod().getDeclaringClass().getName().toString() + " " + node1.getMethod().getSignature();
                                    if(!result.contains(line)){
                                        result.add(line);
                                    }
                                    /*
                                    注意：此时node和node1可能发生重复，由于没有重写hashcode和equals，所以需要去重
                                    */
                                }
                            }
                        }
                    }
                }
                else{
                    //based on method
                    for (CGNode node : graph.get(head)) {
                        //只关心存在于测试代码里面的方法和非初始化方法
                        //只要该节点是一个测试节点，由于之前已经选过，发现他是变化方法的后继节点，那么直接选中
                        if (testGraph.containsKey(node) && node.getMethod().getAnnotations().toString().contains("Test")) {
                            String line = node.getMethod().getDeclaringClass().getName().toString() + " " + node.getMethod().getSignature();
                            if (!result.contains(line)) {
                                result.add(line);
                            }
                            //去重，同上
                        }
                    }
                }
            }
        }
    }
}
