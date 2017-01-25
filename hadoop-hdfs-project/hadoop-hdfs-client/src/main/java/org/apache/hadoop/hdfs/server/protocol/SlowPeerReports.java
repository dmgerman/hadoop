begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
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
name|protocol
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
name|collect
operator|.
name|ImmutableMap
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
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
comment|/**  * A class that allows a DataNode to communicate information about all  * its peer DataNodes that appear to be slow.  *  * The wire representation of this structure is a list of  * SlowPeerReportProto messages.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|SlowPeerReports
specifier|public
specifier|final
class|class
name|SlowPeerReports
block|{
comment|/**    * A map from the DataNode's DataNodeUUID to its aggregate latency    * as seen by the reporting node.    *    * The exact choice of the aggregate is opaque to the NameNode but it    * should be chosen consistently by all DataNodes in the cluster.    * Examples of aggregates are 90th percentile (good) and mean (not so    * good).    *    * The NameNode must not attempt to interpret the aggregate latencies    * beyond exposing them as a diagnostic. e.g. metrics. Also, comparing    * latencies across reports from different DataNodes may not be not    * meaningful and must be avoided.    */
annotation|@
name|Nonnull
DECL|field|slowPeers
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|slowPeers
decl_stmt|;
comment|/**    * An object representing a SlowPeerReports with no entries. Should    * be used instead of null or creating new objects when there are    * no slow peers to report.    */
DECL|field|EMPTY_REPORT
specifier|public
specifier|static
specifier|final
name|SlowPeerReports
name|EMPTY_REPORT
init|=
operator|new
name|SlowPeerReports
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|SlowPeerReports (Map<String, Double> slowPeers)
specifier|private
name|SlowPeerReports
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|slowPeers
parameter_list|)
block|{
name|this
operator|.
name|slowPeers
operator|=
name|slowPeers
expr_stmt|;
block|}
DECL|method|create ( @ullable Map<String, Double> slowPeers)
specifier|public
specifier|static
name|SlowPeerReports
name|create
parameter_list|(
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|slowPeers
parameter_list|)
block|{
if|if
condition|(
name|slowPeers
operator|==
literal|null
operator|||
name|slowPeers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|EMPTY_REPORT
return|;
block|}
return|return
operator|new
name|SlowPeerReports
argument_list|(
name|slowPeers
argument_list|)
return|;
block|}
DECL|method|getSlowPeers ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|getSlowPeers
parameter_list|()
block|{
return|return
name|slowPeers
return|;
block|}
DECL|method|haveSlowPeers ()
specifier|public
name|boolean
name|haveSlowPeers
parameter_list|()
block|{
return|return
name|slowPeers
operator|.
name|size
argument_list|()
operator|>
literal|0
return|;
block|}
comment|/**    * Return true if the two objects represent the same set slow peer    * entries. Primarily for unit testing convenience.    */
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|SlowPeerReports
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SlowPeerReports
name|that
init|=
operator|(
name|SlowPeerReports
operator|)
name|o
decl_stmt|;
return|return
name|slowPeers
operator|.
name|equals
argument_list|(
name|that
operator|.
name|slowPeers
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|slowPeers
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

