begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|InstanceAlreadyExistsException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|lib
operator|.
name|DefaultMetricsSystem
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
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * This util class provides a method to register an MBean using  * our standard naming convention as described in the doc  *  for {link {@link #register(String, String, Object)}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|MBeans
specifier|public
specifier|final
class|class
name|MBeans
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MBeans
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DOMAIN_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|DOMAIN_PREFIX
init|=
literal|"Hadoop:"
decl_stmt|;
DECL|field|SERVICE_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|SERVICE_PREFIX
init|=
literal|"service="
decl_stmt|;
DECL|field|NAME_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|NAME_PREFIX
init|=
literal|"name="
decl_stmt|;
DECL|field|MBEAN_NAME_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|MBEAN_NAME_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^"
operator|+
name|DOMAIN_PREFIX
operator|+
name|SERVICE_PREFIX
operator|+
literal|"([^,]+),"
operator|+
name|NAME_PREFIX
operator|+
literal|"(.+)$"
argument_list|)
decl_stmt|;
DECL|method|MBeans ()
specifier|private
name|MBeans
parameter_list|()
block|{   }
comment|/**    * Register the MBean using our standard MBeanName format    * "hadoop:service={@literal<serviceName>,name=<nameName>}"    * Where the {@literal<serviceName> and<nameName>} are the supplied    * parameters.    *    * @param serviceName    * @param nameName    * @param theMbean - the MBean to register    * @return the named used to register the MBean    */
DECL|method|register (String serviceName, String nameName, Object theMbean)
specifier|static
specifier|public
name|ObjectName
name|register
parameter_list|(
name|String
name|serviceName
parameter_list|,
name|String
name|nameName
parameter_list|,
name|Object
name|theMbean
parameter_list|)
block|{
return|return
name|register
argument_list|(
name|serviceName
argument_list|,
name|nameName
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|,
name|theMbean
argument_list|)
return|;
block|}
comment|/**    * Register the MBean using our standard MBeanName format    * "hadoop:service={@literal<serviceName>,name=<nameName>}"    * Where the {@literal<serviceName> and<nameName>} are the supplied    * parameters.    *    * @param serviceName    * @param nameName    * @param properties - Key value pairs to define additional JMX ObjectName    *                     properties.    * @param theMbean    - the MBean to register    * @return the named used to register the MBean    */
DECL|method|register (String serviceName, String nameName, Map<String, String> properties, Object theMbean)
specifier|static
specifier|public
name|ObjectName
name|register
parameter_list|(
name|String
name|serviceName
parameter_list|,
name|String
name|nameName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|,
name|Object
name|theMbean
parameter_list|)
block|{
specifier|final
name|MBeanServer
name|mbs
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|properties
argument_list|,
literal|"JMX bean properties should not be null for "
operator|+
literal|"bean registration."
argument_list|)
expr_stmt|;
name|ObjectName
name|name
init|=
name|getMBeanName
argument_list|(
name|serviceName
argument_list|,
name|nameName
argument_list|,
name|properties
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|mbs
operator|.
name|registerMBean
argument_list|(
name|theMbean
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Registered "
operator|+
name|name
argument_list|)
expr_stmt|;
return|return
name|name
return|;
block|}
catch|catch
parameter_list|(
name|InstanceAlreadyExistsException
name|iaee
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Failed to register MBean \""
operator|+
name|name
operator|+
literal|"\""
argument_list|,
name|iaee
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to register MBean \""
operator|+
name|name
operator|+
literal|"\": Instance already exists."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to register MBean \""
operator|+
name|name
operator|+
literal|"\""
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getMbeanNameService (final ObjectName objectName)
specifier|public
specifier|static
name|String
name|getMbeanNameService
parameter_list|(
specifier|final
name|ObjectName
name|objectName
parameter_list|)
block|{
name|Matcher
name|matcher
init|=
name|MBEAN_NAME_PATTERN
operator|.
name|matcher
argument_list|(
name|objectName
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|objectName
operator|+
literal|" is not a valid Hadoop mbean"
argument_list|)
throw|;
block|}
block|}
DECL|method|getMbeanNameName (final ObjectName objectName)
specifier|public
specifier|static
name|String
name|getMbeanNameName
parameter_list|(
specifier|final
name|ObjectName
name|objectName
parameter_list|)
block|{
name|Matcher
name|matcher
init|=
name|MBEAN_NAME_PATTERN
operator|.
name|matcher
argument_list|(
name|objectName
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|objectName
operator|+
literal|" is not a valid Hadoop mbean"
argument_list|)
throw|;
block|}
block|}
DECL|method|unregister (ObjectName mbeanName)
specifier|static
specifier|public
name|void
name|unregister
parameter_list|(
name|ObjectName
name|mbeanName
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Unregistering "
operator|+
name|mbeanName
argument_list|)
expr_stmt|;
specifier|final
name|MBeanServer
name|mbs
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
if|if
condition|(
name|mbeanName
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Stacktrace: "
argument_list|,
operator|new
name|Throwable
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|mbs
operator|.
name|unregisterMBean
argument_list|(
name|mbeanName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error unregistering "
operator|+
name|mbeanName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|DefaultMetricsSystem
operator|.
name|removeMBeanName
argument_list|(
name|mbeanName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getMBeanName (String serviceName, String nameName, Map<String, String> additionalParameters)
specifier|static
name|ObjectName
name|getMBeanName
parameter_list|(
name|String
name|serviceName
parameter_list|,
name|String
name|nameName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|additionalParameters
parameter_list|)
block|{
name|String
name|additionalKeys
init|=
name|additionalParameters
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|entry
lambda|->
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"="
operator|+
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|","
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|nameStr
init|=
name|DOMAIN_PREFIX
operator|+
name|SERVICE_PREFIX
operator|+
name|serviceName
operator|+
literal|","
operator|+
name|NAME_PREFIX
operator|+
name|nameName
operator|+
operator|(
name|additionalKeys
operator|.
name|isEmpty
argument_list|()
condition|?
literal|""
else|:
literal|","
operator|+
name|additionalKeys
operator|)
decl_stmt|;
try|try
block|{
return|return
name|DefaultMetricsSystem
operator|.
name|newMBeanName
argument_list|(
name|nameStr
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error creating MBean object name: "
operator|+
name|nameStr
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

