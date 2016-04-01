begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|conf
operator|.
name|Configuration
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|FsDatasetSpi
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|FsVolumeSpi
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
name|OzoneConfigKeys
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
name|ChunkManagerImpl
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
name|ContainerManagerImpl
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
name|Dispatcher
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
name|ChunkManager
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
name|ContainerDispatcher
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
name|ContainerManager
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
name|transport
operator|.
name|server
operator|.
name|XceiverServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_comment
comment|/**  * Ozone main class sets up the network server and initializes the container  * layer.  */
end_comment

begin_class
DECL|class|OzoneContainer
specifier|public
class|class
name|OzoneContainer
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OzoneContainer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ozoneConfig
specifier|private
specifier|final
name|Configuration
name|ozoneConfig
decl_stmt|;
DECL|field|dataSet
specifier|private
specifier|final
name|FsDatasetSpi
name|dataSet
decl_stmt|;
DECL|field|dispatcher
specifier|private
specifier|final
name|ContainerDispatcher
name|dispatcher
decl_stmt|;
DECL|field|manager
specifier|private
specifier|final
name|ContainerManager
name|manager
decl_stmt|;
DECL|field|server
specifier|private
specifier|final
name|XceiverServer
name|server
decl_stmt|;
DECL|field|chunkManager
specifier|private
specifier|final
name|ChunkManager
name|chunkManager
decl_stmt|;
comment|/**    * Creates a network endpoint and enables Ozone container.    *    * @param ozoneConfig - Config    * @param dataSet     - FsDataset.    * @throws IOException    */
DECL|method|OzoneContainer (Configuration ozoneConfig, FsDatasetSpi dataSet)
specifier|public
name|OzoneContainer
parameter_list|(
name|Configuration
name|ozoneConfig
parameter_list|,
name|FsDatasetSpi
name|dataSet
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Path
argument_list|>
name|locations
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
index|[]
name|paths
init|=
name|ozoneConfig
operator|.
name|getStrings
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_OZONE_METADATA_DIRS
argument_list|)
decl_stmt|;
if|if
condition|(
name|paths
operator|!=
literal|null
operator|&&
name|paths
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|p
range|:
name|paths
control|)
block|{
name|locations
operator|.
name|add
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|getDataDir
argument_list|(
name|dataSet
argument_list|,
name|locations
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|ozoneConfig
operator|=
name|ozoneConfig
expr_stmt|;
name|this
operator|.
name|dataSet
operator|=
name|dataSet
expr_stmt|;
name|manager
operator|=
operator|new
name|ContainerManagerImpl
argument_list|()
expr_stmt|;
name|manager
operator|.
name|init
argument_list|(
name|this
operator|.
name|ozoneConfig
argument_list|,
name|locations
argument_list|,
name|this
operator|.
name|dataSet
argument_list|)
expr_stmt|;
name|this
operator|.
name|chunkManager
operator|=
operator|new
name|ChunkManagerImpl
argument_list|(
name|manager
argument_list|)
expr_stmt|;
name|manager
operator|.
name|setChunkManager
argument_list|(
name|this
operator|.
name|chunkManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|dispatcher
operator|=
operator|new
name|Dispatcher
argument_list|(
name|manager
argument_list|)
expr_stmt|;
name|server
operator|=
operator|new
name|XceiverServer
argument_list|(
name|this
operator|.
name|ozoneConfig
argument_list|,
name|this
operator|.
name|dispatcher
argument_list|)
expr_stmt|;
block|}
comment|/**    * Starts serving requests to ozone container.    * @throws Exception    */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * Stops the ozone container.    * @throws Exception    */
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns a paths to data dirs.    * @param dataset - FSDataset.    * @param pathList - List of paths.    * @throws IOException    */
DECL|method|getDataDir (FsDatasetSpi dataset, List<Path> pathList)
specifier|private
name|void
name|getDataDir
parameter_list|(
name|FsDatasetSpi
name|dataset
parameter_list|,
name|List
argument_list|<
name|Path
argument_list|>
name|pathList
parameter_list|)
throws|throws
name|IOException
block|{
name|FsDatasetSpi
operator|.
name|FsVolumeReferences
name|references
decl_stmt|;
try|try
block|{
synchronized|synchronized
init|(
name|dataset
init|)
block|{
name|references
operator|=
name|dataset
operator|.
name|getFsVolumeReferences
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|ndx
init|=
literal|0
init|;
name|ndx
operator|<
name|references
operator|.
name|size
argument_list|()
condition|;
name|ndx
operator|++
control|)
block|{
name|FsVolumeSpi
name|vol
init|=
name|references
operator|.
name|get
argument_list|(
name|ndx
argument_list|)
decl_stmt|;
name|pathList
operator|.
name|add
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|vol
operator|.
name|getBasePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|references
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to get volume paths."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Internal error"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

