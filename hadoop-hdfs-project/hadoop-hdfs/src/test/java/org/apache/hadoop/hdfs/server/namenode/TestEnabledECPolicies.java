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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
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
name|StripedFileTestUtil
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
name|SystemErasureCodingPolicies
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
name|test
operator|.
name|GenericTestUtils
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
name|Timeout
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
name|Set
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

begin_comment
comment|/**  * Test that ErasureCodingPolicyManager correctly parses the set of enabled  * erasure coding policies from configuration and exposes this information.  */
end_comment

begin_class
DECL|class|TestEnabledECPolicies
specifier|public
class|class
name|TestEnabledECPolicies
block|{
annotation|@
name|Rule
DECL|field|testTimeout
specifier|public
name|Timeout
name|testTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|60000
argument_list|)
decl_stmt|;
DECL|method|expectInvalidPolicy (String value)
specifier|private
name|void
name|expectInvalidPolicy
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_SYSTEM_DEFAULT_POLICY
argument_list|,
name|value
argument_list|)
expr_stmt|;
try|try
block|{
name|ErasureCodingPolicyManager
operator|.
name|getInstance
argument_list|()
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception when instantiating ECPolicyManager"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"is not a valid policy"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|expectValidPolicy (String value, final int numEnabled)
specifier|private
name|void
name|expectValidPolicy
parameter_list|(
name|String
name|value
parameter_list|,
specifier|final
name|int
name|numEnabled
parameter_list|)
throws|throws
name|Exception
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|ErasureCodingPolicyManager
name|manager
init|=
name|ErasureCodingPolicyManager
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|manager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|manager
operator|.
name|enablePolicy
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect number of enabled policies"
argument_list|,
name|numEnabled
argument_list|,
name|manager
operator|.
name|getEnabledPolicies
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultPolicy ()
specifier|public
name|void
name|testDefaultPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|String
name|defaultECPolicies
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_SYSTEM_DEFAULT_POLICY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_SYSTEM_DEFAULT_POLICY_DEFAULT
argument_list|)
decl_stmt|;
name|expectValidPolicy
argument_list|(
name|defaultECPolicies
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalid ()
specifier|public
name|void
name|testInvalid
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test first with an invalid policy
name|expectInvalidPolicy
argument_list|(
literal|"not-a-policy"
argument_list|)
expr_stmt|;
comment|// Test with an invalid policy and a valid policy
name|expectInvalidPolicy
argument_list|(
literal|"not-a-policy,"
operator|+
name|StripedFileTestUtil
operator|.
name|getDefaultECPolicy
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test with a valid and an invalid policy
name|expectInvalidPolicy
argument_list|(
name|StripedFileTestUtil
operator|.
name|getDefaultECPolicy
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|", not-a-policy"
argument_list|)
expr_stmt|;
comment|// Some more invalid values
name|expectInvalidPolicy
argument_list|(
literal|"not-a-policy, "
argument_list|)
expr_stmt|;
name|expectInvalidPolicy
argument_list|(
literal|"     ,not-a-policy, "
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValid ()
specifier|public
name|void
name|testValid
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|ecPolicyName
init|=
name|StripedFileTestUtil
operator|.
name|getDefaultECPolicy
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|expectValidPolicy
argument_list|(
name|ecPolicyName
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetPolicies ()
specifier|public
name|void
name|testGetPolicies
parameter_list|()
throws|throws
name|Exception
block|{
name|ErasureCodingPolicy
index|[]
name|enabledPolicies
decl_stmt|;
comment|// Enable no policies
name|enabledPolicies
operator|=
operator|new
name|ErasureCodingPolicy
index|[]
block|{}
expr_stmt|;
name|testGetPolicies
argument_list|(
name|enabledPolicies
argument_list|)
expr_stmt|;
comment|// Enable one policy
name|enabledPolicies
operator|=
operator|new
name|ErasureCodingPolicy
index|[]
block|{
name|SystemErasureCodingPolicies
operator|.
name|getPolicies
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
block|}
expr_stmt|;
name|testGetPolicies
argument_list|(
name|enabledPolicies
argument_list|)
expr_stmt|;
comment|// Enable two policies
name|enabledPolicies
operator|=
operator|new
name|ErasureCodingPolicy
index|[]
block|{
name|SystemErasureCodingPolicies
operator|.
name|getPolicies
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
block|,
name|SystemErasureCodingPolicies
operator|.
name|getPolicies
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
block|}
expr_stmt|;
name|testGetPolicies
argument_list|(
name|enabledPolicies
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetPolicies (ErasureCodingPolicy[] enabledPolicies)
specifier|private
name|void
name|testGetPolicies
parameter_list|(
name|ErasureCodingPolicy
index|[]
name|enabledPolicies
parameter_list|)
throws|throws
name|Exception
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|ErasureCodingPolicyManager
name|manager
init|=
name|ErasureCodingPolicyManager
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|manager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
for|for
control|(
name|ErasureCodingPolicy
name|p
range|:
name|enabledPolicies
control|)
block|{
name|manager
operator|.
name|enablePolicy
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Check that returned values are unique
name|Set
argument_list|<
name|String
argument_list|>
name|found
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ErasureCodingPolicy
name|p
range|:
name|manager
operator|.
name|getEnabledPolicies
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Duplicate policy name found: "
operator|+
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|found
operator|.
name|contains
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|found
operator|.
name|add
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Check that the policies specified in conf are found
for|for
control|(
name|ErasureCodingPolicy
name|p
range|:
name|enabledPolicies
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Did not find specified EC policy "
operator|+
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|found
operator|.
name|contains
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|enabledPolicies
operator|.
name|length
argument_list|,
name|found
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// Check that getEnabledPolicyByName only returns enabled policies
for|for
control|(
name|ErasureCodingPolicy
name|p
range|:
name|SystemErasureCodingPolicies
operator|.
name|getPolicies
argument_list|()
control|)
block|{
if|if
condition|(
name|found
operator|.
name|contains
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|// Enabled policy should be present
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"getEnabledPolicyByName did not find enabled policy"
operator|+
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|manager
operator|.
name|getEnabledPolicyByName
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Disabled policy should not be present
name|Assert
operator|.
name|assertNull
argument_list|(
literal|"getEnabledPolicyByName found disabled policy "
operator|+
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|manager
operator|.
name|getEnabledPolicyByName
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

