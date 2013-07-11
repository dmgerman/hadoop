begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|startupprogress
operator|.
name|StartupProgressTestHelper
operator|.
name|*
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|PrintWriter
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|startupprogress
operator|.
name|StartupProgress
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

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|util
operator|.
name|ajax
operator|.
name|JSON
import|;
end_import

begin_class
DECL|class|TestStartupProgressServlet
specifier|public
class|class
name|TestStartupProgressServlet
block|{
DECL|field|req
specifier|private
name|HttpServletRequest
name|req
decl_stmt|;
DECL|field|resp
specifier|private
name|HttpServletResponse
name|resp
decl_stmt|;
DECL|field|respOut
specifier|private
name|ByteArrayOutputStream
name|respOut
decl_stmt|;
DECL|field|startupProgress
specifier|private
name|StartupProgress
name|startupProgress
decl_stmt|;
DECL|field|servlet
specifier|private
name|StartupProgressServlet
name|servlet
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|startupProgress
operator|=
operator|new
name|StartupProgress
argument_list|()
expr_stmt|;
name|ServletContext
name|context
init|=
name|mock
argument_list|(
name|ServletContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getAttribute
argument_list|(
name|NameNodeHttpServer
operator|.
name|STARTUP_PROGRESS_ATTRIBUTE_KEY
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|startupProgress
argument_list|)
expr_stmt|;
name|servlet
operator|=
name|mock
argument_list|(
name|StartupProgressServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|servlet
operator|.
name|getServletContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|doCallRealMethod
argument_list|()
operator|.
name|when
argument_list|(
name|servlet
argument_list|)
operator|.
name|doGet
argument_list|(
name|any
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|HttpServletResponse
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|=
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
expr_stmt|;
name|respOut
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|respOut
argument_list|)
decl_stmt|;
name|resp
operator|=
name|mock
argument_list|(
name|HttpServletResponse
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|resp
operator|.
name|getWriter
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInitialState ()
specifier|public
name|void
name|testInitialState
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|respBody
init|=
name|doGetAndReturnResponseBody
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|respBody
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expected
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|0.0f
argument_list|)
decl|.
name|put
argument_list|(
literal|"phases"
argument_list|,
name|Arrays
operator|.
expr|<
name|Object
operator|>
name|asList
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"LoadingFsImage"
argument_list|)
operator|.
name|put
argument_list|(
literal|"status"
argument_list|,
literal|"PENDING"
argument_list|)
operator|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|0.0f
argument_list|)
operator|.
name|put
argument_list|(
literal|"steps"
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"LoadingEdits"
argument_list|)
operator|.
name|put
argument_list|(
literal|"status"
argument_list|,
literal|"PENDING"
argument_list|)
operator|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|0.0f
argument_list|)
operator|.
name|put
argument_list|(
literal|"steps"
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"SavingCheckpoint"
argument_list|)
operator|.
name|put
argument_list|(
literal|"status"
argument_list|,
literal|"PENDING"
argument_list|)
operator|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|0.0f
argument_list|)
operator|.
name|put
argument_list|(
literal|"steps"
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"SafeMode"
argument_list|)
operator|.
name|put
argument_list|(
literal|"status"
argument_list|,
literal|"PENDING"
argument_list|)
operator|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|0.0f
argument_list|)
operator|.
name|put
argument_list|(
literal|"steps"
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|JSON
operator|.
name|toString
argument_list|(
name|expected
argument_list|)
argument_list|,
name|filterJson
argument_list|(
name|respBody
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRunningState ()
specifier|public
name|void
name|testRunningState
parameter_list|()
throws|throws
name|Exception
block|{
name|setStartupProgressForRunningState
argument_list|(
name|startupProgress
argument_list|)
expr_stmt|;
name|String
name|respBody
init|=
name|doGetAndReturnResponseBody
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|respBody
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expected
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|0.375f
argument_list|)
decl|.
name|put
argument_list|(
literal|"phases"
argument_list|,
name|Arrays
operator|.
expr|<
name|Object
operator|>
name|asList
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"LoadingFsImage"
argument_list|)
operator|.
name|put
argument_list|(
literal|"status"
argument_list|,
literal|"COMPLETE"
argument_list|)
operator|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|1.0f
argument_list|)
operator|.
name|put
argument_list|(
literal|"steps"
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|singletonList
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"Inodes"
argument_list|)
operator|.
name|put
argument_list|(
literal|"count"
argument_list|,
literal|100L
argument_list|)
operator|.
name|put
argument_list|(
literal|"total"
argument_list|,
literal|100L
argument_list|)
operator|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|1.0f
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"LoadingEdits"
argument_list|)
operator|.
name|put
argument_list|(
literal|"status"
argument_list|,
literal|"RUNNING"
argument_list|)
operator|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|0.5f
argument_list|)
operator|.
name|put
argument_list|(
literal|"steps"
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|singletonList
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"count"
argument_list|,
literal|100L
argument_list|)
operator|.
name|put
argument_list|(
literal|"file"
argument_list|,
literal|"file"
argument_list|)
operator|.
name|put
argument_list|(
literal|"size"
argument_list|,
literal|1000L
argument_list|)
operator|.
name|put
argument_list|(
literal|"total"
argument_list|,
literal|200L
argument_list|)
operator|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|0.5f
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"SavingCheckpoint"
argument_list|)
operator|.
name|put
argument_list|(
literal|"status"
argument_list|,
literal|"PENDING"
argument_list|)
operator|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|0.0f
argument_list|)
operator|.
name|put
argument_list|(
literal|"steps"
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"SafeMode"
argument_list|)
operator|.
name|put
argument_list|(
literal|"status"
argument_list|,
literal|"PENDING"
argument_list|)
operator|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|0.0f
argument_list|)
operator|.
name|put
argument_list|(
literal|"steps"
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|JSON
operator|.
name|toString
argument_list|(
name|expected
argument_list|)
argument_list|,
name|filterJson
argument_list|(
name|respBody
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFinalState ()
specifier|public
name|void
name|testFinalState
parameter_list|()
throws|throws
name|Exception
block|{
name|setStartupProgressForFinalState
argument_list|(
name|startupProgress
argument_list|)
expr_stmt|;
name|String
name|respBody
init|=
name|doGetAndReturnResponseBody
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|respBody
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expected
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|1.0f
argument_list|)
decl|.
name|put
argument_list|(
literal|"phases"
argument_list|,
name|Arrays
operator|.
expr|<
name|Object
operator|>
name|asList
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"LoadingFsImage"
argument_list|)
operator|.
name|put
argument_list|(
literal|"status"
argument_list|,
literal|"COMPLETE"
argument_list|)
operator|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|1.0f
argument_list|)
operator|.
name|put
argument_list|(
literal|"steps"
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|singletonList
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"Inodes"
argument_list|)
operator|.
name|put
argument_list|(
literal|"count"
argument_list|,
literal|100L
argument_list|)
operator|.
name|put
argument_list|(
literal|"total"
argument_list|,
literal|100L
argument_list|)
operator|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|1.0f
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"LoadingEdits"
argument_list|)
operator|.
name|put
argument_list|(
literal|"status"
argument_list|,
literal|"COMPLETE"
argument_list|)
operator|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|1.0f
argument_list|)
operator|.
name|put
argument_list|(
literal|"steps"
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|singletonList
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"count"
argument_list|,
literal|200L
argument_list|)
operator|.
name|put
argument_list|(
literal|"file"
argument_list|,
literal|"file"
argument_list|)
operator|.
name|put
argument_list|(
literal|"size"
argument_list|,
literal|1000L
argument_list|)
operator|.
name|put
argument_list|(
literal|"total"
argument_list|,
literal|200L
argument_list|)
operator|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|1.0f
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"SavingCheckpoint"
argument_list|)
operator|.
name|put
argument_list|(
literal|"status"
argument_list|,
literal|"COMPLETE"
argument_list|)
operator|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|1.0f
argument_list|)
operator|.
name|put
argument_list|(
literal|"steps"
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|singletonList
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"Inodes"
argument_list|)
operator|.
name|put
argument_list|(
literal|"count"
argument_list|,
literal|300L
argument_list|)
operator|.
name|put
argument_list|(
literal|"total"
argument_list|,
literal|300L
argument_list|)
operator|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|1.0f
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"SafeMode"
argument_list|)
operator|.
name|put
argument_list|(
literal|"status"
argument_list|,
literal|"COMPLETE"
argument_list|)
operator|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|1.0f
argument_list|)
operator|.
name|put
argument_list|(
literal|"steps"
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|singletonList
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"AwaitingReportedBlocks"
argument_list|)
operator|.
name|put
argument_list|(
literal|"count"
argument_list|,
literal|400L
argument_list|)
operator|.
name|put
argument_list|(
literal|"total"
argument_list|,
literal|400L
argument_list|)
operator|.
name|put
argument_list|(
literal|"percentComplete"
argument_list|,
literal|1.0f
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|JSON
operator|.
name|toString
argument_list|(
name|expected
argument_list|)
argument_list|,
name|filterJson
argument_list|(
name|respBody
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Calls doGet on the servlet, captures the response body as a string, and    * returns it to the caller.    *     * @return String response body    * @throws IOException thrown if there is an I/O error    */
DECL|method|doGetAndReturnResponseBody ()
specifier|private
name|String
name|doGetAndReturnResponseBody
parameter_list|()
throws|throws
name|IOException
block|{
name|servlet
operator|.
name|doGet
argument_list|(
name|req
argument_list|,
name|resp
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|respOut
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
comment|/**    * Filters the given JSON response body, removing elements that would impede    * testing.  Specifically, it removes elapsedTime fields, because we cannot    * predict the exact values.    *     * @param str String to filter    * @return String filtered value    */
DECL|method|filterJson (String str)
specifier|private
name|String
name|filterJson
parameter_list|(
name|String
name|str
parameter_list|)
block|{
return|return
name|str
operator|.
name|replaceAll
argument_list|(
literal|"\"elapsedTime\":\\d+\\,"
argument_list|,
literal|""
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\\,\"elapsedTime\":\\d+"
argument_list|,
literal|""
argument_list|)
return|;
block|}
block|}
end_class

end_unit

