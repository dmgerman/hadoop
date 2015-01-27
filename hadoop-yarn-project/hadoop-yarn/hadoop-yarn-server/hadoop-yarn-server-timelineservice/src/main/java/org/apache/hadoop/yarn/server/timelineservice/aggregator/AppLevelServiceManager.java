begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.aggregator
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
name|timelineservice
operator|.
name|aggregator
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|Unstable
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
name|service
operator|.
name|CompositeService
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
name|YarnRuntimeException
import|;
end_import

begin_comment
comment|/**  * Class that manages adding and removing app level aggregator services and  * their lifecycle. It provides thread safety access to the app level services.  *  * It is a singleton, and instances should be obtained via  * {@link #getInstance()}.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|AppLevelServiceManager
specifier|public
class|class
name|AppLevelServiceManager
extends|extends
name|CompositeService
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
name|AppLevelServiceManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|INSTANCE
specifier|private
specifier|static
specifier|final
name|AppLevelServiceManager
name|INSTANCE
init|=
operator|new
name|AppLevelServiceManager
argument_list|()
decl_stmt|;
comment|// access to this map is synchronized with the map itself
DECL|field|services
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|AppLevelAggregatorService
argument_list|>
name|services
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|AppLevelAggregatorService
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|getInstance ()
specifier|static
name|AppLevelServiceManager
name|getInstance
parameter_list|()
block|{
return|return
name|INSTANCE
return|;
block|}
DECL|method|AppLevelServiceManager ()
name|AppLevelServiceManager
parameter_list|()
block|{
name|super
argument_list|(
name|AppLevelServiceManager
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates and adds an app level aggregator service for the specified    * application id. The service is also initialized and started. If the service    * already exists, no new service is created.    *    * @throws YarnRuntimeException if there was any exception in initializing and    * starting the app level service    * @return whether it was added successfully    */
DECL|method|addService (String appId)
specifier|public
name|boolean
name|addService
parameter_list|(
name|String
name|appId
parameter_list|)
block|{
synchronized|synchronized
init|(
name|services
init|)
block|{
name|AppLevelAggregatorService
name|service
init|=
name|services
operator|.
name|get
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|service
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|service
operator|=
operator|new
name|AppLevelAggregatorService
argument_list|(
name|appId
argument_list|)
expr_stmt|;
comment|// initialize, start, and add it to the parent service so it can be
comment|// cleaned up when the parent shuts down
name|service
operator|.
name|init
argument_list|(
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
name|services
operator|.
name|put
argument_list|(
name|appId
argument_list|,
name|service
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"the application aggregator service for "
operator|+
name|appId
operator|+
literal|" was added"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|String
name|msg
init|=
literal|"the application aggregator service for "
operator|+
name|appId
operator|+
literal|" already exists!"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
comment|/**    * Removes the app level aggregator service for the specified application id.    * The service is also stopped as a result. If the service does not exist, no    * change is made.    *    * @return whether it was removed successfully    */
DECL|method|removeService (String appId)
specifier|public
name|boolean
name|removeService
parameter_list|(
name|String
name|appId
parameter_list|)
block|{
synchronized|synchronized
init|(
name|services
init|)
block|{
name|AppLevelAggregatorService
name|service
init|=
name|services
operator|.
name|remove
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|service
operator|==
literal|null
condition|)
block|{
name|String
name|msg
init|=
literal|"the application aggregator service for "
operator|+
name|appId
operator|+
literal|" does not exist!"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
comment|// stop the service to do clean up
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"the application aggregator service for "
operator|+
name|appId
operator|+
literal|" was removed"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
comment|/**    * Returns the app level aggregator service for the specified application id.    *    * @return the app level aggregator service or null if it does not exist    */
DECL|method|getService (String appId)
specifier|public
name|AppLevelAggregatorService
name|getService
parameter_list|(
name|String
name|appId
parameter_list|)
block|{
return|return
name|services
operator|.
name|get
argument_list|(
name|appId
argument_list|)
return|;
block|}
comment|/**    * Returns whether the app level aggregator service for the specified    * application id exists.    */
DECL|method|hasService (String appId)
specifier|public
name|boolean
name|hasService
parameter_list|(
name|String
name|appId
parameter_list|)
block|{
return|return
name|services
operator|.
name|containsKey
argument_list|(
name|appId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

