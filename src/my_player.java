import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

public class my_player {

    private static int piece_type;
    public static int size = 5;
    private static int[][] previous_board = new int[size][size];
    private static int[][] board = new int[size][size];
    private static GO go;

    public static void main(String[] args) {
        try (Scanner in = new Scanner(new File("input.txt"))) {
            readInput(in);
            go = new GO(board);

//            for (int[] place : getPossiblePlacements()) {
//                System.out.println(Arrays.toString(place) + " " + evaluation(place[0], place[1]));
////                System.out.println(Arrays.toString(place) + " " + go.getLiberty(place[0], place[1], 3));
//            }

            writeOutput(greedy(getPossiblePlacements()), new PrintStream(new File("output.txt")));
        } catch (FileNotFoundException e) {
            System.err.println("Warning: File \"input.txt\" does not exist!");
            System.err.println("The program exit.");
        }
    }

    private static int[] minimax() {
        return null;
    }

    private static int[] greedy(ArrayList<int[]> possible_placements) {
        double maxEvaluation = 0;
        for (int[] place : possible_placements) {
            double curEvalutation = evaluation(place[0], place[1]);
            maxEvaluation = curEvalutation > maxEvaluation ? curEvalutation : maxEvaluation;
        }
        ArrayList<int[]> prefer_placements = new ArrayList<>();
        for (int[] place : possible_placements)
            if (evaluation(place[0], place[1]) == maxEvaluation)
                prefer_placements.add(new int[] {place[0], place[1]});
        Random r = new Random();
        return prefer_placements.get(r.nextInt(prefer_placements.size()));
    }

    private static double evaluation(int i, int j) {
        int oppo_type = 3 - piece_type;
        GO test_go = new GO(board);
        test_go.updateBoard(i, j, piece_type);
        test_go.remove_died_pieces(oppo_type);
        double my_liberty_delta = test_go.getLiberty_2(piece_type) - go.getLiberty_2(piece_type);
        double oppo_liberty_delta = test_go.getLiberty_2(oppo_type) - go.getLiberty_2(oppo_type);
        int kill = (int)(go.getScore(3 - piece_type) - test_go.getScore(3 - piece_type));
        double kill_facor = 0.5;
        if (kill > 1) kill_facor = 1;
        if (kill > 2) kill_facor = 10;
        return kill * kill_facor + my_liberty_delta - oppo_liberty_delta;
    }

    private static void readInput(Scanner in) {
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
    }

    private static void writeOutput(int[] result, PrintStream out) {
        if (result == null) {
            out.println("PASS");
            return;
        }
        out.println(result[0] + "," + result[1]);
    }

    private static ArrayList<int[]> getPossiblePlacements() {
        ArrayList<int[]> possible_placements = new ArrayList<>();
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (go.valid_place_check(i, j, piece_type, previous_board))
                    possible_placements.add(new int[] {i, j});
        return possible_placements;
    }

}
