import com.ibm.wala.ipa.callgraph.CGNode;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

public class ChangeInfo {

    private HashSet<String> classChangeInfo = new HashSet<String>();
    private HashSet<String> methodChangeInfo = new HashSet<String>();
    private String flag;

    public ChangeInfo(String change_info_path,String flag){
        this.flag = flag;
        ArrayList<String> content = new ArrayList<String>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(change_info_path)));
            String data = null;
            while ((data = bufferedReader.readLine()) != null) {
                content.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < content.size(); i++) {
            classChangeInfo.add(content.get(i).split(" ")[0]);
            methodChangeInfo.add(content.get(i).split(" ")[1]);
        }
    }

    public boolean isChange(CGNode node){
        if(flag.equals("-c")){
            return classChangeInfo.contains(node.getMethod().getDeclaringClass().getName().toString());
        }
        else{
            return methodChangeInfo.contains(node.getMethod().getSignature());
        }
    }
}
