begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|io
operator|.
name|Text
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
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|concurrent
operator|.
name|TimeoutException
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|class|TestMRCJCFileInputFormat
specifier|public
class|class
name|TestMRCJCFileInputFormat
block|{
DECL|field|conf
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|dfs
name|MiniDFSCluster
name|dfs
init|=
literal|null
decl_stmt|;
DECL|method|newDFSCluster (JobConf conf)
specifier|private
name|MiniDFSCluster
name|newDFSCluster
parameter_list|(
name|JobConf
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
return|return
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
literal|4
argument_list|)
operator|.
name|racks
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/rack0"
block|,
literal|"/rack0"
block|,
literal|"/rack1"
block|,
literal|"/rack1"
block|}
argument_list|)
operator|.
name|hosts
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"host0"
block|,
literal|"host1"
block|,
literal|"host2"
block|,
literal|"host3"
block|}
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testLocality ()
specifier|public
name|void
name|testLocality
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|dfs
operator|=
name|newDFSCluster
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|dfs
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"FileSystem "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|inputDir
init|=
operator|new
name|Path
argument_list|(
literal|"/foo/"
argument_list|)
decl_stmt|;
name|String
name|fileName
init|=
literal|"part-0000"
decl_stmt|;
name|createInputs
argument_list|(
name|fs
argument_list|,
name|inputDir
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
comment|// split it using a file input format
name|TextInputFormat
operator|.
name|addInputPath
argument_list|(
name|job
argument_list|,
name|inputDir
argument_list|)
expr_stmt|;
name|TextInputFormat
name|inFormat
init|=
operator|new
name|TextInputFormat
argument_list|()
decl_stmt|;
name|inFormat
operator|.
name|configure
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|InputSplit
index|[]
name|splits
init|=
name|inFormat
operator|.
name|getSplits
argument_list|(
name|job
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|FileStatus
name|fileStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|inputDir
argument_list|,
name|fileName
argument_list|)
argument_list|)
decl_stmt|;
name|BlockLocation
index|[]
name|locations
init|=
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|fileStatus
argument_list|,
literal|0
argument_list|,
name|fileStatus
operator|.
name|getLen
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Made splits"
argument_list|)
expr_stmt|;
comment|// make sure that each split is a block and the locations match
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|splits
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|FileSplit
name|fileSplit
init|=
operator|(
name|FileSplit
operator|)
name|splits
index|[
name|i
index|]
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"File split: "
operator|+
name|fileSplit
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|h
range|:
name|fileSplit
operator|.
name|getLocations
argument_list|()
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Location: "
operator|+
name|h
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Block: "
operator|+
name|locations
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|locations
index|[
name|i
index|]
operator|.
name|getOffset
argument_list|()
argument_list|,
name|fileSplit
operator|.
name|getStart
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|locations
index|[
name|i
index|]
operator|.
name|getLength
argument_list|()
argument_list|,
name|fileSplit
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|blockLocs
init|=
name|locations
index|[
name|i
index|]
operator|.
name|getHosts
argument_list|()
decl_stmt|;
name|String
index|[]
name|splitLocs
init|=
name|fileSplit
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|blockLocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|splitLocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
name|blockLocs
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|splitLocs
index|[
literal|0
index|]
argument_list|)
operator|&&
name|blockLocs
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
name|splitLocs
index|[
literal|1
index|]
argument_list|)
operator|)
operator|||
operator|(
name|blockLocs
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
name|splitLocs
index|[
literal|0
index|]
argument_list|)
operator|&&
name|blockLocs
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|splitLocs
index|[
literal|1
index|]
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Expected value of "
operator|+
name|FileInputFormat
operator|.
name|NUM_INPUT_FILES
argument_list|,
literal|1
argument_list|,
name|job
operator|.
name|getLong
argument_list|(
name|FileInputFormat
operator|.
name|NUM_INPUT_FILES
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createInputs (FileSystem fs, Path inDir, String fileName)
specifier|private
name|void
name|createInputs
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|inDir
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
throws|,
name|TimeoutException
throws|,
name|InterruptedException
block|{
comment|// create a multi-block file on hdfs
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|inDir
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
specifier|final
name|short
name|replication
init|=
literal|2
decl_stmt|;
name|DataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|,
literal|true
argument_list|,
literal|4096
argument_list|,
name|replication
argument_list|,
literal|512
argument_list|,
literal|null
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
literal|1000
condition|;
operator|++
name|i
control|)
block|{
name|out
operator|.
name|writeChars
argument_list|(
literal|"Hello\n"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Wrote file"
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|replication
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNumInputs ()
specifier|public
name|void
name|testNumInputs
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|dfs
operator|=
name|newDFSCluster
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|dfs
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"FileSystem "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|inputDir
init|=
operator|new
name|Path
argument_list|(
literal|"/foo/"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numFiles
init|=
literal|10
decl_stmt|;
name|String
name|fileNameBase
init|=
literal|"part-0000"
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
name|numFiles
condition|;
operator|++
name|i
control|)
block|{
name|createInputs
argument_list|(
name|fs
argument_list|,
name|inputDir
argument_list|,
name|fileNameBase
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|createInputs
argument_list|(
name|fs
argument_list|,
name|inputDir
argument_list|,
literal|"_meta"
argument_list|)
expr_stmt|;
name|createInputs
argument_list|(
name|fs
argument_list|,
name|inputDir
argument_list|,
literal|"_temp"
argument_list|)
expr_stmt|;
comment|// split it using a file input format
name|TextInputFormat
operator|.
name|addInputPath
argument_list|(
name|job
argument_list|,
name|inputDir
argument_list|)
expr_stmt|;
name|TextInputFormat
name|inFormat
init|=
operator|new
name|TextInputFormat
argument_list|()
decl_stmt|;
name|inFormat
operator|.
name|configure
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|InputSplit
index|[]
name|splits
init|=
name|inFormat
operator|.
name|getSplits
argument_list|(
name|job
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected value of "
operator|+
name|FileInputFormat
operator|.
name|NUM_INPUT_FILES
argument_list|,
name|numFiles
argument_list|,
name|job
operator|.
name|getLong
argument_list|(
name|FileInputFormat
operator|.
name|NUM_INPUT_FILES
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|root
specifier|final
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
literal|"/TestFileInputFormat"
argument_list|)
decl_stmt|;
DECL|field|file1
specifier|final
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
name|root
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
DECL|field|dir1
specifier|final
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
DECL|field|file2
specifier|final
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
name|dir1
argument_list|,
literal|"file2"
argument_list|)
decl_stmt|;
DECL|field|BLOCKSIZE
specifier|static
specifier|final
name|int
name|BLOCKSIZE
init|=
literal|1024
decl_stmt|;
DECL|field|databuf
specifier|static
specifier|final
name|byte
index|[]
name|databuf
init|=
operator|new
name|byte
index|[
name|BLOCKSIZE
index|]
decl_stmt|;
DECL|field|rack1
specifier|private
specifier|static
specifier|final
name|String
name|rack1
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"/r1"
block|}
decl_stmt|;
DECL|field|hosts1
specifier|private
specifier|static
specifier|final
name|String
name|hosts1
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"host1.rack1.com"
block|}
decl_stmt|;
DECL|class|DummyFileInputFormat
specifier|private
class|class
name|DummyFileInputFormat
extends|extends
name|FileInputFormat
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
block|{
annotation|@
name|Override
DECL|method|getRecordReader (InputSplit split, JobConf job, Reporter reporter)
specifier|public
name|RecordReader
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|getRecordReader
parameter_list|(
name|InputSplit
name|split
parameter_list|,
name|JobConf
name|job
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMultiLevelInput ()
specifier|public
name|void
name|testMultiLevelInput
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|job
operator|.
name|setBoolean
argument_list|(
literal|"dfs.replication.considerLoad"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dfs
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|job
argument_list|)
operator|.
name|racks
argument_list|(
name|rack1
argument_list|)
operator|.
name|hosts
argument_list|(
name|hosts1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|dfs
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|String
name|namenode
init|=
operator|(
name|dfs
operator|.
name|getFileSystem
argument_list|()
operator|)
operator|.
name|getUri
argument_list|()
operator|.
name|getHost
argument_list|()
operator|+
literal|":"
operator|+
operator|(
name|dfs
operator|.
name|getFileSystem
argument_list|()
operator|)
operator|.
name|getUri
argument_list|()
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|FileSystem
name|fileSys
init|=
name|dfs
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|fileSys
operator|.
name|mkdirs
argument_list|(
name|dir1
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mkdirs failed to create "
operator|+
name|root
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|writeFile
argument_list|(
name|job
argument_list|,
name|file1
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|job
argument_list|,
name|file2
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// split it using a CombinedFile input format
name|DummyFileInputFormat
name|inFormat
init|=
operator|new
name|DummyFileInputFormat
argument_list|()
decl_stmt|;
name|inFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|root
argument_list|)
expr_stmt|;
comment|// By default, we don't allow multi-level/recursive inputs
name|boolean
name|exceptionThrown
init|=
literal|false
decl_stmt|;
try|try
block|{
name|InputSplit
index|[]
name|splits
init|=
name|inFormat
operator|.
name|getSplits
argument_list|(
name|job
argument_list|,
literal|1
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exceptionThrown
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Exception should be thrown by default for scanning a "
operator|+
literal|"directory with directories inside."
argument_list|,
name|exceptionThrown
argument_list|)
expr_stmt|;
comment|// Enable multi-level/recursive inputs
name|job
operator|.
name|setBoolean
argument_list|(
name|FileInputFormat
operator|.
name|INPUT_DIR_RECURSIVE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|InputSplit
index|[]
name|splits
init|=
name|inFormat
operator|.
name|getSplits
argument_list|(
name|job
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|splits
operator|.
name|length
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Test
DECL|method|testLastInputSplitAtSplitBoundary ()
specifier|public
name|void
name|testLastInputSplitAtSplitBoundary
parameter_list|()
throws|throws
name|Exception
block|{
name|FileInputFormat
name|fif
init|=
operator|new
name|FileInputFormatForTest
argument_list|(
literal|1024l
operator|*
literal|1024
operator|*
literal|1024
argument_list|,
literal|128l
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|InputSplit
index|[]
name|splits
init|=
name|fif
operator|.
name|getSplits
argument_list|(
name|job
argument_list|,
literal|8
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|splits
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
name|splits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|InputSplit
name|split
init|=
name|splits
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|"host"
operator|+
name|i
operator|)
argument_list|,
name|split
operator|.
name|getLocations
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Test
DECL|method|testLastInputSplitExceedingSplitBoundary ()
specifier|public
name|void
name|testLastInputSplitExceedingSplitBoundary
parameter_list|()
throws|throws
name|Exception
block|{
name|FileInputFormat
name|fif
init|=
operator|new
name|FileInputFormatForTest
argument_list|(
literal|1027l
operator|*
literal|1024
operator|*
literal|1024
argument_list|,
literal|128l
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|InputSplit
index|[]
name|splits
init|=
name|fif
operator|.
name|getSplits
argument_list|(
name|job
argument_list|,
literal|8
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|splits
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
name|splits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|InputSplit
name|split
init|=
name|splits
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|"host"
operator|+
name|i
operator|)
argument_list|,
name|split
operator|.
name|getLocations
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Test
DECL|method|testLastInputSplitSingleSplit ()
specifier|public
name|void
name|testLastInputSplitSingleSplit
parameter_list|()
throws|throws
name|Exception
block|{
name|FileInputFormat
name|fif
init|=
operator|new
name|FileInputFormatForTest
argument_list|(
literal|100l
operator|*
literal|1024
operator|*
literal|1024
argument_list|,
literal|128l
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|InputSplit
index|[]
name|splits
init|=
name|fif
operator|.
name|getSplits
argument_list|(
name|job
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|splits
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
name|splits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|InputSplit
name|split
init|=
name|splits
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|"host"
operator|+
name|i
operator|)
argument_list|,
name|split
operator|.
name|getLocations
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FileInputFormatForTest
specifier|private
class|class
name|FileInputFormatForTest
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|FileInputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|splitSize
name|long
name|splitSize
decl_stmt|;
DECL|field|length
name|long
name|length
decl_stmt|;
DECL|method|FileInputFormatForTest (long length, long splitSize)
name|FileInputFormatForTest
parameter_list|(
name|long
name|length
parameter_list|,
name|long
name|splitSize
parameter_list|)
block|{
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|splitSize
operator|=
name|splitSize
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRecordReader (InputSplit split, JobConf job, Reporter reporter)
specifier|public
name|RecordReader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|getRecordReader
parameter_list|(
name|InputSplit
name|split
parameter_list|,
name|JobConf
name|job
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|listStatus (JobConf job)
specifier|protected
name|FileStatus
index|[]
name|listStatus
parameter_list|(
name|JobConf
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStatus
name|mockFileStatus
init|=
name|mock
argument_list|(
name|FileStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockFileStatus
operator|.
name|getBlockSize
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|splitSize
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockFileStatus
operator|.
name|isDirectory
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Path
name|mockPath
init|=
name|mock
argument_list|(
name|Path
operator|.
name|class
argument_list|)
decl_stmt|;
name|FileSystem
name|mockFs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|BlockLocation
index|[]
name|blockLocations
init|=
name|mockBlockLocations
argument_list|(
name|length
argument_list|,
name|splitSize
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockFs
operator|.
name|getFileBlockLocations
argument_list|(
name|mockFileStatus
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|blockLocations
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockPath
operator|.
name|getFileSystem
argument_list|(
name|any
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockFs
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockFileStatus
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockPath
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockFileStatus
operator|.
name|getLen
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|FileStatus
index|[]
name|fs
init|=
operator|new
name|FileStatus
index|[
literal|1
index|]
decl_stmt|;
name|fs
index|[
literal|0
index|]
operator|=
name|mockFileStatus
expr_stmt|;
return|return
name|fs
return|;
block|}
annotation|@
name|Override
DECL|method|computeSplitSize (long blockSize, long minSize, long maxSize)
specifier|protected
name|long
name|computeSplitSize
parameter_list|(
name|long
name|blockSize
parameter_list|,
name|long
name|minSize
parameter_list|,
name|long
name|maxSize
parameter_list|)
block|{
return|return
name|splitSize
return|;
block|}
DECL|method|mockBlockLocations (long size, long splitSize)
specifier|private
name|BlockLocation
index|[]
name|mockBlockLocations
parameter_list|(
name|long
name|size
parameter_list|,
name|long
name|splitSize
parameter_list|)
block|{
name|int
name|numLocations
init|=
call|(
name|int
call|)
argument_list|(
name|size
operator|/
name|splitSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|size
operator|%
name|splitSize
operator|!=
literal|0
condition|)
name|numLocations
operator|++
expr_stmt|;
name|BlockLocation
index|[]
name|blockLocations
init|=
operator|new
name|BlockLocation
index|[
name|numLocations
index|]
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
name|numLocations
condition|;
name|i
operator|++
control|)
block|{
name|String
index|[]
name|names
init|=
operator|new
name|String
index|[]
block|{
literal|"b"
operator|+
name|i
block|}
decl_stmt|;
name|String
index|[]
name|hosts
init|=
operator|new
name|String
index|[]
block|{
literal|"host"
operator|+
name|i
block|}
decl_stmt|;
name|blockLocations
index|[
name|i
index|]
operator|=
operator|new
name|BlockLocation
argument_list|(
name|names
argument_list|,
name|hosts
argument_list|,
name|i
operator|*
name|splitSize
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|splitSize
argument_list|,
name|size
operator|-
operator|(
name|splitSize
operator|*
name|i
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|blockLocations
return|;
block|}
block|}
DECL|method|writeFile (Configuration conf, Path name, short replication, int numBlocks)
specifier|static
name|void
name|writeFile
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Path
name|name
parameter_list|,
name|short
name|replication
parameter_list|,
name|int
name|numBlocks
parameter_list|)
throws|throws
name|IOException
throws|,
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|FileSystem
name|fileSys
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
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
name|conf
operator|.
name|getInt
argument_list|(
literal|"io.file.buffer.size"
argument_list|,
literal|4096
argument_list|)
argument_list|,
name|replication
argument_list|,
operator|(
name|long
operator|)
name|BLOCKSIZE
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
name|numBlocks
condition|;
name|i
operator|++
control|)
block|{
name|stm
operator|.
name|write
argument_list|(
name|databuf
argument_list|)
expr_stmt|;
block|}
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fileSys
argument_list|,
name|name
argument_list|,
name|replication
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
name|dfs
operator|!=
literal|null
condition|)
block|{
name|dfs
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dfs
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

