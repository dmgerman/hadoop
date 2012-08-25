begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
package|;
end_package

begin_import
import|import static
name|junit
operator|.
name|framework
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
name|net
operator|.
name|InetAddress
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
name|ipc
operator|.
name|RPC
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
name|ipc
operator|.
name|Server
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
name|ipc
operator|.
name|TestRPC
operator|.
name|TestImpl
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
name|ipc
operator|.
name|TestRPC
operator|.
name|TestProtocol
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
name|server
operator|.
name|nodemanager
operator|.
name|NMAuditLogger
operator|.
name|Keys
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

begin_comment
comment|/**  * Tests {@link NMAuditLogger}.  */
end_comment

begin_class
DECL|class|TestNMAuditLogger
specifier|public
class|class
name|TestNMAuditLogger
block|{
DECL|field|USER
specifier|private
specifier|static
specifier|final
name|String
name|USER
init|=
literal|"test"
decl_stmt|;
DECL|field|OPERATION
specifier|private
specifier|static
specifier|final
name|String
name|OPERATION
init|=
literal|"oper"
decl_stmt|;
DECL|field|TARGET
specifier|private
specifier|static
specifier|final
name|String
name|TARGET
init|=
literal|"tgt"
decl_stmt|;
DECL|field|DESC
specifier|private
specifier|static
specifier|final
name|String
name|DESC
init|=
literal|"description of an audit log"
decl_stmt|;
DECL|field|APPID
specifier|private
specifier|static
specifier|final
name|ApplicationId
name|APPID
init|=
name|mock
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CONTAINERID
specifier|private
specifier|static
specifier|final
name|ContainerId
name|CONTAINERID
init|=
name|mock
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
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
name|when
argument_list|(
name|APPID
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"app_1"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|CONTAINERID
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"container_1"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the AuditLog format with key-val pair.    */
annotation|@
name|Test
DECL|method|testKeyValLogFormat ()
specifier|public
name|void
name|testKeyValLogFormat
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|actLog
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|StringBuilder
name|expLog
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// add the first k=v pair and check
name|NMAuditLogger
operator|.
name|start
argument_list|(
name|Keys
operator|.
name|USER
argument_list|,
name|USER
argument_list|,
name|actLog
argument_list|)
expr_stmt|;
name|expLog
operator|.
name|append
argument_list|(
literal|"USER=test"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expLog
operator|.
name|toString
argument_list|()
argument_list|,
name|actLog
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// append another k1=v1 pair to already added k=v and test
name|NMAuditLogger
operator|.
name|add
argument_list|(
name|Keys
operator|.
name|OPERATION
argument_list|,
name|OPERATION
argument_list|,
name|actLog
argument_list|)
expr_stmt|;
name|expLog
operator|.
name|append
argument_list|(
literal|"\tOPERATION=oper"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expLog
operator|.
name|toString
argument_list|()
argument_list|,
name|actLog
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// append another k1=null pair and test
name|NMAuditLogger
operator|.
name|add
argument_list|(
name|Keys
operator|.
name|APPID
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|,
name|actLog
argument_list|)
expr_stmt|;
name|expLog
operator|.
name|append
argument_list|(
literal|"\tAPPID=null"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expLog
operator|.
name|toString
argument_list|()
argument_list|,
name|actLog
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// now add the target and check of the final string
name|NMAuditLogger
operator|.
name|add
argument_list|(
name|Keys
operator|.
name|TARGET
argument_list|,
name|TARGET
argument_list|,
name|actLog
argument_list|)
expr_stmt|;
name|expLog
operator|.
name|append
argument_list|(
literal|"\tTARGET=tgt"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expLog
operator|.
name|toString
argument_list|()
argument_list|,
name|actLog
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the AuditLog format for successful events.    */
DECL|method|testSuccessLogFormatHelper (boolean checkIP, ApplicationId appId, ContainerId containerId)
specifier|private
name|void
name|testSuccessLogFormatHelper
parameter_list|(
name|boolean
name|checkIP
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|ContainerId
name|containerId
parameter_list|)
block|{
comment|// check without the IP
name|String
name|sLog
init|=
name|NMAuditLogger
operator|.
name|createSuccessLog
argument_list|(
name|USER
argument_list|,
name|OPERATION
argument_list|,
name|TARGET
argument_list|,
name|appId
argument_list|,
name|containerId
argument_list|)
decl_stmt|;
name|StringBuilder
name|expLog
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|expLog
operator|.
name|append
argument_list|(
literal|"USER=test\t"
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkIP
condition|)
block|{
name|InetAddress
name|ip
init|=
name|Server
operator|.
name|getRemoteIp
argument_list|()
decl_stmt|;
name|expLog
operator|.
name|append
argument_list|(
name|Keys
operator|.
name|IP
operator|.
name|name
argument_list|()
operator|+
literal|"="
operator|+
name|ip
operator|.
name|getHostAddress
argument_list|()
operator|+
literal|"\t"
argument_list|)
expr_stmt|;
block|}
name|expLog
operator|.
name|append
argument_list|(
literal|"OPERATION=oper\tTARGET=tgt\tRESULT=SUCCESS"
argument_list|)
expr_stmt|;
if|if
condition|(
name|appId
operator|!=
literal|null
condition|)
block|{
name|expLog
operator|.
name|append
argument_list|(
literal|"\tAPPID=app_1"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|containerId
operator|!=
literal|null
condition|)
block|{
name|expLog
operator|.
name|append
argument_list|(
literal|"\tCONTAINERID=container_1"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expLog
operator|.
name|toString
argument_list|()
argument_list|,
name|sLog
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the AuditLog format for successful events passing nulls.    */
DECL|method|testSuccessLogNulls (boolean checkIP)
specifier|private
name|void
name|testSuccessLogNulls
parameter_list|(
name|boolean
name|checkIP
parameter_list|)
block|{
name|String
name|sLog
init|=
name|NMAuditLogger
operator|.
name|createSuccessLog
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|StringBuilder
name|expLog
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|expLog
operator|.
name|append
argument_list|(
literal|"USER=null\t"
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkIP
condition|)
block|{
name|InetAddress
name|ip
init|=
name|Server
operator|.
name|getRemoteIp
argument_list|()
decl_stmt|;
name|expLog
operator|.
name|append
argument_list|(
name|Keys
operator|.
name|IP
operator|.
name|name
argument_list|()
operator|+
literal|"="
operator|+
name|ip
operator|.
name|getHostAddress
argument_list|()
operator|+
literal|"\t"
argument_list|)
expr_stmt|;
block|}
name|expLog
operator|.
name|append
argument_list|(
literal|"OPERATION=null\tTARGET=null\tRESULT=SUCCESS"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expLog
operator|.
name|toString
argument_list|()
argument_list|,
name|sLog
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the AuditLog format for successful events with the various    * parameters.    */
DECL|method|testSuccessLogFormat (boolean checkIP)
specifier|private
name|void
name|testSuccessLogFormat
parameter_list|(
name|boolean
name|checkIP
parameter_list|)
block|{
name|testSuccessLogFormatHelper
argument_list|(
name|checkIP
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|testSuccessLogFormatHelper
argument_list|(
name|checkIP
argument_list|,
name|APPID
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|testSuccessLogFormatHelper
argument_list|(
name|checkIP
argument_list|,
literal|null
argument_list|,
name|CONTAINERID
argument_list|)
expr_stmt|;
name|testSuccessLogFormatHelper
argument_list|(
name|checkIP
argument_list|,
name|APPID
argument_list|,
name|CONTAINERID
argument_list|)
expr_stmt|;
name|testSuccessLogNulls
argument_list|(
name|checkIP
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the AuditLog format for failure events.    */
DECL|method|testFailureLogFormatHelper (boolean checkIP, ApplicationId appId, ContainerId containerId)
specifier|private
name|void
name|testFailureLogFormatHelper
parameter_list|(
name|boolean
name|checkIP
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|ContainerId
name|containerId
parameter_list|)
block|{
name|String
name|fLog
init|=
name|NMAuditLogger
operator|.
name|createFailureLog
argument_list|(
name|USER
argument_list|,
name|OPERATION
argument_list|,
name|TARGET
argument_list|,
name|DESC
argument_list|,
name|appId
argument_list|,
name|containerId
argument_list|)
decl_stmt|;
name|StringBuilder
name|expLog
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|expLog
operator|.
name|append
argument_list|(
literal|"USER=test\t"
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkIP
condition|)
block|{
name|InetAddress
name|ip
init|=
name|Server
operator|.
name|getRemoteIp
argument_list|()
decl_stmt|;
name|expLog
operator|.
name|append
argument_list|(
name|Keys
operator|.
name|IP
operator|.
name|name
argument_list|()
operator|+
literal|"="
operator|+
name|ip
operator|.
name|getHostAddress
argument_list|()
operator|+
literal|"\t"
argument_list|)
expr_stmt|;
block|}
name|expLog
operator|.
name|append
argument_list|(
literal|"OPERATION=oper\tTARGET=tgt\tRESULT=FAILURE\t"
argument_list|)
expr_stmt|;
name|expLog
operator|.
name|append
argument_list|(
literal|"DESCRIPTION=description of an audit log"
argument_list|)
expr_stmt|;
if|if
condition|(
name|appId
operator|!=
literal|null
condition|)
block|{
name|expLog
operator|.
name|append
argument_list|(
literal|"\tAPPID=app_1"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|containerId
operator|!=
literal|null
condition|)
block|{
name|expLog
operator|.
name|append
argument_list|(
literal|"\tCONTAINERID=container_1"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expLog
operator|.
name|toString
argument_list|()
argument_list|,
name|fLog
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the AuditLog format for failure events with the various    * parameters.    */
DECL|method|testFailureLogFormat (boolean checkIP)
specifier|private
name|void
name|testFailureLogFormat
parameter_list|(
name|boolean
name|checkIP
parameter_list|)
block|{
name|testFailureLogFormatHelper
argument_list|(
name|checkIP
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|testFailureLogFormatHelper
argument_list|(
name|checkIP
argument_list|,
name|APPID
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|testFailureLogFormatHelper
argument_list|(
name|checkIP
argument_list|,
literal|null
argument_list|,
name|CONTAINERID
argument_list|)
expr_stmt|;
name|testFailureLogFormatHelper
argument_list|(
name|checkIP
argument_list|,
name|APPID
argument_list|,
name|CONTAINERID
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test {@link NMAuditLogger} without IP set.    */
annotation|@
name|Test
DECL|method|testNMAuditLoggerWithoutIP ()
specifier|public
name|void
name|testNMAuditLoggerWithoutIP
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test without ip
name|testSuccessLogFormat
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|testFailureLogFormat
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * A special extension of {@link TestImpl} RPC server with     * {@link TestImpl#ping()} testing the audit logs.    */
DECL|class|MyTestRPCServer
specifier|private
class|class
name|MyTestRPCServer
extends|extends
name|TestImpl
block|{
annotation|@
name|Override
DECL|method|ping ()
specifier|public
name|void
name|ping
parameter_list|()
block|{
comment|// test with ip set
name|testSuccessLogFormat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|testFailureLogFormat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test {@link NMAuditLogger} with IP set.    */
annotation|@
name|Test
DECL|method|testNMAuditLoggerWithIP ()
specifier|public
name|void
name|testNMAuditLoggerWithIP
parameter_list|()
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
comment|// start the IPC server
name|Server
name|server
init|=
name|RPC
operator|.
name|getServer
argument_list|(
name|TestProtocol
operator|.
name|class
argument_list|,
operator|new
name|MyTestRPCServer
argument_list|()
argument_list|,
literal|"0.0.0.0"
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|,
literal|true
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
decl_stmt|;
comment|// Make a client connection and test the audit log
name|TestProtocol
name|proxy
init|=
operator|(
name|TestProtocol
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|TestProtocol
operator|.
name|class
argument_list|,
name|TestProtocol
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// Start the testcase
name|proxy
operator|.
name|ping
argument_list|()
expr_stmt|;
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

