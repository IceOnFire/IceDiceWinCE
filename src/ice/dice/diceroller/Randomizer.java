/*
 * Created on 15-ott-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ice.dice.diceroller;

import waba.sys.Convert;
import waba.util.Random;

/**
 * @author Ice
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Randomizer {
	String randomString;
	
	public Randomizer() {
		Random r = new Random();
		double random = r.nextDouble();
		randomString = String.valueOf(random);
	}
	
	double get(int i) {
		return Convert.toDouble("0." + randomString.substring(i+2));
	}
}