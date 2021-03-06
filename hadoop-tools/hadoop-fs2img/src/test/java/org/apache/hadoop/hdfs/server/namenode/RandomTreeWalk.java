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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Random
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
name|fs
operator|.
name|BlockLocation
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
name|fs
operator|.
name|FileStatus
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
name|fs
operator|.
name|LocatedFileStatus
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
name|fs
operator|.
name|Path
import|;
end_import

begin_comment
comment|/**  * Random, repeatable hierarchy generator.  */
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
DECL|class|RandomTreeWalk
specifier|public
class|class
name|RandomTreeWalk
extends|extends
name|TreeWalk
block|{
DECL|field|root
specifier|private
specifier|final
name|Path
name|root
decl_stmt|;
DECL|field|seed
specifier|private
specifier|final
name|long
name|seed
decl_stmt|;
DECL|field|depth
specifier|private
specifier|final
name|float
name|depth
decl_stmt|;
DECL|field|children
specifier|private
specifier|final
name|int
name|children
decl_stmt|;
DECL|field|mSeed
specifier|private
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|mSeed
decl_stmt|;
DECL|method|RandomTreeWalk (long seed)
name|RandomTreeWalk
parameter_list|(
name|long
name|seed
parameter_list|)
block|{
name|this
argument_list|(
name|seed
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
DECL|method|RandomTreeWalk (long seed, int children)
name|RandomTreeWalk
parameter_list|(
name|long
name|seed
parameter_list|,
name|int
name|children
parameter_list|)
block|{
name|this
argument_list|(
name|seed
argument_list|,
name|children
argument_list|,
literal|0.15f
argument_list|)
expr_stmt|;
block|}
DECL|method|RandomTreeWalk (long seed, int children, float depth)
name|RandomTreeWalk
parameter_list|(
name|long
name|seed
parameter_list|,
name|int
name|children
parameter_list|,
name|float
name|depth
parameter_list|)
block|{
name|this
argument_list|(
name|randomRoot
argument_list|(
name|seed
argument_list|)
argument_list|,
name|seed
argument_list|,
name|children
argument_list|,
name|depth
argument_list|)
expr_stmt|;
block|}
DECL|method|RandomTreeWalk (Path root, long seed, int children, float depth)
name|RandomTreeWalk
parameter_list|(
name|Path
name|root
parameter_list|,
name|long
name|seed
parameter_list|,
name|int
name|children
parameter_list|,
name|float
name|depth
parameter_list|)
block|{
name|this
operator|.
name|seed
operator|=
name|seed
expr_stmt|;
name|this
operator|.
name|depth
operator|=
name|depth
expr_stmt|;
name|this
operator|.
name|children
operator|=
name|children
expr_stmt|;
name|mSeed
operator|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|mSeed
operator|.
name|put
argument_list|(
operator|-
literal|1L
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
block|}
DECL|method|randomRoot (long seed)
specifier|static
name|Path
name|randomRoot
parameter_list|(
name|long
name|seed
parameter_list|)
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|String
name|scheme
decl_stmt|;
do|do
block|{
name|scheme
operator|=
name|genName
argument_list|(
name|r
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|Character
operator|.
name|isDigit
argument_list|(
name|scheme
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
do|;
name|String
name|authority
init|=
name|genName
argument_list|(
name|r
argument_list|,
literal|3
argument_list|,
literal|15
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|int
name|port
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|1
operator|<<
literal|13
argument_list|)
operator|+
literal|1000
decl_stmt|;
return|return
operator|new
name|Path
argument_list|(
name|scheme
argument_list|,
name|authority
operator|+
literal|":"
operator|+
name|port
argument_list|,
literal|"/"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|TreeIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|RandomTreeIterator
argument_list|(
name|seed
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getChildren (TreePath p, long id, TreeIterator walk)
specifier|protected
name|Iterable
argument_list|<
name|TreePath
argument_list|>
name|getChildren
parameter_list|(
name|TreePath
name|p
parameter_list|,
name|long
name|id
parameter_list|,
name|TreeIterator
name|walk
parameter_list|)
block|{
specifier|final
name|FileStatus
name|pFs
init|=
name|p
operator|.
name|getFileStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|pFs
operator|.
name|isFile
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|// seed is f(parent seed, attrib)
name|long
name|cseed
init|=
name|mSeed
operator|.
name|get
argument_list|(
name|p
operator|.
name|getParentId
argument_list|()
argument_list|)
operator|*
name|p
operator|.
name|getFileStatus
argument_list|()
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|mSeed
operator|.
name|put
argument_list|(
name|p
operator|.
name|getId
argument_list|()
argument_list|,
name|cseed
argument_list|)
expr_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
name|cseed
argument_list|)
decl_stmt|;
name|int
name|nChildren
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|children
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|TreePath
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<
name|TreePath
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nChildren
condition|;
operator|++
name|i
control|)
block|{
name|ret
operator|.
name|add
argument_list|(
operator|new
name|TreePath
argument_list|(
name|genFileStatus
argument_list|(
name|p
argument_list|,
name|r
argument_list|)
argument_list|,
name|p
operator|.
name|getId
argument_list|()
argument_list|,
name|walk
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|genFileStatus (TreePath parent, Random r)
name|FileStatus
name|genFileStatus
parameter_list|(
name|TreePath
name|parent
parameter_list|,
name|Random
name|r
parameter_list|)
block|{
specifier|final
name|int
name|blocksize
init|=
literal|128
operator|*
operator|(
literal|1
operator|<<
literal|20
operator|)
decl_stmt|;
specifier|final
name|Path
name|name
decl_stmt|;
specifier|final
name|boolean
name|isDir
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|parent
condition|)
block|{
name|name
operator|=
name|root
expr_stmt|;
name|isDir
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|Path
name|p
init|=
name|parent
operator|.
name|getFileStatus
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|name
operator|=
operator|new
name|Path
argument_list|(
name|p
argument_list|,
name|genName
argument_list|(
name|r
argument_list|,
literal|3
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|isDir
operator|=
name|r
operator|.
name|nextFloat
argument_list|()
operator|<
name|depth
expr_stmt|;
block|}
specifier|final
name|long
name|len
init|=
name|isDir
condition|?
literal|0
else|:
name|r
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
specifier|final
name|int
name|nblocks
init|=
literal|0
operator|==
name|len
condition|?
literal|0
else|:
operator|(
operator|(
call|(
name|int
call|)
argument_list|(
operator|(
name|len
operator|-
literal|1
operator|)
operator|/
name|blocksize
argument_list|)
operator|)
operator|+
literal|1
operator|)
decl_stmt|;
name|BlockLocation
index|[]
name|blocks
init|=
name|genBlocks
argument_list|(
name|r
argument_list|,
name|nblocks
argument_list|,
name|blocksize
argument_list|,
name|len
argument_list|)
decl_stmt|;
return|return
operator|new
name|LocatedFileStatus
argument_list|(
operator|new
name|FileStatus
argument_list|(
name|len
argument_list|,
comment|/* long length,             */
name|isDir
argument_list|,
comment|/* boolean isdir,           */
literal|1
argument_list|,
comment|/* int block_replication,   */
name|blocksize
argument_list|,
comment|/* long blocksize,          */
literal|0L
argument_list|,
comment|/* long modification_time,  */
literal|0L
argument_list|,
comment|/* long access_time,        */
literal|null
argument_list|,
comment|/* FsPermission permission, */
literal|"hadoop"
argument_list|,
comment|/* String owner,            */
literal|"hadoop"
argument_list|,
comment|/* String group,            */
name|name
argument_list|)
argument_list|,
comment|/* Path path                */
name|blocks
argument_list|)
return|;
block|}
DECL|method|genBlocks (Random r, int nblocks, int blocksize, long len)
name|BlockLocation
index|[]
name|genBlocks
parameter_list|(
name|Random
name|r
parameter_list|,
name|int
name|nblocks
parameter_list|,
name|int
name|blocksize
parameter_list|,
name|long
name|len
parameter_list|)
block|{
name|BlockLocation
index|[]
name|blocks
init|=
operator|new
name|BlockLocation
index|[
name|nblocks
index|]
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|nblocks
condition|)
block|{
return|return
name|blocks
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nblocks
operator|-
literal|1
condition|;
operator|++
name|i
control|)
block|{
name|blocks
index|[
name|i
index|]
operator|=
operator|new
name|BlockLocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|i
operator|*
name|blocksize
argument_list|,
name|blocksize
argument_list|)
expr_stmt|;
block|}
name|blocks
index|[
name|nblocks
operator|-
literal|1
index|]
operator|=
operator|new
name|BlockLocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|(
name|nblocks
operator|-
literal|1
operator|)
operator|*
name|blocksize
argument_list|,
literal|0
operator|==
operator|(
name|len
operator|%
name|blocksize
operator|)
condition|?
name|blocksize
else|:
name|len
operator|%
name|blocksize
argument_list|)
expr_stmt|;
return|return
name|blocks
return|;
block|}
DECL|method|genName (Random r, int min, int max)
specifier|static
name|String
name|genName
parameter_list|(
name|Random
name|r
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
name|int
name|len
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|max
operator|-
name|min
operator|+
literal|1
argument_list|)
operator|+
name|min
decl_stmt|;
name|char
index|[]
name|ret
init|=
operator|new
name|char
index|[
name|len
index|]
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|int
name|c
init|=
name|r
operator|.
name|nextInt
argument_list|()
operator|&
literal|0x7F
decl_stmt|;
comment|// restrict to ASCII
if|if
condition|(
name|Character
operator|.
name|isLetterOrDigit
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|ret
index|[
operator|--
name|len
index|]
operator|=
operator|(
name|char
operator|)
name|c
expr_stmt|;
block|}
block|}
return|return
operator|new
name|String
argument_list|(
name|ret
argument_list|)
return|;
block|}
DECL|class|RandomTreeIterator
class|class
name|RandomTreeIterator
extends|extends
name|TreeIterator
block|{
DECL|method|RandomTreeIterator ()
name|RandomTreeIterator
parameter_list|()
block|{     }
DECL|method|RandomTreeIterator (long seed)
name|RandomTreeIterator
parameter_list|(
name|long
name|seed
parameter_list|)
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|FileStatus
name|iroot
init|=
name|genFileStatus
argument_list|(
literal|null
argument_list|,
name|r
argument_list|)
decl_stmt|;
name|getPendingQueue
argument_list|()
operator|.
name|addFirst
argument_list|(
operator|new
name|TreePath
argument_list|(
name|iroot
argument_list|,
operator|-
literal|1
argument_list|,
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|RandomTreeIterator (TreePath p)
name|RandomTreeIterator
parameter_list|(
name|TreePath
name|p
parameter_list|)
block|{
name|getPendingQueue
argument_list|()
operator|.
name|addFirst
argument_list|(
operator|new
name|TreePath
argument_list|(
name|p
operator|.
name|getFileStatus
argument_list|()
argument_list|,
name|p
operator|.
name|getParentId
argument_list|()
argument_list|,
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fork ()
specifier|public
name|TreeIterator
name|fork
parameter_list|()
block|{
if|if
condition|(
name|getPendingQueue
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|RandomTreeIterator
argument_list|()
return|;
block|}
return|return
operator|new
name|RandomTreeIterator
argument_list|(
name|getPendingQueue
argument_list|()
operator|.
name|removeFirst
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

