begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.dao
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
operator|.
name|dao
package|;
end_package

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
name|activities
operator|.
name|NodeAllocation
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
name|annotation
operator|.
name|XmlAccessType
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
name|annotation
operator|.
name|XmlAccessorType
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
name|annotation
operator|.
name|XmlRootElement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|ArrayList
import|;
end_import

begin_comment
comment|/*  * DAO object to display node allocation activity.  */
end_comment

begin_class
annotation|@
name|XmlRootElement
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|ActivitiesInfo
specifier|public
class|class
name|ActivitiesInfo
block|{
DECL|field|nodeId
specifier|protected
name|String
name|nodeId
decl_stmt|;
DECL|field|timeStamp
specifier|protected
name|String
name|timeStamp
decl_stmt|;
DECL|field|diagnostic
specifier|protected
name|String
name|diagnostic
init|=
literal|null
decl_stmt|;
DECL|field|allocations
specifier|protected
name|List
argument_list|<
name|NodeAllocationInfo
argument_list|>
name|allocations
decl_stmt|;
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
name|ActivitiesInfo
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|ActivitiesInfo ()
specifier|public
name|ActivitiesInfo
parameter_list|()
block|{   }
DECL|method|ActivitiesInfo (String errorMessage, String nodeId)
specifier|public
name|ActivitiesInfo
parameter_list|(
name|String
name|errorMessage
parameter_list|,
name|String
name|nodeId
parameter_list|)
block|{
name|this
operator|.
name|diagnostic
operator|=
name|errorMessage
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
name|nodeId
expr_stmt|;
block|}
DECL|method|ActivitiesInfo (List<NodeAllocation> nodeAllocations, String nodeId)
specifier|public
name|ActivitiesInfo
parameter_list|(
name|List
argument_list|<
name|NodeAllocation
argument_list|>
name|nodeAllocations
parameter_list|,
name|String
name|nodeId
parameter_list|)
block|{
name|this
operator|.
name|nodeId
operator|=
name|nodeId
expr_stmt|;
name|this
operator|.
name|allocations
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodeAllocations
operator|==
literal|null
condition|)
block|{
name|diagnostic
operator|=
operator|(
name|nodeId
operator|!=
literal|null
condition|?
literal|"waiting for display"
else|:
literal|"waiting for next allocation"
operator|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|nodeAllocations
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|diagnostic
operator|=
literal|"do not have available resources"
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|nodeId
operator|=
name|nodeAllocations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeId
argument_list|()
expr_stmt|;
name|Date
name|date
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|date
operator|.
name|setTime
argument_list|(
name|nodeAllocations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTimeStamp
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|timeStamp
operator|=
name|date
operator|.
name|toString
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodeAllocations
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|NodeAllocation
name|nodeAllocation
init|=
name|nodeAllocations
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|NodeAllocationInfo
name|allocationInfo
init|=
operator|new
name|NodeAllocationInfo
argument_list|(
name|nodeAllocation
argument_list|)
decl_stmt|;
name|this
operator|.
name|allocations
operator|.
name|add
argument_list|(
name|allocationInfo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

