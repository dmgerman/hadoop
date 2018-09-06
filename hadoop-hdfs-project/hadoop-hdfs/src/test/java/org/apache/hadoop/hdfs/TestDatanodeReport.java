begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|MetricsAsserts
operator|.
name|assertGauge
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|getMetrics
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|client
operator|.
name|HdfsClientConfigKeys
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
name|DatanodeAdminProperties
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
name|ExtendedBlock
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
name|protocol
operator|.
name|LocatedBlock
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
name|CombinedHostFileManager
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
name|HostConfigManager
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
name|DataNode
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
name|DatanodeStorageReport
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
name|StorageReport
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
name|util
operator|.
name|HostsFileWriter
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

begin_comment
comment|/**  * This test ensures the all types of data node report work correctly.  */
end_comment

begin_class
DECL|class|TestDatanodeReport
specifier|public
class|class
name|TestDatanodeReport
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestDatanodeReport
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|final
specifier|static
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|field|NUM_OF_DATANODES
specifier|final
specifier|static
specifier|private
name|int
name|NUM_OF_DATANODES
init|=
literal|4
decl_stmt|;
comment|/**    * This test verifies upgrade domain is set according to the JSON host file.    */
annotation|@
name|Test
DECL|method|testDatanodeReportWithUpgradeDomain ()
specifier|public
name|void
name|testDatanodeReportWithUpgradeDomain
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
literal|500
argument_list|)
expr_stmt|;
comment|// 0.5s
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HOSTS_PROVIDER_CLASSNAME_KEY
argument_list|,
name|CombinedHostFileManager
operator|.
name|class
argument_list|,
name|HostConfigManager
operator|.
name|class
argument_list|)
expr_stmt|;
name|HostsFileWriter
name|hostsFileWriter
init|=
operator|new
name|HostsFileWriter
argument_list|()
decl_stmt|;
name|hostsFileWriter
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
literal|"temp/datanodeReport"
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
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|DFSClient
name|client
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|dfs
decl_stmt|;
specifier|final
name|String
name|ud1
init|=
literal|"ud1"
decl_stmt|;
specifier|final
name|String
name|ud2
init|=
literal|"ud2"
decl_stmt|;
try|try
block|{
comment|//wait until the cluster is up
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|DatanodeAdminProperties
name|datanode
init|=
operator|new
name|DatanodeAdminProperties
argument_list|()
decl_stmt|;
name|datanode
operator|.
name|setHostName
argument_list|(
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
name|getDatanodeId
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|datanode
operator|.
name|setUpgradeDomain
argument_list|(
name|ud1
argument_list|)
expr_stmt|;
name|hostsFileWriter
operator|.
name|initIncludeHosts
argument_list|(
operator|new
name|DatanodeAdminProperties
index|[]
block|{
name|datanode
block|}
argument_list|)
expr_stmt|;
name|client
operator|.
name|refreshNodes
argument_list|()
expr_stmt|;
name|DatanodeInfo
index|[]
name|all
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
name|all
index|[
literal|0
index|]
operator|.
name|getUpgradeDomain
argument_list|()
argument_list|,
name|ud1
argument_list|)
expr_stmt|;
name|datanode
operator|.
name|setUpgradeDomain
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|hostsFileWriter
operator|.
name|initIncludeHosts
argument_list|(
operator|new
name|DatanodeAdminProperties
index|[]
block|{
name|datanode
block|}
argument_list|)
expr_stmt|;
name|client
operator|.
name|refreshNodes
argument_list|()
expr_stmt|;
name|all
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
name|all
index|[
literal|0
index|]
operator|.
name|getUpgradeDomain
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|datanode
operator|.
name|setUpgradeDomain
argument_list|(
name|ud2
argument_list|)
expr_stmt|;
name|hostsFileWriter
operator|.
name|initIncludeHosts
argument_list|(
operator|new
name|DatanodeAdminProperties
index|[]
block|{
name|datanode
block|}
argument_list|)
expr_stmt|;
name|client
operator|.
name|refreshNodes
argument_list|()
expr_stmt|;
name|all
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
name|all
index|[
literal|0
index|]
operator|.
name|getUpgradeDomain
argument_list|()
argument_list|,
name|ud2
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * This test attempts to different types of datanode report.    */
annotation|@
name|Test
DECL|method|testDatanodeReport ()
specifier|public
name|void
name|testDatanodeReport
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
literal|500
argument_list|)
expr_stmt|;
comment|// 0.5s
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1L
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
name|numDataNodes
argument_list|(
name|NUM_OF_DATANODES
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
comment|//wait until the cluster is up
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
specifier|final
name|String
name|bpid
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
specifier|final
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
specifier|final
name|DFSClient
name|client
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|dfs
decl_stmt|;
name|assertReports
argument_list|(
name|NUM_OF_DATANODES
argument_list|,
name|DatanodeReportType
operator|.
name|ALL
argument_list|,
name|client
argument_list|,
name|datanodes
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
name|assertReports
argument_list|(
name|NUM_OF_DATANODES
argument_list|,
name|DatanodeReportType
operator|.
name|LIVE
argument_list|,
name|client
argument_list|,
name|datanodes
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
name|assertReports
argument_list|(
literal|0
argument_list|,
name|DatanodeReportType
operator|.
name|DEAD
argument_list|,
name|client
argument_list|,
name|datanodes
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
comment|// bring down one datanode
specifier|final
name|DataNode
name|last
init|=
name|datanodes
operator|.
name|get
argument_list|(
name|datanodes
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"XXX shutdown datanode "
operator|+
name|last
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
expr_stmt|;
name|last
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|DatanodeInfo
index|[]
name|nodeInfo
init|=
name|client
operator|.
name|datanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|DEAD
argument_list|)
decl_stmt|;
while|while
condition|(
name|nodeInfo
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{         }
name|nodeInfo
operator|=
name|client
operator|.
name|datanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|DEAD
argument_list|)
expr_stmt|;
block|}
name|assertReports
argument_list|(
name|NUM_OF_DATANODES
argument_list|,
name|DatanodeReportType
operator|.
name|ALL
argument_list|,
name|client
argument_list|,
name|datanodes
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertReports
argument_list|(
name|NUM_OF_DATANODES
operator|-
literal|1
argument_list|,
name|DatanodeReportType
operator|.
name|LIVE
argument_list|,
name|client
argument_list|,
name|datanodes
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertReports
argument_list|(
literal|1
argument_list|,
name|DatanodeReportType
operator|.
name|DEAD
argument_list|,
name|client
argument_list|,
name|datanodes
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"ExpiredHeartbeats"
argument_list|,
literal|1
argument_list|,
name|getMetrics
argument_list|(
literal|"FSNamesystem"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDatanodeReportMissingBlock ()
specifier|public
name|void
name|testDatanodeReportMissingBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|Retry
operator|.
name|WINDOW_BASE_KEY
argument_list|,
literal|1
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
name|numDataNodes
argument_list|(
name|NUM_OF_DATANODES
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
comment|// wait until the cluster is up
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/testDatanodeReportMissingBlock"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
operator|new
name|String
argument_list|(
literal|"testdata"
argument_list|)
argument_list|)
expr_stmt|;
name|LocatedBlock
name|lb
init|=
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|p
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|lb
operator|.
name|getLocations
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|ExtendedBlock
name|b
init|=
name|lb
operator|.
name|getBlock
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|corruptBlockOnDataNodesByDeletingBlockFile
argument_list|(
name|b
argument_list|)
expr_stmt|;
try|try
block|{
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Must throw exception as the block doesn't exists on disk"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// all bad datanodes
block|}
name|cluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
comment|// IBR delete ack
name|lb
operator|=
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|p
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|lb
operator|.
name|getLocations
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|CMP
specifier|final
specifier|static
name|Comparator
argument_list|<
name|StorageReport
argument_list|>
name|CMP
init|=
operator|new
name|Comparator
argument_list|<
name|StorageReport
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|StorageReport
name|left
parameter_list|,
name|StorageReport
name|right
parameter_list|)
block|{
return|return
name|left
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageID
argument_list|()
operator|.
name|compareTo
argument_list|(
name|right
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageID
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|assertReports (int numDatanodes, DatanodeReportType type, DFSClient client, List<DataNode> datanodes, String bpid)
specifier|static
name|void
name|assertReports
parameter_list|(
name|int
name|numDatanodes
parameter_list|,
name|DatanodeReportType
name|type
parameter_list|,
name|DFSClient
name|client
parameter_list|,
name|List
argument_list|<
name|DataNode
argument_list|>
name|datanodes
parameter_list|,
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DatanodeInfo
index|[]
name|infos
init|=
name|client
operator|.
name|datanodeReport
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numDatanodes
argument_list|,
name|infos
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|DatanodeStorageReport
index|[]
name|reports
init|=
name|client
operator|.
name|getDatanodeStorageReport
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numDatanodes
argument_list|,
name|reports
operator|.
name|length
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
name|infos
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|infos
index|[
name|i
index|]
argument_list|,
name|reports
index|[
name|i
index|]
operator|.
name|getDatanodeInfo
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|DataNode
name|d
init|=
name|findDatanode
argument_list|(
name|infos
index|[
name|i
index|]
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|,
name|datanodes
argument_list|)
decl_stmt|;
if|if
condition|(
name|bpid
operator|!=
literal|null
condition|)
block|{
comment|//check storage
specifier|final
name|StorageReport
index|[]
name|computed
init|=
name|reports
index|[
name|i
index|]
operator|.
name|getStorageReports
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|computed
argument_list|,
name|CMP
argument_list|)
expr_stmt|;
specifier|final
name|StorageReport
index|[]
name|expected
init|=
name|d
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getStorageReports
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|expected
argument_list|,
name|CMP
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|length
argument_list|,
name|computed
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|expected
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|expected
index|[
name|j
index|]
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageID
argument_list|()
argument_list|,
name|computed
index|[
name|j
index|]
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|findDatanode (String id, List<DataNode> datanodes)
specifier|static
name|DataNode
name|findDatanode
parameter_list|(
name|String
name|id
parameter_list|,
name|List
argument_list|<
name|DataNode
argument_list|>
name|datanodes
parameter_list|)
block|{
for|for
control|(
name|DataNode
name|d
range|:
name|datanodes
control|)
block|{
if|if
condition|(
name|d
operator|.
name|getDatanodeUuid
argument_list|()
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
name|d
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Datnode "
operator|+
name|id
operator|+
literal|" not in datanode list: "
operator|+
name|datanodes
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

