begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.store.records
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
name|federation
operator|.
name|store
operator|.
name|records
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

begin_comment
comment|/**  *<p>  * State of a<code>SubCluster</code>.  *</p>  */
end_comment

begin_enum
annotation|@
name|Private
annotation|@
name|Unstable
DECL|enum|SubClusterState
specifier|public
enum|enum
name|SubClusterState
block|{
comment|/** Newly registered subcluster, before the first heartbeat. */
DECL|enumConstant|SC_NEW
name|SC_NEW
block|,
comment|/** Subcluster is registered and the RM sent a heartbeat recently. */
DECL|enumConstant|SC_RUNNING
name|SC_RUNNING
block|,
comment|/** Subcluster is unhealthy. */
DECL|enumConstant|SC_UNHEALTHY
name|SC_UNHEALTHY
block|,
comment|/** Subcluster is in the process of being out of service. */
DECL|enumConstant|SC_DECOMMISSIONING
name|SC_DECOMMISSIONING
block|,
comment|/** Subcluster is out of service. */
DECL|enumConstant|SC_DECOMMISSIONED
name|SC_DECOMMISSIONED
block|,
comment|/** RM has not sent a heartbeat for some configured time threshold. */
DECL|enumConstant|SC_LOST
name|SC_LOST
block|,
comment|/** Subcluster has unregistered. */
DECL|enumConstant|SC_UNREGISTERED
name|SC_UNREGISTERED
block|;
DECL|method|isUnusable ()
specifier|public
name|boolean
name|isUnusable
parameter_list|()
block|{
return|return
operator|(
name|this
operator|!=
name|SC_RUNNING
operator|&&
name|this
operator|!=
name|SC_NEW
operator|)
return|;
block|}
DECL|method|isFinal ()
specifier|public
name|boolean
name|isFinal
parameter_list|()
block|{
return|return
operator|(
name|this
operator|==
name|SC_UNREGISTERED
operator|||
name|this
operator|==
name|SC_DECOMMISSIONED
operator|||
name|this
operator|==
name|SC_LOST
operator|)
return|;
block|}
block|}
end_enum

end_unit

