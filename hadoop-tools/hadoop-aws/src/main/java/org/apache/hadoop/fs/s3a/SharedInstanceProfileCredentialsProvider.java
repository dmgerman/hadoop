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
name|com
operator|.
name|amazonaws
operator|.
name|auth
operator|.
name|InstanceProfileCredentialsProvider
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
comment|/**  * A subclass of {@link InstanceProfileCredentialsProvider} that enforces  * instantiation of only a single instance.  * This credential provider calls the EC2 instance metadata service to obtain  * credentials.  For highly multi-threaded applications, it's possible that  * multiple instances call the service simultaneously and overwhelm it with  * load.  The service handles this by throttling the client with an HTTP 429  * response or forcibly terminating the connection.  Forcing use of a single  * instance reduces load on the metadata service by allowing all threads to  * share the credentials.  The base class is thread-safe, and there is nothing  * that varies in the credentials across different instances of  * {@link S3AFileSystem} connecting to different buckets, so sharing a singleton  * instance is safe.  *  * As of AWS SDK 1.11.39, the SDK code internally enforces a singleton.  After  * Hadoop upgrades to that version or higher, it's likely that we can remove  * this class.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|SharedInstanceProfileCredentialsProvider
specifier|public
specifier|final
class|class
name|SharedInstanceProfileCredentialsProvider
extends|extends
name|InstanceProfileCredentialsProvider
block|{
DECL|field|INSTANCE
specifier|private
specifier|static
specifier|final
name|SharedInstanceProfileCredentialsProvider
name|INSTANCE
init|=
operator|new
name|SharedInstanceProfileCredentialsProvider
argument_list|()
decl_stmt|;
comment|/**    * Returns the singleton instance.    *    * @return singleton instance    */
DECL|method|getInstance ()
specifier|public
specifier|static
name|SharedInstanceProfileCredentialsProvider
name|getInstance
parameter_list|()
block|{
return|return
name|INSTANCE
return|;
block|}
comment|/**    * Default constructor, defined explicitly as private to enforce singleton.    */
DECL|method|SharedInstanceProfileCredentialsProvider ()
specifier|private
name|SharedInstanceProfileCredentialsProvider
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

