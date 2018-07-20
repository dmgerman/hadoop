begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Optional
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|DFSConfigKeys
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
name|conf
operator|.
name|OzoneConfiguration
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
name|ipc
operator|.
name|Client
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
name|ipc
operator|.
name|RPC
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
name|metrics2
operator|.
name|lib
operator|.
name|DefaultMetricsSystem
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
name|net
operator|.
name|NetUtils
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
name|client
operator|.
name|OzoneClient
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
name|client
operator|.
name|OzoneClientFactory
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
name|client
operator|.
name|rest
operator|.
name|OzoneException
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
name|om
operator|.
name|OMConfigKeys
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
name|om
operator|.
name|OzoneManager
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
name|scm
operator|.
name|server
operator|.
name|SCMStorage
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
name|om
operator|.
name|OMStorage
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
name|scm
operator|.
name|ScmConfigKeys
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
name|scm
operator|.
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
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
name|scm
operator|.
name|protocolPB
operator|.
name|StorageContainerLocationProtocolPB
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
name|scm
operator|.
name|server
operator|.
name|StorageContainerManager
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
name|security
operator|.
name|UserGroupInformation
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
name|test
operator|.
name|GenericTestUtils
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
name|org
operator|.
name|slf4j
operator|.
name|event
operator|.
name|Level
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
name|net
operator|.
name|InetSocketAddress
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
name|Files
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
name|UUID
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
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
name|hdds
operator|.
name|HddsConfigKeys
operator|.
name|HDDS_HEARTBEAT_INTERVAL
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|NodeState
operator|.
name|HEALTHY
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|HDDS_DATANODE_PLUGINS_KEY
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_PORT
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_RANDOM_PORT
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_IPC_PORT
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_IPC_RANDOM_PORT
import|;
end_import

