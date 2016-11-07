begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|swift
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
name|FSMainOperationsBaseTest
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
name|swift
operator|.
name|http
operator|.
name|SwiftProtocolConstants
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
name|swift
operator|.
name|util
operator|.
name|SwiftTestUtils
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|swift
operator|.
name|SwiftTestConstants
operator|.
name|SWIFT_TEST_TIMEOUT
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
name|net
operator|.
name|URI
import|;
end_import

begin_class
DECL|class|TestFSMainOperationsSwift
specifier|public
class|class
name|TestFSMainOperationsSwift
extends|extends
name|FSMainOperationsBaseTest
block|{
annotation|@
name|Override
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
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|//small blocksize for faster remote tests
name|conf
operator|.
name|setInt
argument_list|(
name|SwiftProtocolConstants
operator|.
name|SWIFT_BLOCKSIZE
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|URI
name|serviceURI
init|=
name|SwiftTestUtils
operator|.
name|getServiceURI
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|fSys
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|serviceURI
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
DECL|field|wd
specifier|private
name|Path
name|wd
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|createFileSystem ()
specifier|protected
name|FileSystem
name|createFileSystem
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|fSys
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultWorkingDirectory ()
specifier|protected
name|Path
name|getDefaultWorkingDirectory
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|wd
operator|==
literal|null
condition|)
block|{
name|wd
operator|=
name|fSys
operator|.
name|getWorkingDirectory
argument_list|()
expr_stmt|;
block|}
return|return
name|wd
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testWDAbsolute ()
specifier|public
name|void
name|testWDAbsolute
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|absoluteDir
init|=
name|getTestRootPath
argument_list|(
name|fSys
argument_list|,
literal|"test/existingDir"
argument_list|)
decl_stmt|;
name|fSys
operator|.
name|mkdirs
argument_list|(
name|absoluteDir
argument_list|)
expr_stmt|;
name|fSys
operator|.
name|setWorkingDirectory
argument_list|(
name|absoluteDir
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|absoluteDir
argument_list|,
name|fSys
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testListStatusThrowsExceptionForUnreadableDir ()
specifier|public
name|void
name|testListStatusThrowsExceptionForUnreadableDir
parameter_list|()
block|{
name|SwiftTestUtils
operator|.
name|skip
argument_list|(
literal|"unsupported"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testGlobStatusThrowsExceptionForUnreadableDir ()
specifier|public
name|void
name|testGlobStatusThrowsExceptionForUnreadableDir
parameter_list|()
block|{
name|SwiftTestUtils
operator|.
name|skip
argument_list|(
literal|"unsupported"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testFsStatus ()
specifier|public
name|void
name|testFsStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testFsStatus
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testWorkingDirectory ()
specifier|public
name|void
name|testWorkingDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testWorkingDirectory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testMkdirs ()
specifier|public
name|void
name|testMkdirs
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testMkdirs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testMkdirsFailsForSubdirectoryOfExistingFile ()
specifier|public
name|void
name|testMkdirsFailsForSubdirectoryOfExistingFile
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testMkdirsFailsForSubdirectoryOfExistingFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testGetFileStatusThrowsExceptionForNonExistentFile ()
specifier|public
name|void
name|testGetFileStatusThrowsExceptionForNonExistentFile
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testGetFileStatusThrowsExceptionForNonExistentFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testListStatusThrowsExceptionForNonExistentFile ()
specifier|public
name|void
name|testListStatusThrowsExceptionForNonExistentFile
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testListStatusThrowsExceptionForNonExistentFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testListStatus ()
specifier|public
name|void
name|testListStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testListStatus
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testListStatusFilterWithNoMatches ()
specifier|public
name|void
name|testListStatusFilterWithNoMatches
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testListStatusFilterWithNoMatches
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testListStatusFilterWithSomeMatches ()
specifier|public
name|void
name|testListStatusFilterWithSomeMatches
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testListStatusFilterWithSomeMatches
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testGlobStatusNonExistentFile ()
specifier|public
name|void
name|testGlobStatusNonExistentFile
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testGlobStatusNonExistentFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testGlobStatusWithNoMatchesInPath ()
specifier|public
name|void
name|testGlobStatusWithNoMatchesInPath
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testGlobStatusWithNoMatchesInPath
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testGlobStatusSomeMatchesInDirectories ()
specifier|public
name|void
name|testGlobStatusSomeMatchesInDirectories
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testGlobStatusSomeMatchesInDirectories
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testGlobStatusWithMultipleWildCardMatches ()
specifier|public
name|void
name|testGlobStatusWithMultipleWildCardMatches
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testGlobStatusWithMultipleWildCardMatches
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testGlobStatusWithMultipleMatchesOfSingleChar ()
specifier|public
name|void
name|testGlobStatusWithMultipleMatchesOfSingleChar
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testGlobStatusWithMultipleMatchesOfSingleChar
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testGlobStatusFilterWithEmptyPathResults ()
specifier|public
name|void
name|testGlobStatusFilterWithEmptyPathResults
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testGlobStatusFilterWithEmptyPathResults
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testGlobStatusFilterWithSomePathMatchesAndTrivialFilter ()
specifier|public
name|void
name|testGlobStatusFilterWithSomePathMatchesAndTrivialFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testGlobStatusFilterWithSomePathMatchesAndTrivialFilter
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testGlobStatusFilterWithMultipleWildCardMatchesAndTrivialFilter ()
specifier|public
name|void
name|testGlobStatusFilterWithMultipleWildCardMatchesAndTrivialFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testGlobStatusFilterWithMultipleWildCardMatchesAndTrivialFilter
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testGlobStatusFilterWithMultiplePathMatchesAndNonTrivialFilter ()
specifier|public
name|void
name|testGlobStatusFilterWithMultiplePathMatchesAndNonTrivialFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testGlobStatusFilterWithMultiplePathMatchesAndNonTrivialFilter
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testGlobStatusFilterWithNoMatchingPathsAndNonTrivialFilter ()
specifier|public
name|void
name|testGlobStatusFilterWithNoMatchingPathsAndNonTrivialFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testGlobStatusFilterWithNoMatchingPathsAndNonTrivialFilter
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testGlobStatusFilterWithMultiplePathWildcardsAndNonTrivialFilter ()
specifier|public
name|void
name|testGlobStatusFilterWithMultiplePathWildcardsAndNonTrivialFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testGlobStatusFilterWithMultiplePathWildcardsAndNonTrivialFilter
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testWriteReadAndDeleteEmptyFile ()
specifier|public
name|void
name|testWriteReadAndDeleteEmptyFile
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testWriteReadAndDeleteEmptyFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testWriteReadAndDeleteHalfABlock ()
specifier|public
name|void
name|testWriteReadAndDeleteHalfABlock
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testWriteReadAndDeleteHalfABlock
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testWriteReadAndDeleteOneBlock ()
specifier|public
name|void
name|testWriteReadAndDeleteOneBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testWriteReadAndDeleteOneBlock
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testWriteReadAndDeleteOneAndAHalfBlocks ()
specifier|public
name|void
name|testWriteReadAndDeleteOneAndAHalfBlocks
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testWriteReadAndDeleteOneAndAHalfBlocks
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testWriteReadAndDeleteTwoBlocks ()
specifier|public
name|void
name|testWriteReadAndDeleteTwoBlocks
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testWriteReadAndDeleteTwoBlocks
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testOverwrite ()
specifier|public
name|void
name|testOverwrite
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|testOverwrite
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testWriteInNonExistentDirectory ()
specifier|public
name|void
name|testWriteInNonExistentDirectory
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|testWriteInNonExistentDirectory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testDeleteNonExistentFile ()
specifier|public
name|void
name|testDeleteNonExistentFile
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|testDeleteNonExistentFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testDeleteRecursively ()
specifier|public
name|void
name|testDeleteRecursively
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|testDeleteRecursively
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testDeleteEmptyDirectory ()
specifier|public
name|void
name|testDeleteEmptyDirectory
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|testDeleteEmptyDirectory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testRenameNonExistentPath ()
specifier|public
name|void
name|testRenameNonExistentPath
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testRenameNonExistentPath
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testRenameFileToNonExistentDirectory ()
specifier|public
name|void
name|testRenameFileToNonExistentDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testRenameFileToNonExistentDirectory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testRenameFileToDestinationWithParentFile ()
specifier|public
name|void
name|testRenameFileToDestinationWithParentFile
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testRenameFileToDestinationWithParentFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testRenameFileToExistingParent ()
specifier|public
name|void
name|testRenameFileToExistingParent
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testRenameFileToExistingParent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testRenameFileToItself ()
specifier|public
name|void
name|testRenameFileToItself
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testRenameFileToItself
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testRenameFileAsExistingFile ()
specifier|public
name|void
name|testRenameFileAsExistingFile
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testRenameFileAsExistingFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testRenameFileAsExistingDirectory ()
specifier|public
name|void
name|testRenameFileAsExistingDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testRenameFileAsExistingDirectory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testRenameDirectoryToItself ()
specifier|public
name|void
name|testRenameDirectoryToItself
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testRenameDirectoryToItself
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testRenameDirectoryToNonExistentParent ()
specifier|public
name|void
name|testRenameDirectoryToNonExistentParent
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testRenameDirectoryToNonExistentParent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testRenameDirectoryAsNonExistentDirectory ()
specifier|public
name|void
name|testRenameDirectoryAsNonExistentDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testRenameDirectoryAsNonExistentDirectory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testRenameDirectoryAsEmptyDirectory ()
specifier|public
name|void
name|testRenameDirectoryAsEmptyDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testRenameDirectoryAsEmptyDirectory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testRenameDirectoryAsNonEmptyDirectory ()
specifier|public
name|void
name|testRenameDirectoryAsNonEmptyDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testRenameDirectoryAsNonEmptyDirectory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testRenameDirectoryAsFile ()
specifier|public
name|void
name|testRenameDirectoryAsFile
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testRenameDirectoryAsFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testInputStreamClosedTwice ()
specifier|public
name|void
name|testInputStreamClosedTwice
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|testInputStreamClosedTwice
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testOutputStreamClosedTwice ()
specifier|public
name|void
name|testOutputStreamClosedTwice
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|testOutputStreamClosedTwice
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testGetWrappedInputStream ()
specifier|public
name|void
name|testGetWrappedInputStream
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|testGetWrappedInputStream
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
annotation|@
name|Override
DECL|method|testCopyToLocalWithUseRawLocalFileSystemOption ()
specifier|public
name|void
name|testCopyToLocalWithUseRawLocalFileSystemOption
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testCopyToLocalWithUseRawLocalFileSystemOption
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

