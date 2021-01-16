import java.io.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.JSONPointerException;
import org.json.XML;

public class FindSubObjectInJSON {
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

            //build JSON pointer from command line argument
            // args[1] must be of a set of keys or array indices separated by "/" for e.g. "catalog/book/0/price" 
            try{
                JSONPointer jpointer = new JSONPointer(args[1]);
                File outFile = new File("output3.json"); // default output file
                BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
                try{
                    Object subObject = jpointer.queryFrom(json); // cast returned object to JSONObject
                    

                    //write to file depending on the type of object found
                    if (subObject instanceof JSONObject){
                        JSONObject jsubObject = (JSONObject)subObject;
                        bw.write(jsubObject.toString(4)); // written
                    }else if (subObject instanceof JSONArray){
                        JSONArray jsubObject = (JSONArray)subObject;
                        bw.write(jsubObject.toString(4)); // written
                    }else{//normal value just write as it is
                        bw.write(subObject.toString());
                    }
                    bw.flush();
                    bw.close();
                    
                }catch(JSONPointerException jpex){
                    System.err.println("Could not find an object with that key");
                    bw.write("");//write empty string to the file
                    bw.flush();
                    bw.close();
                }
            }catch(IllegalArgumentException iaex){
                System.err.println("Illegal Argument for key-path");
                iaex.printStackTrace();
            }
          
        }catch(IOException ioex){
            ioex.printStackTrace();
        }
    }

}
