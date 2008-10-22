package bscs;

import org.eclipse.jface.text.rules.*;

public class BeanshellPartitionScanner extends RuleBasedPartitionScanner {
	public final static String Partition_DEFAULT = "partition_beanshell_code";
	public final static String Partition_COMMENT = "partition_beanshell_comment";

	public BeanshellPartitionScanner() {

		IToken commentToken = new Token(Partition_COMMENT);

		IPredicateRule[] rules = new IPredicateRule[2];
		rules[0] = new MultiLineRule("/*", "*/", commentToken);
		rules[1] = new EndOfLineRule("//", commentToken);

		setPredicateRules(rules);
	}
}
