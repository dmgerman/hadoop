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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|HadoopIllegalArgumentException
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
comment|/**  * Path string is invalid either because it has invalid characters or due to  * other file system specific reasons.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|InvalidPathException
specifier|public
class|class
name|InvalidPathException
extends|extends
name|HadoopIllegalArgumentException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
comment|/**    * Constructs exception with the specified detail message.    *     * @param path invalid path.    */
DECL|method|InvalidPathException (final String path)
specifier|public
name|InvalidPathException
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
literal|"Invalid path name "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs exception with the specified detail message.    *     * @param path invalid path.    * @param reason Reason<code>path</code> is invalid    */
DECL|method|InvalidPathException (final String path, final String reason)
specifier|public
name|InvalidPathException
parameter_list|(
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|String
name|reason
parameter_list|)
block|{
name|super
argument_list|(
literal|"Invalid path "
operator|+
name|path
operator|+
operator|(
name|reason
operator|==
literal|null
condition|?
literal|""
else|:
literal|". ("
operator|+
name|reason
operator|+
literal|")"
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

