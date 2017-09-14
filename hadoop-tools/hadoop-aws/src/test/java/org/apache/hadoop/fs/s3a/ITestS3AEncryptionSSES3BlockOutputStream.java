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

begin_comment
comment|/**  * Run the encryption tests against the block output stream.  */
end_comment

begin_class
DECL|class|ITestS3AEncryptionSSES3BlockOutputStream
specifier|public
class|class
name|ITestS3AEncryptionSSES3BlockOutputStream
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
name|FAST_UPLOAD_BUFFER
argument_list|,
name|Constants
operator|.
name|FAST_UPLOAD_BYTEBUFFER
argument_list|)
expr_stmt|;
comment|//must specify encryption key as empty because SSE-S3 does not allow it,
comment|//nor can it be null.
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
name|SSE_S3
return|;
block|}
block|}
end_class

end_unit

