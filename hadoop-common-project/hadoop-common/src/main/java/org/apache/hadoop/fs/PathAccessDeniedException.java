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
comment|/** EACCES */
end_comment

begin_class
DECL|class|PathAccessDeniedException
specifier|public
class|class
name|PathAccessDeniedException
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
DECL|method|PathAccessDeniedException (String path)
specifier|public
name|PathAccessDeniedException
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
literal|"Permission denied"
argument_list|)
expr_stmt|;
block|}
DECL|method|PathAccessDeniedException (String path, Throwable cause)
specifier|public
name|PathAccessDeniedException
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
DECL|method|PathAccessDeniedException (String path, String error, Throwable cause)
specifier|public
name|PathAccessDeniedException
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|error
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
name|error
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

