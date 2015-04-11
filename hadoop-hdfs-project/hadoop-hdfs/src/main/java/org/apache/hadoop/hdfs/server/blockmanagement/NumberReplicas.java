begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
package|;
end_package

begin_comment
comment|/**  * A immutable object that stores the number of live replicas and  * the number of decommissioned Replicas.  */
end_comment

begin_class
DECL|class|NumberReplicas
specifier|public
class|class
name|NumberReplicas
block|{
DECL|field|liveReplicas
specifier|private
name|int
name|liveReplicas
decl_stmt|;
comment|// Tracks only the decommissioning replicas
DECL|field|decommissioning
specifier|private
name|int
name|decommissioning
decl_stmt|;
comment|// Tracks only the decommissioned replicas
DECL|field|decommissioned
specifier|private
name|int
name|decommissioned
decl_stmt|;
DECL|field|corruptReplicas
specifier|private
name|int
name|corruptReplicas
decl_stmt|;
DECL|field|excessReplicas
specifier|private
name|int
name|excessReplicas
decl_stmt|;
DECL|field|replicasOnStaleNodes
specifier|private
name|int
name|replicasOnStaleNodes
decl_stmt|;
DECL|method|NumberReplicas ()
name|NumberReplicas
parameter_list|()
block|{
name|initialize
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|NumberReplicas (int live, int decommissioned, int decommissioning, int corrupt, int excess, int stale)
name|NumberReplicas
parameter_list|(
name|int
name|live
parameter_list|,
name|int
name|decommissioned
parameter_list|,
name|int
name|decommissioning
parameter_list|,
name|int
name|corrupt
parameter_list|,
name|int
name|excess
parameter_list|,
name|int
name|stale
parameter_list|)
block|{
name|initialize
argument_list|(
name|live
argument_list|,
name|decommissioned
argument_list|,
name|decommissioning
argument_list|,
name|corrupt
argument_list|,
name|excess
argument_list|,
name|stale
argument_list|)
expr_stmt|;
block|}
DECL|method|initialize (int live, int decommissioned, int decommissioning, int corrupt, int excess, int stale)
name|void
name|initialize
parameter_list|(
name|int
name|live
parameter_list|,
name|int
name|decommissioned
parameter_list|,
name|int
name|decommissioning
parameter_list|,
name|int
name|corrupt
parameter_list|,
name|int
name|excess
parameter_list|,
name|int
name|stale
parameter_list|)
block|{
name|liveReplicas
operator|=
name|live
expr_stmt|;
name|this
operator|.
name|decommissioning
operator|=
name|decommissioning
expr_stmt|;
name|this
operator|.
name|decommissioned
operator|=
name|decommissioned
expr_stmt|;
name|corruptReplicas
operator|=
name|corrupt
expr_stmt|;
name|excessReplicas
operator|=
name|excess
expr_stmt|;
name|replicasOnStaleNodes
operator|=
name|stale
expr_stmt|;
block|}
DECL|method|liveReplicas ()
specifier|public
name|int
name|liveReplicas
parameter_list|()
block|{
return|return
name|liveReplicas
return|;
block|}
comment|/**    *    * @return decommissioned replicas + decommissioning replicas    * It is deprecated by decommissionedAndDecommissioning    * due to its misleading name.    */
annotation|@
name|Deprecated
DECL|method|decommissionedReplicas ()
specifier|public
name|int
name|decommissionedReplicas
parameter_list|()
block|{
return|return
name|decommissionedAndDecommissioning
argument_list|()
return|;
block|}
comment|/**    *    * @return decommissioned and decommissioning replicas    */
DECL|method|decommissionedAndDecommissioning ()
specifier|public
name|int
name|decommissionedAndDecommissioning
parameter_list|()
block|{
return|return
name|decommissioned
operator|+
name|decommissioning
return|;
block|}
comment|/**    *    * @return decommissioned replicas only    */
DECL|method|decommissioned ()
specifier|public
name|int
name|decommissioned
parameter_list|()
block|{
return|return
name|decommissioned
return|;
block|}
comment|/**    *    * @return decommissioning replicas only    */
DECL|method|decommissioning ()
specifier|public
name|int
name|decommissioning
parameter_list|()
block|{
return|return
name|decommissioning
return|;
block|}
DECL|method|corruptReplicas ()
specifier|public
name|int
name|corruptReplicas
parameter_list|()
block|{
return|return
name|corruptReplicas
return|;
block|}
DECL|method|excessReplicas ()
specifier|public
name|int
name|excessReplicas
parameter_list|()
block|{
return|return
name|excessReplicas
return|;
block|}
comment|/**    * @return the number of replicas which are on stale nodes.    * This is not mutually exclusive with the other counts -- ie a    * replica may count as both "live" and "stale".    */
DECL|method|replicasOnStaleNodes ()
specifier|public
name|int
name|replicasOnStaleNodes
parameter_list|()
block|{
return|return
name|replicasOnStaleNodes
return|;
block|}
block|}
end_class

end_unit

