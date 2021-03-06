begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|ObjectMetadata
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|containsString
import|;
end_import

begin_comment
comment|/**  * Concrete class that extends {@link AbstractTestS3AEncryption}  * and tests SSE-KMS encryption when no KMS encryption key is provided and AWS  * uses the default.  Since this resource changes for every account and region,  * there is no good way to explicitly set this value to do a equality check  * in the response.  */
end_comment

begin_class
DECL|class|ITestS3AEncryptionSSEKMSDefaultKey
specifier|public
class|class
name|ITestS3AEncryptionSSEKMSDefaultKey
extends|extends
name|AbstractTestS3AEncryption
block|{
annotation|@
name|Override
DECL|method|createConfiguration ()
specifier|protected
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
name|Constants
operator|.
name|SERVER_SIDE_ENCRYPTION_KEY
argument_list|,
literal|""
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|getSSEAlgorithm ()
specifier|protected
name|S3AEncryptionMethods
name|getSSEAlgorithm
parameter_list|()
block|{
return|return
name|S3AEncryptionMethods
operator|.
name|SSE_KMS
return|;
block|}
annotation|@
name|Override
DECL|method|assertEncrypted (Path path)
specifier|protected
name|void
name|assertEncrypted
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|ObjectMetadata
name|md
init|=
name|getFileSystem
argument_list|()
operator|.
name|getObjectMetadata
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"SSE Algorithm"
argument_list|,
name|AWS_KMS_SSE_ALGORITHM
argument_list|,
name|md
operator|.
name|getSSEAlgorithm
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|md
operator|.
name|getSSEAwsKmsKeyId
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"arn:aws:kms:"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

