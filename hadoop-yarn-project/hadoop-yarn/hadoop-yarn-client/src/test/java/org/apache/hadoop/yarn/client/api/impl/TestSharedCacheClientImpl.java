begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
operator|.
name|api
operator|.
name|impl
package|;
end_package

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
name|assertNull
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
name|isA
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
name|FileNotFoundException
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
name|yarn
operator|.
name|api
operator|.
name|ClientSCMProtocol
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|ReleaseSharedCacheResourceRequest
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|UseSharedCacheResourceRequest
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|UseSharedCacheResourceResponse
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|UseSharedCacheResourceResponsePBImpl
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|URL
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
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
name|AfterClass
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

begin_class
DECL|class|TestSharedCacheClientImpl
specifier|public
class|class
name|TestSharedCacheClientImpl
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
name|TestSharedCacheClientImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|client
specifier|public
specifier|static
name|SharedCacheClientImpl
name|client
decl_stmt|;
DECL|field|cProtocol
specifier|public
specifier|static
name|ClientSCMProtocol
name|cProtocol
decl_stmt|;
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
name|Path
name|TEST_ROOT_DIR
decl_stmt|;
DECL|field|localFs
specifier|private
specifier|static
name|FileSystem
name|localFs
decl_stmt|;
DECL|field|input
specifier|private
specifier|static
name|String
name|input
init|=
literal|"This is a test file."
decl_stmt|;
DECL|field|inputChecksumSHA256
specifier|private
specifier|static
name|String
name|inputChecksumSHA256
init|=
literal|"f29bc64a9d3732b4b9035125fdb3285f5b6455778edca72414671e0ca3b2e0de"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass ()
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|IOException
block|{
name|localFs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|TEST_ROOT_DIR
operator|=
operator|new
name|Path
argument_list|(
literal|"target"
argument_list|,
name|TestSharedCacheClientImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-tmpDir"
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|localFs
operator|.
name|getUri
argument_list|()
argument_list|,
name|localFs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass ()
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|localFs
operator|!=
literal|null
condition|)
block|{
name|localFs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"IO exception in closing file system)"
argument_list|)
expr_stmt|;
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|cProtocol
operator|=
name|mock
argument_list|(
name|ClientSCMProtocol
operator|.
name|class
argument_list|)
expr_stmt|;
name|client
operator|=
operator|new
name|SharedCacheClientImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ClientSCMProtocol
name|createClientProxy
parameter_list|()
block|{
return|return
name|cProtocol
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|stopClientProxy
parameter_list|()
block|{
comment|// do nothing because it is mocked
block|}
block|}
expr_stmt|;
name|client
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|client
operator|.
name|stop
argument_list|()
expr_stmt|;
name|client
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUseCacheMiss ()
specifier|public
name|void
name|testUseCacheMiss
parameter_list|()
throws|throws
name|Exception
block|{
name|UseSharedCacheResourceResponse
name|response
init|=
operator|new
name|UseSharedCacheResourceResponsePBImpl
argument_list|()
decl_stmt|;
name|response
operator|.
name|setPath
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|cProtocol
operator|.
name|use
argument_list|(
name|isA
argument_list|(
name|UseSharedCacheResourceRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|URL
name|newURL
init|=
name|client
operator|.
name|use
argument_list|(
name|mock
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
argument_list|,
literal|"key"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"The path is not null!"
argument_list|,
name|newURL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUseCacheHit ()
specifier|public
name|void
name|testUseCacheHit
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"viewfs://test/path"
argument_list|)
decl_stmt|;
name|URL
name|useUrl
init|=
name|URL
operator|.
name|fromPath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"viewfs://test/path"
argument_list|)
argument_list|)
decl_stmt|;
name|UseSharedCacheResourceResponse
name|response
init|=
operator|new
name|UseSharedCacheResourceResponsePBImpl
argument_list|()
decl_stmt|;
name|response
operator|.
name|setPath
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|cProtocol
operator|.
name|use
argument_list|(
name|isA
argument_list|(
name|UseSharedCacheResourceRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|URL
name|newURL
init|=
name|client
operator|.
name|use
argument_list|(
name|mock
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
argument_list|,
literal|"key"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"The paths are not equal!"
argument_list|,
name|useUrl
argument_list|,
name|newURL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|YarnException
operator|.
name|class
argument_list|)
DECL|method|testUseError ()
specifier|public
name|void
name|testUseError
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|message
init|=
literal|"Mock IOExcepiton!"
decl_stmt|;
name|when
argument_list|(
name|cProtocol
operator|.
name|use
argument_list|(
name|isA
argument_list|(
name|UseSharedCacheResourceRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
operator|new
name|IOException
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|use
argument_list|(
name|mock
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
argument_list|,
literal|"key"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRelease ()
specifier|public
name|void
name|testRelease
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Release does not care about the return value because it is empty
name|when
argument_list|(
name|cProtocol
operator|.
name|release
argument_list|(
name|isA
argument_list|(
name|ReleaseSharedCacheResourceRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|release
argument_list|(
name|mock
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
argument_list|,
literal|"key"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|YarnException
operator|.
name|class
argument_list|)
DECL|method|testReleaseError ()
specifier|public
name|void
name|testReleaseError
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|message
init|=
literal|"Mock IOExcepiton!"
decl_stmt|;
name|when
argument_list|(
name|cProtocol
operator|.
name|release
argument_list|(
name|isA
argument_list|(
name|ReleaseSharedCacheResourceRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
operator|new
name|IOException
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|release
argument_list|(
name|mock
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
argument_list|,
literal|"key"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChecksum ()
specifier|public
name|void
name|testChecksum
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|filename
init|=
literal|"test1.txt"
decl_stmt|;
name|Path
name|file
init|=
name|makeFile
argument_list|(
name|filename
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|inputChecksumSHA256
argument_list|,
name|client
operator|.
name|getFileChecksum
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileNotFoundException
operator|.
name|class
argument_list|)
DECL|method|testNonexistantFileChecksum ()
specifier|public
name|void
name|testNonexistantFileChecksum
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"non-existant-file"
argument_list|)
decl_stmt|;
name|client
operator|.
name|getFileChecksum
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
DECL|method|makeFile (String filename)
specifier|private
name|Path
name|makeFile
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
name|filename
argument_list|)
decl_stmt|;
name|DataOutputStream
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|out
operator|=
name|localFs
operator|.
name|create
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|input
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|file
return|;
block|}
block|}
end_class

end_unit

