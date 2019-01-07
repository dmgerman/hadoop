begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.auth
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
operator|.
name|auth
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|AmazonClientException
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
name|AWSCredentials
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
comment|/**  * This is going to be an IAM credential provider which performs  * async refresh for lower-latency on IO calls.  * Initially it does not do this, simply shares the single IAM instance  * across all instances. This makes it less expensive to declare.  *  */
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
DECL|class|IAMInstanceCredentialsProvider
specifier|public
class|class
name|IAMInstanceCredentialsProvider
implements|implements
name|AWSCredentialsProvider
implements|,
name|Closeable
block|{
DECL|field|INSTANCE
specifier|private
specifier|static
specifier|final
name|InstanceProfileCredentialsProvider
name|INSTANCE
init|=
name|InstanceProfileCredentialsProvider
operator|.
name|getInstance
argument_list|()
decl_stmt|;
DECL|method|IAMInstanceCredentialsProvider ()
specifier|public
name|IAMInstanceCredentialsProvider
parameter_list|()
block|{   }
comment|/**    * Ask for the credentials.    * as it invariably means "you aren't running on EC2"    * @return the credentials    */
annotation|@
name|Override
DECL|method|getCredentials ()
specifier|public
name|AWSCredentials
name|getCredentials
parameter_list|()
block|{
try|try
block|{
return|return
name|INSTANCE
operator|.
name|getCredentials
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|AmazonClientException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NoAwsCredentialsException
argument_list|(
literal|"IAMInstanceCredentialsProvider"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|refresh ()
specifier|public
name|void
name|refresh
parameter_list|()
block|{
name|INSTANCE
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// until async, no-op.
block|}
block|}
end_class

end_unit

