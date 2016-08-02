begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.services.workflow
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|services
operator|.
name|workflow
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|service
operator|.
name|Service
import|;
end_import

begin_comment
comment|/**  * A runnable which terminates its after running; it also catches any  * exception raised and can serve it back.   */
end_comment

begin_class
DECL|class|ServiceTerminatingRunnable
specifier|public
class|class
name|ServiceTerminatingRunnable
implements|implements
name|Runnable
block|{
DECL|field|owner
specifier|private
specifier|final
name|Service
name|owner
decl_stmt|;
DECL|field|action
specifier|private
specifier|final
name|Runnable
name|action
decl_stmt|;
DECL|field|exception
specifier|private
name|Exception
name|exception
decl_stmt|;
comment|/**    * Create an instance.    * @param owner owning service    * @param action action to execute before terminating the service    */
DECL|method|ServiceTerminatingRunnable (Service owner, Runnable action)
specifier|public
name|ServiceTerminatingRunnable
parameter_list|(
name|Service
name|owner
parameter_list|,
name|Runnable
name|action
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|owner
operator|!=
literal|null
argument_list|,
literal|"null owner"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|action
operator|!=
literal|null
argument_list|,
literal|"null action"
argument_list|)
expr_stmt|;
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
block|}
comment|/**    * Get the owning service.    * @return the service to receive notification when    * the runnable completes.    */
DECL|method|getOwner ()
specifier|public
name|Service
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
comment|/**    * Any exception raised by inner<code>action's</code> run.    * @return an exception or null.    */
DECL|method|getException ()
specifier|public
name|Exception
name|getException
parameter_list|()
block|{
return|return
name|exception
return|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|action
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
name|owner
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

