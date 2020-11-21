# AutomatedTesting2020

选题方向：经典自动化测试

------

### 算法释义

通过WALA库构建CHA调用图，总共分为：生成分析域、添加分析域、生成类层次、确定进入点、构建调用图五个步骤。有了调用图之后，遍历调用图寻找CGNode之间的依赖关系，再根据changeInfo来选择测试用例。

对于CGNode的理解：其实是一个基于方法的节点，不是基于类的。

寻找依赖关系的重点在于，找到调用图中每个节点的所有后继节点，形成一个HashSet。

选择测试用例的重点在于广度优先策略。

- 基于类的粒度：某个节点变化了，那么他的后继节点都有可能变化了，所以要把他们全部放到变化队列中，对于每一个变化队列中的节点，去测试依赖图中找所有和他属于同一个类的节点。
- 基于方法的粒度：某个节点变化了，那么他的所有后继节点（依赖于该节点），只要是测试就直接选中。

深度优先策略不好实现，不推荐。

### 所用第三方库和版本

参见pom.xml中的依赖

```html
<dependencies>
        <!-- https://mvnrepository.com/artifact/com.ibm.wala/com.ibm.wala.util -->
        <dependency>
            <groupId>com.ibm.wala</groupId>
            <artifactId>com.ibm.wala.util</artifactId>
            <version>1.5.5</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/com.ibm.wala/com.ibm.wala.shrike -->
        <dependency>
            <groupId>com.ibm.wala</groupId>
            <artifactId>com.ibm.wala.shrike</artifactId>
            <version>1.5.5</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/com.ibm.wala/com.ibm.wala.core -->
        <dependency>
            <groupId>com.ibm.wala</groupId>
            <artifactId>com.ibm.wala.core</artifactId>
            <version>1.5.5</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/com.ibm.wala/com.ibm.wala.cast -->
        <dependency>
            <groupId>com.ibm.wala</groupId>
            <artifactId>com.ibm.wala.cast</artifactId>
            <version>1.5.5</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13</version>
            <scope>test</scope>
        </dependency>
        <!-- https://mvnrepository.com/artifact/com.sap.research.security.vulas/lang-java-reach-wala -->
        <dependency>
            <groupId>com.sap.research.security.vulas</groupId>
            <artifactId>lang-java-reach-wala</artifactId>
            <version>3.1.15</version>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
```

### 程序入口

TestSelector.java文件中的main函数为程序入口

entrance函数为利用CHA算法构建测试用例选择器的全过程

dotGenerate函数用来生成代码依赖图（.dot）文件

main函数最后还需要输出selection-method.txt或selection-class.txt文件

### 程序结构

```java
public class TestSelector {
    public static HashSet<String> entrance(String[] args);
    
    public static void main(String[] args);
    
    private static void addFilePath(LinkedList<File> list, ArrayList<String> arrayList);
    
    private static void dotGenerate(HashMap<CGNode, HashSet<CGNode>> graph,String flag,String fileName);
    
}
```

```java
public class CHA {
    private AnalysisScope scope;
    private CHACallGraph chaCallGraph;
    public String flag;

    public void init(String scopePath,String exPath);
    
    public void addScope(String path);
    
    public void makeCallGraph();
    
    public void findDependency(HashMap<CGNode, HashSet<CGNode>> graph);
    
    public void select(ChangeInfo changeinfo, HashMap<CGNode, HashSet<CGNode>> graph, HashSet<String> result, HashMap<CGNode, HashSet<CGNode>> testGraph);
    
}
    
```

```java
public class ChangeInfo{
    private HashSet<String> classChangeInfo;
    private HashSet<String> methodChangeInfo;
    private String flag;
    
    public ChangeInfo(String change_info_path,String flag);
    
    public boolean isChange(CGNode node);
    
}
```

