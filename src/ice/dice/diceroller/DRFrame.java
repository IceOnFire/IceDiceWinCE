/*
 * Created on 18-set-2004
 * 
 * Last modified on 29-nov-2004
 *
 * The ice.dice.diceroller package gives a small SuperWaba application for
 * rolling up to 10 dice of 7 different kinds. It also provides automatic
 * calculations which are very useful for the 2 games it is addressed to:
 * Vampire: The Masquerade and Dungeons&Dragons v3.5.
 */
package ice.dice.diceroller;

import superwaba.ext.xplat.ui.GridContainer;

import waba.sys.Convert;

import waba.fx.Font;

import waba.ui.Button;
import waba.ui.ComboBox;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.Label;
import waba.ui.MainWindow;
import waba.ui.Radio;
import waba.ui.RadioGroup;

/**
 * @author Ice
 *
 * The DRFrame is the main class of the DiceRoller. It creates the main window
 * and uses the Randomizer class to roll the dice.
 */
public class DRFrame extends MainWindow {
	private static final int
		VAMPIRE = 1,
		DND = 2; 
	
	private Button exitButton, rollButton;
	private ComboBox challengeBox, typeBox, numberBox;
	private GridContainer dicePanel;
	private Label challengeLabel, typeLabel, descLabel, resultLabel;
	private Radio vampireRadio, dndRadio;
	private RadioGroup gameGroup;
	private int game = DND;
	
	public void onStart() {
		/* initializes the radio buttons for switching the games */
		gameGroup = new RadioGroup();
		vampireRadio = new Radio("Vampire: The Masquerade", gameGroup);
		vampireRadio.setRect(30, 5, 165, 20);
		add(vampireRadio);
		dndRadio = new Radio("Dungeons&Dragons v3.5", gameGroup);
		dndRadio.setRect(30, 25, 165, 20);
		add(dndRadio);
		gameGroup.clicked(dndRadio);
		
		/* challenge box (Vampire) */
		challengeLabel = new Label("Select Challenge:");
		challengeLabel.setRect(40, 60, 100, 20);
		
		String[] challenges = new String[] {"2", "3", "4", "5", "6", "7", "8", "9", "10"};
		challengeBox = new ComboBox(challenges);
		challengeBox.setRect(150, 60, 30, 20);
		challengeBox.select(4);
		
		/* type box (DnD) */
		typeLabel = new Label("Select Type:");
		typeLabel.setRect(40, 60, 100, 20);
		
		String[] types = new String[] {"d4", "d6", "d8", "d10", "d12", "d20", "d100"};
		typeBox = new ComboBox(types);
		typeBox.setRect(150, 60, 40, 20);
		typeBox.select(5);
		
		/* number box */
		Label numberLabel = new Label("Select Number:");
		numberLabel.setRect(40, 95, 100, 20);
		add(numberLabel);
		
		String[] number = new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
		numberBox = new ComboBox(number);
		numberBox.setRect(150, 95, 30, 20);
		numberBox.select(0);
		add(numberBox);
		
		/* panel showing the dice faces */
		Container diceBorder = new Container();
		diceBorder.setRect(78, 138, 79, 34);
		diceBorder.setBorderStyle(BORDER_LOWERED);
		add(diceBorder);
		
		dicePanel = new GridContainer(5, 2);
		dicePanel.setGaps(0, 0);
		for (int i=0; i<10; i++)
			dicePanel.add(createDieLabel("#"), i%5, i/5);
		dicePanel.setRect(80, 140, 75, 30);
		add(dicePanel);
		
		/* description label */
		descLabel = new Label("", Label.CENTER);
		descLabel.setRect(80, 187, 75, 16);
		add(descLabel);
		
		/* the default screen is DnD */
		createDndScreen();
		
		/* label with the result */
		Container resultPanel = new Container();
		resultPanel.setRect(83, 205, 69, 20);
		resultPanel.setBorderStyle(BORDER_LOWERED);
		add(resultPanel);
		
		resultLabel = new Label("0", Label.CENTER);
		resultLabel.setRect(85, 207, 65, 16);
		add(resultLabel);
		
		/* button for dice rolling */
		rollButton = new Button("Roll Dice");
		rollButton.setRect(40, 255, 75, 20);
		add(rollButton);
		
		/* button for exit */
		exitButton = new Button("Exit");
		exitButton.setRect(140, 255, 30, 20);
		add(exitButton);
	}
	
	private void createVampireScreen() {
		vampireRadio.setFont(new Font("", Font.BOLD, 10));
		dndRadio.setFont(new Font("", Font.PLAIN, 10));
		
		add(challengeLabel);
		add(challengeBox);
		
		descLabel.setText("Wins:");
		
		remove(typeLabel);
		remove(typeBox);
	}
	
	private void createDndScreen() {
		vampireRadio.setFont(new Font("", Font.PLAIN, 10));
		dndRadio.setFont(new Font("", Font.BOLD, 10));
		
		add(typeLabel);
		add(typeBox);
		
		descLabel.setText("Sum:");
		
		remove(challengeLabel);
		remove(challengeBox);
	}
	
	public void onEvent(Event event) {
		/* pen touched the screen */
		if (event.type == ControlEvent.PRESSED) {
			/* pen touched the exit button */
			if (event.target == exitButton)
				exit(1);
			/* pen selected the Vampire radio */
			else if (event.target == vampireRadio) {
				createVampireScreen();
				repaint();
				game = VAMPIRE;
			/* pen selected the DnD radio */
			} else if (event.target == dndRadio) {
				createDndScreen();
				repaint();
				game = DND;
			/* pen pressed the roll button */
			} else if (event.target == rollButton) {
				int challenge = 6, diceType = 10, number;
				
				if (game == VAMPIRE) {
					String challengeString = (String)challengeBox.getSelectedItem();
					// extracts the challenge from the string
					challenge = Convert.toInt(challengeString);
				} else if (game == DND) {
					String typeString = (String)typeBox.getSelectedItem();
					// extracts the dice type from the string
					String typeChar = typeString.substring(1);
					diceType = Convert.toInt(typeChar);
				}
				
				String numberString = (String)numberBox.getSelectedItem();
				// extracts the dice number from the string
				number = Convert.toInt(numberString);
				
				// initializes the number of successes and the number of ones
				int wins = 0, ones = 0;
				// initializes the sum of the dice faces
				int sum = 0;
				
				for (int i=0; i<10; i++)
					((Label)dicePanel.get(i%5, i/5)).setText("#");
				
				for (int i=0; i<number; i++) {
					int face = rollDie(diceType, i);
					// sums the victories
					if (face >= challenge || face == 0)
						wins++;
					// sums the ones
					else if (face == 1)
						ones++;
					sum += face;
					((Label)dicePanel.get(i%5, i/5)).setText("" + face);
				}
				
				if (game == VAMPIRE) {
					if (wins == 0 && ones > 0)
						resultLabel.setText("CRITICAL!!!");
					else if (wins <= ones)
						resultLabel.setText("lost");
					else
						resultLabel.setText("" + (wins - ones));
				} if (game == DND)
					resultLabel.setText("" + sum);
			}
		}
	}
	
	private Label createDieLabel(String face) {
		Label dieLabel = new Label(face, Label.CENTER);
		dieLabel.setRect(0, 0, 15, 15);
		return dieLabel;
	}
	
	private int rollDie(int dieType, int i) {
		Randomizer r = new Randomizer();
		
		int face = (int)(r.get(i)*dieType);
		if (dieType != 10)
			face++;
		return face;
	}
}