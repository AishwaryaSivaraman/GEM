package ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import ui.handlers.ExtractMethodHandler;
import Util.ExtractMethodResults;
import Util.ModelProvider;
import Util.TextRangeUtil;

public class RefactoringResults extends ViewPart{

	private static TableViewer viewer;
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
//		Text text = new Text(parent, SWT.BORDER);
//        text.setText("Imagine a fantastic user interface here");
        createViewer(parent);                
	}

	public static void updateViewer(){
		viewer.setInput(ModelProvider.INSTANCE.getResults());
		viewer.refresh();
	}
	
	public void selectCode(int startLineNo, int endLineNo){
//		IFile myfile = ExtractMethodHandler.REF_FILE;
//		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		ITextEditor editor = (ITextEditor) IDE.openEditor(page, myfile);
		try {
			ISourceRange range = TextRangeUtil.getSelection(ExtractMethodHandler.compilationUnit, startLineNo, 0, endLineNo, 0);
			ISelection selection =new TextSelection(range.getOffset(), range.getLength()); 
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getSite().getSelectionProvider().setSelection(selection);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void createViewer(Composite parent) {
	        viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
	              
	        createColumns(parent, viewer);
	        final Table table = viewer.getTable();
	        table.setHeaderVisible(true);
	        table.setLinesVisible(true);
	        
	        table.addListener(SWT.Selection, new Listener() {				
				@Override
				public void handleEvent(Event event) {
					ExtractMethodResults currentSelection = null;
			        TableItem[] selection = table.getSelection();
			        for (int i = 0; i < selection.length; i++){
			        	currentSelection = (ExtractMethodResults) selection[i].getData();
			        	
			        }
			        selectCode(Integer.parseInt(currentSelection.getStartinLineNo()), Integer.parseInt(currentSelection.getEndingLineNo()));  					
				}
			});

	        viewer.setContentProvider(new ArrayContentProvider());
	        // get the content for the viewer, setInput will call getElements in the
	        // contentProvider
	        viewer.setInput(ModelProvider.INSTANCE.getResults());
	        // make the selection available to other views
	        getSite().setSelectionProvider(viewer);
	        // set the sorter for the table

	        // define layout for the viewer
	        GridData gridData = new GridData();
	        gridData.verticalAlignment = GridData.FILL;
	        gridData.horizontalSpan = 2;
	        gridData.grabExcessHorizontalSpace = true;
	        gridData.grabExcessVerticalSpace = true;
	        gridData.horizontalAlignment = GridData.FILL;
	        viewer.getControl().setLayoutData(gridData);
    }

	private void createColumns(final Composite parent, final TableViewer viewer) {
	       String[] titles = { "Top", "Method Name", "Starting Line Number", "Ending Line Number","Probability"};
	       int[] bounds = { 200, 200, 200, 200,200 };
	       TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
	       col.setLabelProvider(new ColumnLabelProvider() {
	            @Override
	            public String getText(Object element) {
	                ExtractMethodResults p = (ExtractMethodResults) element;
	                return p.getTop();
	            }
	        });
	       col = createTableViewerColumn(titles[1], bounds[1], 1);	
	       col.setLabelProvider(new ColumnLabelProvider() {
	            @Override
	            public String getText(Object element) {
	                ExtractMethodResults p = (ExtractMethodResults) element;
	                return p.getMethodName();
	            }
	        });
	       col = createTableViewerColumn(titles[2], bounds[2], 2);
	       col.setLabelProvider(new ColumnLabelProvider() {
	            @Override
	            public String getText(Object element) {
	                ExtractMethodResults p = (ExtractMethodResults) element;
	                return p.getStartinLineNo();
	            }
	        });
	       col = createTableViewerColumn(titles[3], bounds[3], 3);
	       col.setLabelProvider(new ColumnLabelProvider() {
	            @Override
	            public String getText(Object element) {
	                ExtractMethodResults p = (ExtractMethodResults) element;
	                return p.getEndingLineNo();
	            }
	        });
	       col = createTableViewerColumn(titles[4], bounds[4], 4);
	       col.setLabelProvider(new ColumnLabelProvider() {
	            @Override
	            public String getText(Object element) {
	                ExtractMethodResults p = (ExtractMethodResults) element;
	                return p.getProb();
	            }
	        });
	       
	}
	
	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(true);
        return viewerColumn;
    }
	
	@Override
	public void setFocus() {		
		viewer.getControl().setFocus();	
	}

		 
}
