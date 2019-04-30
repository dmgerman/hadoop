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
name|fs
operator|.
name|PathIOException
import|;
end_import

begin_comment
comment|/**  * Indicates the metadata associated with the given Path could not be persisted  * to the metadata store (e.g. S3Guard / DynamoDB).  When this occurs, the  * file itself has been successfully written to S3, but the metadata may be out  * of sync.  The metadata can be corrected with the "s3guard import" command  * provided by {@link org.apache.hadoop.fs.s3a.s3guard.S3GuardTool}.  */
end_comment

begin_class
DECL|class|MetadataPersistenceException
specifier|public
class|class
name|MetadataPersistenceException
extends|extends
name|PathIOException
block|{
comment|/**    * Constructs a MetadataPersistenceException.    * @param path path of the affected file    * @param cause cause of the issue    */
DECL|method|MetadataPersistenceException (String path, Throwable cause)
specifier|public
name|MetadataPersistenceException
parameter_list|(
name|String
name|path
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

