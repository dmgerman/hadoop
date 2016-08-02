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
name|IOException
import|;
end_import

begin_comment
comment|/**  * Base class of all probes.  */
end_comment

begin_class
DECL|class|Probe
specifier|public
specifier|abstract
class|class
name|Probe
implements|implements
name|MonitorKeys
block|{
DECL|field|conf
specifier|protected
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
comment|// =======================================================
comment|/*    * These fields are all used by the probe loops    * to maintain state. Please Leave them alone.    */
DECL|field|successCount
specifier|public
name|int
name|successCount
decl_stmt|;
DECL|field|failureCount
specifier|public
name|int
name|failureCount
decl_stmt|;
DECL|field|bootstrapStarted
specifier|public
name|long
name|bootstrapStarted
decl_stmt|;
DECL|field|bootstrapFinished
specifier|public
name|long
name|bootstrapFinished
decl_stmt|;
DECL|field|booted
specifier|private
name|boolean
name|booted
init|=
literal|false
decl_stmt|;
comment|// =======================================================
comment|/**    * Create a probe of a specific name    *    * @param name probe name    * @param conf configuration being stored.    */
DECL|method|Probe (String name, Configuration conf)
specifier|public
name|Probe
parameter_list|(
name|String
name|name
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
DECL|method|setName (String name)
specifier|protected
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getName
argument_list|()
operator|+
literal|" {"
operator|+
literal|"successCount="
operator|+
name|successCount
operator|+
literal|", failureCount="
operator|+
name|failureCount
operator|+
literal|'}'
return|;
block|}
comment|/**    * perform any prelaunch initialization    */
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{    }
comment|/**    * Ping the endpoint. All exceptions must be caught and included in the    * (failure) status.    *    * @param livePing is the ping live: true for live; false for boot time    * @return the status    */
DECL|method|ping (boolean livePing)
specifier|public
specifier|abstract
name|ProbeStatus
name|ping
parameter_list|(
name|boolean
name|livePing
parameter_list|)
function_decl|;
DECL|method|beginBootstrap ()
specifier|public
name|void
name|beginBootstrap
parameter_list|()
block|{
name|bootstrapStarted
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
DECL|method|endBootstrap ()
specifier|public
name|void
name|endBootstrap
parameter_list|()
block|{
name|setBooted
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bootstrapFinished
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
DECL|method|isBooted ()
specifier|public
name|boolean
name|isBooted
parameter_list|()
block|{
return|return
name|booted
return|;
block|}
DECL|method|setBooted (boolean booted)
specifier|public
name|void
name|setBooted
parameter_list|(
name|boolean
name|booted
parameter_list|)
block|{
name|this
operator|.
name|booted
operator|=
name|booted
expr_stmt|;
block|}
block|}
end_class

end_unit

