package Util;

public class InvocationInformation {
	String caller;
	int noOfCalls;
	String returnType;	
	int startLineNumber;
	int endLineNumber;
	int length;

	public InvocationInformation(String caller, int noOfCalls,String returnType) {
		super();
		this.caller = caller;
		this.noOfCalls = noOfCalls;
		this.returnType = returnType;
	}
	
	public InvocationInformation(String caller, int noOfCalls,
			 int startLineNumber, int endLineNumber,
			int length) {
		super();
		this.caller = caller;
		this.noOfCalls = noOfCalls;
		this.returnType = returnType;
		this.startLineNumber = startLineNumber;
		this.endLineNumber = endLineNumber;
		this.length = length;
	}

	public InvocationInformation(String caller, int noOfCalls) {
		super();
		this.caller = caller;
		this.noOfCalls = noOfCalls;	
	}
	
	public String getCaller() {
		return caller;
	}
	public void setCaller(String caller) {
		this.caller = caller;
	}
	public int getNoOfCalls() {
		return noOfCalls;
	}
	public void setNoOfCalls(int noOfCalls) {
		this.noOfCalls = noOfCalls;
	}
	
	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public int getStartLineNumber() {
		return startLineNumber;
	}

	public void setStartLineNumber(int startLineNumber) {
		this.startLineNumber = startLineNumber;
	}

	public int getEndLineNumber() {
		return endLineNumber;
	}

	public void setEndLineNumber(int endLineNumber) {
		this.endLineNumber = endLineNumber;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
}
