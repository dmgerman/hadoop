begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
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
comment|/**  * Stringifier interface offers two methods to convert an object   * to a string representation and restore the object given its   * string representation.  * @param<T> the class of the objects to stringify  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|Stringifier
specifier|public
interface|interface
name|Stringifier
parameter_list|<
name|T
parameter_list|>
extends|extends
name|java
operator|.
name|io
operator|.
name|Closeable
block|{
comment|/**    * Converts the object to a string representation    * @param obj the object to convert    * @return the string representation of the object    * @throws IOException if the object cannot be converted    */
DECL|method|toString (T obj)
specifier|public
name|String
name|toString
parameter_list|(
name|T
name|obj
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Restores the object from its string representation.    * @param str the string representation of the object    * @return restored object    * @throws IOException if the object cannot be restored    */
DECL|method|fromString (String str)
specifier|public
name|T
name|fromString
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**     * Closes this object.     * @throws IOException if an I/O error occurs     * */
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

