begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

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
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|security
operator|.
name|PrivilegedExceptionAction
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
name|junit
operator|.
name|Assert
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
name|hdfs
operator|.
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|security
operator|.
name|SecurityUtilTestHelper
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
name|UserGroupInformation
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
name|token
operator|.
name|Token
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
name|token
operator|.
name|TokenIdentifier
import|;
end_import

begin_class
DECL|class|TestHftpDelegationToken
specifier|public
class|class
name|TestHftpDelegationToken
block|{
annotation|@
name|Test
DECL|method|testHdfsDelegationToken ()
specifier|public
name|void
name|testHdfsDelegationToken
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|user
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"oom"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"memory"
block|}
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|?
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
name|DelegationTokenIdentifier
operator|.
name|HDFS_DELEGATION_KIND
argument_list|,
operator|new
name|Text
argument_list|(
literal|"127.0.0.1:8020"
argument_list|)
argument_list|)
decl_stmt|;
name|user
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|?
argument_list|>
name|token2
init|=
operator|new
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|Text
argument_list|(
literal|"other token"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"127.0.0.1:8020"
argument_list|)
argument_list|)
decl_stmt|;
name|user
operator|.
name|addToken
argument_list|(
name|token2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong tokens in user"
argument_list|,
literal|2
argument_list|,
name|user
operator|.
name|getTokens
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|user
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|FileSystem
argument_list|>
argument_list|()
block|{
specifier|public
name|FileSystem
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
literal|"hftp://localhost:50470/"
argument_list|)
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"wrong kind of file system"
argument_list|,
name|HftpFileSystem
operator|.
name|class
argument_list|,
name|fs
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|Field
name|renewToken
init|=
name|HftpFileSystem
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"renewToken"
argument_list|)
decl_stmt|;
name|renewToken
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"wrong token"
argument_list|,
name|token
argument_list|,
name|renewToken
operator|.
name|get
argument_list|(
name|fs
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSelectHdfsDelegationToken ()
specifier|public
name|void
name|testSelectHdfsDelegationToken
parameter_list|()
throws|throws
name|Exception
block|{
name|SecurityUtilTestHelper
operator|.
name|setTokenServiceUseIp
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|URI
name|hftpUri
init|=
name|URI
operator|.
name|create
argument_list|(
literal|"hftp://localhost:0"
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|Token
argument_list|<
name|?
argument_list|>
name|token
init|=
literal|null
decl_stmt|;
comment|// test fallback to hdfs token
name|Token
argument_list|<
name|?
argument_list|>
name|hdfsToken
init|=
operator|new
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
name|DelegationTokenIdentifier
operator|.
name|HDFS_DELEGATION_KIND
argument_list|,
operator|new
name|Text
argument_list|(
literal|"127.0.0.1:8020"
argument_list|)
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|addToken
argument_list|(
name|hdfsToken
argument_list|)
expr_stmt|;
name|HftpFileSystem
name|fs
init|=
operator|(
name|HftpFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|hftpUri
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|token
operator|=
name|fs
operator|.
name|selectDelegationToken
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hdfsToken
argument_list|,
name|token
argument_list|)
expr_stmt|;
comment|// test hftp is favored over hdfs
name|Token
argument_list|<
name|?
argument_list|>
name|hftpToken
init|=
operator|new
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
name|HftpFileSystem
operator|.
name|TOKEN_KIND
argument_list|,
operator|new
name|Text
argument_list|(
literal|"127.0.0.1:0"
argument_list|)
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|addToken
argument_list|(
name|hftpToken
argument_list|)
expr_stmt|;
name|token
operator|=
name|fs
operator|.
name|selectDelegationToken
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hftpToken
argument_list|,
name|token
argument_list|)
expr_stmt|;
comment|// switch to using host-based tokens, no token should match
name|SecurityUtilTestHelper
operator|.
name|setTokenServiceUseIp
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|token
operator|=
name|fs
operator|.
name|selectDelegationToken
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
comment|// test fallback to hdfs token
name|hdfsToken
operator|=
operator|new
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
name|DelegationTokenIdentifier
operator|.
name|HDFS_DELEGATION_KIND
argument_list|,
operator|new
name|Text
argument_list|(
literal|"localhost:8020"
argument_list|)
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|addToken
argument_list|(
name|hdfsToken
argument_list|)
expr_stmt|;
name|token
operator|=
name|fs
operator|.
name|selectDelegationToken
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hdfsToken
argument_list|,
name|token
argument_list|)
expr_stmt|;
comment|// test hftp is favored over hdfs
name|hftpToken
operator|=
operator|new
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
name|HftpFileSystem
operator|.
name|TOKEN_KIND
argument_list|,
operator|new
name|Text
argument_list|(
literal|"localhost:0"
argument_list|)
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|addToken
argument_list|(
name|hftpToken
argument_list|)
expr_stmt|;
name|token
operator|=
name|fs
operator|.
name|selectDelegationToken
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hftpToken
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

