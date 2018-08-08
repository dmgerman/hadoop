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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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
name|fs
operator|.
name|contract
operator|.
name|AbstractContractSetTimesTest
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
comment|/**  * Contract test for setTimes operation.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|ITestAbfsFileSystemContractSetTimes
specifier|public
class|class
name|ITestAbfsFileSystemContractSetTimes
extends|extends
name|AbstractContractSetTimesTest
block|{
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"SecureMode={0}"
argument_list|)
DECL|method|secure ()
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Object
index|[]
argument_list|>
name|secure
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|true
block|}
block|,
block|{
literal|false
block|}
block|}
argument_list|)
return|;
block|}
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
DECL|method|ITestAbfsFileSystemContractSetTimes (final boolean secure)
specifier|public
name|ITestAbfsFileSystemContractSetTimes
parameter_list|(
specifier|final
name|boolean
name|secure
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|isSecure
operator|=
name|secure
expr_stmt|;
name|binding
operator|=
operator|new
name|ABFSContractTestBinding
argument_list|(
name|this
operator|.
name|isSecure
argument_list|)
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
name|getConfiguration
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

