begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
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
name|security
operator|.
name|UserGroupInformation
operator|.
name|AuthenticationMethod
operator|.
name|KERBEROS
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
name|assertEquals
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
name|java
operator|.
name|net
operator|.
name|URL
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
name|permission
operator|.
name|FsAction
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
name|hdfs
operator|.
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenSecretManager
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
name|server
operator|.
name|namenode
operator|.
name|FSNamesystem
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
name|web
operator|.
name|resources
operator|.
name|DelegationParam
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
name|web
operator|.
name|resources
operator|.
name|DoAsParam
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
name|web
operator|.
name|resources
operator|.
name|GetOpParam
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
name|web
operator|.
name|resources
operator|.
name|PutOpParam
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
name|web
operator|.
name|resources
operator|.
name|TokenArgumentParam
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
name|web
operator|.
name|resources
operator|.
name|UserParam
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
name|web
operator|.
name|resources
operator|.
name|FsActionParam
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
name|net
operator|.
name|NetUtils
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
name|SecurityUtil
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

begin_class
DECL|class|TestWebHdfsUrl
specifier|public
class|class
name|TestWebHdfsUrl
block|{
comment|// NOTE: port is never used
DECL|field|uri
specifier|final
name|URI
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
name|WebHdfsFileSystem
operator|.
name|SCHEME
operator|+
literal|"://"
operator|+
literal|"127.0.0.1:0"
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|resetUGI ()
specifier|public
name|void
name|resetUGI
parameter_list|()
block|{
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testEncodedPathUrl ()
specifier|public
name|void
name|testEncodedPathUrl
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|WebHdfsFileSystem
name|webhdfs
init|=
operator|(
name|WebHdfsFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// Construct a file path that contains percentage-encoded string
name|String
name|pathName
init|=
literal|"/hdtest010%2C60020%2C1371000602151.1371058984668"
decl_stmt|;
name|Path
name|fsPath
init|=
operator|new
name|Path
argument_list|(
name|pathName
argument_list|)
decl_stmt|;
name|URL
name|encodedPathUrl
init|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|PutOpParam
operator|.
name|Op
operator|.
name|CREATE
argument_list|,
name|fsPath
argument_list|)
decl_stmt|;
comment|// We should get back the original file path after cycling back and decoding
name|Assert
operator|.
name|assertEquals
argument_list|(
name|WebHdfsFileSystem
operator|.
name|PATH_PREFIX
operator|+
name|pathName
argument_list|,
name|encodedPathUrl
operator|.
name|toURI
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testSimpleAuthParamsInUrl ()
specifier|public
name|void
name|testSimpleAuthParamsInUrl
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"test-user"
argument_list|)
decl_stmt|;
name|UserGroupInformation
operator|.
name|setLoginUser
argument_list|(
name|ugi
argument_list|)
expr_stmt|;
name|WebHdfsFileSystem
name|webhdfs
init|=
name|getWebHdfsFileSystem
argument_list|(
name|ugi
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Path
name|fsPath
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
comment|// send user+token
name|URL
name|fileStatusUrl
init|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|GetOpParam
operator|.
name|Op
operator|.
name|GETFILESTATUS
argument_list|,
name|fsPath
argument_list|)
decl_stmt|;
name|checkQueryParams
argument_list|(
operator|new
name|String
index|[]
block|{
name|GetOpParam
operator|.
name|Op
operator|.
name|GETFILESTATUS
operator|.
name|toQueryString
argument_list|()
block|,
operator|new
name|UserParam
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|}
argument_list|,
name|fileStatusUrl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testSimpleProxyAuthParamsInUrl ()
specifier|public
name|void
name|testSimpleProxyAuthParamsInUrl
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"test-user"
argument_list|)
decl_stmt|;
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
literal|"test-proxy-user"
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setLoginUser
argument_list|(
name|ugi
argument_list|)
expr_stmt|;
name|WebHdfsFileSystem
name|webhdfs
init|=
name|getWebHdfsFileSystem
argument_list|(
name|ugi
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Path
name|fsPath
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
comment|// send real+effective
name|URL
name|fileStatusUrl
init|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|GetOpParam
operator|.
name|Op
operator|.
name|GETFILESTATUS
argument_list|,
name|fsPath
argument_list|)
decl_stmt|;
name|checkQueryParams
argument_list|(
operator|new
name|String
index|[]
block|{
name|GetOpParam
operator|.
name|Op
operator|.
name|GETFILESTATUS
operator|.
name|toQueryString
argument_list|()
block|,
operator|new
name|UserParam
argument_list|(
name|ugi
operator|.
name|getRealUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|,
operator|new
name|DoAsParam
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|}
argument_list|,
name|fileStatusUrl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testSecureAuthParamsInUrl ()
specifier|public
name|void
name|testSecureAuthParamsInUrl
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// fake turning on security so api thinks it should use tokens
name|SecurityUtil
operator|.
name|setAuthenticationMethod
argument_list|(
name|KERBEROS
argument_list|,
name|conf
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
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"test-user"
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|setAuthenticationMethod
argument_list|(
name|KERBEROS
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setLoginUser
argument_list|(
name|ugi
argument_list|)
expr_stmt|;
name|WebHdfsFileSystem
name|webhdfs
init|=
name|getWebHdfsFileSystem
argument_list|(
name|ugi
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Path
name|fsPath
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|String
name|tokenString
init|=
name|webhdfs
operator|.
name|getDelegationToken
argument_list|()
operator|.
name|encodeToUrlString
argument_list|()
decl_stmt|;
comment|// send user
name|URL
name|getTokenUrl
init|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|GetOpParam
operator|.
name|Op
operator|.
name|GETDELEGATIONTOKEN
argument_list|,
name|fsPath
argument_list|)
decl_stmt|;
name|checkQueryParams
argument_list|(
operator|new
name|String
index|[]
block|{
name|GetOpParam
operator|.
name|Op
operator|.
name|GETDELEGATIONTOKEN
operator|.
name|toQueryString
argument_list|()
block|,
operator|new
name|UserParam
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|}
argument_list|,
name|getTokenUrl
argument_list|)
expr_stmt|;
comment|// send user
name|URL
name|renewTokenUrl
init|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|PutOpParam
operator|.
name|Op
operator|.
name|RENEWDELEGATIONTOKEN
argument_list|,
name|fsPath
argument_list|,
operator|new
name|TokenArgumentParam
argument_list|(
name|tokenString
argument_list|)
argument_list|)
decl_stmt|;
name|checkQueryParams
argument_list|(
operator|new
name|String
index|[]
block|{
name|PutOpParam
operator|.
name|Op
operator|.
name|RENEWDELEGATIONTOKEN
operator|.
name|toQueryString
argument_list|()
block|,
operator|new
name|UserParam
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|,
operator|new
name|TokenArgumentParam
argument_list|(
name|tokenString
argument_list|)
operator|.
name|toString
argument_list|()
block|,         }
argument_list|,
name|renewTokenUrl
argument_list|)
expr_stmt|;
comment|// send token
name|URL
name|cancelTokenUrl
init|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|PutOpParam
operator|.
name|Op
operator|.
name|CANCELDELEGATIONTOKEN
argument_list|,
name|fsPath
argument_list|,
operator|new
name|TokenArgumentParam
argument_list|(
name|tokenString
argument_list|)
argument_list|)
decl_stmt|;
name|checkQueryParams
argument_list|(
operator|new
name|String
index|[]
block|{
name|PutOpParam
operator|.
name|Op
operator|.
name|CANCELDELEGATIONTOKEN
operator|.
name|toQueryString
argument_list|()
block|,
operator|new
name|UserParam
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|,
operator|new
name|TokenArgumentParam
argument_list|(
name|tokenString
argument_list|)
operator|.
name|toString
argument_list|()
block|,         }
argument_list|,
name|cancelTokenUrl
argument_list|)
expr_stmt|;
comment|// send token
name|URL
name|fileStatusUrl
init|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|GetOpParam
operator|.
name|Op
operator|.
name|GETFILESTATUS
argument_list|,
name|fsPath
argument_list|)
decl_stmt|;
name|checkQueryParams
argument_list|(
operator|new
name|String
index|[]
block|{
name|GetOpParam
operator|.
name|Op
operator|.
name|GETFILESTATUS
operator|.
name|toQueryString
argument_list|()
block|,
operator|new
name|DelegationParam
argument_list|(
name|tokenString
argument_list|)
operator|.
name|toString
argument_list|()
block|}
argument_list|,
name|fileStatusUrl
argument_list|)
expr_stmt|;
comment|// wipe out internal token to simulate auth always required
name|webhdfs
operator|.
name|setDelegationToken
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// send user
name|cancelTokenUrl
operator|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|PutOpParam
operator|.
name|Op
operator|.
name|CANCELDELEGATIONTOKEN
argument_list|,
name|fsPath
argument_list|,
operator|new
name|TokenArgumentParam
argument_list|(
name|tokenString
argument_list|)
argument_list|)
expr_stmt|;
name|checkQueryParams
argument_list|(
operator|new
name|String
index|[]
block|{
name|PutOpParam
operator|.
name|Op
operator|.
name|CANCELDELEGATIONTOKEN
operator|.
name|toQueryString
argument_list|()
block|,
operator|new
name|UserParam
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|,
operator|new
name|TokenArgumentParam
argument_list|(
name|tokenString
argument_list|)
operator|.
name|toString
argument_list|()
block|,         }
argument_list|,
name|cancelTokenUrl
argument_list|)
expr_stmt|;
comment|// send user
name|fileStatusUrl
operator|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|GetOpParam
operator|.
name|Op
operator|.
name|GETFILESTATUS
argument_list|,
name|fsPath
argument_list|)
expr_stmt|;
name|checkQueryParams
argument_list|(
operator|new
name|String
index|[]
block|{
name|GetOpParam
operator|.
name|Op
operator|.
name|GETFILESTATUS
operator|.
name|toQueryString
argument_list|()
block|,
operator|new
name|UserParam
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|}
argument_list|,
name|fileStatusUrl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testSecureProxyAuthParamsInUrl ()
specifier|public
name|void
name|testSecureProxyAuthParamsInUrl
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// fake turning on security so api thinks it should use tokens
name|SecurityUtil
operator|.
name|setAuthenticationMethod
argument_list|(
name|KERBEROS
argument_list|,
name|conf
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
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"test-user"
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|setAuthenticationMethod
argument_list|(
name|KERBEROS
argument_list|)
expr_stmt|;
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
literal|"test-proxy-user"
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setLoginUser
argument_list|(
name|ugi
argument_list|)
expr_stmt|;
name|WebHdfsFileSystem
name|webhdfs
init|=
name|getWebHdfsFileSystem
argument_list|(
name|ugi
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Path
name|fsPath
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|String
name|tokenString
init|=
name|webhdfs
operator|.
name|getDelegationToken
argument_list|()
operator|.
name|encodeToUrlString
argument_list|()
decl_stmt|;
comment|// send real+effective
name|URL
name|getTokenUrl
init|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|GetOpParam
operator|.
name|Op
operator|.
name|GETDELEGATIONTOKEN
argument_list|,
name|fsPath
argument_list|)
decl_stmt|;
name|checkQueryParams
argument_list|(
operator|new
name|String
index|[]
block|{
name|GetOpParam
operator|.
name|Op
operator|.
name|GETDELEGATIONTOKEN
operator|.
name|toQueryString
argument_list|()
block|,
operator|new
name|UserParam
argument_list|(
name|ugi
operator|.
name|getRealUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|,
operator|new
name|DoAsParam
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|}
argument_list|,
name|getTokenUrl
argument_list|)
expr_stmt|;
comment|// send real+effective
name|URL
name|renewTokenUrl
init|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|PutOpParam
operator|.
name|Op
operator|.
name|RENEWDELEGATIONTOKEN
argument_list|,
name|fsPath
argument_list|,
operator|new
name|TokenArgumentParam
argument_list|(
name|tokenString
argument_list|)
argument_list|)
decl_stmt|;
name|checkQueryParams
argument_list|(
operator|new
name|String
index|[]
block|{
name|PutOpParam
operator|.
name|Op
operator|.
name|RENEWDELEGATIONTOKEN
operator|.
name|toQueryString
argument_list|()
block|,
operator|new
name|UserParam
argument_list|(
name|ugi
operator|.
name|getRealUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|,
operator|new
name|DoAsParam
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|,
operator|new
name|TokenArgumentParam
argument_list|(
name|tokenString
argument_list|)
operator|.
name|toString
argument_list|()
block|,         }
argument_list|,
name|renewTokenUrl
argument_list|)
expr_stmt|;
comment|// send token
name|URL
name|cancelTokenUrl
init|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|PutOpParam
operator|.
name|Op
operator|.
name|CANCELDELEGATIONTOKEN
argument_list|,
name|fsPath
argument_list|,
operator|new
name|TokenArgumentParam
argument_list|(
name|tokenString
argument_list|)
argument_list|)
decl_stmt|;
name|checkQueryParams
argument_list|(
operator|new
name|String
index|[]
block|{
name|PutOpParam
operator|.
name|Op
operator|.
name|CANCELDELEGATIONTOKEN
operator|.
name|toQueryString
argument_list|()
block|,
operator|new
name|UserParam
argument_list|(
name|ugi
operator|.
name|getRealUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|,
operator|new
name|DoAsParam
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|,
operator|new
name|TokenArgumentParam
argument_list|(
name|tokenString
argument_list|)
operator|.
name|toString
argument_list|()
block|,         }
argument_list|,
name|cancelTokenUrl
argument_list|)
expr_stmt|;
comment|// send token
name|URL
name|fileStatusUrl
init|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|GetOpParam
operator|.
name|Op
operator|.
name|GETFILESTATUS
argument_list|,
name|fsPath
argument_list|)
decl_stmt|;
name|checkQueryParams
argument_list|(
operator|new
name|String
index|[]
block|{
name|GetOpParam
operator|.
name|Op
operator|.
name|GETFILESTATUS
operator|.
name|toQueryString
argument_list|()
block|,
operator|new
name|DelegationParam
argument_list|(
name|tokenString
argument_list|)
operator|.
name|toString
argument_list|()
block|}
argument_list|,
name|fileStatusUrl
argument_list|)
expr_stmt|;
comment|// wipe out internal token to simulate auth always required
name|webhdfs
operator|.
name|setDelegationToken
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// send real+effective
name|cancelTokenUrl
operator|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|PutOpParam
operator|.
name|Op
operator|.
name|CANCELDELEGATIONTOKEN
argument_list|,
name|fsPath
argument_list|,
operator|new
name|TokenArgumentParam
argument_list|(
name|tokenString
argument_list|)
argument_list|)
expr_stmt|;
name|checkQueryParams
argument_list|(
operator|new
name|String
index|[]
block|{
name|PutOpParam
operator|.
name|Op
operator|.
name|CANCELDELEGATIONTOKEN
operator|.
name|toQueryString
argument_list|()
block|,
operator|new
name|UserParam
argument_list|(
name|ugi
operator|.
name|getRealUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|,
operator|new
name|DoAsParam
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|,
operator|new
name|TokenArgumentParam
argument_list|(
name|tokenString
argument_list|)
operator|.
name|toString
argument_list|()
block|}
argument_list|,
name|cancelTokenUrl
argument_list|)
expr_stmt|;
comment|// send real+effective
name|fileStatusUrl
operator|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|GetOpParam
operator|.
name|Op
operator|.
name|GETFILESTATUS
argument_list|,
name|fsPath
argument_list|)
expr_stmt|;
name|checkQueryParams
argument_list|(
operator|new
name|String
index|[]
block|{
name|GetOpParam
operator|.
name|Op
operator|.
name|GETFILESTATUS
operator|.
name|toQueryString
argument_list|()
block|,
operator|new
name|UserParam
argument_list|(
name|ugi
operator|.
name|getRealUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|,
operator|new
name|DoAsParam
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|}
argument_list|,
name|fileStatusUrl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testCheckAccessUrl ()
specifier|public
name|void
name|testCheckAccessUrl
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"test-user"
argument_list|)
decl_stmt|;
name|UserGroupInformation
operator|.
name|setLoginUser
argument_list|(
name|ugi
argument_list|)
expr_stmt|;
name|WebHdfsFileSystem
name|webhdfs
init|=
name|getWebHdfsFileSystem
argument_list|(
name|ugi
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Path
name|fsPath
init|=
operator|new
name|Path
argument_list|(
literal|"/p1"
argument_list|)
decl_stmt|;
name|URL
name|checkAccessUrl
init|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|GetOpParam
operator|.
name|Op
operator|.
name|CHECKACCESS
argument_list|,
name|fsPath
argument_list|,
operator|new
name|FsActionParam
argument_list|(
name|FsAction
operator|.
name|READ_WRITE
argument_list|)
argument_list|)
decl_stmt|;
name|checkQueryParams
argument_list|(
operator|new
name|String
index|[]
block|{
name|GetOpParam
operator|.
name|Op
operator|.
name|CHECKACCESS
operator|.
name|toQueryString
argument_list|()
block|,
operator|new
name|UserParam
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
block|,
name|FsActionParam
operator|.
name|NAME
operator|+
literal|"="
operator|+
name|FsAction
operator|.
name|READ_WRITE
operator|.
name|SYMBOL
block|}
argument_list|,
name|checkAccessUrl
argument_list|)
expr_stmt|;
block|}
DECL|method|checkQueryParams (String[] expected, URL url)
specifier|private
name|void
name|checkQueryParams
parameter_list|(
name|String
index|[]
name|expected
parameter_list|,
name|URL
name|url
parameter_list|)
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|String
index|[]
name|query
init|=
name|url
operator|.
name|getQuery
argument_list|()
operator|.
name|split
argument_list|(
literal|"&"
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|expected
argument_list|)
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getWebHdfsFileSystem (UserGroupInformation ugi, Configuration conf)
specifier|private
name|WebHdfsFileSystem
name|getWebHdfsFileSystem
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|DelegationTokenIdentifier
name|dtId
init|=
operator|new
name|DelegationTokenIdentifier
argument_list|(
operator|new
name|Text
argument_list|(
name|ugi
operator|.
name|getUserName
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|FSNamesystem
name|namesystem
init|=
name|mock
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|DelegationTokenSecretManager
name|dtSecretManager
init|=
operator|new
name|DelegationTokenSecretManager
argument_list|(
literal|86400000
argument_list|,
literal|86400000
argument_list|,
literal|86400000
argument_list|,
literal|86400000
argument_list|,
name|namesystem
argument_list|)
decl_stmt|;
name|dtSecretManager
operator|.
name|startThreads
argument_list|()
expr_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
argument_list|(
name|dtId
argument_list|,
name|dtSecretManager
argument_list|)
decl_stmt|;
name|SecurityUtil
operator|.
name|setTokenService
argument_list|(
name|token
argument_list|,
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|uri
operator|.
name|getAuthority
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|token
operator|.
name|setKind
argument_list|(
name|WebHdfsFileSystem
operator|.
name|TOKEN_KIND
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|WebHdfsFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
end_class

end_unit

