begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api.impl
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
name|api
operator|.
name|impl
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ExecutionType
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
name|ExecutionTypeRequest
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
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|TreeMap
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
name|ResourceRequest
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
name|impl
operator|.
name|AMRMClientImpl
operator|.
name|ResourceRequestInfo
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
name|impl
operator|.
name|AMRMClientImpl
operator|.
name|ResourceReverseMemoryThenCpuComparator
import|;
end_import

begin_class
DECL|class|RemoteRequestsTable
class|class
name|RemoteRequestsTable
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Iterable
argument_list|<
name|ResourceRequestInfo
argument_list|>
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
name|RemoteRequestsTable
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|resourceComparator
specifier|static
name|ResourceReverseMemoryThenCpuComparator
name|resourceComparator
init|=
operator|new
name|ResourceReverseMemoryThenCpuComparator
argument_list|()
decl_stmt|;
comment|/**    * Nested Iterator that iterates over just the ResourceRequestInfo    * object.    */
DECL|class|RequestInfoIterator
class|class
name|RequestInfoIterator
implements|implements
name|Iterator
argument_list|<
name|ResourceRequestInfo
argument_list|>
block|{
specifier|private
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|ExecutionType
argument_list|,
name|TreeMap
argument_list|<
name|Resource
argument_list|,
DECL|field|iLocMap
name|ResourceRequestInfo
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|iLocMap
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|Map
argument_list|<
name|ExecutionType
argument_list|,
name|TreeMap
argument_list|<
name|Resource
argument_list|,
DECL|field|iExecTypeMap
name|ResourceRequestInfo
argument_list|>
argument_list|>
argument_list|>
name|iExecTypeMap
decl_stmt|;
DECL|field|iCapMap
specifier|private
name|Iterator
argument_list|<
name|TreeMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequestInfo
argument_list|>
argument_list|>
name|iCapMap
decl_stmt|;
DECL|field|iResReqInfo
specifier|private
name|Iterator
argument_list|<
name|ResourceRequestInfo
argument_list|>
name|iResReqInfo
decl_stmt|;
DECL|method|RequestInfoIterator (Iterator<Map<String, Map<ExecutionType, TreeMap<Resource, ResourceRequestInfo>>>> iLocationMap)
specifier|public
name|RequestInfoIterator
parameter_list|(
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|ExecutionType
argument_list|,
name|TreeMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequestInfo
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|iLocationMap
parameter_list|)
block|{
name|this
operator|.
name|iLocMap
operator|=
name|iLocationMap
expr_stmt|;
if|if
condition|(
name|iLocMap
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|iExecTypeMap
operator|=
name|iLocMap
operator|.
name|next
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|iExecTypeMap
operator|=
operator|new
name|LinkedList
argument_list|<
name|Map
argument_list|<
name|ExecutionType
argument_list|,
name|TreeMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequestInfo
argument_list|>
argument_list|>
argument_list|>
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|iExecTypeMap
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|iCapMap
operator|=
name|iExecTypeMap
operator|.
name|next
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|iCapMap
operator|=
operator|new
name|LinkedList
argument_list|<
name|TreeMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequestInfo
argument_list|>
argument_list|>
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|iCapMap
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|iResReqInfo
operator|=
name|iCapMap
operator|.
name|next
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|iResReqInfo
operator|=
operator|new
name|LinkedList
argument_list|<
name|ResourceRequestInfo
argument_list|>
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iLocMap
operator|.
name|hasNext
argument_list|()
operator|||
name|iExecTypeMap
operator|.
name|hasNext
argument_list|()
operator|||
name|iCapMap
operator|.
name|hasNext
argument_list|()
operator|||
name|iResReqInfo
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|ResourceRequestInfo
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|iResReqInfo
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|iCapMap
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|iExecTypeMap
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|iExecTypeMap
operator|=
name|iLocMap
operator|.
name|next
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
name|iCapMap
operator|=
name|iExecTypeMap
operator|.
name|next
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
name|iResReqInfo
operator|=
name|iCapMap
operator|.
name|next
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
return|return
name|iResReqInfo
operator|.
name|next
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|remove ()
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Remove is not supported"
operator|+
literal|"for this iterator !!"
argument_list|)
throw|;
block|}
block|}
comment|// Nest map with Primary key :
comment|// Priority -> ResourceName(String) -> ExecutionType -> Capability(Resource)
comment|// and value : ResourceRequestInfo
specifier|private
name|Map
argument_list|<
name|Priority
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|ExecutionType
argument_list|,
name|TreeMap
argument_list|<
name|Resource
argument_list|,
DECL|field|remoteRequestsTable
name|ResourceRequestInfo
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|remoteRequestsTable
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|ResourceRequestInfo
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|RequestInfoIterator
argument_list|(
name|remoteRequestsTable
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
DECL|method|get (Priority priority, String location, ExecutionType execType, Resource capability)
name|ResourceRequestInfo
name|get
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|String
name|location
parameter_list|,
name|ExecutionType
name|execType
parameter_list|,
name|Resource
name|capability
parameter_list|)
block|{
name|TreeMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequestInfo
argument_list|>
name|capabilityMap
init|=
name|getCapabilityMap
argument_list|(
name|priority
argument_list|,
name|location
argument_list|,
name|execType
argument_list|)
decl_stmt|;
if|if
condition|(
name|capabilityMap
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|capabilityMap
operator|.
name|get
argument_list|(
name|capability
argument_list|)
return|;
block|}
DECL|method|put (Priority priority, String resourceName, ExecutionType execType, Resource capability, ResourceRequestInfo resReqInfo)
name|void
name|put
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|String
name|resourceName
parameter_list|,
name|ExecutionType
name|execType
parameter_list|,
name|Resource
name|capability
parameter_list|,
name|ResourceRequestInfo
name|resReqInfo
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|ExecutionType
argument_list|,
name|TreeMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequestInfo
argument_list|>
argument_list|>
argument_list|>
name|locationMap
init|=
name|remoteRequestsTable
operator|.
name|get
argument_list|(
name|priority
argument_list|)
decl_stmt|;
if|if
condition|(
name|locationMap
operator|==
literal|null
condition|)
block|{
name|locationMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|remoteRequestsTable
operator|.
name|put
argument_list|(
name|priority
argument_list|,
name|locationMap
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Added priority="
operator|+
name|priority
argument_list|)
expr_stmt|;
block|}
block|}
name|Map
argument_list|<
name|ExecutionType
argument_list|,
name|TreeMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequestInfo
argument_list|>
argument_list|>
name|execTypeMap
init|=
name|locationMap
operator|.
name|get
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|execTypeMap
operator|==
literal|null
condition|)
block|{
name|execTypeMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|locationMap
operator|.
name|put
argument_list|(
name|resourceName
argument_list|,
name|execTypeMap
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Added resourceName="
operator|+
name|resourceName
argument_list|)
expr_stmt|;
block|}
block|}
name|TreeMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequestInfo
argument_list|>
name|capabilityMap
init|=
name|execTypeMap
operator|.
name|get
argument_list|(
name|execType
argument_list|)
decl_stmt|;
if|if
condition|(
name|capabilityMap
operator|==
literal|null
condition|)
block|{
name|capabilityMap
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|(
name|resourceComparator
argument_list|)
expr_stmt|;
name|execTypeMap
operator|.
name|put
argument_list|(
name|execType
argument_list|,
name|capabilityMap
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Added Execution Type="
operator|+
name|execType
argument_list|)
expr_stmt|;
block|}
block|}
name|capabilityMap
operator|.
name|put
argument_list|(
name|capability
argument_list|,
name|resReqInfo
argument_list|)
expr_stmt|;
block|}
DECL|method|remove (Priority priority, String resourceName, ExecutionType execType, Resource capability)
name|ResourceRequestInfo
name|remove
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|String
name|resourceName
parameter_list|,
name|ExecutionType
name|execType
parameter_list|,
name|Resource
name|capability
parameter_list|)
block|{
name|ResourceRequestInfo
name|retVal
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|ExecutionType
argument_list|,
name|TreeMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequestInfo
argument_list|>
argument_list|>
argument_list|>
name|locationMap
init|=
name|remoteRequestsTable
operator|.
name|get
argument_list|(
name|priority
argument_list|)
decl_stmt|;
if|if
condition|(
name|locationMap
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No such priority="
operator|+
name|priority
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
name|Map
argument_list|<
name|ExecutionType
argument_list|,
name|TreeMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequestInfo
argument_list|>
argument_list|>
name|execTypeMap
init|=
name|locationMap
operator|.
name|get
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|execTypeMap
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No such resourceName="
operator|+
name|resourceName
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
name|TreeMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequestInfo
argument_list|>
name|capabilityMap
init|=
name|execTypeMap
operator|.
name|get
argument_list|(
name|execType
argument_list|)
decl_stmt|;
if|if
condition|(
name|capabilityMap
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No such Execution Type="
operator|+
name|execType
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
name|retVal
operator|=
name|capabilityMap
operator|.
name|remove
argument_list|(
name|capability
argument_list|)
expr_stmt|;
if|if
condition|(
name|capabilityMap
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|execTypeMap
operator|.
name|remove
argument_list|(
name|execType
argument_list|)
expr_stmt|;
if|if
condition|(
name|execTypeMap
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|locationMap
operator|.
name|remove
argument_list|(
name|resourceName
argument_list|)
expr_stmt|;
if|if
condition|(
name|locationMap
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|remoteRequestsTable
operator|.
name|remove
argument_list|(
name|priority
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|retVal
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|ExecutionType
argument_list|,
name|TreeMap
argument_list|<
name|Resource
argument_list|,
DECL|method|getLocationMap (Priority priority)
name|ResourceRequestInfo
argument_list|>
argument_list|>
argument_list|>
name|getLocationMap
parameter_list|(
name|Priority
name|priority
parameter_list|)
block|{
return|return
name|remoteRequestsTable
operator|.
name|get
argument_list|(
name|priority
argument_list|)
return|;
block|}
name|Map
argument_list|<
name|ExecutionType
argument_list|,
name|TreeMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequestInfo
argument_list|>
argument_list|>
DECL|method|getExecutionTypeMap (Priority priority, String location)
name|getExecutionTypeMap
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|String
name|location
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|ExecutionType
argument_list|,
name|TreeMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequestInfo
argument_list|>
argument_list|>
argument_list|>
name|locationMap
init|=
name|getLocationMap
argument_list|(
name|priority
argument_list|)
decl_stmt|;
if|if
condition|(
name|locationMap
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|locationMap
operator|.
name|get
argument_list|(
name|location
argument_list|)
return|;
block|}
DECL|method|getCapabilityMap (Priority priority, String location, ExecutionType execType)
name|TreeMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequestInfo
argument_list|>
name|getCapabilityMap
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|String
name|location
parameter_list|,
name|ExecutionType
name|execType
parameter_list|)
block|{
name|Map
argument_list|<
name|ExecutionType
argument_list|,
name|TreeMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequestInfo
argument_list|>
argument_list|>
name|executionTypeMap
init|=
name|getExecutionTypeMap
argument_list|(
name|priority
argument_list|,
name|location
argument_list|)
decl_stmt|;
if|if
condition|(
name|executionTypeMap
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|executionTypeMap
operator|.
name|get
argument_list|(
name|execType
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getAllResourceRequestInfos (Priority priority, Collection<String> locations)
name|List
argument_list|<
name|ResourceRequestInfo
argument_list|>
name|getAllResourceRequestInfos
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|locations
parameter_list|)
block|{
name|List
name|retList
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|location
range|:
name|locations
control|)
block|{
for|for
control|(
name|ExecutionType
name|eType
range|:
name|ExecutionType
operator|.
name|values
argument_list|()
control|)
block|{
name|TreeMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequestInfo
argument_list|>
name|capabilityMap
init|=
name|getCapabilityMap
argument_list|(
name|priority
argument_list|,
name|location
argument_list|,
name|eType
argument_list|)
decl_stmt|;
if|if
condition|(
name|capabilityMap
operator|!=
literal|null
condition|)
block|{
name|retList
operator|.
name|addAll
argument_list|(
name|capabilityMap
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|retList
return|;
block|}
DECL|method|getMatchingRequests ( Priority priority, String resourceName, ExecutionType executionType, Resource capability)
name|List
argument_list|<
name|ResourceRequestInfo
argument_list|>
name|getMatchingRequests
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|String
name|resourceName
parameter_list|,
name|ExecutionType
name|executionType
parameter_list|,
name|Resource
name|capability
parameter_list|)
block|{
name|List
argument_list|<
name|ResourceRequestInfo
argument_list|>
name|list
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|TreeMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequestInfo
argument_list|>
name|capabilityMap
init|=
name|getCapabilityMap
argument_list|(
name|priority
argument_list|,
name|resourceName
argument_list|,
name|executionType
argument_list|)
decl_stmt|;
if|if
condition|(
name|capabilityMap
operator|!=
literal|null
condition|)
block|{
name|ResourceRequestInfo
name|resourceRequestInfo
init|=
name|capabilityMap
operator|.
name|get
argument_list|(
name|capability
argument_list|)
decl_stmt|;
if|if
condition|(
name|resourceRequestInfo
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|resourceRequestInfo
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|list
operator|.
name|addAll
argument_list|(
name|capabilityMap
operator|.
name|tailMap
argument_list|(
name|capability
argument_list|)
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|addResourceRequest (Priority priority, String resourceName, ExecutionTypeRequest execTypeReq, Resource capability, T req, boolean relaxLocality, String labelExpression)
name|ResourceRequestInfo
name|addResourceRequest
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|String
name|resourceName
parameter_list|,
name|ExecutionTypeRequest
name|execTypeReq
parameter_list|,
name|Resource
name|capability
parameter_list|,
name|T
name|req
parameter_list|,
name|boolean
name|relaxLocality
parameter_list|,
name|String
name|labelExpression
parameter_list|)
block|{
name|ResourceRequestInfo
name|resourceRequestInfo
init|=
name|get
argument_list|(
name|priority
argument_list|,
name|resourceName
argument_list|,
name|execTypeReq
operator|.
name|getExecutionType
argument_list|()
argument_list|,
name|capability
argument_list|)
decl_stmt|;
if|if
condition|(
name|resourceRequestInfo
operator|==
literal|null
condition|)
block|{
name|resourceRequestInfo
operator|=
operator|new
name|ResourceRequestInfo
argument_list|(
name|priority
argument_list|,
name|resourceName
argument_list|,
name|capability
argument_list|,
name|relaxLocality
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|priority
argument_list|,
name|resourceName
argument_list|,
name|execTypeReq
operator|.
name|getExecutionType
argument_list|()
argument_list|,
name|capability
argument_list|,
name|resourceRequestInfo
argument_list|)
expr_stmt|;
block|}
name|resourceRequestInfo
operator|.
name|remoteRequest
operator|.
name|setExecutionTypeRequest
argument_list|(
name|execTypeReq
argument_list|)
expr_stmt|;
name|resourceRequestInfo
operator|.
name|remoteRequest
operator|.
name|setNumContainers
argument_list|(
name|resourceRequestInfo
operator|.
name|remoteRequest
operator|.
name|getNumContainers
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|relaxLocality
condition|)
block|{
name|resourceRequestInfo
operator|.
name|containerRequests
operator|.
name|add
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ResourceRequest
operator|.
name|ANY
operator|.
name|equals
argument_list|(
name|resourceName
argument_list|)
condition|)
block|{
name|resourceRequestInfo
operator|.
name|remoteRequest
operator|.
name|setNodeLabelExpression
argument_list|(
name|labelExpression
argument_list|)
expr_stmt|;
block|}
return|return
name|resourceRequestInfo
return|;
block|}
DECL|method|decResourceRequest (Priority priority, String resourceName, ExecutionTypeRequest execTypeReq, Resource capability, T req)
name|ResourceRequestInfo
name|decResourceRequest
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|String
name|resourceName
parameter_list|,
name|ExecutionTypeRequest
name|execTypeReq
parameter_list|,
name|Resource
name|capability
parameter_list|,
name|T
name|req
parameter_list|)
block|{
name|ResourceRequestInfo
name|resourceRequestInfo
init|=
name|get
argument_list|(
name|priority
argument_list|,
name|resourceName
argument_list|,
name|execTypeReq
operator|.
name|getExecutionType
argument_list|()
argument_list|,
name|capability
argument_list|)
decl_stmt|;
if|if
condition|(
name|resourceRequestInfo
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Not decrementing resource as ResourceRequestInfo with"
operator|+
literal|"priority="
operator|+
name|priority
operator|+
literal|", "
operator|+
literal|"resourceName="
operator|+
name|resourceName
operator|+
literal|", "
operator|+
literal|"executionType="
operator|+
name|execTypeReq
operator|+
literal|", "
operator|+
literal|"capability="
operator|+
name|capability
operator|+
literal|" is not present in request table"
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"BEFORE decResourceRequest:"
operator|+
literal|" applicationId="
operator|+
literal|" priority="
operator|+
name|priority
operator|.
name|getPriority
argument_list|()
operator|+
literal|" resourceName="
operator|+
name|resourceName
operator|+
literal|" numContainers="
operator|+
name|resourceRequestInfo
operator|.
name|remoteRequest
operator|.
name|getNumContainers
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|resourceRequestInfo
operator|.
name|remoteRequest
operator|.
name|setNumContainers
argument_list|(
name|resourceRequestInfo
operator|.
name|remoteRequest
operator|.
name|getNumContainers
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|resourceRequestInfo
operator|.
name|containerRequests
operator|.
name|remove
argument_list|(
name|req
argument_list|)
expr_stmt|;
if|if
condition|(
name|resourceRequestInfo
operator|.
name|remoteRequest
operator|.
name|getNumContainers
argument_list|()
operator|<
literal|0
condition|)
block|{
comment|// guard against spurious removals
name|resourceRequestInfo
operator|.
name|remoteRequest
operator|.
name|setNumContainers
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|resourceRequestInfo
return|;
block|}
DECL|method|isEmpty ()
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|remoteRequestsTable
operator|.
name|isEmpty
argument_list|()
return|;
block|}
block|}
end_class

end_unit

