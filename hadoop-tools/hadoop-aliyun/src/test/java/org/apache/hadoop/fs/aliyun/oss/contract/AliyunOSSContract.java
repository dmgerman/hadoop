begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.aliyun.oss.contract
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|aliyun
operator|.
name|oss
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

begin_comment
comment|/**  * The contract of Aliyun OSS: only enabled if the test bucket is provided.  */
end_comment

begin_class
DECL|class|AliyunOSSContract
specifier|public
class|class
name|AliyunOSSContract
extends|extends
name|AbstractBondedFSContract
block|{
DECL|field|CONTRACT_XML
specifier|public
specifier|static
specifier|final
name|String
name|CONTRACT_XML
init|=
literal|"contract/aliyun-oss.xml"
decl_stmt|;
DECL|method|AliyunOSSContract (Configuration conf)
specifier|public
name|AliyunOSSContract
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|//insert the base features
name|addConfResource
argument_list|(
name|CONTRACT_XML
argument_list|)
expr_stmt|;
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
literal|"oss"
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
name|String
name|testUniqueForkId
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.unique.fork.id"
argument_list|)
decl_stmt|;
return|return
name|testUniqueForkId
operator|==
literal|null
condition|?
name|super
operator|.
name|getTestPath
argument_list|()
else|:
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|testUniqueForkId
argument_list|,
literal|"test"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

