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
comment|/****************************************************************  *CreateFlag specifies the file create semantic. Users can combine flags like:<br>  *<code>  * EnumSet.of(CreateFlag.CREATE, CreateFlag.APPEND)  *<code>  * and pass it to {@link org.apache.hadoop.fs.FileSystem #create(Path f, FsPermission permission,  * EnumSet<CreateFlag> flag, int bufferSize, short replication, long blockSize,  * Progressable progress)}.  *   *<p>  * Combine {@link #OVERWRITE} with either {@link #CREATE}   * or {@link #APPEND} does the same as only use   * {@link #OVERWRITE}.<br>  * Combine {@link #CREATE} with {@link #APPEND} has the semantic:  *<ol>  *<li> create the file if it does not exist;  *<li> append the file if it already exists.  *</ol>  *****************************************************************/
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|enum|CreateFlag
specifier|public
enum|enum
name|CreateFlag
block|{
comment|/**    * create the file if it does not exist, and throw an IOException if it    * already exists    */
DECL|enumConstant|CREATE
name|CREATE
argument_list|(
operator|(
name|short
operator|)
literal|0x01
argument_list|)
block|,
comment|/**    * create the file if it does not exist, if it exists, overwrite it.    */
DECL|enumConstant|OVERWRITE
name|OVERWRITE
argument_list|(
operator|(
name|short
operator|)
literal|0x02
argument_list|)
block|,
comment|/**    * append to a file, and throw an IOException if it does not exist    */
DECL|enumConstant|APPEND
name|APPEND
argument_list|(
operator|(
name|short
operator|)
literal|0x04
argument_list|)
block|;
DECL|field|mode
specifier|private
name|short
name|mode
decl_stmt|;
DECL|method|CreateFlag (short mode)
specifier|private
name|CreateFlag
parameter_list|(
name|short
name|mode
parameter_list|)
block|{
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
block|}
DECL|method|getMode ()
name|short
name|getMode
parameter_list|()
block|{
return|return
name|mode
return|;
block|}
block|}
end_enum

end_unit

