import org.junit.Assert;
import org.junit.Test;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

public class SelectorTest {

    private void testHelper(String path, HashSet<String> result) {
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            String data;
            ArrayList<String> except = new ArrayList<String>();
            while ((data = br.readLine()) != null) {
                except.add(data);
            }
            ArrayList<String> except_copy = new ArrayList<String>(except);
            for(String s : except_copy){
                if(result.contains(s)){
                    result.remove(s);
                    except.remove(s);
                }
                else{
                    if(!s.equals("")){
                        System.out.println(s);
                        //Assert.fail();
                    }
                    else{
                        except.remove(s);
                    }
                }
            }
            Assert.assertEquals(result.size(), except.size());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void TestCMD(){
        String[] args = new String[]{"-m", "C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\0-CMD\\target", "C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\0-CMD\\data\\change_info.txt"};
        HashSet<String> result = TestSelector.entrance(args);
        testHelper("C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\0-CMD\\data\\selection-method.txt", result);
        args[0] = "-c";
        result = TestSelector.entrance(args);
        testHelper("C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\0-CMD\\data\\selection-class.txt", result);
    }

    @Test
    public void TestALU(){
        String[] args = new String[]{"-m", "C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\1-ALU\\target", "C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\1-ALU\\data\\change_info.txt"};
        HashSet<String> result = TestSelector.entrance(args);
        testHelper("C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\1-ALU\\data\\selection-method.txt", result);
        args[0] = "-c";
        result = TestSelector.entrance(args);
        testHelper("C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\1-ALU\\data\\selection-class.txt", result);
    }

    @Test
    public void TestDataLog(){
        String[] args = new String[]{"-m", "C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\2-DataLog\\target", "C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\2-DataLog\\data\\change_info.txt"};
        HashSet<String> result = TestSelector.entrance(args);
        testHelper("C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\2-DataLog\\data\\selection-method.txt", result);
        args[0] = "-c";
        result = TestSelector.entrance(args);
        testHelper("C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\2-DataLog\\data\\selection-class.txt", result);
    }

    @Test
    public void TestBinaryHeap(){
        String[] args = new String[]{"-m", "C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\3-BinaryHeap\\target", "C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\3-BinaryHeap\\data\\change_info.txt"};
        HashSet<String> result = TestSelector.entrance(args);
        testHelper("C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\3-BinaryHeap\\data\\selection-method.txt", result);
        args[0] = "-c";
        result = TestSelector.entrance(args);
        testHelper("C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\3-BinaryHeap\\data\\selection-class.txt", result);
    }

    @Test
    public void TestNextDay(){
        String[] args = new String[]{"-m", "C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\4-NextDay\\target", "C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\4-NextDay\\data\\change_info.txt"};
        HashSet<String> result = TestSelector.entrance(args);
        testHelper("C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\4-NextDay\\data\\selection-method.txt", result);
        args[0] = "-c";
        result = TestSelector.entrance(args);
        testHelper("C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\4-NextDay\\data\\selection-class.txt", result);
    }

    @Test
    public void TestMoreTriangle(){
        String[] args = new String[]{"-m", "C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\5-MoreTriangle\\target", "C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\5-MoreTriangle\\data\\change_info.txt"};
        HashSet<String> result = TestSelector.entrance(args);
        testHelper("C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\5-MoreTriangle\\data\\selection-method.txt", result);
        args[0] = "-c";
        result = TestSelector.entrance(args);
        testHelper("C:\\Users\\hewenbing\\Desktop\\ClassicAutomatedTesting\\Data\\5-MoreTriangle\\data\\selection-class.txt", result);
    }

}

