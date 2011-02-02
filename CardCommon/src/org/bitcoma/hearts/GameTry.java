package org.bitcoma.hearts;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class GameTry
{
	public GameTry()
	{
		
	}
	
	public static void main(String [] args)
	{
		Map<Long, Byte> gameMap = new HashMap<Long, Byte>();
		gameMap.put((long) 1, (byte) 0);
		gameMap.put((long) 2, (byte) 0);
		gameMap.put((long) 3, (byte) 0);
		gameMap.put((long) 4, (byte) 0);
		
		Round r = new Round(gameMap);
		
		r.shuffle(4);
		Map<Long, LinkedList<Card>> cardHands = r.getUserIdToHand();
		
		System.out.println(cardHands.get((long) 1));
		System.out.println(cardHands.get((long) 2));
		System.out.println(cardHands.get((long) 3));
		System.out.println(cardHands.get((long) 4));
	}
}