import java.io.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.JSONPointerException;
import org.json.XML;

public class GetSubObjectsJSON {
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
            
            //file for writing sub Object output
            File outFile = new File("output2.json"); // default output file
            BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
            
            // harcoded string. change for each XML file
            String jsonPointerString = "/root/listing/0";
            JSONPointer jpointer = new JSONPointer(jsonPointerString);
            try{
                Object subObject = jpointer.queryFrom(json);
            
            
             if (subObject instanceof JSONObject){
                JSONObject jsonSubObject = (JSONObject)subObject; // created a new JSON subObject
                bw.write(jsonSubObject.toString(4)); // write to file
            }else if (subObject instanceof JSONArray){
                JSONArray jsonSubObject = (JSONArray)subObject; // created a new JSON subObject
                bw.write(jsonSubObject.toString(4)); // write to file prettified 
            }
            else{
                    //just a value write it as it is
                bw.write(subObject.toString()); // write to file
            }
            bw.flush();
            bw.close();
            } catch(JSONPointerException jex){
                System.out.println("Misformed JSONPOinter. Please correct the hardcoded value");
                jex.printStackTrace();
                bw.write("");
                bw.flush();
                bw.close();
            }      
        }
        catch (FileNotFoundException f){
            System.out.println("File not found");
        }
        catch(IOException ioex){
            ioex.printStackTrace();
        }
    }

}
