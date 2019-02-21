begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|RandomStringUtils
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
name|ObjectStoreHandler
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
name|ozone
operator|.
name|MiniOzoneCluster
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
name|MiniOzoneHAClusterImpl
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
name|rpc
operator|.
name|ha
operator|.
name|OMProxyInfo
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
name|rpc
operator|.
name|ha
operator|.
name|OMProxyProvider
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
name|handlers
operator|.
name|UserArgs
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
name|handlers
operator|.
name|VolumeArgs
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
name|interfaces
operator|.
name|StorageHandler
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
name|response
operator|.
name|VolumeInfo
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
name|utils
operator|.
name|OzoneUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
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
name|UUID
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
name|MiniOzoneHAClusterImpl
operator|.
name|NODE_FAILURE_TIMEOUT
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
name|OZONE_ACL_ENABLED
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
name|OZONE_OPEN_KEY_EXPIRE_THRESHOLD_SECONDS
import|;
end_import

begin_comment
comment|/**  * Test Ozone Manager operation in distributed handler scenario.  */
end_comment

begin_class
DECL|class|TestOzoneManagerHA
specifier|public
class|class
name|TestOzoneManagerHA
block|{
DECL|field|cluster
specifier|private
name|MiniOzoneHAClusterImpl
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|storageHandler
specifier|private
name|StorageHandler
name|storageHandler
decl_stmt|;
DECL|field|userArgs
specifier|private
name|UserArgs
name|userArgs
decl_stmt|;
DECL|field|conf
specifier|private
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|clusterId
specifier|private
name|String
name|clusterId
decl_stmt|;
DECL|field|scmId
specifier|private
name|String
name|scmId
decl_stmt|;
DECL|field|numOfOMs
specifier|private
name|int
name|numOfOMs
init|=
literal|3
decl_stmt|;
annotation|@
name|Rule
DECL|field|exception
specifier|public
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
annotation|@
name|Rule
DECL|field|timeout
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|60_000
argument_list|)
decl_stmt|;
comment|/**    * Create a MiniDFSCluster for testing.    *<p>    * Ozone is made active by setting OZONE_ENABLED = true    *    * @throws IOException    */
annotation|@
name|Before
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|clusterId
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|scmId
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|OZONE_ACL_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OZONE_OPEN_KEY_EXPIRE_THRESHOLD_SECONDS
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|(
name|MiniOzoneHAClusterImpl
operator|)
name|MiniOzoneCluster
operator|.
name|newHABuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
operator|.
name|setScmId
argument_list|(
name|scmId
argument_list|)
operator|.
name|setOMServiceId
argument_list|(
literal|"om-service-test1"
argument_list|)
operator|.
name|setNumOfOzoneManagers
argument_list|(
name|numOfOMs
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
name|storageHandler
operator|=
operator|new
name|ObjectStoreHandler
argument_list|(
name|conf
argument_list|)
operator|.
name|getStorageHandler
argument_list|()
expr_stmt|;
name|userArgs
operator|=
operator|new
name|UserArgs
argument_list|(
literal|null
argument_list|,
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Shutdown MiniDFSCluster.    */
annotation|@
name|After
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test a client request when all OM nodes are running. The request should    * succeed.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testAllOMNodesRunning ()
specifier|public
name|void
name|testAllOMNodesRunning
parameter_list|()
throws|throws
name|Exception
block|{
name|testCreateVolume
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test client request succeeds even if one OM is down.    */
annotation|@
name|Test
DECL|method|testOneOMNodeDown ()
specifier|public
name|void
name|testOneOMNodeDown
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|stopOzoneManager
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|NODE_FAILURE_TIMEOUT
operator|*
literal|2
argument_list|)
expr_stmt|;
name|testCreateVolume
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test client request fails when 2 OMs are down.    */
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"TODO:HDDS-1158"
argument_list|)
DECL|method|testTwoOMNodesDown ()
specifier|public
name|void
name|testTwoOMNodesDown
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|stopOzoneManager
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|stopOzoneManager
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|NODE_FAILURE_TIMEOUT
operator|*
literal|2
argument_list|)
expr_stmt|;
name|testCreateVolume
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a volume and test its attribute.    */
DECL|method|testCreateVolume (boolean checkSuccess)
specifier|private
name|void
name|testCreateVolume
parameter_list|(
name|boolean
name|checkSuccess
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|userName
init|=
literal|"user"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|adminName
init|=
literal|"admin"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|volumeName
init|=
literal|"volume"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|VolumeArgs
name|createVolumeArgs
init|=
operator|new
name|VolumeArgs
argument_list|(
name|volumeName
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|createVolumeArgs
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|createVolumeArgs
operator|.
name|setAdminName
argument_list|(
name|adminName
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|createVolume
argument_list|(
name|createVolumeArgs
argument_list|)
expr_stmt|;
name|VolumeArgs
name|getVolumeArgs
init|=
operator|new
name|VolumeArgs
argument_list|(
name|volumeName
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|VolumeInfo
name|retVolumeinfo
init|=
name|storageHandler
operator|.
name|getVolumeInfo
argument_list|(
name|getVolumeArgs
argument_list|)
decl_stmt|;
if|if
condition|(
name|checkSuccess
condition|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|retVolumeinfo
operator|.
name|getVolumeName
argument_list|()
operator|.
name|equals
argument_list|(
name|volumeName
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|retVolumeinfo
operator|.
name|getOwner
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|userName
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Verify that the request failed
name|Assert
operator|.
name|assertTrue
argument_list|(
name|retVolumeinfo
operator|.
name|getVolumeName
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that OMProxyProvider creates an OM proxy for each OM in the cluster.    */
annotation|@
name|Test
DECL|method|testOMClientProxyProvide ()
specifier|public
name|void
name|testOMClientProxyProvide
parameter_list|()
throws|throws
name|Exception
block|{
name|OzoneClient
name|rpcClient
init|=
name|cluster
operator|.
name|getRpcClient
argument_list|()
decl_stmt|;
name|OMProxyProvider
name|omProxyProvider
init|=
name|rpcClient
operator|.
name|getObjectStore
argument_list|()
operator|.
name|getClientProxy
argument_list|()
operator|.
name|getOMProxyProvider
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|OMProxyInfo
argument_list|>
name|omProxies
init|=
name|omProxyProvider
operator|.
name|getOMProxies
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|numOfOMs
argument_list|,
name|omProxies
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numOfOMs
condition|;
name|i
operator|++
control|)
block|{
name|InetSocketAddress
name|omRpcServerAddr
init|=
name|cluster
operator|.
name|getOzoneManager
argument_list|(
name|i
argument_list|)
operator|.
name|getOmRpcServerAddr
argument_list|()
decl_stmt|;
name|boolean
name|omClientProxyExists
init|=
literal|false
decl_stmt|;
for|for
control|(
name|OMProxyInfo
name|omProxyInfo
range|:
name|omProxies
control|)
block|{
if|if
condition|(
name|omProxyInfo
operator|.
name|getAddress
argument_list|()
operator|.
name|equals
argument_list|(
name|omRpcServerAddr
argument_list|)
condition|)
block|{
name|omClientProxyExists
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"There is no OM Client Proxy corresponding to OM "
operator|+
literal|"node"
operator|+
name|cluster
operator|.
name|getOzoneManager
argument_list|(
name|i
argument_list|)
operator|.
name|getOMNodId
argument_list|()
argument_list|,
name|omClientProxyExists
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

