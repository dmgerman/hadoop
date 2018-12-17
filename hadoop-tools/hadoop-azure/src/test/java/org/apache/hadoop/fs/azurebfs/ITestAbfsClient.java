begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
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
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|exceptions
operator|.
name|AbfsRestOperationException
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
name|azurebfs
operator|.
name|services
operator|.
name|AbfsClient
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
name|azurebfs
operator|.
name|services
operator|.
name|AbfsRestOperation
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
name|Test
import|;
end_import

begin_comment
comment|/**  * Test continuation token which has equal sign.  */
end_comment

begin_class
DECL|class|ITestAbfsClient
specifier|public
specifier|final
class|class
name|ITestAbfsClient
extends|extends
name|AbstractAbfsIntegrationTest
block|{
DECL|field|LIST_MAX_RESULTS
specifier|private
specifier|static
specifier|final
name|int
name|LIST_MAX_RESULTS
init|=
literal|500
decl_stmt|;
DECL|method|ITestAbfsClient ()
specifier|public
name|ITestAbfsClient
parameter_list|()
throws|throws
name|Exception
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContinuationTokenHavingEqualSign ()
specifier|public
name|void
name|testContinuationTokenHavingEqualSign
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|this
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|AbfsClient
name|abfsClient
init|=
name|fs
operator|.
name|getAbfsClient
argument_list|()
decl_stmt|;
try|try
block|{
name|AbfsRestOperation
name|op
init|=
name|abfsClient
operator|.
name|listPath
argument_list|(
literal|"/"
argument_list|,
literal|true
argument_list|,
name|LIST_MAX_RESULTS
argument_list|,
literal|"==========="
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AbfsRestOperationException
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"InvalidQueryParameterValue"
argument_list|,
name|ex
operator|.
name|getErrorCode
argument_list|()
operator|.
name|getErrorCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

