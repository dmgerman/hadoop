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
name|fail
import|;
end_import

begin_class
DECL|class|TestLocatedBlock
specifier|public
class|class
name|TestLocatedBlock
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestLocatedBlock
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testAddCachedLocWhenEmpty ()
specifier|public
name|void
name|testAddCachedLocWhenEmpty
parameter_list|()
block|{
name|DatanodeInfo
index|[]
name|ds
init|=
operator|new
name|DatanodeInfo
index|[
literal|0
index|]
decl_stmt|;
name|ExtendedBlock
name|b1
init|=
operator|new
name|ExtendedBlock
argument_list|(
literal|"bpid"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|LocatedBlock
name|l1
init|=
operator|new
name|LocatedBlock
argument_list|(
name|b1
argument_list|,
name|ds
argument_list|)
decl_stmt|;
name|DatanodeDescriptor
name|dn
init|=
operator|new
name|DatanodeDescriptor
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|"localhost"
argument_list|,
literal|"abcd"
argument_list|,
literal|5000
argument_list|,
literal|5001
argument_list|,
literal|5002
argument_list|,
literal|5003
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|l1
operator|.
name|addCachedLoc
argument_list|(
name|dn
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Adding dn when block is empty should throw"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Expected exception:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

