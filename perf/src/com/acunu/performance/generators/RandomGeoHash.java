package com.acunu.performance.generators;

public class RandomGeoHash extends AbstractRandom {

	private final GeoHash gh = new GeoHash();

	private double top;
	private double left;
	private double bottom;
	private double right;
	private int length;

	@Override
	public Object generate() {

		double lng = left + nextDouble() * (left - right);
		double lat = bottom + nextDouble() * (bottom - top);
		return gh.encode(lat, lng, length);
	}

	public double getTop() {
		return top;
	}

	public void setTop(double top) {
		this.top = top;
	}

	public double getLeft() {
		return left;
	}

	public void setLeft(double left) {
		this.left = left;
	}

	public double getBottom() {
		return bottom;
	}

	public void setBottom(double bottom) {
		this.bottom = bottom;
	}

	public double getRight() {
		return right;
	}

	public void setRight(double right) {
		this.right = right;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

}
