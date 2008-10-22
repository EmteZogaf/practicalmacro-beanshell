package bscs;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.jface.text.*;
import org.eclipse.jface.text.rules.*;


public class BeanshellCodeScanner extends RuleBasedScanner {

	public BeanshellCodeScanner() {
		IToken string =new Token(new TextAttribute(ColorManager.instance().getColor(ColorConstants.STRING)));
		IToken reservedWord=new Token(new TextAttribute(ColorManager.instance().getColor(ColorConstants.RESERVED_WORD)));
		
		List<IRule> rules=new ArrayList<IRule>();

		// Add rule for double quote String
		rules.add(new SingleLineRule("\"", "\"", string, '\\', true));
		// Add a rule for single quote String
		rules.add(new SingleLineRule("'", "'", string, '\\', true));
	
		//add rule for reserved words
		WordRule reservedWordRule = new WordRule(new IWordDetector()
		{
            public boolean isWordStart(char c) { 
         	   return Character.isJavaIdentifierStart(c); 
            }
            public boolean isWordPart(char c) {   
            	return Character.isJavaIdentifierPart(c); 
            }
         });
		
		String[] reservedWords = new String[] { "abstract", "continue", "for",
				"new", "switch", "assert", "default", "goto", "package",
				"synchronized", "boolean", "do", "if", "private", "this",
				"break", "double", "implements", "protected", "throw", "byte",
				"else", "import", "public", "throws", "case", "enum",
				"instanceof", "return", "transient", "catch", "extends", "int",
				"short", "try", "char", "final", "interface", "static", "void",
				"class", "finally", "long", "strictfp", "volatile", "const",
				"float", "native", "super", "while" };

		for (String word: reservedWords) {
			reservedWordRule.addWord(word, reservedWord);
		}
		rules.add(reservedWordRule);

		setRules(rules.toArray(new IRule[]{}));
	}
}
