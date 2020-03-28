import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class my_player {

    private static int size = 5;
    private static int maxStep = size * size - 1;
    private static int piece_type;
    private static int[][] previous_board = new int[size][size];
    private static int[][] board = new int[size][size];
    private static int step;
    private static int maxDepth = 6;
    private static GO go;
    private static long startTime;

    public static void main(String[] args) {
        startTime = System.currentTimeMillis();
        try (Scanner in = new Scanner(new File("input.txt"))) {
            readInput(in);
            go = new GO(previous_board, board);
            writeOutput(alpha_beta(), new PrintStream(new File("output.txt")));
            System.err.println(System.currentTimeMillis() - startTime);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.err.println("The program exit.");
        }
    }

    private static int[] alpha_beta() {
        if (step == 1) return new int[] {2, 2};
        if (step == 2) return go.valid_place_check(2, 2, piece_type) ? new int[] {2, 2}: new int[] {2, 1};
        ArrayList<int[]> possible_placements = go.getWorthPlacements(piece_type);
        if (possible_placements.isEmpty()) return null;
        int[][] reward = new int[][] {
                {-100, 0, 5, 0, -100},
                {0, 5, 10, 5, 0,},
                {5, 10, 100, 10, 5},
                {0, 5, 10, 5, 0},
                {-100, 0, 5, 0, -100}};
        double maxValue = Integer.MIN_VALUE;
        Map<int[], Double> valueMap = new HashMap<>();
        for (int[] place : possible_placements) {
            if (System.currentTimeMillis() - startTime > 9600) {
                System.err.println("Interrupt alpha_beta.");
                break;
            }
            GO test_go = new GO(go, place[0], place[1], piece_type);
            int liberty = test_go.getLiberty(place[0], place[1]);
            int defence = test_go.getDefence(place[0], place[1]);
            double minimax = minValue(test_go, 1, step + 1, 3 - piece_type, Integer.MIN_VALUE, Integer.MAX_VALUE);
            double curValue =  minimax + liberty + defence + reward[place[0]][place[1]] * 0.1;
            valueMap.put(place, curValue);
            maxValue = curValue > maxValue ? curValue : maxValue;
        }
        for (int[] place : valueMap.keySet())
            if (valueMap.get(place) == maxValue) return place;
        return null;
    }

    private static double maxValue(GO go, int depth, int step, int piece_type, double a, double b) {
        if (depth == maxDepth || step > maxStep) return evaluation(go);
        ArrayList<int[]> possible_placements = go.getWorthPlacements(piece_type);
        if (possible_placements.isEmpty()) return evaluation(go);
        double value = Integer.MIN_VALUE;
        for (int[] place : possible_placements) {
            GO test_go = new GO(go, place[0], place[1], piece_type);
            double curValue = minValue(test_go, depth + 1, step + 1, 3 - piece_type, a, b);
            value = curValue > value ? curValue : value;
            if (value >= b) return value;
            a = value > a ? value : a;
        }
        return value;
    }

    private static double minValue(GO go, int depth, int step, int piece_type, double a, double b) {
        if (depth == maxDepth || step > maxStep) return evaluation(go);
        ArrayList<int[]> possible_placements = go.getWorthPlacements(piece_type);
        if (possible_placements.isEmpty()) return evaluation(go);
        double value = Integer.MAX_VALUE;
        for (int[] place : possible_placements) {
            GO test_go = new GO(go, place[0], place[1], piece_type);
            double curValue = maxValue(test_go, depth + 1, step + 1, 3 - piece_type, a, b);
            value = curValue < value ? curValue : value;
            if (value <= a) return value;
            b = value < b ? value : b;
        }
        return value;
    }

    private static double evaluation(GO go) {
        return 100 * (go.getScore(piece_type) - go.getScore(3 - piece_type));
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
        if (GO.compareBoard(previous_board, new int[size][size])) {
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
        if (result == null) out.print("PASS");
        else out.print(result[0] + "," + result[1]);
    }

}
