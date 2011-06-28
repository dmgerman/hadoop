begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

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
name|Random
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
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|DatanodeDescriptor
import|;
end_import

begin_class
DECL|class|Host2NodesMap
class|class
name|Host2NodesMap
block|{
DECL|field|map
specifier|private
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
DECL|field|r
specifier|private
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|hostmapLock
specifier|private
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
name|host
init|=
name|node
operator|.
name|getHost
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
name|host
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
name|host
init|=
name|node
operator|.
name|getHost
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
name|host
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
name|host
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
name|host
init|=
name|node
operator|.
name|getHost
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
name|host
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
name|host
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
name|host
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
comment|/** get a data node by its host.    * @return DatanodeDescriptor if found; otherwise null.    */
DECL|method|getDatanodeByHost (String host)
name|DatanodeDescriptor
name|getDatanodeByHost
parameter_list|(
name|String
name|host
parameter_list|)
block|{
if|if
condition|(
name|host
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
name|host
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
name|r
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
comment|/**    * Find data node by its name.    *     * @return DatanodeDescriptor if found or null otherwise     */
DECL|method|getDatanodeByName (String name)
specifier|public
name|DatanodeDescriptor
name|getDatanodeByName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|colon
init|=
name|name
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|String
name|host
decl_stmt|;
if|if
condition|(
name|colon
operator|<
literal|0
condition|)
block|{
name|host
operator|=
name|name
expr_stmt|;
block|}
else|else
block|{
name|host
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colon
argument_list|)
expr_stmt|;
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
name|host
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
name|name
operator|.
name|equals
argument_list|(
name|containedNode
operator|.
name|getName
argument_list|()
argument_list|)
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
block|}
end_class

end_unit

