package Util;

import java.util.ArrayList;
import java.util.List;

public class Candidates {
	String projectPath;	
	String filePath;
	List<Integer> startIndex;
	List<Integer> endIndex;	
	List<Boolean> isExtractable;
	
	public Candidates(){
		
	}
	
	public Candidates(String projectPath, String filePath,
			List<Integer> startIndex, List<Integer> endIndex) {
		super();
		this.projectPath = projectPath;
		this.filePath = filePath;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	
	public Candidates(String projectPath,String[] data){
		this.projectPath = projectPath;
		this.filePath = data[0];
		this.startIndex = new ArrayList<Integer>();
		this.endIndex = new ArrayList<Integer>();
		this.startIndex.add(Integer.parseInt(data[2]));
		this.endIndex.add(Integer.parseInt(data[4]));
		this.isExtractable = new ArrayList<Boolean>();
	}
	
	public String getProjectPath() {
		return projectPath;
	}
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
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
