import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Main {
    static Map<String, Integer> wordCounts = new HashMap<String, Integer>();

    public static Map<String, Integer> sortByValue(Map<String, Integer> wordCounts2) {
        HashMap<String, Integer> temp = wordCounts2.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        return temp;
    }

    public static void main(String[] args) {
        File directoryPath = new File("./input");
        File[] listFile = directoryPath.listFiles();

//         System.out.println(listFile);

       List<Future<HashMap<String, Integer>>> list = new ArrayList<>();
       ExecutorService executor = Executors.newFixedThreadPool(5);

       Callable<HashMap<String, Integer>> callable;
       Future<HashMap<String, Integer>> future;

       for (File file : listFile) {
           callable = new Worker(file.getPath());
           future = executor.submit(callable);

           list.add(future);
       }

       // shut down the executor service now
       executor.shutdown();

       // Wait until all threads are finish
       while (!executor.isTerminated()) {
           // Running …
       }

       for (Future<HashMap<String, Integer>> f : list) {
           try {
               HashMap<String, Integer> tmp = f.get();
               tmp.forEach((key, value) -> wordCounts.merge(key, value, (v1, v2) -> v1 + v2));

           } catch (InterruptedException e) {
               e.printStackTrace();
           } catch (ExecutionException e) {
               e.printStackTrace();
           }
       }

       Map<String, Integer> res = sortByValue(wordCounts);

       try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
           writer.write("Top 10 tu xuat hien nhieu nhat: \n");
           res.forEach((k, v) -> {
               try {
                   String t = k + " = " + v;
                   writer.write(t + "\n");
               } catch (IOException e) {
                   e.printStackTrace();
               }
           });
       } catch (IOException e) {
           e.printStackTrace();
       }

        System.out.println("OK!!!");
    }
}
