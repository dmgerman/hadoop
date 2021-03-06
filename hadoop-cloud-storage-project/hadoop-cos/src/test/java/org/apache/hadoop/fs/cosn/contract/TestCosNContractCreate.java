begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.cosn.contract
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|cosn
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
comment|/**  * CosN contract tests for creating files.  */
end_comment

begin_class
DECL|class|TestCosNContractCreate
specifier|public
class|class
name|TestCosNContractCreate
extends|extends
name|AbstractContractCreateTest
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
name|CosNContract
argument_list|(
name|configuration
argument_list|)
return|;
block|}
block|}
end_class

end_unit

