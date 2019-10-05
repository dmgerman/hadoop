begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.contract.s3a
package|package
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
name|s3a
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
name|Path
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
name|AbstractBondedFSContract
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
name|s3a
operator|.
name|S3ATestUtils
import|;
end_import

begin_comment
comment|/**  * The contract of S3A: only enabled if the test bucket is provided.  */
end_comment

begin_class
DECL|class|S3AContract
specifier|public
class|class
name|S3AContract
extends|extends
name|AbstractBondedFSContract
block|{
comment|/**    * Test resource with the contract bindings used in the standard    * contract tests: {@value}.    */
DECL|field|CONTRACT_XML
specifier|public
specifier|static
specifier|final
name|String
name|CONTRACT_XML
init|=
literal|"contract/s3a.xml"
decl_stmt|;
comment|/**    * Instantiate, adding the s3a.xml contract file.    * This may force a reload of the entire configuration, so interferes with    * any code which has removed bucket overrides.    * @param conf configuration.    */
DECL|method|S3AContract (Configuration conf)
specifier|public
name|S3AContract
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
argument_list|(
name|conf
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Instantiate, optionally adding the s3a.xml contract file.    * This may force a reload of the entire configuration, so interferes with    * any code which has removed bucket overrides.    * @param conf configuration.    * @param addContractResource should the s3a.xml file be added?    */
DECL|method|S3AContract (Configuration conf, boolean addContractResource)
specifier|public
name|S3AContract
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|boolean
name|addContractResource
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|//insert the base features
if|if
condition|(
name|addContractResource
condition|)
block|{
name|addConfResource
argument_list|(
name|CONTRACT_XML
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getScheme ()
specifier|public
name|String
name|getScheme
parameter_list|()
block|{
return|return
literal|"s3a"
return|;
block|}
annotation|@
name|Override
DECL|method|getTestPath ()
specifier|public
name|Path
name|getTestPath
parameter_list|()
block|{
return|return
name|S3ATestUtils
operator|.
name|createTestPath
argument_list|(
name|super
operator|.
name|getTestPath
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

