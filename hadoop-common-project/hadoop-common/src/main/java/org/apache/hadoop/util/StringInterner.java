begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Interner
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Interners
import|;
end_import

begin_comment
comment|/**  * Provides equivalent behavior to String.intern() to optimize performance,   * whereby does not consume memory in the permanent generation.  */
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
DECL|class|StringInterner
specifier|public
class|class
name|StringInterner
block|{
comment|/**    * Retains a strong reference to each string instance it has interned.    */
DECL|field|strongInterner
specifier|private
specifier|final
specifier|static
name|Interner
argument_list|<
name|String
argument_list|>
name|strongInterner
decl_stmt|;
comment|/**    * Retains a weak reference to each string instance it has interned.     */
DECL|field|weakInterner
specifier|private
specifier|final
specifier|static
name|Interner
argument_list|<
name|String
argument_list|>
name|weakInterner
decl_stmt|;
static|static
block|{
name|strongInterner
operator|=
name|Interners
operator|.
name|newStrongInterner
argument_list|()
expr_stmt|;
name|weakInterner
operator|=
name|Interners
operator|.
name|newWeakInterner
argument_list|()
expr_stmt|;
block|}
comment|/**    * Interns and returns a reference to the representative instance     * for any of a collection of string instances that are equal to each other.    * Retains strong reference to the instance,     * thus preventing it from being garbage-collected.     *     * @param sample string instance to be interned    * @return strong reference to interned string instance    */
DECL|method|strongIntern (String sample)
specifier|public
specifier|static
name|String
name|strongIntern
parameter_list|(
name|String
name|sample
parameter_list|)
block|{
return|return
name|strongInterner
operator|.
name|intern
argument_list|(
name|sample
argument_list|)
return|;
block|}
comment|/**    * Interns and returns a reference to the representative instance     * for any of a collection of string instances that are equal to each other.    * Retains weak reference to the instance,     * and so does not prevent it from being garbage-collected.    *     * @param sample string instance to be interned    * @return weak reference to interned string instance    */
DECL|method|weakIntern (String sample)
specifier|public
specifier|static
name|String
name|weakIntern
parameter_list|(
name|String
name|sample
parameter_list|)
block|{
return|return
name|weakInterner
operator|.
name|intern
argument_list|(
name|sample
argument_list|)
return|;
block|}
block|}
end_class

end_unit

