begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|assertFalse
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
DECL|class|TestExtendedBlock
specifier|public
class|class
name|TestExtendedBlock
block|{
DECL|field|POOL_A
specifier|static
specifier|final
name|String
name|POOL_A
init|=
literal|"blockpool-a"
decl_stmt|;
DECL|field|POOL_B
specifier|static
specifier|final
name|String
name|POOL_B
init|=
literal|"blockpool-b"
decl_stmt|;
DECL|field|BLOCK_1_GS1
specifier|static
specifier|final
name|Block
name|BLOCK_1_GS1
init|=
operator|new
name|Block
argument_list|(
literal|1L
argument_list|,
literal|100L
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
DECL|field|BLOCK_1_GS2
specifier|static
specifier|final
name|Block
name|BLOCK_1_GS2
init|=
operator|new
name|Block
argument_list|(
literal|1L
argument_list|,
literal|100L
argument_list|,
literal|2L
argument_list|)
decl_stmt|;
DECL|field|BLOCK_2_GS1
specifier|static
specifier|final
name|Block
name|BLOCK_2_GS1
init|=
operator|new
name|Block
argument_list|(
literal|2L
argument_list|,
literal|100L
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testEquals ()
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
comment|// Same block -> equal
name|assertEquals
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
name|POOL_A
argument_list|,
name|BLOCK_1_GS1
argument_list|)
argument_list|,
operator|new
name|ExtendedBlock
argument_list|(
name|POOL_A
argument_list|,
name|BLOCK_1_GS1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Different pools, same block id -> not equal
name|assertNotEquals
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
name|POOL_A
argument_list|,
name|BLOCK_1_GS1
argument_list|)
argument_list|,
operator|new
name|ExtendedBlock
argument_list|(
name|POOL_B
argument_list|,
name|BLOCK_1_GS1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Same pool, different block id -> not equal
name|assertNotEquals
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
name|POOL_A
argument_list|,
name|BLOCK_1_GS1
argument_list|)
argument_list|,
operator|new
name|ExtendedBlock
argument_list|(
name|POOL_A
argument_list|,
name|BLOCK_2_GS1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Same block, different genstamps -> equal
name|assertEquals
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
name|POOL_A
argument_list|,
name|BLOCK_1_GS1
argument_list|)
argument_list|,
operator|new
name|ExtendedBlock
argument_list|(
name|POOL_A
argument_list|,
name|BLOCK_1_GS2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHashcode ()
specifier|public
name|void
name|testHashcode
parameter_list|()
block|{
comment|// Different pools, same block id -> different hashcode
name|assertNotEquals
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
name|POOL_A
argument_list|,
name|BLOCK_1_GS1
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|,
operator|new
name|ExtendedBlock
argument_list|(
name|POOL_B
argument_list|,
name|BLOCK_1_GS1
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// Same pool, different block id -> different hashcode
name|assertNotEquals
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
name|POOL_A
argument_list|,
name|BLOCK_1_GS1
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|,
operator|new
name|ExtendedBlock
argument_list|(
name|POOL_A
argument_list|,
name|BLOCK_2_GS1
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// Same block -> same hashcode
name|assertEquals
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
name|POOL_A
argument_list|,
name|BLOCK_1_GS1
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|,
operator|new
name|ExtendedBlock
argument_list|(
name|POOL_A
argument_list|,
name|BLOCK_1_GS1
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNotEquals (Object a, Object b)
specifier|private
specifier|static
name|void
name|assertNotEquals
parameter_list|(
name|Object
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
block|{
name|assertFalse
argument_list|(
literal|"expected not equal: '"
operator|+
name|a
operator|+
literal|"' and '"
operator|+
name|b
operator|+
literal|"'"
argument_list|,
name|a
operator|.
name|equals
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

