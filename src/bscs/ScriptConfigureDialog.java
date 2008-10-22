package bscs;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import beanshellscriptsupport.Activator;
import bscs.epl.DefaultDocumentAdapter;

public class ScriptConfigureDialog extends TrayDialog
{
	private String mScript;
//	private Text mText;
	private ProjectionViewer mTextViewer;
	public ScriptConfigureDialog(Shell shell, String scriptData)
	{
		super(shell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		mScript=scriptData;
		setHelpAvailable(true);
		setDialogHelpAvailable(true);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(shell, Activator.PLUGIN_ID+".scriptHelp");
	}
	
	@Override
	protected Control createDialogArea(Composite parent)
	{
		getShell().setText("Edit Script");
//		Composite comp=new Composite(parent, SWT.None);
//		comp.setLayout(new GridLayout());
//		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group g=new Group(parent, SWT.None);
		g.setText("Script text");
		g.setLayout(new GridLayout());
		GridData gd=new GridData(GridData.FILL_BOTH);
		gd.widthHint=500;
		gd.heightHint=300;
		g.setLayoutData(gd);
//		GridData gd=new GridData(GridData.FILL_BOTH);
//		gd.widthHint=500;
//		gd.heightHint=300;
//		mText.setLayoutData(gd);
		if (mScript==null)
		{
			mScript=Activator.getDefault().getPreferenceStore().getDefaultString(Initializer.Pref_DefaultScriptContents);
		}
//		
//		mText.setText(mScript);
//		mText.setSelection(mText.getText().length());
		
//		mText=new Text(comp, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

        mTextViewer=createEditorViewer(g);
        mTextViewer.getTextWidget().setLayoutData(new GridData(GridData.FILL_BOTH));
        mTextViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		mTextViewer.getDocument().set(mScript);
		return g;
	}
	
	public static ProjectionViewer createEditorViewer(Composite comp)
	{
		boolean showFolding=false;
		boolean showLineNumbers=true;
		boolean showAnnotations=true;
        IVerticalRuler ruler = createVerticalRuler(showFolding, showLineNumbers, showAnnotations);		
        final ProjectionViewer sourceViewer = new ProjectionViewer(comp, ruler, null, false, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        IDocument document = new Document();
        DefaultDocumentAdapter content = new DefaultDocumentAdapter();
        content.setDocument(document);
        sourceViewer.setDocument(document, new AnnotationModel());
        sourceViewer.getTextWidget().setContent(content);
        
		IDocumentPartitioner partitioner =
			new FastPartitioner(
				new BeanshellPartitionScanner(),
				new String[] {
					BeanshellPartitionScanner.Partition_COMMENT });
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
        
        sourceViewer.configure(new BeanShellSourceViewerConfiguration(new BeanshellScript(document)));
        
        //this is one possible way of adding content assist; by leveraging the existing java code in Eclipse.  It doesn't look very feasible though.
//		//I think I would have to create an ITextEditor here that maps to my document
//		sourceViewer.configure(new JavaSourceViewerConfiguration(ColorManager.instance(), Activator.getDefault().getPreferenceStore(), null, null));
        
        final StyledText widget=sourceViewer.getTextWidget();
        final List<AnEdit> undoStack=new ArrayList<AnEdit>();
        final List<AnEdit> redoStack=new ArrayList<AnEdit>();
        widget.setData(UNDO_STACK_Key, undoStack);
        widget.setData(REDO_STACK_Key, redoStack);
        widget.setData(Stack_Edit_Key, Boolean.FALSE);
        widget.addExtendedModifyListener(new ExtendedModifyListener()
        {
        	public void modifyText(ExtendedModifyEvent event) {
        		if (((Boolean)widget.getData(Stack_Edit_Key))==Boolean.TRUE)
        			return;
        		
        		//clear redo stack if there's anything there
        		redoStack.clear();
        		
        		String currText = widget.getText();
        		List<AnEdit> edits=new ArrayList<AnEdit>();
        		if (event.replacedText.length()>0)
        		{
        			edits.add(new DeleteEdit(event.start, event.replacedText));
        		}
        		if (event.length>0)
        		{
        			String newText = currText.substring(event.start, event.start+ event.length);
        			edits.add(new InsertEdit(newText, event.start));
        		}
        		
        		if (edits.size()>0)
        		{
        			if (edits.size()==1)
        			{
        				undoStack.add(0, edits.get(0));
        			}
        			else
        			{
        				undoStack.add(0, new CompoundEdit(edits));
        			}
//        			if (undoStack.size() == MAX_STACK_SIZE) {
//        				undoStack.remove(undoStack.size() - 1);
//        			}
        		}
        	}
        });
        
        widget.addKeyListener(new KeyAdapter()
        {
        	@Override
			public void keyPressed(KeyEvent e)
        	{
//        		if (Character.isISOControl(e.character))
//        			return;
        		boolean isMod1=((e.stateMask & SWT.MOD1) > 0); //ctrl
        		if (!isMod1)
        			return;

        		switch (e.keyCode) {
        		case 'z':
        			undo(undoStack, redoStack, widget);
        			break;
        		case 'y':
        			redo(undoStack, redoStack, widget);
        			break;
        		default:
        			//ignore everything else
        		}
        	}
        });
        
        sourceViewer.getTextWidget().setMenu(createStandardMenu(sourceViewer.getTextWidget()));
        
        //more content assistant code; all this is untested.
//		IAction action= new ContentAssistAction(JavaEditorMessages.getBundleForConstructedKeys(), "ContentAssistProposal.", this); //$NON-NLS-1$
//		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
//		setAction("ContentAssistProposal", action); //$NON-NLS-1$
//		markAsStateDependentAction("ContentAssistProposal", true); //$NON-NLS-1$
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(action, IJavaHelpContextIds.CONTENT_ASSIST_ACTION);
//		ISourceViewer sourceViewer= getSourceViewer();
//		if (sourceViewer instanceof ISourceViewerExtension4)
//			fKeyBindingSupportForAssistant= new KeyBindingSupportForAssistant(((ISourceViewerExtension4) sourceViewer).getContentAssistantFacade());
        
//        sourceViewer.getContentAssistantFacade().addCompletionListener(new ICompletionListener()
//        {
//			public void assistSessionEnded(ContentAssistEvent event) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			public void assistSessionStarted(ContentAssistEvent event) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			public void selectionChanged(ICompletionProposal proposal,
//					boolean smartToggle) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
        return sourceViewer;
	}
	
	@SuppressWarnings("unchecked")
	private static Menu createStandardMenu(final StyledText parent)
	{
		final List<AnEdit> undoStack=(List<AnEdit>)parent.getData(UNDO_STACK_Key);
		final List<AnEdit> redoStack=(List<AnEdit>)parent.getData(REDO_STACK_Key);
		Menu menu=new Menu(parent);
		MenuItem item=new MenuItem(menu, SWT.PUSH);
		item.setText("Cut");
		item.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				parent.cut();
			}
		});
		item=new MenuItem(menu, SWT.PUSH);
		item.setText("Copy");
		item.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				parent.copy();
			}
		});
		item=new MenuItem(menu, SWT.PUSH);
		item.setText("Paste");
		item.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				parent.paste();
			}
		});
		item=new MenuItem(menu, SWT.PUSH);
		item.setText("Undo");
		item.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				undo(undoStack, redoStack, parent);
			}
		});
		item=new MenuItem(menu, SWT.PUSH);
		item.setText("Redo");
		item.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				redo(undoStack, redoStack, parent);
			}
		});
		return menu;
	}

	private static IVerticalRuler createVerticalRuler(boolean showFolding,
			boolean showLineNumbers, boolean showAnnotations)
	{
		CompositeRuler ruler=new CompositeRuler();
		if (showLineNumbers)
		{
			LineNumberRulerColumn lineNumberColumn=new LineNumberRulerColumn();
			lineNumberColumn.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_CYAN));
			ruler.addDecorator(0, lineNumberColumn);
		}
		return ruler;
	}

	@Override
	protected void okPressed()
	{
		mScript=mTextViewer.getDocument().get();
		super.okPressed();
	}

	public String getScript()
	{
		return mScript;
	}

