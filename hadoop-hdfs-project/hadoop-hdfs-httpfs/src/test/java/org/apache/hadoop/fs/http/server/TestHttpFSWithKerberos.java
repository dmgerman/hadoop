begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.http.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|http
operator|.
name|server
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
name|CommonConfigurationKeysPublic
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
name|DelegationTokenRenewer
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
name|http
operator|.
name|client
operator|.
name|HttpFSFileSystem
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
name|WebHdfsFileSystem
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
name|authentication
operator|.
name|client
operator|.
name|AuthenticatedURL
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
name|delegation
operator|.
name|web
operator|.
name|DelegationTokenAuthenticator
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
name|HFSTestCase
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
name|KerberosTestUtils
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
name|TestDir
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
name|TestDirHelper
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
name|TestHdfs
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
name|TestHdfsHelper
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
name|TestJetty
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
name|TestJettyHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|JSONObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|parser
operator|.
name|JSONParser
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
name|Assert
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
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|webapp
operator|.
name|WebAppContext
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
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
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
name|URL
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_class
DECL|class|TestHttpFSWithKerberos
specifier|public
class|class
name|TestHttpFSWithKerberos
extends|extends
name|HFSTestCase
block|{
annotation|@
name|After
DECL|method|resetUGI ()
specifier|public
name|void
name|resetUGI
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|createHttpFSServer ()
specifier|private
name|void
name|createHttpFSServer
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|homeDir
init|=
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"conf"
argument_list|)
operator|.
name|mkdir
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"log"
argument_list|)
operator|.
name|mkdir
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"temp"
argument_list|)
operator|.
name|mkdir
argument_list|()
argument_list|)
expr_stmt|;
name|HttpFSServerWebApp
operator|.
name|setHomeDirForCurrentThread
argument_list|(
name|homeDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|secretFile
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"conf"
argument_list|)
argument_list|,
literal|"secret"
argument_list|)
decl_stmt|;
name|Writer
name|w
init|=
operator|new
name|FileWriter
argument_list|(
name|secretFile
argument_list|)
decl_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|"secret"
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//HDFS configuration
name|File
name|hadoopConfDir
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"conf"
argument_list|)
argument_list|,
literal|"hadoop-conf"
argument_list|)
decl_stmt|;
name|hadoopConfDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|String
name|fsDefaultName
init|=
name|TestHdfsHelper
operator|.
name|getHdfsConf
argument_list|()
operator|.
name|get
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
name|fsDefaultName
argument_list|)
expr_stmt|;
name|File
name|hdfsSite
init|=
operator|new
name|File
argument_list|(
name|hadoopConfDir
argument_list|,
literal|"hdfs-site.xml"
argument_list|)
decl_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|hdfsSite
argument_list|)
decl_stmt|;
name|conf
operator|.
name|writeXml
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"httpfs.proxyuser.client.hosts"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"httpfs.proxyuser.client.groups"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"httpfs.authentication.type"
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"httpfs.authentication.signature.secret.file"
argument_list|,
name|secretFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|httpfsSite
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"conf"
argument_list|)
argument_list|,
literal|"httpfs-site.xml"
argument_list|)
decl_stmt|;
name|os
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|httpfsSite
argument_list|)
expr_stmt|;
name|conf
operator|.
name|writeXml
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|ClassLoader
name|cl
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|URL
name|url
init|=
name|cl
operator|.
name|getResource
argument_list|(
literal|"webapp"
argument_list|)
decl_stmt|;
name|WebAppContext
name|context
init|=
operator|new
name|WebAppContext
argument_list|(
name|url
operator|.
name|getPath
argument_list|()
argument_list|,
literal|"/webhdfs"
argument_list|)
decl_stmt|;
name|Server
name|server
init|=
name|TestJettyHelper
operator|.
name|getJettyServer
argument_list|()
decl_stmt|;
name|server
operator|.
name|setHandler
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|HttpFSServerWebApp
operator|.
name|get
argument_list|()
operator|.
name|setAuthority
argument_list|(
name|TestJettyHelper
operator|.
name|getAuthority
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestDir
annotation|@
name|TestJetty
annotation|@
name|TestHdfs
DECL|method|testValidHttpFSAccess ()
specifier|public
name|void
name|testValidHttpFSAccess
parameter_list|()
throws|throws
name|Exception
block|{
name|createHttpFSServer
argument_list|()
expr_stmt|;
name|KerberosTestUtils
operator|.
name|doAsClient
argument_list|(
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|TestJettyHelper
operator|.
name|getJettyURL
argument_list|()
argument_list|,
literal|"/webhdfs/v1/?op=GETHOMEDIRECTORY"
argument_list|)
decl_stmt|;
name|AuthenticatedURL
name|aUrl
init|=
operator|new
name|AuthenticatedURL
argument_list|()
decl_stmt|;
name|AuthenticatedURL
operator|.
name|Token
name|aToken
init|=
operator|new
name|AuthenticatedURL
operator|.
name|Token
argument_list|()
decl_stmt|;
name|HttpURLConnection
name|conn
init|=
name|aUrl
operator|.
name|openConnection
argument_list|(
name|url
argument_list|,
name|aToken
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestDir
annotation|@
name|TestJetty
annotation|@
name|TestHdfs
DECL|method|testInvalidadHttpFSAccess ()
specifier|public
name|void
name|testInvalidadHttpFSAccess
parameter_list|()
throws|throws
name|Exception
block|{
name|createHttpFSServer
argument_list|()
expr_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|TestJettyHelper
operator|.
name|getJettyURL
argument_list|()
argument_list|,
literal|"/webhdfs/v1/?op=GETHOMEDIRECTORY"
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_UNAUTHORIZED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestDir
annotation|@
name|TestJetty
annotation|@
name|TestHdfs
DECL|method|testDelegationTokenHttpFSAccess ()
specifier|public
name|void
name|testDelegationTokenHttpFSAccess
parameter_list|()
throws|throws
name|Exception
block|{
name|createHttpFSServer
argument_list|()
expr_stmt|;
name|KerberosTestUtils
operator|.
name|doAsClient
argument_list|(
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
comment|//get delegation token doing SPNEGO authentication
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|TestJettyHelper
operator|.
name|getJettyURL
argument_list|()
argument_list|,
literal|"/webhdfs/v1/?op=GETDELEGATIONTOKEN"
argument_list|)
decl_stmt|;
name|AuthenticatedURL
name|aUrl
init|=
operator|new
name|AuthenticatedURL
argument_list|()
decl_stmt|;
name|AuthenticatedURL
operator|.
name|Token
name|aToken
init|=
operator|new
name|AuthenticatedURL
operator|.
name|Token
argument_list|()
decl_stmt|;
name|HttpURLConnection
name|conn
init|=
name|aUrl
operator|.
name|openConnection
argument_list|(
name|url
argument_list|,
name|aToken
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
expr_stmt|;
name|JSONObject
name|json
init|=
operator|(
name|JSONObject
operator|)
operator|new
name|JSONParser
argument_list|()
operator|.
name|parse
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|conn
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|json
operator|=
operator|(
name|JSONObject
operator|)
name|json
operator|.
name|get
argument_list|(
name|DelegationTokenAuthenticator
operator|.
name|DELEGATION_TOKEN_JSON
argument_list|)
expr_stmt|;
name|String
name|tokenStr
init|=
operator|(
name|String
operator|)
name|json
operator|.
name|get
argument_list|(
name|DelegationTokenAuthenticator
operator|.
name|DELEGATION_TOKEN_URL_STRING_JSON
argument_list|)
decl_stmt|;
comment|//access httpfs using the delegation token
name|url
operator|=
operator|new
name|URL
argument_list|(
name|TestJettyHelper
operator|.
name|getJettyURL
argument_list|()
argument_list|,
literal|"/webhdfs/v1/?op=GETHOMEDIRECTORY&delegation="
operator|+
name|tokenStr
argument_list|)
expr_stmt|;
name|conn
operator|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
expr_stmt|;
comment|//try to renew the delegation token without SPNEGO credentials
name|url
operator|=
operator|new
name|URL
argument_list|(
name|TestJettyHelper
operator|.
name|getJettyURL
argument_list|()
argument_list|,
literal|"/webhdfs/v1/?op=RENEWDELEGATIONTOKEN&token="
operator|+
name|tokenStr
argument_list|)
expr_stmt|;
name|conn
operator|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|conn
operator|.
name|setRequestMethod
argument_list|(
literal|"PUT"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_UNAUTHORIZED
argument_list|)
expr_stmt|;
comment|//renew the delegation token with SPNEGO credentials
name|url
operator|=
operator|new
name|URL
argument_list|(
name|TestJettyHelper
operator|.
name|getJettyURL
argument_list|()
argument_list|,
literal|"/webhdfs/v1/?op=RENEWDELEGATIONTOKEN&token="
operator|+
name|tokenStr
argument_list|)
expr_stmt|;
name|conn
operator|=
name|aUrl
operator|.
name|openConnection
argument_list|(
name|url
argument_list|,
name|aToken
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setRequestMethod
argument_list|(
literal|"PUT"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
expr_stmt|;
comment|//cancel delegation token, no need for SPNEGO credentials
name|url
operator|=
operator|new
name|URL
argument_list|(
name|TestJettyHelper
operator|.
name|getJettyURL
argument_list|()
argument_list|,
literal|"/webhdfs/v1/?op=CANCELDELEGATIONTOKEN&token="
operator|+
name|tokenStr
argument_list|)
expr_stmt|;
name|conn
operator|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|conn
operator|.
name|setRequestMethod
argument_list|(
literal|"PUT"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
expr_stmt|;
comment|//try to access httpfs with the canceled delegation token
name|url
operator|=
operator|new
name|URL
argument_list|(
name|TestJettyHelper
operator|.
name|getJettyURL
argument_list|()
argument_list|,
literal|"/webhdfs/v1/?op=GETHOMEDIRECTORY&delegation="
operator|+
name|tokenStr
argument_list|)
expr_stmt|;
name|conn
operator|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_UNAUTHORIZED
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|testDelegationTokenWithFS (Class fileSystemClass)
specifier|private
name|void
name|testDelegationTokenWithFS
parameter_list|(
name|Class
name|fileSystemClass
parameter_list|)
throws|throws
name|Exception
block|{
name|createHttpFSServer
argument_list|()
expr_stmt|;
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
literal|"fs.webhdfs.impl"
argument_list|,
name|fileSystemClass
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"fs.hdfs.impl.disable.cache"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"webhdfs://"
operator|+
name|TestJettyHelper
operator|.
name|getJettyURL
argument_list|()
operator|.
name|toURI
argument_list|()
operator|.
name|getAuthority
argument_list|()
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|?
argument_list|>
name|tokens
index|[]
init|=
name|fs
operator|.
name|addDelegationTokens
argument_list|(
literal|"foo"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tokens
operator|.
name|length
argument_list|)
expr_stmt|;
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
operator|(
operator|(
name|DelegationTokenRenewer
operator|.
name|Renewable
operator|)
name|fs
operator|)
operator|.
name|setDelegationToken
argument_list|(
name|tokens
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|fs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testDelegationTokenWithinDoAs ( final Class fileSystemClass, boolean proxyUser)
specifier|private
name|void
name|testDelegationTokenWithinDoAs
parameter_list|(
specifier|final
name|Class
name|fileSystemClass
parameter_list|,
name|boolean
name|proxyUser
parameter_list|)
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
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.security.authentication"
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
operator|.
name|loginUserFromKeytab
argument_list|(
literal|"client"
argument_list|,
literal|"/Users/tucu/tucu.keytab"
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
decl_stmt|;
if|if
condition|(
name|proxyUser
condition|)
block|{
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
literal|"foo"
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
block|}
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|testDelegationTokenWithFS
argument_list|(
name|fileSystemClass
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestDir
annotation|@
name|TestJetty
annotation|@
name|TestHdfs
DECL|method|testDelegationTokenWithHttpFSFileSystem ()
specifier|public
name|void
name|testDelegationTokenWithHttpFSFileSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|testDelegationTokenWithinDoAs
argument_list|(
name|HttpFSFileSystem
operator|.
name|class
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestDir
annotation|@
name|TestJetty
annotation|@
name|TestHdfs
DECL|method|testDelegationTokenWithWebhdfsFileSystem ()
specifier|public
name|void
name|testDelegationTokenWithWebhdfsFileSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|testDelegationTokenWithinDoAs
argument_list|(
name|WebHdfsFileSystem
operator|.
name|class
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestDir
annotation|@
name|TestJetty
annotation|@
name|TestHdfs
DECL|method|testDelegationTokenWithHttpFSFileSystemProxyUser ()
specifier|public
name|void
name|testDelegationTokenWithHttpFSFileSystemProxyUser
parameter_list|()
throws|throws
name|Exception
block|{
name|testDelegationTokenWithinDoAs
argument_list|(
name|HttpFSFileSystem
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// TODO: WebHdfsFilesystem does work with ProxyUser HDFS-3509
comment|//    @Test
comment|//    @TestDir
comment|//    @TestJetty
comment|//    @TestHdfs
comment|//    public void testDelegationTokenWithWebhdfsFileSystemProxyUser()
comment|//      throws Exception {
comment|//      testDelegationTokenWithinDoAs(WebHdfsFileSystem.class, true);
comment|//    }
block|}
end_class

end_unit

