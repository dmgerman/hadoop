begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
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
name|mapreduce
operator|.
name|*
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
name|task
operator|.
name|JobContextImpl
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
name|lib
operator|.
name|input
operator|.
name|FileSplit
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
name|CopyListing
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
name|CopyListingFileStatus
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
name|DistCpContext
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
name|DistCpOptions
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
name|StubContext
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
name|junit
operator|.
name|AfterClass
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
name|BeforeClass
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
name|ArrayList
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

begin_class
DECL|class|TestUniformSizeInputFormat
specifier|public
class|class
name|TestUniformSizeInputFormat
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|N_FILES
specifier|private
specifier|static
specifier|final
name|int
name|N_FILES
init|=
literal|20
decl_stmt|;
DECL|field|SIZEOF_EACH_FILE
specifier|private
specifier|static
specifier|final
name|int
name|SIZEOF_EACH_FILE
init|=
literal|1024
decl_stmt|;
DECL|field|random
specifier|private
specifier|static
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|totalFileSize
specifier|private
specifier|static
name|int
name|totalFileSize
init|=
literal|0
decl_stmt|;
DECL|field|CREDENTIALS
specifier|private
specifier|static
specifier|final
name|Credentials
name|CREDENTIALS
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
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
operator|new
name|Configuration
argument_list|()
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|format
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|totalFileSize
operator|=
literal|0
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
name|N_FILES
condition|;
operator|++
name|i
control|)
name|totalFileSize
operator|+=
name|createFile
argument_list|(
literal|"/tmp/source/"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
name|SIZEOF_EACH_FILE
argument_list|)
expr_stmt|;
block|}
DECL|method|getOptions (int nMaps)
specifier|private
specifier|static
name|DistCpOptions
name|getOptions
parameter_list|(
name|int
name|nMaps
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|sourcePath
init|=
operator|new
name|Path
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/tmp/source"
argument_list|)
decl_stmt|;
name|Path
name|targetPath
init|=
operator|new
name|Path
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/tmp/target"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|sourceList
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
name|sourceList
operator|.
name|add
argument_list|(
name|sourcePath
argument_list|)
expr_stmt|;
return|return
operator|new
name|DistCpOptions
operator|.
name|Builder
argument_list|(
name|sourceList
argument_list|,
name|targetPath
argument_list|)
operator|.
name|maxMaps
argument_list|(
name|nMaps
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|createFile (String path, int fileSize)
specifier|private
specifier|static
name|int
name|createFile
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|fileSize
parameter_list|)
throws|throws
name|Exception
block|{
name|FileSystem
name|fileSystem
init|=
literal|null
decl_stmt|;
name|DataOutputStream
name|outputStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fileSystem
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|outputStream
operator|=
name|fileSystem
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|int
name|size
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|fileSize
operator|+
operator|(
literal|1
operator|-
name|random
operator|.
name|nextFloat
argument_list|()
operator|)
operator|*
name|fileSize
argument_list|)
decl_stmt|;
name|outputStream
operator|.
name|write
argument_list|(
operator|new
name|byte
index|[
name|size
index|]
argument_list|)
expr_stmt|;
return|return
name|size
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|fileSystem
argument_list|,
name|outputStream
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|testGetSplits (int nMaps)
specifier|public
name|void
name|testGetSplits
parameter_list|(
name|int
name|nMaps
parameter_list|)
throws|throws
name|Exception
block|{
name|DistCpContext
name|context
init|=
operator|new
name|DistCpContext
argument_list|(
name|getOptions
argument_list|(
name|nMaps
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|set
argument_list|(
literal|"mapred.map.tasks"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|context
operator|.
name|getMaxMaps
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|listFile
init|=
operator|new
name|Path
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/tmp/testGetSplits_1/fileList.seq"
argument_list|)
decl_stmt|;
name|CopyListing
operator|.
name|getCopyListing
argument_list|(
name|configuration
argument_list|,
name|CREDENTIALS
argument_list|,
name|context
argument_list|)
operator|.
name|buildListing
argument_list|(
name|listFile
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|JobContext
name|jobContext
init|=
operator|new
name|JobContextImpl
argument_list|(
name|configuration
argument_list|,
operator|new
name|JobID
argument_list|()
argument_list|)
decl_stmt|;
name|UniformSizeInputFormat
name|uniformSizeInputFormat
init|=
operator|new
name|UniformSizeInputFormat
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|InputSplit
argument_list|>
name|splits
init|=
name|uniformSizeInputFormat
operator|.
name|getSplits
argument_list|(
name|jobContext
argument_list|)
decl_stmt|;
name|int
name|sizePerMap
init|=
name|totalFileSize
operator|/
name|nMaps
decl_stmt|;
name|checkSplits
argument_list|(
name|listFile
argument_list|,
name|splits
argument_list|)
expr_stmt|;
name|int
name|doubleCheckedTotalSize
init|=
literal|0
decl_stmt|;
name|int
name|previousSplitSize
init|=
operator|-
literal|1
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
name|splits
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|InputSplit
name|split
init|=
name|splits
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|currentSplitSize
init|=
literal|0
decl_stmt|;
name|RecordReader
argument_list|<
name|Text
argument_list|,
name|CopyListingFileStatus
argument_list|>
name|recordReader
init|=
name|uniformSizeInputFormat
operator|.
name|createRecordReader
argument_list|(
name|split
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|StubContext
name|stubContext
init|=
operator|new
name|StubContext
argument_list|(
name|jobContext
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|recordReader
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|TaskAttemptContext
name|taskAttemptContext
init|=
name|stubContext
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|recordReader
operator|.
name|initialize
argument_list|(
name|split
argument_list|,
name|taskAttemptContext
argument_list|)
expr_stmt|;
while|while
condition|(
name|recordReader
operator|.
name|nextKeyValue
argument_list|()
condition|)
block|{
name|Path
name|sourcePath
init|=
name|recordReader
operator|.
name|getCurrentValue
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|sourcePath
operator|.
name|getFileSystem
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
name|FileStatus
name|fileStatus
index|[]
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|sourcePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileStatus
operator|.
name|length
operator|>
literal|1
condition|)
block|{
continue|continue;
block|}
name|currentSplitSize
operator|+=
name|fileStatus
index|[
literal|0
index|]
operator|.
name|getLen
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|previousSplitSize
operator|==
operator|-
literal|1
operator|||
name|Math
operator|.
name|abs
argument_list|(
name|currentSplitSize
operator|-
name|previousSplitSize
argument_list|)
operator|<
literal|0.1
operator|*
name|sizePerMap
operator|||
name|i
operator|==
name|splits
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|doubleCheckedTotalSize
operator|+=
name|currentSplitSize
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|totalFileSize
argument_list|,
name|doubleCheckedTotalSize
argument_list|)
expr_stmt|;
block|}
DECL|method|checkSplits (Path listFile, List<InputSplit> splits)
specifier|private
name|void
name|checkSplits
parameter_list|(
name|Path
name|listFile
parameter_list|,
name|List
argument_list|<
name|InputSplit
argument_list|>
name|splits
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|lastEnd
init|=
literal|0
decl_stmt|;
comment|//Verify if each split's start is matching with the previous end and
comment|//we are not missing anything
for|for
control|(
name|InputSplit
name|split
range|:
name|splits
control|)
block|{
name|FileSplit
name|fileSplit
init|=
operator|(
name|FileSplit
operator|)
name|split
decl_stmt|;
name|long
name|start
init|=
name|fileSplit
operator|.
name|getStart
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|lastEnd
argument_list|,
name|start
argument_list|)
expr_stmt|;
name|lastEnd
operator|=
name|start
operator|+
name|fileSplit
operator|.
name|getLength
argument_list|()
expr_stmt|;
block|}
comment|//Verify there is nothing more to read from the input file
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
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getConf
argument_list|()
argument_list|,
name|SequenceFile
operator|.
name|Reader
operator|.
name|file
argument_list|(
name|listFile
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|reader
operator|.
name|seek
argument_list|(
name|lastEnd
argument_list|)
expr_stmt|;
name|CopyListingFileStatus
name|srcFileStatus
init|=
operator|new
name|CopyListingFileStatus
argument_list|()
decl_stmt|;
name|Text
name|srcRelPath
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|reader
operator|.
name|next
argument_list|(
name|srcRelPath
argument_list|,
name|srcFileStatus
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetSplits ()
specifier|public
name|void
name|testGetSplits
parameter_list|()
throws|throws
name|Exception
block|{
name|testGetSplits
argument_list|(
literal|9
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|N_FILES
condition|;
operator|++
name|i
control|)
name|testGetSplits
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

