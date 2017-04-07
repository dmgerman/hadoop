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

begin_comment
comment|/**  * This tests read operation of DFS striped file with RS-10-4-64k  *  erasure code policy.  */
end_comment

begin_class
DECL|class|TestDFSRSDefault10x4StripedInputStream
specifier|public
class|class
name|TestDFSRSDefault10x4StripedInputStream
extends|extends
name|TestDFSStripedInputStream
block|{
DECL|method|getEcPolicy ()
specifier|public
name|ErasureCodingPolicy
name|getEcPolicy
parameter_list|()
block|{
return|return
name|SystemErasureCodingPolicies
operator|.
name|getByID
argument_list|(
name|SystemErasureCodingPolicies
operator|.
name|RS_10_4_POLICY_ID
argument_list|)
return|;
block|}
block|}
end_class

end_unit

