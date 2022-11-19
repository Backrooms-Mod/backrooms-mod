package com.kpabr.backrooms.util;

public class Color {
	private int value;

	private Color(int rgb) {
		value = 0xff000000 | rgb;
	}

	private Color(int r, int g, int b) {
		this(r, g, b, 255);
	}

	private Color(int r, int g, int b, int a) {
		value = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
	}

	public static Color colorOf(int rgb) {
		return new Color(rgb);
	}

	public static Color colorOf(int r, int g, int b) {
		return new Color(r, g, b);
	}

	public static Color colorOf(int r, int g, int b, int a) {
		return new Color(r, g, b, a);
	}

	public static int of(int rgb) {
		return new Color(rgb).getRGB();
	}

	public static int of(int r, int g, int b) {
		return new Color(r, g, b).getRGB();
	}

	public static int of(int r, int g, int b, int a) {
		return new Color(r, g, b, a).getRGB();
	}

	public static Color HSBtoRGB(double hue, double saturation, double brightness) {
		int r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = (int) (brightness * 255.0f + 0.5f);
		} else {
			double h = (hue - (double) Math.floor(hue)) * 6.0f;
			double f = h - (double) Math.floor(h);
			double p = brightness * (1.0f - saturation);
			double q = brightness * (1.0f - saturation * f);
			double t = brightness * (1.0f - (saturation * (1.0f - f)));
			switch ((int) h) {
			case 0:
				r = (int) (brightness * 255.0f + 0.5f);
				g = (int) (t * 255.0f + 0.5f);
				b = (int) (p * 255.0f + 0.5f);
				break;
			case 1:
				r = (int) (q * 255.0f + 0.5f);
				g = (int) (brightness * 255.0f + 0.5f);
				b = (int) (p * 255.0f + 0.5f);
				break;
			case 2:
				r = (int) (p * 255.0f + 0.5f);
				g = (int) (brightness * 255.0f + 0.5f);
				b = (int) (t * 255.0f + 0.5f);
				break;
			case 3:
				r = (int) (p * 255.0f + 0.5f);
				g = (int) (q * 255.0f + 0.5f);
				b = (int) (brightness * 255.0f + 0.5f);
				break;
			case 4:
				r = (int) (t * 255.0f + 0.5f);
				g = (int) (p * 255.0f + 0.5f);
				b = (int) (brightness * 255.0f + 0.5f);
				break;
			case 5:
				r = (int) (brightness * 255.0f + 0.5f);
				g = (int) (p * 255.0f + 0.5f);
				b = (int) (q * 255.0f + 0.5f);
				break;
			}
		}
		return colorOf(0xff000000 | (r << 16) | (g << 8) | (b << 0));
	}

	public double[] toHSB() {
		int r = this.getRed();
		int g = this.getGreen();
		int b = this.getBlue();
		double hue, saturation, brightness;
		double[] hsbvals = new double[3];
		int cmax = (r > g) ? r : g;
		if (b > cmax)
			cmax = b;
		int cmin = (r < g) ? r : g;
		if (b < cmin)
			cmin = b;

		brightness = ((double) cmax) / 255.0f;
		if (cmax != 0)
			saturation = ((double) (cmax - cmin)) / ((double) cmax);
		else
			saturation = 0;
		if (saturation == 0)
			hue = 0;
		else {
			double redc = ((double) (cmax - r)) / ((double) (cmax - cmin));
			double greenc = ((double) (cmax - g)) / ((double) (cmax - cmin));
			double bluec = ((double) (cmax - b)) / ((double) (cmax - cmin));
			if (r == cmax)
				hue = bluec - greenc;
			else if (g == cmax)
				hue = 2.0f + redc - bluec;
			else
				hue = 4.0f + greenc - redc;
			hue = hue / 6.0f;
			if (hue < 0)
				hue = hue + 1.0f;
		}
		hsbvals[0] = hue;
		hsbvals[1] = saturation;
		hsbvals[2] = brightness;
		return hsbvals;
	}

	public Color sepia() {
		int p = this.getRGB();

		int a = (p >> 24) & 0xff;
		int r = (p >> 16) & 0xff;
		int g = (p >> 8) & 0xff;
		int b = p & 0xff;

		// calculate tr, tg, tb
		int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
		int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
		int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);

		// check condition
		r = Math.min(tr, 255);
		g = Math.min(tg, 255);
		b = Math.min(tb, 255);

		// set new RGB value
		p = (a << 24) | (r << 16) | (g << 8) | b;

		return colorOf(p);
	}

	public Color boost(int r, int g, int b) {
		return new Color(Math.max(0, Math.min(255, this.getRed() + r)), Math.max(0, Math.min(255, this.getGreen() + g)), Math.max(0, Math.min(255, this.getBlue() + b)));
	}

	public int getRed() {
		return (getRGB() >> 16) & 0xFF;
	}

	public int getGreen() {
		return (getRGB() >> 8) & 0xFF;
	}

	public int getRGB() {
		return value;
	}

	public int getBlue() {
		return (getRGB()) & 0xFF;
	}

	public int getAlpha() {
		return (getRGB() >> 24) & 0xff;
	}
}
