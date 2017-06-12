package ui.handlers;

import gumtree.spoon.AstComparator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.prefs.BackingStoreException;

import com.sun.mail.imap.protocol.Status;

import ui.Activator;
import ui.RefactoringResults;
import Util.CandidateValidation;
import Util.ExtractMethodResults;
import Util.ModelProvider;
import Util.RankCandidates;

public class ExtractMethodHandler extends AbstractHandler{
	public static IFile REF_FILE;
	List<String> methods;
	String PATH_TO_CANDIDATES;
	String PYTHON_PATH;
	private String currentMethod;
	List<ExtractMethodResults> results;
	public static ICompilationUnit compilationUnit;
	public static ILog log;
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		log = Activator.getDefault().getLog();
		parseArguments();
		setPythonPath();
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);		
		ISelection selection = HandlerUtil.getCurrentSelection(event);		
		

		setFileStoragePath();
		
		deleteFile("test_candidates.csv");
		deleteFile("test_feasibility.csv");
		deleteFile("test_features.csv");
		deleteFile("test_prob.csv");
		
		try {
			boolean isJavaFile = checkIfCommandOnJavaFile();
			
			if(!isJavaFile){
				MessageDialog.openInformation(
						window.getShell(),
						"UI",
						"Please Choose a Java File and Method to refactor");
				log.log(new org.eclipse.core.runtime.Status(IStatus.ERROR, Activator.PLUGIN_ID, "Please Choose a Java File and Method to refactor"));
			} else{
				//process the file get the methods and return the menu
				getAllMethodNames();
				int startLine;
				TextSelection textSel = (TextSelection) selection;
				int offset = textSel.getOffset();
				String text = textSel.getText();
				currentMethod = text;
				startLine = textSel.getStartLine();
				if(methods.contains(text)){
					if(selection instanceof TextSelection){
						generateFeatures(REF_FILE.getRawLocation().toOSString(), text, startLine);
						CandidateValidation validation = new CandidateValidation(PATH_TO_CANDIDATES, REF_FILE.getRawLocation().toOSString(), "test_feasibility.csv",
								PATH_TO_CANDIDATES);
						validation.processCandidates();
						//call the python process
						RankCandidates rank = new RankCandidates();
						try{
							log.log(new org.eclipse.core.runtime.Status(IStatus.ERROR, Activator.PLUGIN_ID, "Path and python "+PATH_TO_CANDIDATES+" , "+PYTHON_PATH));
							rank.callPythonProcess(PATH_TO_CANDIDATES,PYTHON_PATH);								
							readProbabilityResults();
							ModelProvider.INSTANCE.setResults(results);
							RefactoringResults.updateViewer();
						} catch(Exception ex){
							MessageDialog.openInformation(
									window.getShell(),
									"UI",
									"Exception while ranking candidates. Make sure python script is in the same path as the created csv files.");
							log.log(new org.eclipse.core.runtime.Status(IStatus.ERROR, Activator.PLUGIN_ID, "Exception while ranking candidates. Make sure python script is in the same path as the created csv files."));
						}						
					}						
				} else{
					MessageDialog.openInformation(
							window.getShell(),
							"UI",
							"Please Choose a Method Name from method definition for refactoring");										
					log.log(new org.eclipse.core.runtime.Status(IStatus.ERROR, Activator.PLUGIN_ID, "Please Choose a Method Name from method definition for refactoring"));
				}								
			}			 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			MessageDialog.openInformation(
					window.getShell(),
					"UI",
					"Please Choose a valid file");
			e.printStackTrace();
			log.log(new org.eclipse.core.runtime.Status(IStatus.ERROR, Activator.PLUGIN_ID, e.toString()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//Throw error back with event
			e.printStackTrace();
			MessageDialog.openInformation(
					window.getShell(),
					"UI",
					"Error while processing the chosen method please choose correctly.");
			log.log(new org.eclipse.core.runtime.Status(IStatus.ERROR, Activator.PLUGIN_ID, e.toString()));
		}
		return null;
	}

	private void setFileStoragePath() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();			
		IPath rootPath = root.getLocation();
		PATH_TO_CANDIDATES = rootPath.toOSString()+"/gems/";
	}
	
	public void generateFeatures(String filePath,String methodName, int lineNo) throws Exception{
		AstComparator compartor = new AstComparator();					
		System.out.println("Output is saved in dir : "+PATH_TO_CANDIDATES);
		log.log(new org.eclipse.core.runtime.Status(IStatus.INFO, Activator.PLUGIN_ID,"Output is saved in dir : "+PATH_TO_CANDIDATES));
		compartor.compare(filePath,methodName,lineNo,PATH_TO_CANDIDATES);								
	}
	
	private void parseArguments(){
		String args[] = Platform.getCommandLineArgs();
		for(int i=0;i<args.length;i++){
			if(args[i].contains("pythonPath")){
				PYTHON_PATH = args[i+1];
				i++;
			}
		}
	}
	
	private void deleteFile(String fileName){
		try{
			File file = new File(PATH_TO_CANDIDATES+fileName);
			if(file.delete()){
				System.out.println("Deleted "+fileName);
			}
		} catch(Exception ex){
			System.out.println("Please delete the csv file: "+ fileName +" at location : "+PATH_TO_CANDIDATES);
			log.log(new org.eclipse.core.runtime.Status(IStatus.ERROR, Activator.PLUGIN_ID, "Please delete the csv file: "+ fileName +" at location : "+PATH_TO_CANDIDATES));
		}			
	}
	private void getAllMethodNames() {
		// TODO Auto-generated method stub		
	    compilationUnit = (ICompilationUnit) JavaCore.create(REF_FILE);
	    
	    methods = new ArrayList<String>();
	    try {
			IType[] allTypes = compilationUnit.getAllTypes();
			for (IType type : allTypes){
				for(IMethod method : type.getMethods()){
					methods.add(method.getElementName());
				}
			}			
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.log(new org.eclipse.core.runtime.Status(IStatus.ERROR, Activator.PLUGIN_ID, e.toString()));
		}
	    
	}

	public boolean checkIfCommandOnJavaFile() throws FileNotFoundException{		 	        
		IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart(); 
		IFile file = (IFile) workbenchPart.getSite().getPage().getActiveEditor().getEditorInput().getAdapter(IFile.class);
		if (file == null) throw new FileNotFoundException();
		if (file.getFileExtension().contains("java")) {
			REF_FILE = file;
			return true;
		}
		return false;
	}
	
	public void readProbabilityResults() throws IOException {
		results = new ArrayList<>();
		String csvFile = PATH_TO_CANDIDATES+"test_prob.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        br = new BufferedReader(new FileReader(csvFile));
        int count=1;
        while ((line = br.readLine()) != null) {
            // use comma as separator
            String[] data = line.split(cvsSplitBy);
            if(count<31){
            	results.add(new ExtractMethodResults(String.valueOf(count), currentMethod, data[0],String.valueOf(Integer.parseInt(data[1])-1), data[2]));
            }            
            count++;
        }
	}
	
	public void setPythonPath(){
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);	
		
		log.log(new org.eclipse.core.runtime.Status(IStatus.ERROR, Activator.PLUGIN_ID, prefs.absolutePath()));
		try {
			if(prefs.keys().length==0){
				prefs.put("PYTHON_PATH", "<replace_this_with_python_path>");
				log.log(new org.eclipse.core.runtime.Status(IStatus.ERROR, Activator.PLUGIN_ID, "Lenght is 0"));
			} else{
				for (String key: prefs.keys()){
					if(key.compareTo("PYTHON_PATH")==0){
						String def;
						PYTHON_PATH = prefs.get("PYTHON_PATH", null);
						log.log(new org.eclipse.core.runtime.Status(IStatus.ERROR, Activator.PLUGIN_ID, "Key found and the value is "+PYTHON_PATH));
					} else{
						prefs.put("PYTHON_PATH", "<replace_this_with_python_path>");
						log.log(new org.eclipse.core.runtime.Status(IStatus.ERROR, Activator.PLUGIN_ID, "key notfound"));
					}
				}
			}			
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block		
			e.printStackTrace();
		}
		
	}
	
}
