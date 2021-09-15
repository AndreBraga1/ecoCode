package fr.cnumr.php.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.php.api.tree.SeparatedList;
import org.sonar.plugins.php.api.tree.Tree;
import org.sonar.plugins.php.api.tree.Tree.Kind;
import org.sonar.plugins.php.api.tree.expression.BinaryExpressionTree;
import org.sonar.plugins.php.api.tree.expression.ExpressionTree;
import org.sonar.plugins.php.api.tree.statement.ForStatementTree;
import org.sonar.plugins.php.api.visitors.PHPSubscriptionCheck;

import java.util.Collections;
import java.util.List;

@Rule(
        key = "S69",
        name = "Developpement",
        description = "Do not call a function in the declaration of a for-type loop",
        priority = Priority.MINOR,
        tags = {"bug"})
public class NoFunctionCallWhenDeclaringForLoop extends PHPSubscriptionCheck {

    private static final String ErrorMessage = "Do not call a function in for-type loop declaration";

    @Override
    public List<Kind> nodesToVisit() {
        return Collections.singletonList(Kind.FOR_STATEMENT);
    }

    @Override
    public void visitNode(Tree tree) {
        ForStatementTree method = (ForStatementTree) tree;
        checkExpressionsTree(method.update());
        checkExpressionsTree(method.condition());
    }

    public void checkExpressionsTree(SeparatedList<ExpressionTree> treeSeparatedList) {
        treeSeparatedList.forEach(expressionTree -> {
            checkBothSideExpression(expressionTree);
        });
    }

    public void checkBothSideExpression(ExpressionTree expressionTree) {
        if (expressionTree.getKind().getAssociatedInterface() == BinaryExpressionTree.class) {
            BinaryExpressionTree binaryExpressionTree = (BinaryExpressionTree) expressionTree;
            isFunctionCall(binaryExpressionTree.leftOperand());
            isFunctionCall(binaryExpressionTree.rightOperand());
        } else
            isFunctionCall(expressionTree);
    }

    public void isFunctionCall(ExpressionTree expressionTree) {
        if (expressionTree.getKind() == Tree.Kind.FUNCTION_CALL)
            context().newIssue(this, expressionTree, ErrorMessage);
    }
}
