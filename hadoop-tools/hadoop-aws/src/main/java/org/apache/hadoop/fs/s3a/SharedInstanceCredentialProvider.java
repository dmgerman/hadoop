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
name|s3a
operator|.
name|auth
operator|.
name|IAMInstanceCredentialsProvider
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
name|s3a
operator|.
name|auth
operator|.
name|NoAwsCredentialsException
import|;
end_import

begin_comment
comment|/**  * This credential provider has jittered between existing and non-existing,  * but it turns up in documentation enough that it has been restored.  * It extends {@link IAMInstanceCredentialsProvider} to pick up its  * bindings, which are currently to use the  * {@code EC2ContainerCredentialsProviderWrapper} class for IAM and container  * authentication.  *<p>  * When it fails to authenticate, it raises a  * {@link NoAwsCredentialsException} which can be recognized by retry handlers  * as a non-recoverable failure.  *<p>  * It is implicitly public; marked evolving as we can change its semantics.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|SharedInstanceCredentialProvider
specifier|public
specifier|final
class|class
name|SharedInstanceCredentialProvider
extends|extends
name|IAMInstanceCredentialsProvider
block|{ }
end_class

end_unit

