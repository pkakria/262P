import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

class twentyEight {
    static List<String> stopWords;
    static HashMap<String, Integer> wordFreqs = new HashMap<>();
    static long countInputs = 0; // to keep track of how many inputs have passed through me. Signals end of stream if countInputs<BATCH_SIZE
    static final int BATCH_SIZE = 5000;
    
    //initializes the static stopWords list
    public static void readStopWords() throws IOException {
        stopWords = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader("../stop_words.txt"));
        stopWords.addAll(Arrays.asList(br.readLine().split(",")));
        br.close();
        for (int i=0; i<26; i++){
            stopWords.add(Character.toString('a'+i));
        }
    }

    //read all words
    public static Stream<String> allWords(String filename) throws FileNotFoundException, IOException{
        return Files.lines(Path.of(filename)).map((line)-> line.toLowerCase().replaceAll("[\\W_]", " ").split(" ")).flatMap((arr) -> Arrays.stream(arr));
    }

    //filter out stop_words
    public static boolean non_stop_words(String word) {
        return !stopWords.contains(word);
    }

    //counter for word frequency. returns the up to date entrySet each time.
    public static Set<Map.Entry<String, Integer>> count_frequency(String word){
        // either get the current frequency or default (0) and add 1 to the frequency
        if (word.length()>1){
            wordFreqs.put(word, wordFreqs.getOrDefault(word, 0)+1);
        }
        return wordFreqs.entrySet();
    }

    public static Set<Map.Entry<String, Integer>> counter(Set<Map.Entry<String, Integer>> obj){
        countInputs++;
        return obj;
    }

    public static Set<Map.Entry<String, Integer>> intermediatePrint(Set<Map.Entry<String, Integer>> obj){
        // print the list if countInputs%BATCH_SIZE==0
        if (countInputs%BATCH_SIZE==0){
            obj.stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(25)
            .forEach(entry-> System.out.println(entry.getKey()+ "-"+ entry.getValue()));;
            System.out.println("------------Latest Update--------------------");
        }
        return obj;
    }
    public static void main(String[] args) {
        try {
            readStopWords();
                System.out.println("------------Latest Update--------------------");
                allWords(args[0])
                .filter((word) -> non_stop_words(word))
                .map((word)-> count_frequency(word))
                .map(entrySet-> counter(entrySet))
                .map(entrySet -> intermediatePrint(entrySet))
                .reduce((a,b)-> b)
                .orElse(null)
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(25)
                .forEach((entry)-> System.out.println(entry.getKey()+ "-"+ entry.getValue()));
                System.out.println("--------------Finished processing-----------------");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
}