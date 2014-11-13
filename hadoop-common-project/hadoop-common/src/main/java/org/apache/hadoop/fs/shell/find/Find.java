begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.shell.find
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
operator|.
name|find
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
operator|.
name|CommandFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
operator|.
name|CommandFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
operator|.
name|FsCommand
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
operator|.
name|PathData
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
comment|/**  * Implements a Hadoop find command.  */
DECL|class|Find
specifier|public
class|class
name|Find
extends|extends
name|FsCommand
block|{
comment|/**    * Register the names for the count command    *     * @param factory the command factory that will instantiate this class    */
DECL|method|registerCommands (CommandFactory factory)
specifier|public
specifier|static
name|void
name|registerCommands
parameter_list|(
name|CommandFactory
name|factory
parameter_list|)
block|{
name|factory
operator|.
name|addClass
argument_list|(
name|Find
operator|.
name|class
argument_list|,
literal|"-find"
argument_list|)
expr_stmt|;
block|}
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"find"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"<path> ...<expression> ..."
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
decl_stmt|;
DECL|field|HELP
specifier|private
specifier|static
name|String
index|[]
name|HELP
init|=
block|{
literal|"Finds all files that match the specified expression and"
block|,
literal|"applies selected actions to them. If no<path> is specified"
block|,
literal|"then defaults to the current working directory. If no"
block|,
literal|"expression is specified then defaults to -print."
block|}
decl_stmt|;
DECL|field|OPTION_FOLLOW_LINK
specifier|private
specifier|static
specifier|final
name|String
name|OPTION_FOLLOW_LINK
init|=
literal|"L"
decl_stmt|;
DECL|field|OPTION_FOLLOW_ARG_LINK
specifier|private
specifier|static
specifier|final
name|String
name|OPTION_FOLLOW_ARG_LINK
init|=
literal|"H"
decl_stmt|;
comment|/** List of expressions recognized by this command. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|field|EXPRESSIONS
specifier|private
specifier|static
specifier|final
name|Class
index|[]
name|EXPRESSIONS
decl_stmt|;
static|static
block|{
comment|// Initialize the static variables.
name|EXPRESSIONS
operator|=
operator|new
name|Class
index|[]
block|{
comment|// Operator Expressions
name|And
operator|.
name|class
block|,
comment|// Action Expressions
name|Print
operator|.
name|class
block|,
comment|// Navigation Expressions
comment|// Matcher Expressions
name|Name
operator|.
name|class
block|}
expr_stmt|;
name|DESCRIPTION
operator|=
name|buildDescription
argument_list|(
name|ExpressionFactory
operator|.
name|getExpressionFactory
argument_list|()
argument_list|)
expr_stmt|;
comment|// Register the expressions with the expression factory.
name|registerExpressions
argument_list|(
name|ExpressionFactory
operator|.
name|getExpressionFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Options for use in this command */
DECL|field|options
specifier|private
name|FindOptions
name|options
decl_stmt|;
comment|/** Root expression for this instance of the command. */
DECL|field|rootExpression
specifier|private
name|Expression
name|rootExpression
decl_stmt|;
comment|/** Set of path items returning a {@link Result#STOP} result. */
DECL|field|stopPaths
specifier|private
name|HashSet
argument_list|<
name|Path
argument_list|>
name|stopPaths
init|=
operator|new
name|HashSet
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Register the expressions with the expression factory. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|registerExpressions (ExpressionFactory factory)
specifier|private
specifier|static
name|void
name|registerExpressions
parameter_list|(
name|ExpressionFactory
name|factory
parameter_list|)
block|{
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|Expression
argument_list|>
name|exprClass
range|:
name|EXPRESSIONS
control|)
block|{
name|factory
operator|.
name|registerExpression
argument_list|(
name|exprClass
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Build the description used by the help command. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|buildDescription (ExpressionFactory factory)
specifier|private
specifier|static
name|String
name|buildDescription
parameter_list|(
name|ExpressionFactory
name|factory
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|Expression
argument_list|>
name|operators
init|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|Expression
argument_list|>
name|primaries
init|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|Expression
argument_list|>
name|exprClass
range|:
name|EXPRESSIONS
control|)
block|{
name|Expression
name|expr
init|=
name|factory
operator|.
name|createExpression
argument_list|(
name|exprClass
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|expr
operator|.
name|isOperator
argument_list|()
condition|)
block|{
name|operators
operator|.
name|add
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|primaries
operator|.
name|add
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|operators
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Expression
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Expression
name|arg0
parameter_list|,
name|Expression
name|arg1
parameter_list|)
block|{
return|return
name|arg0
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|arg1
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|primaries
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Expression
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Expression
name|arg0
parameter_list|,
name|Expression
name|arg1
parameter_list|)
block|{
return|return
name|arg0
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|arg1
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|HELP
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|line
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"The following primary expressions are recognised:\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|Expression
name|expr
range|:
name|primaries
control|)
block|{
for|for
control|(
name|String
name|line
range|:
name|expr
operator|.
name|getUsage
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
operator|.
name|append
argument_list|(
name|line
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|line
range|:
name|expr
operator|.
name|getHelp
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"    "
argument_list|)
operator|.
name|append
argument_list|(
name|line
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"The following operators are recognised:\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|Expression
name|expr
range|:
name|operators
control|)
block|{
for|for
control|(
name|String
name|line
range|:
name|expr
operator|.
name|getUsage
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
operator|.
name|append
argument_list|(
name|line
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|line
range|:
name|expr
operator|.
name|getHelp
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"    "
argument_list|)
operator|.
name|append
argument_list|(
name|line
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Default constructor for the Find command. */
DECL|method|Find ()
specifier|public
name|Find
parameter_list|()
block|{
name|setRecursive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processOptions (LinkedList<String> args)
specifier|protected
name|void
name|processOptions
parameter_list|(
name|LinkedList
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|CommandFormat
name|cf
init|=
operator|new
name|CommandFormat
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|OPTION_FOLLOW_LINK
argument_list|,
name|OPTION_FOLLOW_ARG_LINK
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|cf
operator|.
name|getOpt
argument_list|(
name|OPTION_FOLLOW_LINK
argument_list|)
condition|)
block|{
name|getOptions
argument_list|()
operator|.
name|setFollowLink
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cf
operator|.
name|getOpt
argument_list|(
name|OPTION_FOLLOW_ARG_LINK
argument_list|)
condition|)
block|{
name|getOptions
argument_list|()
operator|.
name|setFollowArgLink
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// search for first non-path argument (ie starts with a "-") and capture and
comment|// remove the remaining arguments as expressions
name|LinkedList
argument_list|<
name|String
argument_list|>
name|expressionArgs
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|args
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|boolean
name|isPath
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|arg
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|isPath
condition|)
block|{
if|if
condition|(
name|arg
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|isPath
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|isPath
condition|)
block|{
name|expressionArgs
operator|.
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|args
operator|.
name|add
argument_list|(
name|Path
operator|.
name|CUR_DIR
argument_list|)
expr_stmt|;
block|}
name|Expression
name|expression
init|=
name|parseExpression
argument_list|(
name|expressionArgs
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|expression
operator|.
name|isAction
argument_list|()
condition|)
block|{
name|Expression
name|and
init|=
name|getExpression
argument_list|(
name|And
operator|.
name|class
argument_list|)
decl_stmt|;
name|Deque
argument_list|<
name|Expression
argument_list|>
name|children
init|=
operator|new
name|LinkedList
argument_list|<
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
name|children
operator|.
name|add
argument_list|(
name|getExpression
argument_list|(
name|Print
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|children
operator|.
name|add
argument_list|(
name|expression
argument_list|)
expr_stmt|;
name|and
operator|.
name|addChildren
argument_list|(
name|children
argument_list|)
expr_stmt|;
name|expression
operator|=
name|and
expr_stmt|;
block|}
name|setRootExpression
argument_list|(
name|expression
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the root expression for this find.    *     * @param expression    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|setRootExpression (Expression expression)
name|void
name|setRootExpression
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
name|this
operator|.
name|rootExpression
operator|=
name|expression
expr_stmt|;
block|}
comment|/**    * Return the root expression for this find.    *     * @return the root expression    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|getRootExpression ()
name|Expression
name|getRootExpression
parameter_list|()
block|{
return|return
name|this
operator|.
name|rootExpression
return|;
block|}
comment|/** Returns the current find options, creating them if necessary. */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|getOptions ()
name|FindOptions
name|getOptions
parameter_list|()
block|{
if|if
condition|(
name|options
operator|==
literal|null
condition|)
block|{
name|options
operator|=
name|createOptions
argument_list|()
expr_stmt|;
block|}
return|return
name|options
return|;
block|}
comment|/** Create a new set of find options. */
DECL|method|createOptions ()
specifier|private
name|FindOptions
name|createOptions
parameter_list|()
block|{
name|FindOptions
name|options
init|=
operator|new
name|FindOptions
argument_list|()
decl_stmt|;
name|options
operator|.
name|setOut
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|options
operator|.
name|setErr
argument_list|(
name|err
argument_list|)
expr_stmt|;
name|options
operator|.
name|setIn
argument_list|(
name|System
operator|.
name|in
argument_list|)
expr_stmt|;
name|options
operator|.
name|setCommandFactory
argument_list|(
name|getCommandFactory
argument_list|()
argument_list|)
expr_stmt|;
name|options
operator|.
name|setConfiguration
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|options
return|;
block|}
comment|/** Add the {@link PathData} item to the stop set. */
DECL|method|addStop (PathData item)
specifier|private
name|void
name|addStop
parameter_list|(
name|PathData
name|item
parameter_list|)
block|{
name|stopPaths
operator|.
name|add
argument_list|(
name|item
operator|.
name|path
argument_list|)
expr_stmt|;
block|}
comment|/** Returns true if the {@link PathData} item is in the stop set. */
DECL|method|isStop (PathData item)
specifier|private
name|boolean
name|isStop
parameter_list|(
name|PathData
name|item
parameter_list|)
block|{
return|return
name|stopPaths
operator|.
name|contains
argument_list|(
name|item
operator|.
name|path
argument_list|)
return|;
block|}
comment|/**    * Parse a list of arguments to to extract the {@link Expression} elements.    * The input Deque will be modified to remove the used elements.    *     * @param args arguments to be parsed    * @return list of {@link Expression} elements applicable to this command    * @throws IOException if list can not be parsed    */
DECL|method|parseExpression (Deque<String> args)
specifier|private
name|Expression
name|parseExpression
parameter_list|(
name|Deque
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|Deque
argument_list|<
name|Expression
argument_list|>
name|primaries
init|=
operator|new
name|LinkedList
argument_list|<
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
name|Deque
argument_list|<
name|Expression
argument_list|>
name|operators
init|=
operator|new
name|LinkedList
argument_list|<
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
name|Expression
name|prevExpr
init|=
name|getExpression
argument_list|(
name|And
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|arg
init|=
name|args
operator|.
name|pop
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"("
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|Expression
name|expr
init|=
name|parseExpression
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|primaries
operator|.
name|add
argument_list|(
name|expr
argument_list|)
expr_stmt|;
name|prevExpr
operator|=
operator|new
name|BaseExpression
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Result
name|apply
parameter_list|(
name|PathData
name|item
parameter_list|,
name|int
name|depth
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Result
operator|.
name|PASS
return|;
block|}
block|}
expr_stmt|;
comment|// stub the previous expression to be a non-op
block|}
elseif|else
if|if
condition|(
literal|")"
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
break|break;
block|}
elseif|else
if|if
condition|(
name|isExpression
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|Expression
name|expr
init|=
name|getExpression
argument_list|(
name|arg
argument_list|)
decl_stmt|;
name|expr
operator|.
name|addArguments
argument_list|(
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|expr
operator|.
name|isOperator
argument_list|()
condition|)
block|{
while|while
condition|(
operator|!
name|operators
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|operators
operator|.
name|peek
argument_list|()
operator|.
name|getPrecedence
argument_list|()
operator|>=
name|expr
operator|.
name|getPrecedence
argument_list|()
condition|)
block|{
name|Expression
name|op
init|=
name|operators
operator|.
name|pop
argument_list|()
decl_stmt|;
name|op
operator|.
name|addChildren
argument_list|(
name|primaries
argument_list|)
expr_stmt|;
name|primaries
operator|.
name|push
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
name|operators
operator|.
name|push
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|prevExpr
operator|.
name|isOperator
argument_list|()
condition|)
block|{
name|Expression
name|and
init|=
name|getExpression
argument_list|(
name|And
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|operators
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|operators
operator|.
name|peek
argument_list|()
operator|.
name|getPrecedence
argument_list|()
operator|>=
name|and
operator|.
name|getPrecedence
argument_list|()
condition|)
block|{
name|Expression
name|op
init|=
name|operators
operator|.
name|pop
argument_list|()
decl_stmt|;
name|op
operator|.
name|addChildren
argument_list|(
name|primaries
argument_list|)
expr_stmt|;
name|primaries
operator|.
name|push
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
name|operators
operator|.
name|push
argument_list|(
name|and
argument_list|)
expr_stmt|;
block|}
name|primaries
operator|.
name|push
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
name|prevExpr
operator|=
name|expr
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected argument: "
operator|+
name|arg
argument_list|)
throw|;
block|}
block|}
while|while
condition|(
operator|!
name|operators
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Expression
name|operator
init|=
name|operators
operator|.
name|pop
argument_list|()
decl_stmt|;
name|operator
operator|.
name|addChildren
argument_list|(
name|primaries
argument_list|)
expr_stmt|;
name|primaries
operator|.
name|push
argument_list|(
name|operator
argument_list|)
expr_stmt|;
block|}
return|return
name|primaries
operator|.
name|isEmpty
argument_list|()
condition|?
name|getExpression
argument_list|(
name|Print
operator|.
name|class
argument_list|)
else|:
name|primaries
operator|.
name|pop
argument_list|()
return|;
block|}
comment|/** Returns true if the target is an ancestor of the source. */
DECL|method|isAncestor (PathData source, PathData target)
specifier|private
name|boolean
name|isAncestor
parameter_list|(
name|PathData
name|source
parameter_list|,
name|PathData
name|target
parameter_list|)
block|{
for|for
control|(
name|Path
name|parent
init|=
name|source
operator|.
name|path
init|;
operator|(
name|parent
operator|!=
literal|null
operator|)
operator|&&
operator|!
name|parent
operator|.
name|isRoot
argument_list|()
condition|;
name|parent
operator|=
name|parent
operator|.
name|getParent
argument_list|()
control|)
block|{
if|if
condition|(
name|parent
operator|.
name|equals
argument_list|(
name|target
operator|.
name|path
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|recursePath (PathData item)
specifier|protected
name|void
name|recursePath
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isStop
argument_list|(
name|item
argument_list|)
condition|)
block|{
comment|// this item returned a stop result so don't recurse any further
return|return;
block|}
if|if
condition|(
name|getDepth
argument_list|()
operator|>=
name|getOptions
argument_list|()
operator|.
name|getMaxDepth
argument_list|()
condition|)
block|{
comment|// reached the maximum depth so don't got any further.
return|return;
block|}
if|if
condition|(
name|item
operator|.
name|stat
operator|.
name|isSymlink
argument_list|()
operator|&&
name|getOptions
argument_list|()
operator|.
name|isFollowLink
argument_list|()
condition|)
block|{
name|PathData
name|linkedItem
init|=
operator|new
name|PathData
argument_list|(
name|item
operator|.
name|stat
operator|.
name|getSymlink
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|isAncestor
argument_list|(
name|item
argument_list|,
name|linkedItem
argument_list|)
condition|)
block|{
name|getOptions
argument_list|()
operator|.
name|getErr
argument_list|()
operator|.
name|println
argument_list|(
literal|"Infinite loop ignored: "
operator|+
name|item
operator|.
name|toString
argument_list|()
operator|+
literal|" -> "
operator|+
name|linkedItem
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|linkedItem
operator|.
name|exists
condition|)
block|{
name|item
operator|=
name|linkedItem
expr_stmt|;
block|}
block|}
if|if
condition|(
name|item
operator|.
name|stat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|super
operator|.
name|recursePath
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|isPathRecursable (PathData item)
specifier|protected
name|boolean
name|isPathRecursable
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|item
operator|.
name|stat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|item
operator|.
name|stat
operator|.
name|isSymlink
argument_list|()
condition|)
block|{
name|PathData
name|linkedItem
init|=
operator|new
name|PathData
argument_list|(
name|item
operator|.
name|fs
operator|.
name|resolvePath
argument_list|(
name|item
operator|.
name|stat
operator|.
name|getSymlink
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|linkedItem
operator|.
name|stat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
if|if
condition|(
name|getOptions
argument_list|()
operator|.
name|isFollowLink
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|getOptions
argument_list|()
operator|.
name|isFollowArgLink
argument_list|()
operator|&&
operator|(
name|getDepth
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|processPath (PathData item)
specifier|protected
name|void
name|processPath
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|getOptions
argument_list|()
operator|.
name|isDepthFirst
argument_list|()
condition|)
block|{
comment|// depth first so leave until post processing
return|return;
block|}
name|applyItem
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postProcessPath (PathData item)
specifier|protected
name|void
name|postProcessPath
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|getOptions
argument_list|()
operator|.
name|isDepthFirst
argument_list|()
condition|)
block|{
comment|// not depth first so already processed
return|return;
block|}
name|applyItem
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
DECL|method|applyItem (PathData item)
specifier|private
name|void
name|applyItem
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|getDepth
argument_list|()
operator|>=
name|getOptions
argument_list|()
operator|.
name|getMinDepth
argument_list|()
condition|)
block|{
name|Result
name|result
init|=
name|getRootExpression
argument_list|()
operator|.
name|apply
argument_list|(
name|item
argument_list|,
name|getDepth
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|Result
operator|.
name|STOP
operator|.
name|equals
argument_list|(
name|result
argument_list|)
condition|)
block|{
name|addStop
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|processArguments (LinkedList<PathData> args)
specifier|protected
name|void
name|processArguments
parameter_list|(
name|LinkedList
argument_list|<
name|PathData
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|Expression
name|expr
init|=
name|getRootExpression
argument_list|()
decl_stmt|;
name|expr
operator|.
name|setOptions
argument_list|(
name|getOptions
argument_list|()
argument_list|)
expr_stmt|;
name|expr
operator|.
name|prepare
argument_list|()
expr_stmt|;
name|super
operator|.
name|processArguments
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|expr
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
comment|/** Gets a named expression from the factory. */
DECL|method|getExpression (String expressionName)
specifier|private
name|Expression
name|getExpression
parameter_list|(
name|String
name|expressionName
parameter_list|)
block|{
return|return
name|ExpressionFactory
operator|.
name|getExpressionFactory
argument_list|()
operator|.
name|getExpression
argument_list|(
name|expressionName
argument_list|,
name|getConf
argument_list|()
argument_list|)
return|;
block|}
comment|/** Gets an instance of an expression from the factory. */
DECL|method|getExpression ( Class<? extends Expression> expressionClass)
specifier|private
name|Expression
name|getExpression
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Expression
argument_list|>
name|expressionClass
parameter_list|)
block|{
return|return
name|ExpressionFactory
operator|.
name|getExpressionFactory
argument_list|()
operator|.
name|createExpression
argument_list|(
name|expressionClass
argument_list|,
name|getConf
argument_list|()
argument_list|)
return|;
block|}
comment|/** Asks the factory whether an expression is recognized. */
DECL|method|isExpression (String expressionName)
specifier|private
name|boolean
name|isExpression
parameter_list|(
name|String
name|expressionName
parameter_list|)
block|{
return|return
name|ExpressionFactory
operator|.
name|getExpressionFactory
argument_list|()
operator|.
name|isExpression
argument_list|(
name|expressionName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

