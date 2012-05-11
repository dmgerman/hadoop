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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSTestUtil
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
name|Test
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

begin_class
DECL|class|TestHost2NodesMap
specifier|public
class|class
name|TestHost2NodesMap
block|{
DECL|field|map
specifier|private
name|Host2NodesMap
name|map
init|=
operator|new
name|Host2NodesMap
argument_list|()
decl_stmt|;
DECL|field|dataNodes
specifier|private
name|DatanodeDescriptor
name|dataNodes
index|[]
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|dataNodes
operator|=
operator|new
name|DatanodeDescriptor
index|[]
block|{
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"1.1.1.1"
argument_list|,
literal|"/d1/r1"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"2.2.2.2"
argument_list|,
literal|"/d1/r1"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"3.3.3.3"
argument_list|,
literal|"/d1/r2"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"3.3.3.3"
argument_list|,
literal|5021
argument_list|,
literal|"/d1/r2"
argument_list|)
block|,     }
expr_stmt|;
for|for
control|(
name|DatanodeDescriptor
name|node
range|:
name|dataNodes
control|)
block|{
name|map
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|map
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContains ()
specifier|public
name|void
name|testContains
parameter_list|()
throws|throws
name|Exception
block|{
name|DatanodeDescriptor
name|nodeNotInMap
init|=
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"3.3.3.3"
argument_list|,
literal|"/d1/r4"
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
name|dataNodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|map
operator|.
name|contains
argument_list|(
name|dataNodes
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|map
operator|.
name|contains
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|map
operator|.
name|contains
argument_list|(
name|nodeNotInMap
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetDatanodeByHost ()
specifier|public
name|void
name|testGetDatanodeByHost
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|map
operator|.
name|getDatanodeByHost
argument_list|(
literal|"1.1.1.1"
argument_list|)
argument_list|,
name|dataNodes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|map
operator|.
name|getDatanodeByHost
argument_list|(
literal|"2.2.2.2"
argument_list|)
argument_list|,
name|dataNodes
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|DatanodeDescriptor
name|node
init|=
name|map
operator|.
name|getDatanodeByHost
argument_list|(
literal|"3.3.3.3"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|node
operator|==
name|dataNodes
index|[
literal|2
index|]
operator|||
name|node
operator|==
name|dataNodes
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|getDatanodeByHost
argument_list|(
literal|"4.4.4.4"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemove ()
specifier|public
name|void
name|testRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|DatanodeDescriptor
name|nodeNotInMap
init|=
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"3.3.3.3"
argument_list|,
literal|"/d1/r4"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|map
operator|.
name|remove
argument_list|(
name|nodeNotInMap
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|remove
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|getDatanodeByHost
argument_list|(
literal|"1.1.1.1."
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|getDatanodeByHost
argument_list|(
literal|"2.2.2.2"
argument_list|)
operator|==
name|dataNodes
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|DatanodeDescriptor
name|node
init|=
name|map
operator|.
name|getDatanodeByHost
argument_list|(
literal|"3.3.3.3"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|node
operator|==
name|dataNodes
index|[
literal|2
index|]
operator|||
name|node
operator|==
name|dataNodes
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|getDatanodeByHost
argument_list|(
literal|"4.4.4.4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|remove
argument_list|(
name|dataNodes
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|getDatanodeByHost
argument_list|(
literal|"1.1.1.1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|map
operator|.
name|getDatanodeByHost
argument_list|(
literal|"2.2.2.2"
argument_list|)
argument_list|,
name|dataNodes
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|map
operator|.
name|getDatanodeByHost
argument_list|(
literal|"3.3.3.3"
argument_list|)
argument_list|,
name|dataNodes
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|remove
argument_list|(
name|dataNodes
index|[
literal|3
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|getDatanodeByHost
argument_list|(
literal|"1.1.1.1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|map
operator|.
name|getDatanodeByHost
argument_list|(
literal|"2.2.2.2"
argument_list|)
argument_list|,
name|dataNodes
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|getDatanodeByHost
argument_list|(
literal|"3.3.3.3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|map
operator|.
name|remove
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|remove
argument_list|(
name|dataNodes
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|map
operator|.
name|remove
argument_list|(
name|dataNodes
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

