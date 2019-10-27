

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/* I pledge on my honor that I have not given or received
any unauthorized assistance on this project.
(Taemin Park, tpark123@terpmail.umd.edu) */
/**
 * 
 * This is a Java program that plays a game on a board composed of hexagonal tiles. There are two players: Green and Red.
 * Each player has four markers. You must move exactly one move on your turn. The objective is to get your four markers into
 * a straight line. Alpha-Beta pruning, node ordering, iterative deepening techniques are used.
 * 
 * @author Taemin
 * 
 */
public class FourInARow {
	static ArrayList<Integer> marks;
	static ArrayList<Integer> startPts;
	static ArrayList<Integer> lBound;
	static ArrayList<Integer> rBound;

	static ArrayList<Integer> specialCases1 = new ArrayList<Integer>(Arrays.asList(0,10,20,30,40,50));
	static ArrayList<Integer> specialCases2 = new ArrayList<Integer>(Arrays.asList(60,70,80,90,100,110));

	/**
	 * A method to find possible moves from current position. Returns ArrayList of possible Integer positions 
	 * that can be made from a given position.
	 * 
	 * @param pos
	 * @param myPos
	 * @param opPos
	 * @return
	 */
	public static ArrayList<Integer> availableMoves(int pos, ArrayList<Integer> myPos, ArrayList<Integer> opPos){
		ArrayList<Integer> avail = new ArrayList<Integer>();
		//left up
		int temp = pos - 11;
		int n = 1;
		while (marks.contains(temp) && !myPos.contains(temp) && !opPos.contains(temp) && n != 4){
			avail.add(temp);
			n += 1;
			temp -= 11;
		}
		//right up
		temp = pos - 10;
		n = 1;
		while (marks.contains(temp) && !myPos.contains(temp) && !opPos.contains(temp) && n != 4 && temp != 50){
			avail.add(temp);
			n += 1;
			temp -= 10;
		}
		//left down
		temp = pos + 10;
		n = 1;
		while (marks.contains(temp) && !myPos.contains(temp) && !opPos.contains(temp) && n != 4 && temp != 60){
			avail.add(temp);
			n += 1;
			temp += 10;
		}
		//right down
		temp = pos + 11;
		n = 1;
		while (marks.contains(temp) && !myPos.contains(temp) && !opPos.contains(temp) && n != 4){
			avail.add(temp);
			n += 1;
			temp += 11;
		}
		//left
		temp = pos - 1;
		n = 1;
		while (marks.contains(temp) && !myPos.contains(temp) && !opPos.contains(temp) && n != 4 && !rBound.contains(temp)){
			avail.add(temp);
			n += 1;
			temp -= 1;
		}
		//right
		temp = pos + 1;
		n = 1;
		while (marks.contains(temp) && !myPos.contains(temp) && !opPos.contains(temp) && n != 4 && !lBound.contains(temp)){
			avail.add(temp);
			n += 1;
			temp += 1;
		}
		return avail;
	}

	/**
	 * This method sorts a given ArrayList from smallest to largest according to the evaluation function.
	 * 
	 * @param whole
	 * @param parent
	 * @param myPos
	 * @param opPos
	 * @return
	 */
	public static ArrayList<Integer> mergeSortSmallest(ArrayList<Integer> whole, int parent, ArrayList<Integer> myPos, ArrayList<Integer> opPos) {
        ArrayList<Integer> left = new ArrayList<Integer>();
        ArrayList<Integer> right = new ArrayList<Integer>();
        int center;
 
        if (whole.size() == 1) {    
            return whole;
        } else {
            center = whole.size()/2;
            for (int i=0; i<center; i++) {
                    left.add(whole.get(i));
            }
 
            for (int i=center; i<whole.size(); i++) {
                    right.add(whole.get(i));
            }
 
            left  = mergeSortSmallest(left, parent, myPos, opPos);
            right = mergeSortSmallest(right, parent, myPos, opPos);
 
            mergeSmall(left, right, whole, parent, myPos, opPos);
        }
        return whole;
    }
 
