begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.adl.live
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|adl
operator|.
name|live
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
name|fs
operator|.
name|contract
operator|.
name|AbstractContractAppendTest
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
name|fs
operator|.
name|contract
operator|.
name|AbstractFSContract
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
name|fs
operator|.
name|contract
operator|.
name|ContractTestUtils
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
comment|/**  * Test Append on Adl file system.  */
end_comment

begin_class
DECL|class|TestAdlContractAppendLive
specifier|public
class|class
name|TestAdlContractAppendLive
extends|extends
name|AbstractContractAppendTest
block|{
annotation|@
name|Override
DECL|method|createContract (Configuration configuration)
specifier|protected
name|AbstractFSContract
name|createContract
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
block|{
return|return
operator|new
name|AdlStorageContract
argument_list|(
name|configuration
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Test
DECL|method|testRenameFileBeingAppended ()
specifier|public
name|void
name|testRenameFileBeingAppended
parameter_list|()
throws|throws
name|Throwable
block|{
name|ContractTestUtils
operator|.
name|unsupported
argument_list|(
literal|"Skipping since renaming file in append "
operator|+
literal|"mode not supported in Adl"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

