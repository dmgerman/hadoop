begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|router
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * A dynamically assignable parameter that is location-specific.  *<p>  * There are 2 ways this mapping is determined:  *<ul>  *<li>Default: Uses the RemoteLocationContext's destination  *<li>Map: Uses the value of the RemoteLocationContext key provided in the  * parameter map.  *</ul>  */
end_comment

begin_class
DECL|class|RemoteParam
specifier|public
class|class
name|RemoteParam
block|{
DECL|field|paramMap
specifier|private
specifier|final
name|Map
argument_list|<
name|?
extends|extends
name|Object
argument_list|,
name|?
extends|extends
name|Object
argument_list|>
name|paramMap
decl_stmt|;
comment|/**    * Constructs a default remote parameter. Always maps the value to the    * destination of the provided RemoveLocationContext.    */
DECL|method|RemoteParam ()
specifier|public
name|RemoteParam
parameter_list|()
block|{
name|this
operator|.
name|paramMap
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Constructs a map based remote parameter. Determines the value using the    * provided RemoteLocationContext as a key into the map.    *    * @param map Map with RemoteLocationContext keys.    */
DECL|method|RemoteParam ( Map<? extends RemoteLocationContext, ? extends Object> map)
specifier|public
name|RemoteParam
parameter_list|(
name|Map
argument_list|<
name|?
extends|extends
name|RemoteLocationContext
argument_list|,
name|?
extends|extends
name|Object
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|paramMap
operator|=
name|map
expr_stmt|;
block|}
comment|/**    * Determine the appropriate value for this parameter based on the location.    *    * @param context Context identifying the location.    * @return A parameter specific to this location.    */
DECL|method|getParameterForContext (RemoteLocationContext context)
specifier|public
name|Object
name|getParameterForContext
parameter_list|(
name|RemoteLocationContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|paramMap
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|paramMap
operator|.
name|get
argument_list|(
name|context
argument_list|)
return|;
block|}
else|else
block|{
comment|// Default case
return|return
name|context
operator|.
name|getDest
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

