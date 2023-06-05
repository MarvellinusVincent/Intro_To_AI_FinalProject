import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class TicTacToe {
    // Static variables for the TicTacToe class, effectively configuration options
    // Use these instead of hard-coding ' ', 'X', and 'O' as symbols for the game
    // In other words, changing one of these variables should change the program
    // to use that new symbol instead without breaking anything
    // Do NOT add additional static variables to the class!
    static char emptySpaceSymbol = ' ';
    static char playerOneSymbol = 'X';
    static char playerTwoSymbol = 'O';

    static int[] option1 = {1, 0, 1, 0, 1, 0, 1, 0, 1};
    static int[] option2 = {0, 1, 0, 1, 0, 1, 0, 1, 0};

    public static void main(String[] args) {
        // This is where the main menu system of the program will be written.
        // Hint: Since many of the game runner methods take in an array of player names,
        // you'll probably need to collect names here.
        String player1, player2;
        Scanner in = new Scanner(System.in);
        ArrayList<char[][]> latest_game = new ArrayList<>();
        boolean end = false;
        String[] players = new String[2];
        while (!end) {
            // Menu
            System.out.println("Welcome to game of Tic Tac Toe, choose one of the following options from below: ");
            System.out.print("11. Single Player \n2. Two Player \nD. Display Last Match \nQ. Quit \nWhat do you want to do: ");
            String choice = in.nextLine();
            // Running one player game
            if (choice.equals("1")) {
                System.out.print("Enter player 1 name: ");
                player1 = in.next();
                players[0] = player1;
                players[1] = "Computer";
                latest_game = runOnePlayerGame(players);
                in.nextLine();
            }
            // Running two player game
            else if (choice.equals("2")) {
                System.out.print("Enter player 1 name: ");
                player1 = in.next();
                System.out.print("Enter player 2 name: ");
                player2 = in.next();
                players[0] = player1;
                players[1] = player2;
                latest_game = runTwoPlayerGame(players);
                in.nextLine();
            }
            // Displaying previous game
            else if (choice.equals("D")) {
                if (latest_game.isEmpty()) {
                    System.out.println("No match found.");
                }
                else{
                    runGameHistory(players, latest_game);
                }
            }
            // Quit
            else if (choice.equals("Q")) {
                System.out.println("Thanks for playing. Hope you had fun!");
                end = true;
            }
            // Error statement
            else{
                System.out.println("Please enter one of the following: 1, 2, D, Q");
            }
        }
    }
    // Given a state, return a String which is the textual representation of the tic-tac-toe board at that state.
    private static String displayGameFromState(char[][] state) {
        // Hint: Make use of the newline character \n to get everything into one String
        // It would be best practice to do this with a loop, but since we hardcode the game to only use 3x3 boards
        // it's fine to do this without one.
        String separate = " | ";
        String board = " " + state[0][0] + separate + state[0][1] + separate + state[0][2] + " " +"\n-----------\n" +
                       " " + state[1][0] + separate + state[1][1] + separate + state[1][2] + " " +"\n-----------\n" +
                       " " + state[2][0] + separate + state[2][1] + separate + state[2][2] + " ";
        return board;
    }

    // Returns the state of a game that has just started.
    // This method is implemented for you. You can use it as an example of how to utilize the static class variables.
    // As you can see, you can use it just like any other variable, since it is instantiated and given a value already.
    private static char[][] getInitialGameState() {
        return new char[][]{{emptySpaceSymbol, emptySpaceSymbol, emptySpaceSymbol},
                            {emptySpaceSymbol, emptySpaceSymbol, emptySpaceSymbol},
                            {emptySpaceSymbol, emptySpaceSymbol, emptySpaceSymbol}};
    }

    // Given the player names, run the two-player game.
    // Return an ArrayList of game states of each turn -- in other words, the gameHistory
    private static ArrayList<char[][]> runTwoPlayerGame(String[] playerNames) {
        Random rand = new Random();
        String player1 = playerNames[0];
        String player2 = playerNames[1];
        char[][] gameBoardCurrent = getInitialGameState();
        // Initializing array and adding the initial game state to the game history
        ArrayList<char[][]> gameHistory = new ArrayList<>();
        gameHistory.add(getInitialGameState());
        // Choosing who goes first
        System.out.println("Tossing a coin to decide who goes first!!!");
        int coinFlip = rand.nextInt(2);
        // Player 1 goes first
        if (coinFlip == 0){
            System.out.println(player1 + " gets to go first.");
            System.out.println(displayGameFromState(gameBoardCurrent));
            // Looping 9 times for the game
            for (int i = 0; i < option1.length; i ++){
                if (option1[i]== 0){
                    System.out.println(player2 + "'s turn:");
                    gameBoardCurrent = runPlayerMove(player2, playerTwoSymbol, gameBoardCurrent);
                    System.out.println(displayGameFromState(gameBoardCurrent));
                    // Adding the current board to the history
                    gameHistory.add(gameBoardCurrent);
                    // Checking if player 2 wins
                    if (checkWin(gameBoardCurrent)){
                        System.out.println(player2 + " wins!");
                        break;
                    }
                }
                else if (option1[i] == 1){
                    System.out.println(player1 + "'s turn:");
                    gameBoardCurrent = runPlayerMove(player1, playerOneSymbol, gameBoardCurrent);
                    // Adding the current board to the history
                    gameHistory.add(gameBoardCurrent);
                    System.out.println(displayGameFromState(gameBoardCurrent));
                    // Checking if player 1 wins
                    if (checkWin(gameBoardCurrent)){
                        System.out.println(player1 + " wins!");
                        break;
                    }
                }
            }
        }
        // Player 2 goes first
        else if (coinFlip == 1){
            System.out.println(player2 + " gets to go first.");
            System.out.println(displayGameFromState(gameBoardCurrent));
            // Looping 9 times for the game
            for (int i = 0; i < option2.length; i ++){
                if (option2[i] == 0){
                    System.out.println(player2 + "'s turn:");
                    gameBoardCurrent = runPlayerMove(player2, playerTwoSymbol, gameBoardCurrent);
                    // Adding the current board to the history
                    gameHistory.add(gameBoardCurrent);
                    System.out.println(displayGameFromState(gameBoardCurrent));
                    // Checking if player 2 wins
                    if (checkWin(gameBoardCurrent)){
                        System.out.println(player2 + " wins!");
                        break;
                    }
                }
                if (option2[i] == 1){
                    System.out.println(player1 + "'s turn:");
                    gameBoardCurrent = (runPlayerMove(player1, playerOneSymbol, gameBoardCurrent));
                    // Adding the current board to the history
                    gameHistory.add(gameBoardCurrent);
                    System.out.println(displayGameFromState(gameBoardCurrent));
                    // Checking if player 1 wins
                    if (checkWin(gameBoardCurrent)){
                        System.out.println(player1 + " wins!");
                        break;
                    }
                }
            }
        }
        // Checking if game ends in a draw
        if (checkDraw(gameBoardCurrent)){
            System.out.println("Its a draw!");
        }
        return gameHistory;
    }

    // Given the player names (where player two is "Computer"),
    // Run the one-player game.
    // Return an ArrayList of game states of each turn -- in other words, the gameHistory
    private static ArrayList<char[][]> runOnePlayerGame(String[] playerNames) {
        Random rand = new Random();
        String player1 = playerNames[0];
        String player2 = playerNames[1];
        // Initializing array and adding the initial game state to the game history
        char[][] gameBoardCurrent = getInitialGameState();
        ArrayList<char[][]> gameHistory = new ArrayList<>();
        gameHistory.add(getInitialGameState());
        // Choosing who goes first
        System.out.println("Tossing a coin to decide who goes first!!!");
        int coinFlip = rand.nextInt(2);
        // Player 1 goes first
        if (coinFlip == 0){
            System.out.println(player1 + " gets to go first.");
            System.out.println(displayGameFromState(gameBoardCurrent));
            // Looping 9 times for the game
            for (int i = 0; i < option1.length; i ++){
                if (option1[i]== 0){
                    System.out.println(player2 + "'s turn:");
                    // Adding the current board to the history
                    gameBoardCurrent = getCPUMove(gameBoardCurrent);
                    gameHistory.add(gameBoardCurrent);
                    System.out.println(displayGameFromState(gameBoardCurrent));
                    // Checking if player 2 wins
                    if (checkWin(gameBoardCurrent)){
                        System.out.println(player2 + " wins!");
                        break;
                    }
                }
                else if (option1[i] == 1){
                    System.out.println(player1 + "'s turn:");
                    // Adding the current board to the history
                    gameBoardCurrent = runPlayerMove(player1, playerOneSymbol, gameBoardCurrent);
                    gameHistory.add(gameBoardCurrent);
                    System.out.println(displayGameFromState(gameBoardCurrent));
                    // Checking if player 1 wins
                    if (checkWin(gameBoardCurrent)){
                        System.out.println(player1 + " wins!");
                        break;
                    }
                }
            }
        }
        // Computer goes first
        else if (coinFlip == 1){
            System.out.println("Computer gets to go first.");
            System.out.println(displayGameFromState(gameBoardCurrent));
            for (int i = 0; i < option2.length; i ++){
                if (option2[i] == 0){
                    System.out.println(player2 + "'s turn:");
                    // Adding the current board to the history
                    gameBoardCurrent = getCPUMove(gameBoardCurrent);
                    gameHistory.add(gameBoardCurrent);
                    System.out.println(displayGameFromState(gameBoardCurrent));
                    // Checking if player 2 wins
                    if (checkWin(gameBoardCurrent)){
                        System.out.println(player2 + " wins!");
                        break;
                    }
                }
                else if (option2[i] == 1){
                    System.out.println(player1 + "'s turn:");
                    // Adding the current board to the history
                    gameBoardCurrent = (runPlayerMove(player1, playerOneSymbol, gameBoardCurrent));
                    gameHistory.add(gameBoardCurrent);
                    System.out.println(displayGameFromState(gameBoardCurrent));
                    // Checking if player 1 wins
                    if (checkWin(gameBoardCurrent)){
                        System.out.println(player1 + " wins!");
                        break;
                    }
                }
            }
        }
        // Check if the game ends in a draw
        if (checkDraw(gameBoardCurrent)){
            System.out.println("Its a draw!");
        }
        return gameHistory;
    }

    // Repeatedly prompts player for move in current state, returning new state after their valid move is made
    private static char[][] runPlayerMove(String playerName, char playerSymbol, char[][] currentState) {
        int[] move = getInBoundsPlayerMove(playerName);
        boolean check_valid = checkValidMove(move, currentState);
        // Go through a loop if the player move is already taken
        while (!check_valid) {
            System.out.println("That space is already taken. Try again.");
            move = getInBoundsPlayerMove(playerName);
            check_valid = checkValidMove(move, currentState);
        }
        return makeMove(move, playerSymbol, currentState);
    }

    // Repeatedly prompts player for move. Returns [row, column] of their desired move such that row & column are on
    // the 3x3 board, but does not check for availability of that space.
    private static int[] getInBoundsPlayerMove(String playerName) {
        Scanner sc = new Scanner(System.in);
        int[] move = new int[2];
        System.out.print(playerName + " enter row: ");
        int row_index = sc.nextInt();
        System.out.print(playerName + " enter column: ");
        int col_index = sc.nextInt();
        // Go through a loop if the player move is out of bounds
        while (row_index < 0 || row_index > 2 || col_index < 0 || col_index > 2){
            System.out.println("That row or column is out of bounds. Try again");
            System.out.print(playerName + " enter row: ");
            row_index = sc.nextInt();
            System.out.print(playerName + " enter column: ");
            col_index = sc.nextInt();
        }
        move[0] = row_index;
        move[1] = col_index;
        return move;
    }

    // Given a [row, col] move, return true if a space is unclaimed.
    // Doesn't need to check whether move is within bounds of the board.
    private static boolean checkValidMove(int[] move, char[][] state) {
        if (state[move[0]][move[1]] == emptySpaceSymbol) {
            return true;
        }
        return false;
    }

    // Given a [row, col] move, the symbol to add, and a game state,
    // Return a NEW array (do NOT modify the argument currentState) with the new game state
    private static char[][] makeMove(int[] move, char symbol, char[][] currentState) {
        // Hint: Make use of Arrays.copyOf() somehow to copy a 1D array easily
        // You may need to use it multiple times for a 1D array
        char[][] state = new char[currentState.length][];
        for (int i = 0; i < currentState.length; i ++){
            char[] copy = Arrays.copyOf(currentState[i], currentState[i].length);
            state[i] = copy;
        }
        state[move[0]][move[1]] = symbol;
        return state;
    }

    // Given a state, return true if some player has won in that state
    private static boolean checkWin(char[][] state) {
        // Hint: no need to check if player one has won and if player two has won in separate steps,
        // you can just check if the same symbol occurs three times in any row, col, or diagonal (except empty space symbol)
        // But either implementation is valid: do whatever makes most sense to you.
        // Check for rows
        for (int i=0; i<3; i++){
            if ((state[i][0] == state[i][1]) && (state[i][1] == state[i][2]) && (state[i][0] != emptySpaceSymbol)){
                return true;
            }
        }
        // Check for columns
        for (int i=0; i<3; i++){
            if ((state[0][i] == state[1][i]) && (state[1][i] == state[2][i]) && (state[0][i] != emptySpaceSymbol)){
                return true;
            }
        }
        // Check for diagonals
        if ((state[0][0] == state[1][1]) && (state[1][1] == state[2][2]) && state[0][0] != emptySpaceSymbol){
            return true;
        }
        // Check for anti-diagonals
        if ((state[2][0] == state[1][1]) && (state[1][1] == state[0][2]) && state[2][0] != emptySpaceSymbol){
            return true;
        }
        return false;
    }

    // Given a state, simply checks whether all spaces are occupied. Does not care or check if a player has won.
    private static boolean checkDraw(char[][] state) {
        boolean check = checkWin(state);
        if (!check){
            return true;
        }
        return false;
    }

    // Given a game state, return a new game state with move from the AI
    // It follows the algorithm in the PDF to ensure a tie (or win if possible)
    private static char[][] getCPUMove(char[][] gameState) {

        // Hint: you can call makeMove() and not end up returning the result, in order to "test" a move
        // and see what would happen. This is one reason why makeMove() does not modify the state argument
        // Determine all available spaces
        ArrayList<int[]> spaces = getValidMoves(gameState);
        // If there is a winning move available, make that move
        for (int[] i : spaces){
            char[][] newState = makeMove(i, playerTwoSymbol, gameState);
            if(checkWin(newState)){
                return newState;
            }
        }
        // If not, check if opponent has a winning move, and if so, make a move there
        for (int[] i : spaces){
            char[][] newState = makeMove(i, playerOneSymbol, gameState);
            if (checkWin(newState)){
                return makeMove(i, playerTwoSymbol, gameState);
            }
        }
        // If not, move on center space if possible
        for (int[] i : spaces){
            if (Arrays.equals(i, new int[]{1, 1})){
                return makeMove(new int[] {1, 1}, playerTwoSymbol, gameState);
            }
        }
        // If not, move on corner spaces if possible
        ArrayList<int[]> available_corner = new ArrayList<>();
        for (int[] i : spaces){
            if (Arrays.equals(i, new int[] {0, 0}) || Arrays.equals(i, new int[] {0, 2}) ||
            Arrays.equals(i, new int[] {2, 0}) || Arrays.equals(i, new int[] {2, 2})){
                available_corner.add(i);
            }
        }
        if (!available_corner.isEmpty()){
            return makeMove(available_corner.get((int) (Math.random() * available_corner.size())), playerTwoSymbol, gameState);
        }
        // Otherwise, move in any available spot
        return makeMove(spaces.get((int) (Math.random() * spaces.size())), playerTwoSymbol, gameState);
    }

    // Given a game state, return an ArrayList of [row, column] positions that are unclaimed on the board
    private static ArrayList<int[]> getValidMoves(char[][] gameState) {
        ArrayList<int[]> list = new ArrayList<>();
        for (int row_check = 0; row_check < gameState.length; row_check ++){
            for (int col_check = 0; col_check < gameState[0].length; col_check ++){
                if (gameState[row_check][col_check] == emptySpaceSymbol){
                    int temp[] = {row_check, col_check};
                    list.add(temp);
                }
            }
        }
        return list;
    }

    // Given player names and the game history, display the past game as in the PDF sample code output
    private static void runGameHistory(String[] playerNames, ArrayList<char[][]> gameHistory) {
        // We have the names of the players in the format [playerOneName, playerTwoName]
        // Player one always gets 'X' while player two always gets 'O'
        // However, we do not know yet which player went first, but we'll need to know...
        // Hint for the above: which symbol appears after one turn is taken?

        // Hint: iterate over gameHistory using a loop
        int currPlayer = 3;
        int winner = 0;
        for (int i = 0; i < gameHistory.get(1).length; i ++){
            for (int j = 0; j < gameHistory.get(1)[0].length; j ++){
                if (gameHistory.get(1)[i][j] == playerOneSymbol){
                    currPlayer = 0;
                }
                else if (gameHistory.get(1)[i][j] == playerTwoSymbol){
                    currPlayer = 1;
                }
            }
        }
        System.out.println(playerNames[0] + " (" + playerOneSymbol + ") vs " + playerNames[1] + " (" + playerTwoSymbol + ")");
        System.out.println(displayGameFromState(gameHistory.get(0)));
        for (int i = 1; i < gameHistory.size(); i ++){
            System.out.println(playerNames[currPlayer] + ": ");
            System.out.println(displayGameFromState(gameHistory.get(i)));
            winner = currPlayer;
            if(currPlayer == 0){
                currPlayer = 1;
            }
            else{
                currPlayer = 0;
            }
        }
        if (gameHistory.size() == 10){
            System.out.println("Its a draw!");
        }
        else{
            System.out.println(playerNames[winner] + " wins!");
        }
    }
}