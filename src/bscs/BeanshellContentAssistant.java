package bscs;

import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;

public class BeanshellContentAssistant extends ContentAssistant
{
	private IContentAssistProcessor mProcessor;
	
	public BeanshellContentAssistant(BeanshellScript script)
	{
		mProcessor=new BeanshellContentAssistantProcessor(script);
	}
	
	@Override
	public IContentAssistProcessor getContentAssistProcessor(String contentType) {
		return mProcessor;
	}


	@Override
	public void uninstall() {
		// TODO Auto-generated method stub

	}

}
