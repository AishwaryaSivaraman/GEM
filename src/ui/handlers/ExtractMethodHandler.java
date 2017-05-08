package ui.handlers;

import gumtree.spoon.AstComparator;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
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

public class ExtractMethodHandler extends AbstractHandler{
	IFile REF_FILE;
	List<String> methods;
	String PATH;
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {		 
//		showPopUp("Hello");
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);		
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		String args[] = Platform.getApplicationArgs();
		PATH = Paths.get(".").toAbsolutePath().normalize().toString();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IWorkspaceRoot root = workspace.getRoot();

		IPath rootPath = root.getLocation();
		PATH = rootPath.toOSString();
		try {
			boolean isJavaFile = checkIfCommandOnJavaFile();
			
			if(!isJavaFile){
				MessageDialog.openInformation(
						window.getShell(),
						"UI",
						"Please Choose a Java File and Method to refactor");
			} else{
				//process the file get the methods and return the menu
				getAllMethodNames();
				int startLine;
				TextSelection textSel = (TextSelection) selection;
				String text = textSel.getText();
				startLine = textSel.getStartLine();
				generateFeatures(REF_FILE.getRawLocation().toOSString(), text, startLine);
				if(selection instanceof TextSelection){
					
				}
			}
			 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			MessageDialog.openInformation(
					window.getShell(),
					"UI",
					"Please Choose a valid file");
			e.printStackTrace();
		}
		return null;
	}
	
	public void generateFeatures(String filePath,String methodName, int lineNo){
		AstComparator compartor = new AstComparator();
		try {			
			System.out.println("Output is saved in dir : "+PATH);
			compartor.compare(filePath,methodName,lineNo,PATH);
//			compartor.compare("/Users/Aish/Downloads/JHotDraw5.2/sources/CH/ifa/draw/util/Iconkit.java", "getImage", 125);
//			compartor.compare("/Users/Aish/Downloads/JHotDraw5.2/sources/CH/ifa/draw/util/Iconkit.java", "getImage", 125,"/Users/Aish/Downloads/");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getAllMethodNames() {
		// TODO Auto-generated method stub		
	    ICompilationUnit compilationUnit = (ICompilationUnit) JavaCore.create(REF_FILE);
	    try {
			IType[] allTypes = compilationUnit.getAllTypes();
			for (IType type : allTypes){
				for(IMethod method : type.getMethods()){
					System.out.println(method.getElementName());
				}
			}			
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	
}
