begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|Writable
import|;
end_import

begin_comment
comment|/**  * A named counter that tracks the progress of a map/reduce job.  *  *<p><code>Counters</code> represent global counters, defined either by the  * Map-Reduce framework or applications. Each<code>Counter</code> is named by  * an {@link Enum} and has a long for the value.</p>  *  *<p><code>Counters</code> are bunched into Groups, each comprising of  * counters from a particular<code>Enum</code> class.  */
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
DECL|interface|Counter
specifier|public
interface|interface
name|Counter
extends|extends
name|Writable
block|{
comment|/**    * Set the display name of the counter    * @param displayName of the counter    * @deprecated (and no-op by default)    */
annotation|@
name|Deprecated
DECL|method|setDisplayName (String displayName)
name|void
name|setDisplayName
parameter_list|(
name|String
name|displayName
parameter_list|)
function_decl|;
comment|/**    * @return the name of the counter    */
DECL|method|getName ()
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * Get the display name of the counter.    * @return the user facing name of the counter    */
DECL|method|getDisplayName ()
name|String
name|getDisplayName
parameter_list|()
function_decl|;
comment|/**    * What is the current value of this counter?    * @return the current value    */
DECL|method|getValue ()
name|long
name|getValue
parameter_list|()
function_decl|;
comment|/**    * Set this counter by the given value    * @param value the value to set    */
DECL|method|setValue (long value)
name|void
name|setValue
parameter_list|(
name|long
name|value
parameter_list|)
function_decl|;
comment|/**    * Increment this counter by the given value    * @param incr the value to increase this counter by    */
DECL|method|increment (long incr)
name|void
name|increment
parameter_list|(
name|long
name|incr
parameter_list|)
function_decl|;
annotation|@
name|Private
comment|/**    * Return the underlying object if this is a facade.    * @return the undelying object.    */
DECL|method|getUnderlyingCounter ()
name|Counter
name|getUnderlyingCounter
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

