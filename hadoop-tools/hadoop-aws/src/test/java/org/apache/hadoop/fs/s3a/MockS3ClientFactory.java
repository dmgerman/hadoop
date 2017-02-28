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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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

begin_comment
comment|/**  * An {@link S3ClientFactory} that returns Mockito mocks of the {@link AmazonS3}  * interface suitable for unit testing.  */
end_comment

begin_class
DECL|class|MockS3ClientFactory
specifier|public
class|class
name|MockS3ClientFactory
implements|implements
name|S3ClientFactory
block|{
annotation|@
name|Override
DECL|method|createS3Client (URI name)
specifier|public
name|AmazonS3
name|createS3Client
parameter_list|(
name|URI
name|name
parameter_list|)
block|{
name|String
name|bucket
init|=
name|name
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|AmazonS3
name|s3
init|=
name|mock
argument_list|(
name|AmazonS3
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|s3
operator|.
name|doesBucketExist
argument_list|(
name|bucket
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|s3
return|;
block|}
block|}
end_class

end_unit

