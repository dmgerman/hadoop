begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|StorageLocation
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
name|impl
operator|.
name|KeyManagerImpl
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
name|interfaces
operator|.
name|KeyManager
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
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
DECL|field|keyManager
specifier|private
specifier|final
name|KeyManager
name|keyManager
decl_stmt|;
comment|/**    * Creates a network endpoint and enables Ozone container.    *    * @param ozoneConfig - Config    * @throws IOException    */
DECL|method|OzoneContainer ( Configuration ozoneConfig)
specifier|public
name|OzoneContainer
parameter_list|(
name|Configuration
name|ozoneConfig
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|ozoneConfig
operator|=
name|ozoneConfig
expr_stmt|;
name|List
argument_list|<
name|StorageLocation
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
name|OZONE_CONTAINER_METADATA_DIRS
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
name|StorageLocation
operator|.
name|parse
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
name|locations
argument_list|)
expr_stmt|;
block|}
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
name|keyManager
operator|=
operator|new
name|KeyManagerImpl
argument_list|(
name|manager
argument_list|,
name|ozoneConfig
argument_list|)
expr_stmt|;
name|manager
operator|.
name|setKeyManager
argument_list|(
name|this
operator|.
name|keyManager
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
argument_list|,
name|this
operator|.
name|ozoneConfig
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
comment|/**    * Starts serving requests to ozone container.    *    * @throws IOException    */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|dispatcher
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
comment|/**    * Stops the ozone container.    *<p>    * Shutdown logic is not very obvious from the following code. if you need to    * modify the logic, please keep these comments in mind. Here is the shutdown    * sequence.    *<p>    * 1. We shutdown the network ports.    *<p>    * 2. Now we need to wait for all requests in-flight to finish.    *<p>    * 3. The container manager lock is a read-write lock with "Fairness"    * enabled.    *<p>    * 4. This means that the waiting threads are served in a "first-come-first    * -served" manner. Please note that this applies to waiting threads only.    *<p>    * 5. Since write locks are exclusive, if we are waiting to get a lock it    * implies that we are waiting for in-flight operations to complete.    *<p>    * 6. if there are other write operations waiting on the reader-writer lock,    * fairness guarantees that they will proceed before the shutdown lock    * request.    *<p>    * 7. Since all operations either take a reader or writer lock of container    * manager, we are guaranteed that we are the last operation since we have    * closed the network port, and we wait until close is successful.    *<p>    * 8. We take the writer lock and call shutdown on each of the managers in    * reverse order. That is chunkManager, keyManager and containerManager is    * shutdown.    */
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempting to stop container services."
argument_list|)
expr_stmt|;
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
name|dispatcher
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|manager
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|this
operator|.
name|chunkManager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|this
operator|.
name|keyManager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|this
operator|.
name|manager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"container services shutdown complete."
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|this
operator|.
name|manager
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns a paths to data dirs.    *    * @param pathList - List of paths.    * @throws IOException    */
DECL|method|getDataDir (List<StorageLocation> pathList)
specifier|private
name|void
name|getDataDir
parameter_list|(
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|pathList
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|dir
range|:
name|ozoneConfig
operator|.
name|getStrings
argument_list|(
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|)
control|)
block|{
name|StorageLocation
name|location
init|=
name|StorageLocation
operator|.
name|parse
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|pathList
operator|.
name|add
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

