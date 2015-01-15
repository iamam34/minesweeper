package com.iamam34.minesweeper;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * TODO only update view of *modified* squares, use flag picture, freeze at gameover, save & load, remember number of deaths
 * @author amy
 *
 */
public class MinesweeperGui extends JFrame {
	private static final String GAME_NOT_SAVED_WARNING = "All current game progress will be lost.";
    private static final String WINDOW_TITLE = "Minesweeper";
	
    private final Minesweeper game;
	private final JButton[][] squares;
    private boolean isPlaying = true;
	
	private MinesweeperGui(Minesweeper game) {
		super(WINDOW_TITLE);
		this.game = game;
		squares = new JButton[game.numRows][game.numCols];
	}
	
	/**
	 * Construct and display a GUI frontend
	 */
	public void createAndShowGUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Set up the content pane.
        this.addSquaresToBoard(this.getContentPane());
        
        //Set up the menus.
        final Container frame = this; // TODO ugly.
        MenuBar mb = new MenuBar();
        Menu m = new Menu("Game");
        mb.add(m);
        MenuItem mi = new MenuItem("New");
        m.add(mi);
        mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (JOptionPane.showConfirmDialog(frame, GAME_NOT_SAVED_WARNING, "New Game", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					isPlaying = true;
					game.newGame();
					updateView();
				}
			}
		});
        MenuItem mi2 = new MenuItem("Quit");
        m.add(mi2);
        mi2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (JOptionPane.showConfirmDialog(frame, GAME_NOT_SAVED_WARNING, "Quit Game", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});
        this.setMenuBar(mb);
        
        //Display the window.
        this.pack();
        this.setVisible(true);
	}
	
	private void addSquaresToBoard(final Container board) {
		board.setLayout(new GridLayout(game.numRows, game.numCols));
		
		//Set up components preferred size
        JButton b = new JButton("_");
        double w = b.getPreferredSize().getWidth()*1.5;
		board.setPreferredSize(new Dimension((int) w*game.numCols, (int) w*game.numRows));
        
        for (int row = 0; row < game.numRows; row++) {
            for (int col = 0; col < game.numCols; col++) {
            	final int rowIndex = row;
            	final int colIndex = col;
            	final JButton square = new JButton();
            	squares[row][col] = square;
            	board.add(square);
            	square.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (isPlaying) {
                        	if (SwingUtilities.isRightMouseButton(e)) {
                				game.flagSquare(rowIndex, colIndex);
                    			updateView();
                				if (game.checkForWin() == true) {
                					JOptionPane.showMessageDialog(board, "Victory!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                					isPlaying = false;
                				}
                			}
                			else {
                				if (!(game.getDisplay()[rowIndex][colIndex] == Minesweeper.IS_FLAGGED)) {
                    				boolean isMine = game.revealSquare(rowIndex, colIndex);
                        			updateView();
                    				if (isMine) {
                    					JOptionPane.showMessageDialog(board, "You lose!", "Game Over", JOptionPane.ERROR_MESSAGE);
                    					isPlaying = false;
                    				}
                				}
                			}
            			}
                    }             
                });
            }
        }
        updateView();
	}
    
	private void updateView() {
		int[][] display = game.getDisplay();
		for (int row = 0; row < game.numRows; row++) {
			System.out.println(Arrays.toString(display[row]));
			for (int col = 0; col < game.numCols; col++) {
				switch (display[row][col]) {
				case Minesweeper.IS_FLAGGED:
					squares[row][col].setText("F");
					break;
				case Minesweeper.IS_HIDDEN:
					squares[row][col].setEnabled(true);
					squares[row][col].setText("");
					break;
				default:
					squares[row][col].setEnabled(false);
					int n = display[row][col];
					if (n == 0) {
						squares[row][col].setText("");
					} else {
						squares[row][col].setText(Integer.toString(n));
					}
				}
			}
		}

	}
	
	
	@SuppressWarnings("javadoc")
	public static void main(String[] args) {
		final Minesweeper game = new Minesweeper(9, 9, 10);
		
    	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		//Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() {
                new MinesweeperGui(game).createAndShowGUI();
            }
        });
	}
}
