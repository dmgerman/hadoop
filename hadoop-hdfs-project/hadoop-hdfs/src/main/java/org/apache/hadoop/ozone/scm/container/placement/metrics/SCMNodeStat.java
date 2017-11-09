begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.container.placement.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|container
operator|.
name|placement
operator|.
name|metrics
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
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * This class represents the SCM node stat.  */
end_comment

begin_class
DECL|class|SCMNodeStat
specifier|public
class|class
name|SCMNodeStat
implements|implements
name|NodeStat
block|{
DECL|field|capacity
specifier|private
name|LongMetric
name|capacity
decl_stmt|;
DECL|field|scmUsed
specifier|private
name|LongMetric
name|scmUsed
decl_stmt|;
DECL|field|remaining
specifier|private
name|LongMetric
name|remaining
decl_stmt|;
DECL|method|SCMNodeStat ()
specifier|public
name|SCMNodeStat
parameter_list|()
block|{
name|this
argument_list|(
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
DECL|method|SCMNodeStat (SCMNodeStat other)
specifier|public
name|SCMNodeStat
parameter_list|(
name|SCMNodeStat
name|other
parameter_list|)
block|{
name|this
argument_list|(
name|other
operator|.
name|capacity
operator|.
name|get
argument_list|()
argument_list|,
name|other
operator|.
name|scmUsed
operator|.
name|get
argument_list|()
argument_list|,
name|other
operator|.
name|remaining
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|SCMNodeStat (long capacity, long used, long remaining)
specifier|public
name|SCMNodeStat
parameter_list|(
name|long
name|capacity
parameter_list|,
name|long
name|used
parameter_list|,
name|long
name|remaining
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|capacity
operator|>=
literal|0
argument_list|,
literal|"Capacity cannot be "
operator|+
literal|"negative."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|used
operator|>=
literal|0
argument_list|,
literal|"used space cannot be "
operator|+
literal|"negative."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|remaining
operator|>=
literal|0
argument_list|,
literal|"remaining cannot be "
operator|+
literal|"negative"
argument_list|)
expr_stmt|;
name|this
operator|.
name|capacity
operator|=
operator|new
name|LongMetric
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
name|this
operator|.
name|scmUsed
operator|=
operator|new
name|LongMetric
argument_list|(
name|used
argument_list|)
expr_stmt|;
name|this
operator|.
name|remaining
operator|=
operator|new
name|LongMetric
argument_list|(
name|remaining
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the total configured capacity of the node.    */
DECL|method|getCapacity ()
specifier|public
name|LongMetric
name|getCapacity
parameter_list|()
block|{
return|return
name|capacity
return|;
block|}
comment|/**    * @return the total SCM used space on the node.    */
DECL|method|getScmUsed ()
specifier|public
name|LongMetric
name|getScmUsed
parameter_list|()
block|{
return|return
name|scmUsed
return|;
block|}
comment|/**    * @return the total remaining space available on the node.    */
DECL|method|getRemaining ()
specifier|public
name|LongMetric
name|getRemaining
parameter_list|()
block|{
return|return
name|remaining
return|;
block|}
comment|/**    * Set the capacity, used and remaining space on a datanode.    *    * @param newCapacity in bytes    * @param newUsed in bytes    * @param newRemaining in bytes    */
annotation|@
name|VisibleForTesting
DECL|method|set (long newCapacity, long newUsed, long newRemaining)
specifier|public
name|void
name|set
parameter_list|(
name|long
name|newCapacity
parameter_list|,
name|long
name|newUsed
parameter_list|,
name|long
name|newRemaining
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|newCapacity
argument_list|,
literal|"Capacity cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|newUsed
argument_list|,
literal|"used cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|newRemaining
argument_list|,
literal|"remaining cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|newCapacity
operator|>=
literal|0
argument_list|,
literal|"Capacity cannot be "
operator|+
literal|"negative."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|newUsed
operator|>=
literal|0
argument_list|,
literal|"used space cannot be "
operator|+
literal|"negative."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|newRemaining
operator|>=
literal|0
argument_list|,
literal|"remaining cannot be "
operator|+
literal|"negative"
argument_list|)
expr_stmt|;
name|this
operator|.
name|capacity
operator|=
operator|new
name|LongMetric
argument_list|(
name|newCapacity
argument_list|)
expr_stmt|;
name|this
operator|.
name|scmUsed
operator|=
operator|new
name|LongMetric
argument_list|(
name|newUsed
argument_list|)
expr_stmt|;
name|this
operator|.
name|remaining
operator|=
operator|new
name|LongMetric
argument_list|(
name|newRemaining
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds a new nodestat to existing values of the node.    *    * @param stat Nodestat.    * @return SCMNodeStat    */
DECL|method|add (NodeStat stat)
specifier|public
name|SCMNodeStat
name|add
parameter_list|(
name|NodeStat
name|stat
parameter_list|)
block|{
name|this
operator|.
name|capacity
operator|.
name|set
argument_list|(
name|this
operator|.
name|getCapacity
argument_list|()
operator|.
name|get
argument_list|()
operator|+
name|stat
operator|.
name|getCapacity
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|scmUsed
operator|.
name|set
argument_list|(
name|this
operator|.
name|getScmUsed
argument_list|()
operator|.
name|get
argument_list|()
operator|+
name|stat
operator|.
name|getScmUsed
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|remaining
operator|.
name|set
argument_list|(
name|this
operator|.
name|getRemaining
argument_list|()
operator|.
name|get
argument_list|()
operator|+
name|stat
operator|.
name|getRemaining
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Subtracts the stat values from the existing NodeStat.    *    * @param stat SCMNodeStat.    * @return Modified SCMNodeStat    */
DECL|method|subtract (NodeStat stat)
specifier|public
name|SCMNodeStat
name|subtract
parameter_list|(
name|NodeStat
name|stat
parameter_list|)
block|{
name|this
operator|.
name|capacity
operator|.
name|set
argument_list|(
name|this
operator|.
name|getCapacity
argument_list|()
operator|.
name|get
argument_list|()
operator|-
name|stat
operator|.
name|getCapacity
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|scmUsed
operator|.
name|set
argument_list|(
name|this
operator|.
name|getScmUsed
argument_list|()
operator|.
name|get
argument_list|()
operator|-
name|stat
operator|.
name|getScmUsed
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|remaining
operator|.
name|set
argument_list|(
name|this
operator|.
name|getRemaining
argument_list|()
operator|.
name|get
argument_list|()
operator|-
name|stat
operator|.
name|getRemaining
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object to)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|to
parameter_list|)
block|{
if|if
condition|(
name|to
operator|instanceof
name|SCMNodeStat
condition|)
block|{
name|SCMNodeStat
name|tempStat
init|=
operator|(
name|SCMNodeStat
operator|)
name|to
decl_stmt|;
return|return
name|capacity
operator|.
name|isEqual
argument_list|(
name|tempStat
operator|.
name|getCapacity
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|&&
name|scmUsed
operator|.
name|isEqual
argument_list|(
name|tempStat
operator|.
name|getScmUsed
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|&&
name|remaining
operator|.
name|isEqual
argument_list|(
name|tempStat
operator|.
name|getRemaining
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
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
name|Long
operator|.
name|hashCode
argument_list|(
name|capacity
operator|.
name|get
argument_list|()
operator|^
name|scmUsed
operator|.
name|get
argument_list|()
operator|^
name|remaining
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

