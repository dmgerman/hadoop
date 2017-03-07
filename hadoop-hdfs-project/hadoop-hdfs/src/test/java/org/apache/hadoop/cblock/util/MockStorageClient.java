begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cblock.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
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
name|scm
operator|.
name|client
operator|.
name|ScmClient
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * This class is the one that directly talks to SCM server.  *  * NOTE : this is only a mock class, only to allow testing volume  * creation without actually creating containers. In real world, need to be  * replaced with actual container look up calls.  *  */
end_comment

begin_class
DECL|class|MockStorageClient
specifier|public
class|class
name|MockStorageClient
implements|implements
name|ScmClient
block|{
DECL|field|currentContainerId
specifier|private
specifier|static
name|long
name|currentContainerId
init|=
operator|-
literal|1
decl_stmt|;
comment|/**    * Ask SCM to get a exclusive container.    *    * @return A container descriptor object to locate this container    * @throws Exception    */
annotation|@
name|Override
DECL|method|createContainer (String containerId)
specifier|public
name|Pipeline
name|createContainer
parameter_list|(
name|String
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
name|currentContainerId
operator|+=
literal|1
expr_stmt|;
name|ContainerLookUpService
operator|.
name|addContainer
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|currentContainerId
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|ContainerLookUpService
operator|.
name|lookUp
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|currentContainerId
argument_list|)
argument_list|)
operator|.
name|getPipeline
argument_list|()
return|;
block|}
comment|/**    * As this is only a testing class, with all "container" maintained in    * memory, no need to really delete anything for now.    * @throws IOException    */
annotation|@
name|Override
DECL|method|deleteContainer (Pipeline pipeline)
specifier|public
name|void
name|deleteContainer
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
block|{    }
comment|/**    * Return reference to an *existing* container with given ID.    *    * @param containerId    * @return    * @throws IOException    */
DECL|method|getContainer (String containerId)
specifier|public
name|Pipeline
name|getContainer
parameter_list|(
name|String
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ContainerLookUpService
operator|.
name|lookUp
argument_list|(
name|containerId
argument_list|)
operator|.
name|getPipeline
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getContainerSize (Pipeline pipeline)
specifier|public
name|long
name|getContainerSize
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
block|{
comment|// just return a constant value for now
return|return
literal|5L
operator|*
literal|1024
operator|*
literal|1024
operator|*
literal|1024
return|;
comment|// 5GB
block|}
block|}
end_class

end_unit

