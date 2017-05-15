package Util;

import java.util.ArrayList;
import java.util.List;

public class Candidate {	
	String filePath;
	String methodName;	
	List<Integer> startIndex;
	List<Integer> endIndex;	
	List<Boolean> isExtractable;
	
	
	public Candidate(){
		
	}
	
	public Candidate(String filePath,
			List<Integer> startIndex, List<Integer> endIndex) {
		super();		
		this.filePath = filePath;
		this.startIndex = startIndex;
		this.endIndex = endIndex;		
	}
	
	public Candidate(String filepath, String methodName, int startIndex, int endIndex){
		this.filePath = filepath;
		this.startIndex = new ArrayList<Integer>();
		this.endIndex = new ArrayList<Integer>();
//		this.startIndex.add(Integer.parseInt(data[2]));
		this.startIndex.add(startIndex);
		this.endIndex.add(endIndex);	
//		this.endIndex.add(Integer.parseInt(data[4]));
		this.methodName = methodName;
		this.isExtractable = new ArrayList<Boolean>();
	}
		
	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public List<Integer> getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(List<Integer> startIndex) {
		this.startIndex = startIndex;
	}

	public void appendStartIndex(int index){
		this.startIndex.add(index);
	}
	
	public void appendEndIndex(int index){
		this.endIndex.add(index);
	}
	
	public List<Integer> getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(List<Integer> endIndex) {
		this.endIndex = endIndex;
	}
	
	public void appendIsExtractable(Boolean isExtractable){
		this.isExtractable.add(isExtractable);
	}

	public List<Boolean> getIsExtractable() {
		return isExtractable;
	}

	public void setIsExtractable(List<Boolean> isExtractable) {
		this.isExtractable = isExtractable;
	}
	
	
}
