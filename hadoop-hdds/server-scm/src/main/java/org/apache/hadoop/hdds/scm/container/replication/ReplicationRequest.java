begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container.replication
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|replication
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|lang3
operator|.
name|builder
operator|.
name|EqualsBuilder
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
name|lang3
operator|.
name|builder
operator|.
name|HashCodeBuilder
import|;
end_import

begin_comment
comment|/**  * Wrapper class for hdds replication queue. Implements its natural  * ordering for priority queue.  */
end_comment

begin_class
DECL|class|ReplicationRequest
specifier|public
class|class
name|ReplicationRequest
implements|implements
name|Comparable
argument_list|<
name|ReplicationRequest
argument_list|>
implements|,
name|Serializable
block|{
DECL|field|containerId
specifier|private
specifier|final
name|long
name|containerId
decl_stmt|;
DECL|field|replicationCount
specifier|private
specifier|final
name|int
name|replicationCount
decl_stmt|;
DECL|field|expecReplicationCount
specifier|private
specifier|final
name|int
name|expecReplicationCount
decl_stmt|;
DECL|field|timestamp
specifier|private
specifier|final
name|long
name|timestamp
decl_stmt|;
DECL|method|ReplicationRequest (long containerId, int replicationCount, long timestamp, int expecReplicationCount)
specifier|public
name|ReplicationRequest
parameter_list|(
name|long
name|containerId
parameter_list|,
name|int
name|replicationCount
parameter_list|,
name|long
name|timestamp
parameter_list|,
name|int
name|expecReplicationCount
parameter_list|)
block|{
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
name|this
operator|.
name|replicationCount
operator|=
name|replicationCount
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
name|this
operator|.
name|expecReplicationCount
operator|=
name|expecReplicationCount
expr_stmt|;
block|}
DECL|method|ReplicationRequest (long containerId, int replicationCount, int expecReplicationCount)
specifier|public
name|ReplicationRequest
parameter_list|(
name|long
name|containerId
parameter_list|,
name|int
name|replicationCount
parameter_list|,
name|int
name|expecReplicationCount
parameter_list|)
block|{
name|this
argument_list|(
name|containerId
argument_list|,
name|replicationCount
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|expecReplicationCount
argument_list|)
expr_stmt|;
block|}
comment|/**    * Compares this object with the specified object for order.  Returns a    * negative integer, zero, or a positive integer as this object is less    * than, equal to, or greater than the specified object.    * @param o the object to be compared.    * @return a negative integer, zero, or a positive integer as this object    * is less than, equal to, or greater than the specified object.    * @throws NullPointerException if the specified object is null    * @throws ClassCastException   if the specified object's type prevents it    *                              from being compared to this object.    */
annotation|@
name|Override
DECL|method|compareTo (ReplicationRequest o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|ReplicationRequest
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|retVal
init|=
name|Integer
operator|.
name|compare
argument_list|(
name|getReplicationCount
argument_list|()
operator|-
name|getExpecReplicationCount
argument_list|()
argument_list|,
name|o
operator|.
name|getReplicationCount
argument_list|()
operator|-
name|o
operator|.
name|getExpecReplicationCount
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|retVal
operator|!=
literal|0
condition|)
block|{
return|return
name|retVal
return|;
block|}
return|return
name|Long
operator|.
name|compare
argument_list|(
name|getTimestamp
argument_list|()
argument_list|,
name|o
operator|.
name|getTimestamp
argument_list|()
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
operator|new
name|HashCodeBuilder
argument_list|(
literal|91
argument_list|,
literal|1011
argument_list|)
operator|.
name|append
argument_list|(
name|getContainerId
argument_list|()
argument_list|)
operator|.
name|toHashCode
argument_list|()
return|;
block|}
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
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ReplicationRequest
name|that
init|=
operator|(
name|ReplicationRequest
operator|)
name|o
decl_stmt|;
return|return
operator|new
name|EqualsBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|getContainerId
argument_list|()
argument_list|,
name|that
operator|.
name|getContainerId
argument_list|()
argument_list|)
operator|.
name|isEquals
argument_list|()
return|;
block|}
DECL|method|getContainerId ()
specifier|public
name|long
name|getContainerId
parameter_list|()
block|{
return|return
name|containerId
return|;
block|}
DECL|method|getReplicationCount ()
specifier|public
name|int
name|getReplicationCount
parameter_list|()
block|{
return|return
name|replicationCount
return|;
block|}
DECL|method|getTimestamp ()
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
DECL|method|getExpecReplicationCount ()
specifier|public
name|int
name|getExpecReplicationCount
parameter_list|()
block|{
return|return
name|expecReplicationCount
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ReplicationRequest{"
operator|+
literal|"containerId="
operator|+
name|containerId
operator|+
literal|", replicationCount="
operator|+
name|replicationCount
operator|+
literal|", expecReplicationCount="
operator|+
name|expecReplicationCount
operator|+
literal|", timestamp="
operator|+
name|timestamp
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

