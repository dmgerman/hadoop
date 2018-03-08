begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
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
name|federation
operator|.
name|router
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
name|net
operator|.
name|NetUtils
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
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_comment
comment|/**  * Test functionalities of {@link ConnectionManager}, which manages a pool  * of connections to NameNodes.  */
end_comment

begin_class
DECL|class|TestConnectionManager
specifier|public
class|class
name|TestConnectionManager
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|connManager
specifier|private
name|ConnectionManager
name|connManager
decl_stmt|;
DECL|field|TEST_GROUP
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|TEST_GROUP
init|=
operator|new
name|String
index|[]
block|{
literal|"TEST_GROUP"
block|}
decl_stmt|;
DECL|field|TEST_USER1
specifier|private
specifier|static
specifier|final
name|UserGroupInformation
name|TEST_USER1
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"user1"
argument_list|,
name|TEST_GROUP
argument_list|)
decl_stmt|;
DECL|field|TEST_USER2
specifier|private
specifier|static
specifier|final
name|UserGroupInformation
name|TEST_USER2
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"user2"
argument_list|,
name|TEST_GROUP
argument_list|)
decl_stmt|;
DECL|field|TEST_NN_ADDRESS
specifier|private
specifier|static
specifier|final
name|String
name|TEST_NN_ADDRESS
init|=
literal|"nn1:8080"
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|connManager
operator|=
operator|new
name|ConnectionManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|NetUtils
operator|.
name|addStaticResolution
argument_list|(
literal|"nn1"
argument_list|,
literal|"localhost"
argument_list|)
expr_stmt|;
name|NetUtils
operator|.
name|createSocketAddrForHost
argument_list|(
literal|"nn1"
argument_list|,
literal|8080
argument_list|)
expr_stmt|;
name|connManager
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|connManager
operator|!=
literal|null
condition|)
block|{
name|connManager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCleanup ()
specifier|public
name|void
name|testCleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|ConnectionPoolId
argument_list|,
name|ConnectionPool
argument_list|>
name|poolMap
init|=
name|connManager
operator|.
name|getPools
argument_list|()
decl_stmt|;
name|ConnectionPool
name|pool1
init|=
operator|new
name|ConnectionPool
argument_list|(
name|conf
argument_list|,
name|TEST_NN_ADDRESS
argument_list|,
name|TEST_USER1
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|addConnectionsToPool
argument_list|(
name|pool1
argument_list|,
literal|9
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|poolMap
operator|.
name|put
argument_list|(
operator|new
name|ConnectionPoolId
argument_list|(
name|TEST_USER1
argument_list|,
name|TEST_NN_ADDRESS
argument_list|)
argument_list|,
name|pool1
argument_list|)
expr_stmt|;
name|ConnectionPool
name|pool2
init|=
operator|new
name|ConnectionPool
argument_list|(
name|conf
argument_list|,
name|TEST_NN_ADDRESS
argument_list|,
name|TEST_USER2
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|addConnectionsToPool
argument_list|(
name|pool2
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|poolMap
operator|.
name|put
argument_list|(
operator|new
name|ConnectionPoolId
argument_list|(
name|TEST_USER2
argument_list|,
name|TEST_NN_ADDRESS
argument_list|)
argument_list|,
name|pool2
argument_list|)
expr_stmt|;
name|checkPoolConnections
argument_list|(
name|TEST_USER1
argument_list|,
literal|9
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|checkPoolConnections
argument_list|(
name|TEST_USER2
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Clean up first pool, one connection should be removed, and second pool
comment|// should remain the same.
name|connManager
operator|.
name|cleanup
argument_list|(
name|pool1
argument_list|)
expr_stmt|;
name|checkPoolConnections
argument_list|(
name|TEST_USER1
argument_list|,
literal|8
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|checkPoolConnections
argument_list|(
name|TEST_USER2
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Clean up the first pool again, it should have no effect since it reached
comment|// the MIN_ACTIVE_RATIO.
name|connManager
operator|.
name|cleanup
argument_list|(
name|pool1
argument_list|)
expr_stmt|;
name|checkPoolConnections
argument_list|(
name|TEST_USER1
argument_list|,
literal|8
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|checkPoolConnections
argument_list|(
name|TEST_USER2
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
DECL|method|addConnectionsToPool (ConnectionPool pool, int numTotalConn, int numActiveConn)
specifier|private
name|void
name|addConnectionsToPool
parameter_list|(
name|ConnectionPool
name|pool
parameter_list|,
name|int
name|numTotalConn
parameter_list|,
name|int
name|numActiveConn
parameter_list|)
throws|throws
name|IOException
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
name|numTotalConn
condition|;
name|i
operator|++
control|)
block|{
name|ConnectionContext
name|cc
init|=
name|pool
operator|.
name|newConnection
argument_list|()
decl_stmt|;
name|pool
operator|.
name|addConnection
argument_list|(
name|cc
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|numActiveConn
condition|)
block|{
name|cc
operator|.
name|getClient
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|checkPoolConnections (UserGroupInformation ugi, int numOfConns, int numOfActiveConns)
specifier|private
name|void
name|checkPoolConnections
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|,
name|int
name|numOfConns
parameter_list|,
name|int
name|numOfActiveConns
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ConnectionPoolId
argument_list|,
name|ConnectionPool
argument_list|>
name|e
range|:
name|connManager
operator|.
name|getPools
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|getUgi
argument_list|()
operator|==
name|ugi
condition|)
block|{
name|assertEquals
argument_list|(
name|numOfConns
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|getNumConnections
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numOfActiveConns
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|getNumActiveConnections
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

