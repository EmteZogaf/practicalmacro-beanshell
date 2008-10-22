package bscs;


import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;


public class BeanShellSourceViewerConfiguration extends SourceViewerConfiguration
{
//	private IContentAssistant mContentAssistant;
	private BeanshellScript mScriptDoc;
	private BeanshellCodeScanner mScanner;
	PresentationReconciler mReconciler;

	public BeanShellSourceViewerConfiguration(BeanshellScript scriptDoc)
	{
//		mContentAssistant=null;
		mScriptDoc=scriptDoc;
	}

//	@Override
//	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer)
//	{
//		if (mContentAssistant!=null)
//			return mContentAssistant;
//		
//		mContentAssistant=new BeanshellContentAssistant(mScriptDoc);
//		((BeanshellContentAssistant)mContentAssistant).setInformationControlCreator(getInformationControlCreator(sourceViewer));
//		((BeanshellContentAssistant)mContentAssistant).enableAutoActivation(true);
//		((BeanshellContentAssistant)mContentAssistant).setContentAssistProcessor(mContentAssistant.getContentAssistProcessor(IDocument.DEFAULT_CONTENT_TYPE), IDocument.DEFAULT_CONTENT_TYPE);
//		return mContentAssistant;
//	}

//	@Override
//	public ITextHover getTextHover(ISourceViewer sourceViewer,
//			String contentType) {
//		return new BeanShellTextHover(sourceViewer, contentType);
//	}
//	
//	private static class BeanShellTextHover implements ITextHover
//	{
//		
//		public BeanShellTextHover(ISourceViewer sourceViewer, String contentType) {
//			// TODO Auto-generated constructor stub
//		}
//
//		public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//	}
	
	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		//for purposes of this method, there are only two types: comment and code
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE, BeanshellPartitionScanner.Partition_COMMENT};
	}

	protected BeanshellCodeScanner getDefaultScanner() {
		if (mScanner == null)
		{
			mScanner = new BeanshellCodeScanner();
			mScanner.setDefaultReturnToken(new Token(new TextAttribute(ColorManager.instance().getColor(ColorConstants.DEFAULT))));
		}
		return mScanner;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		if(mReconciler == null)
		{
			//we're either repairing the comments or the code
			mReconciler = new PresentationReconciler();
			
			DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getDefaultScanner());
			mReconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
			mReconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
			
			NonRuleBasedDamagerRepairer ndr =new NonRuleBasedDamagerRepairer(new TextAttribute(ColorManager.instance().getColor(ColorConstants.COMMENT)));
			mReconciler.setDamager(ndr, BeanshellPartitionScanner.Partition_COMMENT);
			mReconciler.setRepairer(ndr, BeanshellPartitionScanner.Partition_COMMENT);
		}

		return mReconciler;
	}
	
}
