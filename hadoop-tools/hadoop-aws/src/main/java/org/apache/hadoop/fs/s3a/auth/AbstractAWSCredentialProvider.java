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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|auth
operator|.
name|AWSCredentialsProvider
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

begin_comment
comment|/**  * Base class for AWS credential providers which  * take a URI and config in their constructor.  */
end_comment

begin_class
DECL|class|AbstractAWSCredentialProvider
specifier|public
specifier|abstract
class|class
name|AbstractAWSCredentialProvider
implements|implements
name|AWSCredentialsProvider
block|{
DECL|field|binding
specifier|private
specifier|final
name|URI
name|binding
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
comment|/**    * Construct from URI + configuration.    * @param uri URI: may be null.    * @param conf configuration.    */
DECL|method|AbstractAWSCredentialProvider ( @ullable final URI uri, final Configuration conf)
specifier|protected
name|AbstractAWSCredentialProvider
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|URI
name|uri
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|binding
operator|=
name|uri
expr_stmt|;
block|}
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
comment|/**    * Get the binding URI: may be null.    * @return the URI this instance was constructed with,    * if any.    */
DECL|method|getUri ()
specifier|public
name|URI
name|getUri
parameter_list|()
block|{
return|return
name|binding
return|;
block|}
comment|/**    * Refresh is a no-op by default.    */
annotation|@
name|Override
DECL|method|refresh ()
specifier|public
name|void
name|refresh
parameter_list|()
block|{   }
block|}
end_class

end_unit

