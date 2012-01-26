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
name|tools
operator|.
name|util
operator|.
name|DistCpUtils
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
name|net
operator|.
name|URI
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
DECL|class|TestGlobbedCopyListing
specifier|public
class|class
name|TestGlobbedCopyListing
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
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
DECL|field|expectedValues
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|expectedValues
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
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
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|createSourceData
argument_list|()
expr_stmt|;
block|}
DECL|method|createSourceData ()
specifier|private
specifier|static
name|void
name|createSourceData
parameter_list|()
throws|throws
name|Exception
block|{
name|mkdirs
argument_list|(
literal|"/tmp/source/1"
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/tmp/source/2"
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/tmp/source/2/3"
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/tmp/source/2/3/4"
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/tmp/source/5"
argument_list|)
expr_stmt|;
name|touchFile
argument_list|(
literal|"/tmp/source/5/6"
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/tmp/source/7"
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/tmp/source/7/8"
argument_list|)
expr_stmt|;
name|touchFile
argument_list|(
literal|"/tmp/source/7/8/9"
argument_list|)
expr_stmt|;
block|}
DECL|method|mkdirs (String path)
specifier|private
specifier|static
name|void
name|mkdirs
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
try|try
block|{
name|fileSystem
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|fileSystem
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|recordInExpectedValues
argument_list|(
name|path
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
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|touchFile (String path)
specifier|private
specifier|static
name|void
name|touchFile
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
name|recordInExpectedValues
argument_list|(
name|path
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
DECL|method|recordInExpectedValues (String path)
specifier|private
specifier|static
name|void
name|recordInExpectedValues
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
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|sourcePath
init|=
operator|new
name|Path
argument_list|(
name|fileSystem
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
name|path
argument_list|)
decl_stmt|;
name|expectedValues
operator|.
name|put
argument_list|(
name|sourcePath
operator|.
name|toString
argument_list|()
argument_list|,
name|DistCpUtils
operator|.
name|getRelativePath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/source"
argument_list|)
argument_list|,
name|sourcePath
argument_list|)
argument_list|)
expr_stmt|;
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
DECL|method|testRun ()
specifier|public
name|void
name|testRun
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|URI
name|uri
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
decl_stmt|;
specifier|final
name|String
name|pathString
init|=
name|uri
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Path
name|fileSystemPath
init|=
operator|new
name|Path
argument_list|(
name|pathString
argument_list|)
decl_stmt|;
name|Path
name|source
init|=
operator|new
name|Path
argument_list|(
name|fileSystemPath
operator|.
name|toString
argument_list|()
operator|+
literal|"/tmp/source"
argument_list|)
decl_stmt|;
name|Path
name|target
init|=
operator|new
name|Path
argument_list|(
name|fileSystemPath
operator|.
name|toString
argument_list|()
operator|+
literal|"/tmp/target"
argument_list|)
decl_stmt|;
name|Path
name|listingPath
init|=
operator|new
name|Path
argument_list|(
name|fileSystemPath
operator|.
name|toString
argument_list|()
operator|+
literal|"/tmp/META/fileList.seq"
argument_list|)
decl_stmt|;
name|DistCpOptions
name|options
init|=
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
decl_stmt|;
operator|new
name|GlobbedCopyListing
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
name|CREDENTIALS
argument_list|)
operator|.
name|buildListing
argument_list|(
name|listingPath
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|verifyContents
argument_list|(
name|listingPath
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyContents (Path listingPath)
specifier|private
name|void
name|verifyContents
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
name|cluster
operator|.
name|getFileSystem
argument_list|()
argument_list|,
name|listingPath
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|Text
name|key
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|FileStatus
name|value
init|=
operator|new
name|FileStatus
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|actualValues
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
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
name|actualValues
operator|.
name|put
argument_list|(
name|value
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|key
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedValues
operator|.
name|size
argument_list|()
argument_list|,
name|actualValues
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|actualValues
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|expectedValues
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

