begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.ozoneimpl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|ozoneimpl
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerType
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsProto
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ContainerSet
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|Container
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|Handler
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
name|ozone
operator|.
name|container
operator|.
name|keyvalue
operator|.
name|TarContainerPacker
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Control plane for container management in datanode.  */
end_comment

begin_class
DECL|class|ContainerController
specifier|public
class|class
name|ContainerController
block|{
DECL|field|containerSet
specifier|private
specifier|final
name|ContainerSet
name|containerSet
decl_stmt|;
DECL|field|handlers
specifier|private
specifier|final
name|Map
argument_list|<
name|ContainerType
argument_list|,
name|Handler
argument_list|>
name|handlers
decl_stmt|;
DECL|method|ContainerController (final ContainerSet containerSet, final Map<ContainerType, Handler> handlers)
specifier|public
name|ContainerController
parameter_list|(
specifier|final
name|ContainerSet
name|containerSet
parameter_list|,
specifier|final
name|Map
argument_list|<
name|ContainerType
argument_list|,
name|Handler
argument_list|>
name|handlers
parameter_list|)
block|{
name|this
operator|.
name|containerSet
operator|=
name|containerSet
expr_stmt|;
name|this
operator|.
name|handlers
operator|=
name|handlers
expr_stmt|;
block|}
comment|/**    * Returns the Container given a container id.    *    * @param containerId ID of the container    * @return Container    */
DECL|method|getContainer (final long containerId)
specifier|public
name|Container
name|getContainer
parameter_list|(
specifier|final
name|long
name|containerId
parameter_list|)
block|{
return|return
name|containerSet
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
return|;
block|}
comment|/**    * Marks the container for closing. Moves the container to CLOSING state.    *    * @param containerId Id of the container to update    * @throws IOException in case of exception    */
DECL|method|markContainerForClose (final long containerId)
specifier|public
name|void
name|markContainerForClose
parameter_list|(
specifier|final
name|long
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
name|Container
name|container
init|=
name|containerSet
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|container
operator|.
name|getContainerState
argument_list|()
operator|==
name|State
operator|.
name|OPEN
condition|)
block|{
name|getHandler
argument_list|(
name|container
argument_list|)
operator|.
name|markContainerForClose
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns the container report.    *    * @return ContainerReportsProto    * @throws IOException in case of exception    */
DECL|method|getContainerReport ()
specifier|public
name|ContainerReportsProto
name|getContainerReport
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|containerSet
operator|.
name|getContainerReport
argument_list|()
return|;
block|}
comment|/**    * Quasi closes a container given its id.    *    * @param containerId Id of the container to quasi close    * @throws IOException in case of exception    */
DECL|method|quasiCloseContainer (final long containerId)
specifier|public
name|void
name|quasiCloseContainer
parameter_list|(
specifier|final
name|long
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Container
name|container
init|=
name|containerSet
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|getHandler
argument_list|(
name|container
argument_list|)
operator|.
name|quasiCloseContainer
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
comment|/**    * Closes a container given its Id.    *    * @param containerId Id of the container to close    * @throws IOException in case of exception    */
DECL|method|closeContainer (final long containerId)
specifier|public
name|void
name|closeContainer
parameter_list|(
specifier|final
name|long
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Container
name|container
init|=
name|containerSet
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|getHandler
argument_list|(
name|container
argument_list|)
operator|.
name|closeContainer
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
DECL|method|importContainer (final ContainerType type, final long containerId, final long maxSize, final String originPipelineId, final String originNodeId, final FileInputStream rawContainerStream, final TarContainerPacker packer)
specifier|public
name|Container
name|importContainer
parameter_list|(
specifier|final
name|ContainerType
name|type
parameter_list|,
specifier|final
name|long
name|containerId
parameter_list|,
specifier|final
name|long
name|maxSize
parameter_list|,
specifier|final
name|String
name|originPipelineId
parameter_list|,
specifier|final
name|String
name|originNodeId
parameter_list|,
specifier|final
name|FileInputStream
name|rawContainerStream
parameter_list|,
specifier|final
name|TarContainerPacker
name|packer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|handlers
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|.
name|importContainer
argument_list|(
name|containerId
argument_list|,
name|maxSize
argument_list|,
name|originPipelineId
argument_list|,
name|originNodeId
argument_list|,
name|rawContainerStream
argument_list|,
name|packer
argument_list|)
return|;
block|}
comment|/**    * Deletes a container given its Id.    * @param containerId Id of the container to be deleted    * @throws IOException    */
DECL|method|deleteContainer (final long containerId)
specifier|public
name|void
name|deleteContainer
parameter_list|(
specifier|final
name|long
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Container
name|container
init|=
name|containerSet
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|getHandler
argument_list|(
name|container
argument_list|)
operator|.
name|deleteContainer
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
comment|/**    * Given a container, returns its handler instance.    *    * @param container Container    * @return handler of the container    */
DECL|method|getHandler (final Container container)
specifier|private
name|Handler
name|getHandler
parameter_list|(
specifier|final
name|Container
name|container
parameter_list|)
block|{
return|return
name|handlers
operator|.
name|get
argument_list|(
name|container
operator|.
name|getContainerType
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

