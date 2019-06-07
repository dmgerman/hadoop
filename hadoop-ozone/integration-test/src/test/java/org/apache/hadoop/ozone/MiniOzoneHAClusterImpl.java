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
name|security
operator|.
name|authentication
operator|.
name|client
operator|.
name|AuthenticationException
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
name|net
operator|.
name|BindException
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
name|HashMap
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
name|Map
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
name|concurrent
operator|.
name|TimeUnit
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
name|OZONE_METADATA_DIRS
import|;
end_import

begin_comment
comment|/**  * MiniOzoneHAClusterImpl creates a complete in-process Ozone cluster  * with OM HA suitable for running tests.  The cluster consists of a set of  * OzoneManagers, StorageContainerManager and multiple DataNodes.  */
end_comment

begin_class
DECL|class|MiniOzoneHAClusterImpl
specifier|public
specifier|final
class|class
name|MiniOzoneHAClusterImpl
extends|extends
name|MiniOzoneClusterImpl
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
name|MiniOzoneHAClusterImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ozoneManagerMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|OzoneManager
argument_list|>
name|ozoneManagerMap
decl_stmt|;
DECL|field|ozoneManagers
specifier|private
name|List
argument_list|<
name|OzoneManager
argument_list|>
name|ozoneManagers
decl_stmt|;
DECL|field|RANDOM
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|RATIS_LEADER_ELECTION_TIMEOUT
specifier|private
specifier|static
specifier|final
name|int
name|RATIS_LEADER_ELECTION_TIMEOUT
init|=
literal|1000
decl_stmt|;
comment|// 1 seconds
DECL|field|NODE_FAILURE_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|NODE_FAILURE_TIMEOUT
init|=
literal|2000
decl_stmt|;
comment|// 2 seconds
comment|/**    * Creates a new MiniOzoneCluster with OM HA.    *    * @throws IOException if there is an I/O error    */
DECL|method|MiniOzoneHAClusterImpl ( OzoneConfiguration conf, Map<String, OzoneManager> omMap, StorageContainerManager scm, List<HddsDatanodeService> hddsDatanodes)
specifier|private
name|MiniOzoneHAClusterImpl
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|OzoneManager
argument_list|>
name|omMap
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
name|super
argument_list|(
name|conf
argument_list|,
name|scm
argument_list|,
name|hddsDatanodes
argument_list|)
expr_stmt|;
name|this
operator|.
name|ozoneManagerMap
operator|=
name|omMap
expr_stmt|;
name|this
operator|.
name|ozoneManagers
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|omMap
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the first OzoneManager from the list.    * @return    */
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
name|ozoneManagers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|getOzoneManager (int index)
specifier|public
name|OzoneManager
name|getOzoneManager
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|this
operator|.
name|ozoneManagers
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
DECL|method|getOzoneManager (String omNodeId)
specifier|public
name|OzoneManager
name|getOzoneManager
parameter_list|(
name|String
name|omNodeId
parameter_list|)
block|{
return|return
name|this
operator|.
name|ozoneManagerMap
operator|.
name|get
argument_list|(
name|omNodeId
argument_list|)
return|;
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
for|for
control|(
name|OzoneManager
name|ozoneManager
range|:
name|ozoneManagers
control|)
block|{
name|ozoneManager
operator|.
name|stop
argument_list|()
expr_stmt|;
name|ozoneManager
operator|.
name|restart
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
for|for
control|(
name|OzoneManager
name|ozoneManager
range|:
name|ozoneManagers
control|)
block|{
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
literal|"Stopping the OzoneManager "
operator|+
name|ozoneManager
operator|.
name|getOMNodId
argument_list|()
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
block|}
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|stopOzoneManager (int index)
specifier|public
name|void
name|stopOzoneManager
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|ozoneManagers
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|stopOzoneManager (String omNodeId)
specifier|public
name|void
name|stopOzoneManager
parameter_list|(
name|String
name|omNodeId
parameter_list|)
block|{
name|ozoneManagerMap
operator|.
name|get
argument_list|(
name|omNodeId
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Builder for configuring the MiniOzoneCluster to run.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|MiniOzoneClusterImpl
operator|.
name|Builder
block|{
DECL|field|nodeIdBaseStr
specifier|private
specifier|final
name|String
name|nodeIdBaseStr
init|=
literal|"omNode-"
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
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|OzoneManager
argument_list|>
name|omMap
decl_stmt|;
try|try
block|{
name|scm
operator|=
name|createSCM
argument_list|()
expr_stmt|;
name|scm
operator|.
name|start
argument_list|()
expr_stmt|;
name|omMap
operator|=
name|createOMService
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to build MiniOzoneCluster. "
argument_list|,
name|ex
argument_list|)
throw|;
block|}
specifier|final
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
name|MiniOzoneHAClusterImpl
name|cluster
init|=
operator|new
name|MiniOzoneHAClusterImpl
argument_list|(
name|conf
argument_list|,
name|omMap
argument_list|,
name|scm
argument_list|,
name|hddsDatanodes
argument_list|)
decl_stmt|;
if|if
condition|(
name|startDataNodes
condition|)
block|{
name|cluster
operator|.
name|startHddsDatanodes
argument_list|()
expr_stmt|;
block|}
return|return
name|cluster
return|;
block|}
comment|/**      * Initialize OM configurations.      * @throws IOException      */
annotation|@
name|Override
DECL|method|initializeConfiguration ()
name|void
name|initializeConfiguration
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|initializeConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_RATIS_ENABLE_KEY
argument_list|,
literal|true
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
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_KEY
argument_list|,
name|RATIS_LEADER_ELECTION_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_RATIS_SERVER_FAILURE_TIMEOUT_DURATION_KEY
argument_list|,
name|NODE_FAILURE_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_RATIS_CLIENT_REQUEST_MAX_RETRIES_KEY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
comment|/**      * Start OM service with multiple OMs.      * @return list of OzoneManagers      * @throws IOException      * @throws AuthenticationException      */
DECL|method|createOMService ()
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|OzoneManager
argument_list|>
name|createOMService
parameter_list|()
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|OzoneManager
argument_list|>
name|omMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|retryCount
init|=
literal|0
decl_stmt|;
name|int
name|basePort
init|=
literal|10000
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|basePort
operator|=
literal|10000
operator|+
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
operator|*
literal|4
expr_stmt|;
name|initHAConfig
argument_list|(
name|basePort
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numOfOMs
condition|;
name|i
operator|++
control|)
block|{
comment|// Set nodeId
name|String
name|nodeId
init|=
name|nodeIdBaseStr
operator|+
name|i
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_NODE_ID_KEY
argument_list|,
name|nodeId
argument_list|)
expr_stmt|;
comment|// Set the OM http(s) address to null so that the cluster picks
comment|// up the address set with service ID and node ID in initHAConfig
name|conf
operator|.
name|set
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_HTTP_ADDRESS_KEY
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_HTTPS_ADDRESS_KEY
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// Set metadata/DB dir base path
name|String
name|metaDirPath
init|=
name|path
operator|+
literal|"/"
operator|+
name|nodeId
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_METADATA_DIRS
argument_list|,
name|metaDirPath
argument_list|)
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
name|initializeOmStorage
argument_list|(
name|omStore
argument_list|)
expr_stmt|;
name|OzoneManager
name|om
init|=
name|OzoneManager
operator|.
name|createOm
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|om
operator|.
name|setCertClient
argument_list|(
name|certClient
argument_list|)
expr_stmt|;
name|omMap
operator|.
name|put
argument_list|(
name|nodeId
argument_list|,
name|om
argument_list|)
expr_stmt|;
name|om
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Started OzoneManager RPC server at "
operator|+
name|om
operator|.
name|getOmRpcServerAddr
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Set default OM address to point to the first OM. Clients would
comment|// try connecting to this address by default
name|conf
operator|.
name|set
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_ADDRESS_KEY
argument_list|,
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|omMap
operator|.
name|get
argument_list|(
name|nodeIdBaseStr
operator|+
literal|1
argument_list|)
operator|.
name|getOmRpcServerAddr
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|BindException
name|e
parameter_list|)
block|{
for|for
control|(
name|OzoneManager
name|om
range|:
name|omMap
operator|.
name|values
argument_list|()
control|)
block|{
name|om
operator|.
name|stop
argument_list|()
expr_stmt|;
name|om
operator|.
name|join
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping OzoneManager server at "
operator|+
name|om
operator|.
name|getOmRpcServerAddr
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|omMap
operator|.
name|clear
argument_list|()
expr_stmt|;
operator|++
name|retryCount
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"MiniOzoneHACluster port conflicts, retried "
operator|+
name|retryCount
operator|+
literal|" times"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|omMap
return|;
block|}
comment|/**      * Initialize HA related configurations.      */
DECL|method|initHAConfig (int basePort)
specifier|private
name|void
name|initHAConfig
parameter_list|(
name|int
name|basePort
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Set configurations required for starting OM HA service
name|conf
operator|.
name|set
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_SERVICE_IDS_KEY
argument_list|,
name|omServiceId
argument_list|)
expr_stmt|;
name|String
name|omNodesKey
init|=
name|OmUtils
operator|.
name|addKeySuffixes
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_NODES_KEY
argument_list|,
name|omServiceId
argument_list|)
decl_stmt|;
name|StringBuilder
name|omNodesKeyValue
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|port
init|=
name|basePort
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numOfOMs
condition|;
name|i
operator|++
operator|,
name|port
operator|+=
literal|6
control|)
block|{
name|String
name|omNodeId
init|=
name|nodeIdBaseStr
operator|+
name|i
decl_stmt|;
name|omNodesKeyValue
operator|.
name|append
argument_list|(
literal|","
argument_list|)
operator|.
name|append
argument_list|(
name|omNodeId
argument_list|)
expr_stmt|;
name|String
name|omAddrKey
init|=
name|OmUtils
operator|.
name|addKeySuffixes
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_ADDRESS_KEY
argument_list|,
name|omServiceId
argument_list|,
name|omNodeId
argument_list|)
decl_stmt|;
name|String
name|omHttpAddrKey
init|=
name|OmUtils
operator|.
name|addKeySuffixes
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_HTTP_ADDRESS_KEY
argument_list|,
name|omServiceId
argument_list|,
name|omNodeId
argument_list|)
decl_stmt|;
name|String
name|omHttpsAddrKey
init|=
name|OmUtils
operator|.
name|addKeySuffixes
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_HTTPS_ADDRESS_KEY
argument_list|,
name|omServiceId
argument_list|,
name|omNodeId
argument_list|)
decl_stmt|;
name|String
name|omRatisPortKey
init|=
name|OmUtils
operator|.
name|addKeySuffixes
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_RATIS_PORT_KEY
argument_list|,
name|omServiceId
argument_list|,
name|omNodeId
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|omAddrKey
argument_list|,
literal|"127.0.0.1:"
operator|+
name|port
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|omHttpAddrKey
argument_list|,
literal|"127.0.0.1:"
operator|+
operator|(
name|port
operator|+
literal|2
operator|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|omHttpsAddrKey
argument_list|,
literal|"127.0.0.1:"
operator|+
operator|(
name|port
operator|+
literal|3
operator|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|omRatisPortKey
argument_list|,
name|port
operator|+
literal|4
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|omNodesKey
argument_list|,
name|omNodesKeyValue
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

