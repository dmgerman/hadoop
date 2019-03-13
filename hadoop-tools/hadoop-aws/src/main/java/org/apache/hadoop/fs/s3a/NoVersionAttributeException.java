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
comment|/**  * Indicates the S3 object does not provide the versioning attribute required  * by the configured change detection policy.  */
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
DECL|class|NoVersionAttributeException
specifier|public
class|class
name|NoVersionAttributeException
extends|extends
name|PathIOException
block|{
comment|/**    * Constructs a NoVersionAttributeException.    *    * @param path the path accessed when the condition was detected    * @param message a message providing more details about the condition    */
DECL|method|NoVersionAttributeException (String path, String message)
specifier|public
name|NoVersionAttributeException
parameter_list|(
name|String
name|path
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
block|}
block|}
end_class

end_unit

