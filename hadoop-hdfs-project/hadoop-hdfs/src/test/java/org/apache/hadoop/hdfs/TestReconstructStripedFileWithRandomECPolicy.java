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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * This test extends TestReconstructStripedFile to use a random  * (non-default) EC policy.  */
end_comment

begin_class
DECL|class|TestReconstructStripedFileWithRandomECPolicy
specifier|public
class|class
name|TestReconstructStripedFileWithRandomECPolicy
extends|extends
name|TestReconstructStripedFile
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestReconstructStripedFileWithRandomECPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ecPolicy
specifier|private
name|ErasureCodingPolicy
name|ecPolicy
decl_stmt|;
DECL|method|TestReconstructStripedFileWithRandomECPolicy ()
specifier|public
name|TestReconstructStripedFileWithRandomECPolicy
parameter_list|()
block|{
comment|// If you want to debug this test with a specific ec policy, please use
comment|// SystemErasureCodingPolicies class.
comment|// e.g. ecPolicy = SystemErasureCodingPolicies.getByID(RS_3_2_POLICY_ID);
name|ecPolicy
operator|=
name|StripedFileTestUtil
operator|.
name|getRandomNonDefaultECPolicy
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"run {} with {}."
argument_list|,
name|TestReconstructStripedFileWithRandomECPolicy
operator|.
name|class
operator|.
name|getSuperclass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|ecPolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEcPolicy ()
specifier|public
name|ErasureCodingPolicy
name|getEcPolicy
parameter_list|()
block|{
return|return
name|ecPolicy
return|;
block|}
block|}
end_class

end_unit

