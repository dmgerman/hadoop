begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.raid
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|raid
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

begin_class
DECL|class|TestBlockFixerGeneratedBlockDist
specifier|public
class|class
name|TestBlockFixerGeneratedBlockDist
extends|extends
name|TestBlockFixer
block|{
comment|/**    * Tests integrity of generated block.    * Create a file and delete a block entirely. Wait for the block to be    * regenerated. Now stop RaidNode and corrupt the generated block.    * Test that corruption in the generated block can be detected by clients.    */
annotation|@
name|Test
DECL|method|testGeneratedBlockDist ()
specifier|public
name|void
name|testGeneratedBlockDist
parameter_list|()
throws|throws
name|Exception
block|{
name|generatedBlockTestCommon
argument_list|(
literal|"testGeneratedBlock"
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests integrity of generated last block.    * Create a file and delete a block entirely. Wait for the block to be    * regenerated. Now stop RaidNode and corrupt the generated block.    * Test that corruption in the generated block can be detected by clients.    */
annotation|@
name|Test
DECL|method|testGeneratedLastBlockDist ()
specifier|public
name|void
name|testGeneratedLastBlockDist
parameter_list|()
throws|throws
name|Exception
block|{
name|generatedBlockTestCommon
argument_list|(
literal|"testGeneratedLastBlock"
argument_list|,
literal|6
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