	/**
	 * A helper function for mergeSortSmallest function.
	 * 
	 * @param left
	 * @param right
	 * @param whole
	 * @param parent
	 * @param myPos
	 * @param opPos
	 */
    private static void mergeSmall(ArrayList<Integer> left, ArrayList<Integer> right, ArrayList<Integer> whole, int parent, ArrayList<Integer> myPos, ArrayList<Integer> opPos) {
        int leftIndex = 0;
        int rightIndex = 0;
        int wholeIndex = 0;
        
        while (leftIndex < left.size() && rightIndex < right.size()) {
            if (evaluate(parent, (left.get(leftIndex)), myPos, opPos) < evaluate(parent,(right.get(rightIndex)), myPos, opPos)) {
                whole.set(wholeIndex, left.get(leftIndex));
                leftIndex++;
            } else {
                whole.set(wholeIndex, right.get(rightIndex));
                rightIndex++;
            }
            wholeIndex++;
        }
 
        ArrayList<Integer> rest;
        int restIndex;
        if (leftIndex >= left.size()) {
            rest = right;
            restIndex = rightIndex;
        } else {
            rest = left;
            restIndex = leftIndex;
        }
 
        for (int i=restIndex; i<rest.size(); i++) {
            whole.set(wholeIndex, rest.get(i));
            wholeIndex++;
        }
    }
    
    /**
     * Performs merge sorting of given ArrayList from largest to smallest.
     * 
     * @param whole
     * @param parent
     * @param myPos
     * @param opPos
     * @return
     */
    public static ArrayList<Integer> mergeSortLargest(ArrayList<Integer> whole, int parent, ArrayList<Integer> myPos, ArrayList<Integer> opPos) {
        ArrayList<Integer> left = new ArrayList<Integer>();
        ArrayList<Integer> right = new ArrayList<Integer>();
        int center;
 
        if (whole.size() == 1) {    
            return whole;
        } else {
            center = whole.size()/2;
            for (int i=0; i<center; i++) {
                    left.add(whole.get(i));
            }
 
            for (int i=center; i<whole.size(); i++) {
                    right.add(whole.get(i));
            }
 
            left  = mergeSortLargest(left, parent, myPos, opPos);
            right = mergeSortLargest(right, parent, myPos, opPos);
 
            mergeLarge(left, right, whole, parent, myPos, opPos);
        }
        return whole;
    }
 
    /** 
     * A helper function for mergeSortLargest.
     * 
     * @param left
     * @param right
     * @param whole
     * @param parent
     * @param myPos
     * @param opPos
     */
    private static void mergeLarge(ArrayList<Integer> left, ArrayList<Integer> right, ArrayList<Integer> whole, int parent, ArrayList<Integer> myPos, ArrayList<Integer> opPos) {
        int leftIndex = 0;
        int rightIndex = 0;
        int wholeIndex = 0;
        
        while (leftIndex < left.size() && rightIndex < right.size()) {
            if (evaluate(parent, (left.get(leftIndex)), myPos, opPos) > evaluate(parent,(right.get(rightIndex)), myPos, opPos)) {
                whole.set(wholeIndex, left.get(leftIndex));
                leftIndex++;
            } else {
                whole.set(wholeIndex, right.get(rightIndex));
                rightIndex++;
            }
            wholeIndex++;
        }
 
        ArrayList<Integer> rest;
        int restIndex;
        if (leftIndex >= left.size()) {
            rest = right;
            restIndex = rightIndex;
        } else {
            rest = left;
            restIndex = leftIndex;
        }
 
        for (int i=restIndex; i<rest.size(); i++) {
            whole.set(wholeIndex, rest.get(i));
            wholeIndex++;
        }
    }
   
    /**
     * A simple version of evaluation function for merge sort functions for node ordering.
     * 
     * @param old_pos
     * @param new_pos
     * @param myPos
     * @param opPos
     * @return
     */
	public static double evaluate(int old_pos, int new_pos, ArrayList<Integer> myPos, ArrayList<Integer> opPos){
		double score = 0;
		if (startPts.contains(new_pos)) return -1;
		for (Integer x: myPos){
	
			if (x != old_pos){
				if (isLine(new_pos, x, opPos)[0] == 1){
	
					score += 1.1;
				}
			}
		}
		return score;
	}

