begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.contract
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
name|fs
operator|.
name|contract
operator|.
name|AbstractContractCreateTest
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

begin_comment
comment|/**  * Contract test for create operation.  */
end_comment

begin_class
DECL|class|ITestAbfsFileSystemContractCreate
specifier|public
class|class
name|ITestAbfsFileSystemContractCreate
extends|extends
name|AbstractContractCreateTest
block|{
DECL|field|isSecure
specifier|private
specifier|final
name|boolean
name|isSecure
decl_stmt|;
DECL|field|binding
specifier|private
specifier|final
name|ABFSContractTestBinding
name|binding
decl_stmt|;
DECL|method|ITestAbfsFileSystemContractCreate ()
specifier|public
name|ITestAbfsFileSystemContractCreate
parameter_list|()
throws|throws
name|Exception
block|{
name|binding
operator|=
operator|new
name|ABFSContractTestBinding
argument_list|()
expr_stmt|;
name|this
operator|.
name|isSecure
operator|=
name|binding
operator|.
name|isSecureMode
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|binding
operator|.
name|setup
argument_list|()
expr_stmt|;
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createConfiguration ()
specifier|protected
name|Configuration
name|createConfiguration
parameter_list|()
block|{
return|return
name|binding
operator|.
name|getRawConfiguration
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createContract (final Configuration conf)
specifier|protected
name|AbstractFSContract
name|createContract
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|new
name|AbfsFileSystemContract
argument_list|(
name|conf
argument_list|,
name|isSecure
argument_list|)
return|;
block|}
block|}
end_class

end_unit

