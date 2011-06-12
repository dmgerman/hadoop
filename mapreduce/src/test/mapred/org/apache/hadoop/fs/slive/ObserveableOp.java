begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.slive
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|slive
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileSystem
import|;
end_import

begin_comment
comment|/**  * Operation which wraps a given operation and allows an observer to be notified  * when the operation is about to start and when the operation has finished  */
end_comment

begin_class
DECL|class|ObserveableOp
class|class
name|ObserveableOp
extends|extends
name|Operation
block|{
comment|/**    * The observation interface which class that wish to monitor starting and    * ending events must implement.    */
DECL|interface|Observer
interface|interface
name|Observer
block|{
DECL|method|notifyStarting (Operation op)
name|void
name|notifyStarting
parameter_list|(
name|Operation
name|op
parameter_list|)
function_decl|;
DECL|method|notifyFinished (Operation op)
name|void
name|notifyFinished
parameter_list|(
name|Operation
name|op
parameter_list|)
function_decl|;
block|}
DECL|field|op
specifier|private
name|Operation
name|op
decl_stmt|;
DECL|field|observer
specifier|private
name|Observer
name|observer
decl_stmt|;
DECL|method|ObserveableOp (Operation op, Observer observer)
name|ObserveableOp
parameter_list|(
name|Operation
name|op
parameter_list|,
name|Observer
name|observer
parameter_list|)
block|{
name|super
argument_list|(
name|op
operator|.
name|getType
argument_list|()
argument_list|,
name|op
operator|.
name|getConfig
argument_list|()
argument_list|,
name|op
operator|.
name|getRandom
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
name|this
operator|.
name|observer
operator|=
name|observer
expr_stmt|;
block|}
comment|/**    * Proxy to underlying operation toString()    */
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|op
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|// Operation
DECL|method|run (FileSystem fs)
name|List
argument_list|<
name|OperationOutput
argument_list|>
name|run
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
block|{
name|List
argument_list|<
name|OperationOutput
argument_list|>
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|observer
operator|!=
literal|null
condition|)
block|{
name|observer
operator|.
name|notifyStarting
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|op
operator|.
name|run
argument_list|(
name|fs
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|observer
operator|!=
literal|null
condition|)
block|{
name|observer
operator|.
name|notifyFinished
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

