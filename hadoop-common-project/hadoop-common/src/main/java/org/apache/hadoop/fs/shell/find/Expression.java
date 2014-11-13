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
name|Deque
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

begin_comment
comment|/**  * Interface describing an expression to be used in the  * {@link org.apache.hadoop.fs.shell.find.Find} command.  */
end_comment

begin_interface
DECL|interface|Expression
specifier|public
interface|interface
name|Expression
block|{
comment|/**    * Set the options for this expression, called once before processing any    * items.    */
DECL|method|setOptions (FindOptions options)
specifier|public
name|void
name|setOptions
parameter_list|(
name|FindOptions
name|options
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Prepares the expression for execution, called once after setting options    * and before processing any options.    * @throws IOException    */
DECL|method|prepare ()
specifier|public
name|void
name|prepare
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Apply the expression to the specified item, called once for each item.    *    * @param item {@link PathData} item to be processed    * @param depth distance of the item from the command line argument    * @return {@link Result} of applying the expression to the item    */
DECL|method|apply (PathData item, int depth)
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
function_decl|;
comment|/**    * Finishes the expression, called once after processing all items.    *    * @throws IOException    */
DECL|method|finish ()
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns brief usage instructions for this expression. Multiple items should    * be returned if there are multiple ways to use this expression.    *    * @return array of usage instructions    */
DECL|method|getUsage ()
specifier|public
name|String
index|[]
name|getUsage
parameter_list|()
function_decl|;
comment|/**    * Returns a description of the expression for use in help. Multiple lines    * should be returned array items. Lines should be formated to 60 characters    * or less.    *    * @return array of description lines    */
DECL|method|getHelp ()
specifier|public
name|String
index|[]
name|getHelp
parameter_list|()
function_decl|;
comment|/**    * Indicates whether this expression performs an action, i.e. provides output    * back to the user.    */
DECL|method|isAction ()
specifier|public
name|boolean
name|isAction
parameter_list|()
function_decl|;
comment|/** Identifies the expression as an operator rather than a primary. */
DECL|method|isOperator ()
specifier|public
name|boolean
name|isOperator
parameter_list|()
function_decl|;
comment|/**    * Returns the precedence of this expression    * (only applicable to operators).    */
DECL|method|getPrecedence ()
specifier|public
name|int
name|getPrecedence
parameter_list|()
function_decl|;
comment|/**    * Adds children to this expression. Children are popped from the head of the    * deque.    *    * @param expressions    *          deque of expressions from which to take the children    */
DECL|method|addChildren (Deque<Expression> expressions)
specifier|public
name|void
name|addChildren
parameter_list|(
name|Deque
argument_list|<
name|Expression
argument_list|>
name|expressions
parameter_list|)
function_decl|;
comment|/**    * Adds arguments to this expression. Arguments are popped from the head of    * the deque and added to the front of the child list, ie last child added is    * the first evaluated.    * @param args deque of arguments from which to take expression arguments    */
DECL|method|addArguments (Deque<String> args)
specifier|public
name|void
name|addArguments
parameter_list|(
name|Deque
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

