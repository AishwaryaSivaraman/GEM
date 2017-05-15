package Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractMethodRefactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

public class CandidateValidation {

	String PATH_TO_CANDIDATES;
	String PATH_TO_REF_FILE;
	String CANDIDATE_FILE_NAME;
	String PATH_TO_FOLDER;
	List<Candidate> candidates;
	
	public CandidateValidation(String pATH_TO_CANDIDATES, String pATH_TO_REF_FILE,String cANDIDATE_FILE_NAME,String pATH_TO_FOLDER) {
		super();
		PATH_TO_CANDIDATES = pATH_TO_CANDIDATES;		
		PATH_TO_REF_FILE = pATH_TO_REF_FILE;
		CANDIDATE_FILE_NAME = cANDIDATE_FILE_NAME;
		PATH_TO_FOLDER = pATH_TO_FOLDER;
		candidates = new ArrayList<>();
	}
	
	public void processCandidates(){
		readCandidates();
		checkForValidRefactoring();
	}
	
	private void checkForValidRefactoring() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();	    	   
	    try {			
			for(int j=0;j<candidates.size();j++){
		    	Candidate cand = candidates.get(j);
		    	IPath path = Path.fromOSString(cand.getFilePath());
			    IFile file = workspace.getRoot().getFileForLocation(path);
			    ICompilationUnit compilationUnit = (ICompilationUnit) JavaCore.create(file);
			    System.out.println(path.toString());
			    compilationUnit.open(null);
				for(int i=0;i<cand.getStartIndex().size();i++){
		    		Boolean isExtractable  = em(compilationUnit,cand.getStartIndex().get(i),cand.getEndIndex().get(i));		    		
		    		candidates.get(j).appendIsExtractable(isExtractable);		    	
		    	}
		    }						
			writeToFile();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void writeToFile() {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try{
			fw = new FileWriter(PATH_TO_FOLDER + "test_candidates.csv");
			bw = new BufferedWriter(fw);
			bw.write("FilePath,StartIndex,EndIndex,Extractable\n");
			for(int j=0;j<candidates.size();j++){
				Candidate cand = candidates.get(j);
				for(int i=0;i<cand.getStartIndex().size();i++){
					StringBuilder rowData = new StringBuilder(cand.getFilePath()+","+Integer.toString(cand.getStartIndex().get(i))
													+","+Integer.toString(cand.getEndIndex().get(i))+","+cand.getIsExtractable().get(i).toString()+"\n");									
					bw.write(rowData.toString());
				}			
			}			
		} catch(IOException e){
			
		} finally{
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}		
		
	}

	public Boolean em(ICompilationUnit unit,int startIndex, int endIndex){
		ISourceRange expected;
		List parameters;
		try {
			expected= TextRangeUtil.getSelection(unit, startIndex, 0, endIndex, 0);
			ExtractMethodRefactoring refactoring= new ExtractMethodRefactoring(unit,expected.getOffset(),expected.getLength());
		    refactoring.setMethodName("extracted");
			refactoring.setVisibility(0);
			RefactoringStatus status;
			status= refactoring.checkAllConditions(new NullProgressMonitor());
			if(status.isOK()){
				System.out.println("Extractable for start_ "+startIndex+" end_ "+endIndex);
				return true;				
			}			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}			
		return false;
	}
	
	public void readCandidates(){
		 BufferedReader br = null;
		 String line="";
		 int count=0;
		 
		 try {
			Candidate cand = new Candidate();
			br = new BufferedReader(new FileReader(PATH_TO_CANDIDATES+CANDIDATE_FILE_NAME));
			while((line=br.readLine()) != null){
				if(count>0){
					String[] rowData = line.split(",");
					String filePath = PATH_TO_REF_FILE;
					String methodName = rowData[0];
					int startIndex= Integer.parseInt(rowData[1]);
					int endIndex=Integer.parseInt(rowData[3]);
					if(candidates.size()>0 && candidates.get(candidates.size()-1).getFilePath().equals(filePath) 
							&& candidates.get(candidates.size()-1).getMethodName().equals(methodName)){
						//update the start and end index						
						candidates.get(candidates.size()-1).appendStartIndex(startIndex);
						candidates.get(candidates.size()-1).appendEndIndex(endIndex);
					} else{
						//add new project
						cand = new Candidate(PATH_TO_REF_FILE,methodName,startIndex,endIndex);
						candidates.add(cand);						
					}
					System.out.println("Start Number "+rowData[1]+" End Number "+rowData[3]);
				}
				count++;
				System.out.println(candidates.size());	
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}		
}

