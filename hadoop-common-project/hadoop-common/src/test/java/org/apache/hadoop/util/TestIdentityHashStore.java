begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
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
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|util
operator|.
name|IdentityHashStore
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
name|util
operator|.
name|IdentityHashStore
operator|.
name|Visitor
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

begin_class
DECL|class|TestIdentityHashStore
specifier|public
class|class
name|TestIdentityHashStore
block|{
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
name|TestIdentityHashStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|class|Key
specifier|private
specifier|static
class|class
name|Key
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|Key (String name)
name|Key
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"should not be used!"
argument_list|)
throw|;
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
operator|!
operator|(
name|o
operator|instanceof
name|Key
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Key
name|other
init|=
operator|(
name|Key
operator|)
name|o
decl_stmt|;
return|return
name|name
operator|.
name|equals
argument_list|(
name|other
operator|.
name|name
argument_list|)
return|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testStartingWithZeroCapacity ()
specifier|public
name|void
name|testStartingWithZeroCapacity
parameter_list|()
block|{
name|IdentityHashStore
argument_list|<
name|Key
argument_list|,
name|Integer
argument_list|>
name|store
init|=
operator|new
name|IdentityHashStore
argument_list|<
name|Key
argument_list|,
name|Integer
argument_list|>
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|store
operator|.
name|visitAll
argument_list|(
operator|new
name|Visitor
argument_list|<
name|Key
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|Key
name|k
parameter_list|,
name|Integer
name|v
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"found key "
operator|+
name|k
operator|+
literal|" in empty IdentityHashStore."
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|store
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Key
name|key1
init|=
operator|new
name|Key
argument_list|(
literal|"key1"
argument_list|)
decl_stmt|;
name|Integer
name|value1
init|=
operator|new
name|Integer
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|store
operator|.
name|put
argument_list|(
name|key1
argument_list|,
name|value1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|!
name|store
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|value1
argument_list|,
name|store
operator|.
name|get
argument_list|(
name|key1
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|visitAll
argument_list|(
operator|new
name|Visitor
argument_list|<
name|Key
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|Key
name|k
parameter_list|,
name|Integer
name|v
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|key1
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|value1
argument_list|,
name|store
operator|.
name|remove
argument_list|(
name|key1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|store
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testDuplicateInserts ()
specifier|public
name|void
name|testDuplicateInserts
parameter_list|()
block|{
name|IdentityHashStore
argument_list|<
name|Key
argument_list|,
name|Integer
argument_list|>
name|store
init|=
operator|new
name|IdentityHashStore
argument_list|<
name|Key
argument_list|,
name|Integer
argument_list|>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|store
operator|.
name|visitAll
argument_list|(
operator|new
name|Visitor
argument_list|<
name|Key
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|Key
name|k
parameter_list|,
name|Integer
name|v
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"found key "
operator|+
name|k
operator|+
literal|" in empty IdentityHashStore."
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|store
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Key
name|key1
init|=
operator|new
name|Key
argument_list|(
literal|"key1"
argument_list|)
decl_stmt|;
name|Integer
name|value1
init|=
operator|new
name|Integer
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|Integer
name|value2
init|=
operator|new
name|Integer
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|Integer
name|value3
init|=
operator|new
name|Integer
argument_list|(
literal|300
argument_list|)
decl_stmt|;
name|store
operator|.
name|put
argument_list|(
name|key1
argument_list|,
name|value1
argument_list|)
expr_stmt|;
name|Key
name|equalToKey1
init|=
operator|new
name|Key
argument_list|(
literal|"key1"
argument_list|)
decl_stmt|;
comment|// IdentityHashStore compares by object equality, not equals()
name|Assert
operator|.
name|assertNull
argument_list|(
name|store
operator|.
name|get
argument_list|(
name|equalToKey1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|!
name|store
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|value1
argument_list|,
name|store
operator|.
name|get
argument_list|(
name|key1
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|put
argument_list|(
name|key1
argument_list|,
name|value2
argument_list|)
expr_stmt|;
name|store
operator|.
name|put
argument_list|(
name|key1
argument_list|,
name|value3
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|allValues
init|=
operator|new
name|LinkedList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|store
operator|.
name|visitAll
argument_list|(
operator|new
name|Visitor
argument_list|<
name|Key
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|Key
name|k
parameter_list|,
name|Integer
name|v
parameter_list|)
block|{
name|allValues
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|allValues
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|Integer
name|value
init|=
name|store
operator|.
name|remove
argument_list|(
name|key1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|allValues
operator|.
name|remove
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertNull
argument_list|(
name|store
operator|.
name|remove
argument_list|(
name|key1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|store
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testAdditionsAndRemovals ()
specifier|public
name|void
name|testAdditionsAndRemovals
parameter_list|()
block|{
name|IdentityHashStore
argument_list|<
name|Key
argument_list|,
name|Integer
argument_list|>
name|store
init|=
operator|new
name|IdentityHashStore
argument_list|<
name|Key
argument_list|,
name|Integer
argument_list|>
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|int
name|NUM_KEYS
init|=
literal|1000
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"generating "
operator|+
name|NUM_KEYS
operator|+
literal|" keys"
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Key
argument_list|>
name|keys
init|=
operator|new
name|ArrayList
argument_list|<
name|Key
argument_list|>
argument_list|(
name|NUM_KEYS
argument_list|)
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
name|NUM_KEYS
condition|;
name|i
operator|++
control|)
block|{
name|keys
operator|.
name|add
argument_list|(
operator|new
name|Key
argument_list|(
literal|"key "
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
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
name|NUM_KEYS
condition|;
name|i
operator|++
control|)
block|{
name|store
operator|.
name|put
argument_list|(
name|keys
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|visitAll
argument_list|(
operator|new
name|Visitor
argument_list|<
name|Key
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|Key
name|k
parameter_list|,
name|Integer
name|v
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|keys
operator|.
name|contains
argument_list|(
name|k
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_KEYS
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
name|store
operator|.
name|remove
argument_list|(
name|keys
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|visitAll
argument_list|(
operator|new
name|Visitor
argument_list|<
name|Key
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|Key
name|k
parameter_list|,
name|Integer
name|v
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected all entries to be removed"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"expected the store to be "
operator|+
literal|"empty, but found "
operator|+
name|store
operator|.
name|numElements
argument_list|()
operator|+
literal|" elements."
argument_list|,
name|store
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1024
argument_list|,
name|store
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

