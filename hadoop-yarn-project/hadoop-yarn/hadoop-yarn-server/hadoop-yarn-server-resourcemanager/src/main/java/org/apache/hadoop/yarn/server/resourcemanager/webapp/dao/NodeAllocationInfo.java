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

begin_comment
comment|/*  * DAO object to display each node allocation in node heartbeat.  */
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
DECL|class|NodeAllocationInfo
specifier|public
class|class
name|NodeAllocationInfo
block|{
DECL|field|allocatedContainerId
specifier|protected
name|String
name|allocatedContainerId
decl_stmt|;
DECL|field|finalAllocationState
specifier|protected
name|String
name|finalAllocationState
decl_stmt|;
DECL|field|root
specifier|protected
name|ActivityNodeInfo
name|root
init|=
literal|null
decl_stmt|;
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
name|NodeAllocationInfo
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|NodeAllocationInfo ()
name|NodeAllocationInfo
parameter_list|()
block|{   }
DECL|method|NodeAllocationInfo (NodeAllocation allocation)
name|NodeAllocationInfo
parameter_list|(
name|NodeAllocation
name|allocation
parameter_list|)
block|{
name|this
operator|.
name|allocatedContainerId
operator|=
name|allocation
operator|.
name|getContainerId
argument_list|()
expr_stmt|;
name|this
operator|.
name|finalAllocationState
operator|=
name|allocation
operator|.
name|getFinalAllocationState
argument_list|()
operator|.
name|name
argument_list|()
expr_stmt|;
name|root
operator|=
operator|new
name|ActivityNodeInfo
argument_list|(
name|allocation
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

