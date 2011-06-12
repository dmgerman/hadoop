begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|List
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

begin_comment
comment|/**  * This class wraps a list of problems with the input, so that the user  * can get a list of problems together instead of finding and fixing them one   * by one.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|InvalidInputException
specifier|public
class|class
name|InvalidInputException
extends|extends
name|IOException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|problems
specifier|private
name|List
argument_list|<
name|IOException
argument_list|>
name|problems
decl_stmt|;
comment|/**    * Create the exception with the given list.    * @param probs the list of problems to report. this list is not copied.    */
DECL|method|InvalidInputException (List<IOException> probs)
specifier|public
name|InvalidInputException
parameter_list|(
name|List
argument_list|<
name|IOException
argument_list|>
name|probs
parameter_list|)
block|{
name|problems
operator|=
name|probs
expr_stmt|;
block|}
comment|/**    * Get the complete list of the problems reported.    * @return the list of problems, which must not be modified    */
DECL|method|getProblems ()
specifier|public
name|List
argument_list|<
name|IOException
argument_list|>
name|getProblems
parameter_list|()
block|{
return|return
name|problems
return|;
block|}
comment|/**    * Get a summary message of the problems found.    * @return the concatenated messages from all of the problems.    */
DECL|method|getMessage ()
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|IOException
argument_list|>
name|itr
init|=
name|problems
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
name|itr
operator|.
name|next
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

