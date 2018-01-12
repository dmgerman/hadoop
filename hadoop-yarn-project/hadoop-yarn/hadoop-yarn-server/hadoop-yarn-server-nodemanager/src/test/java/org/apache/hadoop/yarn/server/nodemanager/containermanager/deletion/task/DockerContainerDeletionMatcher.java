begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.deletion.task
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|deletion
operator|.
name|task
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
name|server
operator|.
name|nodemanager
operator|.
name|DeletionService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatcher
import|;
end_import

begin_comment
comment|/**  * ArgumentMatcher to check the arguments of the  * {@link DockerContainerDeletionTask}.  */
end_comment

begin_class
DECL|class|DockerContainerDeletionMatcher
specifier|public
class|class
name|DockerContainerDeletionMatcher
extends|extends
name|ArgumentMatcher
argument_list|<
name|DockerContainerDeletionTask
argument_list|>
block|{
DECL|field|delService
specifier|private
specifier|final
name|DeletionService
name|delService
decl_stmt|;
DECL|field|containerId
specifier|private
specifier|final
name|String
name|containerId
decl_stmt|;
DECL|method|DockerContainerDeletionMatcher (DeletionService delService, String containerId)
specifier|public
name|DockerContainerDeletionMatcher
parameter_list|(
name|DeletionService
name|delService
parameter_list|,
name|String
name|containerId
parameter_list|)
block|{
name|this
operator|.
name|delService
operator|=
name|delService
expr_stmt|;
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|matches (Object o)
specifier|public
name|boolean
name|matches
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|DockerContainerDeletionTask
name|task
init|=
operator|(
name|DockerContainerDeletionTask
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|task
operator|.
name|getContainerId
argument_list|()
operator|==
literal|null
operator|&&
name|containerId
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|task
operator|.
name|getContainerId
argument_list|()
operator|!=
literal|null
operator|&&
name|containerId
operator|!=
literal|null
condition|)
block|{
return|return
name|task
operator|.
name|getContainerId
argument_list|()
operator|.
name|equals
argument_list|(
name|containerId
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

