begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.mapred.lib
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
operator|.
name|lib
package|;
end_package

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

begin_class
DECL|class|TestDynamicInputFormat
specifier|public
class|class
name|TestDynamicInputFormat
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
name|TestDynamicInputFormat
operator|.
name|class
argument_list|)
decl_stmt|;
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
literal|1000
decl_stmt|;
DECL|field|NUM_SPLITS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_SPLITS
init|=
literal|7
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
DECL|field|expectedFilePaths
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|expectedFilePaths
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|N_FILES
argument_list|)
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
name|getConfigurationForCluster
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
argument_list|)
expr_stmt|;
name|FileSystem
name|fileSystem
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|expectedFilePaths
operator|.
name|add
argument_list|(
name|fileSystem
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/source/0"
argument_list|)
argument_list|)
index|[
literal|0
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|getParent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getConfigurationForCluster ()
specifier|private
specifier|static
name|Configuration
name|getConfigurationForCluster
parameter_list|()
block|{
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"target/tmp/build/TEST_DYNAMIC_INPUT_FORMAT/data"
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|set
argument_list|(
literal|"hadoop.log.dir"
argument_list|,
literal|"target/tmp"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"fs.default.name  == "
operator|+
name|configuration
operator|.
name|get
argument_list|(
literal|"fs.default.name"
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"dfs.http.address == "
operator|+
name|configuration
operator|.
name|get
argument_list|(
literal|"dfs.http.address"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|configuration
return|;
block|}
DECL|method|getOptions ()
specifier|private
specifier|static
name|DistCpOptions
name|getOptions
parameter_list|()
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
name|DistCpOptions
name|options
init|=
operator|new
name|DistCpOptions
argument_list|(
name|sourceList
argument_list|,
name|targetPath
argument_list|)
decl_stmt|;
name|options
operator|.
name|setMaxMaps
argument_list|(
name|NUM_SPLITS
argument_list|)
expr_stmt|;
return|return
name|options
return|;
block|}
DECL|method|createFile (String path)
specifier|private
specifier|static
name|void
name|createFile
parameter_list|(
name|String
name|path
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
name|expectedFilePaths
operator|.
name|add
argument_list|(
name|fileSystem
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|)
index|[
literal|0
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
name|DistCpOptions
name|options
init|=
name|getOptions
argument_list|()
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
name|options
operator|.
name|getMaxMaps
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|CopyListing
operator|.
name|getCopyListing
argument_list|(
name|configuration
argument_list|,
name|CREDENTIALS
argument_list|,
name|options
argument_list|)
operator|.
name|buildListing
argument_list|(
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
literal|"/tmp/testDynInputFormat/fileList.seq"
argument_list|)
argument_list|,
name|options
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
name|DynamicInputFormat
argument_list|<
name|Text
argument_list|,
name|FileStatus
argument_list|>
name|inputFormat
init|=
operator|new
name|DynamicInputFormat
argument_list|<
name|Text
argument_list|,
name|FileStatus
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|InputSplit
argument_list|>
name|splits
init|=
name|inputFormat
operator|.
name|getSplits
argument_list|(
name|jobContext
argument_list|)
decl_stmt|;
name|int
name|nFiles
init|=
literal|0
decl_stmt|;
name|int
name|taskId
init|=
literal|0
decl_stmt|;
for|for
control|(
name|InputSplit
name|split
range|:
name|splits
control|)
block|{
name|RecordReader
argument_list|<
name|Text
argument_list|,
name|FileStatus
argument_list|>
name|recordReader
init|=
name|inputFormat
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
name|taskId
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
name|splits
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|taskAttemptContext
argument_list|)
expr_stmt|;
name|float
name|previousProgressValue
init|=
literal|0f
decl_stmt|;
while|while
condition|(
name|recordReader
operator|.
name|nextKeyValue
argument_list|()
condition|)
block|{
name|FileStatus
name|fileStatus
init|=
name|recordReader
operator|.
name|getCurrentValue
argument_list|()
decl_stmt|;
name|String
name|source
init|=
name|fileStatus
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|expectedFilePaths
operator|.
name|contains
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|float
name|progress
init|=
name|recordReader
operator|.
name|getProgress
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|progress
operator|>=
name|previousProgressValue
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|progress
operator|>=
literal|0.0f
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|progress
operator|<=
literal|1.0f
argument_list|)
expr_stmt|;
name|previousProgressValue
operator|=
name|progress
expr_stmt|;
operator|++
name|nFiles
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|recordReader
operator|.
name|getProgress
argument_list|()
operator|==
literal|1.0f
argument_list|)
expr_stmt|;
operator|++
name|taskId
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFilePaths
operator|.
name|size
argument_list|()
argument_list|,
name|nFiles
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetSplitRatio ()
specifier|public
name|void
name|testGetSplitRatio
parameter_list|()
throws|throws
name|Exception
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DynamicInputFormat
operator|.
name|getSplitRatio
argument_list|(
literal|1
argument_list|,
literal|1000000000
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|DynamicInputFormat
operator|.
name|getSplitRatio
argument_list|(
literal|11000000
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|DynamicInputFormat
operator|.
name|getSplitRatio
argument_list|(
literal|30
argument_list|,
literal|700
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|DynamicInputFormat
operator|.
name|getSplitRatio
argument_list|(
literal|30
argument_list|,
literal|200
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

