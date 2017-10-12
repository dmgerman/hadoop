begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.ozone.contract
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|ozone
operator|.
name|contract
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
name|tools
operator|.
name|contract
operator|.
name|AbstractContractDistCpTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
comment|/**  * Contract test suite covering S3A integration with DistCp.  * Uses the block output stream, buffered to disk. This is the  * recommended output mechanism for DistCP due to its scalability.  */
end_comment

begin_class
DECL|class|ITestOzoneContractDistCp
specifier|public
class|class
name|ITestOzoneContractDistCp
extends|extends
name|AbstractContractDistCpTest
block|{
annotation|@
name|BeforeClass
DECL|method|createCluster ()
specifier|public
specifier|static
name|void
name|createCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|OzoneContract
operator|.
name|createCluster
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|teardownCluster ()
specifier|public
specifier|static
name|void
name|teardownCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|OzoneContract
operator|.
name|destroyCluster
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createContract (Configuration conf)
specifier|protected
name|OzoneContract
name|createContract
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|new
name|OzoneContract
argument_list|(
name|conf
argument_list|)
return|;
block|}
block|}
end_class

end_unit

