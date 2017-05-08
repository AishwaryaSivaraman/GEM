package ui.handlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.internal.corext.refactoring.code.InlineMethodRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.util.RefactoringASTParser;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.osgi.framework.util.FilePath;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class DataHandler extends AbstractHandler{
	
	public static Hashtable<String, Integer> methodTable;
	public static Hashtable<String, List<InvocationInformation>> invocationTable;
	public String path;
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub			
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
//		getAllProjects();
		String[] projectsToExtractFrom = {"android_after","antlr4_after","deeplearning4j_after","elasticsearch_after","guava_after","intellij-community_after","MapDB_after"
											,"mockito_after","buck_after","presto_after","facebook-android-sdk_after","RxJava_after"};
		path = "/Users/Aish/Documents/RefactoringDataset/";
//		for(int i=0;i<projectsToExtractFrom.length;i++){
//			getAllFilesInFolder(new File(path+"src/"+projectsToExtractFrom[i]));
//			methodTable = new Hashtable<>();	
//		} 				
//			
//		getMethodsInFile("/Users/Aish/Documents/RefactoringDataset/src/android_after/app/src/main/java/com/github/pockethub/android/accounts/AccountUtils.java", "AccountUtils");
		inlineMethod(path+"src/deeplearning4j_after/deeplearning4j-core/src/main/java/org/deeplearning4j/api/storage/impl/RemoteUIStatsStorageRouter.java",46,1);
		MessageDialog.openInformation(
				window.getShell(),
				"UI",
				"Generating Data complete");
		return null;
	}
	
	public void getAllProjects(){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
	    IWorkspaceRoot root = workspace.getRoot();
	    IProject[] projects = root.getProjects();
	    try {
			projects[0].accept(new IResourceVisitor(){

				@Override
				public boolean visit(IResource resource) throws CoreException {
					// TODO Auto-generated method stub
//					if(resource.getType()!=IResource.FILE) return false;
					if(resource.getName().endsWith(".java")){
						//process the java file 											
						getMethodsInFile(resource.getLocation().toOSString(),resource.getFullPath().toOSString());
						
					}
					return true;
					
				}
				
			});
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getAllFilesInFolder(final File folder){
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	getAllFilesInFolder(fileEntry);
	        } else {	            
	            if(fileEntry.getName().endsWith(".java")){
	            	getMethodsInFile(fileEntry.getAbsolutePath(), fileEntry.getName());
	            }
	        }
	    }
	}
	
	public void getMethodsInFile(String filePath,String relativePath){
		System.out.println("In file "+relativePath);
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		String source;
		methodTable = new Hashtable<>();
		invocationTable = new Hashtable<>();
		try {			
			source = new String(Files.readAllBytes(Paths.get(""+filePath)));
			parser.setSource(source.toCharArray());
			final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			AST ast  = cu.getAST();
			
			cu.accept(new ASTVisitor() {
				@Override
	            public boolean visit(MethodDeclaration node) {															
					try{						
						Block body = node.getBody();
						if(body !=null && body.statements()!=null){
							methodTable.put(node.getName().toString(), body.statements().size());
						} else{
							methodTable.put(node.getName().toString(), 0);
						}						
					} catch(Exception e){
						System.out.println("The error is "+e.getMessage());
					}
					
	                return super.visit(node);	               
	            }
				@Override
	            public void endVisit(MethodDeclaration node) {										
											                
	            }
			});
			
				
			cu.accept(new ASTVisitor() {
				String activeMethod="";
				@Override
	            public boolean visit(MethodDeclaration node) {										
					activeMethod = node.getName().toString();					
	                return super.visit(node);	               
	            }
				@Override
	            public void endVisit(MethodDeclaration node) {										
					activeMethod = "";					                	
	            }
				
				@Override				
				public boolean visit(MethodInvocation node){
					String callee = node.getName().toString();
//					int startLineNumber  = cu.getLineNumber(node.getStartPosition()) - 1;
					int startLineNumber  = node.getStartPosition();
					int nodeLength = node.getLength();
					int endLineNumber = cu.getLineNumber(node.getStartPosition() + nodeLength) - 1;
					if(methodTable.containsKey(callee) && methodTable.containsKey(activeMethod)){
						System.out.println("The call is frm the method "+activeMethod+ " invocation is "+node);
						double ratioOfStatements =  (methodTable.get(activeMethod)*1.0)/methodTable.get(callee);
						if(methodTable.get(callee)>=3 && methodTable.get(activeMethod)>=4 && ratioOfStatements>0.5){
							if(!invocationTable.containsKey(callee)){								
								InvocationInformation invo = new InvocationInformation(activeMethod, 1,startLineNumber,endLineNumber,nodeLength);
								List<InvocationInformation> list = new ArrayList<InvocationInformation>();
								list.add(invo);
								invocationTable.put(callee, list);							
							} else{
								List<InvocationInformation> list = invocationTable.get(callee);
								boolean found = false;
								for(int i=0;i<list.size();i++){
									if(list.get(i).getCaller().equals(activeMethod)){
										int currentNo = invocationTable.get(callee).get(i).getNoOfCalls()+1;
										invocationTable.get(callee).get(i).setNoOfCalls(currentNo);
										found = true;
										break;
									}
								}
								if(!found){
									InvocationInformation invo = new InvocationInformation(activeMethod, 1,startLineNumber,endLineNumber,nodeLength);
									List<InvocationInformation> invoList  = invocationTable.get(callee);
									invoList.add(invo);
									invocationTable.put(callee, invoList);
								}							
							}							
						}						
					}					
					return super.visit(node);	
				}
			});
			
			//Use this to generate positive samples
			printToFile(filePath);
			
			//Use this to generate negative sample
//			printNegativeSamplesToFile(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}

	private void printToFile(String filePath) {
		// TODO Auto-generated method stub
		//print		
		FileWriter fw = null;
		try {
			fw = new FileWriter(path+"oracle.txt",true);			
			Set<String> keys = invocationTable.keySet();
			Iterator<String> itr = keys.iterator();	
			while(itr.hasNext()){
				String str = itr.next();
				List<InvocationInformation> invoList = invocationTable.get(str);
				for(int  i=0;i<invoList.size();i++){
					if(invoList.size()<3 && invoList.get(i).getNoOfCalls()<3){
						if(!invoList.get(i).getCaller().equals(str)){
							String print = filePath.split(path)[1] + "," + invoList.get(i).getCaller()+","+str+","
									+invoList.get(i).getStartLineNumber()+","+invoList.get(i).getEndLineNumber()+","+
									invoList.get(i).getLength()+"\n";
							if(!filePath.toUpperCase().contains("TEST")){
								fw.write(print);
							}							
						}
						
					}				
				}			
				 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {				
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
								
	}
	
	@SuppressWarnings({ "restriction", "unused" })
	private void inlineMethod(String filePath,int startLineNumber,int length){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
	    IWorkspaceRoot root = workspace.getRoot();
	    IProject[] projects = root.getProjects();	    
	    IJavaProject javaP = JavaCore.create(projects[0]);
	    String pathString = "/Users/Aish/Documents/workspace/hello/src/hello/Hello.java";
		IPath iPath = Path.fromOSString(pathString);
	    IFile file = workspace.getRoot().getFileForLocation(iPath);	   
	    ICompilationUnit compilationUnit = (ICompilationUnit) JavaCore.create(file);
	    try {
	    	String source = new String(Files.readAllBytes(Paths.get(pathString)));			
	    	final ASTParser parser= ASTParser.newParser(AST.JLS8);
			parser.setResolveBindings(true);
			parser.setSource(source.toCharArray());
			CompilationUnit node= (CompilationUnit) parser.createAST(null);
			compilationUnit.open(null);
			ISourceRange expected = TextRangeUtil.getSelection(compilationUnit, 18, 0, 18, 0);
			InlineMethodRefactoring im = InlineMethodRefactoring.create(compilationUnit, new RefactoringASTParser(ASTProvider.SHARED_AST_LEVEL).parse(compilationUnit, true), expected.getOffset(), expected.getLength());
			IProgressMonitor pm= new NullProgressMonitor();
			RefactoringStatus res = im.checkInitialConditions(pm);
			res = im.checkFinalConditions(pm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void printNegativeSamplesToFile(String filePath) {
		// TODO Auto-generated method stub
		//print		
		FileWriter fw = null;
		try {
			fw = new FileWriter(path+"NegativeOracle.txt",true);			
			Set<String> keys = methodTable.keySet();
			Iterator<String> itr = keys.iterator();	
			while(itr.hasNext()){
				String str = itr.next();				
				if(methodTable.get(str)>=4){
					String print = filePath.split(path)[1] + "," +str+"\n";	
					fw.write(print);																		
				}					
			}							 			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {				
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
								
	}
}
