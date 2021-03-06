begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.http
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
package|;
end_package

begin_import
import|import
name|okhttp3
operator|.
name|mockwebserver
operator|.
name|MockResponse
import|;
end_import

begin_import
import|import
name|okhttp3
operator|.
name|mockwebserver
operator|.
name|MockWebServer
import|;
end_import

begin_import
import|import
name|okhttp3
operator|.
name|mockwebserver
operator|.
name|RecordedRequest
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
name|io
operator|.
name|IOUtils
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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

begin_comment
comment|/**  * Testing HttpFileSystem.  */
end_comment

begin_class
DECL|class|TestHttpFileSystem
specifier|public
class|class
name|TestHttpFileSystem
block|{
annotation|@
name|Test
DECL|method|testHttpFileSystem ()
specifier|public
name|void
name|testHttpFileSystem
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
throws|,
name|InterruptedException
block|{
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
literal|"fs.http.impl"
argument_list|,
name|HttpFileSystem
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|data
init|=
literal|"foo"
decl_stmt|;
try|try
init|(
name|MockWebServer
name|server
init|=
operator|new
name|MockWebServer
argument_list|()
init|)
block|{
name|server
operator|.
name|enqueue
argument_list|(
operator|new
name|MockResponse
argument_list|()
operator|.
name|setBody
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|URI
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"http://%s:%d"
argument_list|,
name|server
operator|.
name|getHostName
argument_list|()
argument_list|,
name|server
operator|.
name|getPort
argument_list|()
argument_list|)
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
try|try
init|(
name|InputStream
name|is
init|=
name|fs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
operator|new
name|URL
argument_list|(
name|uri
operator|.
name|toURL
argument_list|()
argument_list|,
literal|"/foo"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|,
literal|4096
argument_list|)
init|)
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|data
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|is
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data
argument_list|,
operator|new
name|String
argument_list|(
name|buf
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|RecordedRequest
name|req
init|=
name|server
operator|.
name|takeRequest
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/foo"
argument_list|,
name|req
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

