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
name|assertTrue
import|;
end_import

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
name|io
operator|.
name|File
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
name|protocol
operator|.
name|ExtendedBlock
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

begin_comment
comment|/**  * DFS_HOSTS and DFS_HOSTS_EXCLUDE tests  *   */
end_comment

begin_class
DECL|class|TestHostsFiles
specifier|public
class|class
name|TestHostsFiles
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestHostsFiles
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/*    * Return a configuration object with low timeouts for testing and     * a topology script set (which enables rack awareness).      */
DECL|method|getConf ()
specifier|private
name|Configuration
name|getConf
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|// Lower the heart beat interval so the NN quickly learns of dead
comment|// or decommissioned DNs and the NN issues replication and invalidation
comment|// commands quickly (as replies to heartbeats)
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
comment|// Have the NN ReplicationMonitor compute the replication and
comment|// invalidation commands to send DNs every second.
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Have the NN check for pending replications every second so it
comment|// quickly schedules additional replicas as they are identified.
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_PENDING_TIMEOUT_SEC_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// The DNs report blocks every second.
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INTERVAL_MSEC_KEY
argument_list|,
literal|1000L
argument_list|)
expr_stmt|;
comment|// Indicates we have multiple racks
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|NET_TOPOLOGY_SCRIPT_FILE_NAME_KEY
argument_list|,
literal|"xyz"
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Test
DECL|method|testHostsExcludeInUI ()
specifier|public
name|void
name|testHostsExcludeInUI
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
name|short
name|REPLICATION_FACTOR
init|=
literal|2
decl_stmt|;
specifier|final
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/testFile"
argument_list|)
decl_stmt|;
comment|// Configure an excludes file
name|FileSystem
name|localFileSys
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|workingDir
init|=
operator|new
name|Path
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
name|workingDir
argument_list|,
literal|"temp/decommission"
argument_list|)
decl_stmt|;
name|Path
name|excludeFile
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"exclude"
argument_list|)
decl_stmt|;
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
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|localFileSys
argument_list|,
name|excludeFile
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|localFileSys
argument_list|,
name|includeFile
argument_list|,
literal|""
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
comment|// Two blocks and four racks
name|String
name|racks
index|[]
init|=
block|{
literal|"/rack1"
block|,
literal|"/rack1"
block|,
literal|"/rack2"
block|,
literal|"/rack2"
block|}
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
name|numDataNodes
argument_list|(
name|racks
operator|.
name|length
argument_list|)
operator|.
name|racks
argument_list|(
name|racks
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|FSNamesystem
name|ns
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
try|try
block|{
comment|// Create a file with one block
specifier|final
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|,
literal|1L
argument_list|,
name|REPLICATION_FACTOR
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|ExtendedBlock
name|b
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|waitForReplication
argument_list|(
name|cluster
argument_list|,
name|b
argument_list|,
literal|2
argument_list|,
name|REPLICATION_FACTOR
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Decommission one of the hosts with the block, this should cause
comment|// the block to get replicated to another host on the same rack,
comment|// otherwise the rack policy is violated.
name|BlockLocation
name|locs
index|[]
init|=
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|filePath
argument_list|)
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|locs
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
name|String
name|names
init|=
name|name
operator|+
literal|"\n"
operator|+
literal|"localhost:42\n"
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"adding '"
operator|+
name|names
operator|+
literal|"' to exclude file "
operator|+
name|excludeFile
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|localFileSys
argument_list|,
name|excludeFile
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|ns
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|refreshNodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitForDecommission
argument_list|(
name|fs
argument_list|,
name|name
argument_list|)
expr_stmt|;
comment|// Check the block still has sufficient # replicas across racks
name|DFSTestUtil
operator|.
name|waitForReplication
argument_list|(
name|cluster
argument_list|,
name|b
argument_list|,
literal|2
argument_list|,
name|REPLICATION_FACTOR
argument_list|,
literal|0
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
literal|"Hadoop:service=NameNode,name=NameNodeInfo"
argument_list|)
decl_stmt|;
name|String
name|nodes
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
literal|"LiveNodes"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Live nodes should contain the decommissioned node"
argument_list|,
name|nodes
operator|.
name|contains
argument_list|(
literal|"Decommissioned"
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
if|if
condition|(
name|localFileSys
operator|.
name|exists
argument_list|(
name|dir
argument_list|)
condition|)
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
operator|new
name|File
argument_list|(
name|dir
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testHostsIncludeForDeadCount ()
specifier|public
name|void
name|testHostsIncludeForDeadCount
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
comment|// Configure an excludes file
name|FileSystem
name|localFileSys
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|workingDir
init|=
operator|new
name|Path
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
name|workingDir
argument_list|,
literal|"temp/decommission"
argument_list|)
decl_stmt|;
name|Path
name|excludeFile
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"exclude"
argument_list|)
decl_stmt|;
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
name|StringBuilder
name|includeHosts
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|includeHosts
operator|.
name|append
argument_list|(
literal|"localhost:52"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"127.0.0.1:7777"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|localFileSys
argument_list|,
name|excludeFile
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|localFileSys
argument_list|,
name|includeFile
argument_list|,
name|includeHosts
operator|.
name|toString
argument_list|()
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
specifier|final
name|FSNamesystem
name|ns
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|ns
operator|.
name|getNumDeadDataNodes
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ns
operator|.
name|getNumLiveDataNodes
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
comment|// Testing using MBeans
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
literal|"Hadoop:service=NameNode,name=FSNamesystemState"
argument_list|)
decl_stmt|;
name|String
name|nodes
init|=
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"NumDeadDataNodes"
argument_list|)
operator|+
literal|""
decl_stmt|;
name|assertTrue
argument_list|(
operator|(
name|Integer
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"NumDeadDataNodes"
argument_list|)
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
name|Integer
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"NumLiveDataNodes"
argument_list|)
operator|==
literal|0
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
if|if
condition|(
name|localFileSys
operator|.
name|exists
argument_list|(
name|dir
argument_list|)
condition|)
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
operator|new
name|File
argument_list|(
name|dir
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