	/**
	 * This function returns an array of int which represents if given positions form a straight line and
	 * if it does, it tells if a line is diagonally connected or horizontally connected.
	 * 
	 * @param pos
	 * @param pos2
	 * @param opPos
	 * @return
	 */
	public static int[] isLine(int pos, int pos2, ArrayList<Integer> opPos){
		int[] array = {0,0};
		int diff = Math.abs(pos2- pos);
		if (diff % 10 != 0 && diff % 11 != 0){ //is not diagonally connected
			if (pos < 50 && pos2 < 50){ 
				
				if (pos/10 == pos2/10) {
					for (Integer block: opPos){
						if (block < 50){
							if (pos/10 == block/10){
								if ((block > pos && block > pos2) || (block < pos && block < pos2)){
									array = new int[] {1,1};// pos and pos2 are horizontally connected
								}
								else{
									array = new int[] {0,0}; 
									return array;
								}
							}
							else{
								array = new int[] {0,0};
								return array;

							}
						}
						else{
							array = new int[] {1,1}; // pos and pos2 are horizontally connected
						}
					}
				}
				else array = new int[] {0,0};
			}
			else{
				if (!specialCases2.contains(pos) && !specialCases2.contains(pos2)){ // if none is specialCases
					if (pos/10 == pos2/10) {
						for (Integer block: opPos){
							if (block >= 50){
								if (pos/10 == block/10){
									if ((block > pos && block > pos2) || (block < pos && block < pos2)){
										array = new int[] {1,1}; // pos and pos2 are horizontally connected
									}
									else{
										array = new int[] {0,0};  
										return array;

									}
								}
								else{
									array = new int[] {0,0};
									return array;

								}
							}
							else{
								array = new int[] {1,1}; // pos and pos2 are horizontally connected
							}
						}
					}
					else array = new int[] {0,0};
				}
				else { // if one of them are specialCases... they cannot be both in special cases, because diff % 10 != 0.
					if (Math.abs(pos/10-pos2/10) == 1){ 
						for (Integer block: opPos){
							if (pos > pos2){
								if (block/10 == pos2/10 && block > pos2){
									array = new int[] {0,0};
									return array;

								}
								else{
									array = new int[] {1,1};
								}
							}
							else{
								if (block/10 == pos/10 && block > pos){
									array = new int[] {0,0};
									return array;

								}
								else{
									array = new int[] {1,1};
								}
							}
						}
					}
					else{
						array = new int[] {0,0};
					}
				}
			}
		}
		else if (diff % 10 == 0){
			if ((specialCases1.contains(pos) && specialCases2.contains(pos2)) || (specialCases1.contains(pos2) && specialCases2.contains(pos))){
				if ((pos == 50 && pos2 == 60) || (pos == 60 && pos2 ==50)){
					for (Integer block: opPos){
						if (block > 50 && block < 60){
							array = new int[] {0,0};
							return array;
						}
						else{
							array = new int[] {1,1}; // they are horizontally connected
						}
					}
				}
				else if ((pos == 0 && pos2 == 110) || (pos == 110 && pos2 == 0)){
					for (Integer block: opPos){
						if (block % 11 == 0){
							array = new int[] {0,0};
							return array;
						}
						else{
							array = new int[] {1,2}; // they are diagonally(1) connected
						}
					}
				}
				else{
					array = new int[] {0,0};
				}
			}
			else{
				for (Integer block: opPos){
					if (Math.abs(block-pos)% 10 == 0){
						if ((block > pos && block < pos2) || (block < pos && block > pos2)){
							array = new int[] {0,0};
							return array;
						}
						else{
							array = new int[] {1,3}; // they are diagonally(2) connected
						}
					}
					else{
						array = new int[] {1,3}; // they are diagonally(2) connected
					}
				}
			}
		}
		else{
			for (Integer block: opPos){
				if (Math.abs(block-pos)% 11 == 0){
					if ((block > pos && block < pos2) || (block < pos && block > pos2)){
						array = new int[] {0,0};
						return array;
					}
					else{
						array = new int[] {1,2}; // they are diagonally(1) connected
					}
				}
				else{
					array = new int[] {1,2}; // they are diagonally(1) connected
				}
			}
			array = new int[] {1,2};// they are diagonally(1) connected
		}
		return array;
		
	}

