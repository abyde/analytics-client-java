package com.acunu.performance.generators;

public class GeoHash {

	private final String coding = "0123456789bcdefghjkmnpqrstuvwxyz";
	private final int[] powers = new int[] { 16, 8, 4, 2, 1 };

	public String encode(double lat, double lon, int precision) {

		int bitslen = precision / 2 * 5;
		boolean[] lonbits = bits(lon, -180.0, 180.0, bitslen);
		boolean[] latbits = bits(lat, -90.0, 90.0, bitslen);
		boolean[] bits = interleave(lonbits, latbits);
		return base32encode(bits);
	}

	private String base32encode(boolean[] bits) {

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < bits.length / powers.length; i++) {
			int sum = 0;
			for (int j = 0; j < powers.length; j++) {
				int index = i * powers.length + j;
				if (bits[index]) {
					sum += powers[j];
				}
			}
			builder.append(coding.charAt(sum));
		}

		return builder.toString();
	}

	private boolean[] interleave(boolean[] a, boolean[] b) {
		boolean[] result = new boolean[a.length + b.length];
		boolean first = true;
		for (int i = 0; i < result.length; i++) {
			result[i] = first ? a[i / 2] : b[i / 2];
			first = !first;
		}
		return result;
	}

	private boolean[] bits(double value, double low, double high, int precision) {
		boolean[] result = new boolean[precision];
		bits(value, low, high, 0, result);
		return result;
	}

	private void bits(double value, double low, double high, int index, boolean[] result) {
		if (index < result.length) {
			double middle = low + ((high - low) / 2);
			if (value > middle) {
				result[index] = true;
				bits(value, middle, high, index + 1, result);
			} else {
				result[index] = false;
				bits(value, low, middle, index + 1, result);
			}
		}
	}

	public double decodeLatitude(String geohash) {

		boolean[] bits = base32decode(geohash);
		boolean[] latbits = disinterleave(bits, 2, 1);
		return eval(latbits, -90.0, 90.0);
	}

	public double decodeLongitude(String geohash) {

		boolean[] bits = base32decode(geohash);
		boolean[] lonbits = disinterleave(bits, 2, 0);
		return eval(lonbits, -180.0, 180.0);
	}

	private boolean[] base32decode(String geohash) {

		boolean[] result = new boolean[geohash.length() * powers.length];
		for (int i = 0; i < geohash.length(); i++) {
			int val = coding.indexOf(geohash.charAt(i));
			for (int j = 0; j < powers.length; j++) {
				int index = i * powers.length + j;
				result[index] = val / powers[j] == 1 ? true : false;
				val = val % powers[j];
			}
		}
		return result;
	}

	private boolean[] disinterleave(boolean[] bits, int n, int off) {
		boolean[] result = new boolean[bits.length / n];
		for (int i = 0; i < result.length; i++) {
			result[i] = bits[(i * n) + off];
		}
		return result;
	}

	private double eval(boolean[] bits, double low, double high) {
		return eval(bits, 0, high - low, low);
	}

	private double eval(boolean[] bits, int index, double range, double result) {

		if (index < bits.length) {
			double half = range / 2;
			if (bits[index]) {
				return eval(bits, index + 1, half, result + half);
			} else {
				return eval(bits, index + 1, half, result);
			}
		} else {
			return result;
		}
	}

}
