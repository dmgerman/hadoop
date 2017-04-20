begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.model.mock
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|model
operator|.
name|mock
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
import|;
end_import

begin_comment
comment|/**  * Mock container id.  */
end_comment

begin_class
DECL|class|MockContainerId
specifier|public
class|class
name|MockContainerId
extends|extends
name|ContainerId
implements|implements
name|Cloneable
block|{
DECL|field|DEFAULT_APP_ATTEMPT_ID
specifier|private
specifier|static
specifier|final
name|MockApplicationAttemptId
name|DEFAULT_APP_ATTEMPT_ID
init|=
operator|new
name|MockApplicationAttemptId
argument_list|(
operator|new
name|MockApplicationId
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
DECL|field|containerId
specifier|private
name|long
name|containerId
decl_stmt|;
DECL|field|applicationAttemptId
specifier|private
name|ApplicationAttemptId
name|applicationAttemptId
decl_stmt|;
DECL|method|MockContainerId ()
name|MockContainerId
parameter_list|()
block|{   }
comment|/**    * Sets up a default app Attempt ID.    * @param containerId    */
DECL|method|MockContainerId (long containerId)
name|MockContainerId
parameter_list|(
name|long
name|containerId
parameter_list|)
block|{
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
name|this
operator|.
name|applicationAttemptId
operator|=
name|DEFAULT_APP_ATTEMPT_ID
expr_stmt|;
block|}
DECL|method|MockContainerId (ApplicationAttemptId applicationAttemptId, long containerId)
specifier|public
name|MockContainerId
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|,
name|long
name|containerId
parameter_list|)
block|{
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
name|this
operator|.
name|applicationAttemptId
operator|=
name|applicationAttemptId
expr_stmt|;
block|}
DECL|method|MockContainerId (ContainerId that)
name|MockContainerId
parameter_list|(
name|ContainerId
name|that
parameter_list|)
block|{
name|containerId
operator|=
name|that
operator|.
name|getContainerId
argument_list|()
expr_stmt|;
name|applicationAttemptId
operator|=
name|that
operator|.
name|getApplicationAttemptId
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Deprecated
annotation|@
name|Override
DECL|method|getId ()
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|containerId
return|;
block|}
comment|// TODO: Temporarily adding it back
DECL|method|setId (int id)
name|void
name|setId
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|containerId
operator|=
operator|(
name|long
operator|)
name|id
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getContainerId ()
specifier|public
name|long
name|getContainerId
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerId
return|;
block|}
annotation|@
name|Override
DECL|method|setContainerId (long id)
specifier|public
name|void
name|setContainerId
parameter_list|(
name|long
name|id
parameter_list|)
block|{
name|this
operator|.
name|containerId
operator|=
name|id
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicationAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
block|{
return|return
name|applicationAttemptId
return|;
block|}
annotation|@
name|Override
DECL|method|setApplicationAttemptId (ApplicationAttemptId applicationAttemptId)
specifier|public
name|void
name|setApplicationAttemptId
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|)
block|{
name|this
operator|.
name|applicationAttemptId
operator|=
name|applicationAttemptId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build ()
specifier|public
name|void
name|build
parameter_list|()
block|{    }
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"mockcontainer_"
operator|+
name|containerId
return|;
block|}
annotation|@
name|Override
DECL|method|clone ()
specifier|protected
name|Object
name|clone
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
return|return
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
block|}
end_class

end_unit