	/**
	 * The evaluation function for minimax function. Returns a score of given positions.
	 * 
	 * @param mPos
	 * @param oPos
	 * @return
	 */
	public static double evaluateWhole(ArrayList<Integer> mPos, ArrayList<Integer> oPos){
		double score = 0;
	
		for (Integer st : startPts){
			if (mPos.contains(st)){
				score -= 10;
			}
		}
		
		////////////////////// represent all conncections////////////////////
		int [] zero_one = isLine(mPos.get(0), mPos.get(1), oPos);
		int [] zero_two = isLine(mPos.get(0), mPos.get(2), oPos); 
		int [] zero_three = isLine(mPos.get(0), mPos.get(3), oPos);
		int [] one_two = isLine(mPos.get(1), mPos.get(2), oPos);
		int [] one_three = isLine(mPos.get(1), mPos.get(3), oPos);
		int [] two_three = isLine(mPos.get(2), mPos.get(3), oPos);
		// max score when all four form one line
		if (zero_one[1] == zero_two[1] && zero_two[1] == zero_three[1] && zero_three[1] != 0){
			score += 999;
			for (Integer st : startPts){
				if (mPos.contains(st)){
					score = 10;
				}
			}
		}
		// when three form one line
		else if (zero_one[1] == zero_two[1] && zero_one[1] != 0 || 
				zero_two[1] == zero_three[1] && zero_two[1] != 0 || 
				zero_one[1] == zero_two[1] && zero_one[1] != 0){
			score += 15;
		}
		// when there are lines of two
		else{
			score += (double)(zero_one[0] + zero_two[0] + zero_three[0] + one_two[0] + one_three[0] + two_three[0])/2;
		}
		
		//////////////////////represent all conncections of opponents////////////////////
		int [] zero_one1 = isLine(oPos.get(0), oPos.get(1), mPos);
		int [] zero_two1 = isLine(oPos.get(0), oPos.get(2), mPos); 
		int [] zero_three1 = isLine(oPos.get(0), oPos.get(3), mPos);
		int [] one_two1 = isLine(oPos.get(1), oPos.get(2), mPos);
		int [] one_three1 = isLine(oPos.get(1), oPos.get(3), mPos);
		int [] two_three1 = isLine(oPos.get(2), oPos.get(3), mPos);
		// max score when all four form one line
		if (zero_one1[1] == zero_two1[1] && zero_two1[1] == zero_three1[1] && zero_three1[1] != 0){
			score -= 20;
		}
		// when three form one line
		else if (zero_one1[1] == zero_two1[1] && zero_one1[1] != 0 || 
				zero_two1[1] == zero_three1[1] && zero_two1[1] != 0 || 
				zero_one1[1] == zero_two1[1] && zero_one1[1] != 0){
			score -= 8;
		}
		// when there are lines of two
		else{
			score -= (double)(zero_one1[0] + zero_two1[0] + zero_three1[0] + one_two1[0] + one_three1[0] + two_three1[0])/2;
		}
		return score;
		
	}

