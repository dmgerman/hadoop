begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.server
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
operator|.
name|server
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|Rule
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
name|rules
operator|.
name|Timeout
import|;
end_import

begin_comment
comment|/**  * Test class for {@link SCMSecurityProtocolServer}.  * */
end_comment

begin_class
DECL|class|TestSCMSecurityProtocolServer
specifier|public
class|class
name|TestSCMSecurityProtocolServer
block|{
DECL|field|securityProtocolServer
specifier|private
name|SCMSecurityProtocolServer
name|securityProtocolServer
decl_stmt|;
DECL|field|config
specifier|private
name|OzoneConfiguration
name|config
decl_stmt|;
annotation|@
name|Rule
DECL|field|timeout
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|1000
operator|*
literal|20
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
name|config
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|securityProtocolServer
operator|=
operator|new
name|SCMSecurityProtocolServer
argument_list|(
name|config
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|securityProtocolServer
operator|!=
literal|null
condition|)
block|{
name|securityProtocolServer
operator|.
name|stop
argument_list|()
expr_stmt|;
name|securityProtocolServer
operator|=
literal|null
expr_stmt|;
block|}
name|config
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStart ()
specifier|public
name|void
name|testStart
parameter_list|()
block|{
name|securityProtocolServer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStop ()
specifier|public
name|void
name|testStop
parameter_list|()
block|{
name|securityProtocolServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

