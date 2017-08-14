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
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|yarn
operator|.
name|service
operator|.
name|compinstance
operator|.
name|ComponentInstance
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
name|util
operator|.
name|Map
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
return|;
block|}
DECL|method|getProperty (Map<String, String> props, String name, String defaultValue)
specifier|public
specifier|static
name|String
name|getProperty
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|defaultValue
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|value
init|=
name|props
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|value
argument_list|)
condition|)
block|{
if|if
condition|(
name|defaultValue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|name
operator|+
literal|" not specified"
argument_list|)
throw|;
block|}
return|return
name|defaultValue
return|;
block|}
return|return
name|value
return|;
block|}
DECL|method|getPropertyInt (Map<String, String> props, String name, Integer defaultValue)
specifier|public
specifier|static
name|int
name|getPropertyInt
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
parameter_list|,
name|String
name|name
parameter_list|,
name|Integer
name|defaultValue
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|value
init|=
name|props
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|value
argument_list|)
condition|)
block|{
if|if
condition|(
name|defaultValue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|name
operator|+
literal|" not specified"
argument_list|)
throw|;
block|}
return|return
name|defaultValue
return|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
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
comment|/**    * Ping the endpoint. All exceptions must be caught and included in the    * (failure) status.    *    * @param instance instance to ping    * @return the status    */
DECL|method|ping (ComponentInstance instance)
specifier|public
specifier|abstract
name|ProbeStatus
name|ping
parameter_list|(
name|ComponentInstance
name|instance
parameter_list|)
function_decl|;
block|}
end_class

end_unit

