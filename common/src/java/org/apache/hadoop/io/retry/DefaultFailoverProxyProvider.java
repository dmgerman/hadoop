begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.retry
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|retry
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
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * An implementation of {@link FailoverProxyProvider} which does nothing in the  * event of failover, and always returns the same proxy object.   */
end_comment

begin_class
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DefaultFailoverProxyProvider
specifier|public
class|class
name|DefaultFailoverProxyProvider
implements|implements
name|FailoverProxyProvider
block|{
DECL|field|proxy
specifier|private
name|Object
name|proxy
decl_stmt|;
DECL|field|iface
specifier|private
name|Class
argument_list|<
name|?
argument_list|>
name|iface
decl_stmt|;
DECL|method|DefaultFailoverProxyProvider (Class<?> iface, Object proxy)
specifier|public
name|DefaultFailoverProxyProvider
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|iface
parameter_list|,
name|Object
name|proxy
parameter_list|)
block|{
name|this
operator|.
name|proxy
operator|=
name|proxy
expr_stmt|;
name|this
operator|.
name|iface
operator|=
name|iface
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getInterface ()
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getInterface
parameter_list|()
block|{
return|return
name|iface
return|;
block|}
annotation|@
name|Override
DECL|method|getProxy ()
specifier|public
name|Object
name|getProxy
parameter_list|()
block|{
return|return
name|proxy
return|;
block|}
annotation|@
name|Override
DECL|method|performFailover (Object currentProxy)
specifier|public
name|void
name|performFailover
parameter_list|(
name|Object
name|currentProxy
parameter_list|)
block|{
comment|// Nothing to do.
block|}
block|}
end_class

end_unit

