package com.cevelop.gslator.nodes;

import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;


@SuppressWarnings("restriction")
public class ASTComment extends ASTNode implements IASTComment {

    private char[] comment = {};

    @Override
    public IASTNode copy() {
        ASTComment newcomment = new ASTComment();
        newcomment.setText(comment);

        return newcomment;
    }

    @Override
    public IASTNode copy(CopyStyle style) {
        return super.copy((ASTComment) this.copy(), style);
    }
    /*
     * bla
     */

    @Override
    public void setComment(char[] comment) {
        String s = new String(comment);
        if (!s.startsWith("//") && !s.startsWith("/*")) {
            String tmp = s.replaceFirst("\\n\\s*$", "");
            if (!tmp.contains("\n")) {
                s.replaceFirst("\\s*$", "");
                s = "// " + s;
            } else {
                s = "/* " + s;
                s = s.replaceAll("\\n(?!\\s*\\*)", "\n * ");
                s = s.replaceAll("\\*/", "");
                s = s + " */\n";
            }
        } else {
            if (s.startsWith("//")) {
                if (s.endsWith("\n") && s.replace("\n", "").contains("\n")) {
                    s = s.replaceFirst("\\n\\s*$", "");
                    s = s.replaceAll("\\n(?!\\s*\\/\\/)", "\n// ");
                    s = s + "\n";
                }
                if (!s.endsWith("\n")) {
                    s = s + "\n";
                }
            } else {
                s = s.replaceAll("\\*\\/.*\\n.*$", "");
                s = s.replaceAll("\\n(?!\\s*\\*)", "\n * ");
                s = s + " */\n";
            }
        }
        this.comment = s.toCharArray();
    }

    public void setText(char[] comment) {
        this.comment = comment;
    }

    @Override
    public char[] getComment() {
        return comment.clone();
    }

    @Override
    public boolean isBlockComment() {
        return new String(comment).contains("\n");
    }

    @Override
    public String toString() {

        return new String(comment);
    }
}
