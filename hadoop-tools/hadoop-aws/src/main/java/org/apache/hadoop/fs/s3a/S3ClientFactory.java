begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|auth
operator|.
name|AWSCredentialsProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|AmazonS3
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Factory for creation of {@link AmazonS3} client instances.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|S3ClientFactory
specifier|public
interface|interface
name|S3ClientFactory
block|{
comment|/**    * Creates a new {@link AmazonS3} client.    *    * @param name raw input S3A file system URI    * @param bucket Optional bucket to use to look up per-bucket proxy secrets    * @param credentialSet credentials to use    * @param userAgentSuffix optional suffix for the UA field.    * @return S3 client    * @throws IOException IO problem    */
DECL|method|createS3Client (URI name, final String bucket, final AWSCredentialsProvider credentialSet, final String userAgentSuffix)
name|AmazonS3
name|createS3Client
parameter_list|(
name|URI
name|name
parameter_list|,
specifier|final
name|String
name|bucket
parameter_list|,
specifier|final
name|AWSCredentialsProvider
name|credentialSet
parameter_list|,
specifier|final
name|String
name|userAgentSuffix
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

