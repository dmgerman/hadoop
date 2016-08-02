begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.core.launch
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|launch
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
name|security
operator|.
name|Credentials
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
name|ApplicationSubmissionContext
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
name|Priority
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
name|Resource
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
name|client
operator|.
name|api
operator|.
name|YarnClientApplication
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
name|exceptions
operator|.
name|YarnException
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
name|util
operator|.
name|Records
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
name|client
operator|.
name|SliderYarnClientImpl
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
name|common
operator|.
name|tools
operator|.
name|CoreFileSystem
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|AppMasterLauncher
specifier|public
class|class
name|AppMasterLauncher
extends|extends
name|AbstractLauncher
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AppMasterLauncher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|application
specifier|public
specifier|final
name|YarnClientApplication
name|application
decl_stmt|;
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|type
specifier|public
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|submissionContext
specifier|public
specifier|final
name|ApplicationSubmissionContext
name|submissionContext
decl_stmt|;
DECL|field|appId
specifier|public
specifier|final
name|ApplicationId
name|appId
decl_stmt|;
DECL|field|secureCluster
specifier|public
specifier|final
name|boolean
name|secureCluster
decl_stmt|;
DECL|field|maxAppAttempts
specifier|private
name|int
name|maxAppAttempts
init|=
literal|0
decl_stmt|;
DECL|field|keepContainersOverRestarts
specifier|private
name|boolean
name|keepContainersOverRestarts
init|=
literal|true
decl_stmt|;
DECL|field|queue
specifier|private
name|String
name|queue
init|=
name|YarnConfiguration
operator|.
name|DEFAULT_QUEUE_NAME
decl_stmt|;
DECL|field|priority
specifier|private
name|int
name|priority
init|=
literal|1
decl_stmt|;
DECL|field|resource
specifier|private
specifier|final
name|Resource
name|resource
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|yarnClient
specifier|private
specifier|final
name|SliderYarnClientImpl
name|yarnClient
decl_stmt|;
DECL|field|submitTime
specifier|private
name|Long
name|submitTime
decl_stmt|;
comment|/**    * Build the AM Launcher    * @param name app name    * @param type application type    * @param conf hadoop config    * @param fs filesystem binding    * @param yarnClient yarn client    * @param secureCluster flag to indicate secure cluster    * @param options map of options. All values are extracted in this constructor only    * @param resourceGlobalOptions global options    * @param applicationTags any app tags    * @param credentials initial set of credentials    * @throws IOException    * @throws YarnException    */
DECL|method|AppMasterLauncher (String name, String type, Configuration conf, CoreFileSystem fs, SliderYarnClientImpl yarnClient, boolean secureCluster, Map<String, String> options, Map<String, String> resourceGlobalOptions, Set<String> applicationTags, Credentials credentials)
specifier|public
name|AppMasterLauncher
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|type
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|CoreFileSystem
name|fs
parameter_list|,
name|SliderYarnClientImpl
name|yarnClient
parameter_list|,
name|boolean
name|secureCluster
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|resourceGlobalOptions
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|applicationTags
parameter_list|,
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|fs
argument_list|,
name|credentials
argument_list|)
expr_stmt|;
name|this
operator|.
name|yarnClient
operator|=
name|yarnClient
expr_stmt|;
name|this
operator|.
name|application
operator|=
name|yarnClient
operator|.
name|createApplication
argument_list|()
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|secureCluster
operator|=
name|secureCluster
expr_stmt|;
name|submissionContext
operator|=
name|application
operator|.
name|getApplicationSubmissionContext
argument_list|()
expr_stmt|;
name|appId
operator|=
name|submissionContext
operator|.
name|getApplicationId
argument_list|()
expr_stmt|;
comment|// set the application name;
name|submissionContext
operator|.
name|setApplicationName
argument_list|(
name|name
argument_list|)
expr_stmt|;
comment|// app type used in service enum;
name|submissionContext
operator|.
name|setApplicationType
argument_list|(
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|applicationTags
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|submissionContext
operator|.
name|setApplicationTags
argument_list|(
name|applicationTags
argument_list|)
expr_stmt|;
block|}
name|submissionContext
operator|.
name|setNodeLabelExpression
argument_list|(
name|extractLabelExpression
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
name|extractAmRetryCount
argument_list|(
name|submissionContext
argument_list|,
name|resourceGlobalOptions
argument_list|)
expr_stmt|;
name|extractResourceRequirements
argument_list|(
name|resource
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|extractLogAggregationContext
argument_list|(
name|resourceGlobalOptions
argument_list|)
expr_stmt|;
block|}
DECL|method|setMaxAppAttempts (int maxAppAttempts)
specifier|public
name|void
name|setMaxAppAttempts
parameter_list|(
name|int
name|maxAppAttempts
parameter_list|)
block|{
name|this
operator|.
name|maxAppAttempts
operator|=
name|maxAppAttempts
expr_stmt|;
block|}
DECL|method|setKeepContainersOverRestarts (boolean keepContainersOverRestarts)
specifier|public
name|void
name|setKeepContainersOverRestarts
parameter_list|(
name|boolean
name|keepContainersOverRestarts
parameter_list|)
block|{
name|this
operator|.
name|keepContainersOverRestarts
operator|=
name|keepContainersOverRestarts
expr_stmt|;
block|}
DECL|method|getResource ()
specifier|public
name|Resource
name|getResource
parameter_list|()
block|{
return|return
name|resource
return|;
block|}
DECL|method|setMemory (int memory)
specifier|public
name|void
name|setMemory
parameter_list|(
name|int
name|memory
parameter_list|)
block|{
name|resource
operator|.
name|setMemory
argument_list|(
name|memory
argument_list|)
expr_stmt|;
block|}
DECL|method|setVirtualCores (int cores)
specifier|public
name|void
name|setVirtualCores
parameter_list|(
name|int
name|cores
parameter_list|)
block|{
name|resource
operator|.
name|setVirtualCores
argument_list|(
name|cores
argument_list|)
expr_stmt|;
block|}
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
return|return
name|appId
return|;
block|}
DECL|method|getMaxAppAttempts ()
specifier|public
name|int
name|getMaxAppAttempts
parameter_list|()
block|{
return|return
name|maxAppAttempts
return|;
block|}
DECL|method|isKeepContainersOverRestarts ()
specifier|public
name|boolean
name|isKeepContainersOverRestarts
parameter_list|()
block|{
return|return
name|keepContainersOverRestarts
return|;
block|}
DECL|method|getQueue ()
specifier|public
name|String
name|getQueue
parameter_list|()
block|{
return|return
name|queue
return|;
block|}
DECL|method|getPriority ()
specifier|public
name|int
name|getPriority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
DECL|method|setQueue (String queue)
specifier|public
name|void
name|setQueue
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
block|}
DECL|method|setPriority (int priority)
specifier|public
name|void
name|setPriority
parameter_list|(
name|int
name|priority
parameter_list|)
block|{
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
block|}
comment|/**    * Complete the launch context (copy in env vars, etc).    * @return the container to launch    */
DECL|method|completeAppMasterLaunch ()
specifier|public
name|ApplicationSubmissionContext
name|completeAppMasterLaunch
parameter_list|()
throws|throws
name|IOException
block|{
comment|//queue priority
name|Priority
name|pri
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|Priority
operator|.
name|class
argument_list|)
decl_stmt|;
name|pri
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|submissionContext
operator|.
name|setPriority
argument_list|(
name|pri
argument_list|)
expr_stmt|;
comment|// Set the queue to which this application is to be submitted in the RM
comment|// Queue for App master
name|submissionContext
operator|.
name|setQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
comment|//container requirements
name|submissionContext
operator|.
name|setResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|submissionContext
operator|.
name|setLogAggregationContext
argument_list|(
name|logAggregationContext
argument_list|)
expr_stmt|;
if|if
condition|(
name|keepContainersOverRestarts
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Requesting cluster stays running over AM failure"
argument_list|)
expr_stmt|;
name|submissionContext
operator|.
name|setKeepContainersAcrossApplicationAttempts
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxAppAttempts
operator|>
literal|0
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Setting max AM attempts to {}"
argument_list|,
name|maxAppAttempts
argument_list|)
expr_stmt|;
name|submissionContext
operator|.
name|setMaxAppAttempts
argument_list|(
name|maxAppAttempts
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|secureCluster
condition|)
block|{
comment|//tokens
name|log
operator|.
name|debug
argument_list|(
literal|"Credentials: {}"
argument_list|,
name|CredentialUtils
operator|.
name|dumpTokens
argument_list|(
name|getCredentials
argument_list|()
argument_list|,
literal|"\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|propagateUsernameInInsecureCluster
argument_list|()
expr_stmt|;
block|}
name|completeContainerLaunch
argument_list|()
expr_stmt|;
name|submissionContext
operator|.
name|setAMContainerSpec
argument_list|(
name|containerLaunchContext
argument_list|)
expr_stmt|;
return|return
name|submissionContext
return|;
block|}
comment|/**    * Submit the application.     * @return a launched application representing the submitted application    * @throws IOException    * @throws YarnException    */
DECL|method|submitApplication ()
specifier|public
name|LaunchedApplication
name|submitApplication
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|completeAppMasterLaunch
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Submitting application to Resource Manager"
argument_list|)
expr_stmt|;
name|ApplicationId
name|applicationId
init|=
name|yarnClient
operator|.
name|submitApplication
argument_list|(
name|submissionContext
argument_list|)
decl_stmt|;
comment|// implicit success; record the time
name|submitTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
return|return
operator|new
name|LaunchedApplication
argument_list|(
name|applicationId
argument_list|,
name|yarnClient
argument_list|)
return|;
block|}
comment|/**    * Build a serializable application report. This is a very minimal    * report that contains the application Id, name and type âthe information    * available    * @return a data structure which can be persisted    */
DECL|method|createSerializedApplicationReport ()
specifier|public
name|SerializedApplicationReport
name|createSerializedApplicationReport
parameter_list|()
block|{
name|SerializedApplicationReport
name|sar
init|=
operator|new
name|SerializedApplicationReport
argument_list|()
decl_stmt|;
name|sar
operator|.
name|applicationId
operator|=
name|appId
operator|.
name|toString
argument_list|()
expr_stmt|;
name|sar
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|sar
operator|.
name|applicationType
operator|=
name|type
expr_stmt|;
name|sar
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|sar
operator|.
name|submitTime
operator|=
name|submitTime
expr_stmt|;
return|return
name|sar
return|;
block|}
block|}
end_class

end_unit

