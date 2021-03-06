begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

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

begin_comment
comment|/**  * A Unit-test to test bringup and shutdown of Mini Map-Reduce Cluster.  */
end_comment

begin_class
DECL|class|TestMiniMRBringup
specifier|public
class|class
name|TestMiniMRBringup
block|{
annotation|@
name|Test
DECL|method|testBringUp ()
specifier|public
name|void
name|testBringUp
parameter_list|()
throws|throws
name|IOException
block|{
name|MiniMRCluster
name|mr
init|=
literal|null
decl_stmt|;
try|try
block|{
name|mr
operator|=
operator|new
name|MiniMRCluster
argument_list|(
literal|1
argument_list|,
literal|"local"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|mr
operator|!=
literal|null
condition|)
block|{
name|mr
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

