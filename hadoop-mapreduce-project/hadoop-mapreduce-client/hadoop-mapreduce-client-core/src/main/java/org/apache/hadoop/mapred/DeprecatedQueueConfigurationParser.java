begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|MRConfig
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
name|mapreduce
operator|.
name|QueueState
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
name|authorize
operator|.
name|AccessControlList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|QueueManager
operator|.
name|*
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
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * Class to build queue hierarchy using deprecated conf(mapred-site.xml).  * Generates a single level of queue hierarchy.   *   */
end_comment

begin_class
DECL|class|DeprecatedQueueConfigurationParser
class|class
name|DeprecatedQueueConfigurationParser
extends|extends
name|QueueConfigurationParser
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
name|DeprecatedQueueConfigurationParser
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MAPRED_QUEUE_NAMES_KEY
specifier|static
specifier|final
name|String
name|MAPRED_QUEUE_NAMES_KEY
init|=
literal|"mapred.queue.names"
decl_stmt|;
DECL|method|DeprecatedQueueConfigurationParser (Configuration conf)
name|DeprecatedQueueConfigurationParser
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|//If not configuration done return immediately.
if|if
condition|(
operator|!
name|deprecatedConf
argument_list|(
name|conf
argument_list|)
condition|)
block|{
return|return;
block|}
name|List
argument_list|<
name|Queue
argument_list|>
name|listq
init|=
name|createQueues
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|this
operator|.
name|setAclsEnabled
argument_list|(
name|conf
operator|.
name|getBoolean
argument_list|(
name|MRConfig
operator|.
name|MR_ACLS_ENABLED
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|=
operator|new
name|Queue
argument_list|()
expr_stmt|;
name|root
operator|.
name|setName
argument_list|(
literal|""
argument_list|)
expr_stmt|;
for|for
control|(
name|Queue
name|q
range|:
name|listq
control|)
block|{
name|root
operator|.
name|addChild
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createQueues (Configuration conf)
specifier|private
name|List
argument_list|<
name|Queue
argument_list|>
name|createQueues
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
index|[]
name|queueNameValues
init|=
name|conf
operator|.
name|getStrings
argument_list|(
name|MAPRED_QUEUE_NAMES_KEY
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Queue
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Queue
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|queueNameValues
control|)
block|{
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|AccessControlList
argument_list|>
name|acls
init|=
name|getQueueAcls
argument_list|(
name|name
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|QueueState
name|state
init|=
name|getQueueState
argument_list|(
name|name
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Queue
name|q
init|=
operator|new
name|Queue
argument_list|(
name|name
argument_list|,
name|acls
argument_list|,
name|state
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Not able to initialize queue "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
return|;
block|}
comment|/**    * Only applicable to leaf level queues    * Parse ACLs for the queue from the configuration.    */
DECL|method|getQueueState (String name, Configuration conf)
specifier|private
name|QueueState
name|getQueueState
parameter_list|(
name|String
name|name
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|stateVal
init|=
name|conf
operator|.
name|get
argument_list|(
name|toFullPropertyName
argument_list|(
name|name
argument_list|,
literal|"state"
argument_list|)
argument_list|,
name|QueueState
operator|.
name|RUNNING
operator|.
name|getStateName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|QueueState
operator|.
name|getState
argument_list|(
name|stateVal
argument_list|)
return|;
block|}
comment|/**    * Check if queue properties are configured in the passed in    * configuration. If yes, print out deprecation warning messages.    */
DECL|method|deprecatedConf (Configuration conf)
specifier|private
name|boolean
name|deprecatedConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
index|[]
name|queues
init|=
literal|null
decl_stmt|;
name|String
name|queueNameValues
init|=
name|getQueueNames
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|queueNameValues
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Configuring \""
operator|+
name|MAPRED_QUEUE_NAMES_KEY
operator|+
literal|"\" in mapred-site.xml or "
operator|+
literal|"hadoop-site.xml is deprecated and will overshadow "
operator|+
name|QUEUE_CONF_FILE_NAME
operator|+
literal|". Remove this property and configure "
operator|+
literal|"queue hierarchy in "
operator|+
name|QUEUE_CONF_FILE_NAME
argument_list|)
expr_stmt|;
comment|// store queues so we can check if ACLs are also configured
comment|// in the deprecated files.
name|queues
operator|=
name|conf
operator|.
name|getStrings
argument_list|(
name|MAPRED_QUEUE_NAMES_KEY
argument_list|)
expr_stmt|;
block|}
comment|// check if acls are defined
if|if
condition|(
name|queues
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|queue
range|:
name|queues
control|)
block|{
for|for
control|(
name|QueueACL
name|qAcl
range|:
name|QueueACL
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|toFullPropertyName
argument_list|(
name|queue
argument_list|,
name|qAcl
operator|.
name|getAclName
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|aclString
init|=
name|conf
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|aclString
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Configuring queue ACLs in mapred-site.xml or "
operator|+
literal|"hadoop-site.xml is deprecated. Configure queue ACLs in "
operator|+
name|QUEUE_CONF_FILE_NAME
argument_list|)
expr_stmt|;
comment|// even if one string is configured, it is enough for printing
comment|// the warning. so we can return from here.
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|getQueueNames (Configuration conf)
specifier|private
name|String
name|getQueueNames
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|queueNameValues
init|=
name|conf
operator|.
name|get
argument_list|(
name|MAPRED_QUEUE_NAMES_KEY
argument_list|)
decl_stmt|;
return|return
name|queueNameValues
return|;
block|}
comment|/**    * Parse ACLs for the queue from the configuration.    */
DECL|method|getQueueAcls ( String name, Configuration conf)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|AccessControlList
argument_list|>
name|getQueueAcls
parameter_list|(
name|String
name|name
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|AccessControlList
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|AccessControlList
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|QueueACL
name|qAcl
range|:
name|QueueACL
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|aclKey
init|=
name|toFullPropertyName
argument_list|(
name|name
argument_list|,
name|qAcl
operator|.
name|getAclName
argument_list|()
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|aclKey
argument_list|,
operator|new
name|AccessControlList
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|aclKey
argument_list|,
literal|"*"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
block|}
end_class

end_unit

