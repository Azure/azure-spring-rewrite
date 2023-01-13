//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.azure.spring.migration.openrewrite.java.search;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.Validated;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType.Primitive;
import org.openrewrite.marker.SearchResult;

public final class FindLiterals extends Recipe {
    @Option(
            displayName = "Pattern",
            description = "A regular expression pattern to match literals against.",
            example = "file://"
    )
    private final String pattern;

    private final String mark;

    public String getDisplayName() {
        return "Find literals";
    }

    public String getDescription() {
        return "Find literals matching a pattern.";
    }

    public Validated validate() {
        return super.validate().and(Validated.test("pattern", "Must be a valid regular expression", this.pattern, (p) -> {
            try {
                Pattern.compile(p);
                return true;
            } catch (PatternSyntaxException var2) {
                return false;
            }
        }));
    }

    public JavaVisitor<ExecutionContext> getVisitor() {
        final Pattern compiledPattern = Pattern.compile(this.pattern);
//        final String mark = this.mark;
        return new JavaIsoVisitor<ExecutionContext>() {
            public J.Literal visitLiteral(J.Literal literal, ExecutionContext ctx) {
                if (literal.getValueSource() != null) {
                    if (literal.getType() == Primitive.String && compiledPattern.matcher(literal.getValueSource().substring(1, literal.getValueSource().length() - 1)).matches()) {
                        return (J.Literal)SearchResult.found(literal,mark);
                    }

                    if (compiledPattern.matcher(literal.getValueSource()).matches()) {
                        return (J.Literal)SearchResult.found(literal,mark);
                    }
                }

                return literal;
            }
        };
    }

    public FindLiterals(final String pattern, final String mark) {
        this.pattern = pattern;
        this.mark = mark;
    }

    public String getPattern() {
        return this.pattern;
    }

    public @NonNull String toString() {
        return "FindLiterals(pattern=" + this.getPattern() + ")";
    }

    public boolean equals(final @Nullable Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof FindLiterals)) {
            return false;
        } else {
            FindLiterals other = (FindLiterals)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$pattern = this.getPattern();
                Object other$pattern = other.getPattern();
                if (this$pattern == null) {
                    if (other$pattern != null) {
                        return false;
                    }
                } else if (!this$pattern.equals(other$pattern)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final @Nullable Object other) {
        return other instanceof FindLiterals;
    }

    public int hashCode() {
//        int PRIME = true;
        int result = 1;
        Object $pattern = this.getPattern();
        result = result * 59 + ($pattern == null ? 43 : $pattern.hashCode());
        return result;
    }
}
