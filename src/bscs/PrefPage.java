package bscs;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import beanshellscriptsupport.Activator;

public class PrefPage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
//	private Text mBaseScriptText;
	private ProjectionViewer mBaseScriptTextViewer;

	public PrefPage() {
		// TODO Auto-generated constructor stub
	}

	public PrefPage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public PrefPage(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createContents(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(), Activator.PLUGIN_ID+".scriptHelp");
		Group g=new Group(parent, SWT.None);
		g.setText("Default script text");
		g.setLayout(new GridLayout());
		g.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		mBaseScriptTextViewer=ScriptConfigureDialog.createEditorViewer(g);
		mBaseScriptTextViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		mBaseScriptTextViewer.getDocument().set(Activator.getDefault().getPreferenceStore().getString(Initializer.Pref_DefaultScriptContents));
		
//		mBaseScriptText=new Text(g, SWT.MULTI | SWT.BORDER);
//		mBaseScriptText.setLayoutData(new GridData(GridData.FILL_BOTH));
//		mBaseScriptText.setText(Activator.getDefault().getPreferenceStore().getString(Initializer.Pref_DefaultScriptContents));
		return g;
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void performDefaults() {
		mBaseScriptTextViewer.getDocument().set(Activator.getDefault().getPreferenceStore().getDefaultString(Initializer.Pref_DefaultScriptContents));
		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		Activator.getDefault().getPreferenceStore().setValue(Initializer.Pref_DefaultScriptContents, mBaseScriptTextViewer.getDocument().get());
		return super.performOk();
	}

	
}
