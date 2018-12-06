begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|assertNull
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
name|protocol
operator|.
name|AddErasureCodingPolicyResponse
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
name|ErasureCodingPolicy
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
name|io
operator|.
name|erasurecode
operator|.
name|ECSchema
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

begin_comment
comment|/**  * Test that ensures addition of user defined EC policies is allowed only when  * dfs.namenode.ec.userdefined.policy.allowed is set to true.  */
end_comment

begin_class
DECL|class|TestErasureCodingAddConfig
specifier|public
class|class
name|TestErasureCodingAddConfig
block|{
annotation|@
name|Test
DECL|method|testECAddPolicyConfigDisable ()
specifier|public
name|void
name|testECAddPolicyConfigDisable
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_POLICIES_USERPOLICIES_ALLOWED_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
init|(
name|MiniDFSCluster
name|cluster
init|=
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
literal|0
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|ErasureCodingPolicy
name|newPolicy1
init|=
operator|new
name|ErasureCodingPolicy
argument_list|(
operator|new
name|ECSchema
argument_list|(
literal|"rs"
argument_list|,
literal|5
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
name|AddErasureCodingPolicyResponse
index|[]
name|response
init|=
name|fs
operator|.
name|addErasureCodingPolicies
argument_list|(
operator|new
name|ErasureCodingPolicy
index|[]
block|{
name|newPolicy1
block|}
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|response
index|[
literal|0
index|]
operator|.
name|isSucceed
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Addition of user defined erasure coding policy is disabled."
argument_list|,
name|response
index|[
literal|0
index|]
operator|.
name|getErrorMsg
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testECAddPolicyConfigEnable ()
specifier|public
name|void
name|testECAddPolicyConfigEnable
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_POLICIES_USERPOLICIES_ALLOWED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
init|(
name|MiniDFSCluster
name|cluster
init|=
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
literal|0
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|ErasureCodingPolicy
name|newPolicy1
init|=
operator|new
name|ErasureCodingPolicy
argument_list|(
operator|new
name|ECSchema
argument_list|(
literal|"rs"
argument_list|,
literal|5
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
name|AddErasureCodingPolicyResponse
index|[]
name|response
init|=
name|fs
operator|.
name|addErasureCodingPolicies
argument_list|(
operator|new
name|ErasureCodingPolicy
index|[]
block|{
name|newPolicy1
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|response
index|[
literal|0
index|]
operator|.
name|isSucceed
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|response
index|[
literal|0
index|]
operator|.
name|getErrorMsg
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

