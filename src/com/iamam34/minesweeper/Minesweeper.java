package com.iamam34.minesweeper;

import java.util.Random;


/**
 * Holds game state and implements player actions.
 * Each square might contain a mine.
 * Each square may be flagged or deflagged.
 * Each square may become revealed once only (this action is not reversible).
 * Displayed as: obscured OR flagged OR number of neighbours (may be zero)
 */
public class Minesweeper {
	/**
	 * number of rows in board
	 */
	public final int numRows;
	/**
	 * number of columns in board
	 */
	public final int numCols;
	/**
	 * number of mines
	 */
	public final int numMines;
	/**
	 * marks a square that has not yet been revealed (as mine or safe)
	 */
	public static final int IS_HIDDEN = -1;
	/**
	 *  marks a square that has been flagged by user as a potential minesite
	 */
	public static final int IS_FLAGGED = -2;
	/**
	 * marks a square that contains a mine
	 */
	public static final int IS_MINE = -3;
	
	private int[][] fact;
	private int[][] display;
	
	/**
	 * @param numRows
	 * @param numCols
	 * @param numMines 
	 */
	public Minesweeper(int numRows, int numCols, int numMines) {
		this.numRows = numRows;
		this.numCols = numCols;
		this.numMines = numMines;
		newGame();
	}
	
	/**
	 * Toggle the square's displayed state between IS_FLAGGED and IS_HIDDEN.
	 * @param rowIndex
	 * @param colIndex
	 * @throws UnsupportedOperationException "Can't flag a square that has been revealed."
	 */
	public void flagSquare(int rowIndex, int colIndex) throws UnsupportedOperationException {
		int currentState = display[rowIndex][colIndex];
		switch (currentState) {
		case IS_HIDDEN:
			display[rowIndex][colIndex] = IS_FLAGGED;
			break;
		case IS_FLAGGED:
			display[rowIndex][colIndex] = IS_HIDDEN;	
			break;
		default:// else do nothing; can't flag revealed square
			throw new UnsupportedOperationException("Can't flag a square that has been revealed.");
		}
	}
	
	/**
	 * Reveals the number of adjacent mines to this square, growing to maximum area.
	 * @param rowIndex
	 * @param colIndex
	 * @return true if the square contains a mine -- use this to trigger game over; false if safe
	 */
	public boolean revealSquare(int rowIndex, int colIndex) {
		if (fact[rowIndex][colIndex] == IS_MINE) {
			return true;
		}
		if (display[rowIndex][colIndex] == IS_HIDDEN) {
			inspectSquare(rowIndex, colIndex);
			return false;
		} else {
			// else do nothing; can't reveal already-revealed square
			throw new UnsupportedOperationException("Can't reveal a square that is not hidden.");
		}
	}
	
	private void inspectSquare(int rowIndex, int colIndex) {
		if ((0 <= rowIndex && rowIndex < numRows && 0 <= colIndex && colIndex < numCols) && 
				(display[rowIndex][colIndex] == IS_HIDDEN && fact[rowIndex][colIndex] != IS_MINE)) {
			display[rowIndex][colIndex] = fact[rowIndex][colIndex]; // show number of neighbours
			if (fact[rowIndex][colIndex] == 0) {
				for (int r = -1; r <= 1; r++) {
					for (int c = -1; c <= 1; c++) {
						inspectSquare(rowIndex+r, colIndex+c);
					}
				}
			}
		}
	}
	
	/**
	 * Randomly generate a mine configuration.
	 */
	public void newGame() {
		// set all squares to 0 in fact and display
		fact = new int[numRows][numCols];
		display = new int[numRows][numCols];
		
		// hide all squares
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				display[row][col] = IS_HIDDEN;
			}
		}
		
		// generate mines
		int[][] mineLocations = new int[numMines][2];
		Random random = new Random();

		for (int i = 0; i < numMines; i++) {
			int[] pos;
			boolean duplicate;
			do {
				duplicate = false;
				pos = new int[] {random.nextInt(numRows), random.nextInt(numCols)};
				for (int j = 0; j < i; j++) {
					if (mineLocations[j][0] == pos[0] && mineLocations[j][1] == pos[1]) {
						duplicate = true;
						break;
					}
				}
			} while (duplicate);
			mineLocations[i] = pos;
		}

		// count neighbours
		for (int[] mineLocation: mineLocations) {
			int rowIndex = mineLocation[0];
			int colIndex = mineLocation[1];
			for (int row = Math.max(0, rowIndex - 1); row < Math.min(numRows, rowIndex + 2); row++) {
				for (int col = Math.max(0, colIndex - 1); col < Math.min(numCols, colIndex + 2); col++) {
					if (!(col == colIndex && row == rowIndex)) {
						fact[row][col]++;
					}
				}
			}
		}
		
		for (int[] mineLocation: mineLocations) {
			int rowIndex = mineLocation[0];
			int colIndex = mineLocation[1];
			fact[rowIndex][colIndex] = IS_MINE;
		}

		System.out.println();
	}


	/**
	 * @return true if and only if all flagged squares contain mines AND all mines are flagged.
	 */
	public boolean checkForWin() {
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				boolean isFlagged = (display[row][col] == IS_FLAGGED);
				boolean isMine = (fact[row][col] == IS_MINE);
				if (isFlagged != isMine) {
					return false; // you lose!!
				}
			}
		}
		return true; // if and only if all flags line up with all mines, you win.
	}

	/**
	 * @return display state for all squares in board
	 */
	public int[][] getDisplay() {
		return display;
	}
}
