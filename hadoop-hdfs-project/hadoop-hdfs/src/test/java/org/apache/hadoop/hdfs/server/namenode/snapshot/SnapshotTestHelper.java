begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.snapshot
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
operator|.
name|snapshot
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
name|BufferedReader
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
name|java
operator|.
name|io
operator|.
name|FileReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
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
name|PrintWriter
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
name|fs
operator|.
name|FileStatus
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
name|fs
operator|.
name|UnresolvedLinkException
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
name|server
operator|.
name|blockmanagement
operator|.
name|BlockInfoContiguous
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
name|BlockInfoContiguousUnderConstruction
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
name|BlockManager
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
name|BlockPoolSliceStorage
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
name|BlockScanner
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
name|DirectoryScanner
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
name|namenode
operator|.
name|FSDirectory
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
name|namenode
operator|.
name|FSNamesystem
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
name|namenode
operator|.
name|INode
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
name|namenode
operator|.
name|INodeDirectory
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
name|namenode
operator|.
name|INodeFile
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
name|namenode
operator|.
name|LeaseManager
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
name|namenode
operator|.
name|NameNode
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
name|http
operator|.
name|HttpServer2
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
name|ProtobufRpcEngine
operator|.
name|Server
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
name|impl
operator|.
name|MetricsSystemImpl
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

begin_comment
comment|/**  * Helper for writing snapshot related tests  */
end_comment

