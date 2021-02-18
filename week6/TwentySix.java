import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TwentySix {
    public static void create_db_schema(Connection connection) throws SQLException {
        // create connection
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30); // set timeout to 30 sec.
        System.out.println("Creating tables...");
        statement.execute("CREATE TABLE IF NOT EXISTS documents (id INTEGER PRIMARY KEY AUTOINCREMENT, name)");
        statement.execute("CREATE TABLE IF NOT EXISTS words (id, doc_id, value)");
        statement.execute("CREATE TABLE IF NOT EXISTS characters (id, word_id, value)");
    }

    public static void load_file_into_database(String path_to_file, Connection conn) throws SQLException, IOException {
        System.out.println("Loading files into database...");
        List<String> wordList;
        class ExtractWords {
            private String path_to_file;

            ExtractWords(String filePath) {
                this.path_to_file = filePath;
            }

            public List<String> run() throws IOException {
                List<String> allWords = new ArrayList<>();
                //read book words
                BufferedReader br = new BufferedReader(new FileReader(this.path_to_file));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine())!=null){
                    sb.append(line+ " ");
                }
                br.close();
                allWords.addAll(Arrays.asList(sb.toString().toLowerCase().replaceAll("[\\W_]", " ").split(" ")));
                //read stop words
                List<String> stopWords = new ArrayList<>();
                BufferedReader br2 = new BufferedReader(new FileReader("../stop_words.txt"));
                StringBuilder sb2 = new StringBuilder();
                String line2;
                while ((line2 = br2.readLine())!=null){
                    sb2.append(line2+ ",");
                }
                br2.close();
                stopWords.addAll(Arrays.asList(sb2.toString().split(",")));
                // append ascii character
                for (int i=0; i<26; i++){
                    stopWords.add(Character.toString('a'+i));
                }
                return allWords.stream().filter(w -> !stopWords.contains(w)).filter(word -> word.length()>1).collect(Collectors.toList());

            } // end run method
        } // end ExtractWords

        try{
            wordList = new ExtractWords(path_to_file).run();
            String sql = "INSERT INTO documents (name) VALUES(?)";
            PreparedStatement pstatement = conn.prepareStatement(sql);
            pstatement.setString(1, path_to_file);
            pstatement.executeUpdate();
            // get the desired document id
            pstatement = conn.prepareStatement("SELECT id from documents WHERE name=?");
            pstatement.setString(1, path_to_file);
            ResultSet rs = pstatement.executeQuery();
            int document_id = rs.getInt("id");
            // find the last word index in the words database
            pstatement = conn.prepareStatement("SELECT MAX(id) FROM words");
            rs = pstatement.executeQuery();
            int word_id = rs.getInt(1);// would be 0 if SQL NUll was read
            //add words to the database
            // set auto-commit to false. Will commit bunch of operations together to speed up.
            conn.setAutoCommit(false);
            for (String word: wordList){
                pstatement = conn.prepareStatement("INSERT INTO words VALUES(?,?,?)");
                pstatement.setInt(1, word_id);
                pstatement.setInt(2, document_id);
                pstatement.setString(3, word);
                pstatement.executeUpdate();
                // insert characters into table
                for (int i=0; i<word.length(); i++){
                    pstatement = conn.prepareStatement("INSERT INTO characters VALUES(?,?,?)");
                    pstatement.setInt(1, i);
                    pstatement.setInt(2, word_id);
                    pstatement.setInt(3, word.charAt(i));
                    pstatement.executeUpdate();
                }
                word_id +=1;
                if (word_id %100 ==0){
                    conn.commit();
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
        }catch(IOException ex){
            throw new IOException(ex.getMessage());
        }
            
    }// end load into database

    public static void main(String[] args) {
            Connection connection = null;
            Boolean toLoadTables = Files.notExists(Path.of("tf.db"));
            try{
            connection = DriverManager.getConnection("jdbc:sqlite:tf.db");
            if (toLoadTables){
                create_db_schema(connection);
                load_file_into_database(args[0], connection);
            }
            ResultSet rs = connection.prepareStatement("SELECT value, COUNT(*) as C FROM words GROUP BY value ORDER BY C DESC").executeQuery();
            // print top 25 words
            int index = 1;
            while (index <=25 && rs.next()){
                System.out.println(rs.getString("value") + "-" + rs.getString("C"));
                index +=1; // increment index
            }
            System.out.println("-----------Unique words with z in them------------------");
            
            PreparedStatement pstatement = connection.prepareStatement("SELECT value FROM words WHERE value LIKE ? GROUP BY value");
            pstatement.setString(1, "%z%");
            rs = pstatement.executeQuery();
            int countwithz = 0;
            while(rs.next()){
                System.out.println(rs.getString("value"));
                countwithz++;
            }
            System.out.println("Count of unique words with z = "+ countwithz);

        }catch(SQLException ex){
            //System.err.println(ex.getMessage());
            ex.printStackTrace();
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }
        finally{
            if (connection!= null){
                try{
                    connection.close();
                }catch(SQLException ex2){
                    // connection close failed
                    System.err.println(ex2.getMessage());
                }
            }
        }
    }
    
}