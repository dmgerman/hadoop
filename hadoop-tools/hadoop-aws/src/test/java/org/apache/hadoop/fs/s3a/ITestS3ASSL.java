begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

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
name|junit
operator|.
name|Test
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
name|security
operator|.
name|ssl
operator|.
name|OpenSSLSocketFactory
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
name|util
operator|.
name|NativeCodeLoader
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
name|writeDataset
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_comment
comment|/**  * Tests non-default values for {@link Constants#SSL_CHANNEL_MODE}.  */
end_comment

begin_class
DECL|class|ITestS3ASSL
specifier|public
class|class
name|ITestS3ASSL
extends|extends
name|AbstractS3ATestBase
block|{
annotation|@
name|Test
DECL|method|testOpenSSL ()
specifier|public
name|void
name|testOpenSSL
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeTrue
argument_list|(
name|NativeCodeLoader
operator|.
name|buildSupportsOpenssl
argument_list|()
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setEnum
argument_list|(
name|Constants
operator|.
name|SSL_CHANNEL_MODE
argument_list|,
name|OpenSSLSocketFactory
operator|.
name|SSLChannelMode
operator|.
name|OpenSSL
argument_list|)
expr_stmt|;
try|try
init|(
name|S3AFileSystem
name|fs
init|=
name|S3ATestUtils
operator|.
name|createTestFileSystem
argument_list|(
name|conf
argument_list|)
init|)
block|{
name|writeThenReadFile
argument_list|(
name|fs
argument_list|,
name|path
argument_list|(
literal|"ITestS3ASSL/testOpenSSL"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testJSEE ()
specifier|public
name|void
name|testJSEE
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setEnum
argument_list|(
name|Constants
operator|.
name|SSL_CHANNEL_MODE
argument_list|,
name|OpenSSLSocketFactory
operator|.
name|SSLChannelMode
operator|.
name|Default_JSSE
argument_list|)
expr_stmt|;
try|try
init|(
name|S3AFileSystem
name|fs
init|=
name|S3ATestUtils
operator|.
name|createTestFileSystem
argument_list|(
name|conf
argument_list|)
init|)
block|{
name|writeThenReadFile
argument_list|(
name|fs
argument_list|,
name|path
argument_list|(
literal|"ITestS3ASSL/testJSEE"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Helper function that writes and then reads a file. Unlike    * {@link #writeThenReadFile(Path, int)} it takes a {@link FileSystem} as a    * parameter.    */
DECL|method|writeThenReadFile (FileSystem fs, Path path)
specifier|private
name|void
name|writeThenReadFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
name|dataset
argument_list|(
literal|1024
argument_list|,
literal|'a'
argument_list|,
literal|'z'
argument_list|)
decl_stmt|;
name|writeDataset
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|data
argument_list|,
name|data
operator|.
name|length
argument_list|,
literal|1024
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|verifyFileContents
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

