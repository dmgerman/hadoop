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
name|Optional
import|;
end_import

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
name|hdfs
operator|.
name|MiniDFSCluster
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
name|container
operator|.
name|ContainerTestHelper
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
name|ksm
operator|.
name|KSMConfigKeys
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
name|ksm
operator|.
name|KeySpaceManager
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
name|web
operator|.
name|client
operator|.
name|OzoneRestClient
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
name|ozone
operator|.
name|scm
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
name|ozone
operator|.
name|scm
operator|.
name|node
operator|.
name|SCMNodeManager
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
name|web
operator|.
name|exceptions
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
name|apache
operator|.
name|log4j
operator|.
name|Level
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
name|Closeable
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
name|net
operator|.
name|URISyntaxException
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
name|Random
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
name|TimeoutException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_comment
comment|/**  * MiniOzoneCluster creates a complete in-process Ozone cluster suitable for  * running tests.  The cluster consists of a StorageContainerManager, Namenode  * and multiple DataNodes.  This class subclasses {@link MiniDFSCluster} for  * convenient reuse of logic for starting DataNodes.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|MiniOzoneCluster
specifier|public
specifier|final
class|class
name|MiniOzoneCluster
extends|extends
name|MiniDFSCluster
implements|implements
name|Closeable
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
name|MiniOzoneCluster
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|USER_AUTH
specifier|private
specifier|static
specifier|final
name|String
name|USER_AUTH
init|=
literal|"hdfs"
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
DECL|field|ksm
specifier|private
specifier|final
name|KeySpaceManager
name|ksm
decl_stmt|;
DECL|field|tempPath
specifier|private
specifier|final
name|Path
name|tempPath
decl_stmt|;
comment|/**    * Creates a new MiniOzoneCluster.    *    * @param builder cluster builder    * @param scm     StorageContainerManager, already running    * @throws IOException if there is an I/O error    */
DECL|method|MiniOzoneCluster (Builder builder, StorageContainerManager scm, KeySpaceManager ksm)
specifier|private
name|MiniOzoneCluster
parameter_list|(
name|Builder
name|builder
parameter_list|,
name|StorageContainerManager
name|scm
parameter_list|,
name|KeySpaceManager
name|ksm
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|builder
operator|.
name|conf
expr_stmt|;
name|this
operator|.
name|scm
operator|=
name|scm
expr_stmt|;
name|this
operator|.
name|ksm
operator|=
name|ksm
expr_stmt|;
name|tempPath
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|builder
operator|.
name|getPath
argument_list|()
argument_list|,
name|builder
operator|.
name|getRunID
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setupDatanodeAddress ( int i, Configuration dnConf, boolean setupHostsFile, boolean checkDnAddrConf)
specifier|protected
name|void
name|setupDatanodeAddress
parameter_list|(
name|int
name|i
parameter_list|,
name|Configuration
name|dnConf
parameter_list|,
name|boolean
name|setupHostsFile
parameter_list|,
name|boolean
name|checkDnAddrConf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|setupDatanodeAddress
argument_list|(
name|i
argument_list|,
name|dnConf
argument_list|,
name|setupHostsFile
argument_list|,
name|checkDnAddrConf
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|useRatis
init|=
name|dnConf
operator|.
name|getBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_ENABLED_KEY
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_ENABLED_DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|useRatis
condition|)
block|{
return|return;
block|}
specifier|final
name|String
name|address
init|=
name|ContainerTestHelper
operator|.
name|createLocalAddress
argument_list|()
decl_stmt|;
name|setConf
argument_list|(
name|i
argument_list|,
name|dnConf
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_SERVER_ID
argument_list|,
name|address
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|i
argument_list|,
name|dnConf
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_PORT
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|address
argument_list|)
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|i
argument_list|,
name|dnConf
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_DATANODE_STORAGE_DIR
argument_list|,
name|getInstanceStorageDir
argument_list|(
name|i
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|containerMetaDirs
init|=
name|dnConf
operator|.
name|get
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_CONTAINER_METADATA_DIRS
argument_list|)
operator|+
literal|"-dn-"
operator|+
name|i
decl_stmt|;
name|Path
name|containerMetaDirPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|containerMetaDirs
argument_list|)
decl_stmt|;
name|setConf
argument_list|(
name|i
argument_list|,
name|dnConf
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_CONTAINER_METADATA_DIRS
argument_list|,
name|containerMetaDirs
argument_list|)
expr_stmt|;
name|Path
name|containerRootPath
init|=
name|containerMetaDirPath
operator|.
name|resolve
argument_list|(
name|OzoneConsts
operator|.
name|CONTAINER_ROOT_PREFIX
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|containerRootPath
argument_list|)
expr_stmt|;
block|}
DECL|method|setConf (int i, Configuration conf, String key, String value)
specifier|static
name|void
name|setConf
parameter_list|(
name|int
name|i
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"dn{}: set {} = {}"
argument_list|,
name|i
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|tempPath
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|String
name|errorMessage
init|=
literal|"Cleaning up metadata directories failed."
operator|+
name|e
decl_stmt|;
name|assertFalse
argument_list|(
name|errorMessage
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|String
name|localStorage
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT_DEFAULT
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|localStorage
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"Cleaning up local storage failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|super
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down the Mini Ozone Cluster"
argument_list|)
expr_stmt|;
if|if
condition|(
name|ksm
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down the keySpaceManager"
argument_list|)
expr_stmt|;
name|ksm
operator|.
name|stop
argument_list|()
expr_stmt|;
name|ksm
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
block|}
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
DECL|method|getKeySpaceManager ()
specifier|public
name|KeySpaceManager
name|getKeySpaceManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|ksm
return|;
block|}
comment|/**    * Creates an {@link OzoneRestClient} connected to this cluster's REST    * service. Callers take ownership of the client and must close it when done.    *    * @return OzoneRestClient connected to this cluster's REST service    * @throws OzoneException if Ozone encounters an error creating the client    */
DECL|method|createOzoneRestClient ()
specifier|public
name|OzoneRestClient
name|createOzoneRestClient
parameter_list|()
throws|throws
name|OzoneException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|getDataNodes
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"Cannot create OzoneRestClient if the cluster has no DataNodes."
argument_list|)
expr_stmt|;
comment|// An Ozone request may originate at any DataNode, so pick one at random.
name|int
name|dnIndex
init|=
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|getDataNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|uri
init|=
name|String
operator|.
name|format
argument_list|(
literal|"http://127.0.0.1:%d"
argument_list|,
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|dnIndex
argument_list|)
operator|.
name|getInfoPort
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating Ozone client to DataNode {} with URI {} and user {}"
argument_list|,
name|dnIndex
argument_list|,
name|uri
argument_list|,
name|USER_AUTH
argument_list|)
expr_stmt|;
try|try
block|{
return|return
operator|new
name|OzoneRestClient
argument_list|(
name|uri
argument_list|,
name|USER_AUTH
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
comment|// We control the REST service URI, so it should never be invalid.
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unexpected URISyntaxException"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Creates an RPC proxy connected to this cluster's StorageContainerManager    * for accessing container location information.  Callers take ownership of    * the proxy and must close it when done.    *    * @return RPC proxy for accessing container location information    * @throws IOException if there is an I/O error    */
specifier|public
name|StorageContainerLocationProtocolClientSideTranslatorPB
DECL|method|createStorageContainerLocationClient ()
name|createStorageContainerLocationClient
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
comment|/**    * Waits for the Ozone cluster to be ready for processing requests.    */
DECL|method|waitOzoneReady ()
specifier|public
name|void
name|waitOzoneReady
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
name|SCMNodeManager
operator|.
name|NODESTATE
operator|.
name|HEALTHY
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|isReady
init|=
name|healthy
operator|>=
name|numDataNodes
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
name|numDataNodes
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
name|isOutOfNodeChillMode
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
DECL|method|waitForHeartbeatProcessed ()
specifier|public
name|void
name|waitForHeartbeatProcessed
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
name|scm
operator|.
name|getScmNodeManager
argument_list|()
operator|.
name|waitForHeartbeatProcessed
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|4
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|scm
operator|.
name|getScmNodeManager
argument_list|()
operator|.
name|getStats
argument_list|()
operator|.
name|getCapacity
argument_list|()
operator|.
name|get
argument_list|()
operator|>
literal|0
argument_list|,
literal|100
argument_list|,
literal|4
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builder for configuring the MiniOzoneCluster to run.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|MiniDFSCluster
operator|.
name|Builder
block|{
DECL|field|conf
specifier|private
specifier|final
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|DEFAULT_HB_SECONDS
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_HB_SECONDS
init|=
literal|1
decl_stmt|;
DECL|field|DEFAULT_PROCESSOR_MS
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_PROCESSOR_MS
init|=
literal|100
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|runID
specifier|private
specifier|final
name|UUID
name|runID
decl_stmt|;
DECL|field|ozoneHandlerType
specifier|private
name|Optional
argument_list|<
name|String
argument_list|>
name|ozoneHandlerType
init|=
name|java
operator|.
name|util
operator|.
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
DECL|field|enableTrace
specifier|private
name|Optional
argument_list|<
name|Boolean
argument_list|>
name|enableTrace
init|=
name|Optional
operator|.
name|of
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|hbSeconds
specifier|private
name|Optional
argument_list|<
name|Integer
argument_list|>
name|hbSeconds
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
DECL|field|hbProcessorInterval
specifier|private
name|Optional
argument_list|<
name|Integer
argument_list|>
name|hbProcessorInterval
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
DECL|field|scmMetadataDir
specifier|private
name|Optional
argument_list|<
name|String
argument_list|>
name|scmMetadataDir
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
DECL|field|ozoneEnabled
specifier|private
name|Boolean
name|ozoneEnabled
init|=
literal|true
decl_stmt|;
DECL|field|waitForChillModeFinish
specifier|private
name|Boolean
name|waitForChillModeFinish
init|=
literal|true
decl_stmt|;
DECL|field|randomContainerPort
specifier|private
name|Boolean
name|randomContainerPort
init|=
literal|true
decl_stmt|;
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
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|path
operator|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|MiniOzoneCluster
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|runID
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
expr_stmt|;
block|}
DECL|method|setRandomContainerPort (boolean randomPort)
specifier|public
name|Builder
name|setRandomContainerPort
parameter_list|(
name|boolean
name|randomPort
parameter_list|)
block|{
name|this
operator|.
name|randomContainerPort
operator|=
name|randomPort
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|numDataNodes (int val)
specifier|public
name|Builder
name|numDataNodes
parameter_list|(
name|int
name|val
parameter_list|)
block|{
name|super
operator|.
name|numDataNodes
argument_list|(
name|val
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|storageCapacities (long[] capacities)
specifier|public
name|Builder
name|storageCapacities
parameter_list|(
name|long
index|[]
name|capacities
parameter_list|)
block|{
name|super
operator|.
name|storageCapacities
argument_list|(
name|capacities
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setHandlerType (String handler)
specifier|public
name|Builder
name|setHandlerType
parameter_list|(
name|String
name|handler
parameter_list|)
block|{
name|ozoneHandlerType
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|handler
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setTrace (Boolean trace)
specifier|public
name|Builder
name|setTrace
parameter_list|(
name|Boolean
name|trace
parameter_list|)
block|{
name|enableTrace
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|trace
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setSCMHBInterval (int seconds)
specifier|public
name|Builder
name|setSCMHBInterval
parameter_list|(
name|int
name|seconds
parameter_list|)
block|{
name|hbSeconds
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|seconds
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setSCMHeartbeatProcessingInterval (int milliseconds)
specifier|public
name|Builder
name|setSCMHeartbeatProcessingInterval
parameter_list|(
name|int
name|milliseconds
parameter_list|)
block|{
name|hbProcessorInterval
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|milliseconds
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setSCMMetadataDir (String scmMetadataDirPath)
specifier|public
name|Builder
name|setSCMMetadataDir
parameter_list|(
name|String
name|scmMetadataDirPath
parameter_list|)
block|{
name|scmMetadataDir
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|scmMetadataDirPath
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|disableOzone ()
specifier|public
name|Builder
name|disableOzone
parameter_list|()
block|{
name|ozoneEnabled
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|doNotwaitTobeOutofChillMode ()
specifier|public
name|Builder
name|doNotwaitTobeOutofChillMode
parameter_list|()
block|{
name|waitForChillModeFinish
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getPath ()
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|getRunID ()
specifier|public
name|String
name|getRunID
parameter_list|()
block|{
return|return
name|runID
operator|.
name|toString
argument_list|()
return|;
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
name|configureHandler
argument_list|()
expr_stmt|;
name|configureTrace
argument_list|()
expr_stmt|;
name|configureSCMheartbeat
argument_list|()
expr_stmt|;
name|configScmMetadata
argument_list|()
expr_stmt|;
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
name|set
argument_list|(
name|KSMConfigKeys
operator|.
name|OZONE_KSM_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KSMConfigKeys
operator|.
name|OZONE_KSM_HTTP_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
comment|// Use random ports for ozone containers in mini cluster,
comment|// in order to launch multiple container servers per node.
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
name|StorageContainerManager
name|scm
init|=
operator|new
name|StorageContainerManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|scm
operator|.
name|start
argument_list|()
expr_stmt|;
name|KeySpaceManager
name|ksm
init|=
operator|new
name|KeySpaceManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|ksm
operator|.
name|start
argument_list|()
expr_stmt|;
name|String
name|addressString
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
name|addressString
argument_list|)
expr_stmt|;
name|MiniOzoneCluster
name|cluster
init|=
operator|new
name|MiniOzoneCluster
argument_list|(
name|this
argument_list|,
name|scm
argument_list|,
name|ksm
argument_list|)
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitOzoneReady
argument_list|()
expr_stmt|;
if|if
condition|(
name|waitForChillModeFinish
condition|)
block|{
name|cluster
operator|.
name|waitTobeOutOfChillMode
argument_list|()
expr_stmt|;
block|}
name|cluster
operator|.
name|waitForHeartbeatProcessed
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// A workaround to propagate MiniOzoneCluster failures without
comment|// changing the method signature (which would require cascading
comment|// changes to hundreds of unrelated HDFS tests).
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to start MiniOzoneCluster"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|cluster
return|;
block|}
DECL|method|configScmMetadata ()
specifier|private
name|void
name|configScmMetadata
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|scmMetadataDir
operator|.
name|isPresent
argument_list|()
condition|)
block|{
comment|// if user specifies a path in the test, it is assumed that user takes
comment|// care of creating and cleaning up that directory after the tests.
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_CONTAINER_METADATA_DIRS
argument_list|,
name|scmMetadataDir
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// If user has not specified a path, create a UUID for this miniCluster
comment|// and create SCM under that directory.
name|Path
name|scmPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|,
name|runID
operator|.
name|toString
argument_list|()
argument_list|,
literal|"cont-meta"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|scmPath
argument_list|)
expr_stmt|;
name|Path
name|containerPath
init|=
name|scmPath
operator|.
name|resolve
argument_list|(
name|OzoneConsts
operator|.
name|CONTAINER_ROOT_PREFIX
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|containerPath
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_CONTAINER_METADATA_DIRS
argument_list|,
name|scmPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO : Fix this, we need a more generic mechanism to map
comment|// different datanode ID for different datanodes when we have lots of
comment|// datanodes in the cluster.
name|conf
operator|.
name|setStrings
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DATANODE_ID
argument_list|,
name|scmPath
operator|.
name|toString
argument_list|()
operator|+
literal|"/datanode.id"
argument_list|)
expr_stmt|;
block|}
DECL|method|configureHandler ()
specifier|private
name|void
name|configureHandler
parameter_list|()
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_ENABLED
argument_list|,
name|this
operator|.
name|ozoneEnabled
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|ozoneHandlerType
operator|.
name|isPresent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The Ozone handler type must be specified."
argument_list|)
throw|;
block|}
else|else
block|{
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_HANDLER_TYPE_KEY
argument_list|,
name|ozoneHandlerType
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|setLogLevel
argument_list|(
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
operator|.
name|getRootLogger
argument_list|()
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
operator|.
name|getRootLogger
argument_list|()
argument_list|,
name|Level
operator|.
name|INFO
argument_list|)
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
name|hbSeconds
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HEARTBEAT_INTERVAL_SECONDS
argument_list|,
name|hbSeconds
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HEARTBEAT_INTERVAL_SECONDS
argument_list|,
name|DEFAULT_HB_SECONDS
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
name|setInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL_MS
argument_list|,
name|hbProcessorInterval
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL_MS
argument_list|,
name|DEFAULT_PROCESSOR_MS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

