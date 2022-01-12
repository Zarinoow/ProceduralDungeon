package fr.foxelia.proceduraldungeon.utilities;

public class Pair<T1, T2> {
	
	private T1 first;
	private T2 second;
	
	public Pair(T1 key, T2 value) {
		this.first = key;
		this.second = value;
	}
	
	public T1 getFirst() {
		return this.first;
	}
	
	public T2 getSecond() {
		return this.second;
	}

}
