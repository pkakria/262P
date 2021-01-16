import java.io.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointerException;
import org.json.XML;

public class ReplaceSubObjectJSON {
    
    //recursive function
    public static Object replace_object(String [] keys, Object obj, JSONObject myobj){
        if (obj instanceof JSONArray){
            if (keys.length ==0){
                return myobj;
            }else{
                JSONArray ArrayObj = (JSONArray)obj;
                Object nextObj = ArrayObj.get(Integer.valueOf(keys[0]));  
                String [] nextKeys = new String [keys.length-1]; // truncated keys for next level of recursion
                for (int i=0;i<nextKeys.length; i++){
                    nextKeys[i] = keys[i+1];
                }
                Object newObj = replace_object(nextKeys, nextObj, myobj);
                ArrayObj.put(Integer.valueOf(keys[0]), newObj); // replace Array[keys[0]] with the new object
                return (Object)ArrayObj;  
            }  
        }else if (obj instanceof JSONObject){
            if (keys.length ==0){
                //base case
                return (Object)myobj; // return the new object instead of the object I got called with
            }else{
                JSONObject json = (JSONObject)obj;
                Object nextObj = json.get(keys[0]); // object for next level of recursion
                String [] nextKeys = new String [keys.length-1]; // truncated keys for next level of recursion
                for (int i=0;i<nextKeys.length; i++){
                    nextKeys[i] = keys[i+1];
                }
                Object newObj = replace_object(nextKeys, nextObj, myobj);// replace key/value in the sub object
                json.put(keys[0], newObj); // replace existing object with the new object
                return (Object)json; // return the modified json object    
            }
        }
        else{
            //base case. You reached a STring/Number/Boolean/Null. Just return the new object instead
            return (Object)myobj;
        } 
    }
    public static void main(String[] args) {
        File file = new File(args[0]); // the xml file
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder file_string = new StringBuilder("");
            String line;
            while ((line = br.readLine()) != null) {
                file_string.append(line);
                file_string.append(System.lineSeparator());
            }
            String xmlText = file_string.toString();
            br.close();
            // convert XML to JSON
            JSONObject json = XML.toJSONObject(xmlText); // got the json object
            
            //harcoded keypath to /catalog/book/0
            String keypath = "/catalog/book/0";
            String [] keysWithEmpty = keypath.split("/"); // will ignore the first value since it will be empty
            String [] keys = new String[keysWithEmpty.length-1];
            
            //construct array of keys to passs to replace_object() method
            for (int i=0; i<keys.length; i++){
                keys[i] = keysWithEmpty[i+1]; // we don't want first empty key
            }
            File outFile = new File("output5.json"); // default output file
            BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));

            if (json.optQuery(keypath)!= null){
                //if pointer points to a valid key, then only replace it with another JSONObject
                JSONObject myObject = new JSONObject();
                myObject.put("author", "Hemant").put("id", "bk110").put("title", "How to be a successfull women entrepreneur").put("price", 10);
                JSONObject modifiedJSONObject = (JSONObject) replace_object(keys, json, myObject);
               
                bw.write(modifiedJSONObject.toString(4)); // written
                bw.flush();
                bw.close();
            }else{
                System.out.println("Returned object is not a JSON object. Can't be written to a JSON file.");
                bw.write("");
                bw.flush();
                bw.close();
            }
            
        }
        catch (FileNotFoundException f){
            System.out.println("File not found");
        }
        catch(JSONPointerException jex){
            jex.printStackTrace();
        }
        catch(IOException ioex){
            ioex.printStackTrace();
        }
    }

}
