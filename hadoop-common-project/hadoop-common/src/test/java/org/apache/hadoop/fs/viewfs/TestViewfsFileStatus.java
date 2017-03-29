begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.viewfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|viewfs
package|;
end_package

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
name|FileOutputStream
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
name|URISyntaxException
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
name|FileUtil
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
name|FsConstants
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
name|contract
operator|.
name|ContractTestUtils
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
name|DataInputBuffer
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
name|DataOutputBuffer
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
name|AfterClass
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
name|mockito
operator|.
name|Mockito
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
name|*
import|;
end_import

begin_comment
comment|/**  * The FileStatus is being serialized in MR as jobs are submitted.  * Since viewfs has overlayed ViewFsFileStatus, we ran into  * serialization problems. THis test is test the fix.  */
end_comment

begin_class
DECL|class|TestViewfsFileStatus
specifier|public
class|class
name|TestViewfsFileStatus
block|{
DECL|field|TEST_DIR
specifier|private
specifier|static
specifier|final
name|File
name|TEST_DIR
init|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
name|TestViewfsFileStatus
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testFileStatusSerialziation ()
specifier|public
name|void
name|testFileStatusSerialziation
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|String
name|testfilename
init|=
literal|"testFileStatusSerialziation"
decl_stmt|;
name|TEST_DIR
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|infile
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
name|testfilename
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|content
init|=
literal|"dingos"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|FileOutputStream
name|fos
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fos
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|infile
argument_list|)
expr_stmt|;
name|fos
operator|.
name|write
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|fos
operator|!=
literal|null
condition|)
block|{
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
operator|(
name|long
operator|)
name|content
operator|.
name|length
argument_list|,
name|infile
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|ConfigUtil
operator|.
name|addLink
argument_list|(
name|conf
argument_list|,
literal|"/foo/bar/baz"
argument_list|,
name|TEST_DIR
operator|.
name|toURI
argument_list|()
argument_list|)
expr_stmt|;
name|FileSystem
name|vfs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|FsConstants
operator|.
name|VIEWFS_URI
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ViewFileSystem
operator|.
name|class
argument_list|,
name|vfs
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/foo/bar/baz"
argument_list|,
name|testfilename
argument_list|)
decl_stmt|;
name|FileStatus
name|stat
init|=
name|vfs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|content
operator|.
name|length
argument_list|,
name|stat
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|assertNotErasureCoded
argument_list|(
name|vfs
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|+
literal|" should have erasure coding unset in "
operator|+
literal|"FileStatus#toString(): "
operator|+
name|stat
argument_list|,
name|stat
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"isErasureCoded=false"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check serialization/deserialization
name|DataOutputBuffer
name|dob
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|stat
operator|.
name|write
argument_list|(
name|dob
argument_list|)
expr_stmt|;
name|DataInputBuffer
name|dib
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|dib
operator|.
name|reset
argument_list|(
name|dob
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|dob
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|FileStatus
name|deSer
init|=
operator|new
name|FileStatus
argument_list|()
decl_stmt|;
name|deSer
operator|.
name|readFields
argument_list|(
name|dib
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|content
operator|.
name|length
argument_list|,
name|deSer
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|deSer
operator|.
name|isErasureCoded
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Tests that ViewFileSystem.getFileChecksum calls res.targetFileSystem
comment|// .getFileChecksum with res.remainingPath and not with f
annotation|@
name|Test
DECL|method|testGetFileChecksum ()
specifier|public
name|void
name|testGetFileChecksum
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/someFile"
argument_list|)
decl_stmt|;
name|FileSystem
name|mockFS
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|InodeTree
operator|.
name|ResolveResult
argument_list|<
name|FileSystem
argument_list|>
name|res
init|=
operator|new
name|InodeTree
operator|.
name|ResolveResult
argument_list|<
name|FileSystem
argument_list|>
argument_list|(
literal|null
argument_list|,
name|mockFS
argument_list|,
literal|null
argument_list|,
operator|new
name|Path
argument_list|(
literal|"someFile"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|InodeTree
argument_list|<
name|FileSystem
argument_list|>
name|fsState
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|InodeTree
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|fsState
operator|.
name|resolve
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|ViewFileSystem
name|vfs
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ViewFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|vfs
operator|.
name|fsState
operator|=
name|fsState
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|vfs
operator|.
name|getFileChecksum
argument_list|(
name|path
argument_list|)
argument_list|)
operator|.
name|thenCallRealMethod
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|vfs
operator|.
name|getUriPath
argument_list|(
name|path
argument_list|)
argument_list|)
operator|.
name|thenCallRealMethod
argument_list|()
expr_stmt|;
name|vfs
operator|.
name|getFileChecksum
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockFS
argument_list|)
operator|.
name|getFileChecksum
argument_list|(
operator|new
name|Path
argument_list|(
literal|"someFile"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|cleanup ()
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|TEST_DIR
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

