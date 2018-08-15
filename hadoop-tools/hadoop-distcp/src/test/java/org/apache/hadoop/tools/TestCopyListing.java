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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
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
name|tools
operator|.
name|util
operator|.
name|TestDistCpUtils
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
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
name|AfterClass
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
name|io
operator|.
name|OutputStream
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
annotation|@
name|RunWith
argument_list|(
name|value
operator|=
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestCopyListing
specifier|public
class|class
name|TestCopyListing
extends|extends
name|SimpleCopyListing
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestCopyListing
operator|.
name|class
argument_list|)
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
DECL|field|config
specifier|private
specifier|static
specifier|final
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|create ()
specifier|public
specifier|static
name|void
name|create
parameter_list|()
throws|throws
name|IOException
block|{
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|config
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
block|}
annotation|@
name|AfterClass
DECL|method|destroy ()
specifier|public
specifier|static
name|void
name|destroy
parameter_list|()
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
annotation|@
name|Parameters
DECL|method|data ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
name|Object
index|[]
index|[]
name|data
init|=
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|1
block|}
block|,
block|{
literal|2
block|}
block|,
block|{
literal|10
block|}
block|,
block|{
literal|20
block|}
block|}
decl_stmt|;
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|data
argument_list|)
return|;
block|}
DECL|method|TestCopyListing (int numListstatusThreads)
specifier|public
name|TestCopyListing
parameter_list|(
name|int
name|numListstatusThreads
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|,
name|CREDENTIALS
argument_list|,
name|numListstatusThreads
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|TestCopyListing (Configuration configuration)
specifier|protected
name|TestCopyListing
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
block|{
name|super
argument_list|(
name|configuration
argument_list|,
name|CREDENTIALS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBytesToCopy ()
specifier|protected
name|long
name|getBytesToCopy
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getNumberOfPaths ()
specifier|protected
name|long
name|getNumberOfPaths
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testMultipleSrcToFile ()
specifier|public
name|void
name|testMultipleSrcToFile
parameter_list|()
block|{
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|srcPaths
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
name|srcPaths
operator|.
name|add
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/in/1"
argument_list|)
argument_list|)
expr_stmt|;
name|srcPaths
operator|.
name|add
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/in/2"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|target
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/out/1"
argument_list|)
decl_stmt|;
name|TestDistCpUtils
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
literal|"/tmp/in/1"
argument_list|)
expr_stmt|;
name|TestDistCpUtils
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
literal|"/tmp/in/2"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|target
argument_list|)
expr_stmt|;
specifier|final
name|DistCpOptions
name|options
init|=
operator|new
name|DistCpOptions
operator|.
name|Builder
argument_list|(
name|srcPaths
argument_list|,
name|target
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|validatePaths
argument_list|(
operator|new
name|DistCpContext
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
name|TestDistCpUtils
operator|.
name|delete
argument_list|(
name|fs
argument_list|,
literal|"/tmp"
argument_list|)
expr_stmt|;
comment|//No errors
name|fs
operator|.
name|create
argument_list|(
name|target
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|validatePaths
argument_list|(
operator|new
name|DistCpContext
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Invalid inputs accepted"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidInputException
name|ignore
parameter_list|)
block|{ }
name|TestDistCpUtils
operator|.
name|delete
argument_list|(
name|fs
argument_list|,
literal|"/tmp"
argument_list|)
expr_stmt|;
name|srcPaths
operator|.
name|clear
argument_list|()
expr_stmt|;
name|srcPaths
operator|.
name|add
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/in/1"
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/in/1"
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|target
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|validatePaths
argument_list|(
operator|new
name|DistCpContext
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Invalid inputs accepted"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidInputException
name|ignore
parameter_list|)
block|{ }
name|TestDistCpUtils
operator|.
name|delete
argument_list|(
name|fs
argument_list|,
literal|"/tmp"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception encountered "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Test input validation failed"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|TestDistCpUtils
operator|.
name|delete
argument_list|(
name|fs
argument_list|,
literal|"/tmp"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testDuplicates ()
specifier|public
name|void
name|testDuplicates
parameter_list|()
block|{
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|srcPaths
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
name|srcPaths
operator|.
name|add
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/in/*/*"
argument_list|)
argument_list|)
expr_stmt|;
name|TestDistCpUtils
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
literal|"/tmp/in/src1/1.txt"
argument_list|)
expr_stmt|;
name|TestDistCpUtils
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
literal|"/tmp/in/src2/1.txt"
argument_list|)
expr_stmt|;
name|Path
name|target
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/out"
argument_list|)
decl_stmt|;
name|Path
name|listingFile
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/list"
argument_list|)
decl_stmt|;
specifier|final
name|DistCpOptions
name|options
init|=
operator|new
name|DistCpOptions
operator|.
name|Builder
argument_list|(
name|srcPaths
argument_list|,
name|target
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|DistCpContext
name|context
init|=
operator|new
name|DistCpContext
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|CopyListing
name|listing
init|=
name|CopyListing
operator|.
name|getCopyListing
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|CREDENTIALS
argument_list|,
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|listing
operator|.
name|buildListing
argument_list|(
name|listingFile
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Duplicates not detected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DuplicateFileException
name|ignore
parameter_list|)
block|{       }
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception encountered in test"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Test failed "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|TestDistCpUtils
operator|.
name|delete
argument_list|(
name|fs
argument_list|,
literal|"/tmp"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testBuildListing ()
specifier|public
name|void
name|testBuildListing
parameter_list|()
block|{
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|srcPaths
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
name|Path
name|p1
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/in/1"
argument_list|)
decl_stmt|;
name|Path
name|p2
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/in/2"
argument_list|)
decl_stmt|;
name|Path
name|p3
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/in2/2"
argument_list|)
decl_stmt|;
name|Path
name|target
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/out/1"
argument_list|)
decl_stmt|;
name|srcPaths
operator|.
name|add
argument_list|(
name|p1
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|srcPaths
operator|.
name|add
argument_list|(
name|p3
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|TestDistCpUtils
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
literal|"/tmp/in/1"
argument_list|)
expr_stmt|;
name|TestDistCpUtils
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
literal|"/tmp/in/2"
argument_list|)
expr_stmt|;
name|TestDistCpUtils
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
literal|"/tmp/in2/2"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|OutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|p1
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"ABC"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|=
name|fs
operator|.
name|create
argument_list|(
name|p2
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"DEF"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|=
name|fs
operator|.
name|create
argument_list|(
name|p3
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"GHIJ"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|Path
name|listingFile
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/file"
argument_list|)
decl_stmt|;
specifier|final
name|DistCpOptions
name|options
init|=
operator|new
name|DistCpOptions
operator|.
name|Builder
argument_list|(
name|srcPaths
argument_list|,
name|target
argument_list|)
operator|.
name|withSyncFolder
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|CopyListing
name|listing
init|=
operator|new
name|SimpleCopyListing
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|CREDENTIALS
argument_list|)
decl_stmt|;
try|try
block|{
name|listing
operator|.
name|buildListing
argument_list|(
name|listingFile
argument_list|,
operator|new
name|DistCpContext
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Duplicates not detected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DuplicateFileException
name|ignore
parameter_list|)
block|{       }
name|Assert
operator|.
name|assertEquals
argument_list|(
name|listing
operator|.
name|getBytesToCopy
argument_list|()
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|listing
operator|.
name|getNumberOfPaths
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|TestDistCpUtils
operator|.
name|delete
argument_list|(
name|fs
argument_list|,
literal|"/tmp"
argument_list|)
expr_stmt|;
try|try
block|{
name|listing
operator|.
name|buildListing
argument_list|(
name|listingFile
argument_list|,
operator|new
name|DistCpContext
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Invalid input not detected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidInputException
name|ignore
parameter_list|)
block|{       }
name|TestDistCpUtils
operator|.
name|delete
argument_list|(
name|fs
argument_list|,
literal|"/tmp"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception encountered "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Test build listing failed"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|TestDistCpUtils
operator|.
name|delete
argument_list|(
name|fs
argument_list|,
literal|"/tmp"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testWithRandomFileListing ()
specifier|public
name|void
name|testWithRandomFileListing
parameter_list|()
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|srcPaths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|srcFiles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Path
name|target
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/out/1"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|pathCount
init|=
literal|25
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
name|pathCount
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|srcPaths
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|Path
name|fileName
init|=
operator|new
name|Path
argument_list|(
name|p
argument_list|,
name|i
operator|+
literal|".txt"
argument_list|)
decl_stmt|;
name|srcFiles
operator|.
name|add
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
try|try
init|(
name|OutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|fileName
argument_list|)
init|)
block|{
name|out
operator|.
name|write
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
name|Path
name|listingFile
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/file"
argument_list|)
decl_stmt|;
specifier|final
name|DistCpOptions
name|options
init|=
operator|new
name|DistCpOptions
operator|.
name|Builder
argument_list|(
name|srcPaths
argument_list|,
name|target
argument_list|)
operator|.
name|withSyncFolder
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Check without randomizing files
name|getConf
argument_list|()
operator|.
name|setBoolean
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_SIMPLE_LISTING_RANDOMIZE_FILES
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SimpleCopyListing
name|listing
init|=
operator|new
name|SimpleCopyListing
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|CREDENTIALS
argument_list|)
decl_stmt|;
name|listing
operator|.
name|buildListing
argument_list|(
name|listingFile
argument_list|,
operator|new
name|DistCpContext
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|listing
operator|.
name|getNumberOfPaths
argument_list|()
argument_list|,
name|pathCount
argument_list|)
expr_stmt|;
name|validateFinalListing
argument_list|(
name|listingFile
argument_list|,
name|srcFiles
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|listingFile
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Check with randomized file listing
name|getConf
argument_list|()
operator|.
name|setBoolean
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_SIMPLE_LISTING_RANDOMIZE_FILES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|listing
operator|=
operator|new
name|SimpleCopyListing
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|CREDENTIALS
argument_list|)
expr_stmt|;
comment|// Set the seed for randomness, so that it can be verified later
name|long
name|seed
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|listing
operator|.
name|setSeedForRandomListing
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|listing
operator|.
name|buildListing
argument_list|(
name|listingFile
argument_list|,
operator|new
name|DistCpContext
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|listing
operator|.
name|getNumberOfPaths
argument_list|()
argument_list|,
name|pathCount
argument_list|)
expr_stmt|;
comment|// validate randomness
name|Collections
operator|.
name|shuffle
argument_list|(
name|srcFiles
argument_list|,
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|)
expr_stmt|;
name|validateFinalListing
argument_list|(
name|listingFile
argument_list|,
name|srcFiles
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|TestDistCpUtils
operator|.
name|delete
argument_list|(
name|fs
argument_list|,
literal|"/tmp"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|validateFinalListing (Path pathToListFile, List<Path> srcFiles)
specifier|private
name|void
name|validateFinalListing
parameter_list|(
name|Path
name|pathToListFile
parameter_list|,
name|List
argument_list|<
name|Path
argument_list|>
name|srcFiles
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|pathToListFile
operator|.
name|getFileSystem
argument_list|(
name|config
argument_list|)
decl_stmt|;
try|try
init|(
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
name|config
argument_list|,
name|SequenceFile
operator|.
name|Reader
operator|.
name|file
argument_list|(
name|pathToListFile
argument_list|)
argument_list|)
init|)
block|{
name|CopyListingFileStatus
name|currentVal
init|=
operator|new
name|CopyListingFileStatus
argument_list|()
decl_stmt|;
name|Text
name|currentKey
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|next
argument_list|(
name|currentKey
argument_list|)
condition|)
block|{
name|reader
operator|.
name|getCurrentValue
argument_list|(
name|currentVal
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"srcFiles.size="
operator|+
name|srcFiles
operator|.
name|size
argument_list|()
operator|+
literal|", idx="
operator|+
name|idx
argument_list|,
name|fs
operator|.
name|makeQualified
argument_list|(
name|srcFiles
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
argument_list|,
name|currentVal
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"val="
operator|+
name|fs
operator|.
name|makeQualified
argument_list|(
name|srcFiles
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|idx
operator|++
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testBuildListingForSingleFile ()
specifier|public
name|void
name|testBuildListingForSingleFile
parameter_list|()
block|{
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
name|String
name|testRootString
init|=
literal|"/singleFileListing"
decl_stmt|;
name|Path
name|testRoot
init|=
operator|new
name|Path
argument_list|(
name|testRootString
argument_list|)
decl_stmt|;
name|SequenceFile
operator|.
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|testRoot
argument_list|)
condition|)
name|TestDistCpUtils
operator|.
name|delete
argument_list|(
name|fs
argument_list|,
name|testRootString
argument_list|)
expr_stmt|;
name|Path
name|sourceFile
init|=
operator|new
name|Path
argument_list|(
name|testRoot
argument_list|,
literal|"/source/foo/bar/source.txt"
argument_list|)
decl_stmt|;
name|Path
name|decoyFile
init|=
operator|new
name|Path
argument_list|(
name|testRoot
argument_list|,
literal|"/target/moo/source.txt"
argument_list|)
decl_stmt|;
name|Path
name|targetFile
init|=
operator|new
name|Path
argument_list|(
name|testRoot
argument_list|,
literal|"/target/moo/target.txt"
argument_list|)
decl_stmt|;
name|TestDistCpUtils
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|sourceFile
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|TestDistCpUtils
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|decoyFile
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|TestDistCpUtils
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|targetFile
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|srcPaths
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
name|srcPaths
operator|.
name|add
argument_list|(
name|sourceFile
argument_list|)
expr_stmt|;
name|DistCpOptions
name|options
init|=
operator|new
name|DistCpOptions
operator|.
name|Builder
argument_list|(
name|srcPaths
argument_list|,
name|targetFile
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|CopyListing
name|listing
init|=
operator|new
name|SimpleCopyListing
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|CREDENTIALS
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|listFile
init|=
operator|new
name|Path
argument_list|(
name|testRoot
argument_list|,
literal|"/tmp/fileList.seq"
argument_list|)
decl_stmt|;
name|listing
operator|.
name|buildListing
argument_list|(
name|listFile
argument_list|,
operator|new
name|DistCpContext
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|SequenceFile
operator|.
name|Reader
argument_list|(
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
expr_stmt|;
name|CopyListingFileStatus
name|fileStatus
init|=
operator|new
name|CopyListingFileStatus
argument_list|()
decl_stmt|;
name|Text
name|relativePath
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|reader
operator|.
name|next
argument_list|(
name|relativePath
argument_list|,
name|fileStatus
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|relativePath
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Unexpected exception encountered."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected exception: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|TestDistCpUtils
operator|.
name|delete
argument_list|(
name|fs
argument_list|,
name|testRootString
argument_list|)
expr_stmt|;
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
DECL|method|testFailOnCloseError ()
specifier|public
name|void
name|testFailOnCloseError
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|inFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"TestCopyListingIn"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|inFile
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|File
name|outFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"TestCopyListingOut"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|outFile
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|srcs
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
name|srcs
operator|.
name|add
argument_list|(
operator|new
name|Path
argument_list|(
name|inFile
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Exception
name|expectedEx
init|=
operator|new
name|IOException
argument_list|(
literal|"boom"
argument_list|)
decl_stmt|;
name|SequenceFile
operator|.
name|Writer
name|writer
init|=
name|mock
argument_list|(
name|SequenceFile
operator|.
name|Writer
operator|.
name|class
argument_list|)
decl_stmt|;
name|doThrow
argument_list|(
name|expectedEx
argument_list|)
operator|.
name|when
argument_list|(
name|writer
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|SimpleCopyListing
name|listing
init|=
operator|new
name|SimpleCopyListing
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|CREDENTIALS
argument_list|)
decl_stmt|;
specifier|final
name|DistCpOptions
name|options
init|=
operator|new
name|DistCpOptions
operator|.
name|Builder
argument_list|(
name|srcs
argument_list|,
operator|new
name|Path
argument_list|(
name|outFile
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Exception
name|actualEx
init|=
literal|null
decl_stmt|;
try|try
block|{
name|listing
operator|.
name|doBuildListing
argument_list|(
name|writer
argument_list|,
operator|new
name|DistCpContext
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|actualEx
operator|=
name|e
expr_stmt|;
block|}
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"close writer didn't fail"
argument_list|,
name|actualEx
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedEx
argument_list|,
name|actualEx
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

