import java.util.ArrayList;

public class GO {

    private int[][] previous_board;
    private int[][] board;
    private int[] freshStone;

    public GO(int[][] previous_board, int[][] board) {
        this.previous_board = deepCopy2DArray(previous_board);
        this.board = deepCopy2DArray(board);
    }

    // PRE: valid_place_check(i, j, piece_type) must be true.
    public GO(GO previous, int i, int j, int piece_type) {
        this.board = deepCopy2DArray(previous.board);
        this.previous_board = deepCopy2DArray(board);
        board[i][j] = piece_type;
        remove_died_pieces(3 - piece_type);
        freshStone = new int[] {i, j, piece_type};
    }

    public int[] getFreshStone() {
        if (freshStone == null) return null;
        return freshStone;
    }

    public double getSafety(int i, int j, int piece_type) {
        double safety = 0;
        for (int[] neighbor : detect_neighbor(i, j)){
            if (board[neighbor[0]][neighbor[1]] == 0) safety += 1;
            if (board[neighbor[0]][neighbor[1]] == piece_type) safety += 0.5;
        }
        return safety;
    }

    /**
     * Detect all the neighbors of a given stone.
     * @return An ArrayList containing the neighbors coordinates
     */
    public ArrayList<int[]> detect_neighbor(int i, int j) {
        ArrayList<int[]> neighbors = new ArrayList<>();
        if (i > 0) neighbors.add(new int[] {i-1, j});
        if (i < board.length - 1) neighbors.add(new int[] {i+1, j});
        if (j > 0) neighbors.add(new int[] {i, j-1});
        if (j < board[0].length - 1) neighbors.add(new int[] {i, j+1});
        return neighbors;
    }

    /**
     * Detect the neighbor allies of a given stone.
     * @return An ArrayList with all the allies of the given stone
     * PER: board[i][j] != 0
     */
    public ArrayList<int[]> detect_neighbor_ally(int i, int j){
        return detect_neighbor_ally(i, j, board);
    }
    private ArrayList<int[]> detect_neighbor_ally(int i, int j, int[][] board) {
        if (board[i][j] == 0) return null;
        ArrayList<int[]> allies = new ArrayList<>();
        int[][] temp = deepCopy2DArray(board);
        allies.add(new int[] {i, j});
        temp[i][j] = 0;
        for (int[] piece : detect_neighbor(i, j))
            if (board[piece[0]][piece[1]] == board[i][j])
                allies.addAll(detect_neighbor_ally(piece[0], piece[1], temp));
        return allies;
    }

    /**
     * Find liberty of a given stone. If a group of allied stones has no liberty, they all die.
     * @return boolean indicating whether the given stone still has liberty
     * PER: board[i][j] != 0
     */
    public boolean find_liberty(int i, int j) {
        for (int[] ally : detect_neighbor_ally(i, j))
            for (int[] neighbor : detect_neighbor(ally[0], ally[1]))
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
     */
    public boolean remove_died_pieces(int piece_type) {
        ArrayList<int[]> died_pieces = find_died_pieces(piece_type);
        if (died_pieces.isEmpty()) return false;
        for (int[] piece : died_pieces)
            board[piece[0]][piece[1]] = 0;
        return true;
    }

    /**
     * Check whether a placement is valid.
     * @return boolean indicating whether the placement is valid.
     */
    public boolean valid_place_check(int i, int j, int piece_type) {
        // check if the place is in the board range
        if (i < 0 || i > board.length - 1) return false;
        if (j < 0 || j > board[0].length - 1) return false;
        // check if the place already has a piece
        if (board[i][j] != 0) return false;
        // copy the board for testing
        GO test_go = new GO(board, board);
        // check if the place has liberty
        test_go.board[i][j] = piece_type;
        if (test_go.find_liberty(i, j)) return true;
        // if not, remove the died pieces of opponent and check again
        if (!test_go.remove_died_pieces(3 - piece_type)) return false;
        // check special cases (KO rule): repeat placement causing the repeat board state
        return !compareBoard(test_go.board, previous_board);
    }

    public ArrayList<int[]> getPossiblePlacements(int piece_type) {
        ArrayList<int[]> possible_placements = new ArrayList<>();
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++)
                if (valid_place_check(i, j, piece_type))
                    possible_placements.add(new int[]{i, j});
        return possible_placements;
    }

    // PRE: piece_type can only be either 1('X') or 2('O')
    public ArrayList<int[]> getStones(int piece_type) {
        ArrayList<int[]> stones = new ArrayList<>();
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++)
                if (board[i][j] == piece_type)
                    stones.add(new int[] {i, j});
        return stones;
    }

    /**
     * Get final score of a player by counting the number of stones and komi
     * @param piece_type 1('X') or 2('O')
     */
    public double getScore(int piece_type) {
        if (piece_type != 1 && piece_type != 2) return 0;
        double score = getStones(piece_type).size();
        // komi for white
        if (piece_type == 2)
            score += board.length / 2.0;
        return score;
    }

    /**
     * Calculate the liberty of a given place.
     */
    public int getLiberty(int i, int j) {
        return (int)getLiberty(i, j, 1);
    }
    public double getLiberty(int i, int j, int depth) {
        double count = 0;
        double factor = 0.2;
        while (depth > 1) {
            for (int[] neighbor: detect_neighbor(i, j))
                if (board[neighbor[0]][neighbor[1]] == 0)
                    count += factor * getLiberty(neighbor[0], neighbor[1], depth - 1);
            depth--;
        }
        for (int[] neighbor : detect_neighbor(i, j))
            if (board[neighbor[0]][neighbor[1]] == 0)
                count += 1;
        return count;
    }

    /**
     * Calculate the liberties of a given player with duplicate.
     * @param piece_type 1('X') or 2('O')
     */
    public int getLiberties(int piece_type){
        return (int)getLiberties(piece_type, 1);
    }
    public double getLiberties(int piece_type, int depth) {
        double count = 0;
        for (int[] piece : getStones(piece_type))
            count += getLiberty(piece[0], piece[1], depth);
        return count;
    }

    // PRE: "board1" and "board2" cannot be null.
    public static boolean compareBoard(int[][] board1, int[][] board2) {
        if (board1.length != board2.length) return false;
        if (board1[0].length != board2[0].length) return false;
        for (int i = 0; i < board1.length; i++)
            for (int j = 0; j < board1[0].length; j++)
                if (board1[i][j] != board2[i][j]) return false;
        return true;
    }

    // PRE: "target" cannot be null.
    public static int[][] deepCopy2DArray(int[][] target) {
        int[][] copy = new int[target.length][target[0].length];
        for(int i = 0; i < target.length; i++)
            for(int j = 0; j < target[0].length; j++)
                copy[i][j] = target[i][j];
        return copy;
    }
}
