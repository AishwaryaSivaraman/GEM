package ui.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractMethodRefactoring;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import ui.handlers.Candidates;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class RefactoringHandler extends AbstractHandler {
	private String PATH;
	String fileName;
	String PROJECTNAME;
	String filePath;

	/**
	 * The constructor.
	 */
	public RefactoringHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		PATH = "/Users/Aish/Downloads/";
		fileName="junit";
		filePath="CH/ifa/draw/util/";
//		PROJECTNAME="JHotDraw5.2/sources/";
//		PROJECTNAME="junit3.8/src/";
//		PROJECTNAME = "MyWebMarket/src/";
		PROJECTNAME="wikidev-filters/src/";
//		PROJECTNAME = "workspace/SelfPlanner/src/";
		readCandidates(PATH+"wikidev"+".csv");
//		readFileForLineNumber(PATH+"jhotdrawcandidates.csv");
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"UI",
				"Refactoring check complete");
		return null;
	}
	
	public void readCandidates(String fileName){
		 BufferedReader br = null;
		 String line="";
		 int count=0;
		 ArrayList<Candidates> candidates = new ArrayList<>();
		 try {
			Candidates cand = new Candidates();
			br = new BufferedReader(new FileReader(fileName));
			while((line=br.readLine()) != null){
				if(count>0){
					String[] rowData = line.split(",");
					String projectPath = PATH+PROJECTNAME;
					String filePath = rowData[0];
					if(candidates.size()>0 && candidates.get(candidates.size()-1).getProjectPath().equals(projectPath) 
							&& candidates.get(candidates.size()-1).getFilePath().equals(filePath)){
						//update the start and end index
						int startIndex= Integer.parseInt(rowData[2]);
						int endIndex=Integer.parseInt(rowData[4]);
						candidates.get(candidates.size()-1).appendStartIndex(startIndex);
						candidates.get(candidates.size()-1).appendEndIndex(endIndex);
					} else{
						//add new project
						cand = new Candidates(projectPath,rowData);
						candidates.add(cand);
						System.out.print("new project beg");
					}
					System.out.println("Start Number "+rowData[2]+" End Number "+rowData[4]);
				}
				count++;
				System.out.println(candidates.size());	
			}
			refactor(candidates);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void refactor(List<Candidates> candidates){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
	    IWorkspaceRoot root = workspace.getRoot();
	    IProject[] projects = root.getProjects();	    
	    IJavaProject javaP = JavaCore.create(projects[0]);
	    IPackageFragment[] packages;
	    try {
			packages = javaP.getPackageFragments();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    IFolder sourceFolder = projects[0].getFolder("src");
	    
	    try {			
			for(int j=0;j<candidates.size();j++){
		    	Candidates cand = candidates.get(j);
		    	IPath path = Path.fromOSString(PATH+PROJECTNAME+cand.getFilePath()+".java");
			    IFile file = workspace.getRoot().getFileForLocation(path);
			    ICompilationUnit compilationUnit = (ICompilationUnit) JavaCore.create(file);
			    System.out.println(path.toString());
			    compilationUnit.open(null);
				for(int i=0;i<cand.getStartIndex().size();i++){
		    		Boolean isExtractable  = em(compilationUnit,cand.getStartIndex().get(i),cand.getEndIndex().get(i));		    		
		    		candidates.get(j).appendIsExtractable(isExtractable);		    	
		    	}
		    }						
			writeToFile(candidates);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void writeToFile(List<Candidates> candidates) {
		// TODO Auto-generated method stub
		//
		BufferedWriter bw = null;
		FileWriter fw = null;
		try{
			fw = new FileWriter(PATH+"extractable"+fileName+".csv");
			bw = new BufferedWriter(fw);
			bw.write("FilePath,StartIndex,EndIndex,Extractable\n");
			for(int j=0;j<candidates.size();j++){
				Candidates cand = candidates.get(j);
				for(int i=0;i<cand.getStartIndex().size();i++){
					StringBuilder rowData = new StringBuilder(cand.getFilePath()+","+Integer.toString(cand.getStartIndex().get(i))
													+","+Integer.toString(cand.getEndIndex().get(i))+","+cand.getIsExtractable().get(i).toString()+"\n");
					System.out.println(rowData);
					//write row to csv file					
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

	@SuppressWarnings({ "restriction", "unused" })
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
//				parameters = refactoring.getParameterInfos();		
//				Change refChange = refactoring.createChange(new NullProgressMonitor());
//				refChange.perform(new NullProgressMonitor());
//				System.out.println(refChange.getDescriptor());
				System.out.println("Extractable for start_ "+startIndex+" end_ "+endIndex);
				return true;				
			}
			System.out.println("The end of em");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}			
		return false;
	}
	
	
	public void readFileForLineNumber(String filePath){
		BufferedReader br = null;
		 String line="";
		 int count=0;
		 try {
			br = new BufferedReader(new FileReader(filePath));
			while((line=br.readLine()) != null){
				if(count>0){
					System.out.println(line);
					System.out.println(count);
					String[] rowData = line.split(",");
					String javaFilePath = PATH+PROJECTNAME+rowData[0].replace('.', '/')+".java";
					String startChar = rowData[1].split(":")[0].substring(1);
					String length = rowData[1].split(":")[1].split(";")[0];
					getLineNumber(rowData,javaFilePath, Integer.parseInt(startChar), Integer.parseInt(length));
				}
				count++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void getLineNumber(String[] rowData,String filePath,int position, int length){
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		String source;
		try {
			source = new String(Files.readAllBytes(Paths.get(""+filePath)));
			parser.setSource(source.toCharArray());
			final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			int cNo = cu.getColumnNumber(position);
			int lineNo = cu.getLineNumber(position);
			int endLineNo = cu.getLineNumber(position+length);
			
			System.out.println("Start Line no: "+lineNo+" End Line no: "+endLineNo);
			writeLNtoFile(rowData, lineNo, endLineNo+1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void writeLNtoFile(String[] rowData,int startLineNo, int endLineNo){
		BufferedWriter bw = null;
		FileWriter fw = null;					
		try {
			fw = new FileWriter(PATH+"LN.csv",true);
			bw = new BufferedWriter(fw);
			bw.write(rowData[0]+","+rowData[1]+","+startLineNo+","+endLineNo+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
		}
	
	}
}
