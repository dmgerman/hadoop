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
name|util
operator|.
name|Arrays
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
name|LinkedList
import|;
end_import

begin_comment
comment|/** Helper methods for the find expression unit tests. */
end_comment

begin_class
DECL|class|TestHelper
class|class
name|TestHelper
block|{
comment|/** Adds an argument string to an expression */
DECL|method|addArgument (Expression expr, String arg)
specifier|static
name|void
name|addArgument
parameter_list|(
name|Expression
name|expr
parameter_list|,
name|String
name|arg
parameter_list|)
block|{
name|expr
operator|.
name|addArguments
argument_list|(
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|arg
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Converts a command string into a list of arguments. */
DECL|method|getArgs (String cmd)
specifier|static
name|LinkedList
argument_list|<
name|String
argument_list|>
name|getArgs
parameter_list|(
name|String
name|cmd
parameter_list|)
block|{
return|return
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|cmd
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

