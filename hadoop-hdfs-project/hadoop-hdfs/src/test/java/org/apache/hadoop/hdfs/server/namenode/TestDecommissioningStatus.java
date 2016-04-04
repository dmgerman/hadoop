begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

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
name|io
operator|.
name|PrintStream
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
name|ArrayList
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
name|Iterator
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
name|Random
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
name|output
operator|.
name|ByteArrayOutputStream
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
name|BlockLocation
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
name|FSDataOutputStream
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
name|LocatedFileStatus
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
name|fs
operator|.
name|RemoteIterator
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
name|DFSClient
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
name|DistributedFileSystem
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
name|HdfsConfiguration
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
name|MiniDFSCluster
operator|.
name|DataNodeProperties
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
name|BlockManagerTestUtil
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
name|DatanodeDescriptor
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
name|blockmanagement
operator|.
name|DecommissionManager
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
name|datanode
operator|.
name|DataNodeTestUtils
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
name|tools
operator|.
name|DFSAdmin
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
name|apache
operator|.
name|log4j
operator|.
name|Logger
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

begin_comment
comment|/**  * This class tests the decommissioning of nodes.  */
end_comment

begin_class
DECL|class|TestDecommissioningStatus
specifier|public
class|class
name|TestDecommissioningStatus
block|{
DECL|field|seed
specifier|private
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0xDEADBEEFL
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|static
specifier|final
name|int
name|blockSize
init|=
literal|8192
decl_stmt|;
DECL|field|fileSize
specifier|private
specifier|static
specifier|final
name|int
name|fileSize
init|=
literal|16384
decl_stmt|;
DECL|field|numDatanodes
specifier|private
specifier|static
specifier|final
name|int
name|numDatanodes
init|=
literal|2
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fileSys
specifier|private
specifier|static
name|FileSystem
name|fileSys
decl_stmt|;
DECL|field|excludeFile
specifier|private
specifier|static
name|Path
name|excludeFile
decl_stmt|;
DECL|field|localFileSys
specifier|private
specifier|static
name|FileSystem
name|localFileSys
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|dir
specifier|private
specifier|static
name|Path
name|dir
decl_stmt|;
DECL|field|decommissionedNodes
specifier|final
name|ArrayList
argument_list|<
name|String
argument_list|>
name|decommissionedNodes
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|numDatanodes
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_CONSIDERLOAD_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Set up the hosts/exclude files.
name|localFileSys
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Path
name|workingDir
init|=
name|localFileSys
operator|.
name|getWorkingDirectory
argument_list|()
decl_stmt|;
name|dir
operator|=
operator|new
name|Path
argument_list|(
name|workingDir
argument_list|,
literal|"build/test/data/work-dir/decommission"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|localFileSys
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
name|excludeFile
operator|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"exclude"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HOSTS_EXCLUDE
argument_list|,
name|excludeFile
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|includeFile
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"include"
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HOSTS
argument_list|,
name|includeFile
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_PENDING_TIMEOUT_SEC_KEY
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_INTERVAL_KEY
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DECOMMISSION_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_BALANCE_BANDWIDTHPERSEC_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|writeConfigFile
argument_list|(
name|localFileSys
argument_list|,
name|excludeFile
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|writeConfigFile
argument_list|(
name|localFileSys
argument_list|,
name|includeFile
argument_list|,
literal|null
argument_list|)
expr_stmt|;
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
name|numDatanodes
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
name|fileSys
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
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
operator|.
name|setHeartbeatExpireInterval
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|Logger
operator|.
name|getLogger
argument_list|(
name|DecommissionManager
operator|.
name|class
argument_list|)
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|localFileSys
operator|!=
literal|null
condition|)
name|cleanupFile
argument_list|(
name|localFileSys
argument_list|,
name|dir
argument_list|)
expr_stmt|;
if|if
condition|(
name|fileSys
operator|!=
literal|null
condition|)
name|fileSys
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|writeConfigFile (FileSystem fs, Path name, ArrayList<String> nodes)
specifier|private
specifier|static
name|void
name|writeConfigFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|name
parameter_list|,
name|ArrayList
argument_list|<
name|String
argument_list|>
name|nodes
parameter_list|)
throws|throws
name|IOException
block|{
comment|// delete if it already exists
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|fs
operator|.
name|delete
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|FSDataOutputStream
name|stm
init|=
name|fs
operator|.
name|create
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|nodes
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|node
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|stm
operator|.
name|writeBytes
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|stm
operator|.
name|writeBytes
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|writeIncompleteFile (FileSystem fileSys, Path name, short repl)
specifier|private
name|FSDataOutputStream
name|writeIncompleteFile
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|,
name|short
name|repl
parameter_list|)
throws|throws
name|IOException
block|{
comment|// create and write a file that contains three blocks of data
name|FSDataOutputStream
name|stm
init|=
name|fileSys
operator|.
name|create
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|fileSys
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
literal|4096
argument_list|)
argument_list|,
name|repl
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|fileSize
index|]
decl_stmt|;
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|rand
operator|.
name|nextBytes
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
comment|// need to make sure that we actually write out both file blocks
comment|// (see FSOutputSummer#flush)
name|stm
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// Do not close stream, return it
comment|// so that it is not garbage collected
return|return
name|stm
return|;
block|}
DECL|method|cleanupFile (FileSystem fileSys, Path name)
specifier|static
specifier|private
name|void
name|cleanupFile
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|exists
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|fileSys
operator|.
name|delete
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|fileSys
operator|.
name|exists
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*    * Decommissions the node at the given index    */
DECL|method|decommissionNode (FSNamesystem namesystem, DFSClient client, FileSystem localFileSys, int nodeIndex)
specifier|private
name|String
name|decommissionNode
parameter_list|(
name|FSNamesystem
name|namesystem
parameter_list|,
name|DFSClient
name|client
parameter_list|,
name|FileSystem
name|localFileSys
parameter_list|,
name|int
name|nodeIndex
parameter_list|)
throws|throws
name|IOException
block|{
name|DatanodeInfo
index|[]
name|info
init|=
name|client
operator|.
name|datanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|LIVE
argument_list|)
decl_stmt|;
name|String
name|nodename
init|=
name|info
index|[
name|nodeIndex
index|]
operator|.
name|getXferAddr
argument_list|()
decl_stmt|;
name|decommissionNode
argument_list|(
name|namesystem
argument_list|,
name|localFileSys
argument_list|,
name|nodename
argument_list|)
expr_stmt|;
return|return
name|nodename
return|;
block|}
comment|/*    * Decommissions the node by name    */
DECL|method|decommissionNode (FSNamesystem namesystem, FileSystem localFileSys, String dnName)
specifier|private
name|void
name|decommissionNode
parameter_list|(
name|FSNamesystem
name|namesystem
parameter_list|,
name|FileSystem
name|localFileSys
parameter_list|,
name|String
name|dnName
parameter_list|)
throws|throws
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Decommissioning node: "
operator|+
name|dnName
argument_list|)
expr_stmt|;
comment|// write nodename into the exclude file.
name|ArrayList
argument_list|<
name|String
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|decommissionedNodes
argument_list|)
decl_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|dnName
argument_list|)
expr_stmt|;
name|writeConfigFile
argument_list|(
name|localFileSys
argument_list|,
name|excludeFile
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
block|}
DECL|method|checkDecommissionStatus (DatanodeDescriptor decommNode, int expectedUnderRep, int expectedDecommissionOnly, int expectedUnderRepInOpenFiles)
specifier|private
name|void
name|checkDecommissionStatus
parameter_list|(
name|DatanodeDescriptor
name|decommNode
parameter_list|,
name|int
name|expectedUnderRep
parameter_list|,
name|int
name|expectedDecommissionOnly
parameter_list|,
name|int
name|expectedUnderRepInOpenFiles
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Unexpected num under-replicated blocks"
argument_list|,
name|expectedUnderRep
argument_list|,
name|decommNode
operator|.
name|decommissioningStatus
operator|.
name|getUnderReplicatedBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected number of decom-only replicas"
argument_list|,
name|expectedDecommissionOnly
argument_list|,
name|decommNode
operator|.
name|decommissioningStatus
operator|.
name|getDecommissionOnlyReplicas
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected number of replicas in under-replicated open files"
argument_list|,
name|expectedUnderRepInOpenFiles
argument_list|,
name|decommNode
operator|.
name|decommissioningStatus
operator|.
name|getUnderReplicatedInOpenFiles
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkDFSAdminDecommissionStatus ( List<DatanodeDescriptor> expectedDecomm, DistributedFileSystem dfs, DFSAdmin admin)
specifier|private
name|void
name|checkDFSAdminDecommissionStatus
parameter_list|(
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|expectedDecomm
parameter_list|,
name|DistributedFileSystem
name|dfs
parameter_list|,
name|DFSAdmin
name|admin
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|ps
init|=
operator|new
name|PrintStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|PrintStream
name|oldOut
init|=
name|System
operator|.
name|out
decl_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|ps
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Parse DFSAdmin just to check the count
name|admin
operator|.
name|report
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-decommissioning"
block|}
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|String
index|[]
name|lines
init|=
name|baos
operator|.
name|toString
argument_list|()
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
decl_stmt|;
name|Integer
name|num
init|=
literal|null
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|lines
control|)
block|{
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"Decommissioning datanodes"
argument_list|)
condition|)
block|{
comment|// Pull out the "(num)" and parse it into an int
name|String
name|temp
init|=
name|line
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
index|[
literal|2
index|]
decl_stmt|;
name|num
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
operator|(
name|String
operator|)
name|temp
operator|.
name|subSequence
argument_list|(
literal|1
argument_list|,
name|temp
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|contains
argument_list|(
literal|"Decommission in progress"
argument_list|)
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"No decommissioning output"
argument_list|,
name|num
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected number of decomming DNs"
argument_list|,
name|expectedDecomm
operator|.
name|size
argument_list|()
argument_list|,
name|num
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected number of decomming DNs"
argument_list|,
name|expectedDecomm
operator|.
name|size
argument_list|()
argument_list|,
name|count
argument_list|)
expr_stmt|;
comment|// Check Java API for correct contents
name|List
argument_list|<
name|DatanodeInfo
argument_list|>
name|decomming
init|=
operator|new
name|ArrayList
argument_list|<
name|DatanodeInfo
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|dfs
operator|.
name|getDataNodeStats
argument_list|(
name|DatanodeReportType
operator|.
name|DECOMMISSIONING
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected number of decomming DNs"
argument_list|,
name|expectedDecomm
operator|.
name|size
argument_list|()
argument_list|,
name|decomming
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|DatanodeID
name|id
range|:
name|expectedDecomm
control|)
block|{
name|assertTrue
argument_list|(
literal|"Did not find expected decomming DN "
operator|+
name|id
argument_list|,
name|decomming
operator|.
name|contains
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|System
operator|.
name|setOut
argument_list|(
name|oldOut
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Tests Decommissioning Status in DFS.    */
annotation|@
name|Test
DECL|method|testDecommissionStatus ()
specifier|public
name|void
name|testDecommissionStatus
parameter_list|()
throws|throws
name|Exception
block|{
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
name|DatanodeInfo
index|[]
name|info
init|=
name|client
operator|.
name|datanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|LIVE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Number of Datanodes "
argument_list|,
literal|2
argument_list|,
name|info
operator|.
name|length
argument_list|)
expr_stmt|;
name|DistributedFileSystem
name|fileSys
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|DFSAdmin
name|admin
init|=
operator|new
name|DFSAdmin
argument_list|(
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|short
name|replicas
init|=
name|numDatanodes
decl_stmt|;
comment|//
comment|// Decommission one node. Verify the decommission status
comment|//
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
literal|"decommission.dat"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|,
name|fileSize
argument_list|,
name|fileSize
argument_list|,
name|blockSize
argument_list|,
name|replicas
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
literal|"decommission1.dat"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|st1
init|=
name|writeIncompleteFile
argument_list|(
name|fileSys
argument_list|,
name|file2
argument_list|,
name|replicas
argument_list|)
decl_stmt|;
for|for
control|(
name|DataNode
name|d
range|:
name|cluster
operator|.
name|getDataNodes
argument_list|()
control|)
block|{
name|DataNodeTestUtils
operator|.
name|triggerBlockReport
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|FSNamesystem
name|fsn
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
specifier|final
name|DatanodeManager
name|dm
init|=
name|fsn
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|iteration
init|=
literal|0
init|;
name|iteration
operator|<
name|numDatanodes
condition|;
name|iteration
operator|++
control|)
block|{
name|String
name|downnode
init|=
name|decommissionNode
argument_list|(
name|fsn
argument_list|,
name|client
argument_list|,
name|localFileSys
argument_list|,
name|iteration
argument_list|)
decl_stmt|;
name|dm
operator|.
name|refreshNodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|decommissionedNodes
operator|.
name|add
argument_list|(
name|downnode
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|recheckDecommissionState
argument_list|(
name|dm
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|decommissioningNodes
init|=
name|dm
operator|.
name|getDecommissioningNodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|iteration
operator|==
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|decommissioningNodes
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|DatanodeDescriptor
name|decommNode
init|=
name|decommissioningNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|checkDecommissionStatus
argument_list|(
name|decommNode
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|checkDFSAdminDecommissionStatus
argument_list|(
name|decommissioningNodes
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
name|fileSys
argument_list|,
name|admin
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|decommissioningNodes
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|DatanodeDescriptor
name|decommNode1
init|=
name|decommissioningNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|DatanodeDescriptor
name|decommNode2
init|=
name|decommissioningNodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// This one is still 3,3,1 since it passed over the UC block
comment|// earlier, before node 2 was decommed
name|checkDecommissionStatus
argument_list|(
name|decommNode1
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// This one is 4,4,2 since it has the full state
name|checkDecommissionStatus
argument_list|(
name|decommNode2
argument_list|,
literal|4
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|checkDFSAdminDecommissionStatus
argument_list|(
name|decommissioningNodes
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
name|fileSys
argument_list|,
name|admin
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Call refreshNodes on FSNamesystem with empty exclude file.
comment|// This will remove the datanodes from decommissioning list and
comment|// make them available again.
name|writeConfigFile
argument_list|(
name|localFileSys
argument_list|,
name|excludeFile
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|dm
operator|.
name|refreshNodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|st1
operator|.
name|close
argument_list|()
expr_stmt|;
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|)
expr_stmt|;
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|file2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify a DN remains in DECOMMISSION_INPROGRESS state if it is marked    * as dead before decommission has completed. That will allow DN to resume    * the replication process after it rejoins the cluster.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testDecommissionStatusAfterDNRestart ()
specifier|public
name|void
name|testDecommissionStatusAfterDNRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|DistributedFileSystem
name|fileSys
init|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// Create a file with one block. That block has one replica.
name|Path
name|f
init|=
operator|new
name|Path
argument_list|(
literal|"decommission.dat"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fileSys
argument_list|,
name|f
argument_list|,
name|fileSize
argument_list|,
name|fileSize
argument_list|,
name|fileSize
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// Find the DN that owns the only replica.
name|RemoteIterator
argument_list|<
name|LocatedFileStatus
argument_list|>
name|fileList
init|=
name|fileSys
operator|.
name|listLocatedStatus
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|BlockLocation
index|[]
name|blockLocations
init|=
name|fileList
operator|.
name|next
argument_list|()
operator|.
name|getBlockLocations
argument_list|()
decl_stmt|;
name|String
name|dnName
init|=
name|blockLocations
index|[
literal|0
index|]
operator|.
name|getNames
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
comment|// Decommission the DN.
name|FSNamesystem
name|fsn
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
specifier|final
name|DatanodeManager
name|dm
init|=
name|fsn
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
decl_stmt|;
name|decommissionNode
argument_list|(
name|fsn
argument_list|,
name|localFileSys
argument_list|,
name|dnName
argument_list|)
expr_stmt|;
name|dm
operator|.
name|refreshNodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// Stop the DN when decommission is in progress.
comment|// Given DFS_DATANODE_BALANCE_BANDWIDTHPERSEC_KEY is to 1 and the size of
comment|// the block, it will take much longer time that test timeout value for
comment|// the decommission to complete. So when stopDataNode is called,
comment|// decommission should be in progress.
name|DataNodeProperties
name|dataNodeProperties
init|=
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|dnName
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|dead
init|=
operator|new
name|ArrayList
argument_list|<
name|DatanodeDescriptor
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|dm
operator|.
name|fetchDatanodes
argument_list|(
literal|null
argument_list|,
name|dead
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|dead
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
comment|// Force removal of the dead node's blocks.
name|BlockManagerTestUtil
operator|.
name|checkHeartbeat
argument_list|(
name|fsn
operator|.
name|getBlockManager
argument_list|()
argument_list|)
expr_stmt|;
comment|// Force DatanodeManager to check decommission state.
name|BlockManagerTestUtil
operator|.
name|recheckDecommissionState
argument_list|(
name|dm
argument_list|)
expr_stmt|;
comment|// Verify that the DN remains in DECOMMISSION_INPROGRESS state.
name|assertTrue
argument_list|(
literal|"the node should be DECOMMISSION_IN_PROGRESSS"
argument_list|,
name|dead
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isDecommissionInProgress
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check DatanodeManager#getDecommissionNodes, make sure it returns
comment|// the node as decommissioning, even if it's dead
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|decomlist
init|=
name|dm
operator|.
name|getDecommissioningNodes
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"The node should be be decommissioning"
argument_list|,
name|decomlist
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
comment|// Delete the under-replicated file, which should let the
comment|// DECOMMISSION_IN_PROGRESS node become DECOMMISSIONED
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|recheckDecommissionState
argument_list|(
name|dm
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"the node should be decommissioned"
argument_list|,
name|dead
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isDecommissioned
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add the node back
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dataNodeProperties
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// Call refreshNodes on FSNamesystem with empty exclude file.
comment|// This will remove the datanodes from decommissioning list and
comment|// make them available again.
name|writeConfigFile
argument_list|(
name|localFileSys
argument_list|,
name|excludeFile
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|dm
operator|.
name|refreshNodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify the support for decommissioning a datanode that is already dead.    * Under this scenario the datanode should immediately be marked as    * DECOMMISSIONED    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testDecommissionDeadDN ()
specifier|public
name|void
name|testDecommissionDeadDN
parameter_list|()
throws|throws
name|Exception
block|{
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|DecommissionManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|log
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
name|DatanodeID
name|dnID
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
name|getDatanodeId
argument_list|()
decl_stmt|;
name|String
name|dnName
init|=
name|dnID
operator|.
name|getXferAddr
argument_list|()
decl_stmt|;
name|DataNodeProperties
name|stoppedDN
init|=
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|waitForDatanodeState
argument_list|(
name|cluster
argument_list|,
name|dnID
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
name|FSNamesystem
name|fsn
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
specifier|final
name|DatanodeManager
name|dm
init|=
name|fsn
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
decl_stmt|;
name|DatanodeDescriptor
name|dnDescriptor
init|=
name|dm
operator|.
name|getDatanode
argument_list|(
name|dnID
argument_list|)
decl_stmt|;
name|decommissionNode
argument_list|(
name|fsn
argument_list|,
name|localFileSys
argument_list|,
name|dnName
argument_list|)
expr_stmt|;
name|dm
operator|.
name|refreshNodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|recheckDecommissionState
argument_list|(
name|dm
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dnDescriptor
operator|.
name|isDecommissioned
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add the node back
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|stoppedDN
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// Call refreshNodes on FSNamesystem with empty exclude file to remove the
comment|// datanode from decommissioning list and make it available again.
name|writeConfigFile
argument_list|(
name|localFileSys
argument_list|,
name|excludeFile
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|dm
operator|.
name|refreshNodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

