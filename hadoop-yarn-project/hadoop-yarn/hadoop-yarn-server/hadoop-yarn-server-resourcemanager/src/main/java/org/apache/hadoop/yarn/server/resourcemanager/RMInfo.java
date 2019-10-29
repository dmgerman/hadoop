begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
name|resourcemanager
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|metrics2
operator|.
name|util
operator|.
name|MBeans
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
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|NotCompliantMBeanException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|StandardMBean
import|;
end_import

begin_comment
comment|/**  * JMX bean for RM info.  */
end_comment

begin_class
DECL|class|RMInfo
specifier|public
class|class
name|RMInfo
implements|implements
name|RMInfoMXBean
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RMNMInfo
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|resourceManager
specifier|private
name|ResourceManager
name|resourceManager
decl_stmt|;
DECL|field|rmStatusBeanName
specifier|private
name|ObjectName
name|rmStatusBeanName
decl_stmt|;
comment|/**    * Constructor for RMInfo registers the bean with JMX.    *    * @param resourceManager resource manager's context object    */
DECL|method|RMInfo (ResourceManager resourceManager)
name|RMInfo
parameter_list|(
name|ResourceManager
name|resourceManager
parameter_list|)
block|{
name|this
operator|.
name|resourceManager
operator|=
name|resourceManager
expr_stmt|;
block|}
DECL|method|register ()
specifier|public
name|void
name|register
parameter_list|()
block|{
name|StandardMBean
name|bean
decl_stmt|;
try|try
block|{
name|bean
operator|=
operator|new
name|StandardMBean
argument_list|(
name|this
argument_list|,
name|RMInfoMXBean
operator|.
name|class
argument_list|)
expr_stmt|;
name|rmStatusBeanName
operator|=
name|MBeans
operator|.
name|register
argument_list|(
literal|"ResourceManager"
argument_list|,
literal|"RMInfo"
argument_list|,
name|bean
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotCompliantMBeanException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error registering RMInfo MBean"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Registered RMInfo MBean"
argument_list|)
expr_stmt|;
block|}
DECL|method|unregister ()
specifier|public
name|void
name|unregister
parameter_list|()
block|{
if|if
condition|(
name|rmStatusBeanName
operator|!=
literal|null
condition|)
block|{
name|MBeans
operator|.
name|unregister
argument_list|(
name|rmStatusBeanName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getState ()
annotation|@
name|Override
specifier|public
name|String
name|getState
parameter_list|()
block|{
return|return
name|this
operator|.
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getHAServiceState
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getHostAndPort ()
annotation|@
name|Override
specifier|public
name|String
name|getHostAndPort
parameter_list|()
block|{
return|return
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|ResourceManager
operator|.
name|getBindAddress
argument_list|(
name|this
operator|.
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getYarnConfiguration
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|isSecurityEnabled ()
annotation|@
name|Override
specifier|public
name|boolean
name|isSecurityEnabled
parameter_list|()
block|{
return|return
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
return|;
block|}
block|}
end_class

end_unit

