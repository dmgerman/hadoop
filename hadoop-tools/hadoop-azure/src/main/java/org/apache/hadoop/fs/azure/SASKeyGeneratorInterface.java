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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  * Iterface used by AzureNativeFileSysteStore to retrieve SAS Keys for the  * respective azure storage entity. This interface is expected to be  * implemented in two modes:  * 1) Local Mode: In this mode SAS Keys are generated  *    in same address space as the WASB. This will be primarily used for  *    testing purposes.  * 2) Remote Mode: In this mode SAS Keys are generated in a sepearte process  *    other than WASB and will be communicated via client.  */
end_comment

begin_interface
DECL|interface|SASKeyGeneratorInterface
specifier|public
interface|interface
name|SASKeyGeneratorInterface
block|{
comment|/**    * Interface method to retrieve SAS Key for a container within the storage    * account.    *    * @param accountName    *          - Storage account name    * @param container    *          - Container name within the storage account.    * @return SAS URI for the container.    * @throws SASKeyGenerationException    */
DECL|method|getContainerSASUri (String accountName, String container)
name|URI
name|getContainerSASUri
parameter_list|(
name|String
name|accountName
parameter_list|,
name|String
name|container
parameter_list|)
throws|throws
name|SASKeyGenerationException
function_decl|;
comment|/**    * Interface method to retrieve SAS Key for a blob within the container of the    * storage account.    *    * @param accountName    *          - Storage account name    * @param container    *          - Container name within the storage account.    * @param relativePath    *          - Relative path within the container    * @return SAS URI for the relative path blob.    * @throws SASKeyGenerationException    */
DECL|method|getRelativeBlobSASUri (String accountName, String container, String relativePath)
name|URI
name|getRelativeBlobSASUri
parameter_list|(
name|String
name|accountName
parameter_list|,
name|String
name|container
parameter_list|,
name|String
name|relativePath
parameter_list|)
throws|throws
name|SASKeyGenerationException
function_decl|;
block|}
end_interface

end_unit

