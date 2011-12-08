begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
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
name|servlet
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServlet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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

begin_class
DECL|class|TestHFSTestCase
specifier|public
class|class
name|TestHFSTestCase
extends|extends
name|HFSTestCase
block|{
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
DECL|method|testDirNoAnnotation ()
specifier|public
name|void
name|testDirNoAnnotation
parameter_list|()
throws|throws
name|Exception
block|{
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
DECL|method|testJettyNoAnnotation ()
specifier|public
name|void
name|testJettyNoAnnotation
parameter_list|()
throws|throws
name|Exception
block|{
name|TestJettyHelper
operator|.
name|getJettyServer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
DECL|method|testJettyNoAnnotation2 ()
specifier|public
name|void
name|testJettyNoAnnotation2
parameter_list|()
throws|throws
name|Exception
block|{
name|TestJettyHelper
operator|.
name|getJettyURL
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
DECL|method|testHdfsNoAnnotation ()
specifier|public
name|void
name|testHdfsNoAnnotation
parameter_list|()
throws|throws
name|Exception
block|{
name|TestHdfsHelper
operator|.
name|getHdfsConf
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
DECL|method|testHdfsNoAnnotation2 ()
specifier|public
name|void
name|testHdfsNoAnnotation2
parameter_list|()
throws|throws
name|Exception
block|{
name|TestHdfsHelper
operator|.
name|getHdfsTestDir
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestDir
DECL|method|testDirAnnotation ()
specifier|public
name|void
name|testDirAnnotation
parameter_list|()
throws|throws
name|Exception
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|waitFor ()
specifier|public
name|void
name|waitFor
parameter_list|()
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|waited
init|=
name|waitFor
argument_list|(
literal|1000
argument_list|,
operator|new
name|Predicate
argument_list|()
block|{
specifier|public
name|boolean
name|evaluate
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|waited
argument_list|,
literal|0
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|end
operator|-
name|start
operator|-
name|waited
argument_list|,
literal|0
argument_list|,
literal|50
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|waitForTimeOutRatio1 ()
specifier|public
name|void
name|waitForTimeOutRatio1
parameter_list|()
block|{
name|setWaitForRatio
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|waited
init|=
name|waitFor
argument_list|(
literal|200
argument_list|,
operator|new
name|Predicate
argument_list|()
block|{
specifier|public
name|boolean
name|evaluate
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|false
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|waited
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|end
operator|-
name|start
argument_list|,
literal|200
argument_list|,
literal|50
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|waitForTimeOutRatio2 ()
specifier|public
name|void
name|waitForTimeOutRatio2
parameter_list|()
block|{
name|setWaitForRatio
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|waited
init|=
name|waitFor
argument_list|(
literal|200
argument_list|,
operator|new
name|Predicate
argument_list|()
block|{
specifier|public
name|boolean
name|evaluate
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|false
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|waited
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|end
operator|-
name|start
argument_list|,
literal|200
operator|*
name|getWaitForRatio
argument_list|()
argument_list|,
literal|50
operator|*
name|getWaitForRatio
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|sleepRatio1 ()
specifier|public
name|void
name|sleepRatio1
parameter_list|()
block|{
name|setWaitForRatio
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|end
operator|-
name|start
argument_list|,
literal|100
argument_list|,
literal|50
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|sleepRatio2 ()
specifier|public
name|void
name|sleepRatio2
parameter_list|()
block|{
name|setWaitForRatio
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|end
operator|-
name|start
argument_list|,
literal|100
operator|*
name|getWaitForRatio
argument_list|()
argument_list|,
literal|50
operator|*
name|getWaitForRatio
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestHdfs
DECL|method|testHadoopFileSystem ()
specifier|public
name|void
name|testHadoopFileSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|TestHdfsHelper
operator|.
name|getHdfsConf
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|OutputStream
name|os
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|TestHdfsHelper
operator|.
name|getHdfsTestDir
argument_list|()
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|TestHdfsHelper
operator|.
name|getHdfsTestDir
argument_list|()
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|is
operator|.
name|read
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|is
operator|.
name|read
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|MyServlet
specifier|public
specifier|static
class|class
name|MyServlet
extends|extends
name|HttpServlet
block|{
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest req, HttpServletResponse resp)
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|resp
operator|.
name|getWriter
argument_list|()
operator|.
name|write
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|TestJetty
DECL|method|testJetty ()
specifier|public
name|void
name|testJetty
parameter_list|()
throws|throws
name|Exception
block|{
name|Context
name|context
init|=
operator|new
name|Context
argument_list|()
decl_stmt|;
name|context
operator|.
name|setContextPath
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|context
operator|.
name|addServlet
argument_list|(
name|MyServlet
operator|.
name|class
argument_list|,
literal|"/bar"
argument_list|)
expr_stmt|;
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
literal|"/bar"
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
name|Assert
operator|.
name|assertEquals
argument_list|(
name|reader
operator|.
name|readLine
argument_list|()
argument_list|,
literal|"foo"
argument_list|)
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
name|TestException
argument_list|(
name|exception
operator|=
name|RuntimeException
operator|.
name|class
argument_list|)
DECL|method|testException0 ()
specifier|public
name|void
name|testException0
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"foo"
argument_list|)
throw|;
block|}
annotation|@
name|Test
annotation|@
name|TestException
argument_list|(
name|exception
operator|=
name|RuntimeException
operator|.
name|class
argument_list|,
name|msgRegExp
operator|=
literal|".o."
argument_list|)
DECL|method|testException1 ()
specifier|public
name|void
name|testException1
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"foo"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

