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

begin_comment
comment|/**  * Extends ITestAzureConcurrentOutOfBandIo in order to run testReadOOBWrites with secure mode  * (fs.azure.secure.mode) both enabled and disabled.  */
end_comment

begin_class
DECL|class|ITestAzureConcurrentOutOfBandIoWithSecureMode
specifier|public
class|class
name|ITestAzureConcurrentOutOfBandIoWithSecureMode
extends|extends
name|ITestAzureConcurrentOutOfBandIo
block|{
annotation|@
name|Override
DECL|method|createTestAccount ()
specifier|protected
name|AzureBlobStorageTestAccount
name|createTestAccount
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|AzureBlobStorageTestAccount
operator|.
name|createOutOfBandStore
argument_list|(
name|UPLOAD_BLOCK_SIZE
argument_list|,
name|DOWNLOAD_BLOCK_SIZE
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
end_class

end_unit