	/**
	 * A major calcaulation function of this project. This function performs alpha-beta pruning using node ordering and iterative deepening.
	 * Returns an array of double, {score, from mark, to mark}.
	 * 
	 * @param level
	 * @param mPos
	 * @param oPos
	 * @param alpha
	 * @param beta
	 * @param end_time
	 * @return
	 */
	public static double[] minimax(int level, ArrayList<Integer> mPos, ArrayList<Integer> oPos, double alpha, double beta, long end_time){
		double score = 0;
		int from = -1;
		int to = -1;
		
		if (level > 2) {
			//System.out.println(evaluateWhole(mPos, oPos) + " " + from + " " + to);
			return new double[] {evaluateWhole(mPos, oPos), from, to};
		}
		else{
			if (level%2 == 0){ // if it's my turn (max)
				for (Integer mark: mPos){
	
					ArrayList<Integer> children = mergeSortLargest(availableMoves(mark, mPos, oPos), mark, mPos, oPos);
					//ArrayList<Integer> children = availableMoves(mark, mPos, oPos);

					for (Integer child: children){
						if (System.currentTimeMillis() > end_time){
							return new double[] {evaluateWhole(mPos, oPos), from, to};
						}
						ArrayList<Integer> new_mpos = new ArrayList<Integer>();
						new_mpos.addAll(mPos);
						new_mpos.remove(new_mpos.indexOf(mark));
						new_mpos.add(child);
						
						if (evaluateWhole(new_mpos,oPos) >= 900){
							return new double[] {900, mark, child};
						}
						
						score = minimax(level + 1, new_mpos, oPos, alpha, beta, end_time)[0];
						if (score > alpha){
							alpha = score;
							from = mark;
							to = child;
						}
						if (alpha >= beta) break;  // beta cut-off
					}
				}
			}
			else{
				for (Integer mark: oPos){
					ArrayList<Integer> children = mergeSortSmallest(availableMoves(mark, oPos, mPos), mark, oPos, mPos);
					//ArrayList<Integer> children = availableMoves(mark, oPos, mPos);

					for (Integer child: children){
						if (System.currentTimeMillis() > end_time){
							return new double[] {evaluateWhole(mPos, oPos), from, to};
						}
						ArrayList<Integer> new_opos = new ArrayList<Integer>();
						new_opos.addAll(oPos);
						new_opos.remove(new_opos.indexOf(mark));
						new_opos.add(child);
						score = minimax(level + 1, mPos, new_opos, alpha, beta, end_time)[0];
						if (score < beta){
							beta = score;
							from = mark;
							to = child;
						}
						
						if (alpha >= beta) break;  // alpha cut-off
					}
				}
			}
			return new double[] {(level%2 == 0) ? alpha : beta, from , to};
		}	
	}

	public static void main(String[] args){
		ArrayList<Integer>myPos = new ArrayList<Integer>();
		ArrayList<Integer>opPos = new ArrayList<Integer>();
		startPts = new ArrayList<Integer>(Arrays.asList(0, 2, 3, 5, 105, 107, 108, 110));
		lBound = new ArrayList<Integer>(Arrays.asList(0,10,20,30,40,50,61,72,83,94,105));
		rBound = new ArrayList<Integer>(Arrays.asList(5,16,27,38,49,60,70,80,90,100,110));
		marks = new ArrayList<Integer>(Arrays.asList(0,1,2,3,4,5,10,11,12,13,14,15,16,20,21,22,23,24,25,26,27,
				30,31,32,33,34,35,36,37,38,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,
				72,73,74,75,76,77,78,79,80,83,84,85,86,87,88,89,90,94,95,96,97,98,99,100,105,106,107,108,109,110));

		
		/*
		myPos.add(65);
		myPos.add(66);
		myPos.add(67);
		myPos.add(55);
		opPos.add(0);
		opPos.add(5);
		opPos.add(107);
		opPos.add(108);

		//System.out.println(numberLines(65, myPos, opPos) + " " + numberLines(55, myPos, opPos));
		long start_time = System.currentTimeMillis();
		long wait_time = 800;
		long end_time = start_time + wait_time;
		double[] output = minimax(0, myPos, opPos, -999999, 999999, end_time);
		System.out.println(output[0] + " " + output[1] + " "+ output[2]);
		
		*/
		
		
		// Read
		try (Scanner sc = new Scanner(new File("FourInARow.in")))
		{
			for (int i = 0; i < 4; i++){
				myPos.add(sc.nextInt());
			}
			for (int i = 0; i < 4; i++){
				opPos.add(sc.nextInt());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		long start_time = System.currentTimeMillis();
		long wait_time = 800;
		long end_time = start_time + wait_time;
		double[] output = minimax(0, myPos, opPos, -999999, 999999, end_time);
		
		int oldpos = (int)output[1];
		int newpos = (int)output[2];
		
		// Write
		
		try {
            FileWriter fileWriter = new FileWriter("FourInARow.out");
           // fileWriter.write(myPos.get(0) + " " + availableMoves(myPos.get(0), myPos, opPos).get(0));
            fileWriter.write(oldpos + " " + newpos);
            
            fileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
	}
}