begin_comment
comment|/**  * MiniOzoneCluster creates a complete in-process Ozone cluster suitable for  * running tests.  The cluster consists of a OzoneManager,  * StorageContainerManager and multiple DataNodes.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|MiniOzoneClusterImpl
specifier|public
specifier|final
class|class
name|MiniOzoneClusterImpl
implements|implements
name|MiniOzoneCluster
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
name|MiniOzoneClusterImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|scm
specifier|private
specifier|final
name|StorageContainerManager
name|scm
decl_stmt|;
DECL|field|ozoneManager
specifier|private
specifier|final
name|OzoneManager
name|ozoneManager
decl_stmt|;
DECL|field|hddsDatanodes
specifier|private
specifier|final
name|List
argument_list|<
name|HddsDatanodeService
argument_list|>
name|hddsDatanodes
decl_stmt|;
comment|/**    * Creates a new MiniOzoneCluster.    *    * @throws IOException if there is an I/O error    */
DECL|method|MiniOzoneClusterImpl (OzoneConfiguration conf, OzoneManager ozoneManager, StorageContainerManager scm, List<HddsDatanodeService> hddsDatanodes)
specifier|private
name|MiniOzoneClusterImpl
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|OzoneManager
name|ozoneManager
parameter_list|,
name|StorageContainerManager
name|scm
parameter_list|,
name|List
argument_list|<
name|HddsDatanodeService
argument_list|>
name|hddsDatanodes
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|ozoneManager
operator|=
name|ozoneManager
expr_stmt|;
name|this
operator|.
name|scm
operator|=
name|scm
expr_stmt|;
name|this
operator|.
name|hddsDatanodes
operator|=
name|hddsDatanodes
expr_stmt|;
block|}
DECL|method|getConf ()
specifier|public
name|OzoneConfiguration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
comment|/**    * Waits for the Ozone cluster to be ready for processing requests.    */
annotation|@
name|Override
DECL|method|waitForClusterToBeReady ()
specifier|public
name|void
name|waitForClusterToBeReady
parameter_list|()
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
specifier|final
name|int
name|healthy
init|=
name|scm
operator|.
name|getNodeCount
argument_list|(
name|HEALTHY
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|isReady
init|=
name|healthy
operator|==
name|hddsDatanodes
operator|.
name|size
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"{}. Got {} of {} DN Heartbeats."
argument_list|,
name|isReady
condition|?
literal|"Cluster is ready"
else|:
literal|"Waiting for cluster to be ready"
argument_list|,
name|healthy
argument_list|,
name|hddsDatanodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|isReady
return|;
block|}
argument_list|,
literal|1000
argument_list|,
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|//wait for 1 min.
block|}
comment|/**    * Waits for SCM to be out of Chill Mode. Many tests can be run iff we are out    * of Chill mode.    *    * @throws TimeoutException    * @throws InterruptedException    */
annotation|@
name|Override
DECL|method|waitTobeOutOfChillMode ()
specifier|public
name|void
name|waitTobeOutOfChillMode
parameter_list|()
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
if|if
condition|(
name|scm
operator|.
name|getScmNodeManager
argument_list|()
operator|.
name|isOutOfChillMode
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for cluster to be ready. No datanodes found"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
argument_list|,
literal|100
argument_list|,
literal|45000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStorageContainerManager ()
specifier|public
name|StorageContainerManager
name|getStorageContainerManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|scm
return|;
block|}
annotation|@
name|Override
DECL|method|getOzoneManager ()
specifier|public
name|OzoneManager
name|getOzoneManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|ozoneManager
return|;
block|}
annotation|@
name|Override
DECL|method|getHddsDatanodes ()
specifier|public
name|List
argument_list|<
name|HddsDatanodeService
argument_list|>
name|getHddsDatanodes
parameter_list|()
block|{
return|return
name|hddsDatanodes
return|;
block|}
annotation|@
name|Override
DECL|method|getClient ()
specifier|public
name|OzoneClient
name|getClient
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|OzoneClientFactory
operator|.
name|getClient
argument_list|(
name|conf
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRpcClient ()
specifier|public
name|OzoneClient
name|getRpcClient
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|OzoneClientFactory
operator|.
name|getRpcClient
argument_list|(
name|conf
argument_list|)
return|;
block|}
comment|/**    * Creates an {@link OzoneClient} connected to this cluster's REST    * service. Callers take ownership of the client and must close it when done.    *    * @return OzoneRestClient connected to this cluster's REST service    * @throws OzoneException if Ozone encounters an error creating the client    */
annotation|@
name|Override
DECL|method|getRestClient ()
specifier|public
name|OzoneClient
name|getRestClient
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|OzoneClientFactory
operator|.
name|getRestClient
argument_list|(
name|conf
argument_list|)
return|;
block|}
comment|/**    * Returns an RPC proxy connected to this cluster's StorageContainerManager    * for accessing container location information.  Callers take ownership of    * the proxy and must close it when done.    *    * @return RPC proxy for accessing container location information    * @throws IOException if there is an I/O error    */
annotation|@
name|Override
specifier|public
name|StorageContainerLocationProtocolClientSideTranslatorPB
DECL|method|getStorageContainerLocationClient ()
name|getStorageContainerLocationClient
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|version
init|=
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|StorageContainerLocationProtocolPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|address
init|=
name|scm
operator|.
name|getClientRpcAddress
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating StorageContainerLocationProtocol RPC client with address {}"
argument_list|,
name|address
argument_list|)
expr_stmt|;
return|return
operator|new
name|StorageContainerLocationProtocolClientSideTranslatorPB
argument_list|(
name|RPC
operator|.
name|getProxy
argument_list|(
name|StorageContainerLocationProtocolPB
operator|.
name|class
argument_list|,
name|version
argument_list|,
name|address
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
name|conf
argument_list|,
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|conf
argument_list|)
argument_list|,
name|Client
operator|.
name|getRpcTimeout
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|restartStorageContainerManager ()
specifier|public
name|void
name|restartStorageContainerManager
parameter_list|()
throws|throws
name|IOException
block|{
name|scm
operator|.
name|stop
argument_list|()
expr_stmt|;
name|scm
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|restartOzoneManager ()
specifier|public
name|void
name|restartOzoneManager
parameter_list|()
throws|throws
name|IOException
block|{
name|ozoneManager
operator|.
name|stop
argument_list|()
expr_stmt|;
name|ozoneManager
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|restartHddsDatanode (int i)
specifier|public
name|void
name|restartHddsDatanode
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|HddsDatanodeService
name|datanodeService
init|=
name|hddsDatanodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|datanodeService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|datanodeService
operator|.
name|join
argument_list|()
expr_stmt|;
comment|// ensure same ports are used across restarts.
name|Configuration
name|conf
init|=
name|datanodeService
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|int
name|currentPort
init|=
name|datanodeService
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|getPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|STANDALONE
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_CONTAINER_IPC_PORT
argument_list|,
name|currentPort
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFS_CONTAINER_IPC_RANDOM_PORT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|int
name|ratisPort
init|=
name|datanodeService
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|getPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|RATIS
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_CONTAINER_RATIS_IPC_PORT
argument_list|,
name|ratisPort
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFS_CONTAINER_RATIS_IPC_RANDOM_PORT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|datanodeService
operator|.
name|start
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|shutdownHddsDatanode (int i)
specifier|public
name|void
name|shutdownHddsDatanode
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|hddsDatanodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down the Mini Ozone Cluster"
argument_list|)
expr_stmt|;
name|File
name|baseDir
init|=
operator|new
name|File
argument_list|(
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|MiniOzoneClusterImpl
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"-"
operator|+
name|scm
operator|.
name|getClientProtocolServer
argument_list|()
operator|.
name|getScmInfo
argument_list|()
operator|.
name|getClusterId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|baseDir
argument_list|)
expr_stmt|;
if|if
condition|(
name|ozoneManager
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down the OzoneManager"
argument_list|)
expr_stmt|;
name|ozoneManager
operator|.
name|stop
argument_list|()
expr_stmt|;
name|ozoneManager
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|scm
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down the StorageContainerManager"
argument_list|)
expr_stmt|;
name|scm
operator|.
name|stop
argument_list|()
expr_stmt|;
name|scm
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|hddsDatanodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down the HddsDatanodes"
argument_list|)
expr_stmt|;
for|for
control|(
name|HddsDatanodeService
name|hddsDatanode
range|:
name|hddsDatanodes
control|)
block|{
name|hddsDatanode
operator|.
name|stop
argument_list|()
expr_stmt|;
name|hddsDatanode
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while shutting down the cluster."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Builder for configuring the MiniOzoneCluster to run.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|MiniOzoneCluster
operator|.
name|Builder
block|{
comment|/**      * Creates a new Builder.      *      * @param conf configuration      */
DECL|method|Builder (OzoneConfiguration conf)
specifier|public
name|Builder
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build ()
specifier|public
name|MiniOzoneCluster
name|build
parameter_list|()
throws|throws
name|IOException
block|{
name|DefaultMetricsSystem
operator|.
name|setMiniClusterMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|initializeConfiguration
argument_list|()
expr_stmt|;
name|StorageContainerManager
name|scm
init|=
name|createSCM
argument_list|()
decl_stmt|;
name|scm
operator|.
name|start
argument_list|()
expr_stmt|;
name|OzoneManager
name|om
init|=
name|createOM
argument_list|()
decl_stmt|;
name|om
operator|.
name|start
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|HddsDatanodeService
argument_list|>
name|hddsDatanodes
init|=
name|createHddsDatanodes
argument_list|(
name|scm
argument_list|)
decl_stmt|;
name|hddsDatanodes
operator|.
name|forEach
argument_list|(
parameter_list|(
name|datanode
parameter_list|)
lambda|->
name|datanode
operator|.
name|start
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|MiniOzoneClusterImpl
argument_list|(
name|conf
argument_list|,
name|om
argument_list|,
name|scm
argument_list|,
name|hddsDatanodes
argument_list|)
return|;
block|}
comment|/**      * Initializes the configureation required for starting MiniOzoneCluster.      *      * @throws IOException      */
DECL|method|initializeConfiguration ()
specifier|private
name|void
name|initializeConfiguration
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_ENABLED
argument_list|,
name|ozoneEnabled
argument_list|)
expr_stmt|;
name|Path
name|metaDir
init|=
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|,
literal|"ozone-meta"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|metaDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|metaDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|configureTrace
argument_list|()
expr_stmt|;
block|}
comment|/**      * Creates a new StorageContainerManager instance.      *      * @return {@link StorageContainerManager}      *      * @throws IOException      */
DECL|method|createSCM ()
specifier|private
name|StorageContainerManager
name|createSCM
parameter_list|()
throws|throws
name|IOException
block|{
name|configureSCM
argument_list|()
expr_stmt|;
name|SCMStorage
name|scmStore
init|=
operator|new
name|SCMStorage
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|scmStore
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|scmId
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|scmId
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|scmStore
operator|.
name|setScmId
argument_list|(
name|scmId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|scmStore
operator|.
name|initialize
argument_list|()
expr_stmt|;
return|return
name|StorageContainerManager
operator|.
name|createSCM
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/**      * Creates a new OzoneManager instance.      *      * @return {@link OzoneManager}      *      * @throws IOException      */
DECL|method|createOM ()
specifier|private
name|OzoneManager
name|createOM
parameter_list|()
throws|throws
name|IOException
block|{
name|configureOM
argument_list|()
expr_stmt|;
name|OMStorage
name|omStore
init|=
operator|new
name|OMStorage
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|omStore
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
expr_stmt|;
name|omStore
operator|.
name|setScmId
argument_list|(
name|scmId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|omStore
operator|.
name|setOmId
argument_list|(
name|omId
operator|.
name|orElse
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|omStore
operator|.
name|initialize
argument_list|()
expr_stmt|;
return|return
name|OzoneManager
operator|.
name|createOm
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/**      * Creates HddsDatanodeService(s) instance.      *      * @return List of HddsDatanodeService      *      * @throws IOException      */
DECL|method|createHddsDatanodes ( StorageContainerManager scm)
specifier|private
name|List
argument_list|<
name|HddsDatanodeService
argument_list|>
name|createHddsDatanodes
parameter_list|(
name|StorageContainerManager
name|scm
parameter_list|)
throws|throws
name|IOException
block|{
name|configureHddsDatanodes
argument_list|()
expr_stmt|;
name|String
name|scmAddress
init|=
name|scm
operator|.
name|getDatanodeRpcAddress
argument_list|()
operator|.
name|getHostString
argument_list|()
operator|+
literal|":"
operator|+
name|scm
operator|.
name|getDatanodeRpcAddress
argument_list|()
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setStrings
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_NAMES
argument_list|,
name|scmAddress
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|HddsDatanodeService
argument_list|>
name|hddsDatanodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numOfDatanodes
condition|;
name|i
operator|++
control|)
block|{
name|Configuration
name|dnConf
init|=
operator|new
name|OzoneConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|datanodeBaseDir
init|=
name|path
operator|+
literal|"/datanode-"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Path
name|metaDir
init|=
name|Paths
operator|.
name|get
argument_list|(
name|datanodeBaseDir
argument_list|,
literal|"meta"
argument_list|)
decl_stmt|;
name|Path
name|dataDir
init|=
name|Paths
operator|.
name|get
argument_list|(
name|datanodeBaseDir
argument_list|,
literal|"data"
argument_list|,
literal|"containers"
argument_list|)
decl_stmt|;
name|Path
name|ratisDir
init|=
name|Paths
operator|.
name|get
argument_list|(
name|datanodeBaseDir
argument_list|,
literal|"data"
argument_list|,
literal|"ratis"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|metaDir
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|dataDir
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|ratisDir
argument_list|)
expr_stmt|;
name|dnConf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|metaDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|dnConf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|dataDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|dnConf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_DATANODE_STORAGE_DIR
argument_list|,
name|ratisDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|hddsDatanodes
operator|.
name|add
argument_list|(
name|HddsDatanodeService
operator|.
name|createHddsDatanodeService
argument_list|(
name|dnConf
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|hddsDatanodes
return|;
block|}
DECL|method|configureSCM ()
specifier|private
name|void
name|configureSCM
parameter_list|()
block|{
name|conf
operator|.
name|set
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_BLOCK_CLIENT_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DATANODE_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HTTP_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HANDLER_COUNT_KEY
argument_list|,
name|numOfScmHandlers
argument_list|)
expr_stmt|;
name|configureSCMheartbeat
argument_list|()
expr_stmt|;
block|}
DECL|method|configureSCMheartbeat ()
specifier|private
name|void
name|configureSCMheartbeat
parameter_list|()
block|{
if|if
condition|(
name|hbInterval
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|HDDS_HEARTBEAT_INTERVAL
argument_list|,
name|hbInterval
operator|.
name|get
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|HDDS_HEARTBEAT_INTERVAL
argument_list|,
name|DEFAULT_HB_INTERVAL_MS
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hbProcessorInterval
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL
argument_list|,
name|hbProcessorInterval
operator|.
name|get
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL
argument_list|,
name|DEFAULT_HB_PROCESSOR_INTERVAL_MS
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|configureOM ()
specifier|private
name|void
name|configureOM
parameter_list|()
block|{
name|conf
operator|.
name|set
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_HTTP_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_HANDLER_COUNT_KEY
argument_list|,
name|numOfOmHandlers
argument_list|)
expr_stmt|;
block|}
DECL|method|configureHddsDatanodes ()
specifier|private
name|void
name|configureHddsDatanodes
parameter_list|()
block|{
name|conf
operator|.
name|set
argument_list|(
name|ScmConfigKeys
operator|.
name|HDDS_REST_HTTP_ADDRESS_KEY
argument_list|,
literal|"0.0.0.0:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HDDS_DATANODE_PLUGINS_KEY
argument_list|,
literal|"org.apache.hadoop.ozone.web.OzoneHddsDatanodeService"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_RANDOM_PORT
argument_list|,
name|randomContainerPort
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_IPC_RANDOM_PORT
argument_list|,
name|randomContainerPort
argument_list|)
expr_stmt|;
block|}
DECL|method|configureTrace ()
specifier|private
name|void
name|configureTrace
parameter_list|()
block|{
if|if
condition|(
name|enableTrace
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_TRACE_ENABLED_KEY
argument_list|,
name|enableTrace
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setRootLogLevel
argument_list|(
name|Level
operator|.
name|TRACE
argument_list|)
expr_stmt|;
block|}
name|GenericTestUtils
operator|.
name|setRootLogLevel
argument_list|(
name|Level
operator|.
name|INFO
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

