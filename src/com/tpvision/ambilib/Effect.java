package com.tpvision.ambilib;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

public abstract class Effect extends AsyncTask<Object, Object, Object>{
	protected String targetIP;
	protected int nolights;
	private static final String TAG = "Effect";
	private OnEffectDoneListener oefdl;

	public Effect(String targetIP, int nolights, OnEffectDoneListener oefdl) {
		super();
		this.targetIP = targetIP;
		this.nolights = nolights;
		this.oefdl = oefdl;
	}

	public String getTargetIP() {
		return targetIP;
	}

	public void setTargetIP(String targetIP) {
		this.targetIP = targetIP;
	}

	/*
	 * sets the ambilights to manual mode
	 */
	protected void init(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Connection.setAmbilightsMode(targetIP, "manual");
			}
		}).start();
		Connection.setAmbilightsMode(targetIP, "manual");
	}
	
	/*
	 * makes the ambilights follow the TV again
	 */
	protected void cleanup(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Connection.setAmbilightsMode(targetIP, "internal");
			}
		}).start();
	}
	
	public abstract void effect();
	
	@Override
	protected Object doInBackground(Object... params) {
		init();
		effect();
		cleanup();
		return true;
	}
	
	/*
	 * (non-Javadoc) notify the OnEffectDoneListner (only one)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	protected void onPostExecute(Object o) {
		oefdl.effectIsDone();
    }
	
	/*
	 * sprefined effect to flash between two colors
	 */
	public void flash(int color1, int color2, int count, int interval){
		ArrayList<Thread> l = new ArrayList<Thread>();
		
		int[] total = new int[nolights];
		
		for(int i = 0; i < nolights ;++i )
			total[i] = color1;
		final int[] col1 = total.clone();
		
		for(int i = 0; i < nolights ;++i )
			total[i] = color2;
		final int[] col2 = total.clone();
		
		//TODO: add interrupter 
		for(int k = 0; k < count; k++)
		{
			Thread d = new Thread(new Runnable() {
				@Override
				public void run() {
					Connection.setAmbilights(targetIP, col1);
					
				}
			});
			d.start();
			l.add(d);
			
	
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			d = new Thread(new Runnable() {
				@Override
				public void run() {
					Connection.setAmbilights(targetIP, col2);
					
				}
			});
			d.start();
			l.add(d);
			
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		}
		
		//only return when all threads are done
		for(Thread d : l){
			try {
				d.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	
	private final int[] colrs = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DKGRAY, Color.GRAY, Color.GREEN,
			Color.LTGRAY, Color.MAGENTA, Color.RED, Color.WHITE, Color.YELLOW};
	/*
	 * prefined effect to randomly assign color to each led module
	 */
	public void random(int interval, int count){
		Random r = new Random();
		int []lights = new int[nolights];
		
		for(int c = 0; c < count; c++){
			for(int i = 0; i < nolights; i++){
				lights[i] = colrs[r.nextInt(colrs.length)];
			}
			final int[] arg = lights.clone();
			new Thread(new Runnable() {		
				@Override
				public void run() {
					Connection.setAmbilights(targetIP, arg);
				}
			}).start();
			
			//hold this configuration
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
	
	/*
	 * predefined effect to make two colors run towards eachother
	 */
	public void halfRun(int interval, int count){
		int[][] colors = {{Color.YELLOW, Color.BLUE,Color.RED, Color.GREEN},{Color.RED, Color.GREEN,Color.YELLOW, Color.BLUE} };
		
		for(int k = 0; k < 2; k++){
			int[] total = new int[nolights];
			int half = total.length/2;
			for(int i = 0; i < half; ++i){
				total[i] = colors[k][0];
			}
			for(int i = half; i < nolights; ++i){
				total[i] = colors[k][2];
			}
			
			
			for(int i=0; i < nolights; ++i){
				total[i] = colors[k][1];
				total[nolights-1-i] = colors[k][3];
				
				final int[] arg = total.clone();
				new Thread(new Runnable() {
					@Override
					public void run() {
						Connection.setAmbilights(targetIP, arg);
					}
				}).start();
				
	
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}
	

	public void runColor(int background, int foreground, int interval, boolean leftToRight){
		if(leftToRight)
			leftToRight(background, foreground, interval);
		else
			rightToLeft(background, foreground, interval);
	}
	
	/*
	 * predefined effect to make color run from right to left in a certain background
	 */
	protected void rightToLeft(int background, int foreground, int interval){
		Log.d(TAG, "runcolor");
		
		int rb = Color.red(background);
		int gb = Color.green(background);
		int bb = Color.blue(background);
		
		int ir = rb - (rb - Color.red(foreground))/3;
		int ig = gb - (gb - Color.green(foreground))/3;
		int ib = bb - (bb - Color.blue(foreground))/3;
		int icol = Color.rgb(ir, ig, ib);
		
		ir = rb - 2*(rb - Color.red(foreground))/3;
		ig = gb - 2*(gb - Color.green(foreground))/3;
		ib = bb - 2*(bb - Color.blue(foreground))/3;
		int icol2 = Color.rgb(ir, ig, ib);
		
		
		int[] total = new int[nolights];
		for(int i = 0; i < total.length; ++i){
			total[i] = background;
		}
		
		for(int i = total.length - 1; i > -1; --i){
			total[i] = foreground;

			if(i == total.length - 1){
				//nothing
			}else if(i == total.length - 2){
				total[i+1] = icol2;
			}else if(i == total.length - 3){
				total[i+1] = icol2;
				total[i+2] = icol;
			}else{
				total[i+1] = icol2;
				total[i+2] = icol;
				total[i+3] = background;
			}
			
			
			final int[] arg = total.clone();
			new Thread(new Runnable() {
				@Override
				public void run() {
					Connection.setAmbilights(targetIP, arg);
					
				}
			}).start();
			
			//hold this configuration 
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/*
	 * predefined effect to make color run from left to right in a certain background
	 */
	protected void leftToRight(int background, int foreground, int interval){
		
		int rb = Color.red(background);
		int gb = Color.green(background);
		int bb = Color.blue(background);
		
		int ir = rb - (rb - Color.red(foreground))/3;
		int ig = gb - (gb - Color.green(foreground))/3;
		int ib = bb - (bb - Color.blue(foreground))/3;
		int icol = Color.rgb(ir, ig, ib);
		
		ir = rb - 2*(rb - Color.red(foreground))/3;
		ig = gb - 2*(gb - Color.green(foreground))/3;
		ib = bb - 2*(bb - Color.blue(foreground))/3;
		int icol2 = Color.rgb(ir, ig, ib);

		int[] total = new int[nolights];
		for(int i = 0; i < total.length; ++i){
			total[i] = background;
		}
		
		for(int i = 0; i < total.length; ++i){
			total[i] = foreground;

			if(i == 0){
				//nothing
			}else if(i == 1){
				total[0] = icol2;
			}else if(i == 2){
				total[1] = icol2;
				total[0] = icol;
			}else{
				total[i-1] = icol2;
				total[i-2] = icol;
				total[i-3] = background;
			}
			
			final int[] arg = total.clone();
			new Thread(new Runnable() {
				@Override
				public void run() {
					Connection.setAmbilights(targetIP, arg);
				}
			}).start();
			

			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	

}
