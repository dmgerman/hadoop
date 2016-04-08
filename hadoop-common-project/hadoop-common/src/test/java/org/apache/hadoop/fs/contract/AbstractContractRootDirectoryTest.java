begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.contract
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|contract
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
name|junit
operator|.
name|Test
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
name|java
operator|.
name|io
operator|.
name|IOException
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|contract
operator|.
name|ContractTestUtils
operator|.
name|createFile
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|contract
operator|.
name|ContractTestUtils
operator|.
name|dataset
import|;
end_import

begin_comment
comment|/**  * This class does things to the root directory.  * Only subclass this for tests against transient filesystems where  * you don't care about the data.  */
end_comment

begin_class
DECL|class|AbstractContractRootDirectoryTest
specifier|public
specifier|abstract
class|class
name|AbstractContractRootDirectoryTest
extends|extends
name|AbstractFSContractTestBase
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
name|AbstractContractRootDirectoryTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|skipIfUnsupported
argument_list|(
name|TEST_ROOT_TESTS_ENABLED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMkDirDepth1 ()
specifier|public
name|void
name|testMkDirDepth1
parameter_list|()
throws|throws
name|Throwable
block|{
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/testmkdirdepth1"
argument_list|)
decl_stmt|;
name|assertPathDoesNotExist
argument_list|(
literal|"directory already exists"
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertIsDirectory
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertPathExists
argument_list|(
literal|"directory already exists"
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|assertDeleted
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRmEmptyRootDirNonRecursive ()
specifier|public
name|void
name|testRmEmptyRootDirNonRecursive
parameter_list|()
throws|throws
name|Throwable
block|{
comment|//extra sanity checks here to avoid support calls about complete loss of data
name|skipIfUnsupported
argument_list|(
name|TEST_ROOT_TESTS_ENABLED
argument_list|)
expr_stmt|;
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertIsDirectory
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|boolean
name|deleted
init|=
name|getFileSystem
argument_list|()
operator|.
name|delete
argument_list|(
name|root
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"rm / of empty dir result is {}"
argument_list|,
name|deleted
argument_list|)
expr_stmt|;
name|assertIsDirectory
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRmNonEmptyRootDirNonRecursive ()
specifier|public
name|void
name|testRmNonEmptyRootDirNonRecursive
parameter_list|()
throws|throws
name|Throwable
block|{
comment|//extra sanity checks here to avoid support calls about complete loss of data
name|skipIfUnsupported
argument_list|(
name|TEST_ROOT_TESTS_ENABLED
argument_list|)
expr_stmt|;
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|String
name|touchfile
init|=
literal|"/testRmNonEmptyRootDirNonRecursive"
decl_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|touchfile
argument_list|)
decl_stmt|;
name|ContractTestUtils
operator|.
name|touch
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|file
argument_list|)
expr_stmt|;
name|assertIsDirectory
argument_list|(
name|root
argument_list|)
expr_stmt|;
try|try
block|{
name|boolean
name|deleted
init|=
name|getFileSystem
argument_list|()
operator|.
name|delete
argument_list|(
name|root
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"non recursive delete should have raised an exception,"
operator|+
literal|" but completed with exit code "
operator|+
name|deleted
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//expected
name|handleExpectedException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|getFileSystem
argument_list|()
operator|.
name|delete
argument_list|(
name|file
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|assertIsDirectory
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRmRootRecursive ()
specifier|public
name|void
name|testRmRootRecursive
parameter_list|()
throws|throws
name|Throwable
block|{
comment|//extra sanity checks here to avoid support calls about complete loss of data
name|skipIfUnsupported
argument_list|(
name|TEST_ROOT_TESTS_ENABLED
argument_list|)
expr_stmt|;
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertIsDirectory
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"/testRmRootRecursive"
argument_list|)
decl_stmt|;
name|ContractTestUtils
operator|.
name|touch
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|file
argument_list|)
expr_stmt|;
name|boolean
name|deleted
init|=
name|getFileSystem
argument_list|()
operator|.
name|delete
argument_list|(
name|root
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertIsDirectory
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"rm -rf / result is {}"
argument_list|,
name|deleted
argument_list|)
expr_stmt|;
if|if
condition|(
name|deleted
condition|)
block|{
name|assertPathDoesNotExist
argument_list|(
literal|"expected file to be deleted"
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertPathExists
argument_list|(
literal|"expected file to be preserved"
argument_list|,
name|file
argument_list|)
expr_stmt|;
empty_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCreateFileOverRoot ()
specifier|public
name|void
name|testCreateFileOverRoot
parameter_list|()
throws|throws
name|Throwable
block|{
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|dataset
init|=
name|dataset
argument_list|(
literal|1024
argument_list|,
literal|' '
argument_list|,
literal|'z'
argument_list|)
decl_stmt|;
try|try
block|{
name|createFile
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|root
argument_list|,
literal|false
argument_list|,
name|dataset
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected an exception, got a file created over root: "
operator|+
name|ls
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//expected
name|handleExpectedException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|assertIsDirectory
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testListEmptyRootDirectory ()
specifier|public
name|void
name|testListEmptyRootDirectory
parameter_list|()
throws|throws
name|IOException
block|{
comment|//extra sanity checks here to avoid support calls about complete loss of data
name|skipIfUnsupported
argument_list|(
name|TEST_ROOT_TESTS_ENABLED
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|FileStatus
index|[]
name|statuses
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|root
argument_list|)
decl_stmt|;
for|for
control|(
name|FileStatus
name|status
range|:
name|statuses
control|)
block|{
name|ContractTestUtils
operator|.
name|assertDeleted
argument_list|(
name|fs
argument_list|,
name|status
operator|.
name|getPath
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"listStatus on empty root-directory returned a non-empty list"
argument_list|,
literal|0
argument_list|,
name|fs
operator|.
name|listStatus
argument_list|(
name|root
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

