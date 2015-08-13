begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|fail
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
name|conf
operator|.
name|Configuration
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
name|DFSTestUtil
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
name|HdfsConfiguration
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
name|MiniDFSCluster
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
name|protocol
operator|.
name|DatanodeInfo
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
name|protocol
operator|.
name|HdfsConstants
operator|.
name|DatanodeReportType
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
name|protocol
operator|.
name|NamenodeProtocols
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

begin_class
DECL|class|TestNetworkTopology
specifier|public
class|class
name|TestNetworkTopology
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
name|TestNetworkTopology
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|final
specifier|static
name|NetworkTopology
name|cluster
init|=
operator|new
name|NetworkTopology
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
DECL|method|setupDatanodes ()
specifier|public
name|void
name|setupDatanodes
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
literal|"4.4.4.4"
argument_list|,
literal|"/d1/r2"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"5.5.5.5"
argument_list|,
literal|"/d1/r2"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"6.6.6.6"
argument_list|,
literal|"/d2/r3"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"7.7.7.7"
argument_list|,
literal|"/d2/r3"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"8.8.8.8"
argument_list|,
literal|"/d2/r3"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"9.9.9.9"
argument_list|,
literal|"/d3/r1"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"10.10.10.10"
argument_list|,
literal|"/d3/r1"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"11.11.11.11"
argument_list|,
literal|"/d3/r1"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"12.12.12.12"
argument_list|,
literal|"/d3/r2"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"13.13.13.13"
argument_list|,
literal|"/d3/r2"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"14.14.14.14"
argument_list|,
literal|"/d4/r1"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"15.15.15.15"
argument_list|,
literal|"/d4/r1"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"16.16.16.16"
argument_list|,
literal|"/d4/r1"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"17.17.17.17"
argument_list|,
literal|"/d4/r1"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"18.18.18.18"
argument_list|,
literal|"/d4/r1"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"19.19.19.19"
argument_list|,
literal|"/d4/r1"
argument_list|)
block|,
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"20.20.20.20"
argument_list|,
literal|"/d4/r1"
argument_list|)
block|,             }
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
name|dataNodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|cluster
operator|.
name|add
argument_list|(
name|dataNodes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|dataNodes
index|[
literal|9
index|]
operator|.
name|setDecommissioned
argument_list|()
expr_stmt|;
name|dataNodes
index|[
literal|10
index|]
operator|.
name|setDecommissioned
argument_list|()
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
literal|"8.8.8.8"
argument_list|,
literal|"/d2/r4"
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
name|cluster
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
name|cluster
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
DECL|method|testNumOfChildren ()
specifier|public
name|void
name|testNumOfChildren
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|cluster
operator|.
name|getNumOfLeaves
argument_list|()
argument_list|,
name|dataNodes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateInvalidTopology ()
specifier|public
name|void
name|testCreateInvalidTopology
parameter_list|()
throws|throws
name|Exception
block|{
name|NetworkTopology
name|invalCluster
init|=
operator|new
name|NetworkTopology
argument_list|()
decl_stmt|;
name|DatanodeDescriptor
name|invalDataNodes
index|[]
init|=
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
literal|"/d1"
argument_list|)
block|}
decl_stmt|;
name|invalCluster
operator|.
name|add
argument_list|(
name|invalDataNodes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|invalCluster
operator|.
name|add
argument_list|(
name|invalDataNodes
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
try|try
block|{
name|invalCluster
operator|.
name|add
argument_list|(
name|invalDataNodes
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected InvalidTopologyException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NetworkTopology
operator|.
name|InvalidTopologyException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Failed to add "
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"You cannot have a rack and a non-rack node at the same "
operator|+
literal|"level of the network topology."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRacks ()
specifier|public
name|void
name|testRacks
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|cluster
operator|.
name|getNumOfRacks
argument_list|()
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cluster
operator|.
name|isOnSameRack
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|dataNodes
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cluster
operator|.
name|isOnSameRack
argument_list|(
name|dataNodes
index|[
literal|1
index|]
argument_list|,
name|dataNodes
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cluster
operator|.
name|isOnSameRack
argument_list|(
name|dataNodes
index|[
literal|2
index|]
argument_list|,
name|dataNodes
index|[
literal|3
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cluster
operator|.
name|isOnSameRack
argument_list|(
name|dataNodes
index|[
literal|3
index|]
argument_list|,
name|dataNodes
index|[
literal|4
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cluster
operator|.
name|isOnSameRack
argument_list|(
name|dataNodes
index|[
literal|4
index|]
argument_list|,
name|dataNodes
index|[
literal|5
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cluster
operator|.
name|isOnSameRack
argument_list|(
name|dataNodes
index|[
literal|5
index|]
argument_list|,
name|dataNodes
index|[
literal|6
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetDistance ()
specifier|public
name|void
name|testGetDistance
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|cluster
operator|.
name|getDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|dataNodes
index|[
literal|0
index|]
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cluster
operator|.
name|getDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|dataNodes
index|[
literal|1
index|]
argument_list|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cluster
operator|.
name|getDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|dataNodes
index|[
literal|3
index|]
argument_list|)
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cluster
operator|.
name|getDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|dataNodes
index|[
literal|6
index|]
argument_list|)
argument_list|,
literal|6
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSortByDistance ()
specifier|public
name|void
name|testSortByDistance
parameter_list|()
throws|throws
name|Exception
block|{
name|DatanodeDescriptor
index|[]
name|testNodes
init|=
operator|new
name|DatanodeDescriptor
index|[
literal|3
index|]
decl_stmt|;
comment|// array contains both local node& local rack node
name|testNodes
index|[
literal|0
index|]
operator|=
name|dataNodes
index|[
literal|1
index|]
expr_stmt|;
name|testNodes
index|[
literal|1
index|]
operator|=
name|dataNodes
index|[
literal|2
index|]
expr_stmt|;
name|testNodes
index|[
literal|2
index|]
operator|=
name|dataNodes
index|[
literal|0
index|]
expr_stmt|;
name|cluster
operator|.
name|setRandomSeed
argument_list|(
literal|0xDEADBEEF
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|sortByDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|testNodes
argument_list|,
name|testNodes
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|0
index|]
operator|==
name|dataNodes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|1
index|]
operator|==
name|dataNodes
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|2
index|]
operator|==
name|dataNodes
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
comment|// array contains both local node& local rack node& decommissioned node
name|DatanodeDescriptor
index|[]
name|dtestNodes
init|=
operator|new
name|DatanodeDescriptor
index|[
literal|5
index|]
decl_stmt|;
name|dtestNodes
index|[
literal|0
index|]
operator|=
name|dataNodes
index|[
literal|8
index|]
expr_stmt|;
name|dtestNodes
index|[
literal|1
index|]
operator|=
name|dataNodes
index|[
literal|12
index|]
expr_stmt|;
name|dtestNodes
index|[
literal|2
index|]
operator|=
name|dataNodes
index|[
literal|11
index|]
expr_stmt|;
name|dtestNodes
index|[
literal|3
index|]
operator|=
name|dataNodes
index|[
literal|9
index|]
expr_stmt|;
name|dtestNodes
index|[
literal|4
index|]
operator|=
name|dataNodes
index|[
literal|10
index|]
expr_stmt|;
name|cluster
operator|.
name|setRandomSeed
argument_list|(
literal|0xDEADBEEF
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|sortByDistance
argument_list|(
name|dataNodes
index|[
literal|8
index|]
argument_list|,
name|dtestNodes
argument_list|,
name|dtestNodes
operator|.
name|length
operator|-
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dtestNodes
index|[
literal|0
index|]
operator|==
name|dataNodes
index|[
literal|8
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dtestNodes
index|[
literal|1
index|]
operator|==
name|dataNodes
index|[
literal|11
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dtestNodes
index|[
literal|2
index|]
operator|==
name|dataNodes
index|[
literal|12
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dtestNodes
index|[
literal|3
index|]
operator|==
name|dataNodes
index|[
literal|9
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dtestNodes
index|[
literal|4
index|]
operator|==
name|dataNodes
index|[
literal|10
index|]
argument_list|)
expr_stmt|;
comment|// array contains local node
name|testNodes
index|[
literal|0
index|]
operator|=
name|dataNodes
index|[
literal|1
index|]
expr_stmt|;
name|testNodes
index|[
literal|1
index|]
operator|=
name|dataNodes
index|[
literal|3
index|]
expr_stmt|;
name|testNodes
index|[
literal|2
index|]
operator|=
name|dataNodes
index|[
literal|0
index|]
expr_stmt|;
name|cluster
operator|.
name|setRandomSeed
argument_list|(
literal|0xDEADBEEF
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|sortByDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|testNodes
argument_list|,
name|testNodes
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|0
index|]
operator|==
name|dataNodes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|1
index|]
operator|==
name|dataNodes
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|2
index|]
operator|==
name|dataNodes
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
comment|// array contains local rack node
name|testNodes
index|[
literal|0
index|]
operator|=
name|dataNodes
index|[
literal|5
index|]
expr_stmt|;
name|testNodes
index|[
literal|1
index|]
operator|=
name|dataNodes
index|[
literal|3
index|]
expr_stmt|;
name|testNodes
index|[
literal|2
index|]
operator|=
name|dataNodes
index|[
literal|1
index|]
expr_stmt|;
name|cluster
operator|.
name|setRandomSeed
argument_list|(
literal|0xDEADBEEF
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|sortByDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|testNodes
argument_list|,
name|testNodes
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|0
index|]
operator|==
name|dataNodes
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|1
index|]
operator|==
name|dataNodes
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|2
index|]
operator|==
name|dataNodes
index|[
literal|5
index|]
argument_list|)
expr_stmt|;
comment|// array contains local rack node which happens to be in position 0
name|testNodes
index|[
literal|0
index|]
operator|=
name|dataNodes
index|[
literal|1
index|]
expr_stmt|;
name|testNodes
index|[
literal|1
index|]
operator|=
name|dataNodes
index|[
literal|5
index|]
expr_stmt|;
name|testNodes
index|[
literal|2
index|]
operator|=
name|dataNodes
index|[
literal|3
index|]
expr_stmt|;
name|cluster
operator|.
name|setRandomSeed
argument_list|(
literal|0xDEADBEEF
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|sortByDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|testNodes
argument_list|,
name|testNodes
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|0
index|]
operator|==
name|dataNodes
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|1
index|]
operator|==
name|dataNodes
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|2
index|]
operator|==
name|dataNodes
index|[
literal|5
index|]
argument_list|)
expr_stmt|;
comment|// Same as previous, but with a different random seed to test randomization
name|testNodes
index|[
literal|0
index|]
operator|=
name|dataNodes
index|[
literal|1
index|]
expr_stmt|;
name|testNodes
index|[
literal|1
index|]
operator|=
name|dataNodes
index|[
literal|5
index|]
expr_stmt|;
name|testNodes
index|[
literal|2
index|]
operator|=
name|dataNodes
index|[
literal|3
index|]
expr_stmt|;
name|cluster
operator|.
name|setRandomSeed
argument_list|(
literal|0xDEAD
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|sortByDistance
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|testNodes
argument_list|,
name|testNodes
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// sortByDistance does not take the "data center" layer into consideration
comment|// and it doesn't sort by getDistance, so 1, 5, 3 is also valid here
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|0
index|]
operator|==
name|dataNodes
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|1
index|]
operator|==
name|dataNodes
index|[
literal|5
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testNodes
index|[
literal|2
index|]
operator|==
name|dataNodes
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
comment|// Array of just rack-local nodes
comment|// Expect a random first node
name|DatanodeDescriptor
name|first
init|=
literal|null
decl_stmt|;
name|boolean
name|foundRandom
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|5
init|;
name|i
operator|<=
literal|7
condition|;
name|i
operator|++
control|)
block|{
name|testNodes
index|[
literal|0
index|]
operator|=
name|dataNodes
index|[
literal|5
index|]
expr_stmt|;
name|testNodes
index|[
literal|1
index|]
operator|=
name|dataNodes
index|[
literal|6
index|]
expr_stmt|;
name|testNodes
index|[
literal|2
index|]
operator|=
name|dataNodes
index|[
literal|7
index|]
expr_stmt|;
name|cluster
operator|.
name|sortByDistance
argument_list|(
name|dataNodes
index|[
name|i
index|]
argument_list|,
name|testNodes
argument_list|,
name|testNodes
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|first
operator|==
literal|null
condition|)
block|{
name|first
operator|=
name|testNodes
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|first
operator|!=
name|testNodes
index|[
literal|0
index|]
condition|)
block|{
name|foundRandom
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
name|assertTrue
argument_list|(
literal|"Expected to find a different first location"
argument_list|,
name|foundRandom
argument_list|)
expr_stmt|;
comment|// Array of just remote nodes
comment|// Expect random first node
name|first
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|testNodes
index|[
literal|0
index|]
operator|=
name|dataNodes
index|[
literal|13
index|]
expr_stmt|;
name|testNodes
index|[
literal|1
index|]
operator|=
name|dataNodes
index|[
literal|14
index|]
expr_stmt|;
name|testNodes
index|[
literal|2
index|]
operator|=
name|dataNodes
index|[
literal|15
index|]
expr_stmt|;
name|cluster
operator|.
name|sortByDistance
argument_list|(
name|dataNodes
index|[
name|i
index|]
argument_list|,
name|testNodes
argument_list|,
name|testNodes
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|first
operator|==
literal|null
condition|)
block|{
name|first
operator|=
name|testNodes
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|first
operator|!=
name|testNodes
index|[
literal|0
index|]
condition|)
block|{
name|foundRandom
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
name|assertTrue
argument_list|(
literal|"Expected to find a different first location"
argument_list|,
name|foundRandom
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
name|cluster
operator|.
name|remove
argument_list|(
name|dataNodes
index|[
name|i
index|]
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
name|dataNodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
name|cluster
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
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cluster
operator|.
name|getNumOfLeaves
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cluster
operator|.
name|clusterMap
operator|.
name|children
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
name|dataNodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|cluster
operator|.
name|add
argument_list|(
name|dataNodes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This picks a large number of nodes at random in order to ensure coverage    *     * @param numNodes the number of nodes    * @param excludedScope the excluded scope    * @return the frequency that nodes were chosen    */
DECL|method|pickNodesAtRandom (int numNodes, String excludedScope)
specifier|private
name|Map
argument_list|<
name|Node
argument_list|,
name|Integer
argument_list|>
name|pickNodesAtRandom
parameter_list|(
name|int
name|numNodes
parameter_list|,
name|String
name|excludedScope
parameter_list|)
block|{
name|Map
argument_list|<
name|Node
argument_list|,
name|Integer
argument_list|>
name|frequency
init|=
operator|new
name|HashMap
argument_list|<
name|Node
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeDescriptor
name|dnd
range|:
name|dataNodes
control|)
block|{
name|frequency
operator|.
name|put
argument_list|(
name|dnd
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numNodes
condition|;
name|j
operator|++
control|)
block|{
name|Node
name|random
init|=
name|cluster
operator|.
name|chooseRandom
argument_list|(
name|excludedScope
argument_list|)
decl_stmt|;
name|frequency
operator|.
name|put
argument_list|(
name|random
argument_list|,
name|frequency
operator|.
name|get
argument_list|(
name|random
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|frequency
return|;
block|}
comment|/**    * This test checks that chooseRandom works for an excluded node.    */
annotation|@
name|Test
DECL|method|testChooseRandomExcludedNode ()
specifier|public
name|void
name|testChooseRandomExcludedNode
parameter_list|()
block|{
name|String
name|scope
init|=
literal|"~"
operator|+
name|NodeBase
operator|.
name|getPath
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Node
argument_list|,
name|Integer
argument_list|>
name|frequency
init|=
name|pickNodesAtRandom
argument_list|(
literal|100
argument_list|,
name|scope
argument_list|)
decl_stmt|;
for|for
control|(
name|Node
name|key
range|:
name|dataNodes
control|)
block|{
comment|// all nodes except the first should be more than zero
name|assertTrue
argument_list|(
name|frequency
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|>
literal|0
operator|||
name|key
operator|==
name|dataNodes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This test checks that chooseRandom works for an excluded rack.    */
annotation|@
name|Test
DECL|method|testChooseRandomExcludedRack ()
specifier|public
name|void
name|testChooseRandomExcludedRack
parameter_list|()
block|{
name|Map
argument_list|<
name|Node
argument_list|,
name|Integer
argument_list|>
name|frequency
init|=
name|pickNodesAtRandom
argument_list|(
literal|100
argument_list|,
literal|"~"
operator|+
literal|"/d2"
argument_list|)
decl_stmt|;
comment|// all the nodes on the second rack should be zero
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|dataNodes
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|int
name|freq
init|=
name|frequency
operator|.
name|get
argument_list|(
name|dataNodes
index|[
name|j
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|dataNodes
index|[
name|j
index|]
operator|.
name|getNetworkLocation
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"/d2"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|freq
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|freq
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|180000
argument_list|)
DECL|method|testInvalidNetworkTopologiesNotCachedInHdfs ()
specifier|public
name|void
name|testInvalidNetworkTopologiesNotCachedInHdfs
parameter_list|()
throws|throws
name|Exception
block|{
comment|// start a cluster
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// bad rack topology
name|String
name|racks
index|[]
init|=
block|{
literal|"/a/b"
block|,
literal|"/c"
block|}
decl_stmt|;
name|String
name|hosts
index|[]
init|=
block|{
literal|"foo1.example.com"
block|,
literal|"foo2.example.com"
block|}
decl_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|2
argument_list|)
operator|.
name|racks
argument_list|(
name|racks
argument_list|)
operator|.
name|hosts
argument_list|(
name|hosts
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|NamenodeProtocols
name|nn
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|nn
argument_list|)
expr_stmt|;
comment|// Wait for one DataNode to register.
comment|// The other DataNode will not be able to register up because of the rack mismatch.
name|DatanodeInfo
index|[]
name|info
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|info
operator|=
name|nn
operator|.
name|getDatanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|LIVE
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|info
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|length
operator|==
literal|1
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
comment|// Set the network topology of the other node to the match the network
comment|// topology of the node that came up.
name|int
name|validIdx
init|=
name|info
index|[
literal|0
index|]
operator|.
name|getHostName
argument_list|()
operator|.
name|equals
argument_list|(
name|hosts
index|[
literal|0
index|]
argument_list|)
condition|?
literal|0
else|:
literal|1
decl_stmt|;
name|int
name|invalidIdx
init|=
name|validIdx
operator|==
literal|1
condition|?
literal|0
else|:
literal|1
decl_stmt|;
name|StaticMapping
operator|.
name|addNodeToRack
argument_list|(
name|hosts
index|[
name|invalidIdx
index|]
argument_list|,
name|racks
index|[
name|validIdx
index|]
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"datanode "
operator|+
name|validIdx
operator|+
literal|" came up with network location "
operator|+
name|info
index|[
literal|0
index|]
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
expr_stmt|;
comment|// Restart the DN with the invalid topology and wait for it to register.
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|invalidIdx
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|info
operator|=
name|nn
operator|.
name|getDatanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|LIVE
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|length
operator|==
literal|2
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|info
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"got no valid DNs"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|info
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"got one valid DN: "
operator|+
name|info
index|[
literal|0
index|]
operator|.
name|getHostName
argument_list|()
operator|+
literal|" (at "
operator|+
name|info
index|[
literal|0
index|]
operator|.
name|getNetworkLocation
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|info
index|[
literal|0
index|]
operator|.
name|getNetworkLocation
argument_list|()
argument_list|,
name|info
index|[
literal|1
index|]
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

