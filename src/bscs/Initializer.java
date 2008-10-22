package bscs;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import beanshellscriptsupport.Activator;

public class Initializer extends AbstractPreferenceInitializer {

	public static final String Pref_DefaultScriptContents="PracticallyMacro_DefaultScriptConents";
	public Initializer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initializeDefaultPreferences() {
		StringBuffer buffer=new StringBuffer();
		buffer.append("//Scripts are beanshell format (see http://www.beanshell.org/) \n\n");
		buffer.append("//variable               type\n");
		buffer.append("//styledText             the org.eclipse.swt.custom.StyledText instance for the current editor\n");
		buffer.append("//console                write output to the macro console via console.write(String), .writeln(String), .write(Exception)\n");
		//invokeAction(ST.)
		buffer.append("//findTarget             the instance of org.eclipse.jface.text.IFindReplaceTarget\n");
		buffer.append("import org.eclipse.swt.custom.StyledText;\n");
		buffer.append("import org.eclipse.jface.text.IFindReplaceTarget;\n");
		buffer.append("\n");
//		buffer.append("\n");
//		buffer.append("\n");
//		buffer.append("\n");
		
		Activator.getDefault().getPreferenceStore().setDefault(Pref_DefaultScriptContents, buffer.toString());
	}

}
