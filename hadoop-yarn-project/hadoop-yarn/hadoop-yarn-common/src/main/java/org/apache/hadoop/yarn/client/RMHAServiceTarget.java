begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client
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
name|ha
operator|.
name|BadFencingConfigurationException
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
name|ha
operator|.
name|HAServiceTarget
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
name|ha
operator|.
name|NodeFencer
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_class
DECL|class|RMHAServiceTarget
specifier|public
class|class
name|RMHAServiceTarget
extends|extends
name|HAServiceTarget
block|{
DECL|field|haAdminServiceAddress
specifier|private
name|InetSocketAddress
name|haAdminServiceAddress
decl_stmt|;
DECL|method|RMHAServiceTarget (YarnConfiguration conf)
specifier|public
name|RMHAServiceTarget
parameter_list|(
name|YarnConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|haAdminServiceAddress
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ADMIN_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_HA_ADMIN_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_HA_ADMIN_PORT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAddress ()
specifier|public
name|InetSocketAddress
name|getAddress
parameter_list|()
block|{
return|return
name|haAdminServiceAddress
return|;
block|}
annotation|@
name|Override
DECL|method|getZKFCAddress ()
specifier|public
name|InetSocketAddress
name|getZKFCAddress
parameter_list|()
block|{
comment|// TODO (YARN-1177): Hook up ZKFC information
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getFencer ()
specifier|public
name|NodeFencer
name|getFencer
parameter_list|()
block|{
comment|// TODO (YARN-1026): Hook up fencing implementation
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|checkFencingConfigured ()
specifier|public
name|void
name|checkFencingConfigured
parameter_list|()
throws|throws
name|BadFencingConfigurationException
block|{
comment|// TODO (YARN-1026): Based on fencing implementation
block|}
block|}
end_class

end_unit

