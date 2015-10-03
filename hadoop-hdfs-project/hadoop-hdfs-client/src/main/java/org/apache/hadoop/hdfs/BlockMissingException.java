begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

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
comment|/**   * This exception is thrown when a read encounters a block that has no   * locations associated with it.   */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|BlockMissingException
specifier|public
class|class
name|BlockMissingException
extends|extends
name|IOException
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
DECL|field|filename
specifier|private
specifier|final
name|String
name|filename
decl_stmt|;
DECL|field|offset
specifier|private
specifier|final
name|long
name|offset
decl_stmt|;
comment|/**    * An exception that indicates that file was corrupted.    * @param filename name of corrupted file    * @param description a description of the corruption details    */
DECL|method|BlockMissingException (String filename, String description, long offset)
specifier|public
name|BlockMissingException
parameter_list|(
name|String
name|filename
parameter_list|,
name|String
name|description
parameter_list|,
name|long
name|offset
parameter_list|)
block|{
name|super
argument_list|(
name|description
argument_list|)
expr_stmt|;
name|this
operator|.
name|filename
operator|=
name|filename
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
comment|/**    * Returns the name of the corrupted file.    * @return name of corrupted file    */
DECL|method|getFile ()
specifier|public
name|String
name|getFile
parameter_list|()
block|{
return|return
name|filename
return|;
block|}
comment|/**    * Returns the offset at which this file is corrupted    * @return offset of corrupted file    */
DECL|method|getOffset ()
specifier|public
name|long
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
block|}
end_class

end_unit

