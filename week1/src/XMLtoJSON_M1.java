import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;
import org.json.XML;

public class XMLtoJSON_M1 {
    public static void main(String[] args) throws FileNotFoundException {
        String XMLFilePathName = args[0];
        File file = new File(XMLFilePathName); // the xml file
      
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder file_string = new StringBuilder("");
            while ((line = br.readLine())!= null){
                file_string.append(line);
                file_string.append(System.lineSeparator());
            }
            String xmlText = file_string.toString();
            br.close();
            // convert XML to JSON
            JSONObject json = XML.toJSONObject(xmlText);
            String jsonString = json.toString(4); //indent factor of 4 for pretty print

            //write json object to file1.json
            File fileFinal = new File("output1.json");
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileFinal));
            bw.write(jsonString);
            bw.flush();
            bw.close();
        }
        catch(FileNotFoundException f) {
            System.out.println("File not found");
        }
        catch(IOException ex) {
            System.out.println("can not read file");
        }

    }
}
