begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|util
package|;
end_package

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

begin_comment
comment|/**  * Provides a simple interface where one thread can mark an operation  * for cancellation, and another thread can poll for whether the  * cancellation has occurred.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|Canceler
specifier|public
class|class
name|Canceler
block|{
comment|/**    * If the operation has been canceled, set to the reason why    * it has been canceled (eg standby moving to active)    */
DECL|field|cancelReason
specifier|private
specifier|volatile
name|String
name|cancelReason
init|=
literal|null
decl_stmt|;
comment|/**    * Requests that the current operation be canceled if it is still running.    * This does not block until the cancellation is successful.    * @param reason the reason why cancellation is requested    */
DECL|method|cancel (String reason)
specifier|public
name|void
name|cancel
parameter_list|(
name|String
name|reason
parameter_list|)
block|{
name|this
operator|.
name|cancelReason
operator|=
name|reason
expr_stmt|;
block|}
DECL|method|isCancelled ()
specifier|public
name|boolean
name|isCancelled
parameter_list|()
block|{
return|return
name|cancelReason
operator|!=
literal|null
return|;
block|}
DECL|method|getCancellationReason ()
specifier|public
name|String
name|getCancellationReason
parameter_list|()
block|{
return|return
name|cancelReason
return|;
block|}
block|}
end_class

end_unit

