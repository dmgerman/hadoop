begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.file.tfile
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|file
operator|.
name|tfile
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|io
operator|.
name|RawComparator
import|;
end_import

begin_comment
comment|/**  * Interface for objects that can be compared through {@link RawComparator}.  * This is useful in places where we need a single object reference to specify a  * range of bytes in a byte array, such as {@link Comparable} or  * {@link Collections#binarySearch(java.util.List, Object, Comparator)}  *   * The actual comparison among RawComparable's requires an external  * RawComparator and it is applications' responsibility to ensure two  * RawComparable are supposed to be semantically comparable with the same  * RawComparator.  */
end_comment

begin_interface
DECL|interface|RawComparable
specifier|public
interface|interface
name|RawComparable
block|{
comment|/**    * Get the underlying byte array.    *     * @return The underlying byte array.    */
DECL|method|buffer ()
specifier|abstract
name|byte
index|[]
name|buffer
parameter_list|()
function_decl|;
comment|/**    * Get the offset of the first byte in the byte array.    *     * @return The offset of the first byte in the byte array.    */
DECL|method|offset ()
specifier|abstract
name|int
name|offset
parameter_list|()
function_decl|;
comment|/**    * Get the size of the byte range in the byte array.    *     * @return The size of the byte range in the byte array.    */
DECL|method|size ()
specifier|abstract
name|int
name|size
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

