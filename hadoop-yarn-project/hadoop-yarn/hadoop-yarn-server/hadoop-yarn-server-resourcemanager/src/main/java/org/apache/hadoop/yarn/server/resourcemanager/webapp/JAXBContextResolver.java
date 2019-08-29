begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp
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
operator|.
name|webapp
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Singleton
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
name|json
operator|.
name|JSONConfiguration
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
name|json
operator|.
name|JSONJAXBContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|ext
operator|.
name|ContextResolver
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
name|ext
operator|.
name|Provider
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|JAXBContext
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|capacity
operator|.
name|UserInfo
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
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
operator|.
name|dao
operator|.
name|*
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
name|RemoteExceptionData
import|;
end_import

begin_class
annotation|@
name|Singleton
annotation|@
name|Provider
DECL|class|JAXBContextResolver
specifier|public
class|class
name|JAXBContextResolver
implements|implements
name|ContextResolver
argument_list|<
name|JAXBContext
argument_list|>
block|{
DECL|field|typesContextMap
specifier|private
specifier|final
name|Map
argument_list|<
name|Class
argument_list|,
name|JAXBContext
argument_list|>
name|typesContextMap
decl_stmt|;
DECL|method|JAXBContextResolver ()
specifier|public
name|JAXBContextResolver
parameter_list|()
throws|throws
name|Exception
block|{
name|JAXBContext
name|context
decl_stmt|;
name|JAXBContext
name|unWrappedRootContext
decl_stmt|;
comment|// you have to specify all the dao classes here
specifier|final
name|Class
index|[]
name|cTypes
init|=
block|{
name|AppInfo
operator|.
name|class
block|,
name|AppAttemptInfo
operator|.
name|class
block|,
name|AppAttemptsInfo
operator|.
name|class
block|,
name|ClusterInfo
operator|.
name|class
block|,
name|CapacitySchedulerQueueInfo
operator|.
name|class
block|,
name|FifoSchedulerInfo
operator|.
name|class
block|,
name|SchedulerTypeInfo
operator|.
name|class
block|,
name|NodeInfo
operator|.
name|class
block|,
name|UserMetricsInfo
operator|.
name|class
block|,
name|CapacitySchedulerInfo
operator|.
name|class
block|,
name|ClusterMetricsInfo
operator|.
name|class
block|,
name|SchedulerInfo
operator|.
name|class
block|,
name|AppsInfo
operator|.
name|class
block|,
name|NodesInfo
operator|.
name|class
block|,
name|RemoteExceptionData
operator|.
name|class
block|,
name|CapacitySchedulerQueueInfoList
operator|.
name|class
block|,
name|ResourceInfo
operator|.
name|class
block|,
name|UsersInfo
operator|.
name|class
block|,
name|UserInfo
operator|.
name|class
block|,
name|ApplicationStatisticsInfo
operator|.
name|class
block|,
name|StatisticsItemInfo
operator|.
name|class
block|,
name|CapacitySchedulerHealthInfo
operator|.
name|class
block|,
name|FairSchedulerQueueInfoList
operator|.
name|class
block|,
name|AppTimeoutsInfo
operator|.
name|class
block|,
name|AppTimeoutInfo
operator|.
name|class
block|,
name|ResourceInformationsInfo
operator|.
name|class
block|,
name|ActivitiesInfo
operator|.
name|class
block|,
name|AppActivitiesInfo
operator|.
name|class
block|}
decl_stmt|;
comment|// these dao classes need root unwrapping
specifier|final
name|Class
index|[]
name|rootUnwrappedTypes
init|=
block|{
name|NewApplication
operator|.
name|class
block|,
name|ApplicationSubmissionContextInfo
operator|.
name|class
block|,
name|ContainerLaunchContextInfo
operator|.
name|class
block|,
name|LocalResourceInfo
operator|.
name|class
block|,
name|DelegationToken
operator|.
name|class
block|,
name|AppQueue
operator|.
name|class
block|,
name|AppPriority
operator|.
name|class
block|,
name|ResourceOptionInfo
operator|.
name|class
block|}
decl_stmt|;
name|this
operator|.
name|typesContextMap
operator|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|,
name|JAXBContext
argument_list|>
argument_list|()
expr_stmt|;
name|context
operator|=
operator|new
name|JSONJAXBContext
argument_list|(
name|JSONConfiguration
operator|.
name|natural
argument_list|()
operator|.
name|rootUnwrapping
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|cTypes
argument_list|)
expr_stmt|;
name|unWrappedRootContext
operator|=
operator|new
name|JSONJAXBContext
argument_list|(
name|JSONConfiguration
operator|.
name|natural
argument_list|()
operator|.
name|rootUnwrapping
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|rootUnwrappedTypes
argument_list|)
expr_stmt|;
for|for
control|(
name|Class
name|type
range|:
name|cTypes
control|)
block|{
name|typesContextMap
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Class
name|type
range|:
name|rootUnwrappedTypes
control|)
block|{
name|typesContextMap
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|unWrappedRootContext
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getContext (Class<?> objectType)
specifier|public
name|JAXBContext
name|getContext
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|objectType
parameter_list|)
block|{
return|return
name|typesContextMap
operator|.
name|get
argument_list|(
name|objectType
argument_list|)
return|;
block|}
block|}
end_class

end_unit