//	  private static final int MAX_STACK_SIZE = 25;
	  private static final String UNDO_STACK_Key="UndoStack";
	  private static final String REDO_STACK_Key="RedoStack";
	  private static final String Stack_Edit_Key="BeanShell_PerformingUndoRedoEdit";

	  private static void undo(List<AnEdit> undoStack, List<AnEdit> redoStack, StyledText styledText)
	  {
		  if (undoStack.size() > 0)
		  {
			  AnEdit lastEdit = undoStack.remove(0);
			  styledText.setData(Stack_Edit_Key, Boolean.TRUE);
			  lastEdit.performReverseEdit(styledText);
			  styledText.setData(Stack_Edit_Key, Boolean.FALSE);
			  redoStack.add(0, lastEdit);
		  }
	  }

	  private static void redo(List<AnEdit> undoStack, List<AnEdit> redoStack, StyledText styledText)
	  {
		  if (redoStack.size() > 0) {
			  AnEdit edit = redoStack.remove(0);
			  styledText.setData(Stack_Edit_Key, Boolean.TRUE);
			  edit.performEdit(styledText);
			  styledText.setData(Stack_Edit_Key, Boolean.FALSE);
			  undoStack.add(0, edit);
		  }
	  }

	  private interface AnEdit
	  {
		  void performEdit(StyledText text);
		  void performReverseEdit(StyledText text);
	  }
	  
	  private static class InsertEdit implements AnEdit
	  {
		  private String mText;
		  private int mOffset;
		  public InsertEdit(String text, int offset)
		  {
			  mText=text;
			  mOffset=offset;
		  }
		  
		  public void performEdit(StyledText text) {
			  text.replaceTextRange(mOffset, 0, mText);
			  text.setCaretOffset(mOffset+mText.length());
		  }
		  public void performReverseEdit(StyledText text)
		  {
			  text.replaceTextRange(mOffset, mText.length(), "");
			  text.setCaretOffset(mOffset);
		  }
	  }
	  
	  private static class CompoundEdit implements AnEdit
	  {
		  List<AnEdit> mEdits;
		  public CompoundEdit(List<AnEdit> edits)
		  {
			  mEdits=edits;
		  }
		  public void performEdit(StyledText text) {
			  for (AnEdit edit : mEdits) {
				edit.performEdit(text);
			}
		  }
		  public void performReverseEdit(StyledText text)
		  {
			  for (int i=mEdits.size()-1;i>=0;i--)
			  {
				  AnEdit edit=mEdits.get(i);
				  edit.performReverseEdit(text);
			  }
		  }
	  }
	  
	  public static class DeleteEdit implements AnEdit
	  {
		  private int mOffset;
		  private String mText;
		  public DeleteEdit(int offset, String deletedText)
		  {
			  mOffset=offset;
			  mText=deletedText;
		  }
		  
		  public void performEdit(StyledText text)
		  {
			  text.replaceTextRange(mOffset, mText.length(), "");
			  text.setCaretOffset(mOffset);
		  }
		  public void performReverseEdit(StyledText text)
		  {
			  text.replaceTextRange(mOffset, 0, mText);
			  text.setCaretOffset(mOffset+mText.length());
		  }
	  }
}
