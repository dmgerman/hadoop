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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|lib
operator|.
name|server
operator|.
name|Service
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
name|lib
operator|.
name|server
operator|.
name|ServiceException
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
name|lib
operator|.
name|service
operator|.
name|Groups
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
name|HadoopUsersConfTestHelper
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
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
name|BufferedReader
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
name|IOException
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
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
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
name|List
import|;
end_import

begin_class
DECL|class|TestHttpFSServer
specifier|public
class|class
name|TestHttpFSServer
extends|extends
name|HFSTestCase
block|{
annotation|@
name|Test
annotation|@
name|TestDir
annotation|@
name|TestJetty
DECL|method|server ()
specifier|public
name|void
name|server
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dir
init|=
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|Configuration
name|httpfsConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|HttpFSServerWebApp
name|server
init|=
operator|new
name|HttpFSServerWebApp
argument_list|(
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|httpfsConf
argument_list|)
decl_stmt|;
name|server
operator|.
name|init
argument_list|()
expr_stmt|;
name|server
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
DECL|class|MockGroups
specifier|public
specifier|static
class|class
name|MockGroups
implements|implements
name|Service
implements|,
name|Groups
block|{
annotation|@
name|Override
DECL|method|init (org.apache.hadoop.lib.server.Server server)
specifier|public
name|void
name|init
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|server
operator|.
name|Server
name|server
parameter_list|)
throws|throws
name|ServiceException
block|{     }
annotation|@
name|Override
DECL|method|postInit ()
specifier|public
name|void
name|postInit
parameter_list|()
throws|throws
name|ServiceException
block|{     }
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|getServiceDependencies ()
specifier|public
name|Class
index|[]
name|getServiceDependencies
parameter_list|()
block|{
return|return
operator|new
name|Class
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getInterface ()
specifier|public
name|Class
name|getInterface
parameter_list|()
block|{
return|return
name|Groups
operator|.
name|class
return|;
block|}
annotation|@
name|Override
DECL|method|serverStatusChange (org.apache.hadoop.lib.server.Server.Status oldStatus, org.apache.hadoop.lib.server.Server.Status newStatus)
specifier|public
name|void
name|serverStatusChange
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|server
operator|.
name|Server
operator|.
name|Status
name|oldStatus
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|server
operator|.
name|Server
operator|.
name|Status
name|newStatus
parameter_list|)
throws|throws
name|ServiceException
block|{     }
annotation|@
name|Override
DECL|method|getGroups (String user)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getGroups
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|HadoopUsersConfTestHelper
operator|.
name|getHadoopUserGroups
argument_list|(
name|user
argument_list|)
argument_list|)
return|;
block|}
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
comment|//HTTPFS configuration
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
literal|"httpfs.services.ext"
argument_list|,
name|MockGroups
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"httpfs.admin.group"
argument_list|,
name|HadoopUsersConfTestHelper
operator|.
name|getHadoopUserGroups
argument_list|(
name|HadoopUsersConfTestHelper
operator|.
name|getHadoopUsers
argument_list|()
index|[
literal|0
index|]
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"httpfs.proxyuser."
operator|+
name|HadoopUsersConfTestHelper
operator|.
name|getHadoopProxyUser
argument_list|()
operator|+
literal|".groups"
argument_list|,
name|HadoopUsersConfTestHelper
operator|.
name|getHadoopProxyUserGroups
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"httpfs.proxyuser."
operator|+
name|HadoopUsersConfTestHelper
operator|.
name|getHadoopProxyUser
argument_list|()
operator|+
literal|".hosts"
argument_list|,
name|HadoopUsersConfTestHelper
operator|.
name|getHadoopProxyUserHosts
argument_list|()
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
name|addHandler
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
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
DECL|method|instrumentation ()
specifier|public
name|void
name|instrumentation
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
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"/webhdfs/v1?user.name={0}&op=instrumentation"
argument_list|,
literal|"nobody"
argument_list|)
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
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"/webhdfs/v1?user.name={0}&op=instrumentation"
argument_list|,
name|HadoopUsersConfTestHelper
operator|.
name|getHadoopUsers
argument_list|()
index|[
literal|0
index|]
argument_list|)
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
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
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
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|line
operator|.
name|contains
argument_list|(
literal|"\"counters\":{"
argument_list|)
argument_list|)
expr_stmt|;
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
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"/webhdfs/v1/foo?user.name={0}&op=instrumentation"
argument_list|,
name|HadoopUsersConfTestHelper
operator|.
name|getHadoopUsers
argument_list|()
index|[
literal|0
index|]
argument_list|)
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
name|HTTP_BAD_REQUEST
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
DECL|method|testHdfsAccess ()
specifier|public
name|void
name|testHdfsAccess
parameter_list|()
throws|throws
name|Exception
block|{
name|createHttpFSServer
argument_list|()
expr_stmt|;
name|String
name|user
init|=
name|HadoopUsersConfTestHelper
operator|.
name|getHadoopUsers
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
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
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"/webhdfs/v1/?user.name={0}&op=liststatus"
argument_list|,
name|user
argument_list|)
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
name|HTTP_OK
argument_list|)
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
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
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
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
DECL|method|testGlobFilter ()
specifier|public
name|void
name|testGlobFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|createHttpFSServer
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|TestHdfsHelper
operator|.
name|getHdfsConf
argument_list|()
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp"
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/foo.txt"
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|user
init|=
name|HadoopUsersConfTestHelper
operator|.
name|getHadoopUsers
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
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
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"/webhdfs/v1/tmp?user.name={0}&op=liststatus&filter=f*"
argument_list|,
name|user
argument_list|)
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
name|HTTP_OK
argument_list|)
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
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
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
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
DECL|method|testPutNoOperation ()
specifier|public
name|void
name|testPutNoOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|createHttpFSServer
argument_list|()
expr_stmt|;
name|String
name|user
init|=
name|HadoopUsersConfTestHelper
operator|.
name|getHadoopUsers
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
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
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"/webhdfs/v1/foo?user.name={0}"
argument_list|,
name|user
argument_list|)
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
name|conn
operator|.
name|setDoInput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setDoOutput
argument_list|(
literal|true
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
name|HTTP_BAD_REQUEST
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

