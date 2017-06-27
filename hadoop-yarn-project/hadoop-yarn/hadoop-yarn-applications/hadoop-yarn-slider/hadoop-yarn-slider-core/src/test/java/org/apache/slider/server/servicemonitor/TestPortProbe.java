begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.servicemonitor
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|servicemonitor
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|model
operator|.
name|mock
operator|.
name|MockFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|RoleInstance
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

begin_class
DECL|class|TestPortProbe
specifier|public
class|class
name|TestPortProbe
extends|extends
name|Assert
block|{
DECL|field|factory
specifier|private
specifier|final
name|MockFactory
name|factory
init|=
name|MockFactory
operator|.
name|INSTANCE
decl_stmt|;
comment|/**    * Assert that a port probe failed if the port is closed    * @throws Throwable    */
annotation|@
name|Test
DECL|method|testPortProbeFailsClosedPort ()
specifier|public
name|void
name|testPortProbeFailsClosedPort
parameter_list|()
throws|throws
name|Throwable
block|{
name|PortProbe
name|probe
init|=
operator|new
name|PortProbe
argument_list|(
literal|65500
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|probe
operator|.
name|init
argument_list|()
expr_stmt|;
name|RoleInstance
name|roleInstance
init|=
operator|new
name|RoleInstance
argument_list|(
name|factory
operator|.
name|newContainer
argument_list|()
argument_list|)
decl_stmt|;
name|roleInstance
operator|.
name|ip
operator|=
literal|"127.0.0.1"
expr_stmt|;
name|ProbeStatus
name|status
init|=
name|probe
operator|.
name|ping
argument_list|(
name|roleInstance
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Expected a failure but got successful result: "
operator|+
name|status
argument_list|,
name|status
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

