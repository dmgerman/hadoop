begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3
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
name|fs
operator|.
name|s3
operator|.
name|S3FileSystem
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
name|s3
operator|.
name|InMemoryFileSystemStore
import|;
end_import

begin_comment
comment|/**  * A helper implementation of {@link S3FileSystem}  * without actually connecting to S3 for unit testing.  */
end_comment

begin_class
DECL|class|S3InMemoryFileSystem
specifier|public
class|class
name|S3InMemoryFileSystem
extends|extends
name|S3FileSystem
block|{
DECL|method|S3InMemoryFileSystem ()
specifier|public
name|S3InMemoryFileSystem
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|InMemoryFileSystemStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

