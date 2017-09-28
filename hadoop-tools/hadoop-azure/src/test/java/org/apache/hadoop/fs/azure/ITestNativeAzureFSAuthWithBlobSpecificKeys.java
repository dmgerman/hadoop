begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
operator|.
name|SecureStorageInterfaceImpl
operator|.
name|KEY_USE_CONTAINER_SASKEY_FOR_ALL_ACCESS
import|;
end_import

begin_comment
comment|/**  * Test class to hold all WASB authorization tests that use blob-specific keys  * to access storage.  */
end_comment

begin_class
DECL|class|ITestNativeAzureFSAuthWithBlobSpecificKeys
specifier|public
class|class
name|ITestNativeAzureFSAuthWithBlobSpecificKeys
extends|extends
name|TestNativeAzureFileSystemAuthorization
block|{
annotation|@
name|Override
DECL|method|createConfiguration ()
specifier|public
name|Configuration
name|createConfiguration
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|super
operator|.
name|createConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KEY_USE_CONTAINER_SASKEY_FOR_ALL_ACCESS
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
block|}
end_class

end_unit

