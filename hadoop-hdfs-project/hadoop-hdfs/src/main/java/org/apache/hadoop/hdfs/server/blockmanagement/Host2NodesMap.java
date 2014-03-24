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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSUtil
import|;
end_import

begin_comment
comment|/** A map from host names to datanode descriptors. */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|Host2NodesMap
class|class
name|Host2NodesMap
block|{
DECL|field|map
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|DatanodeDescriptor
index|[]
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|DatanodeDescriptor
index|[]
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|hostmapLock
specifier|private
specifier|final
name|ReadWriteLock
name|hostmapLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
comment|/** Check if node is already in the map. */
DECL|method|contains (DatanodeDescriptor node)
name|boolean
name|contains
parameter_list|(
name|DatanodeDescriptor
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|ipAddr
init|=
name|node
operator|.
name|getIpAddr
argument_list|()
decl_stmt|;
name|hostmapLock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|DatanodeDescriptor
index|[]
name|nodes
init|=
name|map
operator|.
name|get
argument_list|(
name|ipAddr
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|DatanodeDescriptor
name|containedNode
range|:
name|nodes
control|)
block|{
if|if
condition|(
name|node
operator|==
name|containedNode
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|hostmapLock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/** add node to the map     * return true if the node is added; false otherwise.    */
DECL|method|add (DatanodeDescriptor node)
name|boolean
name|add
parameter_list|(
name|DatanodeDescriptor
name|node
parameter_list|)
block|{
name|hostmapLock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|node
operator|==
literal|null
operator|||
name|contains
argument_list|(
name|node
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|ipAddr
init|=
name|node
operator|.
name|getIpAddr
argument_list|()
decl_stmt|;
name|DatanodeDescriptor
index|[]
name|nodes
init|=
name|map
operator|.
name|get
argument_list|(
name|ipAddr
argument_list|)
decl_stmt|;
name|DatanodeDescriptor
index|[]
name|newNodes
decl_stmt|;
if|if
condition|(
name|nodes
operator|==
literal|null
condition|)
block|{
name|newNodes
operator|=
operator|new
name|DatanodeDescriptor
index|[
literal|1
index|]
expr_stmt|;
name|newNodes
index|[
literal|0
index|]
operator|=
name|node
expr_stmt|;
block|}
else|else
block|{
comment|// rare case: more than one datanode on the host
name|newNodes
operator|=
operator|new
name|DatanodeDescriptor
index|[
name|nodes
operator|.
name|length
operator|+
literal|1
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|nodes
argument_list|,
literal|0
argument_list|,
name|newNodes
argument_list|,
literal|0
argument_list|,
name|nodes
operator|.
name|length
argument_list|)
expr_stmt|;
name|newNodes
index|[
name|nodes
operator|.
name|length
index|]
operator|=
name|node
expr_stmt|;
block|}
name|map
operator|.
name|put
argument_list|(
name|ipAddr
argument_list|,
name|newNodes
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
finally|finally
block|{
name|hostmapLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** remove node from the map     * return true if the node is removed; false otherwise.    */
DECL|method|remove (DatanodeDescriptor node)
name|boolean
name|remove
parameter_list|(
name|DatanodeDescriptor
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|ipAddr
init|=
name|node
operator|.
name|getIpAddr
argument_list|()
decl_stmt|;
name|hostmapLock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|DatanodeDescriptor
index|[]
name|nodes
init|=
name|map
operator|.
name|get
argument_list|(
name|ipAddr
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|nodes
operator|.
name|length
operator|==
literal|1
condition|)
block|{
if|if
condition|(
name|nodes
index|[
literal|0
index|]
operator|==
name|node
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|ipAddr
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|//rare case
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|nodes
index|[
name|i
index|]
operator|==
name|node
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|i
operator|==
name|nodes
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|DatanodeDescriptor
index|[]
name|newNodes
decl_stmt|;
name|newNodes
operator|=
operator|new
name|DatanodeDescriptor
index|[
name|nodes
operator|.
name|length
operator|-
literal|1
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|nodes
argument_list|,
literal|0
argument_list|,
name|newNodes
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|nodes
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|newNodes
argument_list|,
name|i
argument_list|,
name|nodes
operator|.
name|length
operator|-
name|i
operator|-
literal|1
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|ipAddr
argument_list|,
name|newNodes
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
finally|finally
block|{
name|hostmapLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get a data node by its IP address.    * @return DatanodeDescriptor if found, null otherwise     */
DECL|method|getDatanodeByHost (String ipAddr)
name|DatanodeDescriptor
name|getDatanodeByHost
parameter_list|(
name|String
name|ipAddr
parameter_list|)
block|{
if|if
condition|(
name|ipAddr
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|hostmapLock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|DatanodeDescriptor
index|[]
name|nodes
init|=
name|map
operator|.
name|get
argument_list|(
name|ipAddr
argument_list|)
decl_stmt|;
comment|// no entry
if|if
condition|(
name|nodes
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// one node
if|if
condition|(
name|nodes
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|nodes
index|[
literal|0
index|]
return|;
block|}
comment|// more than one node
return|return
name|nodes
index|[
name|DFSUtil
operator|.
name|getRandom
argument_list|()
operator|.
name|nextInt
argument_list|(
name|nodes
operator|.
name|length
argument_list|)
index|]
return|;
block|}
finally|finally
block|{
name|hostmapLock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Find data node by its transfer address    *    * @return DatanodeDescriptor if found or null otherwise    */
DECL|method|getDatanodeByXferAddr (String ipAddr, int xferPort)
specifier|public
name|DatanodeDescriptor
name|getDatanodeByXferAddr
parameter_list|(
name|String
name|ipAddr
parameter_list|,
name|int
name|xferPort
parameter_list|)
block|{
if|if
condition|(
name|ipAddr
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|hostmapLock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|DatanodeDescriptor
index|[]
name|nodes
init|=
name|map
operator|.
name|get
argument_list|(
name|ipAddr
argument_list|)
decl_stmt|;
comment|// no entry
if|if
condition|(
name|nodes
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
for|for
control|(
name|DatanodeDescriptor
name|containedNode
range|:
name|nodes
control|)
block|{
if|if
condition|(
name|xferPort
operator|==
name|containedNode
operator|.
name|getXferPort
argument_list|()
condition|)
block|{
return|return
name|containedNode
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|hostmapLock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|DatanodeDescriptor
index|[]
argument_list|>
name|e
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"\n  "
operator|+
name|e
operator|.
name|getKey
argument_list|()
operator|+
literal|" => "
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|append
argument_list|(
literal|"\n]"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

