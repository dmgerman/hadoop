begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.cli
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
operator|.
name|cli
package|;
end_package

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
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|Client
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|ClientResponse
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|WebResource
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
name|cli
operator|.
name|CommandLine
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
name|cli
operator|.
name|GnuParser
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
name|cli
operator|.
name|MissingArgumentException
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
name|cli
operator|.
name|Options
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
operator|.
name|Public
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
operator|.
name|Evolving
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
name|Configured
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
name|util
operator|.
name|Tool
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
name|webapp
operator|.
name|dao
operator|.
name|QueueConfigInfo
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
name|webapp
operator|.
name|dao
operator|.
name|SchedConfUpdateInfo
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
name|webapp
operator|.
name|util
operator|.
name|WebAppUtils
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
name|webapp
operator|.
name|util
operator|.
name|YarnWebServiceUtils
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
operator|.
name|Status
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_comment
comment|/**  * CLI for modifying scheduler configuration.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|SchedConfCLI
specifier|public
class|class
name|SchedConfCLI
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|ADD_QUEUES_OPTION
specifier|private
specifier|static
specifier|final
name|String
name|ADD_QUEUES_OPTION
init|=
literal|"addQueues"
decl_stmt|;
DECL|field|REMOVE_QUEUES_OPTION
specifier|private
specifier|static
specifier|final
name|String
name|REMOVE_QUEUES_OPTION
init|=
literal|"removeQueues"
decl_stmt|;
DECL|field|UPDATE_QUEUES_OPTION
specifier|private
specifier|static
specifier|final
name|String
name|UPDATE_QUEUES_OPTION
init|=
literal|"updateQueues"
decl_stmt|;
DECL|field|GLOBAL_OPTIONS
specifier|private
specifier|static
specifier|final
name|String
name|GLOBAL_OPTIONS
init|=
literal|"globalUpdates"
decl_stmt|;
DECL|field|HELP_CMD
specifier|private
specifier|static
specifier|final
name|String
name|HELP_CMD
init|=
literal|"help"
decl_stmt|;
DECL|field|CONF_ERR_MSG
specifier|private
specifier|static
specifier|final
name|String
name|CONF_ERR_MSG
init|=
literal|"Specify configuration key "
operator|+
literal|"value as confKey=confVal."
decl_stmt|;
DECL|method|SchedConfCLI ()
specifier|public
name|SchedConfCLI
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|SchedConfCLI
name|cli
init|=
operator|new
name|SchedConfCLI
argument_list|()
decl_stmt|;
name|int
name|exitCode
init|=
name|cli
operator|.
name|run
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|exitCode
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Options
name|opts
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
literal|"add"
argument_list|,
name|ADD_QUEUES_OPTION
argument_list|,
literal|true
argument_list|,
literal|"Add queues with configurations"
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
literal|"remove"
argument_list|,
name|REMOVE_QUEUES_OPTION
argument_list|,
literal|true
argument_list|,
literal|"Remove queues"
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
literal|"update"
argument_list|,
name|UPDATE_QUEUES_OPTION
argument_list|,
literal|true
argument_list|,
literal|"Update queue configurations"
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
literal|"global"
argument_list|,
name|GLOBAL_OPTIONS
argument_list|,
literal|true
argument_list|,
literal|"Update global scheduler configurations"
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
literal|"h"
argument_list|,
name|HELP_CMD
argument_list|,
literal|false
argument_list|,
literal|"Displays help for all commands."
argument_list|)
expr_stmt|;
name|int
name|exitCode
init|=
operator|-
literal|1
decl_stmt|;
name|CommandLine
name|parsedCli
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parsedCli
operator|=
operator|new
name|GnuParser
argument_list|()
operator|.
name|parse
argument_list|(
name|opts
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MissingArgumentException
name|ex
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Missing argument for options"
argument_list|)
expr_stmt|;
name|printUsage
argument_list|()
expr_stmt|;
return|return
name|exitCode
return|;
block|}
if|if
condition|(
name|parsedCli
operator|.
name|hasOption
argument_list|(
name|HELP_CMD
argument_list|)
condition|)
block|{
name|printUsage
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
name|boolean
name|hasOption
init|=
literal|false
decl_stmt|;
name|SchedConfUpdateInfo
name|updateInfo
init|=
operator|new
name|SchedConfUpdateInfo
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|parsedCli
operator|.
name|hasOption
argument_list|(
name|ADD_QUEUES_OPTION
argument_list|)
condition|)
block|{
name|hasOption
operator|=
literal|true
expr_stmt|;
name|addQueues
argument_list|(
name|parsedCli
operator|.
name|getOptionValue
argument_list|(
name|ADD_QUEUES_OPTION
argument_list|)
argument_list|,
name|updateInfo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parsedCli
operator|.
name|hasOption
argument_list|(
name|REMOVE_QUEUES_OPTION
argument_list|)
condition|)
block|{
name|hasOption
operator|=
literal|true
expr_stmt|;
name|removeQueues
argument_list|(
name|parsedCli
operator|.
name|getOptionValue
argument_list|(
name|REMOVE_QUEUES_OPTION
argument_list|)
argument_list|,
name|updateInfo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parsedCli
operator|.
name|hasOption
argument_list|(
name|UPDATE_QUEUES_OPTION
argument_list|)
condition|)
block|{
name|hasOption
operator|=
literal|true
expr_stmt|;
name|updateQueues
argument_list|(
name|parsedCli
operator|.
name|getOptionValue
argument_list|(
name|UPDATE_QUEUES_OPTION
argument_list|)
argument_list|,
name|updateInfo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parsedCli
operator|.
name|hasOption
argument_list|(
name|GLOBAL_OPTIONS
argument_list|)
condition|)
block|{
name|hasOption
operator|=
literal|true
expr_stmt|;
name|globalUpdates
argument_list|(
name|parsedCli
operator|.
name|getOptionValue
argument_list|(
name|GLOBAL_OPTIONS
argument_list|)
argument_list|,
name|updateInfo
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
operator|!
name|hasOption
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Invalid Command Usage: "
argument_list|)
expr_stmt|;
name|printUsage
argument_list|()
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|Client
name|webServiceClient
init|=
name|Client
operator|.
name|create
argument_list|()
decl_stmt|;
name|WebResource
name|webResource
init|=
name|webServiceClient
operator|.
name|resource
argument_list|(
name|WebAppUtils
operator|.
name|getRMWebAppURLWithScheme
argument_list|(
name|getConf
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ClientResponse
name|response
init|=
name|webResource
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"sched-conf"
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|entity
argument_list|(
name|YarnWebServiceUtils
operator|.
name|toJson
argument_list|(
name|updateInfo
argument_list|,
name|SchedConfUpdateInfo
operator|.
name|class
argument_list|)
argument_list|,
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|put
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|response
operator|.
name|getStatus
argument_list|()
operator|==
name|Status
operator|.
name|OK
operator|.
name|getStatusCode
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Configuration changed successfully."
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Configuration change unsuccessful: "
operator|+
name|response
operator|.
name|getEntity
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Configuration change unsuccessful: null response"
argument_list|)
expr_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|addQueues (String args, SchedConfUpdateInfo updateInfo)
name|void
name|addQueues
parameter_list|(
name|String
name|args
parameter_list|,
name|SchedConfUpdateInfo
name|updateInfo
parameter_list|)
block|{
if|if
condition|(
name|args
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|ArrayList
argument_list|<
name|QueueConfigInfo
argument_list|>
name|queueConfigInfos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|arg
range|:
name|args
operator|.
name|split
argument_list|(
literal|";"
argument_list|)
control|)
block|{
name|queueConfigInfos
operator|.
name|add
argument_list|(
name|getQueueConfigInfo
argument_list|(
name|arg
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|updateInfo
operator|.
name|setAddQueueInfo
argument_list|(
name|queueConfigInfos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|removeQueues (String args, SchedConfUpdateInfo updateInfo)
name|void
name|removeQueues
parameter_list|(
name|String
name|args
parameter_list|,
name|SchedConfUpdateInfo
name|updateInfo
parameter_list|)
block|{
if|if
condition|(
name|args
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|queuesToRemove
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|args
operator|.
name|split
argument_list|(
literal|","
argument_list|)
argument_list|)
decl_stmt|;
name|updateInfo
operator|.
name|setRemoveQueueInfo
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|queuesToRemove
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|updateQueues (String args, SchedConfUpdateInfo updateInfo)
name|void
name|updateQueues
parameter_list|(
name|String
name|args
parameter_list|,
name|SchedConfUpdateInfo
name|updateInfo
parameter_list|)
block|{
if|if
condition|(
name|args
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|ArrayList
argument_list|<
name|QueueConfigInfo
argument_list|>
name|queueConfigInfos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|arg
range|:
name|args
operator|.
name|split
argument_list|(
literal|";"
argument_list|)
control|)
block|{
name|queueConfigInfos
operator|.
name|add
argument_list|(
name|getQueueConfigInfo
argument_list|(
name|arg
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|updateInfo
operator|.
name|setUpdateQueueInfo
argument_list|(
name|queueConfigInfos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|globalUpdates (String args, SchedConfUpdateInfo updateInfo)
name|void
name|globalUpdates
parameter_list|(
name|String
name|args
parameter_list|,
name|SchedConfUpdateInfo
name|updateInfo
parameter_list|)
block|{
if|if
condition|(
name|args
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|globalUpdates
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|globalUpdate
range|:
name|args
operator|.
name|split
argument_list|(
literal|","
argument_list|)
control|)
block|{
name|putKeyValuePair
argument_list|(
name|globalUpdates
argument_list|,
name|globalUpdate
argument_list|)
expr_stmt|;
block|}
name|updateInfo
operator|.
name|setGlobalParams
argument_list|(
name|globalUpdates
argument_list|)
expr_stmt|;
block|}
DECL|method|getQueueConfigInfo (String arg)
specifier|private
name|QueueConfigInfo
name|getQueueConfigInfo
parameter_list|(
name|String
name|arg
parameter_list|)
block|{
name|String
index|[]
name|queueArgs
init|=
name|arg
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|String
name|queuePath
init|=
name|queueArgs
index|[
literal|0
index|]
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|queueConfigs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|queueArgs
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|putKeyValuePair
argument_list|(
name|queueConfigs
argument_list|,
name|queueArgs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|QueueConfigInfo
argument_list|(
name|queuePath
argument_list|,
name|queueConfigs
argument_list|)
return|;
block|}
DECL|method|putKeyValuePair (Map<String, String> kv, String args)
specifier|private
name|void
name|putKeyValuePair
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|kv
parameter_list|,
name|String
name|args
parameter_list|)
block|{
name|String
index|[]
name|argParts
init|=
name|args
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
if|if
condition|(
name|argParts
operator|.
name|length
operator|==
literal|1
condition|)
block|{
if|if
condition|(
name|argParts
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|args
operator|.
name|contains
argument_list|(
literal|"="
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|CONF_ERR_MSG
argument_list|)
throw|;
block|}
else|else
block|{
comment|// key specified, but no value e.g. "confKey="
name|kv
operator|.
name|put
argument_list|(
name|argParts
index|[
literal|0
index|]
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|argParts
operator|.
name|length
operator|>
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|CONF_ERR_MSG
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
name|argParts
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|CONF_ERR_MSG
argument_list|)
throw|;
block|}
name|kv
operator|.
name|put
argument_list|(
name|argParts
index|[
literal|0
index|]
argument_list|,
name|argParts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|printUsage ()
specifier|private
name|void
name|printUsage
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"yarn schedconf [-add queueAddPath1,confKey1=confVal1,"
operator|+
literal|"confKey2=confVal2;queueAddPath2,confKey3=confVal3] "
operator|+
literal|"[-remove queueRemovePath1,queueRemovePath2] "
operator|+
literal|"[-update queueUpdatePath1,confKey1=confVal1] "
operator|+
literal|"[-global globalConfKey1=globalConfVal1,"
operator|+
literal|"globalConfKey2=globalConfVal2]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

