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
name|HashSet
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
name|Set
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
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestName
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Validate randomly generated hierarchies, including fork() support in  * base class.  */
end_comment

begin_class
DECL|class|TestRandomTreeWalk
specifier|public
class|class
name|TestRandomTreeWalk
block|{
DECL|field|name
annotation|@
name|Rule
specifier|public
name|TestName
name|name
init|=
operator|new
name|TestName
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
annotation|@
name|Before
DECL|method|setSeed ()
specifier|public
name|void
name|setSeed
parameter_list|()
block|{
name|long
name|seed
init|=
name|r
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|r
operator|.
name|setSeed
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|name
operator|.
name|getMethodName
argument_list|()
operator|+
literal|" seed: "
operator|+
name|seed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRandomTreeWalkRepeat ()
specifier|public
name|void
name|testRandomTreeWalkRepeat
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|TreePath
argument_list|>
name|ns
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|long
name|seed
init|=
name|r
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|RandomTreeWalk
name|t1
init|=
operator|new
name|RandomTreeWalk
argument_list|(
name|seed
argument_list|,
literal|10
argument_list|,
literal|.1f
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TreePath
name|p
range|:
name|t1
control|)
block|{
name|p
operator|.
name|accept
argument_list|(
name|i
operator|++
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ns
operator|.
name|add
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|RandomTreeWalk
name|t2
init|=
operator|new
name|RandomTreeWalk
argument_list|(
name|seed
argument_list|,
literal|10
argument_list|,
literal|.1f
argument_list|)
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TreePath
name|p
range|:
name|t2
control|)
block|{
name|p
operator|.
name|accept
argument_list|(
name|j
operator|++
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ns
operator|.
name|remove
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|ns
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRandomTreeWalkFork ()
specifier|public
name|void
name|testRandomTreeWalkFork
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|FileStatus
argument_list|>
name|ns
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|long
name|seed
init|=
name|r
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|RandomTreeWalk
name|t1
init|=
operator|new
name|RandomTreeWalk
argument_list|(
name|seed
argument_list|,
literal|10
argument_list|,
literal|.15f
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TreePath
name|p
range|:
name|t1
control|)
block|{
name|p
operator|.
name|accept
argument_list|(
name|i
operator|++
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ns
operator|.
name|add
argument_list|(
name|p
operator|.
name|getFileStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|RandomTreeWalk
name|t2
init|=
operator|new
name|RandomTreeWalk
argument_list|(
name|seed
argument_list|,
literal|10
argument_list|,
literal|.15f
argument_list|)
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
name|ArrayList
argument_list|<
name|TreeWalk
operator|.
name|TreeIterator
argument_list|>
name|iters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|iters
operator|.
name|add
argument_list|(
name|t2
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|iters
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|TreeWalk
operator|.
name|TreeIterator
name|sub
init|=
name|iters
operator|.
name|remove
argument_list|(
name|iters
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
init|;
name|sub
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|TreePath
name|p
init|=
name|sub
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
literal|0
operator|==
operator|(
name|r
operator|.
name|nextInt
argument_list|()
operator|%
literal|4
operator|)
condition|)
block|{
name|iters
operator|.
name|add
argument_list|(
name|sub
operator|.
name|fork
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|iters
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
name|p
operator|.
name|accept
argument_list|(
name|j
operator|++
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ns
operator|.
name|remove
argument_list|(
name|p
operator|.
name|getFileStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|ns
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRandomRootWalk ()
specifier|public
name|void
name|testRandomRootWalk
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|FileStatus
argument_list|>
name|ns
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|long
name|seed
init|=
name|r
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
literal|"foo://bar:4344/dingos"
argument_list|)
decl_stmt|;
name|String
name|sroot
init|=
name|root
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|nroot
init|=
name|sroot
operator|.
name|length
argument_list|()
decl_stmt|;
name|RandomTreeWalk
name|t1
init|=
operator|new
name|RandomTreeWalk
argument_list|(
name|root
argument_list|,
name|seed
argument_list|,
literal|10
argument_list|,
literal|.1f
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TreePath
name|p
range|:
name|t1
control|)
block|{
name|p
operator|.
name|accept
argument_list|(
name|i
operator|++
argument_list|)
expr_stmt|;
name|FileStatus
name|stat
init|=
name|p
operator|.
name|getFileStatus
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|ns
operator|.
name|add
argument_list|(
name|stat
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sroot
argument_list|,
name|stat
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|nroot
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|RandomTreeWalk
name|t2
init|=
operator|new
name|RandomTreeWalk
argument_list|(
name|root
argument_list|,
name|seed
argument_list|,
literal|10
argument_list|,
literal|.1f
argument_list|)
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TreePath
name|p
range|:
name|t2
control|)
block|{
name|p
operator|.
name|accept
argument_list|(
name|j
operator|++
argument_list|)
expr_stmt|;
name|FileStatus
name|stat
init|=
name|p
operator|.
name|getFileStatus
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|ns
operator|.
name|remove
argument_list|(
name|stat
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sroot
argument_list|,
name|stat
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|nroot
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|ns
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

