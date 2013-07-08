begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.cli
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
name|cli
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
name|mockito
operator|.
name|Matchers
operator|.
name|any
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
name|anyInt
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
name|spy
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
name|times
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
name|verify
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
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|commons
operator|.
name|lang
operator|.
name|time
operator|.
name|DateFormatUtils
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
name|ApplicationAttemptId
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
name|ApplicationReport
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
name|FinalApplicationStatus
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
name|NodeId
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
name|NodeReport
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
name|NodeState
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
name|Resource
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
name|YarnApplicationState
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
name|client
operator|.
name|api
operator|.
name|YarnClient
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
name|util
operator|.
name|Records
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
DECL|class|TestYarnCLI
specifier|public
class|class
name|TestYarnCLI
block|{
DECL|field|client
specifier|private
name|YarnClient
name|client
init|=
name|mock
argument_list|(
name|YarnClient
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|sysOutStream
name|ByteArrayOutputStream
name|sysOutStream
decl_stmt|;
DECL|field|sysOut
specifier|private
name|PrintStream
name|sysOut
decl_stmt|;
DECL|field|sysErrStream
name|ByteArrayOutputStream
name|sysErrStream
decl_stmt|;
DECL|field|sysErr
specifier|private
name|PrintStream
name|sysErr
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|sysOutStream
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|sysOut
operator|=
name|spy
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|sysOutStream
argument_list|)
argument_list|)
expr_stmt|;
name|sysErrStream
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|sysErr
operator|=
name|spy
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|sysErrStream
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetApplicationReport ()
specifier|public
name|void
name|testGetApplicationReport
parameter_list|()
throws|throws
name|Exception
block|{
name|ApplicationCLI
name|cli
init|=
name|createAndGetAppCLI
argument_list|()
decl_stmt|;
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1234
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|ApplicationReport
name|newApplicationReport
init|=
name|ApplicationReport
operator|.
name|newInstance
argument_list|(
name|applicationId
argument_list|,
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|applicationId
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"user"
argument_list|,
literal|"queue"
argument_list|,
literal|"appname"
argument_list|,
literal|"host"
argument_list|,
literal|124
argument_list|,
literal|null
argument_list|,
name|YarnApplicationState
operator|.
name|FINISHED
argument_list|,
literal|"diagnostics"
argument_list|,
literal|"url"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|FinalApplicationStatus
operator|.
name|SUCCEEDED
argument_list|,
literal|null
argument_list|,
literal|"N/A"
argument_list|,
literal|0.53789f
argument_list|,
literal|"YARN"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|client
operator|.
name|getApplicationReport
argument_list|(
name|any
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|newApplicationReport
argument_list|)
expr_stmt|;
name|int
name|result
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-status"
block|,
name|applicationId
operator|.
name|toString
argument_list|()
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|client
argument_list|)
operator|.
name|getApplicationReport
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"Application Report : "
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tApplication-Id : application_1234_0005"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tApplication-Name : appname"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tApplication-Type : YARN"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tUser : user"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tQueue : queue"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tStart-Time : 0"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tFinish-Time : 0"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tProgress : 53.79%"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tState : FINISHED"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tFinal-State : SUCCEEDED"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tTracking-URL : N/A"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tRPC Port : 124"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tAM Host : host"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tDiagnostics : diagnostics"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|appReportStr
init|=
name|baos
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appReportStr
argument_list|,
name|sysOutStream
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|sysOut
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|println
argument_list|(
name|isA
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetAllApplications ()
specifier|public
name|void
name|testGetAllApplications
parameter_list|()
throws|throws
name|Exception
block|{
name|ApplicationCLI
name|cli
init|=
name|createAndGetAppCLI
argument_list|()
decl_stmt|;
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1234
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|ApplicationReport
name|newApplicationReport
init|=
name|ApplicationReport
operator|.
name|newInstance
argument_list|(
name|applicationId
argument_list|,
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|applicationId
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"user"
argument_list|,
literal|"queue"
argument_list|,
literal|"appname"
argument_list|,
literal|"host"
argument_list|,
literal|124
argument_list|,
literal|null
argument_list|,
name|YarnApplicationState
operator|.
name|FINISHED
argument_list|,
literal|"diagnostics"
argument_list|,
literal|"url"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|FinalApplicationStatus
operator|.
name|SUCCEEDED
argument_list|,
literal|null
argument_list|,
literal|"N/A"
argument_list|,
literal|0.53789f
argument_list|,
literal|"YARN"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|applicationReports
init|=
operator|new
name|ArrayList
argument_list|<
name|ApplicationReport
argument_list|>
argument_list|()
decl_stmt|;
name|applicationReports
operator|.
name|add
argument_list|(
name|newApplicationReport
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|client
operator|.
name|getApplicationList
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|applicationReports
argument_list|)
expr_stmt|;
name|int
name|result
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-list"
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|client
argument_list|)
operator|.
name|getApplicationList
argument_list|()
expr_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"Total Applications:1"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"                Application-Id\t    Application-Name"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"\t    Application-Type"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"\t      User\t     Queue\t             State\t       "
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"Final-State\t       Progress"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\t                       Tracking-URL"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"         application_1234_0005\t             "
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"appname\t                YARN\t      user\t     "
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"queue\t          FINISHED\t         "
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"SUCCEEDED\t         53.79%"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\t                                N/A"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|appsReportStr
init|=
name|baos
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appsReportStr
argument_list|,
name|sysOutStream
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|sysOut
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|write
argument_list|(
name|any
argument_list|(
name|byte
index|[]
operator|.
expr|class
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testKillApplication ()
specifier|public
name|void
name|testKillApplication
parameter_list|()
throws|throws
name|Exception
block|{
name|ApplicationCLI
name|cli
init|=
name|createAndGetAppCLI
argument_list|()
decl_stmt|;
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1234
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|int
name|result
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-kill"
block|,
name|applicationId
operator|.
name|toString
argument_list|()
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|client
argument_list|)
operator|.
name|killApplication
argument_list|(
name|any
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|sysOut
argument_list|)
operator|.
name|println
argument_list|(
literal|"Killing application application_1234_0005"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testListClusterNodes ()
specifier|public
name|void
name|testListClusterNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeCLI
name|cli
init|=
operator|new
name|NodeCLI
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|client
operator|.
name|getNodeReports
argument_list|(
name|NodeState
operator|.
name|RUNNING
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|getNodeReports
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|cli
operator|.
name|setClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|cli
operator|.
name|setSysOutPrintStream
argument_list|(
name|sysOut
argument_list|)
expr_stmt|;
name|int
name|result
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-list"
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|client
argument_list|)
operator|.
name|getNodeReports
argument_list|(
name|NodeState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"Total Nodes:3"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"         Node-Id\tNode-State\tNode-Http-Address\t"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"Running-Containers"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"         host0:0\t   RUNNING\t       host1:8888"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\t                 0"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"         host1:0\t   RUNNING\t       host1:8888"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\t                 0"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"         host2:0\t   RUNNING\t       host1:8888"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\t                 0"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|nodesReportStr
init|=
name|baos
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|nodesReportStr
argument_list|,
name|sysOutStream
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|sysOut
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|write
argument_list|(
name|any
argument_list|(
name|byte
index|[]
operator|.
expr|class
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodeStatus ()
specifier|public
name|void
name|testNodeStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeId
name|nodeId
init|=
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"host0"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|NodeCLI
name|cli
init|=
operator|new
name|NodeCLI
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|client
operator|.
name|getNodeReports
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|getNodeReports
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|cli
operator|.
name|setClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|cli
operator|.
name|setSysOutPrintStream
argument_list|(
name|sysOut
argument_list|)
expr_stmt|;
name|cli
operator|.
name|setSysErrPrintStream
argument_list|(
name|sysErr
argument_list|)
expr_stmt|;
name|int
name|result
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-status"
block|,
name|nodeId
operator|.
name|toString
argument_list|()
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|client
argument_list|)
operator|.
name|getNodeReports
argument_list|()
expr_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"Node Report : "
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tNode-Id : host0:0"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tRack : rack1"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tNode-State : RUNNING"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tNode-Http-Address : host1:8888"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tLast-Health-Update : "
operator|+
name|DateFormatUtils
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"E dd/MMM/yy hh:mm:ss:SSzz"
argument_list|)
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tHealth-Report : "
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tContainers : 0"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tMemory-Used : 0M"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"\tMemory-Capacity : 0"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|nodeStatusStr
init|=
name|baos
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|sysOut
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|println
argument_list|(
name|isA
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|sysOut
argument_list|)
operator|.
name|println
argument_list|(
name|nodeStatusStr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAbsentNodeStatus ()
specifier|public
name|void
name|testAbsentNodeStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeId
name|nodeId
init|=
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"Absenthost0"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|NodeCLI
name|cli
init|=
operator|new
name|NodeCLI
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|client
operator|.
name|getNodeReports
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|getNodeReports
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|cli
operator|.
name|setClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|cli
operator|.
name|setSysOutPrintStream
argument_list|(
name|sysOut
argument_list|)
expr_stmt|;
name|cli
operator|.
name|setSysErrPrintStream
argument_list|(
name|sysErr
argument_list|)
expr_stmt|;
name|int
name|result
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-status"
block|,
name|nodeId
operator|.
name|toString
argument_list|()
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|client
argument_list|)
operator|.
name|getNodeReports
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|sysOut
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|println
argument_list|(
name|isA
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|sysOut
argument_list|)
operator|.
name|println
argument_list|(
literal|"Could not find the node report for node id : "
operator|+
name|nodeId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppCLIUsageInfo ()
specifier|public
name|void
name|testAppCLIUsageInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|verifyUsageInfo
argument_list|(
operator|new
name|ApplicationCLI
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodeCLIUsageInfo ()
specifier|public
name|void
name|testNodeCLIUsageInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|verifyUsageInfo
argument_list|(
operator|new
name|NodeCLI
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyUsageInfo (YarnCLI cli)
specifier|private
name|void
name|verifyUsageInfo
parameter_list|(
name|YarnCLI
name|cli
parameter_list|)
throws|throws
name|Exception
block|{
name|cli
operator|.
name|setSysErrPrintStream
argument_list|(
name|sysErr
argument_list|)
expr_stmt|;
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|sysErr
argument_list|)
operator|.
name|println
argument_list|(
literal|"Invalid Command Usage : "
argument_list|)
expr_stmt|;
block|}
DECL|method|getNodeReports (int noOfNodes)
specifier|private
name|List
argument_list|<
name|NodeReport
argument_list|>
name|getNodeReports
parameter_list|(
name|int
name|noOfNodes
parameter_list|)
block|{
name|List
argument_list|<
name|NodeReport
argument_list|>
name|nodeReports
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeReport
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|noOfNodes
condition|;
name|i
operator|++
control|)
block|{
name|NodeReport
name|nodeReport
init|=
name|NodeReport
operator|.
name|newInstance
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"host"
operator|+
name|i
argument_list|,
literal|0
argument_list|)
argument_list|,
name|NodeState
operator|.
name|RUNNING
argument_list|,
literal|"host"
operator|+
literal|1
operator|+
literal|":8888"
argument_list|,
literal|"rack1"
argument_list|,
name|Records
operator|.
name|newRecord
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
argument_list|,
name|Records
operator|.
name|newRecord
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|nodeReports
operator|.
name|add
argument_list|(
name|nodeReport
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeReports
return|;
block|}
DECL|method|createAndGetAppCLI ()
specifier|private
name|ApplicationCLI
name|createAndGetAppCLI
parameter_list|()
block|{
name|ApplicationCLI
name|cli
init|=
operator|new
name|ApplicationCLI
argument_list|()
decl_stmt|;
name|cli
operator|.
name|setClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|cli
operator|.
name|setSysOutPrintStream
argument_list|(
name|sysOut
argument_list|)
expr_stmt|;
return|return
name|cli
return|;
block|}
block|}
end_class

end_unit

