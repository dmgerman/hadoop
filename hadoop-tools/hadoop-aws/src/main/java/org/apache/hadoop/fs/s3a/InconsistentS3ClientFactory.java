begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|com
operator|.
name|amazonaws
operator|.
name|ClientConfiguration
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
comment|/**  * S3 Client factory used for testing with eventual consistency fault injection.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|InconsistentS3ClientFactory
specifier|public
class|class
name|InconsistentS3ClientFactory
extends|extends
name|DefaultS3ClientFactory
block|{
annotation|@
name|Override
DECL|method|newAmazonS3Client (AWSCredentialsProvider credentials, ClientConfiguration awsConf)
specifier|protected
name|AmazonS3
name|newAmazonS3Client
parameter_list|(
name|AWSCredentialsProvider
name|credentials
parameter_list|,
name|ClientConfiguration
name|awsConf
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"** FAILURE INJECTION ENABLED.  Do not run in production! **"
argument_list|)
expr_stmt|;
return|return
operator|new
name|InconsistentAmazonS3Client
argument_list|(
name|credentials
argument_list|,
name|awsConf
argument_list|,
name|getConf
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

