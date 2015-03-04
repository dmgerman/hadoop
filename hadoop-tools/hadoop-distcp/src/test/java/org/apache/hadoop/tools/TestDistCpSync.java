begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
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
name|protocol
operator|.
name|SnapshotDiffReport
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
name|io
operator|.
name|IOUtils
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
name|io
operator|.
name|SequenceFile
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
name|io
operator|.
name|Text
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
name|mapreduce
operator|.
name|Mapper
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
name|Credentials
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
name|tools
operator|.
name|mapred
operator|.
name|CopyMapper
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
name|HashMap
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

begin_class
DECL|class|TestDistCpSync
specifier|public
class|class
name|TestDistCpSync
block|{
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|field|dfs
specifier|private
name|DistributedFileSystem
name|dfs
decl_stmt|;
DECL|field|options
specifier|private
name|DistCpOptions
name|options
decl_stmt|;
DECL|field|source
specifier|private
specifier|final
name|Path
name|source
init|=
operator|new
name|Path
argument_list|(
literal|"/source"
argument_list|)
decl_stmt|;
DECL|field|target
specifier|private
specifier|final
name|Path
name|target
init|=
operator|new
name|Path
argument_list|(
literal|"/target"
argument_list|)
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|private
specifier|final
name|long
name|BLOCK_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|DATA_NUM
specifier|private
specifier|final
name|short
name|DATA_NUM
init|=
literal|1
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
name|DATA_NUM
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
name|dfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|options
operator|=
operator|new
name|DistCpOptions
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|source
argument_list|)
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|options
operator|.
name|setSyncFolder
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|options
operator|.
name|setDeleteMissing
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|options
operator|.
name|setUseDiff
argument_list|(
literal|true
argument_list|,
literal|"s1"
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|options
operator|.
name|appendToConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_TARGET_WORK_PATH
argument_list|,
name|target
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_TARGET_FINAL_PATH
argument_list|,
name|target
operator|.
name|toString
argument_list|()
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
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|dfs
argument_list|)
expr_stmt|;
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
comment|/**    * Test the sync returns false in the following scenarios:    * 1. the source/target dir are not snapshottable dir    * 2. the source/target does not have the given snapshots    * 3. changes have been made in target    */
annotation|@
name|Test
DECL|method|testFallback ()
specifier|public
name|void
name|testFallback
parameter_list|()
throws|throws
name|Exception
block|{
comment|// the source/target dir are not snapshottable dir
name|Assert
operator|.
name|assertFalse
argument_list|(
name|DistCpSync
operator|.
name|sync
argument_list|(
name|options
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
comment|// the source/target does not have the given snapshots
name|dfs
operator|.
name|allowSnapshot
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|allowSnapshot
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|DistCpSync
operator|.
name|sync
argument_list|(
name|options
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|createSnapshot
argument_list|(
name|source
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|createSnapshot
argument_list|(
name|source
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|createSnapshot
argument_list|(
name|target
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|DistCpSync
operator|.
name|sync
argument_list|(
name|options
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
comment|// changes have been made in target
specifier|final
name|Path
name|subTarget
init|=
operator|new
name|Path
argument_list|(
name|target
argument_list|,
literal|"sub"
argument_list|)
decl_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|subTarget
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|DistCpSync
operator|.
name|sync
argument_list|(
name|options
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|delete
argument_list|(
name|subTarget
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|DistCpSync
operator|.
name|sync
argument_list|(
name|options
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * create some files and directories under the given directory.    * the final subtree looks like this:    *                     dir/    *              foo/          bar/    *           d1/    f1     d2/    f2    *         f3            f4    */
DECL|method|initData (Path dir)
specifier|private
name|void
name|initData
parameter_list|(
name|Path
name|dir
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|d1
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"d1"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|f1
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"f1"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|d2
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"d2"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|f2
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"f2"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|f3
init|=
operator|new
name|Path
argument_list|(
name|d1
argument_list|,
literal|"f3"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|f4
init|=
operator|new
name|Path
argument_list|(
name|d2
argument_list|,
literal|"f4"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|f1
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|DATA_NUM
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|f2
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|DATA_NUM
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|f3
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|DATA_NUM
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|f4
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|DATA_NUM
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * make some changes under the given directory (created in the above way).    * 1. rename dir/foo/d1 to dir/bar/d1    * 2. delete dir/bar/d1/f3    * 3. rename dir/foo to /dir/bar/d1/foo    * 4. delete dir/bar/d1/foo/f1    * 5. create file dir/bar/d1/foo/f1 whose size is 2*BLOCK_SIZE    * 6. append one BLOCK to file dir/bar/f2    * 7. rename dir/bar to dir/foo    *    * Thus after all these ops the subtree looks like this:    *                       dir/    *                       foo/    *                 d1/    f2(A)    d2/    *                foo/             f4    *                f1(new)    */
DECL|method|changeData (Path dir)
specifier|private
name|void
name|changeData
parameter_list|(
name|Path
name|dir
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|d1
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"d1"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|f2
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"f2"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar_d1
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"d1"
argument_list|)
decl_stmt|;
name|dfs
operator|.
name|rename
argument_list|(
name|d1
argument_list|,
name|bar_d1
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|f3
init|=
operator|new
name|Path
argument_list|(
name|bar_d1
argument_list|,
literal|"f3"
argument_list|)
decl_stmt|;
name|dfs
operator|.
name|delete
argument_list|(
name|f3
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|newfoo
init|=
operator|new
name|Path
argument_list|(
name|bar_d1
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|dfs
operator|.
name|rename
argument_list|(
name|foo
argument_list|,
name|newfoo
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|f1
init|=
operator|new
name|Path
argument_list|(
name|newfoo
argument_list|,
literal|"f1"
argument_list|)
decl_stmt|;
name|dfs
operator|.
name|delete
argument_list|(
name|f1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|f1
argument_list|,
literal|2
operator|*
name|BLOCK_SIZE
argument_list|,
name|DATA_NUM
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|appendFile
argument_list|(
name|dfs
argument_list|,
name|f2
argument_list|,
operator|(
name|int
operator|)
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|rename
argument_list|(
name|bar
argument_list|,
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the basic functionality.    */
annotation|@
name|Test
DECL|method|testSync ()
specifier|public
name|void
name|testSync
parameter_list|()
throws|throws
name|Exception
block|{
name|initData
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|initData
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|allowSnapshot
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|allowSnapshot
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|createSnapshot
argument_list|(
name|source
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|createSnapshot
argument_list|(
name|target
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
comment|// make changes under source
name|changeData
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|createSnapshot
argument_list|(
name|source
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
comment|// do the sync
name|Assert
operator|.
name|assertTrue
argument_list|(
name|DistCpSync
operator|.
name|sync
argument_list|(
name|options
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
comment|// build copy listing
specifier|final
name|Path
name|listingPath
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/META/fileList.seq"
argument_list|)
decl_stmt|;
name|CopyListing
name|listing
init|=
operator|new
name|GlobbedCopyListing
argument_list|(
name|conf
argument_list|,
operator|new
name|Credentials
argument_list|()
argument_list|)
decl_stmt|;
name|listing
operator|.
name|buildListing
argument_list|(
name|listingPath
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Text
argument_list|,
name|CopyListingFileStatus
argument_list|>
name|copyListing
init|=
name|getListing
argument_list|(
name|listingPath
argument_list|)
decl_stmt|;
name|CopyMapper
name|copyMapper
init|=
operator|new
name|CopyMapper
argument_list|()
decl_stmt|;
name|StubContext
name|stubContext
init|=
operator|new
name|StubContext
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Mapper
argument_list|<
name|Text
argument_list|,
name|CopyListingFileStatus
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|>
operator|.
name|Context
name|context
init|=
name|stubContext
operator|.
name|getContext
argument_list|()
decl_stmt|;
comment|// Enable append
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setBoolean
argument_list|(
name|DistCpOptionSwitch
operator|.
name|APPEND
operator|.
name|getConfigLabel
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|copyMapper
operator|.
name|setup
argument_list|(
name|context
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Text
argument_list|,
name|CopyListingFileStatus
argument_list|>
name|entry
range|:
name|copyListing
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|copyMapper
operator|.
name|map
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
comment|// verify that we only copied new appended data of f2 and the new file f1
name|Assert
operator|.
name|assertEquals
argument_list|(
name|BLOCK_SIZE
operator|*
literal|3
argument_list|,
name|stubContext
operator|.
name|getReporter
argument_list|()
operator|.
name|getCounter
argument_list|(
name|CopyMapper
operator|.
name|Counter
operator|.
name|BYTESCOPIED
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify the source and target now has the same structure
name|verifyCopy
argument_list|(
name|dfs
operator|.
name|getFileStatus
argument_list|(
name|source
argument_list|)
argument_list|,
name|dfs
operator|.
name|getFileStatus
argument_list|(
name|target
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|getListing (Path listingPath)
specifier|private
name|Map
argument_list|<
name|Text
argument_list|,
name|CopyListingFileStatus
argument_list|>
name|getListing
parameter_list|(
name|Path
name|listingPath
parameter_list|)
throws|throws
name|Exception
block|{
name|SequenceFile
operator|.
name|Reader
name|reader
init|=
operator|new
name|SequenceFile
operator|.
name|Reader
argument_list|(
name|conf
argument_list|,
name|SequenceFile
operator|.
name|Reader
operator|.
name|file
argument_list|(
name|listingPath
argument_list|)
argument_list|)
decl_stmt|;
name|Text
name|key
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|CopyListingFileStatus
name|value
init|=
operator|new
name|CopyListingFileStatus
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Text
argument_list|,
name|CopyListingFileStatus
argument_list|>
name|values
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|next
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
condition|)
block|{
name|values
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|key
operator|=
operator|new
name|Text
argument_list|()
expr_stmt|;
name|value
operator|=
operator|new
name|CopyListingFileStatus
argument_list|()
expr_stmt|;
block|}
return|return
name|values
return|;
block|}
DECL|method|verifyCopy (FileStatus s, FileStatus t, boolean compareName)
specifier|private
name|void
name|verifyCopy
parameter_list|(
name|FileStatus
name|s
parameter_list|,
name|FileStatus
name|t
parameter_list|,
name|boolean
name|compareName
parameter_list|)
throws|throws
name|Exception
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|s
operator|.
name|isDirectory
argument_list|()
argument_list|,
name|t
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|compareName
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|s
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|t
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|s
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// verify the file content is the same
name|byte
index|[]
name|sbytes
init|=
name|DFSTestUtil
operator|.
name|readFileBuffer
argument_list|(
name|dfs
argument_list|,
name|s
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|tbytes
init|=
name|DFSTestUtil
operator|.
name|readFileBuffer
argument_list|(
name|dfs
argument_list|,
name|t
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|sbytes
argument_list|,
name|tbytes
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FileStatus
index|[]
name|slist
init|=
name|dfs
operator|.
name|listStatus
argument_list|(
name|s
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|FileStatus
index|[]
name|tlist
init|=
name|dfs
operator|.
name|listStatus
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|slist
operator|.
name|length
argument_list|,
name|tlist
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
name|slist
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|verifyCopy
argument_list|(
name|slist
index|[
name|i
index|]
argument_list|,
name|tlist
index|[
name|i
index|]
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|initData2 (Path dir)
specifier|private
name|void
name|initData2
parameter_list|(
name|Path
name|dir
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|test
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|f1
init|=
operator|new
name|Path
argument_list|(
name|test
argument_list|,
literal|"f1"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|f2
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"f2"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|f3
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"f3"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|f1
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|DATA_NUM
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|f2
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|DATA_NUM
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|f3
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|DATA_NUM
argument_list|,
literal|2L
argument_list|)
expr_stmt|;
block|}
DECL|method|changeData2 (Path dir)
specifier|private
name|void
name|changeData2
parameter_list|(
name|Path
name|dir
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|tmpFoo
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"tmpFoo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|test
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
name|dfs
operator|.
name|rename
argument_list|(
name|test
argument_list|,
name|tmpFoo
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|rename
argument_list|(
name|foo
argument_list|,
name|test
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|rename
argument_list|(
name|bar
argument_list|,
name|foo
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|rename
argument_list|(
name|tmpFoo
argument_list|,
name|bar
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSync2 ()
specifier|public
name|void
name|testSync2
parameter_list|()
throws|throws
name|Exception
block|{
name|initData2
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|initData2
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|allowSnapshot
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|allowSnapshot
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|createSnapshot
argument_list|(
name|source
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|createSnapshot
argument_list|(
name|target
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
comment|// make changes under source
name|changeData2
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|createSnapshot
argument_list|(
name|source
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|SnapshotDiffReport
name|report
init|=
name|dfs
operator|.
name|getSnapshotDiffReport
argument_list|(
name|source
argument_list|,
literal|"s1"
argument_list|,
literal|"s2"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|report
argument_list|)
expr_stmt|;
comment|// do the sync
name|Assert
operator|.
name|assertTrue
argument_list|(
name|DistCpSync
operator|.
name|sync
argument_list|(
name|options
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|verifyCopy
argument_list|(
name|dfs
operator|.
name|getFileStatus
argument_list|(
name|source
argument_list|)
argument_list|,
name|dfs
operator|.
name|getFileStatus
argument_list|(
name|target
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|initData3 (Path dir)
specifier|private
name|void
name|initData3
parameter_list|(
name|Path
name|dir
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|test
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|f1
init|=
operator|new
name|Path
argument_list|(
name|test
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|f2
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|f3
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|f1
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|DATA_NUM
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|f2
argument_list|,
name|BLOCK_SIZE
operator|*
literal|2
argument_list|,
name|DATA_NUM
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|f3
argument_list|,
name|BLOCK_SIZE
operator|*
literal|3
argument_list|,
name|DATA_NUM
argument_list|,
literal|2L
argument_list|)
expr_stmt|;
block|}
DECL|method|changeData3 (Path dir)
specifier|private
name|void
name|changeData3
parameter_list|(
name|Path
name|dir
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|test
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|f1
init|=
operator|new
name|Path
argument_list|(
name|test
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|f2
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|f3
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|newf1
init|=
operator|new
name|Path
argument_list|(
name|test
argument_list|,
literal|"newfile"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|newf2
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"newfile"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|newf3
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"newfile"
argument_list|)
decl_stmt|;
name|dfs
operator|.
name|rename
argument_list|(
name|f1
argument_list|,
name|newf1
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|rename
argument_list|(
name|f2
argument_list|,
name|newf2
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|rename
argument_list|(
name|f3
argument_list|,
name|newf3
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test a case where there are multiple source files with the same name    */
annotation|@
name|Test
DECL|method|testSync3 ()
specifier|public
name|void
name|testSync3
parameter_list|()
throws|throws
name|Exception
block|{
name|initData3
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|initData3
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|allowSnapshot
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|allowSnapshot
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|createSnapshot
argument_list|(
name|source
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|createSnapshot
argument_list|(
name|target
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
comment|// make changes under source
name|changeData3
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|createSnapshot
argument_list|(
name|source
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|SnapshotDiffReport
name|report
init|=
name|dfs
operator|.
name|getSnapshotDiffReport
argument_list|(
name|source
argument_list|,
literal|"s1"
argument_list|,
literal|"s2"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|report
argument_list|)
expr_stmt|;
comment|// do the sync
name|Assert
operator|.
name|assertTrue
argument_list|(
name|DistCpSync
operator|.
name|sync
argument_list|(
name|options
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|verifyCopy
argument_list|(
name|dfs
operator|.
name|getFileStatus
argument_list|(
name|source
argument_list|)
argument_list|,
name|dfs
operator|.
name|getFileStatus
argument_list|(
name|target
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

