import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class test_player {

    private static int size = 5;
    private static int maxStep = size * size - 1;
    private static int piece_type;
    private static int[][] previous_board = new int[size][size];
    private static int[][] board = new int[size][size];
    private static int step;
    private static GO go;

    public static void main(String[] args) {
        try (Scanner in = new Scanner(new File("input.txt"))) {
            readInput(in);
            go = new GO(previous_board, board);
            writeOutput(minimax(go.getPossiblePlacements(piece_type)), new PrintStream(new File("output.txt")));
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.err.println("The program exit.");
        }
    }

    private static int[] minimax(ArrayList<int[]> possible_placements) {
        if (step == 1 && piece_type == 1) return new int[] {1, 1};
        if (possible_placements.isEmpty()) return null;
        double maxValue = Integer.MIN_VALUE;
        Map<int[], Double> valueMap = new HashMap<>();
        for (int[] place : possible_placements) {
            GO test_go = new GO(go, place[0], place[1], piece_type);
            double curValue = minValue(test_go, 6, step, piece_type, Integer.MIN_VALUE, Integer.MAX_VALUE);
            valueMap.put(place, curValue);
            maxValue = curValue > maxValue ? curValue : maxValue;
        }
        for (int[] place : valueMap.keySet())
            if (valueMap.get(place) == maxValue)
                return place;
        return null;
    }

    private static double maxValue(GO go, int depth, int step, int piece_type, double a, double b) {
        if (depth == 1 || step == maxStep) return evaluation(go, step);
        ArrayList<int[]> possible_placements = go.getPossiblePlacements(3 - piece_type);
        if (possible_placements.isEmpty()) return evaluation(go, step);
        double value = Integer.MIN_VALUE;
        for (int[] place : possible_placements) {
            GO test_go = new GO(go, place[0], place[1], 3 - piece_type);
            double curValue = minValue(test_go, depth - 1, step + 1, 3 - piece_type, a, b);
            value = curValue > value ? curValue : value;
            if (value >= b) return value;
            a = value > a ? value : a;
        }
        return value;
    }

    private static double minValue(GO go, int depth, int step, int piece_type, double a, double b) {
        if (depth == 1 || step == maxStep) return evaluation(go, step);
        ArrayList<int[]> possible_placements = go.getPossiblePlacements(3 - piece_type);
        if (possible_placements.isEmpty()) return evaluation(go, step);
        double value = Integer.MAX_VALUE;
        for (int[] place : possible_placements) {
            GO test_go = new GO(go, place[0], place[1], 3 - piece_type);
            double curValue = maxValue(test_go, depth - 1, step + 1, 3 - piece_type, a, b);
            value = curValue < value ? curValue : value;
            if (value <= a) return value;
            b = value < b ? value : b;
        }
        return value;
    }

    private static double evaluation(GO test_go, int step) {
        int oppo_type = 3 - piece_type;
        double my_score = test_go.getScore(piece_type);
        double oppo_score = test_go.getScore(oppo_type);
        if (step == maxStep) return my_score > oppo_score ? 1000 : -1000;
        double my_liberty = test_go.getLiberties(piece_type, 2);
        double oppo_liberty = test_go.getLiberties(oppo_type, 2);
        return my_liberty - oppo_liberty + (my_score - oppo_score) * 50;
    }

    private static void readInput(Scanner in) throws FileNotFoundException{
        piece_type = Integer.parseInt(in.nextLine());
        for (int i = 0; i < size; i++) {
            String line = in.nextLine();
            for (int j = 0; j < size; j++)
                previous_board[i][j] = Integer.parseInt(line.substring(j, j+1));
        }
        for (int i = 0; i < size; i++) {
            String line = in.nextLine();
            for (int j = 0; j < size; j++)
                board[i][j] = Integer.parseInt(line.substring(j, j+1));
        }
        step = getStep();
    }

    private static int getStep() throws FileNotFoundException {
        GO previous = new GO(previous_board, previous_board);
        if (previous.getStones(piece_type).size() == 0 && previous.getStones(3 - piece_type).size() == 0) {
            PrintStream stepOut = new PrintStream(new File("step.txt"));
            stepOut.println(piece_type + 2);
            return piece_type;
        }
        Scanner stepIn = new Scanner(new File("step.txt"));
        int step = stepIn.nextInt();
        PrintStream stepOut = new PrintStream(new File("step.txt"));
        stepOut.println(step + 2);
        return step;
    }

    private static void writeOutput(int[] result, PrintStream out) {
        if (result == null) out.println("PASS");
        else out.println(result[0] + "," + result[1]);
    }
}
