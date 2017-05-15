package Util;

public class ExtractMethodResults {

	String top;
	String methodName;
	String startinLineNo;
	String endingLineNo;	
	String prob;
	public ExtractMethodResults(String top, String methodName,
			String startinLineNo, String endingLineNo, String prob) {
		super();
		this.top = top;
		this.methodName = methodName;
		this.startinLineNo = startinLineNo;
		this.endingLineNo = endingLineNo;
		this.prob = prob;
	}
	public String getTop() {
		return top;
	}
	public void setTop(String top) {
		this.top = top;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getStartinLineNo() {
		return startinLineNo;
	}
	public void setStartinLineNo(String startinLineNo) {
		this.startinLineNo = startinLineNo;
	}
	public String getEndingLineNo() {
		return endingLineNo;
	}
	public void setEndingLineNo(String endingLineNo) {
		this.endingLineNo = endingLineNo;
	}
	public String getProb() {
		return prob;
	}
	public void setProb(String prob) {
		this.prob = prob;
	}
	
	
}
