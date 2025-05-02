import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PermutationsGenerator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a string to permute: ");
        String input = scanner.nextLine();

        if (input == null || input.isEmpty()) {
            System.out.println("Error: Input string must not be empty.");
            return;
        }

        System.out.println("\nIterative Permutations:");
        List<String> iterativePermutations = generatePermutationsIteratively(input);
        for (String perm : iterativePermutations) {
            System.out.println(perm);
        }

        System.out.println("\nRecursive Permutations:");
        List<String> recursivePermutations = new ArrayList<>();
        generatePermutationsRecursively("", input, recursivePermutations);
        for (String perm : recursivePermutations) {
            System.out.println(perm);
        }
    }

    /**
     * Iteratively generates all permutations of the input string.
     * This implementation uses a queue-like list to build permutations step-by-step.
     */
    public static List<String> generatePermutationsIteratively(String input) {
        List<String> permutations = new ArrayList<>();
        permutations.add("");

        for (char c : input.toCharArray()) {
            List<String> newPermutations = new ArrayList<>();
            for (String perm : permutations) {
                for (int i = 0; i <= perm.length(); i++) {
                    String newPerm = perm.substring(0, i) + c + perm.substring(i);
                    newPermutations.add(newPerm);
                }
            }
            permutations = newPermutations;
        }

        return permutations;
    }

    /**
     * Recursively generates all permutations of the input string.
     * Appends permutations to the result list.
     */
    public static void generatePermutationsRecursively(String prefix, String remaining, List<String> results) {
        if (remaining.length() == 0) {
            results.add(prefix);
        } else {
            for (int i = 0; i < remaining.length(); i++) {
                String newPrefix = prefix + remaining.charAt(i);
                String newRemaining = remaining.substring(0, i) + remaining.substring(i + 1);
                generatePermutationsRecursively(newPrefix, newRemaining, results);
            }
        }
    }
}
