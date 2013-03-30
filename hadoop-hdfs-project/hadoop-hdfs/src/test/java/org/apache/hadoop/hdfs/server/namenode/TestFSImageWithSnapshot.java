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
name|IOException
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
name|EnumSet
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
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|permission
operator|.
name|FsPermission
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
name|client
operator|.
name|HdfsDataOutputStream
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
name|HdfsDataOutputStream
operator|.
name|SyncFlag
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
name|SafeModeAction
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
name|SnapshottableDirectoryStatus
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
name|NNStorage
operator|.
name|NameNodeFile
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
name|snapshot
operator|.
name|SnapshotTestHelper
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
name|Canceler
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

begin_comment
comment|/**  * Test FSImage save/load when Snapshot is supported  */
end_comment

begin_class
DECL|class|TestFSImageWithSnapshot
specifier|public
class|class
name|TestFSImageWithSnapshot
block|{
block|{
name|SnapshotTestHelper
operator|.
name|disableLogs
parameter_list|()
constructor_decl|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|INode
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
DECL|field|seed
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0
decl_stmt|;
DECL|field|REPLICATION
specifier|static
specifier|final
name|short
name|REPLICATION
init|=
literal|3
decl_stmt|;
DECL|field|BLOCKSIZE
specifier|static
specifier|final
name|int
name|BLOCKSIZE
init|=
literal|1024
decl_stmt|;
DECL|field|txid
specifier|static
specifier|final
name|long
name|txid
init|=
literal|1
decl_stmt|;
DECL|field|dir
specifier|private
specifier|final
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/TestSnapshot"
argument_list|)
decl_stmt|;
DECL|field|testDir
specifier|private
specifier|static
name|String
name|testDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"build/test/data"
argument_list|)
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fsn
name|FSNamesystem
name|fsn
decl_stmt|;
DECL|field|hdfs
name|DistributedFileSystem
name|hdfs
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
name|Configuration
argument_list|()
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
name|REPLICATION
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
name|fsn
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
name|hdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
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
comment|/**    * Create a temp fsimage file for testing.    * @param dir The directory where the fsimage file resides    * @param imageTxId The transaction id of the fsimage    * @return The file of the image file    */
DECL|method|getImageFile (String dir, long imageTxId)
specifier|private
name|File
name|getImageFile
parameter_list|(
name|String
name|dir
parameter_list|,
name|long
name|imageTxId
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"%s_%019d"
argument_list|,
name|NameNodeFile
operator|.
name|IMAGE
argument_list|,
name|imageTxId
argument_list|)
argument_list|)
return|;
block|}
comment|/**     * Create a temp file for dumping the fsdir    * @param dir directory for the temp file    * @param suffix suffix of of the temp file    * @return the temp file    */
DECL|method|getDumpTreeFile (String dir, String suffix)
specifier|private
name|File
name|getDumpTreeFile
parameter_list|(
name|String
name|dir
parameter_list|,
name|String
name|suffix
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"dumpTree_%s"
argument_list|,
name|suffix
argument_list|)
argument_list|)
return|;
block|}
comment|/**     * Dump the fsdir tree to a temp file    * @param fileSuffix suffix of the temp file for dumping    * @return the temp file    */
DECL|method|dumpTree2File (String fileSuffix)
specifier|private
name|File
name|dumpTree2File
parameter_list|(
name|String
name|fileSuffix
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
name|getDumpTreeFile
argument_list|(
name|testDir
argument_list|,
name|fileSuffix
argument_list|)
decl_stmt|;
name|SnapshotTestHelper
operator|.
name|dumpTree2File
argument_list|(
name|fsn
operator|.
name|getFSDirectory
argument_list|()
argument_list|,
name|file
argument_list|)
expr_stmt|;
return|return
name|file
return|;
block|}
comment|/** Append a file without closing the output stream */
DECL|method|appendFileWithoutClosing (Path file, int length)
specifier|private
name|HdfsDataOutputStream
name|appendFileWithoutClosing
parameter_list|(
name|Path
name|file
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|toAppend
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|toAppend
argument_list|)
expr_stmt|;
name|HdfsDataOutputStream
name|out
init|=
operator|(
name|HdfsDataOutputStream
operator|)
name|hdfs
operator|.
name|append
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|toAppend
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
comment|/** Save the fsimage to a temp file */
DECL|method|saveFSImageToTempFile ()
specifier|private
name|File
name|saveFSImageToTempFile
parameter_list|()
throws|throws
name|IOException
block|{
name|SaveNamespaceContext
name|context
init|=
operator|new
name|SaveNamespaceContext
argument_list|(
name|fsn
argument_list|,
name|txid
argument_list|,
operator|new
name|Canceler
argument_list|()
argument_list|)
decl_stmt|;
name|FSImageFormat
operator|.
name|Saver
name|saver
init|=
operator|new
name|FSImageFormat
operator|.
name|Saver
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|FSImageCompression
name|compression
init|=
name|FSImageCompression
operator|.
name|createCompression
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|File
name|imageFile
init|=
name|getImageFile
argument_list|(
name|testDir
argument_list|,
name|txid
argument_list|)
decl_stmt|;
name|fsn
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
name|saver
operator|.
name|save
argument_list|(
name|imageFile
argument_list|,
name|compression
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsn
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
return|return
name|imageFile
return|;
block|}
comment|/** Load the fsimage from a temp file */
DECL|method|loadFSImageFromTempFile (File imageFile)
specifier|private
name|void
name|loadFSImageFromTempFile
parameter_list|(
name|File
name|imageFile
parameter_list|)
throws|throws
name|IOException
block|{
name|FSImageFormat
operator|.
name|Loader
name|loader
init|=
operator|new
name|FSImageFormat
operator|.
name|Loader
argument_list|(
name|conf
argument_list|,
name|fsn
argument_list|)
decl_stmt|;
name|fsn
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|fsn
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|loader
operator|.
name|load
argument_list|(
name|imageFile
argument_list|)
expr_stmt|;
name|FSImage
operator|.
name|updateCountForQuota
argument_list|(
operator|(
name|INodeDirectoryWithQuota
operator|)
name|fsn
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|getINode
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsn
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
name|fsn
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Testing steps:    *<pre>    * 1. Creating/modifying directories/files while snapshots are being taken.    * 2. Dump the FSDirectory tree of the namesystem.    * 3. Save the namesystem to a temp file (FSImage saving).    * 4. Restart the cluster and format the namesystem.    * 5. Load the namesystem from the temp file (FSImage loading).    * 6. Dump the FSDirectory again and compare the two dumped string.    *</pre>    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testSaveLoadImage ()
specifier|public
name|void
name|testSaveLoadImage
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|s
init|=
literal|0
decl_stmt|;
comment|// make changes to the namesystem
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|dir
argument_list|,
literal|"s"
operator|+
operator|++
name|s
argument_list|)
expr_stmt|;
name|Path
name|sub1
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"sub1"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|sub1
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setPermission
argument_list|(
name|sub1
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|sub11
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"sub11"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|sub11
argument_list|)
expr_stmt|;
name|checkImage
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|dir
argument_list|,
literal|"s"
operator|+
operator|++
name|s
argument_list|)
expr_stmt|;
name|Path
name|sub1file1
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"sub1file1"
argument_list|)
decl_stmt|;
name|Path
name|sub1file2
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"sub1file2"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|sub1file1
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|sub1file2
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|checkImage
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|dir
argument_list|,
literal|"s"
operator|+
operator|++
name|s
argument_list|)
expr_stmt|;
name|Path
name|sub2
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"sub2"
argument_list|)
decl_stmt|;
name|Path
name|sub2file1
init|=
operator|new
name|Path
argument_list|(
name|sub2
argument_list|,
literal|"sub2file1"
argument_list|)
decl_stmt|;
name|Path
name|sub2file2
init|=
operator|new
name|Path
argument_list|(
name|sub2
argument_list|,
literal|"sub2file2"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|sub2file1
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|sub2file2
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|checkImage
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|dir
argument_list|,
literal|"s"
operator|+
operator|++
name|s
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setReplication
argument_list|(
name|sub1file1
argument_list|,
call|(
name|short
call|)
argument_list|(
name|REPLICATION
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|delete
argument_list|(
name|sub1file2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setOwner
argument_list|(
name|sub2
argument_list|,
literal|"dr.who"
argument_list|,
literal|"unknown"
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|delete
argument_list|(
name|sub2file2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkImage
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|checkImage (int s)
name|void
name|checkImage
parameter_list|(
name|int
name|s
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|name
init|=
literal|"s"
operator|+
name|s
decl_stmt|;
comment|// dump the fsdir tree
name|File
name|fsnBefore
init|=
name|dumpTree2File
argument_list|(
name|name
operator|+
literal|"_before"
argument_list|)
decl_stmt|;
comment|// save the namesystem to a temp file
name|File
name|imageFile
init|=
name|saveFSImageToTempFile
argument_list|()
decl_stmt|;
name|long
name|numSdirBefore
init|=
name|fsn
operator|.
name|getNumSnapshottableDirs
argument_list|()
decl_stmt|;
name|long
name|numSnapshotBefore
init|=
name|fsn
operator|.
name|getNumSnapshots
argument_list|()
decl_stmt|;
name|SnapshottableDirectoryStatus
index|[]
name|dirBefore
init|=
name|hdfs
operator|.
name|getSnapshottableDirListing
argument_list|()
decl_stmt|;
comment|// restart the cluster, and format the cluster
name|cluster
operator|.
name|shutdown
argument_list|()
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
name|format
argument_list|(
literal|true
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|REPLICATION
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
name|fsn
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
name|hdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
comment|// load the namesystem from the temp file
name|loadFSImageFromTempFile
argument_list|(
name|imageFile
argument_list|)
expr_stmt|;
comment|// dump the fsdir tree again
name|File
name|fsnAfter
init|=
name|dumpTree2File
argument_list|(
name|name
operator|+
literal|"_after"
argument_list|)
decl_stmt|;
comment|// compare two dumped tree
name|SnapshotTestHelper
operator|.
name|compareDumpedTreeInFile
argument_list|(
name|fsnBefore
argument_list|,
name|fsnAfter
argument_list|)
expr_stmt|;
name|long
name|numSdirAfter
init|=
name|fsn
operator|.
name|getNumSnapshottableDirs
argument_list|()
decl_stmt|;
name|long
name|numSnapshotAfter
init|=
name|fsn
operator|.
name|getNumSnapshots
argument_list|()
decl_stmt|;
name|SnapshottableDirectoryStatus
index|[]
name|dirAfter
init|=
name|hdfs
operator|.
name|getSnapshottableDirListing
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|numSdirBefore
argument_list|,
name|numSdirAfter
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|numSnapshotBefore
argument_list|,
name|numSnapshotAfter
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|dirBefore
operator|.
name|length
argument_list|,
name|dirAfter
operator|.
name|length
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|pathListBefore
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SnapshottableDirectoryStatus
name|sBefore
range|:
name|dirBefore
control|)
block|{
name|pathListBefore
operator|.
name|add
argument_list|(
name|sBefore
operator|.
name|getFullPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|SnapshottableDirectoryStatus
name|sAfter
range|:
name|dirAfter
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|pathListBefore
operator|.
name|contains
argument_list|(
name|sAfter
operator|.
name|getFullPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test the fsimage saving/loading while file appending.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testSaveLoadImageWithAppending ()
specifier|public
name|void
name|testSaveLoadImageWithAppending
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|sub1
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"sub1"
argument_list|)
decl_stmt|;
name|Path
name|sub1file1
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"sub1file1"
argument_list|)
decl_stmt|;
name|Path
name|sub1file2
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"sub1file2"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|sub1file1
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|sub1file2
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// 1. create snapshot s0
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|dir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|dir
argument_list|,
literal|"s0"
argument_list|)
expr_stmt|;
comment|// 2. create snapshot s1 before appending sub1file1 finishes
name|HdfsDataOutputStream
name|out
init|=
name|appendFileWithoutClosing
argument_list|(
name|sub1file1
argument_list|,
name|BLOCKSIZE
argument_list|)
decl_stmt|;
name|out
operator|.
name|hsync
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|SyncFlag
operator|.
name|UPDATE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
comment|// also append sub1file2
name|DFSTestUtil
operator|.
name|appendFile
argument_list|(
name|hdfs
argument_list|,
name|sub1file2
argument_list|,
name|BLOCKSIZE
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|dir
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// 3. create snapshot s2 before appending finishes
name|out
operator|=
name|appendFileWithoutClosing
argument_list|(
name|sub1file1
argument_list|,
name|BLOCKSIZE
argument_list|)
expr_stmt|;
name|out
operator|.
name|hsync
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|SyncFlag
operator|.
name|UPDATE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|dir
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// 4. save fsimage before appending finishes
name|out
operator|=
name|appendFileWithoutClosing
argument_list|(
name|sub1file1
argument_list|,
name|BLOCKSIZE
argument_list|)
expr_stmt|;
name|out
operator|.
name|hsync
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|SyncFlag
operator|.
name|UPDATE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
comment|// dump fsdir
name|File
name|fsnBefore
init|=
name|dumpTree2File
argument_list|(
literal|"before"
argument_list|)
decl_stmt|;
comment|// save the namesystem to a temp file
name|File
name|imageFile
init|=
name|saveFSImageToTempFile
argument_list|()
decl_stmt|;
comment|// 5. load fsimage and compare
comment|// first restart the cluster, and format the cluster
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
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
name|format
argument_list|(
literal|true
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|REPLICATION
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
name|fsn
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
name|hdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
comment|// then load the fsimage
name|loadFSImageFromTempFile
argument_list|(
name|imageFile
argument_list|)
expr_stmt|;
comment|// dump the fsdir tree again
name|File
name|fsnAfter
init|=
name|dumpTree2File
argument_list|(
literal|"after"
argument_list|)
decl_stmt|;
comment|// compare two dumped tree
name|SnapshotTestHelper
operator|.
name|compareDumpedTreeInFile
argument_list|(
name|fsnBefore
argument_list|,
name|fsnAfter
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the fsimage loading while there is file under construction.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testLoadImageWithAppending ()
specifier|public
name|void
name|testLoadImageWithAppending
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|sub1
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"sub1"
argument_list|)
decl_stmt|;
name|Path
name|sub1file1
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"sub1file1"
argument_list|)
decl_stmt|;
name|Path
name|sub1file2
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"sub1file2"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|sub1file1
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|sub1file2
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|dir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|dir
argument_list|,
literal|"s0"
argument_list|)
expr_stmt|;
name|HdfsDataOutputStream
name|out
init|=
name|appendFileWithoutClosing
argument_list|(
name|sub1file1
argument_list|,
name|BLOCKSIZE
argument_list|)
decl_stmt|;
name|out
operator|.
name|hsync
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|SyncFlag
operator|.
name|UPDATE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
comment|// save namespace and restart cluster
name|hdfs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|saveNamespace
argument_list|()
expr_stmt|;
name|hdfs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_LEAVE
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
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
name|format
argument_list|(
literal|false
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|REPLICATION
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
name|fsn
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
name|hdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test fsimage loading when 1) there is an empty file loaded from fsimage,    * and 2) there is later an append operation to be applied from edit log.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testLoadImageWithEmptyFile ()
specifier|public
name|void
name|testLoadImageWithEmptyFile
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create an empty file
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|hdfs
operator|.
name|create
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// save namespace
name|hdfs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|saveNamespace
argument_list|()
expr_stmt|;
name|hdfs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_LEAVE
argument_list|)
expr_stmt|;
comment|// append to the empty file
name|out
operator|=
name|hdfs
operator|.
name|append
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// restart cluster
name|cluster
operator|.
name|shutdown
argument_list|()
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
name|format
argument_list|(
literal|false
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|REPLICATION
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
name|hdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|FileStatus
name|status
init|=
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Testing a special case with snapshots. When the following steps happen:    *<pre>    * 1. Take snapshot s1 on dir.    * 2. Create new dir and files under subsubDir, which is descendant of dir.    * 3. Take snapshot s2 on dir.    * 4. Delete subsubDir.    * 5. Delete snapshot s2.    *</pre>    * When we merge the diff from s2 to s1 (since we deleted s2), we need to make    * sure all the files/dirs created after s1 should be destroyed. Otherwise    * we may save these files/dirs to the fsimage, and cause FileNotFound     * Exception while loading fsimage.      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testSaveLoadImageAfterSnapshotDeletion ()
specifier|public
name|void
name|testSaveLoadImageAfterSnapshotDeletion
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create initial dir and subdir
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/dir"
argument_list|)
decl_stmt|;
name|Path
name|subDir
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"subdir"
argument_list|)
decl_stmt|;
name|Path
name|subsubDir
init|=
operator|new
name|Path
argument_list|(
name|subDir
argument_list|,
literal|"subsubdir"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|subsubDir
argument_list|)
expr_stmt|;
comment|// take snapshots on subdir and dir
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|dir
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
comment|// create new dir under initial dir
name|Path
name|newDir
init|=
operator|new
name|Path
argument_list|(
name|subsubDir
argument_list|,
literal|"newdir"
argument_list|)
decl_stmt|;
name|Path
name|newFile
init|=
operator|new
name|Path
argument_list|(
name|newDir
argument_list|,
literal|"newfile"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|newDir
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|newFile
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// create another snapshot
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|dir
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
comment|// delete subsubdir
name|hdfs
operator|.
name|delete
argument_list|(
name|subsubDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// delete snapshot s2
name|hdfs
operator|.
name|deleteSnapshot
argument_list|(
name|dir
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
comment|// restart cluster
name|cluster
operator|.
name|shutdown
argument_list|()
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
name|REPLICATION
argument_list|)
operator|.
name|format
argument_list|(
literal|false
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
name|fsn
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
name|hdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
comment|// save namespace to fsimage
name|hdfs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|saveNamespace
argument_list|()
expr_stmt|;
name|hdfs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_LEAVE
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
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
name|format
argument_list|(
literal|false
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|REPLICATION
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
name|fsn
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
name|hdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

