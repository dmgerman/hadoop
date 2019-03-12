begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|S3SecretValue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Interface to manager s3 secret.  */
end_comment

begin_interface
DECL|interface|S3SecretManager
specifier|public
interface|interface
name|S3SecretManager
block|{
DECL|method|getS3Secret (String kerberosID)
name|S3SecretValue
name|getS3Secret
parameter_list|(
name|String
name|kerberosID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * API to get s3 secret for given awsAccessKey.    * @param awsAccessKey    * */
DECL|method|getS3UserSecretString (String awsAccessKey)
name|String
name|getS3UserSecretString
parameter_list|(
name|String
name|awsAccessKey
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

