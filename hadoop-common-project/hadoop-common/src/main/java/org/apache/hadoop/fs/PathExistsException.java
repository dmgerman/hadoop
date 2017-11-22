begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_comment
comment|/**  * Exception corresponding to File Exists - EEXISTS  */
end_comment

begin_class
DECL|class|PathExistsException
specifier|public
class|class
name|PathExistsException
extends|extends
name|PathIOException
block|{
DECL|field|serialVersionUID
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0L
decl_stmt|;
comment|/** @param path for the exception */
DECL|method|PathExistsException (String path)
specifier|public
name|PathExistsException
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
literal|"File exists"
argument_list|)
expr_stmt|;
block|}
DECL|method|PathExistsException (String path, String error)
specifier|public
name|PathExistsException
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|error
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

