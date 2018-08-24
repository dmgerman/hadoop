begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.services
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
name|services
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
name|azurebfs
operator|.
name|contracts
operator|.
name|exceptions
operator|.
name|KeyProviderException
import|;
end_import

begin_comment
comment|/**  * The interface that every Azure file system key provider must implement.  */
end_comment

begin_interface
DECL|interface|KeyProvider
specifier|public
interface|interface
name|KeyProvider
block|{
comment|/**    * Key providers must implement this method. Given a list of configuration    * parameters for the specified Azure storage account, retrieve the plaintext    * storage account key.    *    * @param accountName    *          the storage account name    * @param conf    *          Hadoop configuration parameters    * @return the plaintext storage account key    * @throws KeyProviderException if an error occurs while attempting to get    *         the storage account key.    */
DECL|method|getStorageAccountKey (String accountName, Configuration conf)
name|String
name|getStorageAccountKey
parameter_list|(
name|String
name|accountName
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|KeyProviderException
function_decl|;
block|}
end_interface

end_unit

