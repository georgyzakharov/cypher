package org.cypher.commons;

public class Student {
	
	private int studentNumber;
	private String name;
	private int clazz;
	private String major;
	
	public int getStudentNumber() {
		return studentNumber;
	}
	public void setStudentNumber(int studentNumber) {
		this.studentNumber = studentNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getClazz() {
		return clazz;
	}
	public void setClazz(int clazz) {
		this.clazz = clazz;
	}
	public String getMajor() {
		return major;
	}
	public void setMajor(String major) {
		this.major = major;
	}
	
	@Override
	public String toString() {
		return "Student [studentNumber=" + studentNumber + ", name=" + name + ", clazz=" + clazz + ", major="
				+ major + "]";
	}
	
}