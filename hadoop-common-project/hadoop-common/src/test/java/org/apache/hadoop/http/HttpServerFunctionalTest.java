begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.http
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|http
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
name|security
operator|.
name|authorize
operator|.
name|AccessControlList
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
name|io
operator|.
name|InputStream
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
name|MalformedURLException
import|;
end_import

begin_comment
comment|/**  * This is a base class for functional tests of the {@link HttpServer}.  * The methods are static for other classes to import statically.  */
end_comment

begin_class
DECL|class|HttpServerFunctionalTest
specifier|public
class|class
name|HttpServerFunctionalTest
extends|extends
name|Assert
block|{
comment|/** JVM property for the webapp test dir : {@value} */
DECL|field|TEST_BUILD_WEBAPPS
specifier|public
specifier|static
specifier|final
name|String
name|TEST_BUILD_WEBAPPS
init|=
literal|"test.build.webapps"
decl_stmt|;
comment|/** expected location of the test.build.webapps dir: {@value} */
DECL|field|BUILD_WEBAPPS_DIR
specifier|private
specifier|static
specifier|final
name|String
name|BUILD_WEBAPPS_DIR
init|=
literal|"build/test/webapps"
decl_stmt|;
comment|/** name of the test webapp: {@value} */
DECL|field|TEST
specifier|private
specifier|static
specifier|final
name|String
name|TEST
init|=
literal|"test"
decl_stmt|;
comment|/**    * Create but do not start the test webapp server. The test webapp dir is    * prepared/checked in advance.    *    * @return the server instance    *    * @throws IOException if a problem occurs    * @throws AssertionError if a condition was not met    */
DECL|method|createTestServer ()
specifier|public
specifier|static
name|HttpServer
name|createTestServer
parameter_list|()
throws|throws
name|IOException
block|{
name|prepareTestWebapp
argument_list|()
expr_stmt|;
return|return
name|createServer
argument_list|(
name|TEST
argument_list|)
return|;
block|}
comment|/**    * Create but do not start the test webapp server. The test webapp dir is    * prepared/checked in advance.    * @param conf the server configuration to use    * @return the server instance    *    * @throws IOException if a problem occurs    * @throws AssertionError if a condition was not met    */
DECL|method|createTestServer (Configuration conf)
specifier|public
specifier|static
name|HttpServer
name|createTestServer
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|prepareTestWebapp
argument_list|()
expr_stmt|;
return|return
name|createServer
argument_list|(
name|TEST
argument_list|,
name|conf
argument_list|)
return|;
block|}
DECL|method|createTestServer (Configuration conf, AccessControlList adminsAcl)
specifier|public
specifier|static
name|HttpServer
name|createTestServer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|AccessControlList
name|adminsAcl
parameter_list|)
throws|throws
name|IOException
block|{
name|prepareTestWebapp
argument_list|()
expr_stmt|;
return|return
name|createServer
argument_list|(
name|TEST
argument_list|,
name|conf
argument_list|,
name|adminsAcl
argument_list|)
return|;
block|}
comment|/**    * Create but do not start the test webapp server. The test webapp dir is    * prepared/checked in advance.    * @param conf the server configuration to use    * @return the server instance    *    * @throws IOException if a problem occurs    * @throws AssertionError if a condition was not met    */
DECL|method|createTestServer (Configuration conf, String[] pathSpecs)
specifier|public
specifier|static
name|HttpServer
name|createTestServer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
index|[]
name|pathSpecs
parameter_list|)
throws|throws
name|IOException
block|{
name|prepareTestWebapp
argument_list|()
expr_stmt|;
return|return
name|createServer
argument_list|(
name|TEST
argument_list|,
name|conf
argument_list|,
name|pathSpecs
argument_list|)
return|;
block|}
comment|/**    * Prepare the test webapp by creating the directory from the test properties    * fail if the directory cannot be created.    * @throws AssertionError if a condition was not met    */
DECL|method|prepareTestWebapp ()
specifier|protected
specifier|static
name|void
name|prepareTestWebapp
parameter_list|()
block|{
name|String
name|webapps
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|TEST_BUILD_WEBAPPS
argument_list|,
name|BUILD_WEBAPPS_DIR
argument_list|)
decl_stmt|;
name|File
name|testWebappDir
init|=
operator|new
name|File
argument_list|(
name|webapps
operator|+
name|File
operator|.
name|separatorChar
operator|+
name|TEST
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|testWebappDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Test webapp dir "
operator|+
name|testWebappDir
operator|.
name|getCanonicalPath
argument_list|()
operator|+
literal|" missing"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{     }
block|}
comment|/**    * Create an HttpServer instance on the given address for the given webapp    * @param host to bind    * @param port to bind    * @return the server    * @throws IOException if it could not be created    */
DECL|method|createServer (String host, int port)
specifier|public
specifier|static
name|HttpServer
name|createServer
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
name|prepareTestWebapp
argument_list|()
expr_stmt|;
return|return
operator|new
name|HttpServer
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
name|TEST
argument_list|)
operator|.
name|setBindAddress
argument_list|(
name|host
argument_list|)
operator|.
name|setPort
argument_list|(
name|port
argument_list|)
operator|.
name|setFindPort
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Create an HttpServer instance for the given webapp    * @param webapp the webapp to work with    * @return the server    * @throws IOException if it could not be created    */
DECL|method|createServer (String webapp)
specifier|public
specifier|static
name|HttpServer
name|createServer
parameter_list|(
name|String
name|webapp
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|HttpServer
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
name|webapp
argument_list|)
operator|.
name|setBindAddress
argument_list|(
literal|"0.0.0.0"
argument_list|)
operator|.
name|setPort
argument_list|(
literal|0
argument_list|)
operator|.
name|setFindPort
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Create an HttpServer instance for the given webapp    * @param webapp the webapp to work with    * @param conf the configuration to use for the server    * @return the server    * @throws IOException if it could not be created    */
DECL|method|createServer (String webapp, Configuration conf)
specifier|public
specifier|static
name|HttpServer
name|createServer
parameter_list|(
name|String
name|webapp
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|HttpServer
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
name|webapp
argument_list|)
operator|.
name|setBindAddress
argument_list|(
literal|"0.0.0.0"
argument_list|)
operator|.
name|setPort
argument_list|(
literal|0
argument_list|)
operator|.
name|setFindPort
argument_list|(
literal|true
argument_list|)
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|createServer (String webapp, Configuration conf, AccessControlList adminsAcl)
specifier|public
specifier|static
name|HttpServer
name|createServer
parameter_list|(
name|String
name|webapp
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|AccessControlList
name|adminsAcl
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|HttpServer
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
name|webapp
argument_list|)
operator|.
name|setBindAddress
argument_list|(
literal|"0.0.0.0"
argument_list|)
operator|.
name|setPort
argument_list|(
literal|0
argument_list|)
operator|.
name|setFindPort
argument_list|(
literal|true
argument_list|)
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
operator|.
name|setACL
argument_list|(
name|adminsAcl
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Create an HttpServer instance for the given webapp    * @param webapp the webapp to work with    * @param conf the configuration to use for the server    * @param pathSpecs the paths specifications the server will service    * @return the server    * @throws IOException if it could not be created    */
DECL|method|createServer (String webapp, Configuration conf, String[] pathSpecs)
specifier|public
specifier|static
name|HttpServer
name|createServer
parameter_list|(
name|String
name|webapp
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|String
index|[]
name|pathSpecs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|HttpServer
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
name|webapp
argument_list|)
operator|.
name|setBindAddress
argument_list|(
literal|"0.0.0.0"
argument_list|)
operator|.
name|setPort
argument_list|(
literal|0
argument_list|)
operator|.
name|setFindPort
argument_list|(
literal|true
argument_list|)
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
operator|.
name|setPathSpec
argument_list|(
name|pathSpecs
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Create and start a server with the test webapp    *    * @return the newly started server    *    * @throws IOException on any failure    * @throws AssertionError if a condition was not met    */
DECL|method|createAndStartTestServer ()
specifier|public
specifier|static
name|HttpServer
name|createAndStartTestServer
parameter_list|()
throws|throws
name|IOException
block|{
name|HttpServer
name|server
init|=
name|createTestServer
argument_list|()
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|server
return|;
block|}
comment|/**    * If the server is non null, stop it    * @param server to stop    * @throws Exception on any failure    */
DECL|method|stop (HttpServer server)
specifier|public
specifier|static
name|void
name|stop
parameter_list|(
name|HttpServer
name|server
parameter_list|)
throws|throws
name|Exception
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
comment|/**    * Pass in a server, return a URL bound to localhost and its port    * @param server server    * @return a URL bonded to the base of the server    * @throws MalformedURLException if the URL cannot be created.    */
DECL|method|getServerURL (HttpServer server)
specifier|public
specifier|static
name|URL
name|getServerURL
parameter_list|(
name|HttpServer
name|server
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|assertNotNull
argument_list|(
literal|"No server"
argument_list|,
name|server
argument_list|)
expr_stmt|;
name|int
name|port
init|=
name|server
operator|.
name|getPort
argument_list|()
decl_stmt|;
return|return
operator|new
name|URL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/"
argument_list|)
return|;
block|}
comment|/**    * Read in the content from a URL    * @param url URL To read    * @return the text from the output    * @throws IOException if something went wrong    */
DECL|method|readOutput (URL url)
specifier|protected
specifier|static
name|String
name|readOutput
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|InputStream
name|in
init|=
name|url
operator|.
name|openConnection
argument_list|()
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|64
operator|*
literal|1024
index|]
decl_stmt|;
name|int
name|len
init|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
argument_list|)
expr_stmt|;
name|len
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

