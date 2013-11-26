package com.tpvision.ambilib;

import android.graphics.Color;

public class Parser {
	
	/**
	 * Parsers array of color values into json String
	 * length 4  (2 left; 2 right) and length 12 (3 left; 3 right; 6 top) supported
	 * @param total
	 * @return
	 */
	public static String parse(int[] total){
		switch(total.length){
		case 4:
			return parse4(total);
		case 12:
			return parse12(total);
		case 17:
			return parse17(total);
		default:
			return null;			
		}
	}
	
	private static String parse4(int[] total){
		
		int[] left = new int[2];
		int[] right = new int[2];
		int[] top = new int[0];
		
		for(int i = 0; i < total.length; i++){
			if(i<2)
				left[i] = total[i];
			else
				right[i-2] = total[i];
		}
		return parse(left,right,top);
	}
	
	private static String parse12(int[] total){
		
		int[] left = new int[3];
		int[] right = new int[3];
		int[] top = new int[6];
		
		for(int i = 0; i < total.length; i++){
			if(i<3)
				left[i] = total[i];
			else if(i<9)
				top[i-3] = total[i];
			else
				right[i-9] = total[i];
		}
		return parse(left,right,top);
	}
	
private static String parse17(int[] total){
		
		int[] left = new int[4];
		int[] right = new int[4];
		int[] top = new int[9];
		
		for(int i = 0; i < total.length; i++){
			if(i<4) //0, 1, 2, 3
				left[i] = total[i];
			else if(i<13)
				top[i-4] = total[i];
			else
				right[i-13] = total[i];
		}
		return parse(left,right,top);
	}
	
	
	/**
	 * Parses arrays of Color values into jason String
	 * 
	 * @param left array of Color values for left ambilight
	 * @param right array of Color values for right ambilight
	 * @param top array of Color values for top ambilight
	 * @return
	 */
	public static String parse(int[] left, int[] right, int[] top){
		String parsed = "{";
			parsed += "\"layer1\": {";
				parsed += "\"left\": {";
					for(int i = 0; i < left.length; i++){
						parsed += "\"" + i + "\": {";
							parsed += "\"r\": " + Color.red(left[i]) + ",";
							parsed += "\"g\": " + Color.green(left[i]) + ",";
							parsed += "\"b\": " + Color.blue(left[i]);
						parsed += "}";
						if(i < (left.length - 1)) // if not last
							parsed += ",";
					}
				parsed += "}";
				parsed += ",";
				parsed += "\"right\": {";
					for(int i = 0; i < right.length; i++){
						parsed += "\"" + i + "\": {";
							parsed += "\"r\": " + Color.red(right[i]) + ",";
							parsed += "\"g\": " + Color.green(right[i]) + ",";
							parsed += "\"b\": " + Color.blue(right[i]);
						parsed += "}";
						if(i < (right.length - 1)) // if not last
							parsed += ",";
					}
				parsed += "}";
				parsed += ",";
				parsed += "\"top\": {";
					for(int i = 0; i < top.length; i++){
						parsed += "\"" + i + "\": {";
							parsed += "\"r\": " + Color.red(top[i]) + ",";
							parsed += "\"g\": " + Color.green(top[i]) + ",";
							parsed += "\"b\": " + Color.blue(top[i]);
						parsed += "}";
						if(i < (top.length - 1)) // if not last
							parsed += ",";
					}
				parsed += "}";
			parsed += "}";
		parsed += "}";
		return parsed;
	}

	
	
}
