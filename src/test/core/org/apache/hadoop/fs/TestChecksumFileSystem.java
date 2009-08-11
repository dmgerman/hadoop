begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
DECL|class|TestChecksumFileSystem
specifier|public
class|class
name|TestChecksumFileSystem
extends|extends
name|TestCase
block|{
DECL|method|testgetChecksumLength ()
specifier|public
name|void
name|testgetChecksumLength
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|ChecksumFileSystem
operator|.
name|getChecksumLength
argument_list|(
literal|0L
argument_list|,
literal|512
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|ChecksumFileSystem
operator|.
name|getChecksumLength
argument_list|(
literal|1L
argument_list|,
literal|512
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|ChecksumFileSystem
operator|.
name|getChecksumLength
argument_list|(
literal|512L
argument_list|,
literal|512
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|ChecksumFileSystem
operator|.
name|getChecksumLength
argument_list|(
literal|513L
argument_list|,
literal|512
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|ChecksumFileSystem
operator|.
name|getChecksumLength
argument_list|(
literal|1023L
argument_list|,
literal|512
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|ChecksumFileSystem
operator|.
name|getChecksumLength
argument_list|(
literal|1024L
argument_list|,
literal|512
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|408
argument_list|,
name|ChecksumFileSystem
operator|.
name|getChecksumLength
argument_list|(
literal|100L
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4000000000008L
argument_list|,
name|ChecksumFileSystem
operator|.
name|getChecksumLength
argument_list|(
literal|10000000000000L
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testVerifyChecksum ()
specifier|public
name|void
name|testVerifyChecksum
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|TEST_ROOT_DIR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"build/test/data/work-dir/localfs"
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|LocalFileSystem
name|localFs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|testPath
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"testPath"
argument_list|)
decl_stmt|;
name|Path
name|testPath11
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"testPath11"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|fout
init|=
name|localFs
operator|.
name|create
argument_list|(
name|testPath
argument_list|)
decl_stmt|;
name|fout
operator|.
name|write
argument_list|(
literal|"testing"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fout
operator|.
name|close
argument_list|()
expr_stmt|;
name|fout
operator|=
name|localFs
operator|.
name|create
argument_list|(
name|testPath11
argument_list|)
expr_stmt|;
name|fout
operator|.
name|write
argument_list|(
literal|"testing you"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fout
operator|.
name|close
argument_list|()
expr_stmt|;
name|TestLocalFileSystem
operator|.
name|readFile
argument_list|(
name|localFs
argument_list|,
name|testPath
argument_list|,
literal|128
argument_list|)
expr_stmt|;
name|TestLocalFileSystem
operator|.
name|readFile
argument_list|(
name|localFs
argument_list|,
name|testPath
argument_list|,
literal|512
argument_list|)
expr_stmt|;
name|TestLocalFileSystem
operator|.
name|readFile
argument_list|(
name|localFs
argument_list|,
name|testPath
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|localFs
operator|.
name|delete
argument_list|(
name|localFs
operator|.
name|getChecksumFile
argument_list|(
name|testPath
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"checksum deleted"
argument_list|,
operator|!
name|localFs
operator|.
name|exists
argument_list|(
name|localFs
operator|.
name|getChecksumFile
argument_list|(
name|testPath
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//copying the wrong checksum file
name|FileUtil
operator|.
name|copy
argument_list|(
name|localFs
argument_list|,
name|localFs
operator|.
name|getChecksumFile
argument_list|(
name|testPath11
argument_list|)
argument_list|,
name|localFs
argument_list|,
name|localFs
operator|.
name|getChecksumFile
argument_list|(
name|testPath
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"checksum exists"
argument_list|,
name|localFs
operator|.
name|exists
argument_list|(
name|localFs
operator|.
name|getChecksumFile
argument_list|(
name|testPath
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|errorRead
init|=
literal|false
decl_stmt|;
try|try
block|{
name|TestLocalFileSystem
operator|.
name|readFile
argument_list|(
name|localFs
argument_list|,
name|testPath
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ChecksumException
name|ie
parameter_list|)
block|{
name|errorRead
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"error reading"
argument_list|,
name|errorRead
argument_list|)
expr_stmt|;
comment|//now setting verify false, the read should succeed
name|localFs
operator|.
name|setVerifyChecksum
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|String
name|str
init|=
name|TestLocalFileSystem
operator|.
name|readFile
argument_list|(
name|localFs
argument_list|,
name|testPath
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"read"
argument_list|,
literal|"testing"
operator|.
name|equals
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

