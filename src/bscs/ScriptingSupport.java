package bscs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;

import practicallymacro.commands.IMacroScriptSupport;
import practicallymacro.util.MacroConsole;
import practicallymacro.util.Utilities;
import bsh.EvalError;
import bsh.Interpreter;

public class ScriptingSupport implements IMacroScriptSupport {

	public static final String ID="bscs.scriptingSupport";
	public static final String DisplayID="Beanshell Scripting";
	
	public ScriptingSupport()
	{
		// nothing to do
	}

	public String editScript(String script, Shell shell)
	{
		ScriptConfigureDialog dlg=new ScriptConfigureDialog(shell, script);
		if (dlg.open()==Dialog.OK)
		{
			return dlg.getScript();
		}
		return script;
	}

	public boolean evaluate(String script, IEditorPart target)
	{
		// use bean shell to evaluate script
		if (script!=null)
		{
			Interpreter executor=new Interpreter();
			try
			{
				executor.set("styledText", Utilities.getStyledText(target));
				executor.set("findTarget", Utilities.getFindReplaceTarget(target));
				executor.set("console", MacroConsole.getConsole());
				//TODO: create a delegate for certain operations or let them issue macro commands
				Object obj=executor.eval(script);
				if (obj instanceof Boolean)
				{
					return ((Boolean)obj).booleanValue();
				}
				return true;
			}
			catch (EvalError e)
			{
				e.printStackTrace();
				MacroConsole.getConsole().write(e);
			}
		}
		
		return false;
	}

	public String getID() {
		return ID;
	}

	public String getIDDisplayString() {
		return DisplayID;
	}

}
