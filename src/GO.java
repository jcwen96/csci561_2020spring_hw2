import java.util.ArrayList;

public class GO {

    private int[][] board;
    public static int size = 5;

    public GO(int[][] board) {
        this.board = deepCopy2DArray(board);
    }

    /**
     * Detect all the neighbors of a given stone.
     * @param i
     * @param j
     * @param board
     * @return An ArrayList containing the neighbors coordinates
     */
    public static ArrayList<int[]> detect_neighbor(int i, int j, int[][] board) {
        ArrayList<int[]> neighbors = new ArrayList<>();
        if (i > 0) neighbors.add(new int[] {i-1, j});
        if (i < board.length-1) neighbors.add(new int[] {i+1, j});
        if (j > 0) neighbors.add(new int[] {i, j-1});
        if (j < board[0].length-1) neighbors.add(new int[] {i, j+1});
        return neighbors;
    }

    /**
     * Detect the neighbor allies of a given stone.
     * @param i
     * @param j
     * @param board
     * @return An ArrayList with all the allies of the given stone
     * PER: board[i][j] != 0
     */
    public static ArrayList<int[]> detect_neighbor_ally(int i, int j, int[][] board) {
        ArrayList<int[]> allies = new ArrayList<>();
        if (board[i][j] == 0) return allies;
        int[][] temp = deepCopy2DArray(board);
        allies.add(new int[] {i, j});
        temp[i][j] = 0;
        for (int[] piece : detect_neighbor(i, j, temp))
            if (board[piece[0]][piece[1]] == board[i][j])
                allies.addAll(detect_neighbor_ally(piece[0], piece[1], temp));
        return allies;
    }

    /**
     * Find liberty of a given stone. If a group of allied stones has no liberty, they all die.
     * @param i
     * @param j
     * @return boolean indicating whether the given stone still has liberty
     * PER: board[i][j] != 0
     */
    public boolean find_liberty(int i, int j) {
        for (int[] ally : detect_neighbor_ally(i, j, board))
            for (int[] neighbor: detect_neighbor(ally[0], ally[1], board))
                if (board[neighbor[0]][neighbor[1]] == 0)
                    return true;
        return false;
    }

    /**
     * Find the died stones that has no liberty in the board for a given piece type.
     * @param piece_type 1('X') or 2('O')
     * @return An ArrayList containing the dead pieces row and column
     */
    public ArrayList<int[]> find_died_pieces(int piece_type) {
        ArrayList<int[]> died_pieces = new ArrayList<>();
        if (piece_type != 1 || piece_type != 2) return died_pieces;
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++)
                if (board[i][j] == piece_type)
                    if (!find_liberty(i, j))
                        died_pieces.add(new int[] {i, j});
        return died_pieces;
    }

    /**
     * Remove the dead stones in the board.
     * @param piece_type 1('X') or 2('O')
     * @return
     */
    public boolean remove_died_pieces(int piece_type) {
        if (piece_type != 1 || piece_type != 2) return false;
        ArrayList<int[]> died_pieces = find_died_pieces(piece_type);
        if (died_pieces.isEmpty()) return false;
        for (int[] piece : died_pieces)
            board[piece[0]][piece[1]] = 0;
        return true;
    }

    /**
     * Check whether a placement is valid.
     * @param i
     * @param j
     * @param piece_type 1('X') or 2('O')
     * @return boolean indicating whether the placement is valid.
     */
    public boolean valid_place_check(int i, int j, int piece_type, int[][] previous_board) {
        // check if the place is in the board range
        if (i < 0 || i > size - 1) return false;
        if (j < 0 || j > size - 1) return false;
        // check if the place already has a piece
        if (board[i][j] != 0) return false;
        // copy the board for testing
        GO test_go = new GO(board);
        // check if the place has liberty
        test_go.board[i][j] = piece_type;
        if (test_go.find_liberty(i, j)) return true;
        // if not, remove the died pieces of opponent and check again
        if (!test_go.remove_died_pieces(3 - piece_type)) return false;
        assert test_go.find_liberty(i, j): "test_go.find_liberty(i, j) should return true";
        // check special cases (KO rule): repeat placement causing the repeat board state
        if (compareBoard(test_go.board, previous_board)) return false;
        return true;
    }

    public static boolean compareBoard(int[][] board1, int[][] board2) {
        if (board1.length != board2.length) return false;
        if (board1[0].length != board2[0].length) return false;
        for (int i = 0; i < board1.length; i++)
            for (int j = 0; j < board1[0].length; j++)
                if (board1[i][j] != board2[i][j]) return false;
        return true;
    }

    public static int[][] deepCopy2DArray(int[][] target) {
        int[][] copy = new int[target.length][target[0].length];
        for(int i = 0; i < target.length; i++)
            for(int j = 0; j < target[0].length; j++)
                copy[i][j] = target[i][j];
        return copy;
    }
}
