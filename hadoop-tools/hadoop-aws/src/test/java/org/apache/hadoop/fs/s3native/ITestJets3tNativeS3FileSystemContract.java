begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3native
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3native
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

begin_comment
comment|/**  * S3N basic contract tests through live S3 service.  */
end_comment

begin_class
DECL|class|ITestJets3tNativeS3FileSystemContract
specifier|public
class|class
name|ITestJets3tNativeS3FileSystemContract
extends|extends
name|NativeS3FileSystemContractBaseTest
block|{
annotation|@
name|Override
DECL|method|getNativeFileSystemStore ()
name|NativeFileSystemStore
name|getNativeFileSystemStore
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|Jets3tNativeFileSystemStore
argument_list|()
return|;
block|}
block|}
end_class

end_unit

