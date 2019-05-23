begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.logaggregation
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|logaggregation
package|;
end_package

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
name|File
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
name|PrintWriter
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|http
operator|.
name|HttpServletRequest
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
name|io
operator|.
name|FileUtils
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAccessType
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
name|ContainerId
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
name|impl
operator|.
name|pb
operator|.
name|ApplicationAttemptIdPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|ApplicationIdPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|ContainerIdPBImpl
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
name|conf
operator|.
name|YarnConfiguration
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
name|logaggregation
operator|.
name|filecontroller
operator|.
name|LogAggregationFileController
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
name|logaggregation
operator|.
name|filecontroller
operator|.
name|LogAggregationFileControllerContext
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
name|logaggregation
operator|.
name|filecontroller
operator|.
name|LogAggregationFileControllerFactory
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
name|logaggregation
operator|.
name|filecontroller
operator|.
name|tfile
operator|.
name|TFileAggregatedLogsBlock
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
name|webapp
operator|.
name|YarnWebParams
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
name|webapp
operator|.
name|View
operator|.
name|ViewContext
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
name|webapp
operator|.
name|log
operator|.
name|AggregatedLogsBlockForTest
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
name|webapp
operator|.
name|view
operator|.
name|BlockForTest
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
name|webapp
operator|.
name|view
operator|.
name|HtmlBlock
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
name|webapp
operator|.
name|view
operator|.
name|HtmlBlockForTest
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
name|mockito
operator|.
name|Mockito
operator|.
name|*
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
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

begin_comment
comment|/**  * Test AggregatedLogsBlock. AggregatedLogsBlock should check user, aggregate a  * logs into one file and show this logs or errors into html code  *   */
end_comment

