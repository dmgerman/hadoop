begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.recovery
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
name|nodemanager
operator|.
name|recovery
package|;
end_package

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
name|fs
operator|.
name|Path
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
name|proto
operator|.
name|YarnProtos
operator|.
name|LocalResourceProto
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
name|proto
operator|.
name|YarnServerNodemanagerRecoveryProtos
operator|.
name|LocalizedResourceProto
import|;
end_import

begin_class
DECL|class|NMMemoryStateStoreService
specifier|public
class|class
name|NMMemoryStateStoreService
extends|extends
name|NMStateStoreService
block|{
DECL|field|trackerStates
specifier|private
name|Map
argument_list|<
name|TrackerKey
argument_list|,
name|TrackerState
argument_list|>
name|trackerStates
decl_stmt|;
DECL|method|NMMemoryStateStoreService ()
specifier|public
name|NMMemoryStateStoreService
parameter_list|()
block|{
name|super
argument_list|(
name|NMMemoryStateStoreService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|loadTrackerState (TrackerState ts)
specifier|private
name|LocalResourceTrackerState
name|loadTrackerState
parameter_list|(
name|TrackerState
name|ts
parameter_list|)
block|{
name|LocalResourceTrackerState
name|result
init|=
operator|new
name|LocalResourceTrackerState
argument_list|()
decl_stmt|;
name|result
operator|.
name|localizedResources
operator|.
name|addAll
argument_list|(
name|ts
operator|.
name|localizedResources
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Path
argument_list|,
name|LocalResourceProto
argument_list|>
name|entry
range|:
name|ts
operator|.
name|inProgressMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|result
operator|.
name|inProgressResources
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|getTrackerState (TrackerKey key)
specifier|private
name|TrackerState
name|getTrackerState
parameter_list|(
name|TrackerKey
name|key
parameter_list|)
block|{
name|TrackerState
name|ts
init|=
name|trackerStates
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|ts
operator|==
literal|null
condition|)
block|{
name|ts
operator|=
operator|new
name|TrackerState
argument_list|()
expr_stmt|;
name|trackerStates
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|ts
argument_list|)
expr_stmt|;
block|}
return|return
name|ts
return|;
block|}
annotation|@
name|Override
DECL|method|loadLocalizationState ()
specifier|public
specifier|synchronized
name|RecoveredLocalizationState
name|loadLocalizationState
parameter_list|()
block|{
name|RecoveredLocalizationState
name|result
init|=
operator|new
name|RecoveredLocalizationState
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|TrackerKey
argument_list|,
name|TrackerState
argument_list|>
name|e
range|:
name|trackerStates
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|TrackerKey
name|tk
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|TrackerState
name|ts
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// check what kind of tracker state we have and recover appropriately
comment|// public trackers have user == null
comment|// private trackers have a valid user but appId == null
comment|// app-specific trackers have a valid user and valid appId
if|if
condition|(
name|tk
operator|.
name|user
operator|==
literal|null
condition|)
block|{
name|result
operator|.
name|publicTrackerState
operator|=
name|loadTrackerState
argument_list|(
name|ts
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|RecoveredUserResources
name|rur
init|=
name|result
operator|.
name|userResources
operator|.
name|get
argument_list|(
name|tk
operator|.
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|rur
operator|==
literal|null
condition|)
block|{
name|rur
operator|=
operator|new
name|RecoveredUserResources
argument_list|()
expr_stmt|;
name|result
operator|.
name|userResources
operator|.
name|put
argument_list|(
name|tk
operator|.
name|user
argument_list|,
name|rur
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tk
operator|.
name|appId
operator|==
literal|null
condition|)
block|{
name|rur
operator|.
name|privateTrackerState
operator|=
name|loadTrackerState
argument_list|(
name|ts
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rur
operator|.
name|appTrackerStates
operator|.
name|put
argument_list|(
name|tk
operator|.
name|appId
argument_list|,
name|loadTrackerState
argument_list|(
name|ts
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|startResourceLocalization (String user, ApplicationId appId, LocalResourceProto proto, Path localPath)
specifier|public
specifier|synchronized
name|void
name|startResourceLocalization
parameter_list|(
name|String
name|user
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|LocalResourceProto
name|proto
parameter_list|,
name|Path
name|localPath
parameter_list|)
block|{
name|TrackerState
name|ts
init|=
name|getTrackerState
argument_list|(
operator|new
name|TrackerKey
argument_list|(
name|user
argument_list|,
name|appId
argument_list|)
argument_list|)
decl_stmt|;
name|ts
operator|.
name|inProgressMap
operator|.
name|put
argument_list|(
name|localPath
argument_list|,
name|proto
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishResourceLocalization (String user, ApplicationId appId, LocalizedResourceProto proto)
specifier|public
specifier|synchronized
name|void
name|finishResourceLocalization
parameter_list|(
name|String
name|user
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|LocalizedResourceProto
name|proto
parameter_list|)
block|{
name|TrackerState
name|ts
init|=
name|getTrackerState
argument_list|(
operator|new
name|TrackerKey
argument_list|(
name|user
argument_list|,
name|appId
argument_list|)
argument_list|)
decl_stmt|;
name|Path
name|localPath
init|=
operator|new
name|Path
argument_list|(
name|proto
operator|.
name|getLocalPath
argument_list|()
argument_list|)
decl_stmt|;
name|ts
operator|.
name|inProgressMap
operator|.
name|remove
argument_list|(
name|localPath
argument_list|)
expr_stmt|;
name|ts
operator|.
name|localizedResources
operator|.
name|put
argument_list|(
name|localPath
argument_list|,
name|proto
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeLocalizedResource (String user, ApplicationId appId, Path localPath)
specifier|public
specifier|synchronized
name|void
name|removeLocalizedResource
parameter_list|(
name|String
name|user
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|Path
name|localPath
parameter_list|)
block|{
name|TrackerState
name|ts
init|=
name|trackerStates
operator|.
name|get
argument_list|(
operator|new
name|TrackerKey
argument_list|(
name|user
argument_list|,
name|appId
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|ts
operator|!=
literal|null
condition|)
block|{
name|ts
operator|.
name|inProgressMap
operator|.
name|remove
argument_list|(
name|localPath
argument_list|)
expr_stmt|;
name|ts
operator|.
name|localizedResources
operator|.
name|remove
argument_list|(
name|localPath
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|initStorage (Configuration conf)
specifier|protected
name|void
name|initStorage
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|trackerStates
operator|=
operator|new
name|HashMap
argument_list|<
name|TrackerKey
argument_list|,
name|TrackerState
argument_list|>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startStorage ()
specifier|protected
name|void
name|startStorage
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|closeStorage ()
specifier|protected
name|void
name|closeStorage
parameter_list|()
block|{   }
DECL|class|TrackerState
specifier|private
specifier|static
class|class
name|TrackerState
block|{
DECL|field|inProgressMap
name|Map
argument_list|<
name|Path
argument_list|,
name|LocalResourceProto
argument_list|>
name|inProgressMap
init|=
operator|new
name|HashMap
argument_list|<
name|Path
argument_list|,
name|LocalResourceProto
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|localizedResources
name|Map
argument_list|<
name|Path
argument_list|,
name|LocalizedResourceProto
argument_list|>
name|localizedResources
init|=
operator|new
name|HashMap
argument_list|<
name|Path
argument_list|,
name|LocalizedResourceProto
argument_list|>
argument_list|()
decl_stmt|;
block|}
DECL|class|TrackerKey
specifier|private
specifier|static
class|class
name|TrackerKey
block|{
DECL|field|user
name|String
name|user
decl_stmt|;
DECL|field|appId
name|ApplicationId
name|appId
decl_stmt|;
DECL|method|TrackerKey (String user, ApplicationId appId)
specifier|public
name|TrackerKey
parameter_list|(
name|String
name|user
parameter_list|,
name|ApplicationId
name|appId
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|appId
operator|=
name|appId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|appId
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|appId
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|user
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|user
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|TrackerKey
operator|)
condition|)
return|return
literal|false
return|;
name|TrackerKey
name|other
init|=
operator|(
name|TrackerKey
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|appId
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|appId
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|appId
operator|.
name|equals
argument_list|(
name|other
operator|.
name|appId
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|user
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|user
operator|.
name|equals
argument_list|(
name|other
operator|.
name|user
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

