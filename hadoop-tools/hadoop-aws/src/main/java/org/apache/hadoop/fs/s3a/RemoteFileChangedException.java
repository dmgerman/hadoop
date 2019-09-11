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
comment|/**  * Indicates the S3 object is out of sync with the expected version.  Thrown in  * cases such as when the object is updated while an {@link S3AInputStream} is  * open, or when a file expected was never found.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|RemoteFileChangedException
specifier|public
class|class
name|RemoteFileChangedException
extends|extends
name|PathIOException
block|{
DECL|field|PRECONDITIONS_FAILED
specifier|public
specifier|static
specifier|final
name|String
name|PRECONDITIONS_FAILED
init|=
literal|"Constraints of request were unsatisfiable"
decl_stmt|;
comment|/**    * While trying to get information on a file known to S3Guard, the    * file never became visible in S3.    */
DECL|field|FILE_NEVER_FOUND
specifier|public
specifier|static
specifier|final
name|String
name|FILE_NEVER_FOUND
init|=
literal|"File to rename not found on guarded S3 store after repeated attempts"
decl_stmt|;
comment|/**    * The file wasn't found in rename after a single attempt -the unguarded    * codepath.    */
DECL|field|FILE_NOT_FOUND_SINGLE_ATTEMPT
specifier|public
specifier|static
specifier|final
name|String
name|FILE_NOT_FOUND_SINGLE_ATTEMPT
init|=
literal|"File to rename not found on unguarded S3 store"
decl_stmt|;
comment|/**    * Constructs a RemoteFileChangedException.    *    * @param path the path accessed when the change was detected    * @param operation the operation (e.g. open, re-open) performed when the    * change was detected    * @param message a message providing more details about the condition    */
DECL|method|RemoteFileChangedException (String path, String operation, String message)
specifier|public
name|RemoteFileChangedException
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|setOperation
argument_list|(
name|operation
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a RemoteFileChangedException.    *    * @param path the path accessed when the change was detected    * @param operation the operation (e.g. open, re-open) performed when the    * change was detected    * @param message a message providing more details about the condition    * @param cause inner cause.    */
DECL|method|RemoteFileChangedException (String path, String operation, String message, Throwable cause)
specifier|public
name|RemoteFileChangedException
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|setOperation
argument_list|(
name|operation
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

