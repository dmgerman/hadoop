begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
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
name|hdds
operator|.
name|scm
operator|.
name|server
operator|.
name|StorageContainerManagerHttpServer
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
name|DFSConfigKeys
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
name|URLConnectionFactory
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
name|http
operator|.
name|HttpConfig
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
name|http
operator|.
name|HttpConfig
operator|.
name|Policy
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
name|ssl
operator|.
name|KeyStoreTestUtil
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
name|Test
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|net
operator|.
name|URLConnection
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
name|Collection
import|;
end_import

begin_comment
comment|/**  * Test http server os SCM with various HTTP option.  */
end_comment

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
DECL|class|TestStorageContainerManagerHttpServer
specifier|public
class|class
name|TestStorageContainerManagerHttpServer
block|{
DECL|field|BASEDIR
specifier|private
specifier|static
specifier|final
name|String
name|BASEDIR
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestStorageContainerManagerHttpServer
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|keystoresDir
specifier|private
specifier|static
name|String
name|keystoresDir
decl_stmt|;
DECL|field|sslConfDir
specifier|private
specifier|static
name|String
name|sslConfDir
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|connectionFactory
specifier|private
specifier|static
name|URLConnectionFactory
name|connectionFactory
decl_stmt|;
DECL|method|policy ()
annotation|@
name|Parameters
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|policy
parameter_list|()
block|{
name|Object
index|[]
index|[]
name|params
init|=
operator|new
name|Object
index|[]
index|[]
block|{
block|{
name|HttpConfig
operator|.
name|Policy
operator|.
name|HTTP_ONLY
block|}
block|,
block|{
name|HttpConfig
operator|.
name|Policy
operator|.
name|HTTPS_ONLY
block|}
block|,
block|{
name|HttpConfig
operator|.
name|Policy
operator|.
name|HTTP_AND_HTTPS
block|}
block|}
decl_stmt|;
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|params
argument_list|)
return|;
block|}
DECL|field|policy
specifier|private
specifier|final
name|HttpConfig
operator|.
name|Policy
name|policy
decl_stmt|;
DECL|method|TestStorageContainerManagerHttpServer (Policy policy)
specifier|public
name|TestStorageContainerManagerHttpServer
parameter_list|(
name|Policy
name|policy
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|policy
operator|=
name|policy
expr_stmt|;
block|}
DECL|method|setUp ()
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|base
init|=
operator|new
name|File
argument_list|(
name|BASEDIR
argument_list|)
decl_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|base
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|keystoresDir
operator|=
operator|new
name|File
argument_list|(
name|BASEDIR
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|sslConfDir
operator|=
name|KeyStoreTestUtil
operator|.
name|getClasspathDir
argument_list|(
name|TestStorageContainerManagerHttpServer
operator|.
name|class
argument_list|)
expr_stmt|;
name|KeyStoreTestUtil
operator|.
name|setupSSLConfig
argument_list|(
name|keystoresDir
argument_list|,
name|sslConfDir
argument_list|,
name|conf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|connectionFactory
operator|=
name|URLConnectionFactory
operator|.
name|newDefaultURLConnectionFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_HTTPS_KEYSTORE_RESOURCE_KEY
argument_list|,
name|KeyStoreTestUtil
operator|.
name|getClientSSLConfigFileName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_SERVER_HTTPS_KEYSTORE_RESOURCE_KEY
argument_list|,
name|KeyStoreTestUtil
operator|.
name|getServerSSLConfigFileName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown ()
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|BASEDIR
argument_list|)
argument_list|)
expr_stmt|;
name|KeyStoreTestUtil
operator|.
name|cleanupSSLConfig
argument_list|(
name|keystoresDir
argument_list|,
name|sslConfDir
argument_list|)
expr_stmt|;
block|}
DECL|method|testHttpPolicy ()
annotation|@
name|Test
specifier|public
name|void
name|testHttpPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HTTP_POLICY_KEY
argument_list|,
name|policy
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HTTPS_ADDRESS_KEY
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
name|InetSocketAddress
operator|.
name|createUnresolved
argument_list|(
literal|"localhost"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|StorageContainerManagerHttpServer
name|server
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|=
operator|new
name|StorageContainerManagerHttpServer
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|implies
argument_list|(
name|policy
operator|.
name|isHttpEnabled
argument_list|()
argument_list|,
name|canAccess
argument_list|(
literal|"http"
argument_list|,
name|server
operator|.
name|getHttpAddress
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|implies
argument_list|(
operator|!
name|policy
operator|.
name|isHttpEnabled
argument_list|()
argument_list|,
name|server
operator|.
name|getHttpAddress
argument_list|()
operator|==
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|implies
argument_list|(
name|policy
operator|.
name|isHttpsEnabled
argument_list|()
argument_list|,
name|canAccess
argument_list|(
literal|"https"
argument_list|,
name|server
operator|.
name|getHttpsAddress
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|implies
argument_list|(
operator|!
name|policy
operator|.
name|isHttpsEnabled
argument_list|()
argument_list|,
name|server
operator|.
name|getHttpsAddress
argument_list|()
operator|==
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|canAccess (String scheme, InetSocketAddress addr)
specifier|private
specifier|static
name|boolean
name|canAccess
parameter_list|(
name|String
name|scheme
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|)
block|{
if|if
condition|(
name|addr
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
try|try
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|scheme
operator|+
literal|"://"
operator|+
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|addr
argument_list|)
operator|+
literal|"/jmx"
argument_list|)
decl_stmt|;
name|URLConnection
name|conn
init|=
name|connectionFactory
operator|.
name|openConnection
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|conn
operator|.
name|getContent
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|implies (boolean a, boolean b)
specifier|private
specifier|static
name|boolean
name|implies
parameter_list|(
name|boolean
name|a
parameter_list|,
name|boolean
name|b
parameter_list|)
block|{
return|return
operator|!
name|a
operator|||
name|b
return|;
block|}
block|}
end_class

end_unit