begin_class
DECL|class|SnapshotTestHelper
specifier|public
class|class
name|SnapshotTestHelper
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
name|SnapshotTestHelper
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Disable the logs that are not very useful for snapshot related tests. */
DECL|method|disableLogs ()
specifier|public
specifier|static
name|void
name|disableLogs
parameter_list|()
block|{
specifier|final
name|String
index|[]
name|lognames
init|=
block|{
literal|"org.apache.hadoop.hdfs.server.datanode.BlockPoolSliceScanner"
block|,
literal|"org.apache.hadoop.hdfs.server.datanode.fsdataset.impl.FsDatasetImpl"
block|,
literal|"org.apache.hadoop.hdfs.server.datanode.fsdataset.impl.FsDatasetAsyncDiskService"
block|,     }
decl_stmt|;
for|for
control|(
name|String
name|n
range|:
name|lognames
control|)
block|{
name|GenericTestUtils
operator|.
name|disableLog
argument_list|(
name|LogFactory
operator|.
name|getLog
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|GenericTestUtils
operator|.
name|disableLog
argument_list|(
name|LogFactory
operator|.
name|getLog
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|disableLog
argument_list|(
name|LogFactory
operator|.
name|getLog
argument_list|(
name|BlockManager
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|disableLog
argument_list|(
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|disableLog
argument_list|(
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DirectoryScanner
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|disableLog
argument_list|(
name|LogFactory
operator|.
name|getLog
argument_list|(
name|MetricsSystemImpl
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|disableLog
argument_list|(
name|BlockScanner
operator|.
name|LOG
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|disableLog
argument_list|(
name|HttpServer2
operator|.
name|LOG
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|disableLog
argument_list|(
name|DataNode
operator|.
name|LOG
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|disableLog
argument_list|(
name|BlockPoolSliceStorage
operator|.
name|LOG
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|disableLog
argument_list|(
name|LeaseManager
operator|.
name|LOG
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|disableLog
argument_list|(
name|NameNode
operator|.
name|stateChangeLog
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|disableLog
argument_list|(
name|NameNode
operator|.
name|blockStateChangeLog
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|disableLog
argument_list|(
name|DFSClient
operator|.
name|LOG
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|disableLog
argument_list|(
name|Server
operator|.
name|LOG
argument_list|)
expr_stmt|;
block|}
DECL|method|SnapshotTestHelper ()
specifier|private
name|SnapshotTestHelper
parameter_list|()
block|{
comment|// Cannot be instantinatied
block|}
DECL|method|getSnapshotRoot (Path snapshottedDir, String snapshotName)
specifier|public
specifier|static
name|Path
name|getSnapshotRoot
parameter_list|(
name|Path
name|snapshottedDir
parameter_list|,
name|String
name|snapshotName
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|snapshottedDir
argument_list|,
name|HdfsConstants
operator|.
name|DOT_SNAPSHOT_DIR
operator|+
literal|"/"
operator|+
name|snapshotName
argument_list|)
return|;
block|}
DECL|method|getSnapshotPath (Path snapshottedDir, String snapshotName, String fileLocalName)
specifier|public
specifier|static
name|Path
name|getSnapshotPath
parameter_list|(
name|Path
name|snapshottedDir
parameter_list|,
name|String
name|snapshotName
parameter_list|,
name|String
name|fileLocalName
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|getSnapshotRoot
argument_list|(
name|snapshottedDir
argument_list|,
name|snapshotName
argument_list|)
argument_list|,
name|fileLocalName
argument_list|)
return|;
block|}
comment|/**    * Create snapshot for a dir using a given snapshot name    *     * @param hdfs DistributedFileSystem instance    * @param snapshotRoot The dir to be snapshotted    * @param snapshotName The name of the snapshot    * @return The path of the snapshot root    */
DECL|method|createSnapshot (DistributedFileSystem hdfs, Path snapshotRoot, String snapshotName)
specifier|public
specifier|static
name|Path
name|createSnapshot
parameter_list|(
name|DistributedFileSystem
name|hdfs
parameter_list|,
name|Path
name|snapshotRoot
parameter_list|,
name|String
name|snapshotName
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"createSnapshot "
operator|+
name|snapshotName
operator|+
literal|" for "
operator|+
name|snapshotRoot
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hdfs
operator|.
name|exists
argument_list|(
name|snapshotRoot
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|snapshotRoot
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|snapshotRoot
argument_list|,
name|snapshotName
argument_list|)
expr_stmt|;
comment|// set quota to a large value for testing counts
name|hdfs
operator|.
name|setQuota
argument_list|(
name|snapshotRoot
argument_list|,
name|Long
operator|.
name|MAX_VALUE
operator|-
literal|1
argument_list|,
name|Long
operator|.
name|MAX_VALUE
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|SnapshotTestHelper
operator|.
name|getSnapshotRoot
argument_list|(
name|snapshotRoot
argument_list|,
name|snapshotName
argument_list|)
return|;
block|}
comment|/**    * Check the functionality of a snapshot.    *     * @param hdfs DistributedFileSystem instance    * @param snapshotRoot The root of the snapshot    * @param snapshottedDir The snapshotted directory    */
DECL|method|checkSnapshotCreation (DistributedFileSystem hdfs, Path snapshotRoot, Path snapshottedDir)
specifier|public
specifier|static
name|void
name|checkSnapshotCreation
parameter_list|(
name|DistributedFileSystem
name|hdfs
parameter_list|,
name|Path
name|snapshotRoot
parameter_list|,
name|Path
name|snapshottedDir
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Currently we only check if the snapshot was created successfully
name|assertTrue
argument_list|(
name|hdfs
operator|.
name|exists
argument_list|(
name|snapshotRoot
argument_list|)
argument_list|)
expr_stmt|;
comment|// Compare the snapshot with the current dir
name|FileStatus
index|[]
name|currentFiles
init|=
name|hdfs
operator|.
name|listStatus
argument_list|(
name|snapshottedDir
argument_list|)
decl_stmt|;
name|FileStatus
index|[]
name|snapshotFiles
init|=
name|hdfs
operator|.
name|listStatus
argument_list|(
name|snapshotRoot
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"snapshottedDir="
operator|+
name|snapshottedDir
operator|+
literal|", snapshotRoot="
operator|+
name|snapshotRoot
argument_list|,
name|currentFiles
operator|.
name|length
argument_list|,
name|snapshotFiles
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Compare two dumped trees that are stored in two files. The following is an    * example of the dumped tree:    *     *<pre>    * information of root    * +- the first child of root (e.g., /foo)    *   +- the first child of /foo    *   ...    *   \- the last child of /foo (e.g., /foo/bar)    *     +- the first child of /foo/bar    *     ...    *   snapshots of /foo    *   +- snapshot s_1    *   ...    *   \- snapshot s_n    * +- second child of root    *   ...    * \- last child of root    *     * The following information is dumped for each inode:    * localName (className@hashCode) parent permission group user    *     * Specific information for different types of INode:     * {@link INodeDirectory}:childrenSize     * {@link INodeFile}: fileSize, block list. Check {@link BlockInfoContiguous#toString()}    * and {@link BlockInfoContiguousUnderConstruction#toString()} for detailed information.    * {@link FileWithSnapshot}: next link    *</pre>    * @see INode#dumpTreeRecursively()    */
DECL|method|compareDumpedTreeInFile (File file1, File file2, boolean compareQuota)
specifier|public
specifier|static
name|void
name|compareDumpedTreeInFile
parameter_list|(
name|File
name|file1
parameter_list|,
name|File
name|file2
parameter_list|,
name|boolean
name|compareQuota
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|compareDumpedTreeInFile
argument_list|(
name|file1
argument_list|,
name|file2
argument_list|,
name|compareQuota
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"FAILED compareDumpedTreeInFile("
operator|+
name|file1
operator|+
literal|", "
operator|+
name|file2
operator|+
literal|")"
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|compareDumpedTreeInFile
argument_list|(
name|file1
argument_list|,
name|file2
argument_list|,
name|compareQuota
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|compareDumpedTreeInFile (File file1, File file2, boolean compareQuota, boolean print)
specifier|private
specifier|static
name|void
name|compareDumpedTreeInFile
parameter_list|(
name|File
name|file1
parameter_list|,
name|File
name|file2
parameter_list|,
name|boolean
name|compareQuota
parameter_list|,
name|boolean
name|print
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|print
condition|)
block|{
name|printFile
argument_list|(
name|file1
argument_list|)
expr_stmt|;
name|printFile
argument_list|(
name|file2
argument_list|)
expr_stmt|;
block|}
name|BufferedReader
name|reader1
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file1
argument_list|)
argument_list|)
decl_stmt|;
name|BufferedReader
name|reader2
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file2
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|line1
init|=
literal|""
decl_stmt|;
name|String
name|line2
init|=
literal|""
decl_stmt|;
while|while
condition|(
operator|(
name|line1
operator|=
name|reader1
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
operator|&&
operator|(
name|line2
operator|=
name|reader2
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|print
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"1) "
operator|+
name|line1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"2) "
operator|+
name|line2
argument_list|)
expr_stmt|;
block|}
comment|// skip the hashCode part of the object string during the comparison,
comment|// also ignore the difference between INodeFile/INodeFileWithSnapshot
name|line1
operator|=
name|line1
operator|.
name|replaceAll
argument_list|(
literal|"INodeFileWithSnapshot"
argument_list|,
literal|"INodeFile"
argument_list|)
expr_stmt|;
name|line2
operator|=
name|line2
operator|.
name|replaceAll
argument_list|(
literal|"INodeFileWithSnapshot"
argument_list|,
literal|"INodeFile"
argument_list|)
expr_stmt|;
name|line1
operator|=
name|line1
operator|.
name|replaceAll
argument_list|(
literal|"@[\\dabcdef]+"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|line2
operator|=
name|line2
operator|.
name|replaceAll
argument_list|(
literal|"@[\\dabcdef]+"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// skip the replica field of the last block of an
comment|// INodeFileUnderConstruction
name|line1
operator|=
name|line1
operator|.
name|replaceAll
argument_list|(
literal|"replicas=\\[.*\\]"
argument_list|,
literal|"replicas=[]"
argument_list|)
expr_stmt|;
name|line2
operator|=
name|line2
operator|.
name|replaceAll
argument_list|(
literal|"replicas=\\[.*\\]"
argument_list|,
literal|"replicas=[]"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|compareQuota
condition|)
block|{
name|line1
operator|=
name|line1
operator|.
name|replaceAll
argument_list|(
literal|"Quota\\[.*\\]"
argument_list|,
literal|"Quota[]"
argument_list|)
expr_stmt|;
name|line2
operator|=
name|line2
operator|.
name|replaceAll
argument_list|(
literal|"Quota\\[.*\\]"
argument_list|,
literal|"Quota[]"
argument_list|)
expr_stmt|;
block|}
comment|// skip the specific fields of BlockInfoUnderConstruction when the node
comment|// is an INodeFileSnapshot or an INodeFileUnderConstructionSnapshot
if|if
condition|(
name|line1
operator|.
name|contains
argument_list|(
literal|"(INodeFileSnapshot)"
argument_list|)
operator|||
name|line1
operator|.
name|contains
argument_list|(
literal|"(INodeFileUnderConstructionSnapshot)"
argument_list|)
condition|)
block|{
name|line1
operator|=
name|line1
operator|.
name|replaceAll
argument_list|(
literal|"\\{blockUCState=\\w+, primaryNodeIndex=[-\\d]+, replicas=\\[\\]\\}"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|line2
operator|=
name|line2
operator|.
name|replaceAll
argument_list|(
literal|"\\{blockUCState=\\w+, primaryNodeIndex=[-\\d]+, replicas=\\[\\]\\}"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|line1
argument_list|,
name|line2
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertNull
argument_list|(
name|reader1
operator|.
name|readLine
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|reader2
operator|.
name|readLine
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reader1
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|printFile (File f)
specifier|static
name|void
name|printFile
parameter_list|(
name|File
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"File: "
operator|+
name|f
argument_list|)
expr_stmt|;
name|BufferedReader
name|in
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|f
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|String
name|line
init|;
operator|(
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|;
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|dumpTree2File (FSDirectory fsdir, File f)
specifier|public
specifier|static
name|void
name|dumpTree2File
parameter_list|(
name|FSDirectory
name|fsdir
parameter_list|,
name|File
name|f
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|f
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|fsdir
operator|.
name|getINode
argument_list|(
literal|"/"
argument_list|)
operator|.
name|dumpTreeRecursively
argument_list|(
name|out
argument_list|,
operator|new
name|StringBuilder
argument_list|()
argument_list|,
name|Snapshot
operator|.
name|CURRENT_STATE_ID
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Generate the path for a snapshot file.    *     * @param snapshotRoot of format    *          {@literal<snapshottble_dir>/.snapshot/<snapshot_name>}    * @param file path to a file    * @return The path of the snapshot of the file assuming the file has a    *         snapshot under the snapshot root of format    *         {@literal<snapshottble_dir>/.snapshot/<snapshot_name>/<path_to_file_inside_snapshot>}    *         . Null if the file is not under the directory associated with the    *         snapshot root.    */
DECL|method|getSnapshotFile (Path snapshotRoot, Path file)
specifier|static
name|Path
name|getSnapshotFile
parameter_list|(
name|Path
name|snapshotRoot
parameter_list|,
name|Path
name|file
parameter_list|)
block|{
name|Path
name|rootParent
init|=
name|snapshotRoot
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|rootParent
operator|!=
literal|null
operator|&&
name|rootParent
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|".snapshot"
argument_list|)
condition|)
block|{
name|Path
name|snapshotDir
init|=
name|rootParent
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|snapshotDir
operator|.
name|toString
argument_list|()
argument_list|)
operator|&&
operator|!
name|file
operator|.
name|equals
argument_list|(
name|snapshotDir
argument_list|)
condition|)
block|{
name|String
name|fileName
init|=
name|file
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
name|snapshotDir
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|Path
name|snapshotFile
init|=
operator|new
name|Path
argument_list|(
name|snapshotRoot
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
return|return
name|snapshotFile
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * A class creating directories trees for snapshot testing. For simplicity,    * the directory tree is a binary tree, i.e., each directory has two children    * as snapshottable directories.    */
DECL|class|TestDirectoryTree
specifier|static
class|class
name|TestDirectoryTree
block|{
comment|/** Height of the directory tree */
DECL|field|height
specifier|final
name|int
name|height
decl_stmt|;
comment|/** Top node of the directory tree */
DECL|field|topNode
specifier|final
name|Node
name|topNode
decl_stmt|;
comment|/** A map recording nodes for each tree level */
DECL|field|levelMap
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|ArrayList
argument_list|<
name|Node
argument_list|>
argument_list|>
name|levelMap
decl_stmt|;
comment|/**      * Constructor to build a tree of given {@code height}      */
DECL|method|TestDirectoryTree (int height, FileSystem fs)
name|TestDirectoryTree
parameter_list|(
name|int
name|height
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|height
operator|=
name|height
expr_stmt|;
name|this
operator|.
name|topNode
operator|=
operator|new
name|Node
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/TestSnapshot"
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|this
operator|.
name|levelMap
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|ArrayList
argument_list|<
name|Node
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|addDirNode
argument_list|(
name|topNode
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|genChildren
argument_list|(
name|topNode
argument_list|,
name|height
operator|-
literal|1
argument_list|,
name|fs
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add a node into the levelMap      */
DECL|method|addDirNode (Node node, int atLevel)
specifier|private
name|void
name|addDirNode
parameter_list|(
name|Node
name|node
parameter_list|,
name|int
name|atLevel
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|Node
argument_list|>
name|list
init|=
name|levelMap
operator|.
name|get
argument_list|(
name|atLevel
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
operator|new
name|ArrayList
argument_list|<
name|Node
argument_list|>
argument_list|()
expr_stmt|;
name|levelMap
operator|.
name|put
argument_list|(
name|atLevel
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
DECL|field|id
name|int
name|id
init|=
literal|0
decl_stmt|;
comment|/**      * Recursively generate the tree based on the height.      *       * @param parent The parent node      * @param level The remaining levels to generate      * @param fs The FileSystem where to generate the files/dirs      * @throws Exception      */
DECL|method|genChildren (Node parent, int level, FileSystem fs)
specifier|private
name|void
name|genChildren
parameter_list|(
name|Node
name|parent
parameter_list|,
name|int
name|level
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|level
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|parent
operator|.
name|leftChild
operator|=
operator|new
name|Node
argument_list|(
operator|new
name|Path
argument_list|(
name|parent
operator|.
name|nodePath
argument_list|,
literal|"left"
operator|+
operator|++
name|id
argument_list|)
argument_list|,
name|height
operator|-
name|level
argument_list|,
name|parent
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|parent
operator|.
name|rightChild
operator|=
operator|new
name|Node
argument_list|(
operator|new
name|Path
argument_list|(
name|parent
operator|.
name|nodePath
argument_list|,
literal|"right"
operator|+
operator|++
name|id
argument_list|)
argument_list|,
name|height
operator|-
name|level
argument_list|,
name|parent
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|addDirNode
argument_list|(
name|parent
operator|.
name|leftChild
argument_list|,
name|parent
operator|.
name|leftChild
operator|.
name|level
argument_list|)
expr_stmt|;
name|addDirNode
argument_list|(
name|parent
operator|.
name|rightChild
argument_list|,
name|parent
operator|.
name|rightChild
operator|.
name|level
argument_list|)
expr_stmt|;
name|genChildren
argument_list|(
name|parent
operator|.
name|leftChild
argument_list|,
name|level
operator|-
literal|1
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|genChildren
argument_list|(
name|parent
operator|.
name|rightChild
argument_list|,
name|level
operator|-
literal|1
argument_list|,
name|fs
argument_list|)
expr_stmt|;
block|}
comment|/**      * Randomly retrieve a node from the directory tree.      *       * @param random A random instance passed by user.      * @param excludedList Excluded list, i.e., the randomly generated node      *          cannot be one of the nodes in this list.      * @return a random node from the tree.      */
DECL|method|getRandomDirNode (Random random, List<Node> excludedList)
name|Node
name|getRandomDirNode
parameter_list|(
name|Random
name|random
parameter_list|,
name|List
argument_list|<
name|Node
argument_list|>
name|excludedList
parameter_list|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|level
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|height
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|Node
argument_list|>
name|levelList
init|=
name|levelMap
operator|.
name|get
argument_list|(
name|level
argument_list|)
decl_stmt|;
name|int
name|index
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|levelList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|Node
name|randomNode
init|=
name|levelList
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|excludedList
operator|==
literal|null
operator|||
operator|!
name|excludedList
operator|.
name|contains
argument_list|(
name|randomNode
argument_list|)
condition|)
block|{
return|return
name|randomNode
return|;
block|}
block|}
block|}
comment|/**      * The class representing a node in {@link TestDirectoryTree}.      *<br>      * This contains:      *<ul>      *<li>Two children representing the two snapshottable directories</li>      *<li>A list of files for testing, so that we can check snapshots      * after file creation/deletion/modification.</li>      *<li>A list of non-snapshottable directories, to test snapshots with      * directory creation/deletion. Note that this is needed because the      * deletion of a snapshottale directory with snapshots is not allowed.</li>      *</ul>      */
DECL|class|Node
specifier|static
class|class
name|Node
block|{
comment|/** The level of this node in the directory tree */
DECL|field|level
specifier|final
name|int
name|level
decl_stmt|;
comment|/** Children */
DECL|field|leftChild
name|Node
name|leftChild
decl_stmt|;
DECL|field|rightChild
name|Node
name|rightChild
decl_stmt|;
comment|/** Parent node of the node */
DECL|field|parent
specifier|final
name|Node
name|parent
decl_stmt|;
comment|/** File path of the node */
DECL|field|nodePath
specifier|final
name|Path
name|nodePath
decl_stmt|;
comment|/**        * The file path list for testing snapshots before/after file        * creation/deletion/modification        */
DECL|field|fileList
name|ArrayList
argument_list|<
name|Path
argument_list|>
name|fileList
decl_stmt|;
comment|/**        * Each time for testing snapshots with file creation, since we do not        * want to insert new files into the fileList, we always create the file        * that was deleted last time. Thus we record the index for deleted file        * in the fileList, and roll the file modification forward in the list.        */
DECL|field|nullFileIndex
name|int
name|nullFileIndex
init|=
literal|0
decl_stmt|;
comment|/**        * A list of non-snapshottable directories for testing snapshots with        * directory creation/deletion        */
DECL|field|nonSnapshotChildren
specifier|final
name|ArrayList
argument_list|<
name|Node
argument_list|>
name|nonSnapshotChildren
decl_stmt|;
DECL|method|Node (Path path, int level, Node parent, FileSystem fs)
name|Node
parameter_list|(
name|Path
name|path
parameter_list|,
name|int
name|level
parameter_list|,
name|Node
name|parent
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|nodePath
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|nonSnapshotChildren
operator|=
operator|new
name|ArrayList
argument_list|<
name|Node
argument_list|>
argument_list|()
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|nodePath
argument_list|)
expr_stmt|;
block|}
comment|/**        * Create files and add them in the fileList. Initially the last element        * in the fileList is set to null (where we start file creation).        */
DECL|method|initFileList (FileSystem fs, String namePrefix, long fileLen, short replication, long seed, int numFiles)
name|void
name|initFileList
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|namePrefix
parameter_list|,
name|long
name|fileLen
parameter_list|,
name|short
name|replication
parameter_list|,
name|long
name|seed
parameter_list|,
name|int
name|numFiles
parameter_list|)
throws|throws
name|Exception
block|{
name|fileList
operator|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|(
name|numFiles
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
name|numFiles
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|nodePath
argument_list|,
name|namePrefix
operator|+
literal|"-f"
operator|+
name|i
argument_list|)
decl_stmt|;
name|fileList
operator|.
name|add
argument_list|(
name|file
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|numFiles
operator|-
literal|1
condition|)
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file
argument_list|,
name|fileLen
argument_list|,
name|replication
argument_list|,
name|seed
argument_list|)
expr_stmt|;
block|}
block|}
name|nullFileIndex
operator|=
name|numFiles
operator|-
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|!=
literal|null
operator|&&
name|o
operator|instanceof
name|Node
condition|)
block|{
name|Node
name|node
init|=
operator|(
name|Node
operator|)
name|o
decl_stmt|;
return|return
name|node
operator|.
name|nodePath
operator|.
name|equals
argument_list|(
name|nodePath
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|nodePath
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
block|}
DECL|method|dumpTree (String message, MiniDFSCluster cluster )
specifier|public
specifier|static
name|void
name|dumpTree
parameter_list|(
name|String
name|message
parameter_list|,
name|MiniDFSCluster
name|cluster
parameter_list|)
throws|throws
name|UnresolvedLinkException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"XXX "
operator|+
name|message
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|getINode
argument_list|(
literal|"/"
argument_list|)
operator|.
name|dumpTreeRecursively
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

