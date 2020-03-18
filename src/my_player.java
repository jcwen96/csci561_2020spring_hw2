import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class my_player {

    private static int piece_type;
    public static int size = 5;
    private static int[][] previous_board = new int[size][size];
    private static int[][] board = new int[size][size];


    public static void main(String[] args) {
        try (Scanner in = new Scanner(new File("input.txt"))) {
            readInput(in);
            ArrayList<int[]> possible_placements = new ArrayList<>();
            GO go = new GO(board);
            for (int i = 0; i < size; i++){
                for (int j = 0; j < size; j++) {
                    if (go.valid_place_check(i, j, piece_type, previous_board))
                        possible_placements.add(new int[] {i, j});
                }
            }
            Random r = new Random();
            if (possible_placements.isEmpty()) writeOutput(null, new PrintStream(new File("output.txt")));
            else writeOutput(possible_placements.get(r.nextInt(possible_placements.size())), new PrintStream(new File("output.txt")));


        } catch (FileNotFoundException e) {
            System.err.println("Warning: File \"input.txt\" does not exist!");
            System.err.println("The program exit.");
        }
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






}