begin_class
DECL|class|TestAggregatedLogsBlock
specifier|public
class|class
name|TestAggregatedLogsBlock
block|{
comment|/**    * Bad user. User 'owner' is trying to read logs without access    */
annotation|@
name|Test
DECL|method|testAccessDenied ()
specifier|public
name|void
name|testAccessDenied
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
literal|"target/logs"
argument_list|)
argument_list|)
expr_stmt|;
name|Configuration
name|configuration
init|=
name|getConfiguration
argument_list|()
decl_stmt|;
name|writeLogs
argument_list|(
literal|"target/logs/logs/application_0_0001/container_0_0001_01_000001"
argument_list|)
expr_stmt|;
name|writeLog
argument_list|(
name|configuration
argument_list|,
literal|"owner"
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|data
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintWriter
name|printWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|HtmlBlock
name|html
init|=
operator|new
name|HtmlBlockForTest
argument_list|()
decl_stmt|;
name|HtmlBlock
operator|.
name|Block
name|block
init|=
operator|new
name|BlockForTest
argument_list|(
name|html
argument_list|,
name|printWriter
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TFileAggregatedLogsBlockForTest
name|aggregatedBlock
init|=
name|getTFileAggregatedLogsBlockForTest
argument_list|(
name|configuration
argument_list|,
literal|"owner"
argument_list|,
literal|"container_0_0001_01_000001"
argument_list|,
literal|"localhost:1234"
argument_list|)
decl_stmt|;
name|aggregatedBlock
operator|.
name|render
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|block
operator|.
name|getWriter
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|out
init|=
name|data
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"User [owner] is not authorized to view the logs for entity"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlockContainsPortNumForUnavailableAppLog ()
specifier|public
name|void
name|testBlockContainsPortNumForUnavailableAppLog
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/logs"
argument_list|)
argument_list|)
expr_stmt|;
name|Configuration
name|configuration
init|=
name|getConfiguration
argument_list|()
decl_stmt|;
name|String
name|nodeName
init|=
name|configuration
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_WEBAPP_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_WEBAPP_ADDRESS
argument_list|)
decl_stmt|;
name|AggregatedLogsBlockForTest
name|aggregatedBlock
init|=
name|getAggregatedLogsBlockForTest
argument_list|(
name|configuration
argument_list|,
literal|"admin"
argument_list|,
literal|"container_0_0001_01_000001"
argument_list|,
name|nodeName
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|data
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintWriter
name|printWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|HtmlBlock
name|html
init|=
operator|new
name|HtmlBlockForTest
argument_list|()
decl_stmt|;
name|HtmlBlock
operator|.
name|Block
name|block
init|=
operator|new
name|BlockForTest
argument_list|(
name|html
argument_list|,
name|printWriter
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|aggregatedBlock
operator|.
name|render
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|block
operator|.
name|getWriter
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|out
init|=
name|data
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|contains
argument_list|(
name|nodeName
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * try to read bad logs    *     * @throws Exception    */
annotation|@
name|Test
DECL|method|testBadLogs ()
specifier|public
name|void
name|testBadLogs
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
literal|"target/logs"
argument_list|)
argument_list|)
expr_stmt|;
name|Configuration
name|configuration
init|=
name|getConfiguration
argument_list|()
decl_stmt|;
name|writeLogs
argument_list|(
literal|"target/logs/logs/application_0_0001/container_0_0001_01_000001"
argument_list|)
expr_stmt|;
name|writeLog
argument_list|(
name|configuration
argument_list|,
literal|"owner"
argument_list|)
expr_stmt|;
name|AggregatedLogsBlockForTest
name|aggregatedBlock
init|=
name|getAggregatedLogsBlockForTest
argument_list|(
name|configuration
argument_list|,
literal|"admin"
argument_list|,
literal|"container_0_0001_01_000001"
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|data
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintWriter
name|printWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|HtmlBlock
name|html
init|=
operator|new
name|HtmlBlockForTest
argument_list|()
decl_stmt|;
name|HtmlBlock
operator|.
name|Block
name|block
init|=
operator|new
name|BlockForTest
argument_list|(
name|html
argument_list|,
name|printWriter
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|aggregatedBlock
operator|.
name|render
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|block
operator|.
name|getWriter
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|out
init|=
name|data
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"Logs not available for entity. Aggregation may not be complete, Check back later or try the nodemanager at localhost:1234"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reading from logs should succeed and they should be shown in the    * AggregatedLogsBlock html.    *     * @throws Exception    */
annotation|@
name|Test
DECL|method|testAggregatedLogsBlock ()
specifier|public
name|void
name|testAggregatedLogsBlock
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
literal|"target/logs"
argument_list|)
argument_list|)
expr_stmt|;
name|Configuration
name|configuration
init|=
name|getConfiguration
argument_list|()
decl_stmt|;
name|writeLogs
argument_list|(
literal|"target/logs/logs/application_0_0001/container_0_0001_01_000001"
argument_list|)
expr_stmt|;
name|writeLog
argument_list|(
name|configuration
argument_list|,
literal|"admin"
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|data
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintWriter
name|printWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|HtmlBlock
name|html
init|=
operator|new
name|HtmlBlockForTest
argument_list|()
decl_stmt|;
name|HtmlBlock
operator|.
name|Block
name|block
init|=
operator|new
name|BlockForTest
argument_list|(
name|html
argument_list|,
name|printWriter
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TFileAggregatedLogsBlockForTest
name|aggregatedBlock
init|=
name|getTFileAggregatedLogsBlockForTest
argument_list|(
name|configuration
argument_list|,
literal|"admin"
argument_list|,
literal|"container_0_0001_01_000001"
argument_list|,
literal|"localhost:1234"
argument_list|)
decl_stmt|;
name|aggregatedBlock
operator|.
name|render
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|block
operator|.
name|getWriter
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|out
init|=
name|data
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"test log1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"test log2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"test log3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reading from logs should succeed (from a HAR archive) and they should be    * shown in the AggregatedLogsBlock html.    *    * @throws Exception    */
annotation|@
name|Test
DECL|method|testAggregatedLogsBlockHar ()
specifier|public
name|void
name|testAggregatedLogsBlockHar
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
literal|"target/logs"
argument_list|)
argument_list|)
expr_stmt|;
name|Configuration
name|configuration
init|=
name|getConfiguration
argument_list|()
decl_stmt|;
name|URL
name|harUrl
init|=
name|ClassLoader
operator|.
name|getSystemClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"application_1440536969523_0001.har"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|harUrl
argument_list|)
expr_stmt|;
name|String
name|path
init|=
literal|"target/logs/admin/logs/application_1440536969523_0001"
operator|+
literal|"/application_1440536969523_0001.har"
decl_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|harUrl
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|data
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintWriter
name|printWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|HtmlBlock
name|html
init|=
operator|new
name|HtmlBlockForTest
argument_list|()
decl_stmt|;
name|HtmlBlock
operator|.
name|Block
name|block
init|=
operator|new
name|BlockForTest
argument_list|(
name|html
argument_list|,
name|printWriter
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TFileAggregatedLogsBlockForTest
name|aggregatedBlock
init|=
name|getTFileAggregatedLogsBlockForTest
argument_list|(
name|configuration
argument_list|,
literal|"admin"
argument_list|,
literal|"container_1440536969523_0001_01_000001"
argument_list|,
literal|"host1:1111"
argument_list|)
decl_stmt|;
name|aggregatedBlock
operator|.
name|render
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|block
operator|.
name|getWriter
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|out
init|=
name|data
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"Hello stderr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"Hello stdout"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"Hello syslog"
argument_list|)
argument_list|)
expr_stmt|;
name|aggregatedBlock
operator|=
name|getTFileAggregatedLogsBlockForTest
argument_list|(
name|configuration
argument_list|,
literal|"admin"
argument_list|,
literal|"container_1440536969523_0001_01_000002"
argument_list|,
literal|"host2:2222"
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|printWriter
operator|=
operator|new
name|PrintWriter
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|html
operator|=
operator|new
name|HtmlBlockForTest
argument_list|()
expr_stmt|;
name|block
operator|=
operator|new
name|BlockForTest
argument_list|(
name|html
argument_list|,
name|printWriter
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|aggregatedBlock
operator|.
name|render
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|block
operator|.
name|getWriter
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
name|out
operator|=
name|data
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"Goodbye stderr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"Goodbye stdout"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"Goodbye syslog"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Log files was deleted.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testNoLogs ()
specifier|public
name|void
name|testNoLogs
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
literal|"target/logs"
argument_list|)
argument_list|)
expr_stmt|;
name|Configuration
name|configuration
init|=
name|getConfiguration
argument_list|()
decl_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
literal|"target/logs/logs/application_0_0001/container_0_0001_01_000001"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|exists
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|f
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writeLog
argument_list|(
name|configuration
argument_list|,
literal|"admin"
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|data
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintWriter
name|printWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|HtmlBlock
name|html
init|=
operator|new
name|HtmlBlockForTest
argument_list|()
decl_stmt|;
name|HtmlBlock
operator|.
name|Block
name|block
init|=
operator|new
name|BlockForTest
argument_list|(
name|html
argument_list|,
name|printWriter
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TFileAggregatedLogsBlockForTest
name|aggregatedBlock
init|=
name|getTFileAggregatedLogsBlockForTest
argument_list|(
name|configuration
argument_list|,
literal|"admin"
argument_list|,
literal|"container_0_0001_01_000001"
argument_list|,
literal|"localhost:1234"
argument_list|)
decl_stmt|;
name|aggregatedBlock
operator|.
name|render
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|block
operator|.
name|getWriter
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|out
init|=
name|data
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"No logs available for container container_0_0001_01_000001"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getConfiguration ()
specifier|private
name|Configuration
name|getConfiguration
parameter_list|()
block|{
name|Configuration
name|configuration
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|LOG_AGGREGATION_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_REMOTE_APP_LOG_DIR
argument_list|,
literal|"target/logs"
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ACL_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ADMIN_ACL
argument_list|,
literal|"admin"
argument_list|)
expr_stmt|;
return|return
name|configuration
return|;
block|}
DECL|method|getAggregatedLogsBlockForTest ( Configuration configuration, String user, String containerId)
specifier|private
name|AggregatedLogsBlockForTest
name|getAggregatedLogsBlockForTest
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|containerId
parameter_list|)
block|{
return|return
name|getAggregatedLogsBlockForTest
argument_list|(
name|configuration
argument_list|,
name|user
argument_list|,
name|containerId
argument_list|,
literal|"localhost:1234"
argument_list|)
return|;
block|}
DECL|method|getTFileAggregatedLogsBlockForTest ( Configuration configuration, String user, String containerId, String nodeName)
specifier|private
name|TFileAggregatedLogsBlockForTest
name|getTFileAggregatedLogsBlockForTest
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|containerId
parameter_list|,
name|String
name|nodeName
parameter_list|)
block|{
name|HttpServletRequest
name|request
init|=
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|request
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|ViewContext
name|mockContext
init|=
name|mock
argument_list|(
name|ViewContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|TFileAggregatedLogsBlockForTest
name|aggregatedBlock
init|=
operator|new
name|TFileAggregatedLogsBlockForTest
argument_list|(
name|mockContext
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|aggregatedBlock
operator|.
name|setRequest
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|aggregatedBlock
operator|.
name|moreParams
argument_list|()
operator|.
name|put
argument_list|(
name|YarnWebParams
operator|.
name|CONTAINER_ID
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
name|aggregatedBlock
operator|.
name|moreParams
argument_list|()
operator|.
name|put
argument_list|(
name|YarnWebParams
operator|.
name|NM_NODENAME
argument_list|,
name|nodeName
argument_list|)
expr_stmt|;
name|aggregatedBlock
operator|.
name|moreParams
argument_list|()
operator|.
name|put
argument_list|(
name|YarnWebParams
operator|.
name|APP_OWNER
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|aggregatedBlock
operator|.
name|moreParams
argument_list|()
operator|.
name|put
argument_list|(
literal|"start"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|aggregatedBlock
operator|.
name|moreParams
argument_list|()
operator|.
name|put
argument_list|(
literal|"end"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|aggregatedBlock
operator|.
name|moreParams
argument_list|()
operator|.
name|put
argument_list|(
name|YarnWebParams
operator|.
name|ENTITY_STRING
argument_list|,
literal|"entity"
argument_list|)
expr_stmt|;
return|return
name|aggregatedBlock
return|;
block|}
DECL|method|getAggregatedLogsBlockForTest ( Configuration configuration, String user, String containerId, String nodeName)
specifier|private
name|AggregatedLogsBlockForTest
name|getAggregatedLogsBlockForTest
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|containerId
parameter_list|,
name|String
name|nodeName
parameter_list|)
block|{
name|HttpServletRequest
name|request
init|=
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|request
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|AggregatedLogsBlockForTest
name|aggregatedBlock
init|=
operator|new
name|AggregatedLogsBlockForTest
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
name|aggregatedBlock
operator|.
name|setRequest
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|aggregatedBlock
operator|.
name|moreParams
argument_list|()
operator|.
name|put
argument_list|(
name|YarnWebParams
operator|.
name|CONTAINER_ID
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
name|aggregatedBlock
operator|.
name|moreParams
argument_list|()
operator|.
name|put
argument_list|(
name|YarnWebParams
operator|.
name|NM_NODENAME
argument_list|,
name|nodeName
argument_list|)
expr_stmt|;
name|aggregatedBlock
operator|.
name|moreParams
argument_list|()
operator|.
name|put
argument_list|(
name|YarnWebParams
operator|.
name|APP_OWNER
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|aggregatedBlock
operator|.
name|moreParams
argument_list|()
operator|.
name|put
argument_list|(
literal|"start"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|aggregatedBlock
operator|.
name|moreParams
argument_list|()
operator|.
name|put
argument_list|(
literal|"end"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|aggregatedBlock
operator|.
name|moreParams
argument_list|()
operator|.
name|put
argument_list|(
name|YarnWebParams
operator|.
name|ENTITY_STRING
argument_list|,
literal|"entity"
argument_list|)
expr_stmt|;
return|return
name|aggregatedBlock
return|;
block|}
DECL|method|writeLog (Configuration configuration, String user)
specifier|private
name|void
name|writeLog
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|String
name|user
parameter_list|)
throws|throws
name|Exception
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationIdPBImpl
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptIdPBImpl
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|ContainerIdPBImpl
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|String
name|path
init|=
literal|"target/logs/"
operator|+
name|user
operator|+
literal|"/logs/application_0_0001/localhost_1234"
decl_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|getParentFile
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|f
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|rootLogDirs
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"target/logs/logs"
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
name|LogAggregationFileControllerFactory
name|factory
init|=
operator|new
name|LogAggregationFileControllerFactory
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
name|LogAggregationFileController
name|fileController
init|=
name|factory
operator|.
name|getFileControllerForWrite
argument_list|()
decl_stmt|;
try|try
block|{
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|appAcls
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|appAcls
operator|.
name|put
argument_list|(
name|ApplicationAccessType
operator|.
name|VIEW_APP
argument_list|,
name|ugi
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|NodeId
name|nodeId
init|=
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"localhost"
argument_list|,
literal|1234
argument_list|)
decl_stmt|;
name|LogAggregationFileControllerContext
name|context
init|=
operator|new
name|LogAggregationFileControllerContext
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|3600
argument_list|,
name|appId
argument_list|,
name|appAcls
argument_list|,
name|nodeId
argument_list|,
name|ugi
argument_list|)
decl_stmt|;
name|fileController
operator|.
name|initializeWriter
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|fileController
operator|.
name|write
argument_list|(
operator|new
name|AggregatedLogFormat
operator|.
name|LogKey
argument_list|(
literal|"container_0_0001_01_000001"
argument_list|)
argument_list|,
operator|new
name|AggregatedLogFormat
operator|.
name|LogValue
argument_list|(
name|rootLogDirs
argument_list|,
name|containerId
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fileController
operator|.
name|closeWriter
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|writeLogs (String dirName)
specifier|private
name|void
name|writeLogs
parameter_list|(
name|String
name|dirName
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|dirName
operator|+
name|File
operator|.
name|separator
operator|+
literal|"log1"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|getParentFile
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|f
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writeLog
argument_list|(
name|dirName
operator|+
name|File
operator|.
name|separator
operator|+
literal|"log1"
argument_list|,
literal|"test log1"
argument_list|)
expr_stmt|;
name|writeLog
argument_list|(
name|dirName
operator|+
name|File
operator|.
name|separator
operator|+
literal|"log2"
argument_list|,
literal|"test log2"
argument_list|)
expr_stmt|;
name|writeLog
argument_list|(
name|dirName
operator|+
name|File
operator|.
name|separator
operator|+
literal|"log3"
argument_list|,
literal|"test log3"
argument_list|)
expr_stmt|;
block|}
DECL|method|writeLog (String fileName, String text)
specifier|private
name|void
name|writeLog
parameter_list|(
name|String
name|fileName
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|Writer
name|writer
init|=
operator|new
name|FileWriter
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|TFileAggregatedLogsBlockForTest
specifier|private
specifier|static
class|class
name|TFileAggregatedLogsBlockForTest
extends|extends
name|TFileAggregatedLogsBlock
block|{
DECL|field|params
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|request
specifier|private
name|HttpServletRequest
name|request
decl_stmt|;
annotation|@
name|Inject
DECL|method|TFileAggregatedLogsBlockForTest (ViewContext ctx, Configuration conf)
name|TFileAggregatedLogsBlockForTest
parameter_list|(
name|ViewContext
name|ctx
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|,
name|conf
argument_list|,
operator|new
name|Path
argument_list|(
literal|"target/logs"
argument_list|)
argument_list|,
literal|"logs"
argument_list|)
expr_stmt|;
block|}
DECL|method|render (Block html)
specifier|public
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|super
operator|.
name|render
argument_list|(
name|html
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|moreParams ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|moreParams
parameter_list|()
block|{
return|return
name|params
return|;
block|}
DECL|method|request ()
specifier|public
name|HttpServletRequest
name|request
parameter_list|()
block|{
return|return
name|request
return|;
block|}
DECL|method|setRequest (HttpServletRequest request)
specifier|public
name|void
name|setRequest
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

