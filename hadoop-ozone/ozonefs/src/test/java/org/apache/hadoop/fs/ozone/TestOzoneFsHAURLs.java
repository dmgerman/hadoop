begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|FsShell
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
name|fs
operator|.
name|Path
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
name|HddsConfigKeys
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
name|OmUtils
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
name|OzoneConsts
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
name|ObjectStore
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
name|OzoneVolume
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
name|hadoop
operator|.
name|util
operator|.
name|ToolRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|util
operator|.
name|LifeCycle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|StringContains
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
name|Test
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
name|util
operator|.
name|Collection
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|HddsUtils
operator|.
name|getHostName
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
name|HddsUtils
operator|.
name|getHostPort
import|;
end_import

begin_comment
comment|/**  * Test client-side URI handling with Ozone Manager HA.  */
end_comment

begin_class
DECL|class|TestOzoneFsHAURLs
specifier|public
class|class
name|TestOzoneFsHAURLs
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestOzoneFsHAURLs
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniOzoneCluster
name|cluster
decl_stmt|;
DECL|field|omId
specifier|private
name|String
name|omId
decl_stmt|;
DECL|field|omServiceId
specifier|private
name|String
name|omServiceId
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
DECL|field|om
specifier|private
name|OzoneManager
name|om
decl_stmt|;
DECL|field|numOfOMs
specifier|private
name|int
name|numOfOMs
decl_stmt|;
DECL|field|volumeName
specifier|private
name|String
name|volumeName
decl_stmt|;
DECL|field|bucketName
specifier|private
name|String
name|bucketName
decl_stmt|;
DECL|field|rootPath
specifier|private
name|String
name|rootPath
decl_stmt|;
DECL|field|o3fsImplKey
specifier|private
specifier|final
name|String
name|o3fsImplKey
init|=
literal|"fs."
operator|+
name|OzoneConsts
operator|.
name|OZONE_URI_SCHEME
operator|+
literal|".impl"
decl_stmt|;
DECL|field|o3fsImplValue
specifier|private
specifier|final
name|String
name|o3fsImplValue
init|=
literal|"org.apache.hadoop.fs.ozone.OzoneFileSystem"
decl_stmt|;
DECL|field|LEADER_ELECTION_TIMEOUT
specifier|private
specifier|static
specifier|final
name|long
name|LEADER_ELECTION_TIMEOUT
init|=
literal|500L
decl_stmt|;
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
name|omId
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|omServiceId
operator|=
literal|"om-service-test1"
expr_stmt|;
name|numOfOMs
operator|=
literal|3
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
specifier|final
name|String
name|path
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|omId
argument_list|)
decl_stmt|;
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
name|metaDirPath
init|=
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|,
literal|"om-meta"
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|metaDirPath
operator|.
name|toString
argument_list|()
argument_list|)
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
name|setTimeDuration
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_KEY
argument_list|,
name|LEADER_ELECTION_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
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
argument_list|)
expr_stmt|;
comment|// writes the version file properties
name|omStore
operator|.
name|initialize
argument_list|()
expr_stmt|;
comment|// Start the cluster
name|cluster
operator|=
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
name|omServiceId
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
name|om
operator|=
name|cluster
operator|.
name|getOzoneManager
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|LifeCycle
operator|.
name|State
operator|.
name|RUNNING
argument_list|,
name|om
operator|.
name|getOmRatisServerState
argument_list|()
argument_list|)
expr_stmt|;
name|volumeName
operator|=
literal|"volume"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|ObjectStore
name|objectStore
init|=
name|OzoneClientFactory
operator|.
name|getRpcClient
argument_list|(
name|omServiceId
argument_list|,
name|conf
argument_list|)
operator|.
name|getObjectStore
argument_list|()
decl_stmt|;
name|objectStore
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|OzoneVolume
name|retVolumeinfo
init|=
name|objectStore
operator|.
name|getVolume
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|bucketName
operator|=
literal|"bucket"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|retVolumeinfo
operator|.
name|createBucket
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|rootPath
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"%s://%s.%s.%s/"
argument_list|,
name|OzoneConsts
operator|.
name|OZONE_URI_SCHEME
argument_list|,
name|bucketName
argument_list|,
name|volumeName
argument_list|,
name|omServiceId
argument_list|)
expr_stmt|;
comment|// Set fs.defaultFS
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
name|rootPath
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Create some dirs
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Path
name|dir1
init|=
operator|new
name|Path
argument_list|(
name|root
argument_list|,
literal|"dir1"
argument_list|)
decl_stmt|;
name|Path
name|dir12
init|=
operator|new
name|Path
argument_list|(
name|dir1
argument_list|,
literal|"dir12"
argument_list|)
decl_stmt|;
name|Path
name|dir2
init|=
operator|new
name|Path
argument_list|(
name|root
argument_list|,
literal|"dir2"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir12
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir2
argument_list|)
expr_stmt|;
block|}
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
comment|/**    * @return the leader OM's RPC address in the MiniOzoneHACluster    */
DECL|method|getLeaderOMNodeAddr ()
specifier|private
name|String
name|getLeaderOMNodeAddr
parameter_list|()
block|{
name|String
name|leaderOMNodeAddr
init|=
literal|null
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|omNodeIds
init|=
name|OmUtils
operator|.
name|getOMNodeIds
argument_list|(
name|conf
argument_list|,
name|omServiceId
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|omNodeIds
operator|.
name|size
argument_list|()
operator|==
name|numOfOMs
operator|)
assert|;
name|MiniOzoneHAClusterImpl
name|haCluster
init|=
operator|(
name|MiniOzoneHAClusterImpl
operator|)
name|cluster
decl_stmt|;
comment|// Note: this loop may be implemented inside MiniOzoneHAClusterImpl
for|for
control|(
name|String
name|omNodeId
range|:
name|omNodeIds
control|)
block|{
comment|// Find the leader OM
if|if
condition|(
operator|!
name|haCluster
operator|.
name|getOzoneManager
argument_list|(
name|omNodeId
argument_list|)
operator|.
name|isLeader
argument_list|()
condition|)
block|{
continue|continue;
block|}
comment|// ozone.om.address.omServiceId.omNode
name|String
name|leaderOMNodeAddrKey
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
name|leaderOMNodeAddr
operator|=
name|conf
operator|.
name|get
argument_list|(
name|leaderOMNodeAddrKey
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Found leader OM: nodeId="
operator|+
name|omNodeId
operator|+
literal|", "
operator|+
name|leaderOMNodeAddrKey
operator|+
literal|"="
operator|+
name|leaderOMNodeAddr
argument_list|)
expr_stmt|;
comment|// Leader found, no need to continue loop
break|break;
block|}
comment|// There has to be a leader
assert|assert
operator|(
name|leaderOMNodeAddr
operator|!=
literal|null
operator|)
assert|;
return|return
name|leaderOMNodeAddr
return|;
block|}
comment|/**    * Get host name from an address. This uses getHostName() internally.    * @param addr Address with port number    * @return Host name    */
DECL|method|getHostFromAddress (String addr)
specifier|private
name|String
name|getHostFromAddress
parameter_list|(
name|String
name|addr
parameter_list|)
block|{
name|Optional
argument_list|<
name|String
argument_list|>
name|hostOptional
init|=
name|getHostName
argument_list|(
name|addr
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|hostOptional
operator|.
name|isPresent
argument_list|()
operator|)
assert|;
return|return
name|hostOptional
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Get port number from an address. This uses getHostPort() internally.    * @param addr Address with port    * @return Port number    */
DECL|method|getPortFromAddress (String addr)
specifier|private
name|int
name|getPortFromAddress
parameter_list|(
name|String
name|addr
parameter_list|)
block|{
name|Optional
argument_list|<
name|Integer
argument_list|>
name|portOptional
init|=
name|getHostPort
argument_list|(
name|addr
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|portOptional
operator|.
name|isPresent
argument_list|()
operator|)
assert|;
return|return
name|portOptional
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Test OM HA URLs with qualified fs.defaultFS.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testWithQualifiedDefaultFS ()
specifier|public
name|void
name|testWithQualifiedDefaultFS
parameter_list|()
throws|throws
name|Exception
block|{
name|OzoneConfiguration
name|clientConf
init|=
operator|new
name|OzoneConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|clientConf
operator|.
name|setQuietMode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|clientConf
operator|.
name|set
argument_list|(
name|o3fsImplKey
argument_list|,
name|o3fsImplValue
argument_list|)
expr_stmt|;
comment|// fs.defaultFS = o3fs://bucketName.volumeName.omServiceId/
name|clientConf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
name|rootPath
argument_list|)
expr_stmt|;
comment|// Pick leader OM's RPC address and assign it to ozone.om.address for
comment|// the test case: ozone fs -ls o3fs://bucket.volume.om1/
name|String
name|leaderOMNodeAddr
init|=
name|getLeaderOMNodeAddr
argument_list|()
decl_stmt|;
comment|// ozone.om.address was set to service id in MiniOzoneHAClusterImpl
name|clientConf
operator|.
name|set
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_ADDRESS_KEY
argument_list|,
name|leaderOMNodeAddr
argument_list|)
expr_stmt|;
name|FsShell
name|shell
init|=
operator|new
name|FsShell
argument_list|(
name|clientConf
argument_list|)
decl_stmt|;
name|int
name|res
decl_stmt|;
try|try
block|{
comment|// Test case 1: ozone fs -ls /
comment|// Expectation: Success.
name|res
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|shell
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-ls"
block|,
literal|"/"
block|}
argument_list|)
expr_stmt|;
comment|// Check return value, should be 0 (success)
name|Assert
operator|.
name|assertEquals
argument_list|(
name|res
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test case 2: ozone fs -ls o3fs:///
comment|// Expectation: Success. fs.defaultFS is a fully qualified path.
name|res
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|shell
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-ls"
block|,
literal|"o3fs:///"
block|}
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|res
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test case 3: ozone fs -ls o3fs://bucket.volume/
comment|// Expectation: Fail. Must have service id or host name when HA is enabled
name|String
name|unqualifiedPath1
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s://%s.%s/"
argument_list|,
name|OzoneConsts
operator|.
name|OZONE_URI_SCHEME
argument_list|,
name|bucketName
argument_list|,
name|volumeName
argument_list|)
decl_stmt|;
try|try
init|(
name|GenericTestUtils
operator|.
name|SystemErrCapturer
name|capture
init|=
operator|new
name|GenericTestUtils
operator|.
name|SystemErrCapturer
argument_list|()
init|)
block|{
name|res
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|shell
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-ls"
block|,
name|unqualifiedPath1
block|}
argument_list|)
expr_stmt|;
comment|// Check stderr, inspired by testDFSWithInvalidCommmand
name|Assert
operator|.
name|assertThat
argument_list|(
literal|"Command did not print the error message "
operator|+
literal|"correctly for test case: ozone fs -ls o3fs://bucket.volume/"
argument_list|,
name|capture
operator|.
name|getOutput
argument_list|()
argument_list|,
name|StringContains
operator|.
name|containsString
argument_list|(
literal|"-ls: Service ID or host name must not"
operator|+
literal|" be omitted when ozone.om.service.ids is defined."
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Check return value, should be -1 (failure)
name|Assert
operator|.
name|assertEquals
argument_list|(
name|res
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// Test case 4: ozone fs -ls o3fs://bucket.volume.om1/
comment|// Expectation: Success. The client should use the port number
comment|// set in ozone.om.address.
name|String
name|qualifiedPath1
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s://%s.%s.%s/"
argument_list|,
name|OzoneConsts
operator|.
name|OZONE_URI_SCHEME
argument_list|,
name|bucketName
argument_list|,
name|volumeName
argument_list|,
name|getHostFromAddress
argument_list|(
name|leaderOMNodeAddr
argument_list|)
argument_list|)
decl_stmt|;
name|res
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|shell
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-ls"
block|,
name|qualifiedPath1
block|}
argument_list|)
expr_stmt|;
comment|// Note: this test case will fail if the port is not from the leader node
name|Assert
operator|.
name|assertEquals
argument_list|(
name|res
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test case 5: ozone fs -ls o3fs://bucket.volume.om1:port/
comment|// Expectation: Success.
name|String
name|qualifiedPath2
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s://%s.%s.%s/"
argument_list|,
name|OzoneConsts
operator|.
name|OZONE_URI_SCHEME
argument_list|,
name|bucketName
argument_list|,
name|volumeName
argument_list|,
name|leaderOMNodeAddr
argument_list|)
decl_stmt|;
name|res
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|shell
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-ls"
block|,
name|qualifiedPath2
block|}
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|res
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test case 6: ozone fs -ls o3fs://bucket.volume.id1/
comment|// Expectation: Success.
name|String
name|qualifiedPath3
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s://%s.%s.%s/"
argument_list|,
name|OzoneConsts
operator|.
name|OZONE_URI_SCHEME
argument_list|,
name|bucketName
argument_list|,
name|volumeName
argument_list|,
name|omServiceId
argument_list|)
decl_stmt|;
name|res
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|shell
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-ls"
block|,
name|qualifiedPath3
block|}
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|res
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test case 7: ozone fs -ls o3fs://bucket.volume.id1:port/
comment|// Expectation: Fail. Service ID does not use port information.
comment|// Use the port number from leader OM (doesn't really matter)
name|String
name|unqualifiedPath2
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s://%s.%s.%s:%d/"
argument_list|,
name|OzoneConsts
operator|.
name|OZONE_URI_SCHEME
argument_list|,
name|bucketName
argument_list|,
name|volumeName
argument_list|,
name|omServiceId
argument_list|,
name|getPortFromAddress
argument_list|(
name|leaderOMNodeAddr
argument_list|)
argument_list|)
decl_stmt|;
try|try
init|(
name|GenericTestUtils
operator|.
name|SystemErrCapturer
name|capture
init|=
operator|new
name|GenericTestUtils
operator|.
name|SystemErrCapturer
argument_list|()
init|)
block|{
name|res
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|shell
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-ls"
block|,
name|unqualifiedPath2
block|}
argument_list|)
expr_stmt|;
comment|// Check stderr
name|Assert
operator|.
name|assertThat
argument_list|(
literal|"Command did not print the error message "
operator|+
literal|"correctly for test case: "
operator|+
literal|"ozone fs -ls o3fs://bucket.volume.id1:port/"
argument_list|,
name|capture
operator|.
name|getOutput
argument_list|()
argument_list|,
name|StringContains
operator|.
name|containsString
argument_list|(
literal|"does not use port information"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Check return value, should be -1 (failure)
name|Assert
operator|.
name|assertEquals
argument_list|(
name|res
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|shell
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Helper function for testOtherDefaultFS(),    * run fs -ls o3fs:/// against different fs.defaultFS input.    *    * @param defaultFS Desired fs.defaultFS to be used in the test    * @throws Exception    */
DECL|method|testWithDefaultFS (String defaultFS)
specifier|private
name|void
name|testWithDefaultFS
parameter_list|(
name|String
name|defaultFS
parameter_list|)
throws|throws
name|Exception
block|{
name|OzoneConfiguration
name|clientConf
init|=
operator|new
name|OzoneConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|clientConf
operator|.
name|setQuietMode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|clientConf
operator|.
name|set
argument_list|(
name|o3fsImplKey
argument_list|,
name|o3fsImplValue
argument_list|)
expr_stmt|;
comment|// fs.defaultFS = file:///
name|clientConf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
name|defaultFS
argument_list|)
expr_stmt|;
name|FsShell
name|shell
init|=
operator|new
name|FsShell
argument_list|(
name|clientConf
argument_list|)
decl_stmt|;
try|try
block|{
comment|// Test case: ozone fs -ls o3fs:///
comment|// Expectation: Fail. fs.defaultFS is not a qualified o3fs URI.
name|int
name|res
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|shell
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-ls"
block|,
literal|"o3fs:///"
block|}
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|res
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|shell
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test OM HA URLs with some unqualified fs.defaultFS.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testOtherDefaultFS ()
specifier|public
name|void
name|testOtherDefaultFS
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test scenarios where fs.defaultFS isn't a fully qualified o3fs
comment|// fs.defaultFS = file:///
name|testWithDefaultFS
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_DEFAULT
argument_list|)
expr_stmt|;
comment|// fs.defaultFS = hdfs://ns1/
name|testWithDefaultFS
argument_list|(
literal|"hdfs://ns1/"
argument_list|)
expr_stmt|;
comment|// fs.defaultFS = o3fs:///
name|String
name|unqualifiedFs1
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s:///"
argument_list|,
name|OzoneConsts
operator|.
name|OZONE_URI_SCHEME
argument_list|)
decl_stmt|;
name|testWithDefaultFS
argument_list|(
name|unqualifiedFs1
argument_list|)
expr_stmt|;
comment|// fs.defaultFS = o3fs://bucketName.volumeName/
name|String
name|unqualifiedFs2
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s://%s.%s/"
argument_list|,
name|OzoneConsts
operator|.
name|OZONE_URI_SCHEME
argument_list|,
name|bucketName
argument_list|,
name|volumeName
argument_list|)
decl_stmt|;
name|testWithDefaultFS
argument_list|(
name|unqualifiedFs2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

