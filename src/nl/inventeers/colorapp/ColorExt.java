package nl.inventeers.colorapp;

import android.graphics.Color;

public class ColorExt extends Color {
	
	public static String getColorName(int color) {
		
		String colorString = null;
		
		float[] hsv = {0,0,0};
		int r = red(color);
		int g = green(color);
		int b = blue(color);
		
		RGBToHSV(r, g, b, hsv);
		
		float h = hsv[0];
		float s = hsv[1];
		float v = hsv[2];
		
		//TODO Better check for grayscales
		if (s < 0.15) {
			if (v <= 0.2 ) {
				colorString = "Te donker";
			} else if (v > 0.2 && v <= 0.85 ) {
				colorString = "Grijs";
			} else if (v > 0.85) {
				colorString = "Te licht";
			}
		} else if (s > 0.15 && s < 0.7) {
			if (v <= 0.2) {
				colorString = "Te donker";
			} else if (v > 0.2 && v <= 0.85) {
				// Dark colors
				if (h >= 0 && h <= 13) {
					colorString = "Bruin";
				} else if (h > 13 && h <= 38) {
					colorString = "Oranje";
				} else if (h > 38 && h <= 60) {
					colorString = "Goud";
				} else if (h > 60 && h <= 136) {
					colorString = "Donker Groen";
				} else if (h > 108 && h <= 140) {
					colorString = "Groen";
				} else if (h > 140 && h <= 167) {
					colorString = "Mint groen";
				} else if (h > 167 && h <= 203) {
					colorString = "Turqoise";
				} else if (h > 203 && h <= 222) {
					colorString = "Donker Blauw";
				} else if (h > 222 && h <= 315) {
					colorString = "Donker Blauw";
				} else if (h > 315 && h <= 360) {
					colorString = "Rood"; /* Bordaux */
				}
			} else if (v > 0.85) {
				// Bright colors
				if (h >= 0 && h <= 13) {
					colorString = "Zalm";
				} else if (h > 13 && h <= 38) {
					colorString = "Oranje";
				} else if (h > 38 && h <= 60) {
					colorString = "Geel";
				} else if (h > 60 && h <= 108) {
					colorString = "Licht Groen";
				} else if (h > 108 && h <= 140) {
					colorString = "Mintgroen";
				} else if (h > 140 && h <= 167) {
					colorString = "Turqoise";
				} else if (h > 167 && h <= 220) {
					colorString = "Licht Blauw";
				} else if (h > 220 && h <= 276) {
					colorString = "Licht Paars";
				} else if (h > 276 && h <= 336) {
					colorString = "Roze";
				} else if (h > 336 && h <= 360) {
					colorString = "Zalm";
				}
			}
		} else {
			if (v <= 0.2) {
				colorString = "Te donker";
			} else if (v > 0.2 && v <= 0.85) {
				// Dark colors
				if (h >= 0 && h <= 13) {
					colorString = "Donker Rood";
				} else if (h > 13 && h <= 38) {
					colorString = "Bruin";
				} else if (h > 38 && h <= 60) {
					colorString = "Goud";
				} else if (h > 60 && h <= 136) {
					colorString = "Donker Groen";
				} else if (h > 136 && h <= 167) {
					colorString = "Turqoise";
				} else if (h > 167 && h <= 249) {
					colorString = "Donker Blauw";
				} else if (h > 249 && h <= 294) {
					colorString = "Donker Paars";
				} else if (h > 294 && h <= 315) {
					colorString = "Donker Roze";
				} else if (h > 315 && h <= 336) {
					colorString = "Donker Paars";
				} else if (h > 336 && h <= 360) {
					colorString = "Donker Rood";
				}
			} else if (v > 0.85) {
				// Bright colors
				if (h >= 0 && h <= 13) {
					colorString = "Rood";
				} else if (h > 13 && h <= 38) {
					colorString = "Oranje";
				} else if (h > 38 && h <= 60) {
					colorString = "Geel";
				} else if (h > 60 && h <= 136) {
					colorString = "Groen";
				} else if (h > 136 && h <= 167) {
					colorString = "Turqoise";
				} else if (h > 167 && h <= 249) {
					colorString = "Blauw";
				} else if (h > 249 && h <= 294) {
					colorString = "Paars";
				} else if (h > 294 && h <= 315) {
					colorString = "Roze";
				} else if (h > 315 && h <= 336) {
					colorString = "Paars";
				} else if (h > 336 && h <= 360) {
					colorString = "Rood";
				}
			}
		}
		
		if (colorString != null) return colorString;
		
		return null;
	}
}