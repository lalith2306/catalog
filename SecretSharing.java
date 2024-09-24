import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class SecretSharing {

    public static void main(String[] args) {
        // Process both JSON files
        processFile("input.json");
        processFile("input1.json");
    }

    // Method to read and process a JSON file
    private static void processFile(String jsonFilePath) {
        String jsonData = readJsonFile(jsonFilePath);
        
        if (jsonData != null) {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject keys = jsonObject.getJSONObject("keys");
            int n = keys.getInt("n");
            int k = keys.getInt("k");

            Map<Integer, BigInteger> xValues = new HashMap<>();
            Map<Integer, BigInteger> yValues = new HashMap<>();
            
            int count = 0;
            for (int i = 1; i <= n && count < k; i++) {
                if (jsonObject.has(String.valueOf(i))) {
                    JSONObject root = jsonObject.getJSONObject(String.valueOf(i));
                    int base = Integer.parseInt(root.getString("base"));
                    String value = root.getString("value");

                    BigInteger y = new BigInteger(value, base);
                    xValues.put(i, BigInteger.valueOf(i));
                    yValues.put(i, y);
                    
                    count++;
                }
            }

            // Calculate constant term 'c'
            BigInteger c = calculateConstantTerm(xValues, yValues, k);
            System.out.println("The constant term c from " + jsonFilePath + " is: " + c);
        }
    }

    // Method to read the JSON file
    private static String readJsonFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return content.toString();
    }

    // Lagrange interpolation to find the constant term (c)
    private static BigInteger calculateConstantTerm(Map<Integer, BigInteger> xValues, Map<Integer, BigInteger> yValues, int k) {
        BigInteger result = BigInteger.ZERO;
        
        for (int i = 1; i <= k; i++) {
            BigInteger term = yValues.get(i);
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;
            
            for (int j = 1; j <= k; j++) {
                if (i != j) {
                    numerator = numerator.multiply(BigInteger.ZERO.subtract(xValues.get(j)));
                    denominator = denominator.multiply(xValues.get(i).subtract(xValues.get(j)));
                }
            }
            
            term = term.multiply(numerator).divide(denominator);
            result = result.add(term);
        }
        
        return result;
    }
}