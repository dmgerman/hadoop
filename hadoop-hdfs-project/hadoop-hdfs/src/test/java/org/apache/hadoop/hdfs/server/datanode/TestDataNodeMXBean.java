begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
package|package
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
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
name|Collection
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
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|type
operator|.
name|TypeReference
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
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
name|Supplier
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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|hdfs
operator|.
name|DFSTestUtil
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
name|hdfs
operator|.
name|protocol
operator|.
name|datatransfer
operator|.
name|sasl
operator|.
name|SaslDataTransferTestCase
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|util
operator|.
name|ajax
operator|.
name|JSON
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
name|assertEquals
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Class for testing {@link DataNodeMXBean} implementation  */
end_comment

begin_class
DECL|class|TestDataNodeMXBean
specifier|public
class|class
name|TestDataNodeMXBean
extends|extends
name|SaslDataTransferTestCase
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
name|TestDataNodeMXBean
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testDataNodeMXBean ()
specifier|public
name|void
name|testDataNodeMXBean
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|DataNode
argument_list|>
name|datanodes
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanodes
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|DataNode
name|datanode
init|=
name|datanodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|MBeanServer
name|mbs
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|ObjectName
name|mxbeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"Hadoop:service=DataNode,name=DataNodeInfo"
argument_list|)
decl_stmt|;
comment|// get attribute "ClusterId"
name|String
name|clusterId
init|=
operator|(
name|String
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"ClusterId"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanode
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|clusterId
argument_list|)
expr_stmt|;
comment|// get attribute "Version"
name|String
name|version
init|=
operator|(
name|String
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"Version"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanode
operator|.
name|getVersion
argument_list|()
argument_list|,
name|version
argument_list|)
expr_stmt|;
comment|// get attribute "SotfwareVersion"
name|String
name|softwareVersion
init|=
operator|(
name|String
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"SoftwareVersion"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanode
operator|.
name|getSoftwareVersion
argument_list|()
argument_list|,
name|softwareVersion
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|version
argument_list|,
name|softwareVersion
operator|+
literal|", r"
operator|+
name|datanode
operator|.
name|getRevision
argument_list|()
argument_list|)
expr_stmt|;
comment|// get attribute "RpcPort"
name|String
name|rpcPort
init|=
operator|(
name|String
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"RpcPort"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanode
operator|.
name|getRpcPort
argument_list|()
argument_list|,
name|rpcPort
argument_list|)
expr_stmt|;
comment|// get attribute "HttpPort"
name|String
name|httpPort
init|=
operator|(
name|String
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"HttpPort"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanode
operator|.
name|getHttpPort
argument_list|()
argument_list|,
name|httpPort
argument_list|)
expr_stmt|;
comment|// get attribute "NamenodeAddresses"
name|String
name|namenodeAddresses
init|=
operator|(
name|String
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"NamenodeAddresses"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanode
operator|.
name|getNamenodeAddresses
argument_list|()
argument_list|,
name|namenodeAddresses
argument_list|)
expr_stmt|;
comment|// get attribute "getDatanodeHostname"
name|String
name|datanodeHostname
init|=
operator|(
name|String
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"DatanodeHostname"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanode
operator|.
name|getDatanodeHostname
argument_list|()
argument_list|,
name|datanodeHostname
argument_list|)
expr_stmt|;
comment|// get attribute "getVolumeInfo"
name|String
name|volumeInfo
init|=
operator|(
name|String
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"VolumeInfo"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|replaceDigits
argument_list|(
name|datanode
operator|.
name|getVolumeInfo
argument_list|()
argument_list|)
argument_list|,
name|replaceDigits
argument_list|(
name|volumeInfo
argument_list|)
argument_list|)
expr_stmt|;
comment|// Ensure mxbean's XceiverCount is same as the DataNode's
comment|// live value.
name|int
name|xceiverCount
init|=
operator|(
name|Integer
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"XceiverCount"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanode
operator|.
name|getXceiverCount
argument_list|()
argument_list|,
name|xceiverCount
argument_list|)
expr_stmt|;
comment|// Ensure mxbean's XmitsInProgress is same as the DataNode's
comment|// live value.
name|int
name|xmitsInProgress
init|=
operator|(
name|Integer
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"XmitsInProgress"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanode
operator|.
name|getXmitsInProgress
argument_list|()
argument_list|,
name|xmitsInProgress
argument_list|)
expr_stmt|;
name|String
name|bpActorInfo
init|=
operator|(
name|String
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"BPServiceActorInfo"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanode
operator|.
name|getBPServiceActorInfo
argument_list|()
argument_list|,
name|bpActorInfo
argument_list|)
expr_stmt|;
name|String
name|slowDisks
init|=
operator|(
name|String
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"SlowDisks"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanode
operator|.
name|getSlowDisks
argument_list|()
argument_list|,
name|slowDisks
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
DECL|method|testDataNodeMXBeanSecurityEnabled ()
specifier|public
name|void
name|testDataNodeMXBeanSecurityEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|simpleConf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Configuration
name|secureConf
init|=
name|createSecureConfig
argument_list|(
literal|"authentication"
argument_list|)
decl_stmt|;
comment|// get attribute "SecurityEnabled" with simple configuration
try|try
init|(
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|simpleConf
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
name|List
argument_list|<
name|DataNode
argument_list|>
name|datanodes
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanodes
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|DataNode
name|datanode
init|=
name|datanodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|MBeanServer
name|mbs
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|ObjectName
name|mxbeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"Hadoop:service=DataNode,name=DataNodeInfo"
argument_list|)
decl_stmt|;
name|boolean
name|securityEnabled
init|=
operator|(
name|boolean
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"SecurityEnabled"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|securityEnabled
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanode
operator|.
name|isSecurityEnabled
argument_list|()
argument_list|,
name|securityEnabled
argument_list|)
expr_stmt|;
block|}
comment|// get attribute "SecurityEnabled" with secure configuration
try|try
init|(
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|secureConf
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
name|List
argument_list|<
name|DataNode
argument_list|>
name|datanodes
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanodes
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|DataNode
name|datanode
init|=
name|datanodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|MBeanServer
name|mbs
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|ObjectName
name|mxbeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"Hadoop:service=DataNode,name=DataNodeInfo"
argument_list|)
decl_stmt|;
name|boolean
name|securityEnabled
init|=
operator|(
name|boolean
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"SecurityEnabled"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|securityEnabled
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanode
operator|.
name|isSecurityEnabled
argument_list|()
argument_list|,
name|securityEnabled
argument_list|)
expr_stmt|;
block|}
comment|// setting back the authentication method
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|simpleConf
argument_list|)
expr_stmt|;
block|}
DECL|method|replaceDigits (final String s)
specifier|private
specifier|static
name|String
name|replaceDigits
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|replaceAll
argument_list|(
literal|"[0-9]+"
argument_list|,
literal|"_DIGITS_"
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testDataNodeMXBeanBlockSize ()
specifier|public
name|void
name|testDataNodeMXBeanBlockSize
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
try|try
init|(
name|MiniDFSCluster
name|cluster
init|=
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
init|)
block|{
name|DataNode
name|dn
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/foo"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
operator|+
literal|".txt"
argument_list|)
argument_list|,
literal|"test content"
argument_list|)
expr_stmt|;
block|}
name|DataNodeTestUtils
operator|.
name|triggerBlockReport
argument_list|(
name|dn
argument_list|)
expr_stmt|;
name|MBeanServer
name|mbs
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|ObjectName
name|mxbeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"Hadoop:service=DataNode,name=DataNodeInfo"
argument_list|)
decl_stmt|;
name|String
name|bpActorInfo
init|=
operator|(
name|String
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"BPServiceActorInfo"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|dn
operator|.
name|getBPServiceActorInfo
argument_list|()
argument_list|,
name|bpActorInfo
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"bpActorInfo is "
operator|+
name|bpActorInfo
argument_list|)
expr_stmt|;
name|TypeReference
argument_list|<
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|>
name|typeRef
init|=
operator|new
name|TypeReference
argument_list|<
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|>
argument_list|()
block|{}
decl_stmt|;
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|bpActorInfoList
init|=
operator|new
name|ObjectMapper
argument_list|()
operator|.
name|readValue
argument_list|(
name|bpActorInfo
argument_list|,
name|typeRef
argument_list|)
decl_stmt|;
name|int
name|maxDataLength
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|bpActorInfoList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"maxDataLength"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|confMaxDataLength
init|=
name|dn
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IPC_MAXIMUM_DATA_LENGTH
argument_list|,
name|CommonConfigurationKeys
operator|.
name|IPC_MAXIMUM_DATA_LENGTH_DEFAULT
argument_list|)
decl_stmt|;
name|int
name|maxBlockReportSize
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|bpActorInfoList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"maxBlockReportSize"
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"maxDataLength is "
operator|+
name|maxDataLength
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"maxBlockReportSize is "
operator|+
name|maxBlockReportSize
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"maxBlockReportSize should be greater than zero"
argument_list|,
name|maxBlockReportSize
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"maxDataLength should be exactly "
operator|+
literal|"the same value of ipc.maximum.data.length"
argument_list|,
name|confMaxDataLength
argument_list|,
name|maxDataLength
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDataNodeMXBeanBlockCount ()
specifier|public
name|void
name|testDataNodeMXBeanBlockCount
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|DataNode
argument_list|>
name|datanodes
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|datanodes
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|MBeanServer
name|mbs
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|ObjectName
name|mxbeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"Hadoop:service=DataNode,name=DataNodeInfo"
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/tmp.txt"
operator|+
name|i
argument_list|)
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Before restart DN"
argument_list|,
literal|5
argument_list|,
name|getTotalNumBlocks
argument_list|(
name|mbs
argument_list|,
name|mxbeanName
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartDataNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"After restart DN"
argument_list|,
literal|5
argument_list|,
name|getTotalNumBlocks
argument_list|(
name|mbs
argument_list|,
name|mxbeanName
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp.txt1"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// The total numBlocks should be updated after one file is deleted
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
try|try
block|{
return|return
name|getTotalNumBlocks
argument_list|(
name|mbs
argument_list|,
name|mxbeanName
argument_list|)
operator|==
literal|4
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
argument_list|,
literal|100
argument_list|,
literal|30000
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
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getTotalNumBlocks (MBeanServer mbs, ObjectName mxbeanName)
specifier|private
name|int
name|getTotalNumBlocks
parameter_list|(
name|MBeanServer
name|mbs
parameter_list|,
name|ObjectName
name|mxbeanName
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|totalBlocks
init|=
literal|0
decl_stmt|;
name|String
name|volumeInfo
init|=
operator|(
name|String
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"VolumeInfo"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|m
init|=
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|JSON
operator|.
name|parse
argument_list|(
name|volumeInfo
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|values
init|=
operator|(
name|Collection
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
operator|)
name|m
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|volumeInfoMap
range|:
name|values
control|)
block|{
name|totalBlocks
operator|+=
name|volumeInfoMap
operator|.
name|get
argument_list|(
literal|"numBlocks"
argument_list|)
expr_stmt|;
block|}
return|return
name|totalBlocks
return|;
block|}
annotation|@
name|Test
DECL|method|testDataNodeMXBeanSlowDisksEnabled ()
specifier|public
name|void
name|testDataNodeMXBeanSlowDisksEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FILEIO_PROFILING_SAMPLING_PERCENTAGE_KEY
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|DataNode
argument_list|>
name|datanodes
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanodes
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|DataNode
name|datanode
init|=
name|datanodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|slowDiskPath
init|=
literal|"test/data1/slowVolume"
decl_stmt|;
name|datanode
operator|.
name|getDiskMetrics
argument_list|()
operator|.
name|addSlowDiskForTesting
argument_list|(
name|slowDiskPath
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|MBeanServer
name|mbs
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|ObjectName
name|mxbeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"Hadoop:service=DataNode,name=DataNodeInfo"
argument_list|)
decl_stmt|;
name|String
name|slowDisks
init|=
operator|(
name|String
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"SlowDisks"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanode
operator|.
name|getSlowDisks
argument_list|()
argument_list|,
name|slowDisks
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|slowDisks
operator|.
name|contains
argument_list|(
name|slowDiskPath
argument_list|)
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
block|}
end_class

end_unit

