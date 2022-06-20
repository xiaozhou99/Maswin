package main.lu;

import java.io.Serializable;
import java.util.Objects;

public class Chess implements Comparable<Chess>, Serializable {
	private static final long serialVersionUID = -5607784187863552783L;


	public static final int BLACK = 1;
	public static final int WHITE = 2;
	public static final int BORDER = -1;
	public static final int EMPTY = 0;
	protected int x;
	protected int y;
	protected int color;//落子
	protected int score;

	public Chess(int x, int y, int color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}

	public Chess(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getScore() {
		return score;
	}


	//清空
	public void reset() {
		color = EMPTY;
	}


	// 打印chess对象
	@Override
	public String toString() {
		return "Chess{" +
				"x=" + x +
				", y=" + y +
				", color=" + color +
				'}';
	}

	public boolean isEmpty() {
		return color == EMPTY;
	}


	@Override
	public int compareTo(Chess o) {
		if (o == null)
			return 0;
		int val1 = score;
		int val2 = o.getScore();
		if (val1 == val2)
			return 0;
		else if (val1 < val2)
			return 1;
		else
			return -1;
	}

	//判断判断两个对象是否相等，对象是否存在，对象的x和y和color是否相等
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Chess chess = (Chess) o;
		return x == chess.x && Objects.equals(y, chess.y) && Objects.equals(color, chess.color);
	}

	//返回对象的x y color的hash值
	@Override
	public int hashCode() {
		return Objects.hash(x, y, color);
	}
}
