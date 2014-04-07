begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|protocol
operator|.
name|DatanodeID
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
name|protocol
operator|.
name|DatanodeInfo
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
name|protocol
operator|.
name|HdfsConstants
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
name|protocol
operator|.
name|HdfsConstants
operator|.
name|DatanodeReportType
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
name|blockmanagement
operator|.
name|DatanodeManager
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
name|common
operator|.
name|IncorrectVersionException
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
name|common
operator|.
name|StorageInfo
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
name|protocol
operator|.
name|DatanodeRegistration
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
name|protocol
operator|.
name|NamenodeProtocols
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
name|VersionInfo
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
name|java
operator|.
name|net
operator|.
name|InetAddress
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
name|security
operator|.
name|Permission
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
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doReturn
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_comment
comment|/**  * This class tests data node registration.  */
end_comment

begin_class
DECL|class|TestDatanodeRegistration
specifier|public
class|class
name|TestDatanodeRegistration
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestDatanodeRegistration
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|MonitorDNS
specifier|private
specifier|static
class|class
name|MonitorDNS
extends|extends
name|SecurityManager
block|{
DECL|field|lookups
name|int
name|lookups
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|checkPermission (Permission perm)
specifier|public
name|void
name|checkPermission
parameter_list|(
name|Permission
name|perm
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|checkConnect (String host, int port)
specifier|public
name|void
name|checkConnect
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|)
block|{
if|if
condition|(
name|port
operator|==
operator|-
literal|1
condition|)
block|{
name|lookups
operator|++
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Ensure the datanode manager does not do host lookup after registration,    * especially for node reports.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testDNSLookups ()
specifier|public
name|void
name|testDNSLookups
parameter_list|()
throws|throws
name|Exception
block|{
name|MonitorDNS
name|sm
init|=
operator|new
name|MonitorDNS
argument_list|()
decl_stmt|;
name|System
operator|.
name|setSecurityManager
argument_list|(
name|sm
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|8
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|int
name|initialLookups
init|=
name|sm
operator|.
name|lookups
decl_stmt|;
name|assertTrue
argument_list|(
literal|"dns security manager is active"
argument_list|,
name|initialLookups
operator|!=
literal|0
argument_list|)
expr_stmt|;
name|DatanodeManager
name|dm
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
decl_stmt|;
comment|// make sure no lookups occur
name|dm
operator|.
name|refreshNodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|initialLookups
argument_list|,
name|sm
operator|.
name|lookups
argument_list|)
expr_stmt|;
name|dm
operator|.
name|refreshNodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|initialLookups
argument_list|,
name|sm
operator|.
name|lookups
argument_list|)
expr_stmt|;
comment|// ensure none of the reports trigger lookups
name|dm
operator|.
name|getDatanodeListForReport
argument_list|(
name|DatanodeReportType
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|initialLookups
argument_list|,
name|sm
operator|.
name|lookups
argument_list|)
expr_stmt|;
name|dm
operator|.
name|getDatanodeListForReport
argument_list|(
name|DatanodeReportType
operator|.
name|LIVE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|initialLookups
argument_list|,
name|sm
operator|.
name|lookups
argument_list|)
expr_stmt|;
name|dm
operator|.
name|getDatanodeListForReport
argument_list|(
name|DatanodeReportType
operator|.
name|DEAD
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|initialLookups
argument_list|,
name|sm
operator|.
name|lookups
argument_list|)
expr_stmt|;
block|}
finally|finally
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
name|System
operator|.
name|setSecurityManager
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Regression test for HDFS-894 ensures that, when datanodes    * are restarted, the new IPC port is registered with the    * namenode.    */
annotation|@
name|Test
DECL|method|testChangeIpcPort ()
specifier|public
name|void
name|testChangeIpcPort
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|cluster
operator|.
name|getNameNodePort
argument_list|()
argument_list|)
decl_stmt|;
name|DFSClient
name|client
init|=
operator|new
name|DFSClient
argument_list|(
name|addr
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// Restart datanodes
name|cluster
operator|.
name|restartDataNodes
argument_list|()
expr_stmt|;
comment|// Wait until we get a heartbeat from the new datanode
name|DatanodeInfo
index|[]
name|report
init|=
name|client
operator|.
name|datanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|long
name|firstUpdateAfterRestart
init|=
name|report
index|[
literal|0
index|]
operator|.
name|getLastUpdate
argument_list|()
decl_stmt|;
name|boolean
name|gotHeartbeat
init|=
literal|false
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
literal|10
operator|&&
operator|!
name|gotHeartbeat
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|i
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{}
name|report
operator|=
name|client
operator|.
name|datanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|gotHeartbeat
operator|=
operator|(
name|report
index|[
literal|0
index|]
operator|.
name|getLastUpdate
argument_list|()
operator|>
name|firstUpdateAfterRestart
operator|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|gotHeartbeat
condition|)
block|{
name|fail
argument_list|(
literal|"Never got a heartbeat from restarted datanode."
argument_list|)
expr_stmt|;
block|}
name|int
name|realIpcPort
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getIpcPort
argument_list|()
decl_stmt|;
comment|// Now make sure the reported IPC port is the correct one.
name|assertEquals
argument_list|(
name|realIpcPort
argument_list|,
name|report
index|[
literal|0
index|]
operator|.
name|getIpcPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
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
block|}
annotation|@
name|Test
DECL|method|testChangeStorageID ()
specifier|public
name|void
name|testChangeStorageID
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|DN_IP_ADDR
init|=
literal|"127.0.0.1"
decl_stmt|;
specifier|final
name|String
name|DN_HOSTNAME
init|=
literal|"localhost"
decl_stmt|;
specifier|final
name|int
name|DN_XFER_PORT
init|=
literal|12345
decl_stmt|;
specifier|final
name|int
name|DN_INFO_PORT
init|=
literal|12346
decl_stmt|;
specifier|final
name|int
name|DN_INFO_SECURE_PORT
init|=
literal|12347
decl_stmt|;
specifier|final
name|int
name|DN_IPC_PORT
init|=
literal|12348
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|cluster
operator|.
name|getNameNodePort
argument_list|()
argument_list|)
decl_stmt|;
name|DFSClient
name|client
init|=
operator|new
name|DFSClient
argument_list|(
name|addr
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|NamenodeProtocols
name|rpcServer
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
decl_stmt|;
comment|// register a datanode
name|DatanodeID
name|dnId
init|=
operator|new
name|DatanodeID
argument_list|(
name|DN_IP_ADDR
argument_list|,
name|DN_HOSTNAME
argument_list|,
literal|"fake-datanode-id"
argument_list|,
name|DN_XFER_PORT
argument_list|,
name|DN_INFO_PORT
argument_list|,
name|DN_INFO_SECURE_PORT
argument_list|,
name|DN_IPC_PORT
argument_list|)
decl_stmt|;
name|long
name|nnCTime
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
operator|.
name|getCTime
argument_list|()
decl_stmt|;
name|StorageInfo
name|mockStorageInfo
init|=
name|mock
argument_list|(
name|StorageInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|nnCTime
argument_list|)
operator|.
name|when
argument_list|(
name|mockStorageInfo
argument_list|)
operator|.
name|getCTime
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|HdfsConstants
operator|.
name|DATANODE_LAYOUT_VERSION
argument_list|)
operator|.
name|when
argument_list|(
name|mockStorageInfo
argument_list|)
operator|.
name|getLayoutVersion
argument_list|()
expr_stmt|;
name|DatanodeRegistration
name|dnReg
init|=
operator|new
name|DatanodeRegistration
argument_list|(
name|dnId
argument_list|,
name|mockStorageInfo
argument_list|,
literal|null
argument_list|,
name|VersionInfo
operator|.
name|getVersion
argument_list|()
argument_list|)
decl_stmt|;
name|rpcServer
operator|.
name|registerDatanode
argument_list|(
name|dnReg
argument_list|)
expr_stmt|;
name|DatanodeInfo
index|[]
name|report
init|=
name|client
operator|.
name|datanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected a registered datanode"
argument_list|,
literal|1
argument_list|,
name|report
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// register the same datanode again with a different storage ID
name|dnId
operator|=
operator|new
name|DatanodeID
argument_list|(
name|DN_IP_ADDR
argument_list|,
name|DN_HOSTNAME
argument_list|,
literal|"changed-fake-datanode-id"
argument_list|,
name|DN_XFER_PORT
argument_list|,
name|DN_INFO_PORT
argument_list|,
name|DN_INFO_SECURE_PORT
argument_list|,
name|DN_IPC_PORT
argument_list|)
expr_stmt|;
name|dnReg
operator|=
operator|new
name|DatanodeRegistration
argument_list|(
name|dnId
argument_list|,
name|mockStorageInfo
argument_list|,
literal|null
argument_list|,
name|VersionInfo
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|rpcServer
operator|.
name|registerDatanode
argument_list|(
name|dnReg
argument_list|)
expr_stmt|;
name|report
operator|=
name|client
operator|.
name|datanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Datanode with changed storage ID not recognized"
argument_list|,
literal|1
argument_list|,
name|report
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
finally|finally
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
block|}
annotation|@
name|Test
DECL|method|testRegistrationWithDifferentSoftwareVersions ()
specifier|public
name|void
name|testRegistrationWithDifferentSoftwareVersions
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MIN_SUPPORTED_NAMENODE_VERSION_KEY
argument_list|,
literal|"3.0.0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MIN_SUPPORTED_DATANODE_VERSION_KEY
argument_list|,
literal|"3.0.0"
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|NamenodeProtocols
name|rpcServer
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
decl_stmt|;
name|long
name|nnCTime
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
operator|.
name|getCTime
argument_list|()
decl_stmt|;
name|StorageInfo
name|mockStorageInfo
init|=
name|mock
argument_list|(
name|StorageInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|nnCTime
argument_list|)
operator|.
name|when
argument_list|(
name|mockStorageInfo
argument_list|)
operator|.
name|getCTime
argument_list|()
expr_stmt|;
name|DatanodeRegistration
name|mockDnReg
init|=
name|mock
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|HdfsConstants
operator|.
name|DATANODE_LAYOUT_VERSION
argument_list|)
operator|.
name|when
argument_list|(
name|mockDnReg
argument_list|)
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
literal|"127.0.0.1"
argument_list|)
operator|.
name|when
argument_list|(
name|mockDnReg
argument_list|)
operator|.
name|getIpAddr
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
literal|123
argument_list|)
operator|.
name|when
argument_list|(
name|mockDnReg
argument_list|)
operator|.
name|getXferPort
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
literal|"fake-storage-id"
argument_list|)
operator|.
name|when
argument_list|(
name|mockDnReg
argument_list|)
operator|.
name|getDatanodeUuid
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|mockStorageInfo
argument_list|)
operator|.
name|when
argument_list|(
name|mockDnReg
argument_list|)
operator|.
name|getStorageInfo
argument_list|()
expr_stmt|;
comment|// Should succeed when software versions are the same.
name|doReturn
argument_list|(
literal|"3.0.0"
argument_list|)
operator|.
name|when
argument_list|(
name|mockDnReg
argument_list|)
operator|.
name|getSoftwareVersion
argument_list|()
expr_stmt|;
name|rpcServer
operator|.
name|registerDatanode
argument_list|(
name|mockDnReg
argument_list|)
expr_stmt|;
comment|// Should succeed when software version of DN is above minimum required by NN.
name|doReturn
argument_list|(
literal|"4.0.0"
argument_list|)
operator|.
name|when
argument_list|(
name|mockDnReg
argument_list|)
operator|.
name|getSoftwareVersion
argument_list|()
expr_stmt|;
name|rpcServer
operator|.
name|registerDatanode
argument_list|(
name|mockDnReg
argument_list|)
expr_stmt|;
comment|// Should fail when software version of DN is below minimum required by NN.
name|doReturn
argument_list|(
literal|"2.0.0"
argument_list|)
operator|.
name|when
argument_list|(
name|mockDnReg
argument_list|)
operator|.
name|getSoftwareVersion
argument_list|()
expr_stmt|;
try|try
block|{
name|rpcServer
operator|.
name|registerDatanode
argument_list|(
name|mockDnReg
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should not have been able to register DN with too-low version."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IncorrectVersionException
name|ive
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"The reported DataNode version is too low"
argument_list|,
name|ive
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got expected exception"
argument_list|,
name|ive
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
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
block|}
annotation|@
name|Test
DECL|method|testRegistrationWithDifferentSoftwareVersionsDuringUpgrade ()
specifier|public
name|void
name|testRegistrationWithDifferentSoftwareVersionsDuringUpgrade
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MIN_SUPPORTED_NAMENODE_VERSION_KEY
argument_list|,
literal|"1.0.0"
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|NamenodeProtocols
name|rpcServer
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
decl_stmt|;
name|long
name|nnCTime
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
operator|.
name|getCTime
argument_list|()
decl_stmt|;
name|StorageInfo
name|mockStorageInfo
init|=
name|mock
argument_list|(
name|StorageInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|nnCTime
argument_list|)
operator|.
name|when
argument_list|(
name|mockStorageInfo
argument_list|)
operator|.
name|getCTime
argument_list|()
expr_stmt|;
name|DatanodeRegistration
name|mockDnReg
init|=
name|mock
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|HdfsConstants
operator|.
name|DATANODE_LAYOUT_VERSION
argument_list|)
operator|.
name|when
argument_list|(
name|mockDnReg
argument_list|)
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
literal|"fake-storage-id"
argument_list|)
operator|.
name|when
argument_list|(
name|mockDnReg
argument_list|)
operator|.
name|getDatanodeUuid
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|mockStorageInfo
argument_list|)
operator|.
name|when
argument_list|(
name|mockDnReg
argument_list|)
operator|.
name|getStorageInfo
argument_list|()
expr_stmt|;
comment|// Should succeed when software versions are the same and CTimes are the
comment|// same.
name|doReturn
argument_list|(
name|VersionInfo
operator|.
name|getVersion
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|mockDnReg
argument_list|)
operator|.
name|getSoftwareVersion
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
literal|"127.0.0.1"
argument_list|)
operator|.
name|when
argument_list|(
name|mockDnReg
argument_list|)
operator|.
name|getIpAddr
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
literal|123
argument_list|)
operator|.
name|when
argument_list|(
name|mockDnReg
argument_list|)
operator|.
name|getXferPort
argument_list|()
expr_stmt|;
name|rpcServer
operator|.
name|registerDatanode
argument_list|(
name|mockDnReg
argument_list|)
expr_stmt|;
comment|// Should succeed when software versions are the same and CTimes are
comment|// different.
name|doReturn
argument_list|(
name|nnCTime
operator|+
literal|1
argument_list|)
operator|.
name|when
argument_list|(
name|mockStorageInfo
argument_list|)
operator|.
name|getCTime
argument_list|()
expr_stmt|;
name|rpcServer
operator|.
name|registerDatanode
argument_list|(
name|mockDnReg
argument_list|)
expr_stmt|;
comment|// Should fail when software version of DN is different from NN and CTimes
comment|// are different.
name|doReturn
argument_list|(
name|VersionInfo
operator|.
name|getVersion
argument_list|()
operator|+
literal|".1"
argument_list|)
operator|.
name|when
argument_list|(
name|mockDnReg
argument_list|)
operator|.
name|getSoftwareVersion
argument_list|()
expr_stmt|;
try|try
block|{
name|rpcServer
operator|.
name|registerDatanode
argument_list|(
name|mockDnReg
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should not have been able to register DN with different software"
operator|+
literal|" versions and CTimes"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IncorrectVersionException
name|ive
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"does not match CTime of NN"
argument_list|,
name|ive
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got expected exception"
argument_list|,
name|ive
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
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
block|}
block|}
end_class

end_unit

