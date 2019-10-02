begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.auth
package|package
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
name|auth
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
name|s3a
operator|.
name|auth
operator|.
name|delegation
operator|.
name|DelegationTokenProvider
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
name|S3AFileSystem
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
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_comment
comment|/**  * Interface which can be implemented to allow initialization of any custom  * signers which may be used by the {@link S3AFileSystem}.  */
end_comment

begin_interface
DECL|interface|AwsSignerInitializer
specifier|public
interface|interface
name|AwsSignerInitializer
block|{
comment|/**    * Register a store instance.    *    * @param bucketName the bucket name    * @param storeConf the store configuration    * @param dtProvider delegation token provider for the store    * @param storeUgi ugi under which the store is operating    */
DECL|method|registerStore (String bucketName, Configuration storeConf, DelegationTokenProvider dtProvider, UserGroupInformation storeUgi)
name|void
name|registerStore
parameter_list|(
name|String
name|bucketName
parameter_list|,
name|Configuration
name|storeConf
parameter_list|,
name|DelegationTokenProvider
name|dtProvider
parameter_list|,
name|UserGroupInformation
name|storeUgi
parameter_list|)
function_decl|;
comment|/**    * Unregister a store instance.    *    * @param bucketName the bucket name    * @param storeConf the store configuration    * @param dtProvider delegation token provider for the store    * @param storeUgi ugi under which the store is operating    */
DECL|method|unregisterStore (String bucketName, Configuration storeConf, DelegationTokenProvider dtProvider, UserGroupInformation storeUgi)
name|void
name|unregisterStore
parameter_list|(
name|String
name|bucketName
parameter_list|,
name|Configuration
name|storeConf
parameter_list|,
name|DelegationTokenProvider
name|dtProvider
parameter_list|,
name|UserGroupInformation
name|storeUgi
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

